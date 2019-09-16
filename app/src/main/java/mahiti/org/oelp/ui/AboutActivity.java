package mahiti.org.oelp.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

import mahiti.org.oelp.R;
import mahiti.org.oelp.utils.ThemeHelper;

public class AboutActivity extends XmppActivity {

    private Button privacyButton;
    private Button termsOfUseButton;

    @Override
    protected void refreshUiReal() {

    }

    @Override
    void onBackendConnected() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(ThemeHelper.find(this));
        setContentView(R.layout.activity_about);
        setSupportActionBar(findViewById(R.id.toolbar));
        configureActionBar(getSupportActionBar());

        privacyButton = findViewById(R.id.show_privacy_policy);
        privacyButton.setOnClickListener(view -> {
            final Uri uri = Uri.parse("https://jabber.pix-art.de/privacy/");
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(browserIntent);
        });
        termsOfUseButton = findViewById(R.id.show_terms_of_use);
        termsOfUseButton.setOnClickListener(view -> {
            final Uri uri = Uri.parse("https://jabber.pix-art.de/termsofuse/");
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(browserIntent);
        });
    }
}