package mahiti.org.oelp.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.content.Context;
import androidx.annotation.NonNull;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.List;

import mahiti.org.oelp.R;
import mahiti.org.oelp.database.DAOs.LocationDao;
import mahiti.org.oelp.database.DatabaseHandlerClass;
import mahiti.org.oelp.models.LocationContent;
import mahiti.org.oelp.models.LocationModel;
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
 * Created by RAJ ARYAN on 18/07/19.
 */
public class TeacherRegistrationViewModel extends AndroidViewModel{

    private final MySharedPref sharedPref;
    Context context;

    public MutableLiveData<String> errorName = new MutableLiveData<>();
    public MutableLiveData<String> errorPhone = new MutableLiveData<>();
    public MutableLiveData<String> errorSchool = new MutableLiveData<>();
    public MutableLiveData<String> errorState = new MutableLiveData<>();
    public MutableLiveData<String> errorDistrict = new MutableLiveData<>();
    public MutableLiveData<String> errorBlock = new MutableLiveData<>();
    public MutableLiveData<String> errorVillage = new MutableLiveData<>();

    public MutableLiveData<String> name = new MutableLiveData<>();
    public MutableLiveData<String> phoneNo = new MutableLiveData<>();
    public MutableLiveData<String> school = new MutableLiveData<>();
    public MutableLiveData<String> state = new MutableLiveData<>();
    public MutableLiveData<String> village = new MutableLiveData<>();
    public MutableLiveData<String> block = new MutableLiveData<>();
    public MutableLiveData<String> district = new MutableLiveData<>();
    public MutableLiveData<Integer> blockId = new MutableLiveData<>();
    public MutableLiveData<Integer> stateId = new MutableLiveData<>();
    public MutableLiveData<Integer> districtId = new MutableLiveData<>();
    public MutableLiveData<Boolean> districtClickable = new MutableLiveData<>();
    public MutableLiveData<Boolean> blockClickable = new MutableLiveData<>();
    public MutableLiveData<UserDetailsModel> userDetailsModelData = new MutableLiveData<>();

    public MutableLiveData<Integer> activityType = new MutableLiveData<>();



    private MutableLiveData<Action> mAction = new MutableLiveData<>();
    public MutableLiveData<Boolean> showProgresBar = new MutableLiveData<>();

    public MutableLiveData<String> errorMessage = new MutableLiveData<>();
    public MutableLiveData<Long> insertLong = new MutableLiveData<>();
//    private DatabaseHandlerClass databaseHandlerClass;
    private LocationDao locationDao;
    public MutableLiveData<MobileVerificationResponseModel> data = new MutableLiveData<>();
    private static final String TAG = TeacherRegistrationViewModel.class.getSimpleName();



