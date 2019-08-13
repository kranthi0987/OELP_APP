package oelp.mahiti.org.newoepl.fileandvideodownloader;

import java.util.regex.Pattern;

/**
 * Created by sandeep HR on 03/05/19.
 */
public class DownloadConstant {
    public static final String ERROR_WHILE_DOWN_FROM_SERVER = "Error while downloading From Server";

    public static int VideoFile=101;
    public static int ImageFile=102;
    public static int AudioFile=103;
    public static int DBZipFile=102;
    public static String Slash="/";
    public static final int VIDEO_DOWNLOADED = 1;
    public static final int VIDEO_PARTIAL_DOWNLOADED = 0;
    public static final String CHANNEL_ID = "101";

    public static final String USER_REGISTERED = "userRegistered";
    public static final String CHECK_INTERNET = "ृपया इंटरनेट कनेक्शन की जाँच करें और पुनः प्रयास करें!";



    public static final String USER_ID = "userid";
    public static final String USER_MOBILE = "usermobile";
    public static final String USER_DATA = "userdata";
    public static final String USER_OTP = "1111";
    public static final String DB_URL = "db_url";
    public static final String USER_DETAILS = "userdetails";
    public static final String INTRO_COUNT = "intropagecount";
    public static final int GRID_NO_OF_COLUMNS = 3;
    public static final long CONSTANT_TO_CONVERT_BYTES = 1024;
    /** The default socket timeout in seconds */
    public static final int DEFAULT_TIMEOUT_SEC = 90;

    /** The default number of retries */
    public static final int DEFAULT_MAX_RETRIES = 0;

    /** The default backoff multiplier */
    public static final float DEFAULT_BACKOFF_MULT = 1f;
    //    public static final String KHPT = "OELP-Student";
    public static final String KHPTPDF = "KHPT/PDF";
    public static final String KHPTIMAGE = "KHPT/Image";
    public static final String KHPTVIDEO = "KHPT/Video";
    public static final String KHPTAUDIO = "KHPT/Audio";

    public static final String SUB_PATH_INSIDE_ZIP_FILE = "home/mahiti/OELP/static/DB";
    public static final String VIDEO_BASE_URL = "http://oelp.mahiti.org/";
    public static final String ANDROID_IMEI_ID = "android_imeiId";
    public static final String ANDROID_DEVICE_ID = "android_deviceId";
    public static final String ISUSERLOGIN = "isuserlogin";
    public static final int QA = 2;
    public static final int FAQ = 1;
    public static final String COMPLETEDVIDEOID = "completed_video_id";
    public static final int QUESTION_ANSWER_SYNC = 1;
    public static final int QUESTION_ANSWER_ASYNC = 0;
    public static final String OFFLINE_DATA_SYNC_MESSAGE = "You are offline , Data Synced offline";
    public static final String COMINGSOON = "Coming Soon";
    public static final String INV_ID = "inv_id";
    public static final String MOBILE = "mobile";
    public static final String ACTIVE_STATUS = "activestatus";
    public static final String DOWNLOADINGVIDEO = "downloading_video";
    public static final String VIDEOLIST = "videoidlist";
    public static final String ISVIDEOPLAYING= "isvideoplaying";
    public static final String DOWNLAODEDUNPLAYEDVIDEOLIST = "downloaded_unplayed_video_list";


    public static boolean isValid(String email)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    public static final String SUCCESS = "Success";
    public static final String BAD_REQUEST = "Bad Request";
    public static final String UNAUTHORISED = "Unauthorized";
    public static final String FORBIDDEN = "Forbidden";
    public static final String NOT_FOUND = "Not Found";
    public static final String REQ_TIME_OUT = "Request time out";
    public static final String INTERNAL_SERVER_ERROR = "Internal Server Error";
    public static final String BAD_GATEWAY = "Bad Gateway";
    public static final String SERVICE_UNAVAILABLE = "Service unavailable";
    public static final String GATEWAY_TIMEOUT = "Gateway time out";
    public static final String SOMETHING_WRONG = "कुछ गलत हो गया";

}
