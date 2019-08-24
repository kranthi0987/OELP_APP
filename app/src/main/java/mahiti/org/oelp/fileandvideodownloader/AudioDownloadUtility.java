package mahiti.org.oelp.fileandvideodownloader;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import mahiti.org.oelp.R;
import mahiti.org.oelp.utils.AppUtils;
import mahiti.org.oelp.utils.Logger;
import mahiti.org.oelp.utils.MySharedPref;


public class AudioDownloadUtility extends AsyncTask<Void, String, String> {
    private final int position;
    Context context;
    private String baseURl;
    private MySharedPref preferences;
    private URL url;
    private HttpURLConnection connection;
    String TAG = AudioDownloadUtility.class.getSimpleName();
    //    private final String sdCardPath;
    private long totalSize = 0;
    List<Integer> idOfDownloadingVideoList = new ArrayList<>();
    private OnMediaDownloadListener playVideoInterfaceObj;
    List<FileModel> fileModelList = new ArrayList<>();
    int type;
    String audioLastName;
    String videoSavingBasePath;
    String videoUrl;
    int videoId =0;
    String audioTitle ="";
    private String uuid;


    public AudioDownloadUtility(Context context, String baseURl, String videoSavingBasePath, List<FileModel> fileModelList, int type) {
        PRDownloader.initialize(context);
        this.context = context;
        this.baseURl = baseURl;
        this.videoSavingBasePath = videoSavingBasePath;
        this.fileModelList = fileModelList;
        this.type = type;
        preferences = new MySharedPref(context);
        this.playVideoInterfaceObj = (OnMediaDownloadListener) context;
        position = fileModelList.get(0).getPosition();
        uuid= fileModelList.get(0).getUuid();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... voids) {
        for (int i = 0; i < fileModelList.size(); i++) {

            startDownloading(fileModelList.get(i));
        }
        return null;
    }

    public void startDownloading(FileModel fileModel) {
        audioTitle = fileModel.getFileName();
        audioLastName = AppUtils.getFileName(fileModel.getFileUrl());
        videoUrl = fileModel.getFileUrl();
        String videoPathUrl = fileModel.getFileUrl();
        Logger.logD(TAG, "Downloading Image : " + videoId + " " + audioTitle + " " + audioTitle);
        try {
            if ((checkConnection().contains("200"))) {
                checkFreeSpaceAndDownload(videoPathUrl);
            } else {
                Thread.sleep(200);
                DownloadUtility.writeToFile("Missing Videos.txt", "\n" + (audioTitle).replace("//", DownloadConstant.Slash));
            }

        } catch (Exception e) {
            Logger.logE(TAG, "loopThroughPathsNDownload", e);
        }


    }

    private void checkFreeSpaceAndDownload(String videoUrl) {
        String statusFOrDownloadSpace;
        try {
            File file = new File(videoSavingBasePath.concat(DownloadConstant.Slash + audioLastName));
            boolean folderToSaveVideo;
            folderToSaveVideo = file.exists() || file.mkdirs();
            if (!folderToSaveVideo) {
                Logger.logD(TAG, context.getString(R.string.couldnot_create_file));
                statusFOrDownloadSpace = context.getString(R.string.couldnot_create_file);
                ToastUtils.displayToast(statusFOrDownloadSpace, context);
                return;
            }
            file = new File(file.getAbsolutePath());
            Logger.logD(TAG, " file path to download : " + file.getAbsolutePath());
            totalSize = connection.getContentLength();
//            putVideoFileSizeInDB(totalSize, videoId);
            Logger.logV(TAG, "the  size of the url and file size  is........." + url + "......" + totalSize);
            long megaBytes = DownloadUtility.getExternalFreeSpace();
            long bytesMemory = DownloadUtility.convertMegaBytesToBytes(megaBytes);
            if (bytesMemory > totalSize) {
                Logger.logV(TAG, " CONTENT length " + "the total size is......." + totalSize);
                if (totalSize == -1 || totalSize <= 0) {
                    Logger.logV(TAG, "the files are already downloaded" + "downloaded is completed.....");
                    Logger.logD(TAG, context.getString(R.string.content_size_invalid));
                }
                checkFileExits(file);
            } else {
                ToastUtils.displayToast(context.getString(R.string.memory_is_not_there_to_download_the_videos), context);
                Logger.logD(TAG, context.getString(R.string.insufficient_memory));
            }
        } catch (Exception e) {
            Logger.logE(TAG, audioLastName, e);
        }
    }

