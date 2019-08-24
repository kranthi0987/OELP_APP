package mahiti.org.oelp.fileandvideodownloader;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import mahiti.org.oelp.utils.Logger;


/**
 * Updated by mahiti on 24/02/16.
 */
public class ToastUtils {

    private ToastUtils() {
        /*
        Private constructor
         */
    }

    /**
     * public static void displayToast(final String message, final Context context) {
     * if (context != null) {
     * <p>
     * ((Activity) context).runOnUiThread(new Runnable() {
     * public void run() {
     * Toast t1 = Toast.makeText(context, message, Toast.LENGTH_LONG);
     * LinearLayout toastLayout = (LinearLayout) t1.getView();
     * TextView toastTV = (TextView) toastLayout.getChildAt(0);
     * toastTV.setTextSize(20);
     * t1.setGravity(Gravity.CENTER, 0, 0);
     * t1.show();
     * }
     * });
     * <p>
     * <p>
     * }
     * }
     */

    public static void displayToast(final String message, final Context context) {
        if (context != null) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    View view = ((Activity) context).getWindow().getDecorView().getRootView();
                    Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
                    (snackbar.getView()).getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                    snackbar.show();
                }
            });
        }
    }


    public static void displayToastUi(final String message, final Context context) {
        try {
            if (context != null && null != message && !"".equals(message)) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast t1 = Toast.makeText(context, message, Toast.LENGTH_LONG);
                        LinearLayout toastLayout = (LinearLayout) t1.getView();
                        TextView toastTV = (TextView) toastLayout.getChildAt(0);
                        toastTV.setTextSize(20);
                        t1.setGravity(Gravity.CENTER, 0, 0);
                        t1.show();
                    }
                });

            }
        } catch (Exception e) {
            Logger.logE(ToastUtils.class.getSimpleName(), "Exception in displaying toast", e);
        }
    }

}