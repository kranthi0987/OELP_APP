package mahiti.org.oelp;

import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;

import androidx.multidex.MultiDexApplication;

public class MyApplication extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
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
