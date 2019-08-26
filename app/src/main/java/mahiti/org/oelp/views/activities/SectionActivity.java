package mahiti.org.oelp.views.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import mahiti.org.oelp.R;
import mahiti.org.oelp.database.DatabaseHandlerClass;
import mahiti.org.oelp.databinding.ActivitySectionBinding;
import mahiti.org.oelp.fileandvideodownloader.DownloadClass;
import mahiti.org.oelp.fileandvideodownloader.DownloadUtility;
import mahiti.org.oelp.fileandvideodownloader.FileModel;
import mahiti.org.oelp.fileandvideodownloader.OnMediaDownloadListener;
import mahiti.org.oelp.interfaces.ItemClickListerner;
import mahiti.org.oelp.models.CatalogueDetailsModel;
import mahiti.org.oelp.models.GroupModel;
import mahiti.org.oelp.services.RetrofitConstant;
import mahiti.org.oelp.utils.AppUtils;
import mahiti.org.oelp.utils.CheckNetwork;
import mahiti.org.oelp.utils.Constants;
import mahiti.org.oelp.utils.Logger;
import mahiti.org.oelp.utils.MySharedPref;
import mahiti.org.oelp.utils.PermissionClass;
import mahiti.org.oelp.viewmodel.HomeViewModel;
import mahiti.org.oelp.views.fragments.UnitsFragment;

public class SectionActivity extends AppCompatActivity implements ItemClickListerner, OnMediaDownloadListener {

    private static final String TAG = SectionActivity.class.getSimpleName();
    ActivitySectionBinding activitySectionBinding;
    HomeViewModel homeViewModel;
    Toolbar toolbar;
    private String parentId;
    private String title;
    private Fragment fragment;
    private DatabaseHandlerClass handlerClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySectionBinding = DataBindingUtil.setContentView(this, R.layout.activity_section);
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        activitySectionBinding.setHomeViewModels(homeViewModel);
        activitySectionBinding.setLifecycleOwner(this);
        toolbar = findViewById(R.id.white_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }
        getIntentData();
        checkPermission();
    }

    private void checkPermission() {
        if (!PermissionClass.checkPermission(this)) {
            PermissionClass.requestPermission(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        handlerClass = new DatabaseHandlerClass(this);

    }

    private void setFragment(String units) {
        Bundle bundle = new Bundle();
        bundle.putString("ParentId", parentId);
        bundle.putString("Title", title);
        fragment = new UnitsFragment();
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.setCustomAnimations(R.anim.zoom_in, R.anim.zoom_out);
        transaction.replace(R.id.fragmentContainer1, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void getIntentData() {
        CatalogueDetailsModel detailsModel = getIntent().getParcelableExtra("CatalogDetailsModel");
        parentId = detailsModel.getUuid();
        title = detailsModel.getName();
        activitySectionBinding.tvTitle.setText(title);
        setFragment(Constants.Units);
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

    private void ShowAboutUsActivity() {
        AppUtils.showAboutUsActivity(this);
    }


    @Override
    public void onItemClick(CatalogueDetailsModel item) {
        if (item.getContType() != null) {
            if (item.getContType().equalsIgnoreCase("video")) {
                checkVideoAndDownload(new FileModel(item.getName(), item.getPath(), item.getUuid()));
            } else {
                moveToNext(item);
            }
        } else {
            moveToNext(item);
        }
    }

    @Override
    public void onItemClick(GroupModel item) {

    }

    private void moveToNext(CatalogueDetailsModel item) {
        Intent intent = new Intent(this, SectionActivity.class);
        intent.putExtra("CatalogDetailsModel", item);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_slide_in_left,
                R.anim.anim_slide_out_left);
    }


    private void checkVideoAndDownload(FileModel fileModel) {

        String userUUId = new MySharedPref(this).readString(Constants.USER_ID, "");
        if (videoAvailable(fileModel) && DownloadUtility.checkFileCorruptStatus(fileModel,SectionActivity.this)) {
            DownloadUtility.playVideo(this, fileModel.getFileUrl(), fileModel.getFileName(), userUUId, fileModel.getUuid(), parentId);
        } else {
            downloadVideo(fileModel);
        }
    }

    private void downloadVideo(FileModel fileModel) {
        if (CheckNetwork.checkNet(this)) {
            List<FileModel> fileModelList = new ArrayList<>();
            fileModelList.add(fileModel);
//            new DownloadClass(Constants.VIDEO, this, RetrofitConstant.BASE_URL, AppUtils.completePathInSDCard(Constants.VIDEO).getAbsolutePath(), fileModelList);
            new DownloadClass(Constants.VIDEO, this, RetrofitConstant.BASE_URL, AppUtils.completeInternalStoragePath(this, Constants.VIDEO).getAbsolutePath(), fileModelList);
        } else {
            Toast.makeText(this, getString(R.string.check_internet), Toast.LENGTH_SHORT).show();

        }
    }

    private boolean videoAvailable(FileModel fileModel) {
        try {
//            File videoFile = new File(AppUtils.completePathInSDCard(Constants.VIDEO), AppUtils.getFileName(fileModel.getFileUrl()));
            File videoFile = new File(AppUtils.completeInternalStoragePath(this,Constants.VIDEO), AppUtils.getFileName(fileModel.getFileUrl()));
            if (videoFile.exists())
                return true;
        } catch (Exception ex) {
            Logger.logE(TAG, ex.getMessage(), ex);
        }
        return false;
    }


    @Override
    public void onBackPressed() {
        SectionActivity.this.finish();
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
    }


    @Override
    public void onMediaDownload(int type, String savedPath, String name, int position, String uuid) {
        if (savedPath != null && !savedPath.isEmpty()) {

            String userUUID = new MySharedPref(this).readString(Constants.USER_ID, "");
            DownloadUtility.playVideo(this, savedPath, name, userUUID, uuid, parentId);
        } else {
            Toast.makeText(this, getString(R.string.error_downloading), Toast.LENGTH_SHORT).show();
        }
    }
}
