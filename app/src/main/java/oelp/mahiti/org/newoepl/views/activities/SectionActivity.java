package oelp.mahiti.org.newoepl.views.activities;

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

import oelp.mahiti.org.newoepl.R;
import oelp.mahiti.org.newoepl.databinding.ActivitySectionBinding;
import oelp.mahiti.org.newoepl.fileandvideodownloader.DownloadClass;
import oelp.mahiti.org.newoepl.fileandvideodownloader.DownloadUtility;
import oelp.mahiti.org.newoepl.fileandvideodownloader.FileModel;
import oelp.mahiti.org.newoepl.fileandvideodownloader.OnMediaDownloadListener;
import oelp.mahiti.org.newoepl.fileandvideodownloader.PermissionClass;
import oelp.mahiti.org.newoepl.interfaces.ItemClickListerner;
import oelp.mahiti.org.newoepl.models.CatalogueDetailsModel;
import oelp.mahiti.org.newoepl.services.RetrofitConstant;
import oelp.mahiti.org.newoepl.utils.AppUtils;
import oelp.mahiti.org.newoepl.utils.CheckNetwork;
import oelp.mahiti.org.newoepl.utils.Constants;
import oelp.mahiti.org.newoepl.utils.Logger;
import oelp.mahiti.org.newoepl.utils.MySharedPref;
import oelp.mahiti.org.newoepl.viewmodel.HomeViewModel;
import oelp.mahiti.org.newoepl.views.fragments.UnitsFragment;

public class SectionActivity extends AppCompatActivity implements ItemClickListerner, OnMediaDownloadListener {

    private static final String TAG = SectionActivity.class.getSimpleName();
    ActivitySectionBinding activitySectionBinding;
    HomeViewModel homeViewModel;
    Toolbar toolbar;
    private String parentId;
    private String title;
    private Fragment fragment;

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
        toolbar.inflateMenu(R.menu.teacher_menu);
        getIntentData();
        checkPermission();
    }

    private void checkPermission() {
        if (!PermissionClass.checkPermission(this)){
            PermissionClass.requestPermission(this);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
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
        Toast.makeText(getApplicationContext(), "About Us Clicked", Toast.LENGTH_LONG).show();
    }



    @Override
    public void onItemClick(CatalogueDetailsModel item) {
        if (item.getContType().equalsIgnoreCase("video")) {
            checkVideoAndDownload(new FileModel(item.getName(), item.getPath(), item.getUuid()));
        } else {
            Intent intent = new Intent(this, SectionActivity.class);
            intent.putExtra("CatalogDetailsModel", item);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_slide_in_left,
                    R.anim.anim_slide_out_left);
        }

    }


    private void checkVideoAndDownload(FileModel fileModel) {

        if (videoAvailable(fileModel)) {
            DownloadUtility.playVideo(this, fileModel.getFileUrl(), fileModel.getFileName(), new MySharedPref(this).readInt(Constants.USER_ID, 0), fileModel.getUuid(), parentId);
        } else {
            downloadVideo(fileModel);
        }
    }

    private void downloadVideo(FileModel fileModel) {
        if (CheckNetwork.checkNet(this)) {
            List<FileModel> fileModelList = new ArrayList<>();
            fileModelList.add(fileModel);
            new DownloadClass(Constants.VIDEO, this, RetrofitConstant.BASE_URL, AppUtils.completePathInSDCard(Constants.VIDEO).getAbsolutePath(), fileModelList);
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
    public void onBackPressed() {
        SectionActivity.this.finish();
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
    }


    @Override
    public void onMediaDownload(int type, String savedPath, String name, int position, String uuid) {
        if (savedPath != null && !savedPath.isEmpty()) {
            DownloadUtility.playVideo(this, savedPath, name, new MySharedPref(this).readInt(Constants.USER_ID, 0), uuid, parentId);
        } else {
            Toast.makeText(this, getString(R.string.error_downloading), Toast.LENGTH_SHORT).show();
        }
    }
}
