package mahiti.org.oelp.views.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import mahiti.org.oelp.R;
import mahiti.org.oelp.database.CreateGroupActivity;
import mahiti.org.oelp.database.DAOs.MediaContentDao;
import mahiti.org.oelp.databinding.ActivityChatAndContributionBinding;
import mahiti.org.oelp.fileandvideodownloader.DownloadClass;
import mahiti.org.oelp.fileandvideodownloader.DownloadUtility;
import mahiti.org.oelp.fileandvideodownloader.FileModel;
import mahiti.org.oelp.fileandvideodownloader.OnMediaDownloadListener;
import mahiti.org.oelp.interfaces.FetchDataFromApiListener;
import mahiti.org.oelp.interfaces.SharedMediaClickListener;
import mahiti.org.oelp.models.MobileVerificationResponseModel;
import mahiti.org.oelp.models.SharedMediaModel;
import mahiti.org.oelp.services.CallAPIServicesData;
import mahiti.org.oelp.services.RetrofitConstant;
import mahiti.org.oelp.utils.AppUtils;
import mahiti.org.oelp.utils.CheckNetwork;
import mahiti.org.oelp.utils.Constants;
import mahiti.org.oelp.utils.Logger;
import mahiti.org.oelp.utils.MySharedPref;
import mahiti.org.oelp.viewmodel.ChatAndContributionViewModel;
import mahiti.org.oelp.views.fragments.ChatFragment;
import mahiti.org.oelp.views.fragments.ContributionsFragment;
import mahiti.org.oelp.views.fragments.MembersFragment;

