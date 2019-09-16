package mahiti.org.oelp.views.activities;

import androidx.lifecycle.ViewModelProviders;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import mahiti.org.oelp.R;
import mahiti.org.oelp.databinding.ActivityChangeMobileNoBinding;
import mahiti.org.oelp.models.MobileVerificationResponseModel;
import mahiti.org.oelp.services.RetrofitConstant;
import mahiti.org.oelp.utils.Action;
import mahiti.org.oelp.utils.AppUtils;
import mahiti.org.oelp.utils.Constants;
import mahiti.org.oelp.utils.MySharedPref;
import mahiti.org.oelp.viewmodel.ChangeMobileNoViewModel;

public class ChangeMobileNoActivity extends AppCompatActivity {

    ChangeMobileNoViewModel changeMobileNoViewModel;
    ActivityChangeMobileNoBinding activityChangeMobileNoBinding;
    private MySharedPref sharedPref;
    private String savedMobileNo;
    private EditText etMobileNo;
    private EditText etMobileNoNew;
    private EditText etMobileNoCnf;
    private Button btnSubmit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activityChangeMobileNoBinding = DataBindingUtil.setContentView(this, R.layout.activity_change_mobile_no);
        changeMobileNoViewModel = ViewModelProviders.of(this).get(ChangeMobileNoViewModel.class);
        activityChangeMobileNoBinding.setLifecycleOwner(this);
        activityChangeMobileNoBinding.setChangeMobileNoViewModel(changeMobileNoViewModel);
        sharedPref = new MySharedPref(this);


        savedMobileNo = sharedPref.readString(Constants.MOBILE_NO, "");

        etMobileNo = activityChangeMobileNoBinding.etOldMobile;
        etMobileNoNew = activityChangeMobileNoBinding.etNewMobileNo;
        etMobileNoCnf = activityChangeMobileNoBinding.etConfirmNewMobileNo;
        btnSubmit = activityChangeMobileNoBinding.btnSubmit;
        btnSubmit.setEnabled(false);


        etMobileNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() == 10) {
                    checkMobileValidation(editable.toString(), etMobileNo);
                    validateWithOldPhoneNo(editable.toString());
                } else {
                    checkMobileValidation(editable.toString(), etMobileNo);
                }
            }
        });

        etMobileNoNew.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() == 10) {
                    checkMobileValidation(editable.toString(), etMobileNoNew);
                } else {
                    checkMobileValidation(editable.toString(), etMobileNoNew);

                }
            }
        });

        etMobileNoCnf.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() == 10) {
                    checkMobileValidation(editable.toString(), etMobileNoCnf);
                    checkEnteredMobileIsMatchingOrNot(editable.toString(), etMobileNoNew.getText().toString());
                } else {
                    checkMobileValidation(editable.toString(), etMobileNoCnf);

                }
            }
        });

        changeMobileNoViewModel.getAction().observe(this, action -> {
            if (action != null) {
                handleAction(action);
            }
        });

        changeMobileNoViewModel.getData().observe(this, data -> {
            if (data != null) {
                handleData(data);
            }
        });

    }

    private void handleAction(Action action) {
        if (action.getValue() == Action.ON_BACK_PRESSED){
            onBackPressed();
        }
    }

    private void handleData(MobileVerificationResponseModel data) {
        if (data.getStatus().equals(RetrofitConstant.STATUS_TRUE)) {
            new MySharedPref(this).writeString(Constants.MOBILE_NO, changeMobileNoViewModel.newMobileNo.getValue());
            Toast.makeText(this, data.getMessage(), Toast.LENGTH_SHORT).show();
            finishCurrentActivity();
        } else {
            Toast.makeText(this, data.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void finishCurrentActivity() {
        ChangeMobileNoActivity.this.finish();
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
    }

    private void checkMobileValidation(String phoneNo, EditText etMobile) {
        String status = AppUtils.checkMobileValidation(this, phoneNo);
        if (!status.equalsIgnoreCase(Constants.STATUS_TRUE)) {
            etMobile.setError(status);
        } else {
            etMobile.setError(null);
        }
    }

    private void checkEnteredMobileIsMatchingOrNot(String cnfMobileNo, String newMobileNo) {
        if (!cnfMobileNo.equalsIgnoreCase(newMobileNo)) {
            etMobileNoCnf.setError(getResources().getString(R.string.please_enter_same_name));
        } else {
            etMobileNoCnf.setError(null);
        }
    }

    private void validateWithOldPhoneNo(String mobileNo) {
        if (!savedMobileNo.equalsIgnoreCase(mobileNo)) {
            etMobileNo.setError(getResources().getString(R.string.old_mobile_doesnt_exist));
        } else {
            etMobileNo.setError(null);

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);

    }
}
