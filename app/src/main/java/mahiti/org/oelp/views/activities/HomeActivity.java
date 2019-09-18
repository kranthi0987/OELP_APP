package mahiti.org.oelp.views.activities;

import android.app.Activity;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import mahiti.org.oelp.R;
import mahiti.org.oelp.database.CreateGroupActivity;
import mahiti.org.oelp.databinding.ActivityHomeBinding;
import mahiti.org.oelp.fileandvideodownloader.DownloadClass;
import mahiti.org.oelp.fileandvideodownloader.FileModel;
import mahiti.org.oelp.fileandvideodownloader.OnMediaDownloadListener;
import mahiti.org.oelp.interfaces.ItemClickListerner;
import mahiti.org.oelp.models.CatalogueDetailsModel;
import mahiti.org.oelp.models.GroupModel;
import mahiti.org.oelp.services.RetrofitConstant;
import mahiti.org.oelp.ui.StartUI;
import mahiti.org.oelp.utils.AppUtils;
import mahiti.org.oelp.utils.Constants;
import mahiti.org.oelp.utils.MySharedPref;
import mahiti.org.oelp.utils.PermissionClass;
import mahiti.org.oelp.viewmodel.HomeViewModel;
import mahiti.org.oelp.views.fragments.GroupsFragment;
import mahiti.org.oelp.views.fragments.HomeFragment;
import mahiti.org.oelp.views.fragments.UnitsFragment;

public class HomeActivity extends AppCompatActivity implements ItemClickListerner, OnMediaDownloadListener {

    Toolbar toolbar;
    ActivityHomeBinding activityHomeBinding;
    HomeViewModel homeViewModel;
    private int userType;
    private MySharedPref sharedPref;
    private int clicked = 0;
    private List<GroupModel> groupModelList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityHomeBinding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        activityHomeBinding.setHomeViewModel(homeViewModel);
        activityHomeBinding.setLifecycleOwner(this);

        sharedPref = new MySharedPref(this);
        userType = sharedPref.readInt(Constants.USER_TYPE, Constants.USER_TEACHER);

        toolbar = findViewById(R.id.white_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }
        if (homeViewModel.teacherLogin)
            toolbar.inflateMenu(R.menu.teacher_menu);
        else
            toolbar.inflateMenu(R.menu.trainer_menu);

        if (!PermissionClass.checkPermission(this)) {
            PermissionClass.requestPermission(this);
        }
        if (getIntent().getBooleanExtra("UnitClick", false))
            homeViewModel.unitsClick.setValue(true);


//        if (homeViewModel.apiCount==0){
//            homeViewModel.unitsClick.setValue(true);
//        }


        homeViewModel.unitsClick.observe(this, aBoolean -> {
            if (aBoolean != null && aBoolean) {
                setImageAndTextColor(Constants.Units);
                setFragment(Constants.Units);
                homeViewModel.title.setValue(getResources().getString(R.string.units));
                clicked = 0;
            }
        });


        homeViewModel.groupsClick.observe(this, aBoolean -> {
            if (aBoolean != null && aBoolean) {
                if (userType == Constants.USER_TEACHER && clicked == 0) {
                    homeViewModel.groupsClick.setValue(false);
                }
                groupModelList = homeViewModel.getGroupList();
                checKConditionAndProceed(groupModelList);
            }
        });

        homeViewModel.getApiErrorMessage().observe(this, s -> {
            if (s != null)
                Toast.makeText(HomeActivity.this, s, Toast.LENGTH_SHORT).show();
        });



