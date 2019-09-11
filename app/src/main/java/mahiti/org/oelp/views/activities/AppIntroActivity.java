package mahiti.org.oelp.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import mahiti.org.oelp.R;
import mahiti.org.oelp.utils.AppUtils;
import mahiti.org.oelp.utils.Constants;
import mahiti.org.oelp.utils.Logger;
import mahiti.org.oelp.utils.MySharedPref;
import mahiti.org.oelp.views.adapters.ViewPagerAdapter;

public class AppIntroActivity extends AppCompatActivity {

    private int dotscount;
    private ImageView[] dots;
    Toolbar toolbar;
    private MySharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_intro);
        toolbar = findViewById(R.id.white_toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setTitle("");
        }
        sharedPref = new MySharedPref(this);
        sharedPref.writeBoolean(Constants.IS_INTRO_DISPLAYED, true);
        toolbar.inflateMenu(R.menu.teacher_menu);
        initViews();
    }

    private void initViews() {
        findViewById(R.id.btnNext).setOnClickListener(view -> moveToHomeActivity());
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        ViewPager viewPager = findViewById(R.id.viewPager);
        LinearLayout llDots = findViewById(R.id.llDots);
        Button btnNext = findViewById(R.id.btnNext);
        viewPager.setAdapter(viewPagerAdapter);
        dotscount = viewPagerAdapter.getCount();
        dots = new ImageView[dotscount];

        btnNext.setVisibility(View.GONE);


        for (int i = 0; i < dotscount; i++) {

            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.nonactive_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(8, 0, 8, 0);

            llDots.addView(dots[i], params);

        }


        dots[0].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.active_dot));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Logger.logD(AppIntroActivity.this.getClass().getSimpleName(), "onPageScrolled");
            }

            @Override
            public void onPageSelected(int position) {

                for (int i = 0; i < dotscount; i++) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(AppIntroActivity.this, R.drawable.nonactive_dot));
                }

                dots[position].setImageDrawable(ContextCompat.getDrawable(AppIntroActivity.this, R.drawable.active_dot));

                if (position >= 2)
                    btnNext.setVisibility(View.VISIBLE);
                else
                    btnNext.setVisibility(View.GONE);

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Logger.logD(AppIntroActivity.this.getClass().getSimpleName(), "onPageScrollStateChanged");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.teacher_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.aboutus:
                ShowAboutUsActivity();
                return true;
            case R.id.logout:
                makeUserLogout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void makeUserLogout() {
        new MySharedPref(this).deleteAllData();
        Intent intent = new Intent(AppIntroActivity.this, MobileLoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        AppIntroActivity.this.finish();
        startActivity(intent);
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
    }

    private void ShowAboutUsActivity() {
        AppUtils.showAboutUsActivity(this);
    }

    public void moveToHomeActivity() {
        Intent intent = new Intent(AppIntroActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        this.finish();
        startActivity(intent);
        overridePendingTransition(R.anim.anim_slide_in_left,
                R.anim.anim_slide_out_left);
    }

}
