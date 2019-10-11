package mahiti.org.oelp.http;

import android.os.PowerManager;
import android.util.Log;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import mahiti.org.oelp.Config;
import mahiti.org.oelp.entities.Account;
import mahiti.org.oelp.entities.DownloadableFile;
import mahiti.org.oelp.entities.Message;
import mahiti.org.oelp.entities.Transferable;
import mahiti.org.oelp.persistance.FileBackend;
import mahiti.org.oelp.services.AbstractConnectionManager;
import mahiti.org.oelp.services.XmppConnectionService;
import mahiti.org.oelp.utils.Checksum;
import mahiti.org.oelp.utils.CryptoHelper;
import mahiti.org.oelp.utils.WakeLockHelper;

public class HttpUploadConnection implements Transferable {

    static final List<String> WHITE_LISTED_HEADERS = Arrays.asList(
            "Authorization",
            "Cookie",
            "Expires"
    );

    private final HttpConnectionManager mHttpConnectionManager;
    private final XmppConnectionService mXmppConnectionService;
    private final SlotRequester mSlotRequester;
    private final Method method;
    private final boolean mUseTor;
    private boolean cancelled = false;
    private boolean delayed = false;
    private DownloadableFile file;
    private final Message message;
    private String mime;
    private SlotRequester.Slot slot;
    private byte[] key = null;
    private int mStatus = Transferable.STATUS_UNKNOWN;

    private long transmitted = 0;

    public HttpUploadConnection(Message message, Method method, HttpConnectionManager httpConnectionManager) {
        this.message = message;
        this.method = method;
        this.mHttpConnectionManager = httpConnectionManager;
        this.mXmppConnectionService = httpConnectionManager.getXmppConnectionService();
        this.mSlotRequester = new SlotRequester(this.mXmppConnectionService);
        this.mUseTor = mXmppConnectionService.useTorToConnect();
    }

    @Override
    public boolean start() {
        return false;
    }

    @Override
    public int getStatus() {
        return this.mStatus;
    }

    @Override
    public long getFileSize() {
        return file == null ? 0 : file.getExpectedSize();
    }

    @Override
    public int getProgress() {
        if (file == null) {
            return 0;
        }
        return (int) ((((double) transmitted) / file.getExpectedSize()) * 100);
    }

    @Override
    public void cancel() {
        this.cancelled = true;
    }

    private void fail(String errorMessage) {
        finish();
        mXmppConnectionService.markMessage(message, Message.STATUS_SEND_FAILED, cancelled ? Message.ERROR_MESSAGE_CANCELLED : errorMessage);
    }

    private void finish() {
        mHttpConnectionManager.finishUploadConnection(this);
        message.setTransferable(null);
    }

    public void init(boolean delay) {
        final Account account = message.getConversation().getAccount();
        this.file = mXmppConnectionService.getFileBackend().getFile(message, false);
        if (message.getEncryption() == Message.ENCRYPTION_PGP || message.getEncryption() == Message.ENCRYPTION_DECRYPTED) {
            this.mime = "application/pgp-encrypted";
        } else {
            this.mime = this.file.getMimeType();
        }
        this.delayed = delay;
        if (mahiti.org.oelp.Config.ENCRYPT_ON_HTTP_UPLOADED
                || message.getEncryption() == Message.ENCRYPTION_AXOLOTL
                || message.getEncryption() == Message.ENCRYPTION_OTR) {
            this.key = new byte[48];
            mXmppConnectionService.getRNG().nextBytes(this.key);
            this.file.setKeyAndIv(this.key);
        }

        final String md5;

        if (method == Method.P1_S3) {
            try {
                md5 = Checksum.md5(AbstractConnectionManager.upgrade(file, new FileInputStream(file)));
            } catch (Exception e) {
                Log.d(mahiti.org.oelp.Config.LOGTAG, account.getJid().asBareJid() + ": unable to calculate md5()", e);
                fail(e.getMessage());
                return;
            }
        } else {
            md5 = null;
        }

        this.file.setExpectedSize(file.getSize() + (file.getKey() != null ? 16 : 0));
        message.resetFileParams();
        this.mSlotRequester.request(method, account, file, mime, md5, new SlotRequester.OnSlotRequested() {
            @Override
            public void success(SlotRequester.Slot slot) {
                if (!cancelled) {
                    changeStatus(STATUS_WAITING);
                    mXmppConnectionService.mUploadExecutor.execute(() -> {
                        changeStatus(STATUS_UPLOADING);
                        HttpUploadConnection.this.slot = slot;
                        HttpUploadConnection.this.upload();
                    });
                }
            }

            @Override
            public void failure(String message) {
                fail(message);
            }
        });
        message.setTransferable(this);
        mXmppConnectionService.markMessage(message, Message.STATUS_UNSEND);
    }

