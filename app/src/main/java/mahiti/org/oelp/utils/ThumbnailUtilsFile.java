package mahiti.org.oelp.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;

/**
 * Created by RAJ ARYAN on 2019-10-21.
 */
public class ThumbnailUtilsFile {

    /**
     * API for creating thumbnail from Video
     * @param filePath - video file path
     * @param type - size MediaStore.Images.Thumbnails.MINI_KIND or MICRO_KIND
     * @return thumbnail bitmap
     */
    public Bitmap createThumbnailFromPath(String filePath, int type){
        return ThumbnailUtils.createVideoThumbnail(filePath, type);
    }


    /**
     * API for generating Thumbnail from particular time frame
     * @param filePath - video file path
     * @param timeInSeconds - thumbnail to generate at time
     * @return- thhumbnail bitmap
     */
    private Bitmap createThumbnailAtTime(String filePath, int timeInSeconds){
        MediaMetadataRetriever mMMR = new MediaMetadataRetriever();
        mMMR.setDataSource(filePath);
        //api time unit is microseconds
        return mMMR.getFrameAtTime(timeInSeconds*1000000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
    }

    /**
     * API for creating thumbnail of specified dimensions from an image file
     * @param filePath - source image path
     * @param width - output image width
     * @param height - output image height
     * @return - thumbnail bitmap of specified dimension
     */
    private Bitmap createThumbnailFromBitmap(String filePath, int width, int height){
        return ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(filePath), width, height);
    }

    /**
     * API for creating thumbnail from existing Bitmap
     * @param source - source Bitmap file
     * @param width - output image width
     * @param height - output image height
     * @return - thumbnail bitmap of specified dimension
     */
    private Bitmap createThumbnailFromBitmap(Bitmap source, int width, int height){
        //OPTIONS_RECYCLE_INPUT- Constant used to indicate we should recycle the input
        return ThumbnailUtils.extractThumbnail(source, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
    }
}
