package oelp.mahiti.org.newoepl.views.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import oelp.mahiti.org.newoepl.R;
import oelp.mahiti.org.newoepl.databinding.ActivityHomeBinding;
import oelp.mahiti.org.newoepl.fileandvideodownloader.DownloadClass;
import oelp.mahiti.org.newoepl.fileandvideodownloader.OnMediaDownloadListener;
import oelp.mahiti.org.newoepl.interfaces.ItemClickListerner;
import oelp.mahiti.org.newoepl.models.CatalogueDetailsModel;
import oelp.mahiti.org.newoepl.services.RetrofitConstant;
import oelp.mahiti.org.newoepl.utils.AppUtils;
import oelp.mahiti.org.newoepl.utils.Constants;
import oelp.mahiti.org.newoepl.utils.MySharedPref;
import oelp.mahiti.org.newoepl.viewmodel.HomeViewModel;
import oelp.mahiti.org.newoepl.views.fragments.GroupsFragment;
import oelp.mahiti.org.newoepl.views.fragments.HomeFragment;
import oelp.mahiti.org.newoepl.views.fragments.UnitsFragment;

public class HomeActivity extends AppCompatActivity implements ItemClickListerner, OnMediaDownloadListener {

    Toolbar toolbar;
    ActivityHomeBinding activityHomeBinding;
    HomeViewModel homeViewModel;
    private String previouValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityHomeBinding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        activityHomeBinding.setHomeViewModel(homeViewModel);
        activityHomeBinding.setLifecycleOwner(this);
        toolbar = findViewById(R.id.white_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }
        toolbar.inflateMenu(R.menu.teacher_menu);

//        setImageAndTextColor(Constants.Units);
//        setFragment(Constants.Units);


        homeViewModel.homeClick.observe(this, aBoolean -> {
            if (aBoolean != null && aBoolean) {
                setImageAndTextColor(Constants.Home);
                setFragment(Constants.Home);
            }
        });

        homeViewModel.unitsClick.observe(this, aBoolean -> {
            if (aBoolean != null && aBoolean) {
                setImageAndTextColor(Constants.Units);
                setFragment(Constants.Units);
                homeViewModel.title.setValue(getResources().getString(R.string.units));
            }
        });

        homeViewModel.groupsClick.observe(this, aBoolean -> {
            if (aBoolean != null && aBoolean) {
                setImageAndTextColor(Constants.Groups);
                setFragment(Constants.Groups);
            }
        });

        homeViewModel.getApiErrorMessage().observe(this, s -> {
            if (s != null)
                Toast.makeText(HomeActivity.this, s, Toast.LENGTH_SHORT).show();
        });
        homeViewModel.getDataInserted().observe(this, integer -> {
            if (integer!=null)
                setFragment(Constants.Units);
        });

//        homeViewModel.getDataInserted().observe(this, aLong -> {
//            if (aLong != null && !aLong)
//
//        });

        homeViewModel.getListOfImageToDownload().observe(this, fileModels ->
                new DownloadClass(Constants.IMAGE, HomeActivity.this, RetrofitConstant.BASE_URL, AppUtils.completePathInSDCard(Constants.IMAGE).getAbsolutePath(), fileModels));
    }

    private void setFragment(String type) {
        Fragment fragment = null;
        switch (type) {
            case Constants.Home:
                fragment = new HomeFragment();
                break;
            case Constants.Units:
                previouValue = Constants.Units;
                homeViewModel.parentId.setValue("");
                Bundle bundle = new Bundle();
                bundle.putString("ParentId", "");
                bundle.putString("Title", getResources().getString(R.string.units));
                fragment = new UnitsFragment();
                fragment.setArguments(bundle);
                break;
            case Constants.Groups:
                fragment = new GroupsFragment();
                break;
        }
        if (fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//            transaction.setCustomAnimations(R.anim.zoom_in, R.anim.zoom_out);
            transaction.replace(R.id.fragmentContainer, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    private void setImageAndTextColor(String type) {
        switch (type) {
            case Constants.Home:
                activityHomeBinding.ivHome.setBackgroundResource(R.drawable.home_select);
                activityHomeBinding.ivUnits.setBackgroundResource(R.drawable.units_normal);
                activityHomeBinding.ivGroups.setBackgroundResource(R.drawable.group_normal);

                activityHomeBinding.tvHome.setTextColor(getResources().getColor(R.color.colorPrimary));
                activityHomeBinding.tvUnits.setTextColor(getResources().getColor(R.color.grey));
                activityHomeBinding.tvGroups.setTextColor(getResources().getColor(R.color.grey));
                break;
            case Constants.Units:
                activityHomeBinding.ivHome.setBackgroundResource(R.drawable.home_normal);
                activityHomeBinding.ivUnits.setBackgroundResource(R.drawable.units_select);
                activityHomeBinding.ivGroups.setBackgroundResource(R.drawable.group_normal);

                activityHomeBinding.tvHome.setTextColor(getResources().getColor(R.color.grey));
                activityHomeBinding.tvUnits.setTextColor(getResources().getColor(R.color.colorPrimary));
                activityHomeBinding.tvGroups.setTextColor(getResources().getColor(R.color.grey));
                break;
            case Constants.Groups:
                activityHomeBinding.ivHome.setBackgroundResource(R.drawable.home_normal);
                activityHomeBinding.ivUnits.setBackgroundResource(R.drawable.units_normal);
                activityHomeBinding.ivGroups.setBackgroundResource(R.drawable.group_select);

                activityHomeBinding.tvHome.setTextColor(getResources().getColor(R.color.grey));
                activityHomeBinding.tvUnits.setTextColor(getResources().getColor(R.color.grey));
                activityHomeBinding.tvGroups.setTextColor(getResources().getColor(R.color.colorPrimary));
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
                makeUserLogout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void makeUserLogout() {
        new MySharedPref(this).deleteAllData();
        Intent intent = new Intent(HomeActivity.this, MobileLoginActivity.class);
        startActivity(intent);
        this.finish();
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
    }

    private void ShowAboutUsActivity() {
        Toast.makeText(getApplicationContext(), "About Us Clicked", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.teacher_menu, menu);
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
    public void onItemClick(CatalogueDetailsModel item) {
        Intent intent = new Intent(HomeActivity.this, SectionActivity.class);
        intent.putExtra("CatalogDetailsModel", item);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_slide_in_left,
                R.anim.anim_slide_out_left);
    }

    @Override
    public void onMediaDownload(int type, String savedPath, String name, int position, String uuid) {
        homeViewModel.showProgresBar.setValue(false);
        homeViewModel.setDataInserted(1);
    }
}
