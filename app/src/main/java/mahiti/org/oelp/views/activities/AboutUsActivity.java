package mahiti.org.oelp.views.activities;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import mahiti.org.oelp.R;

public class AboutUsActivity extends AppCompatActivity {

    private WebView aboutweb;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        toolbar = findViewById(R.id.white_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }
        aboutweb = findViewById(R.id.webview);
        //aboutweb.loadUrl(url);
        WebSettings settings = aboutweb.getSettings();
        settings.setJavaScriptEnabled(true);
        aboutweb.setWebViewClient(new MyWebViewClient());
        settings.setLoadsImagesAutomatically(true);
//        aboutweb.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        aboutweb.setScrollbarFadingEnabled(false);
        aboutweb.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        //aboutweb.loadUrl(url);
        aboutweb.loadUrl("file:///android_asset/page.html");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }



    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
        }

        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            final Uri uri = Uri.parse(url);
            return true;
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(request.getUrl().toString());
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        AboutUsActivity.this.finish();
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
    }
}
