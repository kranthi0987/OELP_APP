package mahiti.org.oelp;

import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;

import android.content.Context;

import androidx.multidex.MultiDexApplication;

public class MyApplication extends MultiDexApplication {

    private static MyApplication instance;

    /**
     * instance
     */
    public MyApplication() {
        instance = this;
    }

    /**
     * @return
     */
    public static Context getContext() {
        return instance;
    }

    /**
     * @return
     */
    public static synchronized MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initializePrDownloader();
    }

    private void initializePrDownloader() {
        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setReadTimeout(30_000)
                .setConnectTimeout(30_000)
                .build();
//        PRDownloader.initialize(getApplicationContext(), config);
        PRDownloader.initialize(getApplicationContext());
    }
}
