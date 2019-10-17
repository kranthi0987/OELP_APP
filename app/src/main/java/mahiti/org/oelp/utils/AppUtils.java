package mahiti.org.oelp.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;
import java.sql.Struct;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.loader.content.CursorLoader;
import mahiti.org.oelp.R;
import mahiti.org.oelp.database.DBConstants;
import mahiti.org.oelp.database.DatabaseHandlerClass;
import mahiti.org.oelp.models.UserDetailsModel;
import mahiti.org.oelp.ui.StartUI;
import mahiti.org.oelp.views.activities.AboutUsActivity;
import mahiti.org.oelp.views.activities.MobileLoginActivity;

/**
 * Created by sandeep HR on 19/12/18.
 */
public class AppUtils {


    private static final String TAG = AppUtils.class.getSimpleName();

    private AppUtils() {
        /*
        Empty Constructor
         */
    }

//    private static KProgressHUD hud;

//    public static void showProgressDialog(Context context) {
//
//        hud = KProgressHUD.create(context)
//                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
//                .setAnimationSpeed(2)
//                .setDimAmount(0.5f)
//                .show();
//    }

//    public static void dismissProgressDialog() {
//        hud.dismiss();
//    }


    public static String checkMobileValidation(Context context, String mobilenumber) {
        String mobileRegex = "^[6-9]\\d{9}$";
        String message = "";
        if (mobilenumber == null)
            return context.getString(R.string.please_enter_mobile_number);

        if (TextUtils.isEmpty(mobilenumber) && mobilenumber.equalsIgnoreCase("")) {
            message = context.getString(R.string.please_enter_mobile_number);
        } else {
            Matcher matcherObj = Pattern.compile(mobileRegex).matcher(mobilenumber);
            if (mobilenumber.length() < 10) {
                message = context.getString(R.string.please_enter_valid_mobile_number);
            } else if (!matcherObj.matches()) {
                message = context.getString(R.string.please_enter_valid_mobile_number);
            } else {
                message = Constants.STATUS_TRUE;
            }
        }
        return message;
    }

    public static void showChatUI(Context context) {
        Intent intent = new Intent(context, StartUI.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.anim_slide_in_left,
                R.anim.anim_slide_out_left);
    }


    public static String getDateTime() {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.000000");
        Date date = new Date();
        return simpleDateFormat.format(date);

    }

