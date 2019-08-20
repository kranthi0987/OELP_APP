package oelp.mahiti.org.newoepl.fileandvideodownloader;

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

public class DownloadPermissionClass {

    public static boolean checkPermission(Activity activity) {

        int externalRead = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        int externalWrite = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int internet = ContextCompat.checkSelfPermission(activity, Manifest.permission.INTERNET);
        int networkState = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_NETWORK_STATE);

        if (externalRead != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        if (externalWrite != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        if (internet != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        if (networkState != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;

    }

    public static void requestPermission(Activity activity) {

        List<String> list = new ArrayList();
        if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            list.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE))
            list.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_NETWORK_STATE))
            list.add(Manifest.permission.ACCESS_NETWORK_STATE);
        String[] stockArr = new String[list.size()];
        stockArr = list.toArray(stockArr);
        if (stockArr.length != 0) {
            ActivityCompat.requestPermissions(activity, stockArr, 1);
        }
    }
}
