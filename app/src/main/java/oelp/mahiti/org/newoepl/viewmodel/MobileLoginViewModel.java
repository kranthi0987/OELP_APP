package oelp.mahiti.org.newoepl.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import org.json.JSONObject;

import oelp.mahiti.org.newoepl.R;
import oelp.mahiti.org.newoepl.models.MobileVerificationResponseModel;
import oelp.mahiti.org.newoepl.models.UserDetailsModel;
import oelp.mahiti.org.newoepl.services.ApiInterface;
import oelp.mahiti.org.newoepl.services.RetrofitClass;
import oelp.mahiti.org.newoepl.services.RetrofitConstant;
import oelp.mahiti.org.newoepl.utils.Action;
import oelp.mahiti.org.newoepl.utils.AppUtils;
import oelp.mahiti.org.newoepl.utils.CheckNetwork;
import oelp.mahiti.org.newoepl.utils.Constants;
import oelp.mahiti.org.newoepl.utils.Logger;
import oelp.mahiti.org.newoepl.utils.MySharedPref;
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
        }else {
            Toast.makeText(context, context.getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
        }

    }

    private void validateAndProceed() {
        if (phoneNo.getValue()!=null){
            String validationStatus = AppUtils.checkMobileValidation(context, phoneNo.getValue());
            if (validationStatus.equalsIgnoreCase(Constants.STATUS_TRUE)){
                showProgresBar.setValue(true);
                getMobileVerified(phoneNo.getValue());
            }else
                status.setValue(validationStatus);
        }else {
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
        Logger.logD(TAG, "URL: "+RetrofitConstant.BASE_URL+RetrofitConstant.MOBILE_VALIDATION_URL +" Param :"+"mobile_number:"+mobileNo);
        apiInterface.mobileValidation(mobileNo).enqueue(new Callback<MobileVerificationResponseModel>() {
            @Override
            public void onResponse(Call<MobileVerificationResponseModel> call, Response<MobileVerificationResponseModel> response) {
                Logger.logD(TAG, "URL "+ RetrofitConstant.BASE_URL+RetrofitConstant.MOBILE_VALIDATION_URL +" Response :"+response.body());

                if (response.body() != null) {
                    MobileVerificationResponseModel model = response.body();
                    model.setmAction(new Action(Action.STATUS_TRUE));
                    sharedPref.writeString(Constants.MOBILE_NO, mobileNo);
                    if (!model.getUserDetails().getUserid().equals(Constants.USER_INVALID)){
                        saveUserDataToPref(model.getUserDetails());
                        saveUserIDAndUserType(model.getUserDetails().getUserid(), model.getUserDetails().getIsTrainer());
                    }
                    data.setValue(model);
                    showProgresBar.setValue(false);
                } else{
                    MobileVerificationResponseModel model = new MobileVerificationResponseModel(2,
                            context.getResources().getString(R.string.SOMETHING_WRONG));
                    model.setmAction(new Action(Action.STATUS_FALSE));
                    data.setValue(model);
                    showProgresBar.setValue(false);
                }
            }

            @Override
            public void onFailure(Call<MobileVerificationResponseModel> call, Throwable t) {
                Logger.logD(TAG, "URL "+ RetrofitConstant.BASE_URL+RetrofitConstant.MOBILE_VALIDATION_URL +" Response :"+t.getMessage());

                MobileVerificationResponseModel model = new MobileVerificationResponseModel(2, t.getMessage());
                model.setmAction(new Action(Action.STATUS_FALSE));
                data.setValue(model);
                showProgresBar.setValue(false);
            }
        });


    }

    private void saveUserIDAndUserType(String userid, Integer isTrainer) {
       sharedPref.writeString(Constants.USER_ID, userid);
       sharedPref.writeInt(Constants.USER_TYPE, isTrainer);
    }

    private void saveUserDataToPref(UserDetailsModel userDetails) {
        if (userDetails!=null){
            try{
                JSONObject obj = new JSONObject();
                obj.put("name", userDetails.getName());
                obj.put("school", userDetails.getSchool());
                obj.put("mobile_number", userDetails.getMobile_number());
                obj.put("blockIds", userDetails.getBlockIds());
                sharedPref.writeString(Constants.USER_DETAILS,obj.toString());
            }catch (Exception ex){
                Logger.logE(TAG, ex.getMessage(), ex);
            }
        }
    }
}