    public TeacherRegistrationViewModel(@NonNull Application application) {
        super(application);
        context = application;
        showProgresBar.setValue(true);
        insertLong.setValue(null);
        locationDao = new LocationDao(context);
        districtClickable.setValue(false);
        blockClickable.setValue(false);
        stateId.setValue(0);
        districtId.setValue(0);
        blockId.setValue(0);
        activityType.setValue(0);

        sharedPref = new MySharedPref(context);
        if (CheckNetwork.checkNet(context)) {
            callApiForLocation();
        }else {
            Toast.makeText(context, context.getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
        }
    }

    private void callApiForCatalogData() {

    }

    private void callApiForLocation() {
        ApiInterface apiInterface = RetrofitClass.getAPIService();
        apiInterface.getLocationData().enqueue(new Callback<LocationModel>() {
            @Override
            public void onResponse(Call<LocationModel> call, Response<LocationModel> response) {
                Logger.logD(TAG, "URL "+ RetrofitConstant.BASE_URL+RetrofitConstant.LOCATION_LIST_URL+" Response :"+response.body());
                LocationModel locationModel = response.body();
                if (locationModel!=null){
                   checkDataAndProceed(locationModel);
                }else {
                    errorMessage.setValue("Something went wrong");
                    showProgresBar.setValue(false);

                }
            }

            @Override
            public void onFailure(Call<LocationModel> call, Throwable t) {
                Logger.logD(TAG, "URL "+ RetrofitConstant.BASE_URL+RetrofitConstant.LOCATION_LIST_URL+" Response :"+t.getMessage());
                errorMessage.setValue(t.getMessage());
                showProgresBar.setValue(false);

            }
        });
    }

    private void checkDataAndProceed(LocationModel locationModel) {
        if (locationModel.getStatus().equals(RetrofitConstant.STATUS_TRUE) && !locationModel.getLocationContent().isEmpty()){
            insertLong.setValue(locationDao.insertLocationDataToDB(locationModel));
            showProgresBar.setValue(false);

        }else {
            errorMessage.setValue("Something went wrong");
            showProgresBar.setValue(false);

        }
    }

    public MutableLiveData<Long> getInsertLong(){
        return insertLong;
    }




    public LiveData<Action> getAction() {
        return mAction;
    }


    public LiveData<List<LocationContent>> getStateSpinnerData(){
        MutableLiveData<List<LocationContent>> stateArray = new MutableLiveData<>();
        stateArray = locationDao.getSpinnerList(1, 2);
        return stateArray;
    }

    public LiveData<List<LocationContent>> getDistrictSpinnerData(int parentId, int level){
        MutableLiveData<List<LocationContent>> stateArray = new MutableLiveData<>();
        stateArray = locationDao.getSpinnerList(parentId, level);
        return stateArray;
    }

    public LiveData<List<LocationContent>> getBlockSpinnerData(int parentId, int level){
        MutableLiveData<List<LocationContent>> stateArray = new MutableLiveData<>();
        stateArray = locationDao.getSpinnerList(parentId, level);
        return stateArray;
    }


    public void onSubmitClick(){
        showProgresBar.setValue(true);
        if(validation()){
            prepareJsonAndCallApi();
        }else {
            showProgresBar.setValue(false);
            mAction.setValue(new Action(Action.STATUS_FALSE));
        }
    }

    private void prepareJsonAndCallApi() {
        String userDetails ="";
        if (activityType.getValue()==1){
            UserDetailsModel details = new UserDetailsModel();
            details.setName(name.getValue());
            details.setMobile_number(phoneNo.getValue());
            details.setSchool(school.getValue());
            details.setStateName(state.getValue());
            details.setDistrictname(district.getValue());
            details.setBlockName(district.getValue());
            details.setVillageName(district.getValue());
            userDetailsModelData.setValue(details);
        }
        try{
            JSONObject obj = new JSONObject();
            obj.put("name", name.getValue());
            if (school.getValue()==null)
                obj.put("school", "");
            else
                obj.put("school", school.getValue());
            obj.put("mobile_number", phoneNo.getValue());
            obj.put("stateId", stateId.getValue());
            obj.put("districtId", districtId.getValue());
            obj.put("blockId", blockId.getValue());
            userDetails = obj.toString();

        }catch (Exception exp){
            Logger.logE(TAG, exp.getMessage(), exp);
        }

        if (CheckNetwork.checkNet(context)) {
             callApiForRegistration(userDetails);
        }else {
            Toast.makeText(context, context.getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
        }



    }

    public UserDetailsModel getUserDetailsData(){
        return userDetailsModelData.getValue();
    }

    private void callApiForRegistration(String userDetails) {
        ApiInterface apiInterface = RetrofitClass.getAPIService();
        Logger.logD(TAG, "URL: "+RetrofitConstant.BASE_URL+RetrofitConstant.USER_REGISTRATION_URL +" Param : userDetails:"+userDetails);
        apiInterface.userRegistration(userDetails).enqueue(new Callback<MobileVerificationResponseModel>() {
            @Override
            public void onResponse(Call<MobileVerificationResponseModel> call, Response<MobileVerificationResponseModel> response) {
                Logger.logD(TAG, "URL "+ RetrofitConstant.BASE_URL+RetrofitConstant.USER_REGISTRATION_URL +" Response :"+response.body());
                if (response.body()!=null){
                    MobileVerificationResponseModel model = response.body();
                    model.setmAction(new Action(Action.STATUS_TRUE));
                    data.setValue(model);
                    showProgresBar.setValue(false);

                    if (activityType.getValue()==0) {
                        sharedPref.writeString(Constants.MOBILE_NO, userDetails);
                        saveUserIDAndUserTypeAndUserName(model.getUseridreg(), Constants.USER_TEACHER);
                    }

                }else {
                    MobileVerificationResponseModel model = new MobileVerificationResponseModel(2, context.getResources().getString(R.string.SOMETHING_WRONG));
                    model.setmAction(new Action(Action.STATUS_FALSE));
                    data.setValue(model);
                    showProgresBar.setValue(false);
                }


            }

            @Override
            public void onFailure(Call<MobileVerificationResponseModel> call, Throwable t) {
                Logger.logD(TAG, "URL "+ RetrofitConstant.BASE_URL+RetrofitConstant.USER_REGISTRATION_URL +" Response :"+t.getMessage());
                MobileVerificationResponseModel model = new MobileVerificationResponseModel(2, t.getMessage());
                model.setmAction(new Action(Action.STATUS_FALSE));
                data.setValue(model);
                showProgresBar.setValue(false);

            }
        });

    }

    private void saveUserIDAndUserTypeAndUserName(String userid, Integer isTrainer) {
        sharedPref.writeString(Constants.USER_ID, userid);
        sharedPref.writeInt(Constants.USER_TYPE, isTrainer);
    }
    public LiveData<MobileVerificationResponseModel> getData() {
        if (data!=null)
            showProgresBar.setValue(false);
        return data;
    }

    private boolean validation() {
        boolean status = true;
        if (null!=name.getValue() && !AppUtils.textEmpty(name.getValue().trim())){
            errorName.setValue(null);
        }else {
            status = false;
            errorName.setValue(context.getResources().getString(R.string.please_enter_name));
            return status;
        }

        if (!AppUtils.textEmpty(state.getValue()) && !state.getValue().equalsIgnoreCase(context.getString(R.string.please_select_state_start))){
            errorState = null;
        }else {
            status = false;
            errorState.setValue(context.getResources().getString(R.string.please_select_state));
            return status;
        }
        if (activityType.getValue()!=null && activityType.getValue()==1) {

            if (!AppUtils.textEmpty(district.getValue())) {
                errorDistrict = null;
            } else {
                status = false;
                errorDistrict.setValue(context.getResources().getString(R.string.please_select_district));
            }

            if (!AppUtils.textEmpty(block.getValue())) {
                errorBlock = null;
            } else {
                status = false;
                errorBlock.setValue(context.getResources().getString(R.string.please_select_block));
            }

            if (!AppUtils.textEmpty(village.getValue())) {
                errorVillage = null;
            } else {
                status = false;
                errorVillage.setValue(context.getResources().getString(R.string.please_select_village));
            }
        }

        return status;
    }


    public void setActivityType(int activityType) {
        this.activityType.setValue(activityType);
        if (this.activityType!=null && this.activityType.getValue()==0){
            state.setValue(context.getResources().getString(R.string.please_select_state_start));
            district.setValue(context.getResources().getString(R.string.please_select_district));
            block.setValue(context.getResources().getString(R.string.please_select_block));
        }
    }

}
