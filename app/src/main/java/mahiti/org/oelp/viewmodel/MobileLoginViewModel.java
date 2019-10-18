package mahiti.org.oelp.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import android.content.Context;

import androidx.annotation.NonNull;

import android.widget.Toast;

import org.json.JSONObject;

import mahiti.org.oelp.R;
import mahiti.org.oelp.models.MobileVerificationResponseModel;
import mahiti.org.oelp.models.UserDetailsModel;
import mahiti.org.oelp.services.ApiInterface;
import mahiti.org.oelp.services.RetrofitClass;
import mahiti.org.oelp.services.RetrofitConstant;
import mahiti.org.oelp.utils.Action;
import mahiti.org.oelp.utils.AppUtils;
import mahiti.org.oelp.utils.CheckNetwork;
import mahiti.org.oelp.utils.Constants;
import mahiti.org.oelp.utils.Logger;
import mahiti.org.oelp.utils.MySharedPref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by RAJ ARYAN on 22/07/19.
 */
public class MobileLoginViewModel extends AndroidViewModel {

    private final MySharedPref sharedPref;
    public MutableLiveData<String> phoneNo = new MutableLiveData<>();
    public MutableLiveData<Boolean> showProgresBar = new MutableLiveData<>();
    public MutableLiveData<String> status = new MutableLiveData<>();
    private RetrofitClass retroClass = RetrofitClass.getRetrofitClass();
    public MutableLiveData<MobileVerificationResponseModel> data = new MutableLiveData<>();
    Context context;
    private static final String TAG = MobileLoginViewModel.class.getSimpleName();


    public MobileLoginViewModel(@NonNull Application application) {
        super(application);
        context = application;
        status.setValue("");
        showProgresBar.setValue(false);
        sharedPref = new MySharedPref(context);
    }


    public void onSubmitClick() {
        if (CheckNetwork.checkNet(context)) {
            validateAndProceed();
        } else {
            Toast.makeText(context, context.getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
        }

    }

    private void validateAndProceed() {
        if (phoneNo.getValue() != null) {
            String validationStatus = AppUtils.checkMobileValidation(context, phoneNo.getValue());
            if (validationStatus.equalsIgnoreCase(Constants.STATUS_TRUE)) {
                showProgresBar.setValue(true);
                getMobileVerified(phoneNo.getValue());
            } else
                status.setValue(validationStatus);
        } else {
            status.setValue(context.getResources().getString(R.string.please_enter_mobile_number));
        }
    }


    public LiveData<MobileVerificationResponseModel> getData() {
        if (data.getValue() != null) {
            showProgresBar.setValue(false);
        }
        return data;
    }


    private void getMobileVerified(String mobileNo) {

        MutableLiveData<MobileVerificationResponseModel> model = new MutableLiveData<MobileVerificationResponseModel>();

        ApiInterface apiInterface = RetrofitClass.getAPIService();
        Logger.logD(TAG, "URL: " + RetrofitConstant.BASE_URL + RetrofitConstant.MOBILE_VALIDATION_URL + " Param :" + "mobile_number:" + mobileNo);
        apiInterface.mobileValidation(mobileNo).enqueue(new Callback<MobileVerificationResponseModel>() {
            public String groupId = "";

            @Override
            public void onResponse(Call<MobileVerificationResponseModel> call, Response<MobileVerificationResponseModel> response) {
                Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.MOBILE_VALIDATION_URL + " Response :" + response.body());

                if (response.body() != null) {
                    if (response.body().getStatus()==2) {
                        MobileVerificationResponseModel model = response.body();
                        model.setmAction(new Action(Action.STATUS_TRUE));
                        sharedPref.writeString(Constants.MOBILE_NO_New, mobileNo);

                        if (!model.getUserDetails().getUserid().equals(Constants.USER_INVALID)) {

                            saveUserDataToPref(model.getUserDetails());
                            if (!model.getUserDetails().getUserGroup().isEmpty())
                                saveUserIDAndUserType(model.getUserDetails().getUserGroup().toString());
                        }
                        data.setValue(model);
                    }else
                        Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    showProgresBar.setValue(false);

                } else {
                    MobileVerificationResponseModel model = new MobileVerificationResponseModel(2,
                            context.getResources().getString(R.string.SOMETHING_WRONG));
                    model.setmAction(new Action(Action.STATUS_FALSE));
                    data.setValue(model);
                    showProgresBar.setValue(false);
                }
            }

            @Override
            public void onFailure(Call<MobileVerificationResponseModel> call, Throwable t) {
                Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.MOBILE_VALIDATION_URL + " Response :" + t.getMessage());

                MobileVerificationResponseModel model = new MobileVerificationResponseModel(2, t.getMessage());
                model.setmAction(new Action(Action.STATUS_FALSE));
                data.setValue(model);
                showProgresBar.setValue(false);
            }
        });


    }

    private void saveUserIDAndUserType(String groupUUIDList) {
        sharedPref.writeString(Constants.GROUP_UUID_LIST, groupUUIDList);
    }

    private void saveUserDataToPref(UserDetailsModel userDetails) {
        if (userDetails != null) {
            try {
                JSONObject obj = new JSONObject();
                obj.put("name", userDetails.getName());
                obj.put("school", userDetails.getSchool());
                obj.put("mobile_number", userDetails.getMobile_number());
                obj.put("blockIds", userDetails.getBlockIds());
                sharedPref.writeString(Constants.USER_DETAILS, obj.toString());
                sharedPref.writeString(Constants.USER_NAME, userDetails.getName());
                sharedPref.writeString(Constants.USER_ID, userDetails.getUserid());
                sharedPref.writeInt(Constants.USER_TYPE, userDetails.getIsTrainer());


            } catch (Exception ex) {
                Logger.logE(TAG, ex.getMessage(), ex);
            }
        }
    }
}