    private void upload() {
        OutputStream os = null;
        InputStream fileInputStream = null;
        HttpURLConnection connection = null;
        PowerManager.WakeLock wakeLock = mHttpConnectionManager.createWakeLock("http_upload_" + message.getUuid());
        try {
            fileInputStream = new FileInputStream(file);
            final String slotHostname = slot.getPutUrl().getHost();
            final boolean onionSlot = slotHostname != null && slotHostname.endsWith(".onion");
            final int expectedFileSize = (int) file.getExpectedSize();
            final int readTimeout = ((expectedFileSize / 2048) + mahiti.org.oelp.Config.SOCKET_TIMEOUT) * 1000; //assuming a minimum transfer speed of 16kbit/s
            wakeLock.acquire(readTimeout);
            Log.d(mahiti.org.oelp.Config.LOGTAG, "uploading to " + slot.getPutUrl().toString() + " w/ read timeout of " + readTimeout / 1000 + "s");
            if (mUseTor || message.getConversation().getAccount().isOnion() || onionSlot) {
                connection = (HttpURLConnection) slot.getPutUrl().openConnection(HttpConnectionManager.getProxy());
            } else {
                connection = (HttpURLConnection) slot.getPutUrl().openConnection();
            }
            if (connection instanceof HttpsURLConnection) {
                mHttpConnectionManager.setupTrustManager((HttpsURLConnection) connection, true);
            }
            connection.setUseCaches(false);
            connection.setRequestMethod("PUT");
            connection.setFixedLengthStreamingMode(expectedFileSize);
            connection.setRequestProperty("User-Agent", mXmppConnectionService.getIqGenerator().getUserAgent());
            if (slot.getHeaders() != null) {
                for (HashMap.Entry<String, String> entry : slot.getHeaders().entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setConnectTimeout(mahiti.org.oelp.Config.SOCKET_TIMEOUT * 1000);
            connection.setReadTimeout(readTimeout);
            connection.connect();
            final InputStream innerInputStream = AbstractConnectionManager.upgrade(file, fileInputStream);
            os = connection.getOutputStream();
            transmitted = 0;
            int count;
            byte[] buffer = new byte[4096];
            while (((count = innerInputStream.read(buffer)) != -1) && !cancelled) {
                transmitted += count;
                os.write(buffer, 0, count);
                mHttpConnectionManager.updateConversationUi(false);
            }
            os.flush();
            os.close();
            int code = connection.getResponseCode();
            InputStream is = connection.getErrorStream();
            if (is != null) {
                try (Scanner scanner = new Scanner(is)) {
                    scanner.useDelimiter("\\Z");
                    Log.d(mahiti.org.oelp.Config.LOGTAG, "body: " + scanner.next());
                }
            }
            if (code == 200 || code == 201) {
                Log.d(mahiti.org.oelp.Config.LOGTAG, "finished uploading file");
                final URL get;
                if (key != null) {
                    if (method == Method.P1_S3) {
                        get = new URL(slot.getGetUrl().toString() + "#" + CryptoHelper.bytesToHex(key));
                    } else {
                        get = CryptoHelper.toAesGcmUrl(new URL(slot.getGetUrl().toString() + "#" + CryptoHelper.bytesToHex(key)));
                    }
                } else {
                    get = slot.getGetUrl();
                }
                mXmppConnectionService.getFileBackend().updateFileParams(message, get);
                mXmppConnectionService.getFileBackend().updateMediaScanner(file);
                finish();
                if (!message.isPrivateMessage()) {
                    message.setCounterpart(message.getConversation().getJid().asBareJid());
                }
                mXmppConnectionService.resendMessage(message, delayed);
            } else {
                Log.d(mahiti.org.oelp.Config.LOGTAG, "http upload failed because response code was " + code);
                fail("http upload failed because response code was " + code);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(Config.LOGTAG, "http upload failed " + e.getMessage());
            fail(e.getMessage());
        } finally {
            FileBackend.close(fileInputStream);
            FileBackend.close(os);
            if (connection != null) {
                connection.disconnect();
            }
            WakeLockHelper.release(wakeLock);
        }
    }

    private void changeStatus(int status) {
        this.mStatus = status;
        mHttpConnectionManager.updateConversationUi(true);
    }

    public Message getMessage() {
        return message;
    }
}