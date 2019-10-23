package mahiti.org.oelp.views.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mahiti.org.oelp.R;
import mahiti.org.oelp.chat.service.XMPP;
import mahiti.org.oelp.chat.utilies.ConnectionUtils;
import mahiti.org.oelp.databinding.ActivityTeacherRegistrationBinding;
import mahiti.org.oelp.models.LocationContent;
import mahiti.org.oelp.models.MobileVerificationResponseModel;
import mahiti.org.oelp.models.UserDetailsModel;
import mahiti.org.oelp.utils.Action;
import mahiti.org.oelp.utils.Constants;
import mahiti.org.oelp.utils.MySharedPref;
import mahiti.org.oelp.viewmodel.TeacherRegistrationViewModel;
import mahiti.org.oelp.xmpp.XmppConnection;


public class TeacherRegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    TeacherRegistrationViewModel teacherRegistrationViewModel;
    ActivityTeacherRegistrationBinding teacherRegistrationActivityBinding;

    RelativeLayout rlState;
    RelativeLayout rlDistrict;
    RelativeLayout rlBlock;
    String mobileNo;
    private int activityType;

    XmppConnection connection = null;
    ConnectionUtils connectionUtils = new ConnectionUtils();


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
            if (s != null) {
                showToast(s);
            }
        });

        teacherRegistrationViewModel.errorBlock.observe(this, s -> {
            if (s != null) {
                showToast(s);
            }
        });

        teacherRegistrationViewModel.errorVillage.observe(this, s -> {
            if (s != null) {
                showToast(s);
            }
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
        if (!s.isEmpty())
            Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    private void setErrorState(String s) {
        if (!s.isEmpty()) {
            showToast(s);
        }

    }


    private void getIntentData() {
        activityType = getIntent().getIntExtra("ActivityType", 0);
        teacherRegistrationViewModel.setActivityType(activityType);
        if (activityType == 0) {
            mobileNo = new MySharedPref(this).readString(Constants.MOBILE_NO_New, "");
            teacherRegistrationViewModel.phoneNo.setValue(mobileNo);
        } else {
            UserDetailsModel userDetail = getIntent().getParcelableExtra("UserDetails");
            if (userDetail.getName() != null && !userDetail.getName().isEmpty()) {
                teacherRegistrationViewModel.name.setValue(userDetail.getName());
            }
            if (userDetail.getMobile_number() != null && userDetail.getMobile_number().isEmpty()) {
                teacherRegistrationViewModel.phoneNo.setValue(userDetail.getMobile_number());
            }
            if (userDetail.getSchool() != null && userDetail.getSchool().isEmpty()) {
                teacherRegistrationViewModel.school.setValue(userDetail.getSchool());
            }
            if (userDetail.getStateName() != null && userDetail.getStateName().isEmpty()) {
                teacherRegistrationViewModel.state.setValue(userDetail.getStateName());
            }
            if (userDetail.getDistrictname() != null && userDetail.getDistrictname().isEmpty()) {
                teacherRegistrationViewModel.district.setValue(userDetail.getDistrictname());
            }
            if (userDetail.getBlockName() != null && userDetail.getBlockName().isEmpty()) {
                teacherRegistrationViewModel.block.setValue(userDetail.getBlockName());
            }
            if (userDetail.getVillageName() != null && userDetail.getVillageName().isEmpty()) {
                teacherRegistrationViewModel.village.setValue(userDetail.getVillageName());
            }
        }
    }

    private void handleData(@NonNull final MobileVerificationResponseModel data) {

        if (data.getmAction().getValue() == Action.STATUS_FALSE) {
            Toast.makeText(this, data.getMessage(), Toast.LENGTH_SHORT).show();
        } else if (data.getmAction().getValue() == Action.STATUS_TRUE) {
            if (activityType == 0)
                if (register(data.getUseridreg(), Constants.CHAT_PASSWORD))
                    moveToVerifyActivity(data.getUserDetails());
            else
                movetOPreviousActivity(teacherRegistrationViewModel.getUserDetailsData());
        }
    }

    private void movetOPreviousActivity(UserDetailsModel userDetailsData) {
        Intent intent = new Intent();
        intent.putExtra("UserDetails", userDetailsData);
        setResult(RESULT_OK, intent);
        finish();
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
        List<LocationContent> stateList = teacherRegistrationViewModel.getStateSpinnerData().getValue();
        if (stateList != null && !stateList.isEmpty()) {
            contentArrayList.addAll(stateList);
        }

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
        List<LocationContent> districtList = teacherRegistrationViewModel.getDistrictSpinnerData(id, boundaryLevelType).getValue();
        if (districtList != null && !districtList.isEmpty()) {
            contentArrayList.addAll(districtList);
        }

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
        List<LocationContent> blockList = teacherRegistrationViewModel.getBlockSpinnerData(id, boundaryLevelType).getValue();
        if (blockList != null && !blockList.isEmpty()) {
            contentArrayList.addAll(blockList);
        }


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

    private boolean register(final String paramString1, final String paramString2) {
        try {
            XMPP.getInstance().register(paramString1, paramString2);
            return true;

        } catch (XMPPException localXMPPException) {
            localXMPPException.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SmackException e) {
            e.printStackTrace();
        }
        return false;
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
