package mahiti.org.oelp.views.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import mahiti.org.oelp.R;
import mahiti.org.oelp.databinding.ActivityTeacherRegistrationBinding;
import mahiti.org.oelp.models.LocationContent;
import mahiti.org.oelp.models.MobileVerificationResponseModel;
import mahiti.org.oelp.models.UserDetailsModel;
import mahiti.org.oelp.utils.Action;
import mahiti.org.oelp.utils.Constants;
import mahiti.org.oelp.utils.MySharedPref;
import mahiti.org.oelp.viewmodel.TeacherRegistrationViewModel;


public class TeacherRegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    TeacherRegistrationViewModel teacherRegistrationViewModel;
    ActivityTeacherRegistrationBinding teacherRegistrationActivityBinding;

    RelativeLayout rlState;
    RelativeLayout rlDistrict;
    RelativeLayout rlBlock;
    String mobileNo;



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        teacherRegistrationActivityBinding = DataBindingUtil.setContentView(this, R.layout.activity_teacher_registration);

        teacherRegistrationViewModel = ViewModelProviders.of(this).get(TeacherRegistrationViewModel.class);

        teacherRegistrationActivityBinding.setRegistrationViewModel(teacherRegistrationViewModel);
        teacherRegistrationActivityBinding.setLifecycleOwner(this);

        rlState = teacherRegistrationActivityBinding.rlState;
        rlDistrict = teacherRegistrationActivityBinding.rlDistrict;
        rlBlock = teacherRegistrationActivityBinding.rlBlock;

        rlState.setOnClickListener(this);
        rlDistrict.setOnClickListener(this);
        rlBlock.setOnClickListener(this);
        getIntentData();
        teacherRegistrationViewModel.getAction().observe(this, action -> {
            if (action != null) {
                handleAction(action);
            }
        });

        teacherRegistrationViewModel.errorName.observe(this, s -> {
            if (s != null) {
                showToast(s);
//                teacherRegistrationActivityBinding.etName.setError(s);
            }
        });

        teacherRegistrationViewModel.errorSchool.observe(this, s -> {
            if (s != null) {
                showToast(s);
//                teacherRegistrationActivityBinding.etSchool.setError(s);
            }
        });

        teacherRegistrationViewModel.errorState.observe(this, s -> {
            if (s != null) {
                setErrorState(s);
            }
        });

        teacherRegistrationViewModel.errorDistrict.observe(this, s -> {
//            if (s != null) {
//                setErrorDistrict(s);
//            }
        });

        teacherRegistrationViewModel.errorBlock.observe(this, s -> {
//            if (s != null) {
//                setErrorBlock(s);
//            }
        });

        teacherRegistrationViewModel.errorVillage.observe(this, s -> {
//            if (s != null) {
//                teacherRegistrationActivityBinding.etVillage.setError(s);
//            }
        });


        teacherRegistrationViewModel.getInsertLong().observe(this, aLong -> {
            if (aLong != null) {
                setAdapterToStateSpinner();
            }
        });


        teacherRegistrationViewModel.getData().observe(this, data -> {
            if (data != null) {
                handleData(data);
            }
        });

    }

    private void showToast(String s) {
            Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    private void setErrorState(String s) {
        if (!s.isEmpty()){
            showToast(s);
        }

    }


    private void getIntentData() {
        mobileNo = new MySharedPref(this).readString(Constants.MOBILE_NO, "");
        teacherRegistrationViewModel.phoneNo.setValue(mobileNo);
    }

    private void handleData(@NonNull final MobileVerificationResponseModel data) {

        if (data.getmAction().getValue() == Action.STATUS_FALSE) {
            Toast.makeText(this, data.getMessage(), Toast.LENGTH_SHORT).show();
        } else if (data.getmAction().getValue() == Action.STATUS_TRUE) {
            moveToVerifyActivity(data.getUserDetails());
        }
    }

    private void moveToVerifyActivity(UserDetailsModel userDetails) {
        Intent intent = new Intent(TeacherRegistrationActivity.this, OTPVerificationActivity.class);
        intent.putExtra("userId", userDetails);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_slide_in_left,
                R.anim.anim_slide_out_left);
    }


    private void setAdapterToStateSpinner() {
        final int[] stateCount = {0};
        List<LocationContent> contentArrayList = new ArrayList<>();
        LocationContent locationContent = new LocationContent();
        locationContent.setName(getResources().getString(R.string.please_select_state));
        contentArrayList.add(locationContent);
        contentArrayList.addAll(teacherRegistrationViewModel.getStateSpinnerData().getValue());
        if (contentArrayList != null && !contentArrayList.isEmpty()) {
//            ArrayAdapter<LocationContent> stateArrayAdpter = new ArrayAdapter<>(this, R.layout.custom_spinner_item,R.id.tvSpinnerItem, contentArrayList);
            ArrayAdapter<LocationContent> stateArrayAdpter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, contentArrayList);
            stateArrayAdpter.setDropDownViewResource(R.layout.custom_spinner_item);
            teacherRegistrationActivityBinding.setSpinnerState(stateArrayAdpter);
        }
        teacherRegistrationActivityBinding.spinnerState1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (stateCount[0] != 0) {
                    if (i == 0) {
                        onNothingSelected(adapterView);
                    } else {
                        teacherRegistrationViewModel.districtClickable.setValue(true);
                        teacherRegistrationViewModel.errorState.setValue("");
                        LocationContent content = contentArrayList.get(i);
                        teacherRegistrationViewModel.state.setValue(content.getName());
                        teacherRegistrationViewModel.stateId.setValue(content.getId());
                        setAdapterToDistrictSpinner(content.getId(), content.getBoundaryLevelType() + 1);
                    }
                }
                stateCount[0] = 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                teacherRegistrationViewModel.districtClickable.setValue(false);
                teacherRegistrationViewModel.blockClickable.setValue(false);
                teacherRegistrationViewModel.state.setValue("");
                teacherRegistrationViewModel.district.setValue(getString(R.string.please_select_district));
                teacherRegistrationViewModel.districtId.setValue(0);
                teacherRegistrationViewModel.block.setValue(getString(R.string.please_select_block));
                teacherRegistrationViewModel.blockId.setValue(0);
                teacherRegistrationViewModel.errorState.setValue(getResources().getString(R.string.please_select_state));
            }
        });


    }

    private void setAdapterToDistrictSpinner(Integer id, Integer boundaryLevelType) {
        final int[] districtCount = {0};
        List<LocationContent> contentArrayList = new ArrayList<>();
        LocationContent locationContent = new LocationContent();
        locationContent.setName(getResources().getString(R.string.please_select_district));
        contentArrayList.add(locationContent);
        contentArrayList.addAll(teacherRegistrationViewModel.getDistrictSpinnerData(id, boundaryLevelType).getValue());

//        ArrayAdapter<LocationContent> stateArrayAdpter = new ArrayAdapter<>(this, R.layout.custom_spinner_item,R.id.tvSpinnerItem, contentArrayList);
        ArrayAdapter<LocationContent> stateArrayAdpter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, contentArrayList);
        stateArrayAdpter.setDropDownViewResource(R.layout.custom_spinner_item);
        teacherRegistrationActivityBinding.setSpinnerDistrict(stateArrayAdpter);
        teacherRegistrationActivityBinding.spinnerDistrict1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (districtCount[0] != 0) {
                    if (i == 0) {
                        onNothingSelected(adapterView);
                    } else {
                        teacherRegistrationViewModel.blockClickable.setValue(true);
                        teacherRegistrationViewModel.errorDistrict.setValue("");
                        LocationContent content = contentArrayList.get(i);
                        teacherRegistrationViewModel.district.setValue(content.getName());
                        teacherRegistrationViewModel.districtId.setValue(content.getId());
                        setAdapterToBlockSpinner(content.getId(), content.getBoundaryLevelType() + 1);
                    }
                }
                districtCount[0] = 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                teacherRegistrationViewModel.district.setValue(getResources().getString(R.string.please_select_district));
                teacherRegistrationViewModel.blockClickable.setValue(false);
                teacherRegistrationViewModel.errorDistrict.setValue(getResources().getString(R.string.please_select_district));
                teacherRegistrationViewModel.block.setValue(getString(R.string.please_select_block));
                teacherRegistrationViewModel.blockId.setValue(0);

            }
        });

    }

    private void setAdapterToBlockSpinner(Integer id, Integer boundaryLevelType) {
        final int[] blockCount = {0};
        List<LocationContent> contentArrayList = new ArrayList<>();
        LocationContent locationContent = new LocationContent();
        locationContent.setName(getResources().getString(R.string.please_select_block));
        contentArrayList.add(locationContent);
        contentArrayList.addAll(teacherRegistrationViewModel.getBlockSpinnerData(id, boundaryLevelType).getValue());


        ArrayAdapter<LocationContent> stateArrayAdpter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, contentArrayList);
        stateArrayAdpter.setDropDownViewResource(R.layout.custom_spinner_item);
        teacherRegistrationActivityBinding.setSpinnerBlock(stateArrayAdpter);
        teacherRegistrationActivityBinding.spinnerBlock1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (blockCount[0] != 0) {
                    if (i == 0) {
                        onNothingSelected(adapterView);
                    } else {
                        teacherRegistrationViewModel.errorBlock.setValue("");
                        LocationContent content = contentArrayList.get(i);
                        teacherRegistrationViewModel.block.setValue(content.getName());
                        teacherRegistrationViewModel.blockId.setValue(content.getId());
                    }
                }
                blockCount[0] = 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                teacherRegistrationViewModel.block.setValue(getResources().getString(R.string.please_select_block));
                teacherRegistrationViewModel.blockId.setValue(0);
                teacherRegistrationViewModel.errorBlock.setValue(getResources().getString(R.string.please_select_block));

            }
        });
    }

    private void setAdapterToVillageSpinner(Integer id, Integer boundaryLevelType) {
//        List<LocationContent> contentArrayList = teacherRegistrationViewModel.getVillageSpinnerData(id, boundaryLevelType).getValue();
//        ArrayAdapter<LocationContent> stateArrayAdpter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, contentArrayList);
//        teacherRegistrationActivityBinding.setSpinnerVillage(stateArrayAdpter);
//        teacherRegistrationActivityBinding.spinnerVillage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                LocationContent content = contentArrayList.get(i);
//                teacherRegistrationActivityBinding.etVillage.setText(content.getName());
//                teacherRegistrationViewModel.errorVillage.setValue(null);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//                teacherRegistrationActivityBinding.etVillage.setTextColor(Color.RED);
//                teacherRegistrationActivityBinding.etVillage.setText(getResources().getString(R.string.please_select_village));
//                teacherRegistrationViewModel.errorVillage.setValue(getResources().getString(R.string.please_select_village));
//
//            }
//        });
    }

    private void handleAction(@NonNull final Action action) {
        switch (action.getValue()) {
            case Action.STATUS_TRUE:
                moveToNextActivity();
                break;
            case Action.STATUS_FALSE:
                //show Toast
                break;
        }
    }


    private void moveToNextActivity() {
//        Intent intent = new Intent(TeacherRegistrationActivity.this, AppIntroActivity.class);
        Intent intent = new Intent(TeacherRegistrationActivity.this, HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_slide_in_left,
                R.anim.anim_slide_out_left);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rlState:
                teacherRegistrationActivityBinding.spinnerState1.performClick();
                break;
            case R.id.rlDistrict:
                teacherRegistrationActivityBinding.spinnerDistrict1.performClick();
                break;
            case R.id.rlBlock:
                teacherRegistrationActivityBinding.spinnerBlock1.performClick();
                break;
//            case R.id.rlVillage:
//                teacherRegistrationActivityBinding.spinnerVillage.performClick();
//                break;

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);

    }

}
