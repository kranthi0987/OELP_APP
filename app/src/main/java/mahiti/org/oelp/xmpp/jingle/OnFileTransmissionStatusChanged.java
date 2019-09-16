package mahiti.org.oelp.xmpp.jingle;

import mahiti.org.oelp.entities.DownloadableFile;

public interface OnFileTransmissionStatusChanged {
    void onFileTransmitted(DownloadableFile file);

    void onFileTransferAborted();
}
