package mahiti.org.oelp.fileandvideodownloader;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import mahiti.org.oelp.utils.Constants;


/**
 * Created by sandeep HR on 03/05/19.
 */
public class DownloadClass {
    private int mType;
    private Context mContext;
    private String mBaseUrl;
    private String mFileSavingPath;

    List<FileModel> imageListModel = new ArrayList<>();

    public DownloadClass(int mType, Context mContext, String mBaseUrl, String mFileSavingBasePath, List<FileModel> imageListModel) {
        this.mType = mType;
        this.mContext = mContext;
        this.mBaseUrl = mBaseUrl;
        this.imageListModel=imageListModel;
        this.mFileSavingPath = mFileSavingBasePath;
        checkFileType(mType);
    }

    private void checkFileType(int mType) {
        if (mType == Constants.VIDEO)
            callVideoDownloader(mType);
        else if (mType == Constants.IMAGE)
            callImageDownloader(mType);
        else if (mType == Constants.AUDIO)
            callAudioDownloader(mType);
        else
            callFileDownloader(mType);
    }

    private void callFileDownloader(int mType) {

    }

    private void callImageDownloader(int mType) {
        new ImageDownloadingUtility(mContext, mBaseUrl, mFileSavingPath, imageListModel, mType).checkPerm();
    }

    private void callVideoDownloader(int mType) {
        new VideoDownloaderClass(mContext, mBaseUrl, mFileSavingPath, imageListModel, mType).execute();
    }

    private void callAudioDownloader(int mType) {
        new AudioDownloadUtility(mContext, mBaseUrl, mFileSavingPath, imageListModel, mType).execute();
    }
}