    public static String getDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        return simpleDateFormat.format(date);

    }

    public static void copyDataBase( Context mContext, String destinationFolder) {
        File destDbFile = new File(destinationFolder);
        if (!destDbFile.exists()) {
            try {
                InputStream assestDB = mContext.getAssets().open(DBConstants.DB_NAME);
                OutputStream appDB = new FileOutputStream(destinationFolder, false);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = assestDB.read(buffer)) > 0) {
                    appDB.write(buffer, 0, length);
                }
                appDB.flush();
                appDB.close();
                assestDB.close();

            } catch (IOException e) {

                e.printStackTrace();
            }
        }

    }


    public static String validateOtp(Context context, String otpString) {
        String message;
        if (TextUtils.isEmpty(otpString)) {
            message = context.getString(R.string.please_enter_OTP);
        } else if (otpString.length() < 4) {
            message = context.getString(R.string.please_enter_valid_OTP);
        } else
            message = Constants.STATUS_TRUE;
        return message;
    }

    public static String appVersionName(Context context) {
        String versionName = "";
        PackageManager manager = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(
                    context.getPackageName(), 0);
            versionName = info.versionName;
        } catch (Exception e) {
            Logger.logE(AppUtils.class.getSimpleName(), e.getMessage(), e);
        }
        return versionName;
    }

    public static void showBarDeterminateDialog(Context context) {

//        hud = KProgressHUD.create(context)
//                .setStyle(KProgressHUD.Style.BAR_DETERMINATE)
//                .setLabel("Please wait");
    }

    /*  private static void simulateProgressUpdate(String message, int progress, int fileSize) {
          hud.setMaxProgress(100);
          final Handler handler = new Handler();
          handler.postDelayed(new Runnable() {
              int currentProgress;

              @Override
              public void run() {
                  currentProgress += 1;
                  hud.setProgress(currentProgress);
                  if (currentProgress == 80) {
                      hud.setLabel("Almost finish...");
                  }
                  if (currentProgress < 100) {
                      handler.postDelayed(this, 50);
                  }
              }
          }, 100);
      }
  */
    public static boolean isSdPresent() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * @param item
     * @return
     */
    static String fetchFileName(String item) {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, "Survey_Files");
        return checkFileExistsNgetPath(file, item);
    }

    /**
     * @param item
     * @return
     */
    public static String getEncryptedFilename(String item) {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, "Survey_Records");
        return checkFileExistsNgetPath(file, item);
    }

    /**
     * @param file
     * @param item
     */

    public static String checkFileExistsNgetPath(File file, String item) {
        if (!file.exists())
            file.mkdirs();
        String targetFile = replaceMethod(item, ".db", ".abc");
        String targetFile2 = replaceMethod(targetFile, "db_", "");
        return file.getAbsolutePath() + "/" + "Survey_" + targetFile2;
    }

    /**
     * @param item
     * @param source
     * @param dest
     * @return
     */
    public static String replaceMethod(String item, String source, String dest) {
        if (item.contains(source)) {
            return item.replace(source, dest);
        }
        return item;
    }

    /**
     * All fragments having blobk name, instruction. question and help text. Using bellow method the text will be setting
     *
     * @param label
     * @param data
     */
    public static void isEmptySetText(TextView label, String data) {
        if (data != null && !"".equalsIgnoreCase(data)) {
            label.setVisibility(View.VISIBLE);
            label.setText(data);
        } else {
            label.setVisibility(View.GONE);
        }
    }

    //spaning and underline
    public static void isEmptySetTextForHelpText(TextView label, String data) {
        if (data != null && !"".equalsIgnoreCase(data)) {
            label.setVisibility(View.VISIBLE);
            SpannableString spanString = new SpannableString(data);
            spanString.setSpan(new UnderlineSpan(), 0, spanString.length(), 0);
            spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
            spanString.setSpan(new StyleSpan(Typeface.ITALIC), 0, spanString.length(), 0);
            label.setText(data);
        } else {
            label.setVisibility(View.GONE);
        }
    }


    public static String getParentName(String file) {
        File fil = new File(file);
        return fil.getParent();
    }

    /**
     * Sets ListView height dynamically based on the height of the items.
     *
     * @param listView to be resized
     * @return true if the listView is successfully resized, false otherwise
     */
    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();

            return true;

        } else {
            return false;
        }

    }

    public static boolean textEmpty(String text) {

        return text != null && !text.isEmpty() ? false : true;

    }

    public static File completePathInSDCard(int type) {
        return new File(Environment.getExternalStorageDirectory().getAbsolutePath(), subPath(type));

    }

    public static File completeInternalStoragePath(Context context, int type) {
        return new File(context.getFilesDir(), subPath(type));

    }

    public static String getRealPathFromURI_API19(Context context, Uri uri){
        String filePath = "";

        try {
            String wholeID = DocumentsContract.getDocumentId(uri);

            // Split at colon, use second item in the array
            String id = wholeID.split(":")[1];

            String[] column = {MediaStore.Images.Media.DATA};

            // where id is equal to
            String sel = MediaStore.Images.Media._ID + "=?";

            Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    column, sel, new String[]{id}, null);

            int columnIndex = cursor.getColumnIndex(column[0]);

            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex);
            }
            cursor.close();
        }catch (Exception ex){
            Logger.logE(TAG, ex.getMessage(), ex);
        }
        return filePath;
    }


    public static String getRealPathFromURI_API11to18(Context context, Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        String result = null;

        CursorLoader cursorLoader = new CursorLoader(
                context,
                contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        if(cursor != null){
            int column_index =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
        }
        return result;
    }

    public static String getRealPathFromURI_BelowAPI11(Context context, Uri contentUri){
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        int column_index
                = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    public static String subPath(int type) {
        String subPath = "";
        switch (type) {
            case Constants.IMAGE:
//                subPath = File.separator + Constants.OELPIMAGE;
                subPath = Constants.OELPIMAGE;
                break;
            case Constants.PDF:
//                subPath = File.separator + Constants.OELPPDF;
                subPath = Constants.OELPPDF;
                break;
            case Constants.VIDEO:
//                subPath = File.separator + Constants.OELPVIDEO;
                subPath = Constants.OELPVIDEO;
                break;
            case Constants.AUDIO:
//                subPath = File.separator + Constants.OELPAUDIO;
                subPath = Constants.OELPAUDIO;
                break;
            default:
//                subPath = File.separator + Constants.OELPIMAGE;
                subPath = Constants.OELPIMAGE;
                break;
        }

        return subPath;
    }

    public static String getFileName(String imagePath) {
        String[] data = imagePath.split("/");
        return data[data.length - 1];
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            File dir2;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                dir2 = context.getCodeCacheDir();
                if (dir2 != null && dir2.isDirectory()) {
                    deleteDir(dir2);
                }
            }
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
                Logger.logD(TAG,"Cache cleared: "+dir.delete());
            }

        } catch (Exception e) {
            Logger.logE(TAG,"deleteCache",e);
        }
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

    public static void clearPreviousUserData(Context context){
        DatabaseHandlerClass dbHandler = new DatabaseHandlerClass(context);
        dbHandler.updateAllWatchStatus();
        dbHandler.deleteAllDataFromDB(5);
        dbHandler.deleteAllDataFromDB(6);
        dbHandler.deleteAllDataFromDB(7);
        dbHandler.deleteAllDataFromDB(8);
        MySharedPref sharedPref = new MySharedPref(context);
        /*sharedPref.deleteAllData();*/
        sharedPref.writeBoolean("ClearCheck",true);
    }


    public static void makeUserLogout(Context context) {
        MySharedPref sharedPref = new MySharedPref(context);
        sharedPref.writeBoolean(Constants.USER_LOGIN, false);
        String mobileNo = sharedPref.readString(Constants.MOBILE_NO, "");
        sharedPref.deleteAllData();
        sharedPref.writeString(Constants.MOBILE_NO, mobileNo);
        Intent intent = new Intent(context, MobileLoginActivity.class);
        context.startActivity(intent);
        ((AppCompatActivity) context).finish();
        ((AppCompatActivity) context).overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
    }

    public static void showAboutUsActivity(Context context) {
        Intent intent = new Intent(context, AboutUsActivity.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.anim_slide_in_left,
                R.anim.anim_slide_out_left);
    }

    public static String getUUID() {
        return UUID.randomUUID().toString();
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        if (imm != null)
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static int getFileType(String fileName ){
        int fileType = Constants.IMAGE;
        String lastFileExtension = fileName.substring(fileName.lastIndexOf("."));
        if (lastFileExtension.contains(".mp4") || lastFileExtension.contains(".3GP") || lastFileExtension.contains(".OGG")
                || lastFileExtension.contains(".WMV")||lastFileExtension.contains(".WEBM")||lastFileExtension.contains(".FLV")
                || lastFileExtension.contains(".AVI")||lastFileExtension.contains(".HDV")||lastFileExtension.contains(".MPEG4")){
            fileType = Constants.VIDEO;
        }else {
            fileType = Constants.IMAGE;
        }
        return fileType;
    }

    public static void checkImageFileLocation(Context context, List<File> imagesFiles) {
        for (File file : imagesFiles) {
            String fileName = AppUtils.getFileName(file.getAbsolutePath());
            File folderName = null;
            if (getFileType(fileName)==Constants.VIDEO){
                folderName = AppUtils.completeInternalStoragePath(context, Constants.VIDEO);
            }else {
                folderName = AppUtils.completeInternalStoragePath(context, Constants.IMAGE);
            }
            File fileKHPT = new File(folderName, fileName);
            if (!fileKHPT.exists()) {
                try {
                    AppUtils.copyFilesToInternalFolder(file, fileKHPT);
                } catch (IOException ex) {
                    Logger.logE(TAG, ex.getMessage(), ex);
                }
            }
        }
    }

    public static void copyFilesToInternalFolder(File sourceFile, File destFile) throws IOException {
        if (!sourceFile.exists()) {
            return;
        }

        FileChannel source=null;
        FileChannel destination=null;
        try {


            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            if (destination != null && source != null) {
                destination.transferFrom(source, 0, source.size());
            }

        } catch (Exception ex) {
            Logger.logE(TAG, ex.getMessage(), ex);
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

    public static boolean renameFileName(File oldFile, File newFile) {
        if(oldFile.renameTo(newFile)){
            System.out.println("File rename success");
            return true;
        }else{
            System.out.println("File rename failed");
            return false;
        }
    }

    public static void createDir(File file) {
        if (!file.exists()) {
            if (file.mkdir()) {
                System.out.println("Directory is created!");
            } else {
                System.out.println("Failed to create directory!");
            }
        }
    }

    public static String getRealPathFromURI_OreoAndAbove(Uri uri) {
        String filePath = null;
        if (uri.getPath()!=null) {
            File file = new File(uri.getPath());//create path from uri
            final String[] split = file.getPath().split(":");//split the path.
            filePath = split[1];//assign it to a string(your choice).
        }
        return filePath;
    }

    @SuppressLint("NewApi")
    public static String getRealPathFromURI_OreoBelow(Context context, Uri uri) throws URISyntaxException {
        final boolean needToCheckUri = Build.VERSION.SDK_INT >= 19;
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        // deal with different Uris.
        if (needToCheckUri && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{ split[1] };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { MediaStore.Images.Media.DATA };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static void chnageToString(MySharedPref sharedPref, List<String> userDetails) {
        String userData="";
        if (userDetails!=null){
            try {
                Gson gson = new Gson();
                userData = gson.toJson(userDetails);
                sharedPref.writeString(Constants.GROUP_UUID_LIST, userData);
            }catch (Exception ex){
                Logger.logE(TAG, ex.getMessage(), ex);
            }
        }

    }

    public static String makeJsonArray(String usergroup1) {
        String jsonArray = "";
        JSONArray array= new JSONArray();
        JSONObject obj;
        String use1 = usergroup1.replace("[","");
        String use = use1.replace("]","");
        String[] userGroupArray = use.split(",");
        try {
            for (int i =0; i<userGroupArray.length;i++){
                obj = new JSONObject();
                obj.put("group_uuid", userGroupArray[i]);
                array.put(obj);
            }
            jsonArray = array.toString();
        }catch (Exception ex){
            Logger.logE(TAG, "group uuid error: "+ex.getMessage(), ex);
        }

        return jsonArray;
    }

    /* Check whether this uri is a content uri or not.
     *  content uri like content://media/external/images/media/1302716
     *  */
    private static boolean isContentUri(Uri uri)
    {
        boolean ret = false;
        if(uri != null) {
            String uriSchema = uri.getScheme();
            if("content".equalsIgnoreCase(uriSchema))
            {
                ret = true;
            }
        }
        return ret;
    }

    /* Check whether this document is provided by google photos. */
    private static boolean isGooglePhotoDoc(String uriAuthority)
    {
        boolean ret = false;

        if("com.google.android.apps.photos.content".equals(uriAuthority))
        {
            ret = true;
        }

        return ret;
    }

    /* Return uri represented document file real local path.*/
    public static String getImageRealPath(ContentResolver contentResolver, Uri uri, String whereClause)
    {
        String ret = "";

        // Query the uri with condition.
        Cursor cursor = contentResolver.query(uri, null, whereClause, null, null);

        if(cursor!=null)
        {
            boolean moveToFirst = cursor.moveToFirst();
            if(moveToFirst)
            {

                // Get columns name by uri type.
                String columnName = MediaStore.Images.Media.DATA;

                if( uri==MediaStore.Images.Media.EXTERNAL_CONTENT_URI )
                {
                    columnName = MediaStore.Images.Media.DATA;
                }else if( uri==MediaStore.Audio.Media.EXTERNAL_CONTENT_URI )
                {
                    columnName = MediaStore.Audio.Media.DATA;
                }else if( uri==MediaStore.Video.Media.EXTERNAL_CONTENT_URI )
                {
                    columnName = MediaStore.Video.Media.DATA;
                }

                // Get column index.
                int imageColumnIndex = cursor.getColumnIndex(columnName);

                // Get column value which is the uri related file local path.
                ret = cursor.getString(imageColumnIndex);
            }
        }

        return ret;
    }

    /* Check whether this uri is a file uri or not.
     *  file uri like file:///storage/41B7-12F1/DCIM/Camera/IMG_20180211_095139.jpg
     * */
    private static boolean isFileUri(Uri uri)
    {
        boolean ret = false;
        if(uri != null) {
            String uriSchema = uri.getScheme();
            if("file".equalsIgnoreCase(uriSchema))
            {
                ret = true;
            }
        }
        return ret;
    }

    /* Check whether this uri represent a document or not. */
    private static boolean isDocumentUri(Context ctx, Uri uri)
    {
        boolean ret = false;
        if(ctx != null && uri != null) {
            ret = DocumentsContract.isDocumentUri(ctx, uri);
        }
        return ret;
    }

    /* Check whether this document is provided by MediaProvider. */
    private static boolean staisMediaDoc(String uriAuthority)
    {
        boolean ret = false;

        if("com.android.providers.media.documents".equals(uriAuthority))
        {
            ret = true;
        }

        return ret;
    }

    /* Check whether this document is provided by MediaProvider. */
    private static boolean isMediaDoc(String uriAuthority)
    {
        boolean ret = false;

        if("com.android.providers.media.documents".equals(uriAuthority))
        {
            ret = true;
        }

        return ret;
    }

    /* Check whether this document is provided by DownloadsProvider. */
    private static boolean isDownloadDoc(String uriAuthority)
    {
        boolean ret = false;

        if("com.android.providers.downloads.documents".equals(uriAuthority))
        {
            ret = true;
        }

        return ret;
    }

    /* Check whether this document is provided by ExternalStorageProvider. */
    private static boolean isExternalStoreDoc(String uriAuthority)
    {
        boolean ret = false;

        if("com.android.externalstorage.documents".equals(uriAuthority))
        {
            ret = true;
        }

        return ret;
    }





    public static String getUriRealPathAboveKitkat(FragmentActivity ctx, Uri uri) {
        String path=null;
        try{

            if(ctx != null && uri != null) {

                if(isContentUri(uri))
                {
                    if(isGooglePhotoDoc(uri.getAuthority()))
                    {
                        path = uri.getLastPathSegment();
                    }else {
                        path = getImageRealPath(ctx.getContentResolver(), uri, null);
                    }
                }else if(isFileUri(uri)) {
                    path = uri.getPath();
                }else if(isDocumentUri(ctx, uri)){

                    // Get uri related document id.
                    String documentId = DocumentsContract.getDocumentId(uri);

                    // Get uri authority.
                    String uriAuthority = uri.getAuthority();

                    if(isMediaDoc(uriAuthority))
                    {
                        String idArr[] = documentId.split(":");
                        if(idArr.length == 2)
                        {
                            // First item is document type.
                            String docType = idArr[0];

                            // Second item is document real id.
                            String realDocId = idArr[1];

                            // Get content uri by document type.
                            Uri mediaContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                            if("image".equals(docType))
                            {
                                mediaContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                            }else if("video".equals(docType))
                            {
                                mediaContentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                            }else if("audio".equals(docType))
                            {
                                mediaContentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                            }

                            // Get where clause with real document id.
                            String whereClause = MediaStore.Images.Media._ID + " = " + realDocId;

                            path = getImageRealPath(ctx.getContentResolver(), mediaContentUri, whereClause);
                        }

                    }else if(isDownloadDoc(uriAuthority))
                    {
                        // Build download uri.
                        Uri downloadUri = Uri.parse("content://downloads/public_downloads");

                        // Append download document id at uri end.
                        Uri downloadUriAppendId = ContentUris.withAppendedId(downloadUri, Long.valueOf(documentId));

                        path = getImageRealPath(ctx.getContentResolver(), downloadUriAppendId, null);

                    }else if(isExternalStoreDoc(uriAuthority))
                    {
                        String idArr[] = documentId.split(":");
                        if(idArr.length == 2)
                        {
                            String type = idArr[0];
                            String realDocId = idArr[1];

                            if("primary".equalsIgnoreCase(type))
                            {
                                path = Environment.getExternalStorageDirectory() + "/" + realDocId;
                            }
                        }
                    }
                }
            }

            return path;
        }catch (Exception ex){
            Logger.logE(TAG, ex.getMessage(), ex);
        }
        return null;
    }

//    public static UserDetails changeToModel(String userData){
//        UserDetails userDetails = null;
//        Gson gson;
//        try {
//           gson = new Gson();
//           userDetails = gson.fromJson(userData, UserDetails.class);
//        }
//    }

    public void showDocumentsView(Activity context) {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        String[] mimeTypes =
                {"image/*","application/pdf","video/mp4"};

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*"

        context.startActivityForResult(intent, Constants.READ_REQUEST_CODE);

    }

    public void renderFileView( Intent data) {
        Uri result = data.getData();
        ClipData resultMulti = data.getClipData();
        List<Uri> fileList = getFileList(result,resultMulti);
//        uiHelper.showFileView(fileList);
    }

    public static List<Uri> getFileList(Uri result, ClipData resultMulti) {
        List<Uri> list = new ArrayList<>();
        if (resultMulti == null) {
            Uri filePath = result;
            list.add(filePath);

        } else {
            for (int i = 0; i < resultMulti.getItemCount(); i++) {
                ClipData.Item path = resultMulti.getItemAt(i);
                Uri fileUti = path.getUri();
                list.add(fileUti);
            }
        }
        return list;
    }




}




