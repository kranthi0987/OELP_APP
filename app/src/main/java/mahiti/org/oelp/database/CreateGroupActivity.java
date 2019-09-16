package mahiti.org.oelp.database;

import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mahiti.org.oelp.R;
import mahiti.org.oelp.models.GroupModel;
import mahiti.org.oelp.models.MobileVerificationResponseModel;
import mahiti.org.oelp.models.UserDetailsModel;
import mahiti.org.oelp.services.ApiInterface;
import mahiti.org.oelp.services.RetrofitClass;
import mahiti.org.oelp.services.RetrofitConstant;
import mahiti.org.oelp.utils.AppUtils;
import mahiti.org.oelp.utils.CheckNetwork;
import mahiti.org.oelp.utils.Constants;
import mahiti.org.oelp.utils.Logger;
import mahiti.org.oelp.utils.MySharedPref;
import mahiti.org.oelp.views.adapters.AddTeacherToGroupAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CreateGroupActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = CreateGroupActivity.class.getSimpleName();
    private EditText etGroupName;
    private EditText etMobileNo;
    private Button btnCreate;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private Toolbar toolbar;
    private String phoneNo;
    private String groupName;
    private RelativeLayout llView;
    private String userMobileNo;
    private MySharedPref sharedPref;
    private List<UserDetailsModel> userDetailList = new ArrayList<>();
    private AddTeacherToGroupAdapter adapter;
    private String userUUID;
    private AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        sharedPref = new MySharedPref(this);
        userMobileNo = sharedPref.readString(Constants.MOBILE_NO, "");
        toolbar = findViewById(R.id.black_toolbar);
        etGroupName = findViewById(R.id.etGroupName);
        etMobileNo = findViewById(R.id.etMobileNo);
        btnCreate = findViewById(R.id.btnCreate);
        llView = findViewById(R.id.llView);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }

        btnCreate.setOnClickListener(this);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setFocusable(false);
        adapter = new AddTeacherToGroupAdapter();
        recyclerView.setAdapter(adapter);

        etMobileNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                phoneNo = editable.toString().trim();
                if (editable.toString().length() == 10) {
                    AppUtils.hideKeyboard(CreateGroupActivity.this);
                    if (validationMobile(phoneNo)) {
                        if (CheckNetwork.checkNet(CreateGroupActivity.this)) {
                            callApiForTeacher(phoneNo);
                        } else {
                            Toast.makeText(CreateGroupActivity.this, getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                        }
                        if (userDetailList.size() > 0) {
                            llView.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });

    }

    private void callApiForTeacher(String mobileNo) {
        progressBar.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = RetrofitClass.getAPIService();
        Logger.logD(TAG, "URL: " + RetrofitConstant.BASE_URL + RetrofitConstant.MOBILE_VALIDATION_URL + " Param :" + "mobile_number:" + mobileNo);
        apiInterface.mobileValidation(mobileNo).enqueue(new Callback<MobileVerificationResponseModel>() {
            @Override
            public void onResponse(Call<MobileVerificationResponseModel> call, Response<MobileVerificationResponseModel> response) {
                Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.MOBILE_VALIDATION_URL + " Response :" + response.body());
                if (response.body() != null) {
                    UserDetailsModel userDetail = response.body().getUserDetails();
                    if (!userDetail.getUserid().isEmpty()) {
                        if (userDetail.getUserGroup().isEmpty()) {
                            userDetailList.add(response.body().getUserDetails());
                            etMobileNo.getText().clear();
                            adapter.setList(userDetailList);
                        } else {
                            showAlertDialog(userDetail);
                        }
                    } else {
                        Toast.makeText(CreateGroupActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.GONE);
                } else {
                    Toast.makeText(CreateGroupActivity.this, getResources().getString(R.string.SOMETHING_WRONG), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
                if (userDetailList.size() > 0) {
                    llView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<MobileVerificationResponseModel> call, Throwable t) {
                Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.MOBILE_VALIDATION_URL + " Response :" + t.getMessage());
                Toast.makeText(CreateGroupActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void showAlertDialog(UserDetailsModel userDetail) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
        builder.setMessage(userDetail.getName()+" is already added to some other group");
        builder.setNegativeButton(R.string.ok, (dialog, id) -> dialog.dismiss());
        dialog = builder.create();
        dialog.show();
    }

    private boolean validationMobile(String phoneNo) {
        String message = AppUtils.checkMobileValidation(this, phoneNo);
        if (!message.equalsIgnoreCase(Constants.STATUS_TRUE)) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            return false;
        } else if (userMobileNo.equals(phoneNo)) {
            Toast.makeText(this, "Please enter teacher numbers", Toast.LENGTH_SHORT).show();
            return false;
        } else if (checkFromList(phoneNo)) {
            Toast.makeText(this, "This number is already added", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean checkFromList(String phoneNo) {
        for (UserDetailsModel uerDetail : userDetailList) {
            if (uerDetail.getMobile_number().equals(phoneNo)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        if (CheckNetwork.checkNet(this)) {
            progressBar.setVisibility(View.VISIBLE);
            phoneNo = etMobileNo.getText().toString().trim();
            groupName = etGroupName.getText().toString().trim();
            validateAndProceed();
        } else {
            Toast.makeText(this, getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
        }
    }

    private void validateAndProceed() {
        List<UserDetailsModel> lsitModel = new ArrayList<>();
        lsitModel.clear();
        lsitModel = AddTeacherToGroupAdapter.getUserDetailsList();
        if (groupName.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Please enter group name", Toast.LENGTH_SHORT).show();
        } else if (lsitModel.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Please select teacher", Toast.LENGTH_SHORT).show();
        } else {
            craeteUUIDJSONandProceed(lsitModel);
        }
    }

    private void craeteUUIDJSONandProceed(List<UserDetailsModel> lsitModel) {
        userUUID = sharedPref.readString(Constants.USER_ID, "");
        String groupCreationKey = AppUtils.getUUID();
        String teacherJson = createJson(lsitModel);
        callApiForCreateGroup(userUUID, groupName, groupCreationKey, teacherJson);
    }

    private void callApiForCreateGroup(String userUUID, String groupName, String groupCreationKey, String teacherJson) {
        ApiInterface apiInterface = RetrofitClass.getAPIService();
        Logger.logD(TAG, "URL: " + RetrofitConstant.BASE_URL + RetrofitConstant.CREATE_GROUP_URL + " Param :" + "user_uuid:" + userUUID + "name:" + groupName + "creation_key:" + groupCreationKey + "members:" + teacherJson);
        apiInterface.createGroup(userUUID, groupName, groupCreationKey, teacherJson).enqueue(new Callback<MobileVerificationResponseModel>() {
            @Override
            public void onResponse(Call<MobileVerificationResponseModel> call, Response<MobileVerificationResponseModel> response) {
                Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.MOBILE_VALIDATION_URL + " Response :" + response.body());
                if (response.body() != null) {
                    checkAndFinish(response.body());
                } else {
                    Toast.makeText(CreateGroupActivity.this, getResources().getString(R.string.SOMETHING_WRONG), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<MobileVerificationResponseModel> call, Throwable t) {
                Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.MOBILE_VALIDATION_URL + " Response :" + t.getMessage());
                Toast.makeText(CreateGroupActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void checkAndFinish(MobileVerificationResponseModel body) {
        if (body.getStatus() == 2) {
            callApiForGroupList(userUUID);

        } else {
            Toast.makeText(this, body.getMessage(), Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
    }

    public void callApiForGroupList(String userId) {
        ApiInterface apiInterface = RetrofitClass.getAPIService();
        Logger.logD(TAG, "URL :" + RetrofitConstant.BASE_URL + RetrofitConstant.GROUP_LIST_URL + " Param : userId:" + userId);
        apiInterface.getGroupList(userId).enqueue(new Callback<MobileVerificationResponseModel>() {
            @Override
            public void onResponse(Call<MobileVerificationResponseModel> call, Response<MobileVerificationResponseModel> response) {
                Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.GROUP_LIST_URL + " Response :" + response.body());
                MobileVerificationResponseModel model = response.body();
                if (model != null) {
                    insertDataIntoGroupTable(model.getGroups());
                }
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(Call<MobileVerificationResponseModel> call, Throwable t) {
                Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.GROUP_LIST_URL + " Response :" + t.getMessage());
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    private void insertDataIntoGroupTable(List<GroupModel> groups) {
        if (!groups.isEmpty()) {
            new DatabaseHandlerClass(this).insertDatatoGroupsTable(groups);
            onBackPressed();
        }
    }

    private String createJson(List<UserDetailsModel> lsitModel) {
        JSONArray array = new JSONArray();
        try {

            JSONObject object = new JSONObject();
            for (UserDetailsModel model : lsitModel) {
                object = new JSONObject();
                object.put("creation_key", model.getUserid());
                array.put(object);
            }
        } catch (Exception ex) {
            Logger.logE(TAG, ex.getMessage(), ex);
        }
        return array.toString();
    }

    @Override
    public void onBackPressed() {
        CreateGroupActivity.this.finish();
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