    private int calculatelengthofFileInMB(long file) {

        long inkB = file / 1024;
        return (int) inkB / 1024;


    }

    private void checkFileExits(File file) {
        try {
            if (file.exists()) {
                cleanUp(file.getAbsolutePath());
            }
            file.createNewFile();
            downloadVideo((baseURl + DownloadConstant.Slash + videoUrl), videoSavingBasePath, audioLastName, videoId);
            Log.v(TAG, "the size of the file is......" + file.getName());

        } catch (Exception e) {
            Logger.logE(TAG, " checkFileExits ", e);
        }
    }

    private String getVideoName(String videoPath) {
        return DownloadUtility.getFileName(videoPath);
    }

    private String getVideoSavingPath(String videoPath) {
//        return imageSavingBasePath.concat(Constant.Slash) + videoPath;
        return videoSavingBasePath.concat(DownloadConstant.Slash) + AppUtils.getFileName(videoPath);
    }

    public void cleanUp(String path) throws IOException {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Path paths = Paths.get(path);
            Files.delete(paths);
        }
    }

    private void putVideoFileSizeInDB(long videoSize, int videoId) {

//        manager.saveVideoSize(videoSize, videoId);
    }

    private String checkConnection() {
        String strRes;
        try {
            url = new URL(baseURl + DownloadConstant.Slash + videoUrl);
            Logger.logV(TAG, "path of the video is : " + videoUrl );
            Logger.logV(TAG, "the video path is " + "the url is......." + url);
            connection = (HttpURLConnection) url.openConnection();
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            connection.connect();
            Logger.logV(TAG, "the response code is " + url + "\n " + connection.getResponseCode());
            strRes = "" + connection.getResponseCode() + " " + connection.getResponseMessage();

        } catch (Exception e) {
            Logger.logE(TAG, "the downloading video error is", e);
            strRes = context.getString(R.string.couldnot_connect_to_server);
        }
        return strRes + " \n " + audioLastName;

    }

    int downloadId;

    private void downloadVideo(final String videourl, String downloadPath, String fileName, final int videoId) {

        downloadId = PRDownloader.download(videourl, downloadPath, fileName)
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {
                        showProgressDialoG();
                        showNotification();
                    }
                })
                .setOnPauseListener(() -> Logger.logD(TAG + "on Pause VideoDownloading: ", audioTitle))
                .setOnCancelListener(() -> {
                    dismissDialog();
                    dismissNotification(false);
                })
                .setOnProgressListener(progress -> {
                    setProgressOnDialog(progress, audioTitle);
                    showNotificationProgress(progress);
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        dismissDialog();
                        dismissNotification(true);
                        playVideoInterfaceObj.onMediaDownload(type, downloadPath+"/"+ audioLastName, audioTitle,position,"");
                    }


                    @Override
                    public void onError(Error error) {
                        ToastUtils.displayToast(error.toString(), context);
                        dismissDialog();
                        dismissNotification(false);
                    }
                });

    }

    private void dismissNotification(boolean isComplete) {
        if (isComplete)
            builder.setContentText(context.getString(R.string.down_comp));
        builder.setProgress(0, 0, false);
        notificationManager.notify(videoId, builder.build());
    }


    private void showNotificationProgress(Progress progress) {
        builder.setProgress(calculatelengthofFileInMB(progress.totalBytes), calculatelengthofFileInMB(progress.currentBytes), false);
        notificationManager.notify(videoId, builder.build());
    }

    NotificationManagerCompat notificationManager;
    NotificationCompat.Builder builder;

    private void showNotification() {
        notificationManager = NotificationManagerCompat.from(context);
        builder = new NotificationCompat.Builder(context, DownloadConstant.CHANNEL_ID);
        builder.setContentTitle(context.getString(R.string.app_name) + ": " + audioTitle)
                .setContentText(context.getString(R.string.down_prog))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_LOW);
        notificationManager.notify(videoId, builder.build());
    }


    private void compareTheVideoId() {
//        videoDownlodedStatus = manager.getVideoDownloadedStatus(videoId);
//        if (videoDownlodedStatus == 1 && imageModelList != null) {
//            addPreviousValues();
//        }
    }

    private void addPreviousValues() {
//        if (preferences.fetchDownloadedUnPlayedVideoList() != null) {
//            fileModelList.addAll(preferences.fetchDownloadedUnPlayedVideoList());
//        }
//        Logger.logD(VideoCompletionBroadcast.class.getSimpleName(), "Get Downloaded but not played video : " + Hawk.get(Constant.DOWNLAODEDUNPLAYEDVIDEOLIST, null));
//        imageModelList.add(new ImageModel("play", Utility.getParentName(videoPath), Utility.getFileName(videoPath), videoId, audioLastName));
//        preferences.saveDownloadedUnPlayedVideoList(fileModelList);
    }

    public void saveVideoDownloadingIds(int videoId) {
//        idOfDownloadingVideoList = preferences.getDownloadingVideoIDList();
        if (idOfDownloadingVideoList != null) {
//            Logger.logD(VideoDownloadAsyncTask.class.getSimpleName(), "list of video downloading in background getting : " + Hawk.get(Constant.DOWNLOADINGVIDEO, null).toString());
            idOfDownloadingVideoList.add(videoId);
        } else {
            idOfDownloadingVideoList = new ArrayList<>();
            idOfDownloadingVideoList.add(videoId);
        }
//        preferences.setDownloadingVideoIDList(idOfDownloadingVideoList);
    }

    private void getVideoIdandDelete() {
        try {


//            idOfDownloadingVideoList = preferences.getDownloadingVideoIDList();
//            if (idOfDownloadingVideoList != null && !idOfDownloadingVideoList.isEmpty()) {
//                for (int i = 0; i < idOfDownloadingVideoList.size(); i++) {
//                    if (idOfDownloadingVideoList.get(i) == videoId) {
//                        idOfDownloadingVideoList.remove(i);
//                        preferences.setDownloadingVideoIDList(idOfDownloadingVideoList);
//                        break;
//                    }
//                }
//            }
        } catch (Exception e) {
//            Logger.logE(VideoDownloadAsyncTask.class.getSimpleName(), "Video delete", e);

        }

    }

    private void putDownloadStatusForVideoInDb(int videoDownloaded, int videoId) {
//        DBManager dbManager = new DBManager(context);
//        dbManager.open();
//        dbManager.setVideoDownloadedStatus(videoId, videoDownloaded);
//        Logger.logD(VideoDownloadAsyncTask.class.getSimpleName(), "Status Saving for Video Downloaded");
    }

    private void setProgressOnDialog(Progress progress, String videoName) {
        if (type== DownloadConstant.AudioFile)  // we are not displaying progress dialog for audio downloading
            return;

        int downloadedPercentage = (int) ((progress.currentBytes * 100) / progress.totalBytes);
        tvPercentage.setText("" + downloadedPercentage + " %");
        tvContentName.setText(videoName);
        progressBar.setProgress(downloadedPercentage);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissDialog();
                dismissNotification(false);
                dismissDownloading();
            }
        });

        btnBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissDialog();
            }
        });

    }

    private void dismissDialog() {
        if (dialog != null && context != null)
            dialog.dismiss();
    }

    private Dialog dialog;
    ProgressBar progressBar;
    Button btnCancel;
    Button btnBackground;
    TextView tvPercentage;
    TextView tvContentName;


    private void showProgressDialoG() {
        if (type== DownloadConstant.AudioFile)  // we are not displaying progress dialog for audio downloading
            return;
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_progress_dialog_view);
        progressBar = dialog.findViewById(R.id.progressBar);
        btnCancel = dialog.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        btnBackground = dialog.findViewById(R.id.btnBackground);
        tvPercentage = dialog.findViewById(R.id.tvPercentage);
        tvContentName = dialog.findViewById(R.id.tvContentName);
        dialog.show();
    }


    private void dismissDownloading() {
        if (type== DownloadConstant.AudioFile)  // we are not displaying progress dialog for audio downloading
            return;
        PRDownloader.cancel(downloadId);
    }


}
