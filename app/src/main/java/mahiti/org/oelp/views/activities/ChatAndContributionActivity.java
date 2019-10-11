package mahiti.org.oelp.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import mahiti.org.oelp.R;
import mahiti.org.oelp.database.DAOs.MediaContentDao;
import mahiti.org.oelp.databinding.ActivityChatAndContributionBinding;
import mahiti.org.oelp.fileandvideodownloader.DownloadClass;
import mahiti.org.oelp.fileandvideodownloader.DownloadUtility;
import mahiti.org.oelp.fileandvideodownloader.FileModel;
import mahiti.org.oelp.fileandvideodownloader.OnMediaDownloadListener;
import mahiti.org.oelp.interfaces.ListRefresh;
import mahiti.org.oelp.interfaces.SharedMediaClickListener;
import mahiti.org.oelp.models.SharedMediaModel;
import mahiti.org.oelp.services.CallAPIServicesData;
import mahiti.org.oelp.services.RetrofitConstant;
import mahiti.org.oelp.utils.AppUtils;
import mahiti.org.oelp.utils.CheckNetwork;
import mahiti.org.oelp.utils.Constants;
import mahiti.org.oelp.utils.Logger;
import mahiti.org.oelp.utils.MySharedPref;
import mahiti.org.oelp.viewmodel.ChatAndContributionViewModel;
import mahiti.org.oelp.views.fragments.ContributionsFragment;
import mahiti.org.oelp.views.fragments.MembersFragment;

public class ChatAndContributionActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, SharedMediaClickListener, OnMediaDownloadListener {


    private Toolbar toolbar;

