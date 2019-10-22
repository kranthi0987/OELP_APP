package mahiti.org.oelp.views.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import mahiti.org.oelp.R;
import mahiti.org.oelp.database.DAOs.MediaContentDao;
import mahiti.org.oelp.fileandvideodownloader.DownloadClass;
import mahiti.org.oelp.fileandvideodownloader.DownloadUtility;
import mahiti.org.oelp.fileandvideodownloader.FileModel;
import mahiti.org.oelp.fileandvideodownloader.OnMediaDownloadListener;
import mahiti.org.oelp.interfaces.ListRefresh;
import mahiti.org.oelp.interfaces.SharedMediaClickListener;
import mahiti.org.oelp.models.SharedMediaModel;
import mahiti.org.oelp.services.RetrofitConstant;
import mahiti.org.oelp.utils.AppUtils;
import mahiti.org.oelp.utils.CheckNetwork;
import mahiti.org.oelp.utils.Constants;
import mahiti.org.oelp.utils.Logger;
import mahiti.org.oelp.utils.MySharedPref;
import mahiti.org.oelp.views.fragments.MembersFragment;
import mahiti.org.oelp.views.fragments.MyContFragment;
import mahiti.org.oelp.views.fragments.TeacherInfoFragment;

public class TeacherInfoTabActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, SharedMediaClickListener , OnMediaDownloadListener {

    private static final String TAG = TeacherInfoTabActivity.class.getSimpleName();
    private Toolbar toolbar;

    private FragmentManager fragManager;

    private ViewPagerAdapter adapter;
    private String teacherUuid = "";
    private String tecaherContri="";
    private String teacherName = "";
    private String groupUUID = "";
    private TextView tvTitle;
    private AlertDialog alertDialogPop;
    private ListRefresh refresh;
    private View lineMembers;
    private View lineContri;
    private LinearLayout llMembersInfo;
    private LinearLayout llContributions;
    private TextView tvMemberInfo;
    private TextView tvContribution;
    private boolean membersClick = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacherinfo_tab);
        toolbar = findViewById(R.id.white_toolbar);
        tvTitle = findViewById(R.id.tvTitle);
        lineMembers = findViewById(R.id.lineMembers);
        lineContri = findViewById(R.id.lineContri);
        llMembersInfo = findViewById(R.id.llMembersInfo);
        llContributions = findViewById(R.id.llContributions);
        tvMemberInfo = findViewById(R.id.tvMemberInfo);
        tvContribution = findViewById(R.id.tvContribution);
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

        setFragment(new TeacherInfoFragment());
        setTextColor(membersClick, 0);

        llMembersInfo.setOnClickListener(view -> {
            if (!membersClick) {
                setFragment(new TeacherInfoFragment());
                setTextColor(!membersClick, 1);
                membersClick=true;
            }

        });
        llContributions.setOnClickListener(view -> {
            if (membersClick) {
                setFragment(new MyContFragment());
                setTextColor(!membersClick,1);
                membersClick = false;
            }
        });

    }

    private void getIntentValues() {
        teacherUuid = getIntent().getStringExtra(Constants.TEACHER_UUID);
        teacherName = getIntent().getStringExtra("teacherName");
        groupUUID = getIntent().getStringExtra(Constants.GROUP_UUID);
        tecaherContri = getIntent().getStringExtra("teachercontri");
        tvTitle.setText(teacherName);
    }

    public void setFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_content, fragment).addToBackStack(null).commitAllowingStateLoss();
    }

    public void setTextColor(boolean membersClicks, int type){
        if (membersClicks){
            if (type!=0) {
                lineMembers.setVisibility(View.VISIBLE);
                lineContri.setVisibility(View.INVISIBLE);

            }
            tvMemberInfo.setTextColor(getResources().getColor(R.color.black));
            tvContribution.setTextColor(getResources().getColor(R.color.grey));
        }else {
            if (type!=0) {
                lineContri.setVisibility(View.VISIBLE);
                lineMembers.setVisibility(View.INVISIBLE);
            }

            tvMemberInfo.setTextColor(getResources().getColor(R.color.grey));
            tvContribution.setTextColor(getResources().getColor(R.color.black));
        }
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

    @Override
    public void onSharedMediaClick(SharedMediaModel mediaModel, boolean shareGlobally, int position) {
        if (!shareGlobally) {
            if (mediaModel.getMediaType().equalsIgnoreCase(String.valueOf(Constants.VIDEO))) {
                checkVideoAndDownload(new FileModel(mediaModel.getMediaTitle(), mediaModel.getMediaFile(), mediaModel.getMediaUuid(), 0));
            } else if (mediaModel.getMediaType().equalsIgnoreCase(String.valueOf(Constants.IMAGE))) {
                shoWImagePopUp(mediaModel);
            }
        } else {
            if (new MySharedPref(this).readInt(Constants.USER_TYPE, Constants.USER_TEACHER) == Constants.USER_TEACHER) {
                showPopupForDelete(mediaModel, position);
            } else {
                showPopForDeleteAndGlobalShare(mediaModel, position);
            }
        }

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

    public void shareDataGlobally(SharedMediaModel model, int position) {
        MediaContentDao mediaContentDao = new MediaContentDao(this);
        long shareLong = mediaContentDao.updateGloballyShareMediaUUID(model.getMediaUuid());
        if (shareLong != -1)
            if (getFragmentRefreshListener() != null)
                getFragmentRefreshListener().onRefresh(position, false);
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

    private void deleteData(SharedMediaModel model, int position) {
        MediaContentDao mediaContentDao = new MediaContentDao(this);
        long deletelong = mediaContentDao.deleteMediaUUID(model.getMediaUuid());
        if (deletelong != -1)
            if (getFragmentRefreshListener() != null)
                getFragmentRefreshListener().onRefresh(position, true);
    }

    public ListRefresh getFragmentRefreshListener() {
        return refresh;
    }

    public void setFragmentRefreshListener(ListRefresh fragmentRefreshListener) {
        this.refresh = fragmentRefreshListener;
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


    private void checkVideoAndDownload(FileModel fileModel) {

        String userUUId = new MySharedPref(this).readString(Constants.USER_ID, "");
        if (videoAvailable(fileModel)) {
            String filePath = AppUtils.completePathInSDCard(Constants.VIDEO)+"/"+AppUtils.getFileName(fileModel.getFileUrl());
            DownloadUtility.playVideo(this, filePath, fileModel.getFileName(), userUUId, fileModel.getUuid(), "", fileModel.getDcfId(), "");
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
        new MySharedPref(this).writeInt(Constants.SELECTED_POSITION, i);
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }
}
