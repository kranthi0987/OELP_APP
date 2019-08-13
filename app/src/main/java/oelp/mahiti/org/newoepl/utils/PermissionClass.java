package oelp.mahiti.org.newoepl.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mahiti on 19/01/17.
 */

public class PermissionClass {

    private PermissionClass() {
        /*
        private constructor
         */
    }

    public static boolean checkPermission(Activity activity) {

        int externalRead = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        int externalWrite = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int internet = ContextCompat.checkSelfPermission(activity, Manifest.permission.INTERNET);
        int networkstate = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_NETWORK_STATE);
        int readphonestate = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE);
        int readsms = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_SMS);
        int recievesms = ContextCompat.checkSelfPermission(activity, Manifest.permission.RECEIVE_SMS);

        return externalRead == PackageManager.PERMISSION_GRANTED &&
                externalWrite == PackageManager.PERMISSION_GRANTED &&
                internet == PackageManager.PERMISSION_GRANTED &&
                networkstate == PackageManager.PERMISSION_GRANTED &&
                readphonestate == PackageManager.PERMISSION_GRANTED &&
                readsms == PackageManager.PERMISSION_GRANTED &&
                recievesms == PackageManager.PERMISSION_GRANTED;

    }

    public static void requestPermission(Activity activity) {

        List<String> list = new ArrayList();
        if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE))
            list.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            list.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.INTERNET))
            list.add(Manifest.permission.INTERNET);

        String[] stockArr = new String[list.size()];
        stockArr = list.toArray(stockArr);
        if (stockArr.length != 0) {
            ActivityCompat.requestPermissions(activity, stockArr, 1);
        }

    }


}
