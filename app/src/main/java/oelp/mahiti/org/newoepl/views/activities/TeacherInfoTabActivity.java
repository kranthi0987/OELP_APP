package oelp.mahiti.org.newoepl.views.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import oelp.mahiti.org.newoepl.R;
import oelp.mahiti.org.newoepl.views.fragments.ContributionsFragment;
import oelp.mahiti.org.newoepl.views.fragments.NewTeacherFragment;
import oelp.mahiti.org.newoepl.views.fragments.TeacherInfoFragment;

public class TeacherInfoTabActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener{

    private Toolbar toolbar;

    private FragmentManager fragManager;

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacherinfo_tab);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        initViews();
        viewPager.setOnPageChangeListener(this);
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
