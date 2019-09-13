package mahiti.org.oelp.views.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import mahiti.org.oelp.MySMSBroadCastReceiver;
import mahiti.org.oelp.R;
import mahiti.org.oelp.databinding.ActivityVerificationBinding;
import mahiti.org.oelp.models.MobileVerificationResponseModel;
import mahiti.org.oelp.models.UserDetailsModel;
import mahiti.org.oelp.utils.Action;
import mahiti.org.oelp.utils.AppUtils;
import mahiti.org.oelp.utils.Constants;
import mahiti.org.oelp.utils.MySharedPref;
import mahiti.org.oelp.viewmodel.OTPVerificationViewModel;


public class OTPVerificationActivity extends AppCompatActivity {

    private static final String TAG = OTPVerificationActivity.class.getSimpleName();
    public OTPVerificationViewModel verificationViewModel;
    private MySMSBroadCastReceiver receiver;
    private IntentFilter filter;
    ActivityVerificationBinding verificationActivityBinding;
    private Button btnResend;
    private Button btnSubmit;
    private EditText etOtp1;
    private EditText etOtp2;
    private EditText etOtp3;
    private EditText etOtp4;
    /*private OneTimeWorkRequest workRequest;*/
    MySharedPref sharedPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        verificationActivityBinding = DataBindingUtil.setContentView(this, R.layout.activity_verification);
        verificationViewModel = ViewModelProviders.of(this).get(OTPVerificationViewModel.class);
        verificationActivityBinding.setVerificationViewModel(verificationViewModel);
        verificationActivityBinding.setLifecycleOwner(this);
        receiver = new MySMSBroadCastReceiver();
        filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        btnResend = verificationActivityBinding.btnResend;
        btnSubmit = verificationActivityBinding.btnSubmit;
        btnSubmit.setEnabled(false);



        sharedPref = new MySharedPref(this);

        etOtp1 = verificationActivityBinding.etOpt1;
        etOtp2 = verificationActivityBinding.etOpt2;
        etOtp3 = verificationActivityBinding.etOpt3;
        etOtp4 = verificationActivityBinding.etOpt4;

        getIntentData();


        /*workRequest = verificationViewModel.workRequest.getValue();


        WorkManager.getInstance().getWorkInfoByIdLiveData(workRequest.getId())
                .observe(this, workInfo -> {
                    //receiving back the data
                    if(workInfo != null && workInfo.getState().isFinished()){
                        String currentTime = workInfo.getOutputData().getString(OTPTimer.TASK_DESC) + "\n";
                        if (currentTime.equals("30")){
                            verificationActivityBinding.btnResend.setEnabled(true);
                            WorkManager.getInstance().cancelWorkById(workRequest.getId());
                            verificationViewModel.resendButtonText.setValue(getResources().getString(R.string.resend_code));

                        }else {
                            verificationViewModel.resendButtonText.setValue(currentTime);
                            verificationActivityBinding.btnResend.setEnabled(false);

                        }
                    }
                });*/


        verificationViewModel.getData().observe(this, data -> {
            if (data != null) {
                handleData(data);
            }
        });

        verificationViewModel.status.observe(this, status -> {
            if (status != null) {
                Toast.makeText(this, status, Toast.LENGTH_SHORT).show();
            }
        });

        etOtp2.setOnKeyListener((view, i, keyEvent) -> {
//            if (i == KeyEvent.KEYCODE_DEL) {
//                etOtp1.requestFocus();
//            }

            return false;
        });

        etOtp3.setOnKeyListener((view, i, keyEvent) -> {
//            if (i == KeyEvent.KEYCODE_DEL) {
//                etOtp2.requestFocus();
//            }

            return false;
        });

        etOtp4.setOnKeyListener((view, i, keyEvent) -> {
//            if (i == KeyEvent.KEYCODE_DEL) {
//                etOtp3.requestFocus();
//            }

            return false;
        });


        etOtp1.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!AppUtils.textEmpty(editable.toString())) {
                    etOtp2.requestFocus();
                    checkTextInOTPET();
                }
            }

        });

        verificationViewModel.clearOTPText().observe(this, aBoolean -> {
            if (aBoolean != null)
                clearAllOTPText();
        });

        etOtp2.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!AppUtils.textEmpty(editable.toString())) {
                    etOtp3.requestFocus();
                }
            }

        });

        etOtp3.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!AppUtils.textEmpty(editable.toString()))
                    etOtp4.requestFocus();
            }

        });

        etOtp4.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!AppUtils.textEmpty(editable.toString())) {
                    btnSubmit.setEnabled(true);
                    verificationViewModel.onSubmitClick();
                }
            }

        });


    }

    private void checkTextInOTPET() {
        if ((etOtp1.getText().toString() + etOtp2.getText().toString() + etOtp3.getText().toString() + etOtp4.getText().toString()).isEmpty())
            verificationViewModel.clearButtonVisible.setValue(false);
        else
            verificationViewModel.clearButtonVisible.setValue(true);
    }

    private void clearAllOTPText() {
        etOtp1.setText("");
        etOtp2.setText("");
        etOtp3.setText("");
        etOtp4.setText("");

        etOtp1.requestFocus();
    }

    private void getIntentData() {
        if (getIntent().getData() != null) {
            UserDetailsModel userDetails = getIntent().getParcelableExtra("UserDetails");
            verificationViewModel.userData.setValue(userDetails);
        }
    }

    private void moveToAppIntroNextActivity(boolean isIntroDisplayed) {
        Intent intent;
        if (isIntroDisplayed)
            intent = new Intent(OTPVerificationActivity.this, HomeActivity.class);
        else
            intent = new Intent(OTPVerificationActivity.this, AppIntroActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_slide_in_left,
                R.anim.anim_slide_out_left);
    }

    private void moveToHomeActivityNextActivity() {
        Intent intent = new Intent(OTPVerificationActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_slide_in_left,
                R.anim.anim_slide_out_left);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);

    }


    private void handleData(@NonNull final MobileVerificationResponseModel data) {

        if (data.getmAction().getValue() == Action.RESEND_OTP) {
            Toast.makeText(this, data.getMessage(), Toast.LENGTH_SHORT).show();
        } else if (data.getmAction().getValue() == Action.VERIFY_OTP) {
            compareUserTypeAndMoveToNextActivity();
        } else if (data.getmAction().getValue() == Action.MOVE_TO_CHANGE_MOBILE_ACTIVITY) {
            moveToChangeMobileNoActivity();
//            saveUserTypeToPref();
        } else {
            Toast.makeText(this, data.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void compareUserTypeAndMoveToNextActivity() {
        boolean isIntroDisplayed = sharedPref.readBoolean(Constants.IS_INTRO_DISPLAYED, false);
        int userType = sharedPref.readInt(Constants.USER_TYPE, 0);
        if (userType == Constants.USER_TEACHER) {
            moveToAppIntroNextActivity(isIntroDisplayed);
        } else if (userType == Constants.USER_TRAINER) {
            moveToHomeActivityNextActivity();
        }
    }


    private void moveToChangeMobileNoActivity() {
        Intent intent = new Intent(OTPVerificationActivity.this, ChangeMobileNoActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_slide_in_left,
                R.anim.anim_slide_out_left);
    }
}
