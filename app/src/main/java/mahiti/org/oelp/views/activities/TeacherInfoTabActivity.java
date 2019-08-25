package mahiti.org.oelp.views.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import mahiti.org.oelp.R;
import mahiti.org.oelp.utils.AppUtils;
import mahiti.org.oelp.utils.Constants;
import mahiti.org.oelp.utils.MySharedPref;
import mahiti.org.oelp.views.fragments.ContributionsFragment;
import mahiti.org.oelp.views.fragments.NewTeacherFragment;
import mahiti.org.oelp.views.fragments.TeacherInfoFragment;

public class TeacherInfoTabActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private Toolbar toolbar;

    private FragmentManager fragManager;

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ViewPagerAdapter adapter;
    private String teacherUuid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacherinfo_tab);

        toolbar = findViewById(R.id.black_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }
        int userType = new MySharedPref(this).readInt(Constants.USER_TYPE, Constants.USER_TEACHER);
        if (userType == Constants.USER_TEACHER)
            toolbar.inflateMenu(R.menu.teacher_menu);
        else
            toolbar.inflateMenu(R.menu.trainer_menu);
        getIntentValues();

        initViews();
        viewPager.setOnPageChangeListener(this);
    }

    private void getIntentValues() {
        teacherUuid = getIntent().getStringExtra(Constants.TEACHER_UUID);
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        TeacherInfoTabActivity.this.finish();
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
    }

    private void ShowAboutUsActivity() {
        AppUtils.showAboutUsActivity(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.aboutus:
                ShowAboutUsActivity();
                return true;
            case R.id.logout:
                AppUtils.makeUserLogout(this);
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void initViews() {

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        //TeacherInfo1Fragment teacherInfoFragment = new TeacherInfo1Fragment();
        TeacherInfoFragment teacherInfoFragment = new TeacherInfoFragment();
        ContributionsFragment contributionsFragment = new ContributionsFragment();
        NewTeacherFragment newTeacherFragment = new NewTeacherFragment();


        adapter.addFragment(teacherInfoFragment, "Teacher Info");
        adapter.addFragment(contributionsFragment, "Contributions");
        adapter.addFragment(newTeacherFragment, "New");

        viewPager.setAdapter(adapter);
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {

    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }
}