    private FragmentManager fragManager;
    private ListRefresh refresh;

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
    private AlertDialog alertDialogPop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat_and_contribution);
        viewModel = ViewModelProviders.of(this).get(ChatAndContributionViewModel.class);
        binding.setChatAndContributionViewModel(viewModel);
        binding.setLifecycleOwner(this);


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


        viewModel.getListOfImageToDownload().observe(this, imageListToDownload -> {
            if (imageListToDownload != null && !imageListToDownload.isEmpty()) {
                new DownloadClass(Constants.IMAGE, this, RetrofitConstant.BASE_URL, AppUtils.completePathInSDCard( Constants.IMAGE).getAbsolutePath(), imageListToDownload, "");
            }
        });


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
            /*case R.id.groupInfo:
                moveToGroupActivity();
                return true;*/
            default:
                return super.onOptionsItemSelected(item);
        }
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

        /*if (sharedPref.readBoolean(RetrofitConstant.FETCH_MEDIA_SHARED, false)) {
            progressBar.setVisibility(View.VISIBLE);
        }*/

        viewPager.setOnPageChangeListener(this);

        /*viewModel.insertLong.observe(this, aLong -> {
            if (aLong != null) {
                progressBar.setVisibility(View.GONE);
            }
        });*/

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

        ContributionsFragment contributionsFragment;
        MembersFragment membersFragment;

        membersFragment = new MembersFragment();
        membersFragment.setArguments(bundle);
        contributionsFragment = new ContributionsFragment();
        contributionsFragment.setArguments(bundle);

        adapter.addFragment(membersFragment, getString(R.string.members));
        adapter.addFragment(contributionsFragment, getString(R.string.contributions));

        viewPager.setAdapter(adapter);
    }

    @Override
    public void onSharedMediaClick(SharedMediaModel mediaModel, boolean shareGlobally, int position) {
        if (!shareGlobally) {
            if (mediaModel.getMediaType().equalsIgnoreCase(String.valueOf(Constants.VIDEO))) {
                checkVideoAndDownload(new FileModel(mediaModel.getMediaTitle(), mediaModel.getMediaFile(), mediaModel.getMediaUuid(), 0));
            } else if (mediaModel.getMediaType().equalsIgnoreCase(String.valueOf(Constants.IMAGE))) {
                shoWImagePopUp(mediaModel);
            }
        } else {
            if (sharedPref.readInt(Constants.USER_TYPE, Constants.USER_TEACHER) == Constants.USER_TEACHER) {
                showPopupForDelete(mediaModel, position);
            } else {
                showPopForDeleteAndGlobalShare(mediaModel, position);
            }
        }

    }

    private void shoWImagePopUp(SharedMediaModel mediaModel) {
        if (mediaModel == null)
            return;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.imageview_popup, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(true);
        String fileName = "";

        TextView tvMediaName = dialogView.findViewById(R.id.tvMediaName);
        TextView tvSharedBy = dialogView.findViewById(R.id.tvSharedBy);
        TextView tvSharedOn = dialogView.findViewById(R.id.tvSharedOn);
        LinearLayout rlSharedBy = dialogView.findViewById(R.id.rlSharedBy);
        LinearLayout rlSharedOn = dialogView.findViewById(R.id.rlSharedOn);
        Button btnClose = dialogView.findViewById(R.id.btnClose);
        RoundedImageView ivSharedContent = dialogView.findViewById(R.id.ivSharedContent);

        if (!mediaModel.getMediaTitle().isEmpty()) {
            tvMediaName.setVisibility(View.VISIBLE);
            tvMediaName.setText(mediaModel.getMediaTitle());
        }

        if (!mediaModel.getUserName().isEmpty()) {
            rlSharedBy.setVisibility(View.VISIBLE);
            tvSharedBy.setText(mediaModel.getUserName());
        }

        if (!mediaModel.getSubmissionTime().isEmpty()) {
            rlSharedOn.setVisibility(View.VISIBLE);
            tvSharedOn.setText(mediaModel.getSubmissionTime());
        }

        btnClose.setOnClickListener(view -> {
            alertDialogPop.dismiss();
        });

        if (mediaModel.getMediaFile() != null && !mediaModel.getMediaFile().isEmpty()) {
            String fileName1 = AppUtils.getFileName(mediaModel.getMediaFile());
            File file = null;
            try {
                file = new File(AppUtils.completePathInSDCard(Constants.IMAGE), fileName1);
            } catch (Exception ex) {
                Logger.logE("Exce", ex.getMessage(), ex);
            }
            if (file.exists()) {
                Glide.with(this)
                        .load("file://" + file)
                        .into(ivSharedContent);
            }
        }


        alertDialogPop = dialogBuilder.create();
        alertDialogPop.show();
    }


    public void showPopForDeleteAndGlobalShare(SharedMediaModel model, int position) {
        new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setTitle(R.string.what_you_want_to_do_with_media)
                .setItems(R.array.option_array, (dialog, which) -> {
                    if (which == 0)
                        deleteData(model, position);
                    else
                        shareDataGlobally(model, position);
                    dialog.dismiss();

                })
                .show();

    }

    public ListRefresh getFragmentRefreshListener() {
        return refresh;
    }

    public void setFragmentRefreshListener(ListRefresh fragmentRefreshListener) {
        this.refresh = fragmentRefreshListener;
    }

    private void deleteData(SharedMediaModel model, int position) {
        MediaContentDao mediaContentDao = new MediaContentDao(this);
        long deletelong = mediaContentDao.deleteMediaUUID(model.getMediaUuid());
        if (deletelong != -1)
            if (getFragmentRefreshListener() != null)
                getFragmentRefreshListener().onRefresh(position, true);
    }

    private void showPopupForDelete(SharedMediaModel mediaModel, int position) {


        new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setTitle("Delete Media")
                .setMessage("Delete " + mediaModel.getMediaTitle())
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteData(mediaModel, position);
                    dialog.dismiss();
                })

                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();

    }

    public void shareDataGlobally(SharedMediaModel model, int position) {
        MediaContentDao mediaContentDao = new MediaContentDao(this);
        long sharelong = mediaContentDao.updateGloballyShareMediaUUID(model.getMediaUuid());
        if (sharelong != -1)
            if (getFragmentRefreshListener() != null)
                getFragmentRefreshListener().onRefresh(position, false);
    }

    private void checkVideoAndDownload(FileModel fileModel) {

        String userUUId = new MySharedPref(this).readString(Constants.USER_ID, "");
        if (videoAvailable(fileModel)) {
            DownloadUtility.playVideo(this, fileModel.getFileUrl(), fileModel.getFileName(), userUUId, fileModel.getUuid(), "", fileModel.getDcfId(), "");
        } else {
            downloadVideo(fileModel);
        }
    }

    private void downloadVideo(FileModel fileModel) {
        if (CheckNetwork.checkNet(this)) {
            List<FileModel> fileModelList = new ArrayList<>();
            fileModelList.add(fileModel);
            new DownloadClass(Constants.VIDEO, this, RetrofitConstant.BASE_URL, AppUtils.completePathInSDCard(Constants.VIDEO).getAbsolutePath(), fileModelList, "");
        } else {
            Toast.makeText(this, getString(R.string.check_internet), Toast.LENGTH_SHORT).show();

        }
    }

    private boolean videoAvailable(FileModel fileModel) {
        try {
            File videoFile = new File(AppUtils.completePathInSDCard(Constants.VIDEO), AppUtils.getFileName(fileModel.getFileUrl()));
            if (videoFile.exists())
                return true;
        } catch (Exception ex) {
            Logger.logE(TAG, ex.getMessage(), ex);
        }
        return false;
    }

    @Override
    public void onMediaDownload(int type, String savedPath, String name, int position, String uuid, int dcfId, String unitUUID) {
        if (type == Constants.VIDEO) {
            if (savedPath != null && !savedPath.isEmpty()) {
                String userUUID = new MySharedPref(this).readString(Constants.USER_ID, "");
                DownloadUtility.playVideo(this, savedPath, name, userUUID, uuid, "", dcfId, unitUUID);
            }
        }

    }

    public void setGroupName(String groupName) {
        if (groupName!=null && !groupName.isEmpty())
            tvTitle.setText(groupName);
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