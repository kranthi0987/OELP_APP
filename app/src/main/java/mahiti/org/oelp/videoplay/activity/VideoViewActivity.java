package mahiti.org.oelp.videoplay.activity;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONArray;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import mahiti.org.oelp.R;
import mahiti.org.oelp.database.DatabaseHandlerClass;
import mahiti.org.oelp.utils.CheckNetwork;
import mahiti.org.oelp.utils.Constants;
import mahiti.org.oelp.utils.MySharedPref;
import mahiti.org.oelp.videoplay.UpdateDbInterface;
import mahiti.org.oelp.videoplay.api.VideoAPI;
import mahiti.org.oelp.videoplay.tracker.MediaTracker;
import mahiti.org.oelp.videoplay.utils.CheckNet;
import mahiti.org.oelp.videoplay.utils.DecryptClass;
import mahiti.org.oelp.videoplay.utils.SevendaysVariables;
import mahiti.org.oelp.videoplay.utils.Validation;
import mahiti.org.oelp.videoplay.utils.VideoDecryptionDb;
import mahiti.org.oelp.views.activities.QuestionAnswerActivity;


public class VideoViewActivity extends AppCompatActivity implements SevendaysVariables,
        MediaPlayer.OnErrorListener, UpdateDbInterface, MediaPlayer.OnPreparedListener,
        View.OnClickListener, MediaPlayer.OnInfoListener, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnSeekCompleteListener {
    private static final String TAG = "VIDEOVIEWACTIVITY";
    private static final int REQUEST_CODE_GALLERY = 0x1;
    private static final int REQ_CODE_PHONESTATE = 123;
    MediaPlayer mediaPlayer;
    VideoDecryptionDb videoDecryptionDb;
    VideoAPI videoAPI;
    DecryptClass de;
    File srcPath;
    MediaTracker mediaTracker;
    String path;
    int videoMins = 0;
    //    MediaTrackerApi mediaTrackerApi;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    CheckNet checkNet;
    int videoId = 0;
    private VideoView videoView;
    private KProgressHUD hud;
    private String mediaApiUrl;
    private int playingId;
    TextView toolbarTitle;
    RelativeLayout relativeBack;
    View actionBar;
    String userId;
    String unitId;
    String videoTitle;
    String sectionUUID;
    String parentUUID;
    String mediaUUID = "";
    String deviceId = "";
    private DatabaseHandlerClass catalogDbHandler;
    private String errorMessage;


    @SuppressLint("DefaultLocale")
    static String getTimeString(Long millis) {
        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

    public static String getRealPathFromURI_API11to18(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        String result = null;

        CursorLoader cursorLoader = new CursorLoader(
                context,
                contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        if (cursor != null) {
            int column_index =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
        }
        return result;
    }

    RelativeLayout rlMain;
    MediaController mediaController;
    MySharedPref mySharedPref;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);
        mySharedPref = new MySharedPref(this);
        catalogDbHandler = new DatabaseHandlerClass(this);
        actionBar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbarTitle);
        relativeBack = findViewById(R.id.relativeBack);
        mediaController = new MediaController(this);
        videoView = (VideoView) findViewById(R.id.video_view);
        deviceId = getDeviceID();
        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();

        relativeBack.setOnClickListener(view -> onBackPressed());

        rlMain = findViewById(R.id.rlMain);
        mediaController.show(3000);


        videoAPI = new VideoAPI(this);
        videoDecryptionDb = new VideoDecryptionDb(this);
//        mediaTrackerApi = new MediaTrackerApi(this);

        if (videoView != null) {
            videoView.setOnCompletionListener(this);
            videoView.setOnErrorListener(this);
            videoView.canSeekBackward();
            videoView.canSeekForward();

        }
        mediaPlayer = new MediaPlayer();
        sharedPreferences = getApplicationContext().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        checkNet = new CheckNet(this);
        getIntentData();

        if (CheckNetwork.checkNet(this)) {
            callMediaTrackerApi();
        }
        putCompletedStatusForVideo(mediaUUID);

    }

    private void putCompletedStatusForVideo(String mediaUUID) {
//        catalogDbHandler.insertViewStatusToDatabase(mediaUUID, "1");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    private void getIntentData() {
        try {


            if (!TextUtils.isEmpty(getIntent().getStringExtra("mediaUUID"))) {
                mediaUUID = getIntent().getStringExtra("mediaUUID");
                Log.i("mediaUUID", mediaUUID);
            }


            /* getting video name and setting to title*/

            if (!TextUtils.isEmpty(getIntent().getStringExtra("videoTitle"))) {
                videoTitle = getIntent().getStringExtra("videoTitle");
                toolbarTitle.setText(videoTitle);
                Log.i("videoname", videoTitle);
            }

            if (!TextUtils.isEmpty(getIntent().getStringExtra("sectionUUID"))) {
                sectionUUID = getIntent().getStringExtra("sectionUUID");
                Log.i("sectionUUID", sectionUUID);
            }

            if (!TextUtils.isEmpty(getIntent().getStringExtra("unitId"))) {
                unitId = getIntent().getStringExtra("unitId");
                Log.i("unitId", unitId);
            }

            /*getting user id from Intent*/

            if (!TextUtils.isEmpty(getIntent().getStringExtra("userId")))
                userId = getIntent().getStringExtra("userId");
//            Log.i("userId", userId);

            /*Getting Media Tracker Api*/

            if (!TextUtils.isEmpty(getIntent().getStringExtra("mediaTrackerApi")))
                mediaApiUrl = getIntent().getStringExtra("mediaTrackerApi");
//            Log.i("MediaTrackerApi", mediaApiUrl);

            if (getIntent().getStringExtra("uriPath").isEmpty() || getIntent().getStringExtra("uriPath").contentEquals("")) {
                Toast.makeText(this, "File path is empty! please check.", Toast.LENGTH_LONG).show();
                finish();
                overridePendingTransition(R.anim.anim_slide_in_right,
                        R.anim.anim_slide_out_right);

            } else {
                path = getIntent().getStringExtra("uriPath");
                Log.i("uriPath", path);
                srcPath = new File(path);

                if (srcPath.exists()) {
                    videoId = getIntent().getIntExtra("videoId", 0);
                    Log.i("VideoId", "" + videoId);
                    videoSelected();
                } else {
                    Toast.makeText(this, "Not able to get File from this path.", Toast.LENGTH_LONG).show();
                    finish();
                    overridePendingTransition(R.anim.anim_slide_in_right,
                            R.anim.anim_slide_out_right);
                }
            }
            /*    Hitting Api for Update the data for Video*/
            if (checkNet.haveNetworkConnection()) {
                JSONArray ar = getMediaDetails();
//                if (ar.length() > 0)
//                    mediaTrackerApi.mediaTracking(mediaApiUrl, userId, ar, VideoViewActivity.this, deviceId, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @SuppressLint("HardwareIds")
    private String getDeviceID() {
        String deviceId = "";
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            assert telephonyManager != null;
            deviceId = telephonyManager.getDeviceId();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 100);
        }
        return deviceId;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void onPrepared(MediaPlayer mp) {
        try {
            videoMins = ((mp.getDuration() % (1000 * 60 * 60)) % (1000 * 60) / 1000);
            mediaTracker = new MediaTracker();
            mediaTracker.setStartTime(getDateTime());
            mediaTracker.setEndTime("0");                   //update
            mediaTracker.setMediaId(mediaUUID);
            mediaTracker.setWatchMin("0");                  //update
            mediaTracker.setComStatus(0);
            mediaTracker.setMediaName("");

            playingId = (int) videoDecryptionDb.mediaTrackerInsert(mediaTracker);

            editor = sharedPreferences.edit();
            editor.putInt("id", playingId);
            editor.apply();
            hud.dismiss();
            mediaPlayer = mp;
            videoView.start();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                videoView.setOnInfoListener(this);
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private String getDateTime() {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        // Log.i("VDB "," Date "+simpleDateFormat.format(date));
        return simpleDateFormat.format(date);

    }

    @Override
    public void onClick(View v) {
        finish();
        overridePendingTransition(R.anim.anim_slide_in_right,
                R.anim.anim_slide_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        updateOnVideoWatching();
        if (CheckNetwork.checkNet(this)) {
            callMediaTrackerApi();
        }
//        contentUpdateStatus(mediaUUID);
        finish();
    }


    private String getModifiedDate(int tableType) {
//        return catalogDbHandler.getModifiedDate(tableType);
        return null;
    }

    private void updateOnVideoWatching() {

        if (mediaTracker == null)
            return;

        mediaTracker.setStartTime("");
        mediaTracker.setEndTime(getDateTime());
        mediaTracker.setMediaId(mediaUUID);
        mediaTracker.setWatchMin(getTimeString((long) mediaPlayer.getCurrentPosition()));
        mediaTracker.setComStatus(0);
        mediaTracker.setMediaName("");
//todo
        videoDecryptionDb.mediaTrackerUpdateDb(mediaTracker, mediaUUID);


    }


    public JSONArray getMediaDetails() {
        videoDecryptionDb = new VideoDecryptionDb(this);
        JSONArray array = videoDecryptionDb.getMediaDetails();
        Log.i("MediaTrackerJson", "" + array.toString());
        return array;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    void videoSelected() {
        try {
            Log.d("selected Path", "selected Path 1 " + path);
            if (path == null || path.isEmpty())
                return;
            if (srcPath.isDirectory()) {
                errorMessage = "Input should be a File";
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                onErrorEncounter(errorMessage);
            } else {
                String fname = srcPath.getName();
                Log.i(TAG, " selcted fileName : " + fname);
                displayDirectoryContents(srcPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onErrorEncounter(String errorMessage) {
        srcPath.deleteOnExit();
//        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        finish();
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
            srcPath.deleteOnExit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_GALLERY && data != null) {
                path = getRealPathFromURI_API11to18(this, data.getData());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateDB() {
        updateDbAfterApi();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQ_CODE_PHONESTATE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        this.finish();
                        return;
                    }
                    getIntentData();
//
                }
            }

        }
    }

    public void contentUpdateStatus(String uuid) {
        if (!uuid.isEmpty()) {
            DatabaseHandlerClass catalogDBHandler = new DatabaseHandlerClass(VideoViewActivity.this);
            int getInsertedPD = catalogDBHandler.insertViewStatusToDatabase(uuid, "1");
            Log.i("getInsertedPD", getInsertedPD + "");
        }
    }

    private void updateDbAfterApi() {
        displayDirectoryContents(srcPath);
    }

    public void trackerUpdate(Validation validation) {
        if (validation.getStatus() == 1) {
            videoDecryptionDb.delMediaTrackerRecords();
        }
    }

    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    //
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        int duration = mediaPlayer.getDuration();
        Log.i(TAG, "onCompletion Track Info " + duration);

        mediaTracker = new MediaTracker();
        mediaTracker.setStartTime(getDateTime());
        mediaTracker.setEndTime(getDateTime());
        mediaTracker.setMediaId(mediaUUID);
        mediaTracker.setComStatus(0);
        mediaTracker.setWatchMin(String.valueOf(getTimeString((long) mediaPlayer.getDuration())));
        mediaTracker.setMediaName("");

        videoDecryptionDb.mediaTrackerUpdateDb(mediaTracker, mediaUUID);


        if (CheckNetwork.checkNet(this)) {
            callMediaTrackerApi();
        }
//        contentUpdateStatus(mediaUUID);
        boolean loginType = new MySharedPref(this).readInt(Constants.USER_TYPE, Constants.USER_TEACHER) == Constants.USER_TEACHER;
        if (!getIntent().getStringExtra("sectionUUID").isEmpty() && loginType)
            moveToQuestionAnswerActivity();
        else
            onBackPressed();
    }

    private void moveToQuestionAnswerActivity() {
        Intent intent = new Intent(VideoViewActivity.this, QuestionAnswerActivity.class);
        intent.putExtra("mediaUUID", mediaUUID);
        intent.putExtra("sectionUUID", sectionUUID);
        intent.putExtra("videoTitle", videoTitle);
        startActivity(intent);
        VideoViewActivity.this.finish();
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
    }

    public void callMediaTrackerApi() {
//        JSONArray ar = getMediaDetails();
//        if (ar.length() > 0) {
//            mediaTrackerApi.mediaTracking(DBConstants.MEDIA_TRACKER_API, String.valueOf(new MySharedPref(this).readInt(Constants.USER_ID, 0)), getMediaDetails(), this, deviceId, true);
//        }
    }


    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {
        int duration = mediaPlayer.getDuration();
        Log.i(TAG, "onSeekComplete Track Info" + duration);

        int secsPlayed = mediaPlayer.getCurrentPosition();
        Log.i(TAG, "onSeekComplete Track Info" + secsPlayed);

    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        Log.i(TAG, " onError Track Info" + i);
        Log.i(TAG, " onError Track Info" + i1);
        onErrorEncounter("Error in Media Playing");
        return false;
    }

    private void displayDirectoryContents(File file) {
        try {
            if (file != null) {
                switchOnplaying(file.getPath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void switchOnplaying(String vUrl) {
        try {

            mediaController = new MediaController(VideoViewActivity.this);
            mediaController.setAnchorView(videoView);
            Uri video = Uri.parse(vUrl);
            videoView.setMediaController(mediaController);
            videoView.setVideoURI(video);
        } catch (Exception e) {
            Log.e("VVActivity", " switchonPlaying  ", e);
        }
        videoView.requestFocus();
        videoView.setOnPreparedListener(this);
        videoView.setKeepScreenOn(false);

    }

}


