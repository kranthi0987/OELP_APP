package oelp.mahiti.org.newoepl.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import oelp.mahiti.org.newoepl.R;
import oelp.mahiti.org.newoepl.models.MobileVerificationResponseModel;
import oelp.mahiti.org.newoepl.models.UserDetailsModel;
import oelp.mahiti.org.newoepl.services.ApiInterface;
import oelp.mahiti.org.newoepl.services.RetrofitClass;
import oelp.mahiti.org.newoepl.services.RetrofitConstant;
import oelp.mahiti.org.newoepl.utils.Action;
import oelp.mahiti.org.newoepl.utils.Constants;
import oelp.mahiti.org.newoepl.utils.Logger;
import oelp.mahiti.org.newoepl.utils.MySharedPref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by RAJ ARYAN on 23/07/19.
 */
public class OTPVerificationViewModel extends AndroidViewModel {


    private final String mobileNo;
    public MutableLiveData<String> otpMobileText = new MutableLiveData<>();
    public MutableLiveData<String> otp1 = new MutableLiveData<>();
    public MutableLiveData<String> otp2 = new MutableLiveData<>();
    public MutableLiveData<String> otp3 = new MutableLiveData<>();
    public MutableLiveData<String> otp4 = new MutableLiveData<>();
    public MutableLiveData<String> resendButtonText = new MutableLiveData<>();
    public MutableLiveData<String> resendButtonTime = new MutableLiveData<>();
    public MutableLiveData<Boolean> showProgresBar = new MutableLiveData<>();
    public MutableLiveData<Action> mAction = new MutableLiveData<>();
    private Context context;
    public MutableLiveData<String> status = new MutableLiveData<>();
    public MutableLiveData<MobileVerificationResponseModel> data = new MutableLiveData<>();
    public MutableLiveData<UserDetailsModel> userData = new MutableLiveData<>();
    public MutableLiveData<String> completeOtp = new MediatorLiveData<>();
    private RetrofitClass retroClass = RetrofitClass.getRetrofitClass();
    /*public MutableLiveData<OneTimeWorkRequest> workRequest = new MutableLiveData<>();*/
    private static final String TAG = OTPVerificationViewModel.class.getSimpleName();
    MySharedPref sharedPref;
    public MutableLiveData<Boolean> clearButtonVisible = new MutableLiveData<>();
    private MutableLiveData<Boolean> clearOTP = new MutableLiveData<>();


    public OTPVerificationViewModel(@NonNull Application application) {
        super(application);
        context = application;
        otp1.setValue("");
        otp2.setValue("");
        otp3.setValue("");
        otp4.setValue("");
        sharedPref = new MySharedPref(context);
        mobileNo = sharedPref.readString(Constants.MOBILE_NO, "");
        clearButtonVisible.setValue(false);
        String messageText = application.getString(R.string.otp_has_been_sent_to_your_mobile).concat(mobileNo.substring(6, 10)).concat("  ").concat(application.getString(R.string.please_enter_the_same));
        otpMobileText.setValue(messageText);
        resendButtonText.setValue(context.getResources().getString(R.string.resend_code));
        resendButtonText.setValue("00");
    }

    public void onResendClick() {
//        if (CheckNetwork.checkNet(context)) {
//            validateAndProceed();
//        } else {
//            Toast.makeText(context, context.getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
//        }
    }

    private void validateAndProceed() {
       /* workRequest.setValue(new OneTimeWorkRequest.Builder(OTPTimer.class).build());
        if (resendButtonTime.getValue().equals("00")) {
            WorkManager.getInstance().enqueue(workRequest.getValue());
            showProgresBar.setValue(true);
            getResendOTP(mobileNo);
        }*/
    }

    public void onSubmitClick() {
        completeOtp.setValue(otp1.getValue() + otp2.getValue() + otp3.getValue() + otp4.getValue());
        if (validateOTP(completeOtp.getValue())) {
            showProgresBar.setValue(true);
            getOTPVerified(prepareUserDetail(), completeOtp.getValue());
        }
    }

    public MutableLiveData<Boolean> clearOTPText(){
         clearOTP.setValue(true);
         clearButtonVisible.setValue(false);
         return clearOTP;
    }

    private boolean validateOTP(String value) {
        boolean statusOTP = true;
        if (value == null || value.isEmpty()) {
            statusOTP = false;
            this.status.setValue(context.getResources().getString(R.string.please_enter_OTP));
        } else if (value.length() != 4) {
            statusOTP = false;
            this.status.setValue(context.getResources().getString(R.string.please_enter_valid_OTP));
        }
        return statusOTP;
    }

