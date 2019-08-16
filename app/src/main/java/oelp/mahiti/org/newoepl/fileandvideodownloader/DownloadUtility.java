package oelp.mahiti.org.newoepl.fileandvideodownloader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import oelp.mahiti.org.newoepl.BuildConfig;
import oelp.mahiti.org.newoepl.R;
import oelp.mahiti.org.newoepl.utils.AppUtils;
import oelp.mahiti.org.newoepl.utils.Constants;
import oelp.mahiti.org.newoepl.utils.Logger;
import oelp.mahiti.org.newoepl.utils.MySharedPref;
import oelp.mahiti.org.newoepl.videoplay.activity.VideoViewActivity;


public class DownloadUtility {
    private static final String TAG = "Utility ";

    private DownloadUtility() {
        /*
        private constructor
         */
    }

    public static String getParentName(String file) {
        File fil = new File(file);
        return fil.getParent();
    }

    public static String getFileName(String file) {
        File fil = new File(file);
        return fil.getName();
    }


    public static void writeToFile(String path, String Message) {
        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File(sdCard.getAbsolutePath());
        dir.mkdirs();
        File file = new File(dir, path);
        try (FileOutputStream f = new FileOutputStream(file, true);
             PrintStream p = new PrintStream(f)) {

            p.print(Message);
            p.println();
            p.close();
            f.close();
        } catch (FileNotFoundException e) {
            Logger.logE(TAG, "writeToFile", e);
            //file not found
        } catch (Exception e) {
            // not IOException
            Logger.logE(TAG, "writeToFile error is", e);

        }
    }


//    public static void handleVolleyError(VolleyError error, String fromClass, Context context) {
//        error.printStackTrace();
//        Logger.logE(TAG, " onErrorResponse " + fromClass, error);
//        try {
//            int errorNum = 0;
//            if (error.networkResponse != null)
//                errorNum = error.networkResponse.statusCode;
//            ToastUtils.displayToast(context.getResources().getString(R.string.app_name) + String.valueOf(errorNum), context);
//
//        } catch (Exception e) {
//            Logger.logE(TAG, " onErrorResponse : ", e);
//        }
//    }

    public static Map<String, String> fetchHeaders(String token) {
        Map<String, String> mHeaderPart = new HashMap<>();
        mHeaderPart.put("Authorization", "Bearer " + token);
        Logger.logD(TAG, " token : " + token);
        mHeaderPart.put("Content-Type", "application/json");
        return mHeaderPart;

    }

//    public static StringRequest setVolleyRetryPolicyStrReq(StringRequest getReq) {
//        getReq.setRetryPolicy(createNewRetryPloicy());
//        return getReq;
//    }

//    private static RetryPolicy createNewRetryPloicy() {
//        return new DefaultRetryPolicy(
//                (int) TimeUnit.SECONDS.toMillis(Constants.DEFAULT_TIMEOUT_SEC),
//                Constants.DEFAULT_MAX_RETRIES,
//                Constants.DEFAULT_BACKOFF_MULT);
//    }


//    public static JsonObjectRequest setVolleyRetryPolice(JsonObjectRequest getReq) {
//        getReq.setRetryPolicy(createNewRetryPloicy());
//        return getReq;
//    }

    public static boolean checkFileExists(String pathFrom) {
        File file = new File(pathFrom);
        Logger.logD(TAG, " checkFile exists : " + file.exists());
        return file.exists();

    }

    public static boolean createFile(String pathTo) {
        File file = new File(pathTo);
        if (file.exists())
            return true;
        Logger.logD(TAG, " folders : " + file.getParent());
        boolean filedircreta = new File(file.getParent()).mkdirs();
        Logger.logD(TAG, " folders Creation status : " + file.getParent() + " = " + filedircreta);
        try {
            filedircreta = file.createNewFile();
            Logger.logD(TAG, " file Creation status : " + file.getAbsolutePath() + " = " + filedircreta);
        } catch (IOException e) {
            Logger.logE(TAG, "createFile", e);
        }
        return filedircreta;
    }

    public static String subPath(int type) { // 0-Image, 1- Pdf, 2- Video
        String subPath = "";
        switch (type) {
            case 0:
                subPath = File.separator + DownloadConstant.KHPTIMAGE;
                break;
            case 1:
                subPath = File.separator + DownloadConstant.KHPTPDF;
                break;
            case 2:
                subPath = File.separator + DownloadConstant.KHPTVIDEO;
                break;
            case 3:
                subPath = File.separator + DownloadConstant.KHPTAUDIO;
                break;
            default:
                subPath = File.separator + DownloadConstant.KHPTIMAGE;
                break;
        }

        return subPath;
    }


    public static String subPathInisdeZip() {
        return DownloadConstant.SUB_PATH_INSIDE_ZIP_FILE;
    }


    public static File completePathInSDCard(int type) {
        return new File(Environment.getExternalStorageDirectory().getAbsolutePath(), subPath(type));

    }

    public static long convertToMB(long convDonwBytesInLong) {
        while (convDonwBytesInLong >= DownloadConstant.CONSTANT_TO_CONVERT_BYTES)
            convDonwBytesInLong = (convDonwBytesInLong / DownloadConstant.CONSTANT_TO_CONVERT_BYTES);
        return convDonwBytesInLong;
    }

