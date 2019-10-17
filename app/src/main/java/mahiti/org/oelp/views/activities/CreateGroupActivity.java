package mahiti.org.oelp.views.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import mahiti.org.oelp.R;
import mahiti.org.oelp.database.DAOs.GroupDao;
import mahiti.org.oelp.database.DAOs.TeacherDao;
import mahiti.org.oelp.models.MobileVerificationResponseModel;
import mahiti.org.oelp.models.TeacherModel;
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
    private String groupUUID;
    private String groupTitle;
    private UserDetailsModel model;


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

        groupUUID = getIntent().getStringExtra("groupUUID");
        groupTitle = getIntent().getStringExtra("groupName");
        if (!groupTitle.isEmpty()) {
            etGroupName.setText(groupTitle);
        }

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

        if (!groupUUID.isEmpty()) {
            TeacherDao dao = new TeacherDao(this);
            List<TeacherModel> teacherList = dao.getTeachers(groupUUID, 1);
            userDetailList = dao.getUserDetailsModels(teacherList);
            if (userDetailList.size() > 0) {
                llView.setVisibility(View.VISIBLE);
            }
            adapter.setList(userDetailList, Constants.EDIT);
            btnCreate.setText(R.string.update);

        } else {
            btnCreate.setText(R.string.create);
        }

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
                            if (userDetail.getIsTrainer().equals(Constants.USER_TEACHER))
//                                if (validateUser(userDetail)) {
                                    aDDTeacherToList(userDetail);
