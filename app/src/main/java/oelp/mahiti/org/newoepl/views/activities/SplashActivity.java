package oelp.mahiti.org.newoepl.views.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import java.util.Locale;

import oelp.mahiti.org.newoepl.R;
import oelp.mahiti.org.newoepl.utils.Constants;
import oelp.mahiti.org.newoepl.utils.MySharedPref;

public class SplashActivity extends AppCompatActivity {

    RelativeLayout rlMain;
    private MySharedPref sharedPref;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        rlMain = findViewById(R.id.rlMain);
        setLocale(1);
        sharedPref = new MySharedPref(this);
        new android.os.Handler().postDelayed(() -> checkUserIsRegisteredAndProceed(), 2000);

    }

    private void checkUserIsRegisteredAndProceed() {
        if (sharedPref.readBoolean(Constants.USER_LOGIN, false)){
            moveToHomeActivity();
        }else {
            moveToLoginActivity();
        }
    }

    private void moveToLoginActivity() {
        Intent intent = new Intent(SplashActivity.this, MobileLoginActivity.class);
        startActivity(intent);
        this.finish();
        overridePendingTransition(R.anim.anim_slide_in_left,
                R.anim.anim_slide_out_left);
    }

    private void moveToHomeActivity() {
        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_slide_in_left,
                R.anim.anim_slide_out_left);
    }

    public void setLocale(Integer languageCode) {
        String localeName = "kn";
        if (languageCode == 2) {
            localeName = "kn";
        } else {
            localeName = "en";
        }

        Locale myLocale = new Locale(localeName);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }



}
