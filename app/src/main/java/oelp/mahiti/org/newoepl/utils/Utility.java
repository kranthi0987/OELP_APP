package oelp.mahiti.org.newoepl.utils;


/**
 * Created by sandeep HR on 27/12/18.
 */
public class Utility {
    private static final String TAG = "Utility ";

    private Utility() {
        /*
        private constructor
         */
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
//
//    public static Map<String, String> fetchHeaders(String token) {
//        Map<String, String> mHeaderPart = new HashMap<>();
//        mHeaderPart.put("Authorization", "Bearer " + token);
//        Logger.logD(TAG, " token : " + token);
//        mHeaderPart.put("Content-Type", "application/json");
//        return mHeaderPart;
//
//    }
//
//    public static StringRequest setVolleyRetryPolicyStrReq(StringRequest getReq) {
//        getReq.setRetryPolicy(createNewRetryPloicy());
//        return getReq;
//    }
//
//    private static RetryPolicy createNewRetryPloicy() {
//        return new DefaultRetryPolicy(
//                (int) TimeUnit.SECONDS.toMillis(Constants.DEFAULT_TIMEOUT_SEC),
//                Constants.DEFAULT_MAX_RETRIES,
//                Constants.DEFAULT_BACKOFF_MULT);
//    }
//
//
//    public static JsonObjectRequest setVolleyRetryPolice(JsonObjectRequest getReq) {
//        getReq.setRetryPolicy(createNewRetryPloicy());
//        return getReq;
//    }
//
//    public static boolean checkFileExists(String pathFrom) {
//        File file = new File(pathFrom);
//        Logger.logD(TAG, " checkFile exists : " + file.exists());
//        return file.exists();
//
//    }
//
//    public static boolean createFile(String pathTo) {
//        File file = new File(pathTo);
//        if (file.exists())
//            return true;
//        Logger.logD(TAG, " folders : " + file.getParent());
//        boolean filedircreta = new File(file.getParent()).mkdirs();
//        Logger.logD(TAG, " folders Creation status : " + file.getParent() + " = " + filedircreta);
//        try {
//            filedircreta = file.createNewFile();
//            Logger.logD(TAG, " file Creation status : " + file.getAbsolutePath() + " = " + filedircreta);
//        } catch (IOException e) {
//            Logger.logE(TAG, "createFile", e);
//        }
//        return filedircreta;
//    }
//
//    public static String subPath() {
//        String subPath = "";
//        subPath = File.separator + Constants.STUDENT + File.separator;
//        return subPath;
//    }
//
//    public static String subPathInisdeZip() {
//        return Constants.SUB_PATH_INSIDE_ZIP_FILE;
//    }
//
//    public static File completePathInSDCard() {
//        return new File(Environment.getExternalStorageDirectory().getAbsolutePath(), subPath());
//
//    }
//
//    public static long convertToMB(long convDonwBytesInLong) {
//        while (convDonwBytesInLong >= Constants.CONSTANT_TO_CONVERT_BYTES)
//            convDonwBytesInLong = (convDonwBytesInLong / Constants.CONSTANT_TO_CONVERT_BYTES);
//        return convDonwBytesInLong;
//    }
//
//    public static void playVideo(Activity activity, String path, String vName, long userId, String unitId, String videoId, String videoTitle) {
//        try {
//
//            File f = new File(completePathInSDCard() + File.separator + path + File.separator + vName);
//            Logger.logD(TAG, " complete video path : " + f);
//            if (f.exists()) {
//                Intent i = new Intent(activity, org.mahiti.videoplay.activity.VideoViewActivity.class);
//                i.putExtra("uriPath", f.getAbsolutePath());
//                i.putExtra("userId", String.valueOf(userId));
//                i.putExtra("videoId", videoId);
//                i.putExtra("videoName", vName);
//                i.putExtra("unitId", unitId);
//                i.putExtra("videoTitle", videoTitle);
//                i.putExtra("mediaTrackerApi", RetrofitConstant.MEDIA_TRACKER_API);
//                activity.startActivity(i);
//                activity.overridePendingTransition(R.anim.anim_slide_in_left,
//                        R.anim.anim_slide_out_left);
//
//                MySharedPreferences preferences = new MySharedPreferences();
//                preferences.setIsVideoPLaying(true);
//
//            } else {
//                ToastUtils.displayToast(activity.getString(R.string.video_not_found), activity);
//            }
//        } catch (Exception e) {
//            Logger.logE(TAG, " playVideo : ", e);
//        }
//    }
//
//
//
//    public static int findScreenSize(Context context) {
//        return context.getResources().getConfiguration().screenLayout &
//                Configuration.SCREENLAYOUT_SIZE_MASK;
//
//    }
//
//    public static String getDeviceName() {
//        String manufacturer = Build.MANUFACTURER;
//        String model = Build.MODEL;
//        String deviceName = "";
//        if (model.startsWith(manufacturer)) {
//            deviceName = capitalize(model);
//            Logger.logD(TAG, " deviceName " + deviceName);
//        } else {
//            deviceName = capitalize(manufacturer) + " " + model;
//            Logger.logD(TAG, " deviceName " + deviceName);
//        }
//        return deviceName;
//    }
//
//
//    private static String capitalize(String s) {
//        if (s == null || s.length() == 0) {
//            return "";
//        }
//        char first = s.charAt(0);
//        if (Character.isUpperCase(first)) {
//            return s;
//        } else {
//            return Character.toUpperCase(first) + s.substring(1);
//        }
//    }
//
//    public static String getFileNameFromURL(String url) {
//        if (url == null) {
//            return "";
//        }
//
//        int startIndex = url.lastIndexOf('/') + 1;
//        int length = url.length();
//
//        // find end index for ?
//        int lastQMPos = url.lastIndexOf('?');
//        if (lastQMPos == -1) {
//            lastQMPos = length;
//        }
//
//        // find end index for #
//        int lastHashPos = url.lastIndexOf('#');
//        if (lastHashPos == -1) {
//            lastHashPos = length;
//        }
//
//        // calculate the end index
//        int endIndex = Math.min(lastQMPos, lastHashPos);
//        return url.substring(startIndex, endIndex);
//    }
//
//    public static String getDateTime() {
//
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
//        Date date = new Date();
//        return simpleDateFormat.format(date);
//
//    }
//
//
//    public static boolean canDisplayPDF(Activity activity, File file) {
//        PackageManager packageManager = activity.getPackageManager();
//        Intent i;
//        if (Build.VERSION.SDK_INT >= 24) {
//            i = new Intent(Intent.ACTION_VIEW, FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider", file));
//            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        } else {
//            i = new Intent(Intent.ACTION_VIEW);
//            i.setDataAndType(Uri.fromFile(file), "application/pdf");
//        }
//        return packageManager.queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
//    }
//
//
//    public static void hideSoftKeyboard(Activity activity) {
//        InputMethodManager inputMethodManager =
//                (InputMethodManager) activity.getSystemService(
//                        Activity.INPUT_METHOD_SERVICE);
//        if (activity.getCurrentFocus() != null && inputMethodManager != null) {
//            inputMethodManager.hideSoftInputFromWindow(
//                    activity.getCurrentFocus().getWindowToken(), 0);
//        }
//
//    }
//
//    public static float getWidthOfScreen(Context context) {
//        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
//        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
//        Log.e("Width", "" + dpWidth);
//        return dpWidth;
//    }
//
//    public static int getNoOfColumnForGridVideo(float widthOfScreen, int widthForOneGrid) {
//
//        float widthForVideoPart = widthOfScreen - widthOfScreen * 15 / 100;
//        Log.e("No Of column", "" + widthForVideoPart / widthForOneGrid);
//        return (int) widthForVideoPart / widthForOneGrid;
//
//    }
//
//    public static void showNotificationForDownload(Context context, String videoName) {
//
//        mBuilder = new NotificationCompat.Builder(context, "101")
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentTitle("OELP")
//                .setContentText("Video downloading file " + videoName)
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
////                .setContentIntent(pendingIntent)
//                .setAutoCancel(true);
//    }
//
//    private static NotificationCompat.Builder mBuilder;
//
//    public static void updateNotification(String percent, String fileName, Context context) {
//        try {
//            mBuilder.setContentText(fileName)
//                    .setContentTitle("OELP")
//                    //.setSmallIcon(android.R.drawable.stat_sys_download)
//                    .setOngoing(true)
//                    .setContentInfo(percent + "%")
//                    .setProgress(100, Integer.parseInt(percent), false);
//
//            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
//
//// notificationId is a unique int for each notification that you must define
//            notificationManager.notify(101, mBuilder.build());
//            if (Integer.parseInt(percent) == 100) {
//                notificationManager.cancel(101);
//                mBuilder = null;
//            }
//
//        } catch (Exception e) {
//
//            Logger.logE("Error...Notification.", e.getMessage() + ".....",e);
//
//        }
//
//    }
}
