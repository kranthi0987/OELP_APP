package mahiti.org.oelp.views.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import mahiti.org.oelp.R;
import mahiti.org.oelp.utils.Constants;
import mahiti.org.oelp.utils.MySharedPref;
import mahiti.org.oelp.utils.PermissionClass;

public class SplashActivity extends AppCompatActivity {

    RelativeLayout rlMain;
    private MySharedPref sharedPref;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        rlMain = findViewById(R.id.rlMain);
//        setLocale(1);
        sharedPref = new MySharedPref(this);
        checkPermissionAndProceed();
    }

    private void checkPermissionAndProceed() {
        if (PermissionClass.checkPermission(this)){
            checkUserIsRegisteredAndProceed();
        }else {
            PermissionClass.requestPermission(this);
        }

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

//    public void setLocale(Integer languageCode) {
//        String localeName = "kn";
//        if (languageCode == 2) {
//            localeName = "kn";
//        } else {
//            localeName = "en";
//        }
//
//        Locale myLocale = new Locale(localeName);
//        Resources res = getResources();
//        DisplayMetrics dm = res.getDisplayMetrics();
//        Configuration conf = res.getConfiguration();
//        conf.locale = myLocale;
//        res.updateConfiguration(conf, dm);
//    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==1)
            checkUserIsRegisteredAndProceed();
    }
}
