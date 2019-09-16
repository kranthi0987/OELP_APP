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
import mahiti.org.oelp.database.DatabaseHandlerClass;
import mahiti.org.oelp.models.LocationContent;
import mahiti.org.oelp.models.LocationModel;
import mahiti.org.oelp.models.MobileVerificationResponseModel;
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


    private MutableLiveData<Action> mAction = new MutableLiveData<>();
    public MutableLiveData<Boolean> showProgresBar = new MutableLiveData<>();

    public MutableLiveData<String> errorMessage = new MutableLiveData<>();
    public MutableLiveData<Long> insertLong = new MutableLiveData<>();
    private DatabaseHandlerClass databaseHandlerClass;
    public MutableLiveData<MobileVerificationResponseModel> data = new MutableLiveData<>();
    private static final String TAG = TeacherRegistrationViewModel.class.getSimpleName();



    public TeacherRegistrationViewModel(@NonNull Application application) {
        super(application);
        context = application;
        showProgresBar.setValue(true);
        insertLong.setValue(null);
        databaseHandlerClass = new DatabaseHandlerClass(context);
        districtClickable.setValue(false);
        blockClickable.setValue(false);
        stateId.setValue(0);
        districtId.setValue(0);
        blockId.setValue(0);
        state.setValue(context.getResources().getString(R.string.please_select_state_start));
        district.setValue(context.getResources().getString(R.string.please_select_district));
        block.setValue(context.getResources().getString(R.string.please_select_block));
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
        if (locationModel.getStatus().equals(RetrofitConstant.STATUS_TRUE)){
            insertLong.setValue(databaseHandlerClass.insertLocationDataToDB(locationModel));
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
        stateArray = databaseHandlerClass.getSpinnerList(1, 2);
        return stateArray;
    }

    public LiveData<List<LocationContent>> getDistrictSpinnerData(int parentId, int level){
        MutableLiveData<List<LocationContent>> stateArray = new MutableLiveData<>();
        stateArray = databaseHandlerClass.getSpinnerList(parentId, level);
        return stateArray;
    }

    public LiveData<List<LocationContent>> getBlockSpinnerData(int parentId, int level){
        MutableLiveData<List<LocationContent>> stateArray = new MutableLiveData<>();
        stateArray = databaseHandlerClass.getSpinnerList(parentId, level);
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
                    sharedPref.writeString(Constants.USER_DETAILS, userDetails);
                    saveUserIDAndUserTypeAndUserName(model.getUserid(), Constants.USER_TEACHER);
                    showProgresBar.setValue(false);

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

//        if (AppUtils.textEmpty(phoneNo.getValue())){
//            phoneNo = null;
//        }else {
//            status = false;
//            phoneNo.setValue(context.getResources().getString(R.string.please_enter_phone));
//        }

//        if (!AppUtils.textEmpty(school.getValue())){
//            errorSchool = null;
//        }else {
//            status = false;
//            errorSchool.setValue(context.getResources().getString(R.string.please_enter_school));
//        }

        if (!AppUtils.textEmpty(state.getValue()) && !state.getValue().equalsIgnoreCase(context.getString(R.string.please_select_state_start))){
            errorState = null;
        }else {
            status = false;
            errorState.setValue(context.getResources().getString(R.string.please_select_state));
            return status;
        }

//        if (!AppUtils.textEmpty(district.getValue())){
//            errorDistrict = null;
//        }else {
//            status = false;
//            errorDistrict.setValue(context.getResources().getString(R.string.please_select_district));
//        }

//        if (!AppUtils.textEmpty(block.getValue())){
//            errorBlock = null;
//        }else {
//            status = false;
//            errorBlock.setValue(context.getResources().getString(R.string.please_select_block));
//        }

//        if (!AppUtils.textEmpty(village.getValue())){
//            errorVillage = null;
//        }else {
//            status = false;
//            errorVillage.setValue(context.getResources().getString(R.string.please_select_village));
//        }

        return status;
    }


    /*@InverseBindingAdapter(attribute = "state", event = "selectionAttrChanged")
    public static String getSelectedStateValue(AdapterView view) {
        return (String) view.getSelectedItem();
    }

    @InverseBindingAdapter(attribute = "district", event = "selectionAttrChanged")
    public static String getSelectedDistrictValue(AdapterView view) {
        return (String) view.getSelectedItem();
    }

    @InverseBindingAdapter(attribute = "block", event = "selectionAttrChanged")
    public static String getSelectedBlockValue(AdapterView view) {
        return (String) view.getSelectedItem();
    }

    @InverseBindingAdapter(attribute = "village", event = "selectionAttrChanged")
    public static String getSelectedVillageValue(AdapterView view) {
        return (String) view.getSelectedItem();
    }

    @BindingAdapter(value = {"state", "selectionAttrChanged", "adapter"}, requireAll = false)
    public static void setStateAdapter(AdapterView view, String newSelection, final InverseBindingListener bindingListener, ArrayAdapter adapter) {
        view.setAdapter(adapter);
        view.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bindingListener.onChange();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Nothing
            }
        });
        if (newSelection != null) {
            int pos = ((ArrayAdapter) view.getAdapter()).getPosition(newSelection);
            view.setSelection(pos);
        }
    }

    @BindingAdapter(value = {"district", "selectionAttrChanged", "adapter"}, requireAll = false)
    public static void setDistrictAdapter(AdapterView view, String newSelection, final InverseBindingListener bindingListener, ArrayAdapter adapter) {
        view.setAdapter(adapter);
        view.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bindingListener.onChange();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Nothing
            }
        });
        if (newSelection != null) {
            int pos = ((ArrayAdapter) view.getAdapter()).getPosition(newSelection);
            view.setSelection(pos);
        }
    }

    @BindingAdapter(value = {"block", "selectionAttrChanged", "adapter"}, requireAll = false)
    public static void setBlockAdapter(AdapterView view, String newSelection, final InverseBindingListener bindingListener, ArrayAdapter adapter) {
        view.setAdapter(adapter);
        view.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bindingListener.onChange();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Nothing
            }
        });
        if (newSelection != null) {
            int pos = ((ArrayAdapter) view.getAdapter()).getPosition(newSelection);
            view.setSelection(pos);
        }
    }


    @BindingAdapter(value = {"village", "selectionAttrChanged", "adapter"}, requireAll = false)
    public static void setVillageAdapter(AdapterView view, String newSelection, final InverseBindingListener bindingListener, ArrayAdapter adapter) {
        view.setAdapter(adapter);
        view.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bindingListener.onChange();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Nothing
            }
        });
        if (newSelection != null) {
            int pos = ((ArrayAdapter) view.getAdapter()).getPosition(newSelection);
            view.setSelection(pos);
        }
    }
*/
}