        homeViewModel.getDataInserted().observe(this, integer -> {
            if (integer != null) {
                clicked = 0;
                setImageAndTextColor(Constants.Units);
                setFragment(Constants.Units);
            }

        });
        homeViewModel.getListOfImageToDownload().observe(this, fileModels ->
//                new DownloadClass(Constants.IMAGE, HomeActivity.this, RetrofitConstant.BASE_URL, AppUtils.completePathInSDCard(Constants.IMAGE).getAbsolutePath(), fileModels));
                new DownloadClass(Constants.IMAGE, HomeActivity.this, RetrofitConstant.BASE_URL, AppUtils.completeInternalStoragePath(this, Constants.IMAGE).getAbsolutePath(), fileModels, ""));
    }

    private void checKConditionAndProceed(List<GroupModel> groupModelList) {
        if(userType == Constants.USER_TEACHER){
            if(groupModelList!=null && !groupModelList.isEmpty()) {
                moveTONextActivity(groupModelList.get(0).getGroupName(), groupModelList.get(0).getGroupUUID());
            }
            else {
                Toast.makeText(this, "Not in any group", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            setImageAndTextColor(Constants.Groups);
            setFragment(Constants.Groups);
        }


    }

    private void moveTONextActivity(String groupName, String groupUUID) {
        Intent intent = new Intent(HomeActivity.this, ChatAndContributionActivity.class);
        intent.putExtra("groupUUID", groupUUID);
        intent.putExtra("groupName", groupName);
        startActivityForResult(intent, 102);
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
        clicked = 0;
    }



    GroupsFragment groupsFragment = null;
    UnitsFragment unitsFragment = null;
    HomeFragment homeFragment = null;

    private void setFragment(String type) {

        switch (type) {
            case Constants.Home:
                homeFragment = new HomeFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmentContainer, homeFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            case Constants.Units:
                activityHomeBinding.tvTitle.setText(getResources().getString(R.string.units));
                homeViewModel.parentId.setValue("");
                Bundle bundle = new Bundle();
                bundle.putString("ParentId", "");
                bundle.putString("Title", getResources().getString(R.string.units));
                unitsFragment = new UnitsFragment();
                unitsFragment.setArguments(bundle);
                FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
                transaction1.replace(R.id.fragmentContainer, unitsFragment);
                transaction1.addToBackStack(null);
                transaction1.commit();
                break;
            case Constants.Groups:
                activityHomeBinding.tvTitle.setText(getResources().getString(R.string.groups));
                groupsFragment = new GroupsFragment();
                FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
                transaction2.replace(R.id.fragmentContainer, groupsFragment);
                transaction2.addToBackStack(null);
                transaction2.commit();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(sharedPref.readBoolean(Constants.IS_UPDATED, false)){
            if (groupsFragment!=null) {
                groupsFragment.setValueToAdapter();
                sharedPref.writeBoolean(Constants.IS_UPDATED, false);
            }
        }
    }

    private void setImageAndTextColor(String type) {
        switch (type) {
            case Constants.Home:

                activityHomeBinding.ivHomeTeacher.setBackgroundResource(R.drawable.home_select);
                activityHomeBinding.ivUnitsTeacher.setBackgroundResource(R.drawable.units_normal);
                activityHomeBinding.ivGroupsTeacher.setBackgroundResource(R.drawable.group_normal);

                activityHomeBinding.tvHomeTeacher.setTextColor(getResources().getColor(R.color.red));
                activityHomeBinding.tvUnitsTeacher.setTextColor(getResources().getColor(R.color.grey));
                activityHomeBinding.tvGroupsTeacher.setTextColor(getResources().getColor(R.color.grey));
                break;
            case Constants.Units:
                if (homeViewModel.teacherLogin) {
                    activityHomeBinding.ivHomeTeacher.setBackgroundResource(R.drawable.home_normal);
                    activityHomeBinding.ivUnitsTeacher.setBackgroundResource(R.drawable.units_select);
                    activityHomeBinding.ivGroupsTeacher.setBackgroundResource(R.drawable.group_normal);

                    activityHomeBinding.tvHomeTeacher.setTextColor(getResources().getColor(R.color.grey));
                    activityHomeBinding.tvUnitsTeacher.setTextColor(getResources().getColor(R.color.red));
                    activityHomeBinding.tvGroupsTeacher.setTextColor(getResources().getColor(R.color.grey));
                } else {
                    activityHomeBinding.ivUnitsTrainer.setBackgroundResource(R.drawable.units_select);
                    activityHomeBinding.ivGroupsTrainer.setBackgroundResource(R.drawable.group_normal);

                    activityHomeBinding.tvUnitsTrainer.setTextColor(getResources().getColor(R.color.red));
                    activityHomeBinding.tvGroupsTrainer.setTextColor(getResources().getColor(R.color.grey));
                }
                break;
            case Constants.Groups:
                if (homeViewModel.teacherLogin) {
                    activityHomeBinding.ivHomeTeacher.setBackgroundResource(R.drawable.home_normal);
                    activityHomeBinding.ivUnitsTeacher.setBackgroundResource(R.drawable.units_normal);
                    activityHomeBinding.ivGroupsTeacher.setBackgroundResource(R.drawable.group_select);

                    activityHomeBinding.tvHomeTeacher.setTextColor(getResources().getColor(R.color.grey));
                    activityHomeBinding.tvUnitsTeacher.setTextColor(getResources().getColor(R.color.grey));
                    activityHomeBinding.tvGroupsTeacher.setTextColor(getResources().getColor(R.color.red));
                } else {
                    activityHomeBinding.ivUnitsTrainer.setBackgroundResource(R.drawable.units_normal);
                    activityHomeBinding.ivGroupsTrainer.setBackgroundResource(R.drawable.group_select);

                    activityHomeBinding.tvUnitsTrainer.setTextColor(getResources().getColor(R.color.grey));
                    activityHomeBinding.tvGroupsTrainer.setTextColor(getResources().getColor(R.color.red));
                }
                break;
        }
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
//                makeUserLogout();
                return true;
            case R.id.chats:
                showChatUI();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showChatUI() {
        AppUtils.showChatUI(this);
    }

    private void makeUserLogout() {
        sharedPref.deleteAllData();
        Intent intent = new Intent(HomeActivity.this, MobileLoginActivity.class);
        startActivity(intent);
        this.finish();
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
    }

    private void ShowAboutUsActivity() {
        AppUtils.showAboutUsActivity(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (homeViewModel.teacherLogin)
            getMenuInflater().inflate(R.menu.teacher_menu, menu);
        else
            getMenuInflater().inflate(R.menu.trainer_menu, menu);
        return true;
    }

    boolean doubleBackToExitPressedOnce = false;


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            this.finish();
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getResources().getString(R.string.double_back_exit), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }


    @Override
    public void onItemClick(CatalogueDetailsModel item, int position) {
        Intent intent = new Intent(HomeActivity.this, SectionActivity.class);
        intent.putExtra("CatalogDetailsModel", item);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_slide_in_left,
                R.anim.anim_slide_out_left);
    }

    @Override
    public void onItemClick(GroupModel item) {

        if (item != null) {
            Intent intent = new Intent(HomeActivity.this, ChatAndContributionActivity.class);
            intent.putExtra("groupUUID", item.getGroupUUID());
            intent.putExtra("groupName", item.getGroupName());
            startActivity(intent);
            overridePendingTransition(R.anim.anim_slide_in_left,
                    R.anim.anim_slide_out_left);
        }
    }

    @Override
    public void onMediaDownload(int type, String savedPath, String name, int position, String uuid, int dcfId, String unitUUID) {
        homeViewModel.setDataInserted(1);
//        if (uuid.equals("1111")) {
//            playVideo();
//        }
    }

    public void onCallNextActivity(){
        Intent intent = new Intent(this, CreateGroupActivity.class);
        intent.putExtra("groupUUID","");
        intent.putExtra("groupName", "");
        startActivityForResult(intent, 101);
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK) {
            homeViewModel.apiCountMutable.setValue(0);
            homeViewModel.callApiForGroupList(sharedPref.readString(Constants.USER_ID,""));
            groupsFragment.setValueToAdapter();
        }else if (requestCode == 102 && resultCode == RESULT_OK) {
            homeViewModel.apiCountMutable.setValue(0);
            homeViewModel.callApiForGroupList(sharedPref.readString(Constants.USER_ID,""));
            groupsFragment.setValueToAdapter();
        }
    }
}
