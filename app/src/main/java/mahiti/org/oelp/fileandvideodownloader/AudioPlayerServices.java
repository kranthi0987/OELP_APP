package mahiti.org.oelp.fileandvideodownloader;

import android.app.IntentService;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.annotation.Nullable;

/**
 * Created by RAJ ARYAN on 04/06/19.
 */
public class AudioPlayerServices extends IntentService implements MediaPlayer.OnPreparedListener , MediaPlayer.OnErrorListener{
    private static final String ACTION_PLAY = "com.example.action.PLAY";
    MediaPlayer mediaPlayer = null;

    public AudioPlayerServices(String name) {
        super(name);
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
//        MediaPlayer mediaPlayer = new MediaPlayer();
//        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        mediaPlayer.setDataSource(context, i);
//        mediaPlayer.prepare();
//        mediaPlayer.start();
//        mediaPlayer = ... // initialize it here
//        mediaPlayer.setOnPreparedListener(this);
//        mediaPlayer.prepareAsync();
//        initMediaPlayer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /** Called when MediaPlayer is ready */
    public void onPrepared(MediaPlayer player) {
        player.start();
    }

    public void initMediaPlayer() {
        // ...initialize the MediaPlayer here...
        mediaPlayer.setOnErrorListener(this);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        // ... react appropriately ...
        // The MediaPlayer has moved to the Error state, must be reset!
        return false;
    }
}