    private String prepareUserDetail() {
        return sharedPref.readString(Constants.MOBILE_NO, "");
//        JSONObject object = new JSONObject();
//
//        userData.setValue();
//        try {
//            object.put("name", userData.getValue().getName());
//            object.put("school", userData.getValue().getSchool());
//            object.put("mobile_number", userData.getValue().getMobile_number());
//            object.put("blockIds", userData.getValue().getBlockIds());
//            userString = object.toString();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    public void changeMobileNumber() {
        showProgresBar.setValue(false);
        MobileVerificationResponseModel model = new MobileVerificationResponseModel();
        model.setmAction(new Action(Action.MOVE_TO_CHANGE_MOBILE_ACTIVITY));
        data.setValue(model);
    }


    public LiveData<MobileVerificationResponseModel> getData() {
        if (data != null)
            showProgresBar.setValue(false);
        return data;
    }

    public LiveData<Action> getAction() {
        return mAction;
    }

    private void getOTPVerified(String userString, String otpDigit) {

        ApiInterface apiInterface = RetrofitClass.getAPIService();
        Logger.logD(TAG, "URL: "+RetrofitConstant.BASE_URL+RetrofitConstant.OTP_VALIDATION_URL +" Param :"+"otp:"+otpDigit+"userDetails:"+userData);
        apiInterface.otpValidation(otpDigit, userString).enqueue(new Callback<MobileVerificationResponseModel>() {
            @Override
            public void onResponse(Call<MobileVerificationResponseModel> call, Response<MobileVerificationResponseModel> response) {
                Logger.logD(TAG, "URL "+ RetrofitConstant.BASE_URL+RetrofitConstant.OTP_VALIDATION_URL +" Response :"+response.body());
                if (response.body() != null) {
                    MobileVerificationResponseModel model = response.body();
                    if(model.getStatus().equals(RetrofitConstant.STATUS_TRUE)) {
                        model.setmAction(new Action(Action.VERIFY_OTP));
                        data.setValue(model);
                        sharedPref.writeString(Constants.USER_DETAILS, userString);
                        sharedPref.writeBoolean(Constants.USER_LOGIN, true);
                    }else {
                        model.setmAction(new Action(Action.STATUS_FALSE));
                        data.setValue(model);
                    }
                } else {
                    MobileVerificationResponseModel model = new MobileVerificationResponseModel(2, "Some Thing Went Wrong");
                    model.setmAction(new Action(Action.VERIFY_OTP));
                    data.setValue(model);
                }
                showProgresBar.setValue(false);

            }

            @Override
            public void onFailure(Call<MobileVerificationResponseModel> call, Throwable t) {
                Logger.logD(TAG, "URL "+ RetrofitConstant.BASE_URL+RetrofitConstant.OTP_VALIDATION_URL +" Response :"+t.getMessage());
                MobileVerificationResponseModel model = new MobileVerificationResponseModel(2, t.getMessage());
                model.setmAction(new Action(Action.STATUS_FALSE));
                data.setValue(model);
                showProgresBar.setValue(false);

            }
        });

    }

    private void getResendOTP(String mobileNo) {


        ApiInterface apiInterface = RetrofitClass.getAPIService();
        Logger.logD(TAG, "URL: "+RetrofitConstant.BASE_URL+RetrofitConstant.MOBILE_VALIDATION_URL +" Param :"+"mobile_number:"+mobileNo);
        apiInterface.mobileValidation(mobileNo).enqueue(new Callback<MobileVerificationResponseModel>() {
            @Override
            public void onResponse(Call<MobileVerificationResponseModel> call, Response<MobileVerificationResponseModel> response) {
                Logger.logD(TAG, "URL "+ RetrofitConstant.BASE_URL+RetrofitConstant.MOBILE_VALIDATION_URL +" Response :"+response.body());

                if (response.body() != null) {
                    MobileVerificationResponseModel model = response.body();
                    model.setmAction(new Action(Action.RESEND_OTP));
                    data.setValue(model);

                } else {
                    MobileVerificationResponseModel model = new MobileVerificationResponseModel(2, "Some Thing Went Wrong");
                    model.setmAction(new Action(Action.RESEND_OTP));
                    data.setValue(model);
                }
                showProgresBar.setValue(false);

            }

            @Override
            public void onFailure(Call<MobileVerificationResponseModel> call, Throwable t) {
                Logger.logD(TAG, "URL "+ RetrofitConstant.BASE_URL+RetrofitConstant.MOBILE_VALIDATION_URL +" Response :"+t.getMessage());
                MobileVerificationResponseModel model = new MobileVerificationResponseModel(2, t.getMessage());
                model.setmAction(new Action(Action.RESEND_OTP));
                data.setValue(model);
                showProgresBar.setValue(false);

            }
        });


    }
}