package oelp.mahiti.org.newoepl.views.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import oelp.mahiti.org.newoepl.R;
import oelp.mahiti.org.newoepl.views.fragments.ChatFragment;
import oelp.mahiti.org.newoepl.views.fragments.MyContFragment;
import oelp.mahiti.org.newoepl.views.fragments.TeacherContFragment;

public class GroupTabsActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener{


    private Toolbar toolbar;

    private FragmentManager fragManager;

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ViewPagerAdapter adapter;
    private ImageView ivBack;
    private ImageView ivMore;

    private RelativeLayout relativeLayout;

    View actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_tabs);


       // setSupportActionBar(toolbar);


        initViews();
        viewPager.setOnPageChangeListener(this);
    }

    private void initViews() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        actionBar = findViewById(R.id.toolbar);
        /*toolbar = (Toolbar) findViewById(R.id.custom_toolbar);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.group_menu);*/

        ivBack = (ImageView)findViewById(R.id.ivBack);
        ivBack.setOnClickListener(view -> onBackPressed());

        ivMore = (ImageView)findViewById(R.id.ivMore);

        ivMore.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Context wrapper = new ContextThemeWrapper(GroupTabsActivity.this, R.style.PopupMenu);
               // PopupMenu popup = new PopupMenu(wrapper, view);
                PopupMenu popupMenu = new PopupMenu(wrapper, ivMore);
               // popupMenu.getMenuInflater().inflate(R.menu.group_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.groupInfo:
                                // item one clicked
                                return true;

                        }

                        return false;
                    }
                });
                popupMenu.inflate(R.menu.group_menu);
                popupMenu.show();
            }
            });

    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        ChatFragment chatFragment = new ChatFragment();
        TeacherContFragment teacherContFragment = new TeacherContFragment();
        MyContFragment myContriFragment = new MyContFragment();


        adapter.addFragment(chatFragment, "Chats");
        adapter.addFragment(teacherContFragment, "Teachers"+"\n"+"Contributions");
        adapter.addFragment(myContriFragment, "My Contributions");

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


    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.group_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId())
        {
            case R.id.groupInfo:
                Intent intent = new Intent(GroupTabsActivity.this, TeacherInfoTabActivity.class);
                startActivity(intent);
                finish();
                break;
                *//*case R.id.bac*//*

        }

        return true;
    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}