//                                } else {
//                                    movetoRegistrationActivity(userDetail);
//                                }
                            else {
                                Toast.makeText(CreateGroupActivity.this, "Please enter teacher numbers", Toast.LENGTH_SHORT).show();
                            }


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

    private void aDDTeacherToList(UserDetailsModel userDetail) {
        userDetailList.add(userDetail);
        etMobileNo.getText().clear();
        adapter.setList(userDetailList, Constants.ADD);
    }

    private void movetoRegistrationActivity(UserDetailsModel userDetail) {
        new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setTitle("User Profile Incomplete")
                .setMessage("Do you want to complete the profile ")
                .setPositiveButton("Yes", (dialog, which) -> {
                    Intent i = new Intent(CreateGroupActivity.this, TeacherRegistrationActivity.class);
                    i.putExtra("UserDetails", userDetail);
                    i.putExtra("ActivityType", 1);  // Activity Type 1 for CreateGroupActivity 0 for MobileLoginActivity
                    startActivityForResult(i, 100);
                    overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
                    dialog.dismiss();
                })

                .setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != 100 || resultCode == Activity.RESULT_CANCELED)
            return;
        else {
            model = getIntent().getParcelableExtra("UserDetails");
            aDDTeacherToList(model);
        }

    }

    private boolean validateUser(UserDetailsModel userDetail) {
        boolean status=true;

        if (userDetail.getName()!=null) {
            if (userDetail.getName().isEmpty()) {
                status = false;
            }
        }else
            status = false;

        if (userDetail.getMobile_number()!=null) {
            if (userDetail.getMobile_number().isEmpty()) {
                status = false;
            }
        }else
            status = false;

        if (userDetail.getSchool()!=null) {
            if (userDetail.getSchool().isEmpty()) {
                status = false;
            }
        }else
            status = false;

        if (userDetail.getStateName()!=null) {
            if (userDetail.getStateName().isEmpty()) {
                status = false;
            }
        }else
            status = false;

        if (userDetail.getDistrictname()!=null) {
            if (userDetail.getDistrictname().isEmpty()) {
                status = false;
            }
        }else
            status = false;

        if (userDetail.getBlockName()!=null) {
            if (userDetail.getBlockName().isEmpty()) {
                status = false;
            }
        }else
            status = false;

        if (userDetail.getVillageName()!=null) {
            if (userDetail.getVillageName().isEmpty()) {
                status = false;
            }
        }else
            status = false;

        return status;

    }

    private void showAlertDialog(UserDetailsModel userDetail) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
        builder.setMessage(userDetail.getName() + " is already added to some other group");
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
            createOrAddGroup();
        } else {
            Toast.makeText(this, getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
        }
    }

    private void createOrAddGroup() {
        if (groupUUID.isEmpty()) {
            addTeacher();
        } else {
            editTeacher();
        }
    }

    private void editTeacher() {
        progressBar.setVisibility(View.VISIBLE);
        phoneNo = etMobileNo.getText().toString().trim();
        groupName = etGroupName.getText().toString().trim();
        validateAndProceedAdd();
    }

    private void addTeacher() {
        progressBar.setVisibility(View.VISIBLE);
        groupName = etGroupName.getText().toString().trim();
        validateAndProceedEdit();
    }

    private void validateAndProceedEdit() {
        if (groupName.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Please enter group name", Toast.LENGTH_SHORT).show();
        } else if (userDetailList.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Please select teacher", Toast.LENGTH_SHORT).show();
        } else {
            craeteUUIDJSONandProceed(userDetailList);
        }
    }

    private void validateAndProceedAdd() {
        List<UserDetailsModel> lsitModel = new ArrayList<>();
        lsitModel.clear();
        lsitModel = adapter.getUserDetailsList();

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
        String groupCreationKey = "";
        if (groupUUID.isEmpty())
            groupCreationKey = AppUtils.getUUID();
        else
            groupCreationKey = groupUUID;

        createJson(lsitModel, userUUID, groupCreationKey);

    }

    private void callApiForCreateGroup(String userUUID, String groupName, String groupCreationKey, String teacherJson) {
        ApiInterface apiInterface = RetrofitClass.getAPIService();
        Logger.logD(TAG, "URL: " + RetrofitConstant.BASE_URL + RetrofitConstant.CREATE_GROUP_URL + " Param :" + "user_uuid:" + userUUID + "name:" + groupName + "creation_key:" + groupCreationKey + "members:" + teacherJson);
        apiInterface.createGroup(userUUID, groupName, groupCreationKey, teacherJson).enqueue(new Callback<MobileVerificationResponseModel>() {
            @Override
            public void onResponse(Call<MobileVerificationResponseModel> call, Response<MobileVerificationResponseModel> response) {
                Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.MOBILE_VALIDATION_URL + " Response :" + response.body());
                if (response.body() != null) {
                    if (CheckNetwork.checkNet(CreateGroupActivity.this))
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

    // Teacher API call
    public void callApiForTeachersList(String userId) {
        ApiInterface apiInterface = RetrofitClass.getAPIService();
        Logger.logD(TAG, "URL :" + RetrofitConstant.BASE_URL + RetrofitConstant.GROUP_LIST_URL + " Param : userId:" + userId);
        apiInterface.getTeacherList(userId).enqueue(new Callback<MobileVerificationResponseModel>() {
            @Override
            public void onResponse(Call<MobileVerificationResponseModel> call, Response<MobileVerificationResponseModel> response) {
                Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.GROUP_LIST_URL + " Response :" + response.body());

                MobileVerificationResponseModel model = response.body();
                if (model != null) {
                    long insertedCount = new TeacherDao(CreateGroupActivity.this).insertTeacherDataToDB(model.getTeachers());
                    Logger.logD(TAG, "teachers inserted count - " + insertedCount);
                } else {
                    Logger.logD(TAG, "teachers inserted count - " + model.getMessage());
                }
                progressBar.setVisibility(View.GONE);
                insertDataIntoGroupTable();

            }

            @Override
            public void onFailure(Call<MobileVerificationResponseModel> call, Throwable t) {
                Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.GROUP_LIST_URL + " Response :" + t.getMessage());
                progressBar.setVisibility(View.GONE);
                onBackPressed();
            }
        });
    }

    private void checkAndFinish(MobileVerificationResponseModel body) {
        if (body.getStatus() == 2) {
            callApiForGroupList(userUUID);
        } else {
            Toast.makeText(this, body.getMessage(), Toast.LENGTH_SHORT).show();
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
                    if (!model.getGroups().isEmpty()) {
                        new GroupDao(CreateGroupActivity.this).insertDataToGroupsTable(model.getGroups());
                    }
                    if (CheckNetwork.checkNet(CreateGroupActivity.this))
                        callApiForTeachersList(userId);
                    else
                        progressBar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Call<MobileVerificationResponseModel> call, Throwable t) {
                Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.GROUP_LIST_URL + " Response :" + t.getMessage());
                progressBar.setVisibility(View.GONE);
                onBackPressed();
            }
        });

    }

    private void insertDataIntoGroupTable() {
        Intent intent = new Intent();
        intent.putExtra("result", true);
        intent.putExtra(Constants.GROUP_NAME, etGroupName.getText().toString().trim());
        setResult(RESULT_OK, intent);
        onBackPressed();

    }

    private void createJson(List<UserDetailsModel> lsitModel, String userUUID, String groupCreationKey) {
        JSONArray array = new JSONArray();
        /*
        Add Trainer Info also to group
         */

        boolean valide = false;
        try {
            JSONObject object;
            for (UserDetailsModel model : lsitModel) {
                    object = new JSONObject();
                    object.put("creation_key", model.getUserid());
                    array.put(object);
                    valide = true;
            }
        } catch (Exception ex) {
            Logger.logE(TAG, ex.getMessage(), ex);
        }

        /*JSONObject object = new JSONObject();
        try {
            object.put("creation_key", userUUID);
            array.put(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        if (valide) {
            callApiForCreateGroup(userUUID, groupName, groupCreationKey, array.toString());
        } else {
            Toast.makeText(this, "Please select teacher", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }
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
