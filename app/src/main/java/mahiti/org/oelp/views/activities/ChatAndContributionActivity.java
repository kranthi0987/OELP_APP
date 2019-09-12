package mahiti.org.oelp.views.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import mahiti.org.oelp.R;
import mahiti.org.oelp.database.CreateGroupActivity;
import mahiti.org.oelp.databinding.ActivityChatAndContributionBinding;
import mahiti.org.oelp.utils.Constants;
import mahiti.org.oelp.utils.MySharedPref;
import mahiti.org.oelp.viewmodel.ChatAndContributionViewModel;
import mahiti.org.oelp.views.adapters.ChatAdapter;
import mahiti.org.oelp.views.fragments.ChatFragment;
import mahiti.org.oelp.views.fragments.MyContFragment;
import mahiti.org.oelp.views.fragments.TeacherContFragment;

public class ChatAndContributionActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {


    private Toolbar toolbar;

    private FragmentManager fragManager;

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ViewPagerAdapter adapter;
    private MySharedPref sharedPref;
    private int userType;
    private String groupUUID;
    private String groupName;
    private String usertype;
    ChatAndContributionViewModel viewModel;
    ActivityChatAndContributionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat_and_contribution);
        viewModel = ViewModelProviders.of(this).get(ChatAndContributionViewModel.class);
        binding.setChatAndContributionViewModel(viewModel);
        binding.setLifecycleOwner(this);

        sharedPref = new MySharedPref(this);
        userType = sharedPref.readInt(Constants.USER_TYPE, Constants.USER_TEACHER);
        toolbar = findViewById(R.id.black_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getIntentValues();
        initViews();
        viewPager.setOnPageChangeListener(this);
    }

    private void getIntentValues() {
        groupUUID = getIntent().getStringExtra("groupUUID");
        groupName = getIntent().getStringExtra("groupName");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(groupName);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.groupInfo:
                moveToGroupActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void moveToGroupActivity() {
        Intent intent = new Intent(ChatAndContributionActivity.this, CreateGroupActivity.class);
        intent.putExtra("groupUUID", groupUUID);
        intent.putExtra("groupName", groupName);
        startActivityForResult(intent, 103);
        overridePendingTransition(R.anim.anim_slide_in_left,
                R.anim.anim_slide_out_left);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (userType == Constants.USER_TRAINER)
            getMenuInflater().inflate(R.menu.group_menu, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        ChatAndContributionActivity.this.finish();
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
    }

    private void initViews() {
        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==103 && resultCode==RESULT_OK){
            Intent intent= new Intent();
            intent.putExtra("result",true);
            setResult(RESULT_OK,intent);
            sharedPref.writeBoolean(Constants.IS_UPDATED, true);
            onBackPressed();

        }
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        ChatFragment chatFragment;
        MyContFragment myContriFragment;
        TeacherContFragment teacherContFragment;
        if (userType == Constants.USER_TEACHER) {
            chatFragment = new ChatFragment();
            myContriFragment = new MyContFragment();
            teacherContFragment = new TeacherContFragment();

            /*adapter.addFragment(chatFragment, getString(R.string.chats));*/
            adapter.addFragment(teacherContFragment, "Group's" + "\n" + "Teacher");
            adapter.addFragment(myContriFragment, "Contribution");
        } else {
            chatFragment = new ChatFragment();
            teacherContFragment = new TeacherContFragment();
            myContriFragment = new MyContFragment();

            /*adapter.addFragment(chatFragment, getString(R.string.chats));*/
            adapter.addFragment(teacherContFragment, "Teachers" + "\n" + "Contributions");
            adapter.addFragment(myContriFragment, getString(R.string.my_contributions));

        }
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

//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.group_menu, menu);
//        return true;
//    }


}