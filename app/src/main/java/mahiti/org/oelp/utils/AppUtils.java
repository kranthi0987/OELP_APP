package mahiti.org.oelp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mahiti.org.oelp.R;
import mahiti.org.oelp.database.DatabaseHandlerClass;
import mahiti.org.oelp.views.activities.AboutUsActivity;
import mahiti.org.oelp.views.activities.MobileLoginActivity;

/**
 * Created by sandeep HR on 19/12/18.
 */
public class AppUtils {


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

    public static String getDateTime() {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = new Date();
        return simpleDateFormat.format(date);

    }

    public static String getDate() {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        return simpleDateFormat.format(date);

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

    public static String subPath(int type) { // 0-Image, 1- Pdf, 2- Video
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

    public static void makeUserLogout(Context context) {
        DatabaseHandlerClass dbHandler = new DatabaseHandlerClass(context);
        dbHandler.deleteAllDataFromDB(1);
        dbHandler.deleteAllDataFromDB(2);
        dbHandler.deleteAllDataFromDB(3);
        dbHandler.deleteAllDataFromDB(4);
        dbHandler.deleteAllDataFromDB(5);
        dbHandler.deleteAllDataFromDB(6);
        dbHandler.deleteAllDataFromDB(7);
        new MySharedPref(context).deleteAllData();
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
}




