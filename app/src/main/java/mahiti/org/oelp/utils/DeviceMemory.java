package mahiti.org.oelp.utils;

import android.os.Environment;
import android.os.StatFs;

/**
 * Created by sandeep HR on 27/12/18.
 */
public class DeviceMemory {

    private DeviceMemory() {
        /*
        Empty Constructor
         */
    }


// Return size's in Megabytes

    /**
     * public static long getInternalStorageSpace()
     * {
     * StatFs statFs = new StatFs(Environment.getDataDirectory().getAbsolutePath());
     * long total = ((long)statFs.getBlockCount() * (long)statFs.getBlockSize()) / 1048576;
     * return total;
     * }
     * <p>
     * public static long getInternalFreeSpace()
     * {
     * StatFs statFs = new StatFs(Environment.getDataDirectory().getAbsolutePath());
     * //StatFs statFs = new StatFs("/data");
     * long free  = ((long)statFs.getAvailableBlocks() * (long)statFs.getBlockSize()) / 1048576;
     * return free;
     * }
     * // Return size's in Megabytes
     * public static long getExternalStorageSpace()
     * {
     * StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
     * long total = ((long)statFs.getBlockCount() * (long)statFs.getBlockSize()) / 1048576;
     * return total;
     * }
     */

    public static long getExternalFreeSpace() {
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        return ((long) statFs.getAvailableBlocks() * (long) statFs.getBlockSize()) / 1048576;

    }

    /**
     * public static long getInternalUsedSpace()
     * {
     * StatFs statFs = new StatFs(Environment.getDataDirectory().getAbsolutePath());
     * //StatFs statFs = new StatFs("/data");
     * long total = ((long)statFs.getBlockCount() * (long)statFs.getBlockSize()) / 1048576;
     * long free  = ((long)statFs.getAvailableBlocks() * (long)statFs.getBlockSize()) / 1048576;
     * long busy  = total - free;
     * return busy;
     * }
     * public static long getExternalUsedSpace()
     * {
     * StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
     * //StatFs statFs = new StatFs("/data");
     * long total = ((long)statFs.getBlockCount() * (long)statFs.getBlockSize()) / 1048576;
     * long free  = ((long)statFs.getAvailableBlocks() * (long)statFs.getBlockSize()) / 1048576;
     * long busy  = total - free;
     * return busy;
     * }
     */
    public static long convertMegaBytesToBytes(long mb) {
        return mb * 1024 * 1024;

    }

}

