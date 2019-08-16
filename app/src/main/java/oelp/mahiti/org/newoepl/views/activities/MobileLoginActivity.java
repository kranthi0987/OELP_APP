package oelp.mahiti.org.newoepl.views.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import oelp.mahiti.org.newoepl.R;
import oelp.mahiti.org.newoepl.databinding.ActivityMobileLoginBinding;
import oelp.mahiti.org.newoepl.models.MobileVerificationResponseModel;
import oelp.mahiti.org.newoepl.models.UserDetailsModel;
import oelp.mahiti.org.newoepl.utils.Action;
import oelp.mahiti.org.newoepl.utils.AppUtils;
import oelp.mahiti.org.newoepl.utils.Constants;
import oelp.mahiti.org.newoepl.viewmodel.MobileLoginViewModel;


public class MobileLoginActivity extends AppCompatActivity {

    private MobileLoginViewModel mobileLoginViewModel;
    ActivityMobileLoginBinding activityMobileLoginBinding;
    EditText etMobileNo;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activityMobileLoginBinding= DataBindingUtil.setContentView(this,R.layout.activity_mobile_login);
        mobileLoginViewModel = ViewModelProviders.of(this).get(MobileLoginViewModel.class);
        activityMobileLoginBinding.setViewModel(mobileLoginViewModel);
        activityMobileLoginBinding.setLifecycleOwner(this);

        etMobileNo = activityMobileLoginBinding.mobileNo;
        btnSubmit = activityMobileLoginBinding.btnSubmit;
        btnSubmit.setEnabled(false);

        mobileLoginViewModel.status.observe(this, s -> {
            if (!s.isEmpty() && !s.equalsIgnoreCase(Constants.STATUS_TRUE))
                etMobileNo.setError(s);

        });

       

        mobileLoginViewModel.getData().observe(this, data -> {
            if(data != null){
                handleData(data);
            }
        });


        etMobileNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length()==10){
                    checkMobileValidation(editable.toString(), etMobileNo);
                }
            }
        });
    }



    private void checkMobileValidation(String phoneNo, EditText etMobile) {
        String status = AppUtils.checkMobileValidation(this, phoneNo);
        if (!status.equalsIgnoreCase(Constants.STATUS_TRUE)){
            etMobile.setError(status);
            btnSubmit.setEnabled(false);
        }else {
            etMobile.setError(null);
            btnSubmit.setEnabled(true);
        }
    }


    private void handleData(@NonNull final MobileVerificationResponseModel data) {
        if (data.getmAction().getValue()==Action.STATUS_TRUE) {
            switch (data.getStatus()) {
                case 0:
                    Toast.makeText(this, data.getMessage(), Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    checkUserIsValid(data);
                    break;
            }
        }else {
            Toast.makeText(this, data.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void checkUserIsValid(MobileVerificationResponseModel data) {
        if (data.getUserDetails()!=null) {
            if (!data.getUserDetails().getUserid().equals(Constants.USER_INVALID)) {
                moveToVerifyActivity(data.getUserDetails());
            } else {
                moveToRegistrationActivity();
            }
        }else{
            Toast.makeText(this, data.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void moveToRegistrationActivity() {
        Intent intent = new Intent(MobileLoginActivity.this, TeacherRegistrationActivity.class);
        intent.putExtra("mobileno", etMobileNo.getText().toString());
        startActivity(intent);
        overridePendingTransition(R.anim.anim_slide_in_left,
                R.anim.anim_slide_out_left);
    }


    private void moveToVerifyActivity(UserDetailsModel userDetails) {
        Intent intent = new Intent(MobileLoginActivity.this, OTPVerificationActivity.class);
        intent.putExtra("userData", userDetails);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_slide_in_left,
                R.anim.anim_slide_out_left);
    }
}