public class ChatAndContributionActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, SharedMediaClickListener, OnMediaDownloadListener, FetchDataFromApiListener {


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
    private TextView tvTitle;
    ChatAndContributionViewModel viewModel;
    ActivityChatAndContributionBinding binding;
    private ProgressBar progressBar;
    private String TAG = ChatAndContributionActivity.class.getSimpleName();
    private CallAPIServicesData servicesData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat_and_contribution);
        viewModel = ViewModelProviders.of(this).get(ChatAndContributionViewModel.class);
        binding.setChatAndContributionViewModel(viewModel);
        binding.setLifecycleOwner(this);

        servicesData = new CallAPIServicesData(this);

        tvTitle = binding.tvTitle;
        progressBar = binding.progressBar;

        sharedPref = new MySharedPref(this);
        userType = sharedPref.readInt(Constants.USER_TYPE, Constants.USER_TEACHER);
        toolbar = findViewById(R.id.white_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }

        getIntentValues();
        initViews();
        viewPager.setOnPageChangeListener(this);


        if (CheckNetwork.checkNet(this)) {
            servicesData.getTeacherList(sharedPref.readString(Constants.USER_ID, ""));
        }

    }


    private void getIntentValues() {
        groupUUID = getIntent().getStringExtra("groupUUID");
        groupName = getIntent().getStringExtra("groupName");
        tvTitle.setText(groupName);
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

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        if (sharedPref.readBoolean(RetrofitConstant.FETCH_MEDIA_SHARED, false)) {
            progressBar.setVisibility(View.VISIBLE);
        }

        viewModel.insertLong.observe(this, aLong -> {
            if (aLong != null) {
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 103 && resultCode == RESULT_OK) {
            Intent intent = new Intent();
            intent.putExtra("result", true);
            setResult(RESULT_OK, intent);
            sharedPref.writeBoolean(Constants.IS_UPDATED, true);
            onBackPressed();

        }
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        Bundle bundle = new Bundle();
        bundle.putString("groupUUID", groupUUID);

        ChatFragment chatFragment;
        ContributionsFragment contributionsFragment;
        MembersFragment membersFragment;
        /*if (userType == Constants.USER_TEACHER) {
            chatFragment = new ChatFragment();
            myContriFragment = new MyContFragment();
            teacherContFragment = new TeacherContFragment();

            adapter.addFragment(chatFragment, getString(R.string.chats));
            adapter.addFragment(teacherContFragment, "Group's" + "\n" + "Teacher");
            adapter.addFragment(myContriFragment, "Contribution");
        } else {
            chatFragment = new ChatFragment();*/
        membersFragment = new MembersFragment();
        membersFragment.setArguments(bundle);
        contributionsFragment = new ContributionsFragment();
        contributionsFragment.setArguments(bundle);

        /*adapter.addFragment(chatFragment, getString(R.string.chats));*/
        adapter.addFragment(membersFragment, getString(R.string.members));
        adapter.addFragment(contributionsFragment, getString(R.string.contributions));

        /*}*/
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onSharedMediaClick(SharedMediaModel mediaModel, boolean shareGlobally) {
        if (!shareGlobally)
            checkVideoAndDownload(new FileModel(mediaModel.getMediaTitle(), mediaModel.getMediaFile(), mediaModel.getMediaUuid(), 0));
        else
            showPopup(mediaModel);
    }

    private void showPopup(SharedMediaModel mediaModel) {


        new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setTitle("Share Media")
                .setMessage("Share " + mediaModel.getMediaTitle() + " with all group")
                .setPositiveButton("Share", (dialog, which) -> {
                    progressBar.setVisibility(View.VISIBLE);
                    shareDataGlobally(mediaModel);
                    dialog.dismiss();
                })

                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();

    }

    public void shareDataGlobally(SharedMediaModel mediaModel) {
        String userUUID = new MySharedPref(this).readString(Constants.USER_ID, "");
        String data = "";
        try {
            JSONArray array = new JSONArray();
            JSONObject obj = new JSONObject();
            obj.put("media_uuid", mediaModel.getMediaUuid());
            array.put(obj);
            data = array.toString();
        } catch (Exception ex) {
            Logger.logE(TAG, "Exception in Conversion :" + ex.getMessage(), ex);
        }
        servicesData.shareMediaGlobally(userUUID, data);
    }

    private void checkVideoAndDownload(FileModel fileModel) {

        String userUUId = new MySharedPref(this).readString(Constants.USER_ID, "");
        if (videoAvailable(fileModel) && DownloadUtility.checkFileCorruptStatus(fileModel, this)) {
            DownloadUtility.playVideo(this, fileModel.getFileUrl(), fileModel.getFileName(), userUUId, fileModel.getUuid(), "", fileModel.getDcfId(), "");
        } else {
            downloadVideo(fileModel);
        }
    }

    private void downloadVideo(FileModel fileModel) {
        if (CheckNetwork.checkNet(this)) {
            List<FileModel> fileModelList = new ArrayList<>();
            fileModelList.add(fileModel);
            new DownloadClass(Constants.VIDEO, this, RetrofitConstant.BASE_URL, AppUtils.completeInternalStoragePath(this, Constants.VIDEO).getAbsolutePath(), fileModelList, "");
        } else {
            Toast.makeText(this, getString(R.string.check_internet), Toast.LENGTH_SHORT).show();

        }
    }

    private boolean videoAvailable(FileModel fileModel) {
        try {
            File videoFile = new File(AppUtils.completeInternalStoragePath(this, Constants.VIDEO), AppUtils.getFileName(fileModel.getFileUrl()));
            if (videoFile.exists())
                return true;
        } catch (Exception ex) {
            Logger.logE(TAG, ex.getMessage(), ex);
        }
        return false;
    }

    @Override
    public void onMediaDownload(int type, String savedPath, String name, int position, String uuid, int dcfId, String unitUUID) {
        if (savedPath != null && !savedPath.isEmpty()) {
            String userUUID = new MySharedPref(this).readString(Constants.USER_ID, "");
            DownloadUtility.playVideo(this, savedPath, name, userUUID, uuid, "", dcfId, unitUUID);
        } else {
            Toast.makeText(this, getString(R.string.error_downloading), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFetchDataFromApi(MobileVerificationResponseModel model, String type) {
        progressBar.setVisibility(View.GONE);
        if (type.equalsIgnoreCase("global")) {

        } else {
            if (model.getData() != null && !model.getData().isEmpty()) {
                MediaContentDao mediaContentDao = new MediaContentDao(this);
                mediaContentDao.insertSharedMedia(model.getData());
                if (model.getGlobally() != null && !model.getGlobally().isEmpty()) {
                    mediaContentDao.updateGloabllyShareMediaUUID(model.getGlobally());
                }
            }
        }
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