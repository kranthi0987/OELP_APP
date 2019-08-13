package oelp.mahiti.org.newoepl.fileandvideodownloader;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.gun0912.tedpermission.PermissionListener;
import com.snatik.storage.Storage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import oelp.mahiti.org.newoepl.utils.AppUtils;
import oelp.mahiti.org.newoepl.utils.Logger;

public class ImageDownloadingUtility {

    private static final String TAG = " ImageDownldUtility ";
    private int counter;
    private List<String> imagesList;
    private Context context;
    private String path = "";
    private ProgressDialog mProgressDialog;
    String baseUrl;
    String imageSavingBasePath;
    List<FileModel> fileModelList = new ArrayList<>();
    int mType;
    int imageListSize;
    private int i = 0;
    int imageDownloadInBackground = 0;
    private OnMediaDownloadListener playVideoInterfaceObj;
    String ImagefileName;
    String fileNAme1 = "";
    int position;


    public ImageDownloadingUtility(Context context, String baseURl, String imageSavingBasePath, List<FileModel> fileModelList, int mType) {
        this.context = context;
        this.baseUrl = baseURl;
        this.imageSavingBasePath = imageSavingBasePath;
        this.fileModelList = fileModelList;
        imageListSize = fileModelList.size();
        this.mType = mType;
        this.playVideoInterfaceObj = (OnMediaDownloadListener) context;
        position = fileModelList.get(0).getPosition();


    }

    public void checkPerm() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                if (!new File(imageSavingBasePath).exists()) {
                    createDirectory();
                } else {
                    new ImagesFetchAdyncTask().execute();
                }


            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(context, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();

            }

        };
        TedPermission.with(context)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }


    public void createDirectory() {
        Storage storage = new Storage(context);
//        for (int i = 0; i < fileModelList.size(); i++) {
//            File imageSavingPath = new File(imageSavingBasePath, new File(fileModelList.get(i).getFileUrl()).getAbsoluteFile().getParent());
        File imageSavingPath = new File(imageSavingBasePath);
        path = imageSavingPath.getAbsolutePath();
        storage.createDirectory(path, true);
//        }
        new ImagesFetchAdyncTask().execute();
    }

    @SuppressLint("StaticFieldLeak")
    class ImagesFetchAdyncTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (imageDownloadInBackground == 0) {
                mProgressDialog = new ProgressDialog(context);
                mProgressDialog.setTitle("Downloading Content, Please Wait!");
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.setMax(100);
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.setCancelable(false);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                mProgressDialog.show();
            }

        }

        @Override
        protected String doInBackground(String... strings) {
            String totalDownload = "";
            if (fileModelList != null && !fileModelList.isEmpty()) {
                fileNAme1 = fileModelList.get(0).getFileName();
                initDownloading(fileModelList.get(0).getFileUrl());
            } else {
                totalDownload = "true";
            }

            return totalDownload;
        }

        private void initDownloading(String imagePath) {
            counter = counter + 1;
            startDownloading(imagePath);

        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            if (imageDownloadInBackground == 0)
                mProgressDialog.setMessage("Downloading " + (values[0]) + " of " + imageListSize);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equalsIgnoreCase("true")) {
                dismissDialog();

            }
        }

        private void startDownloading(final String imagePath1) {
            String imagePath = AppUtils.getFileName(imagePath1);
            ImagefileName = imagePath;

            try {
//                String fileName = Utils.getFileName(imagePath);
                publishProgress(String.valueOf(counter));
                File imageSavingPath = new File(imageSavingBasePath + DownloadConstant.Slash, new File(imagePath).getAbsoluteFile().getParent());
                path = imageSavingPath.getAbsolutePath();
                String imgeCompleteUrl = "";
                if (imagePath1.contains(baseUrl)) {
                    imgeCompleteUrl = imagePath1;
                } else {
                    imgeCompleteUrl = baseUrl + DownloadConstant.Slash + imagePath1;
                }

                Logger.logD(TAG, "~ : " + imgeCompleteUrl);

                int downloadId = PRDownloader.download(imgeCompleteUrl, path + "/", DownloadUtility.getFileNameFromURL(imagePath))
                        .build()
                        .setOnStartOrResumeListener(() -> {

                        })
                        .setOnPauseListener(() -> {

                        })
                        .setOnCancelListener(new OnCancelListener() {
                            @Override
                            public void onCancel() {

                            }
                        })
                        .setOnProgressListener(progress -> {
                        })
                        .start(new OnDownloadListener() {
                            @Override
                            public void onDownloadComplete() {
                                fileModelList.remove(0);
                                if (fileModelList != null && !fileModelList.isEmpty())
                                    initDownloading(fileModelList.get(0).getFileUrl());
                                else
                                    dismissDialog();
                            }

                            @Override
                            public void onError(Error error) {
                                Log.e(TAG, " image download error " + error.toString());
                                DownloadUtility.writeToFile(imagePath, DownloadConstant.ERROR_WHILE_DOWN_FROM_SERVER);
                                fileModelList.remove(0);
                                if (fileModelList != null && !fileModelList.isEmpty())
                                    initDownloading(fileModelList.get(0).getFileUrl());
                                else
                                    dismissDialog();
                            }

                        });
            } catch (Exception e) {
                Logger.logE(TAG, " Exception : ", e);
                DownloadUtility.writeToFile(imagePath, e.getMessage());
                fileModelList.remove(0);
                if (imagesList != null && !fileModelList.isEmpty())
                    initDownloading(fileModelList.get(0).getFileUrl());
                else
                    dismissDialog();
            }
        }

        private void dismissDialog() {
            try {
                if (mProgressDialog != null && mProgressDialog.isShowing() && context != null)
                    mProgressDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            playVideoInterfaceObj.onMediaDownload(mType, imageSavingBasePath + "/" + ImagefileName, fileNAme1, position,"");
        }
    }
}





