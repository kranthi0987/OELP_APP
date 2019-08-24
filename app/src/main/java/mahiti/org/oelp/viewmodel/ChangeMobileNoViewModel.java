package mahiti.org.oelp.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import mahiti.org.oelp.R;
import mahiti.org.oelp.models.MobileVerificationResponseModel;
import mahiti.org.oelp.services.ApiInterface;
import mahiti.org.oelp.services.RetrofitClass;
import mahiti.org.oelp.services.RetrofitConstant;
import mahiti.org.oelp.utils.Action;
import mahiti.org.oelp.utils.AppUtils;
import mahiti.org.oelp.utils.CheckNetwork;
import mahiti.org.oelp.utils.Constants;
import mahiti.org.oelp.utils.Logger;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by RAJ ARYAN on 26/07/19.
 */
public class ChangeMobileNoViewModel extends AndroidViewModel {

    private final Context context;
    public MutableLiveData<String> oldMobileNo = new MutableLiveData<>();
    public MutableLiveData<String> newMobileNo = new MutableLiveData<>();
    public MutableLiveData<String> confirmNewMobileNo = new MutableLiveData<>();

    public MutableLiveData<String> oldMobileNoError = new MutableLiveData<>();
    public MutableLiveData<String> newMobileNoError = new MutableLiveData<>();
    public MutableLiveData<String> confirmNewMobileNoError = new MutableLiveData<>();

    public MutableLiveData<Boolean> showProgresBar = new MutableLiveData<>();
    private MutableLiveData<Action> mAction = new MutableLiveData<>();

    public MutableLiveData<String> status = new MutableLiveData<>();
    MutableLiveData<MobileVerificationResponseModel> data = new MutableLiveData<>();
    private static final String TAG = ChangeMobileNoViewModel.class.getSimpleName();


    public ChangeMobileNoViewModel(@NonNull Application application) {
        super(application);
        context = application;
        showProgresBar.setValue(false);
    }

    public void onSubmitClick() {
        if (CheckNetwork.checkNet(context)) {
            validateAndProceed();
        }else {
            Toast.makeText(context, context.getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
        }
    }

    private void validateAndProceed() {
        if (validation().getValue().equalsIgnoreCase(Constants.STATUS_TRUE)) {
            showProgresBar.setValue(true);
            changeMobileNumber(oldMobileNo.getValue(), newMobileNo.getValue());
        }
    }

    public void changeMobileNumber(String currentMobileNo, String newMobileNo){

        MutableLiveData<MobileVerificationResponseModel> model = new MutableLiveData<MobileVerificationResponseModel>();

        ApiInterface apiInterface = RetrofitClass.getAPIService();
        Logger.logD(TAG, "URL "+ RetrofitConstant.BASE_URL+RetrofitConstant.CHANGE_MOBILE_NO_URL+" Param :"+"current_mobile_number"+currentMobileNo+"new_mobile_number"+newMobileNo);
        apiInterface.changeMobileNumber(currentMobileNo, newMobileNo).enqueue(new Callback<MobileVerificationResponseModel>() {
            @Override
            public void onResponse(Call<MobileVerificationResponseModel> call, Response<MobileVerificationResponseModel> response) {
                Logger.logD(TAG, "URL "+ RetrofitConstant.BASE_URL+RetrofitConstant.CHANGE_MOBILE_NO_URL+" Response :"+response.body());


                if (response.body()!=null){
                    data.setValue(response.body());
                    data.getValue().setmAction(new Action(Action.STATUS_TRUE));
                }else {
                    data.setValue(new MobileVerificationResponseModel(2, "Some Thing Went Wrong"));
                    data.getValue().setmAction(new Action(Action.STATUS_FALSE));

                }
                showProgresBar.setValue(false);
            }

            @Override
            public void onFailure(Call<MobileVerificationResponseModel> call, Throwable t) {
                Logger.logD(TAG, "URL "+ RetrofitConstant.BASE_URL+RetrofitConstant.CHANGE_MOBILE_NO_URL+" Response :"+t.getMessage());
                showProgresBar.setValue(false);
                data.setValue(new MobileVerificationResponseModel(2, t.getMessage()));
                data.getValue().setmAction(new Action(Action.STATUS_FALSE));
            }
        });

    }

    private MutableLiveData<String> validation() {
        status.setValue(Constants.STATUS_TRUE);

        if (AppUtils.checkMobileValidation(context, oldMobileNo.getValue()).equals(Constants.STATUS_TRUE)) {
            oldMobileNoError.setValue(null);
        } else {
            oldMobileNoError.setValue(AppUtils.checkMobileValidation(context, oldMobileNo.getValue()));
            status.setValue(oldMobileNoError.getValue());
        }

        if (AppUtils.checkMobileValidation(context, newMobileNo.getValue()).equals(Constants.STATUS_TRUE)) {
            newMobileNoError.setValue(null);
        } else {
            newMobileNoError.setValue(AppUtils.checkMobileValidation(context, newMobileNo.getValue()));
            status.setValue(newMobileNoError.getValue());
        }

        if (AppUtils.checkMobileValidation(context, confirmNewMobileNo.getValue()).equals(Constants.STATUS_TRUE)) {
            confirmNewMobileNoError.setValue(null);
        } else {
            confirmNewMobileNoError.setValue(AppUtils.checkMobileValidation(context, confirmNewMobileNo.getValue()));
            status.setValue(confirmNewMobileNoError.getValue());
        }

        if (newMobileNo.getValue().equals(confirmNewMobileNo.getValue())) {
            confirmNewMobileNoError.setValue(null);
        } else {
            confirmNewMobileNoError.setValue(context.getResources().getString(R.string.please_enter_same_name));
            status.setValue(confirmNewMobileNoError.getValue());
        }
        return status;
    }

    public LiveData<Action> getAction() {
        return mAction;
    }

    public LiveData<MobileVerificationResponseModel> getData() {
        showProgresBar.setValue(false);
        return data;
    }


    public void onBackPressed() {
        mAction.setValue(new Action(Action.ON_BACK_PRESSED));
    }

}
