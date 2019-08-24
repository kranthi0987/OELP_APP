package oelp.mahiti.org.newoepl.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class Utils {
    public static int pixelToDp(Context context, int pixel) {
        float density = context.getResources().getDisplayMetrics().density;
        float dp = pixel / density;
        return (int) dp;
    }

    public static int DpToPixel(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        float px = dp * density;
        return (int) px;
    }

   /* public static String imagePath(Context context) {
        // internal file path
        String path = context.getFilesDir() + "image_" + System.currentTimeMillis() + ".jpg";
        return path;
    }

    public static String getRealPathFromURI(Uri contentURI, Context context) {
        String result;
        try {
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            result = cursor.getString(columnIndex);
            cursor.close();
            return result;
        } catch (Exception e) {
            return null;

        }
    }*/

}
