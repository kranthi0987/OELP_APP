package oelp.mahiti.org.newoepl.fileandvideodownloader;

/**
 * Created by sandeep HR on 03/05/19.
 */
public interface OnMediaDownloadListener {

//    void  onMediaDownload(int type, String savedPath, String name);
    void onMediaDownload(int type, String savedPath, String name, int position, String uuid);

}