    public static void playVideo(Activity activity, String path, String vName, int userId, String uuid, String sectionId) {
        try {

            File f = new File(AppUtils.completePathInSDCard(Constants.VIDEO) + File.separator + AppUtils.getFileName(path));
            Logger.logD(TAG, " complete video path : " + f);
            if (f.exists()) {
                Intent i = new Intent(activity, VideoViewActivity.class);
                i.putExtra("uriPath", f.getAbsolutePath());
                i.putExtra("userId", String.valueOf(userId));
                i.putExtra("videoTitle", vName);
                i.putExtra("UUID", uuid);
                i.putExtra("mediaTrackerApi", "");
                i.putExtra("sectionUUID", sectionId);
                activity.startActivity(i);
                activity.overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);

                MySharedPref preferences = new MySharedPref(activity);
//                preferences.setIsVideoPLaying(true);

            } else {
                ToastUtils.displayToast(activity.getString(R.string.video_not_found), activity);
            }
        } catch (Exception e) {
            Logger.logE(TAG, " playVideo : ", e);
        }
    }


    public static int findScreenSize(Context context) {
        return context.getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;

    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        String deviceName = "";
        if (model.startsWith(manufacturer)) {
            deviceName = capitalize(model);
            Logger.logD(TAG, " deviceName " + deviceName);
        } else {
            deviceName = capitalize(manufacturer) + " " + model;
            Logger.logD(TAG, " deviceName " + deviceName);
        }
        return deviceName;
    }


    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    public static String getFileNameFromURL(String url) {
        if (url == null) {
            return "";
        }

        int startIndex = url.lastIndexOf('/') + 1;
        int length = url.length();

        // find end index for ?
        int lastQMPos = url.lastIndexOf('?');
        if (lastQMPos == -1) {
            lastQMPos = length;
        }

        // find end index for #
        int lastHashPos = url.lastIndexOf('#');
        if (lastHashPos == -1) {
            lastHashPos = length;
        }

        // calculate the end index
        int endIndex = Math.min(lastQMPos, lastHashPos);
        return url.substring(startIndex, endIndex);
    }

    public static String getDateTime() {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = new Date();
        return simpleDateFormat.format(date);

    }


    public static boolean canDisplayPDF(Activity activity, File file) {
        PackageManager packageManager = activity.getPackageManager();
        Intent i;
        if (Build.VERSION.SDK_INT >= 24) {
            i = new Intent(Intent.ACTION_VIEW, FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider", file));
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            i = new Intent(Intent.ACTION_VIEW);
            i.setDataAndType(Uri.fromFile(file), "application/pdf");
        }
        return packageManager.queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
    }


    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus() != null && inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(), 0);
        }

    }

    public static float getWidthOfScreen(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        Log.e("Width", "" + dpWidth);
        return dpWidth;
    }

    public static int getNoOfColumnForGridVideo(float widthOfScreen, int widthForOneGrid) {

        float widthForVideoPart = widthOfScreen - widthOfScreen * 15 / 100;
        Log.e("No Of column", "" + widthForVideoPart / widthForOneGrid);
        return (int) widthForVideoPart / widthForOneGrid;

    }

    public static void showNotificationForDownload(Context context, String videoName) {

//        mBuilder = new NotificationCompat.Builder(context, "101")
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentTitle("OELP")
//                .setContentText("Video downloading file " + audioLastName)
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
////                .setContentIntent(pendingIntent)
//                .setAutoCancel(true);
    }

    private static NotificationCompat.Builder mBuilder;

    public static void updateNotification(String percent, String fileName, Context context) {
        try {
            mBuilder.setContentText(fileName)
                    .setContentTitle("OELP")
                    //.setSmallIcon(android.R.drawable.stat_sys_download)
                    .setOngoing(true)
                    .setContentInfo(percent + "%")
                    .setProgress(100, Integer.parseInt(percent), false);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

// notificationId is a unique int for each notification that you must define
            notificationManager.notify(101, mBuilder.build());
            if (Integer.parseInt(percent) == 100) {
                notificationManager.cancel(101);
                mBuilder = null;
            }

        } catch (Exception e) {

            Logger.logE("Error...Notification.", e.getMessage() + ".....", e);

        }

    }

    public void customProgressalertDialog(Context context, final CancelListener cancelListener) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.custom_progress_dialog_view, null);
        dialogBuilder.setView(dialogView);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnBackground = dialogView.findViewById(R.id.btnBackground);
        TextView tvContentName = dialogView.findViewById(R.id.tvContentName);
        TextView tvPercentage = dialogView.findViewById(R.id.tvPercentage);
        ProgressBar progressBar = dialogView.findViewById(R.id.progressBar);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                cancelListener.onCancel();
            }
        });
        btnBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });


    }

    public void updateProgressDialog() {

    }


    public static long getExternalFreeSpace() {
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        return ((long) statFs.getAvailableBlocks() * (long) statFs.getBlockSize()) / 1048576;

    }

    public static long convertMegaBytesToBytes(long mb) {
        return mb * 1024 * 1024;

    }
}
