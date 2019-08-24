package oelp.mahiti.org.newoepl.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import oelp.mahiti.org.newoepl.R;
import oelp.mahiti.org.newoepl.database.DBConstants;
import oelp.mahiti.org.newoepl.database.DatabaseHandlerClass;
import oelp.mahiti.org.newoepl.fileandvideodownloader.FileModel;
import oelp.mahiti.org.newoepl.models.CatalogueDetailsModel;
import oelp.mahiti.org.newoepl.models.GroupModel;
import oelp.mahiti.org.newoepl.models.MobileVerificationResponseModel;
import oelp.mahiti.org.newoepl.models.QuestionChoicesModel;
import oelp.mahiti.org.newoepl.models.QuestionModel;
import oelp.mahiti.org.newoepl.models.TeacherModel;
import oelp.mahiti.org.newoepl.services.ApiInterface;
import oelp.mahiti.org.newoepl.services.RetrofitClass;
import oelp.mahiti.org.newoepl.services.RetrofitConstant;
import oelp.mahiti.org.newoepl.utils.AppUtils;
import oelp.mahiti.org.newoepl.utils.Constants;
import oelp.mahiti.org.newoepl.utils.Logger;
import oelp.mahiti.org.newoepl.utils.MySharedPref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by RAJ ARYAN on 07/08/19.
 */
public class HomeViewModel extends AndroidViewModel {

    private static final String TAG = AndroidViewModel.class.getSimpleName();
    private final Context context;
    private final String userId;
    public boolean teacherLogin;
    private final boolean catalogApiCalled;
    private final boolean questionApiCalled;
    private final boolean questionChoicesApiCalled;
    private final boolean groupApiCalled;
    private final boolean teacherApiCalled;
    //    public MutableLiveData<Boolean> homeClick = new MutableLiveData<>();
    public MutableLiveData<Boolean> unitsClick = new MutableLiveData<>();
    public MutableLiveData<Boolean> groupsClick = new MutableLiveData<>();
    public MutableLiveData<Integer> userType = new MutableLiveData<>();
    public MutableLiveData<Boolean> showProgresBar = new MutableLiveData<>();
    public MutableLiveData<Boolean> moduleCompleted = new MutableLiveData<>();
    public MutableLiveData<List<CatalogueDetailsModel>> catalogDataList = new MutableLiveData<>();
    public MutableLiveData<String> title = new MutableLiveData<>();
    private MutableLiveData<String> apiErrorMessage = new MutableLiveData<>();
    private MutableLiveData<Integer> dataInserted = new MutableLiveData<>();
    private MutableLiveData<List<FileModel>> listToDownloadImage = new MutableLiveData<>();

    private MySharedPref sharedPref;
    private DatabaseHandlerClass databaseHandlerClass;
    private String catalogDummyJson = "{\n" +
            "\n" +
            "  \"status\": 2,\n" +
            "  \"message\": \"success\",\n" +
            "  \"content\": [{\n" +
            "    \"active\":2,\n" +
            "    \"uuid\":\"a5092fa49bb747019847a676bc3a080a\", \n" +
            "    \"name\":\"शुरूआती कक्षा\", \n" +
            "    \"code\":1, \n" +
            "    \"order\":1, \n" +
            "    \"udf1\":{\n" +
            "      \"color_code\":\"#fbc464\"\n" +
            "    }, \n" +
            "    \"parent\":\"\", \n" +
            "    \"modified\":\"2019-07-30T12:04:00.817487Z\", \n" +
            "    \"icon\":\"\", \n" +
            "    \"path\":\"\", \n" +
            "    \"media_level_type\":\"\", \n" +
            "    \"desc\":\"\", \n" +
            "    \"icon_type\":\"\", \n" +
            "    \"cont_type\":\"\", \n" +
            "    \"type_content\":\"Catalog\", \n" +
            "    \"con_uuid\":\"\" \n" +
            "  },\n" +
            "  {\n" +
            "    \"active\":2,\n" +
            "    \"uuid\":\"a5092fa49bb747019847a676bc3a080b\", \n" +
            "    \"name\":\"कक्षा 2\", \n" +
            "    \"code\":1, \n" +
            "    \"order\":1, \n" +
            "    \"udf1\":{\n" +
            "      \"color_code\":\"#fbc464\"\n" +
            "    }, \n" +
            "    \"parent\":\"\", \n" +
            "    \"modified\":\"2019-07-30T12:04:00.817487Z\", \n" +
            "    \"icon\":\"\", \n" +
            "    \"path\":\"\", \n" +
            "    \"media_level_type\":\"\", \n" +
            "    \"desc\":\"\", \n" +
            "    \"icon_type\":\"\", \n" +
            "    \"cont_type\":\"\", \n" +
            "    \"type_content\":\"Catalog\", \n" +
            "    \"con_uuid\":\"\" \n" +
            "  },\n" +
            "  {\n" +
            "    \"active\":2,\n" +
            "    \"uuid\":\"a5092fa49bb747019847a676bc3a080c\", \n" +
            "    \"name\":\"पढ़ने के तरीके\", \n" +
            "    \"code\":1, \n" +
            "    \"order\":1, \n" +
            "    \"udf1\":{\n" +
            "      \"color_code\":\"#fbc464\"\n" +
            "    }, \n" +
            "    \"parent\":\"\", \n" +
            "    \"modified\":\"2019-07-30T12:04:00.817487Z\", \n" +
            "    \"icon\":\"\", \n" +
            "    \"path\":\"\", \n" +
            "    \"media_level_type\":\"\", \n" +
            "    \"desc\":\"\", \n" +
            "    \"icon_type\":\"\", \n" +
            "    \"cont_type\":\"\", \n" +
            "    \"type_content\":\"Catalog\", \n" +
            "    \"con_uuid\":\"\" \n" +
            "  },{\n" +
            "    \"active\":2,\n" +
            "    \"uuid\":\"a5092fa49bb747019847a676bc3a080d\", \n" +
            "    \"name\":\"लिखने के तरीके\", \n" +
            "    \"code\":1, \n" +
            "    \"order\":1, \n" +
            "    \"udf1\":{\n" +
            "      \"color_code\":\"#fbc464\"\n" +
            "    }, \n" +
            "    \"parent\":\"\", \n" +
            "    \"modified\":\"2019-07-30T12:04:00.817487Z\", \n" +
            "    \"icon\":\"\", \n" +
            "    \"path\":\"\", \n" +
            "    \"media_level_type\":\"\", \n" +
            "    \"desc\":\"\", \n" +
            "    \"icon_type\":\"\", \n" +
            "    \"cont_type\":\"\", \n" +
            "    \"type_content\":\"Catalog\", \n" +
            "    \"con_uuid\":\"\" \n" +
            "  },\n" +
            "  {\n" +
            "    \"active\":2,\n" +
            "    \"uuid\":\"a5092fa49bb747019847a676bc3a080e\", \n" +
            "    \"name\":\"भाग - 1 तैयार\",\n" +
            "    \"code\":1, \n" +
            "    \"order\":1, \n" +
            "    \"udf1\":{\n" +
            "      \"color_code\":\"#fbc464\"\n" +
            "    }, \n" +
            "    \"parent\":\"a5092fa49bb747019847a676bc3a080a\", \n" +
            "    \"modified\":\"2019-07-30T12:04:00.817487Z\", \n" +
            "    \"icon\":\"\", \n" +
            "    \"path\":\"\", \n" +
            "    \"media_level_type\":\"\", \n" +
            "    \"desc\":\"\", \n" +
            "    \"icon_type\":\"\", \n" +
            "    \"cont_type\":\"\", \n" +
            "    \"type_content\":\"Catalog\", \n" +
            "    \"con_uuid\":\"\" \n" +
            "  },\n" +
            "  {\n" +
            "    \"active\":2,\n" +
            "    \"uuid\":\"a5092fa49bb747019847a676bc3a080f\", \n" +
            "    \"name\":\"भाग - 2 लिखित माहौल बनाना\",\n" +
            "    \"code\":1, \n" +
            "    \"order\":1, \n" +
            "    \"udf1\":{\n" +
            "      \"color_code\":\"#fbc464\"\n" +
            "    }, \n" +
            "    \"parent\":\"a5092fa49bb747019847a676bc3a080a\", \n" +
            "    \"modified\":\"2019-07-30T12:04:00.817487Z\", \n" +
            "    \"icon\":\"\", \n" +
            "    \"path\":\"\", \n" +
            "    \"media_level_type\":\"\", \n" +
            "    \"desc\":\"\", \n" +
            "    \"icon_type\":\"\", \n" +
            "    \"cont_type\":\"\", \n" +
            "    \"type_content\":\"Catalog\", \n" +
            "    \"con_uuid\":\"\" \n" +
            "  },\n" +
            "  {\n" +
            "    \"active\":2,\n" +
            "    \"uuid\":\"a5092fa49bb747019847a676bc3a080g\", \n" +
            "    \"name\":\"भाग - 3 लिपि से जुड़ना \",\n" +
            "    \"code\":1, \n" +
            "    \"order\":1, \n" +
            "    \"udf1\":{\n" +
            "      \"color_code\":\"#fbc464\"\n" +
            "    }, \n" +
            "    \"parent\":\"a5092fa49bb747019847a676bc3a080a\", \n" +
            "    \"modified\":\"2019-07-30T12:04:00.817487Z\", \n" +
            "    \"icon\":\"\", \n" +
            "    \"path\":\"\", \n" +
            "    \"media_level_type\":\"\", \n" +
            "    \"desc\":\"\", \n" +
            "    \"icon_type\":\"\", \n" +
            "    \"cont_type\":\"\", \n" +
            "    \"type_content\":\"Catalog\", \n" +
            "    \"con_uuid\":\"\" \n" +
            "  },\n" +
            "  {\n" +
            "    \"active\":2,\n" +
            "    \"uuid\":\"a5092fa49bb747019847a676bc3a080h\", \n" +
            "    \"name\":\"भाग - 4 शुरूआती लेखन \",\n" +
            "    \"code\":1, \n" +
            "    \"order\":1, \n" +
            "    \"udf1\":{\n" +
            "      \"color_code\":\"#fbc464\"\n" +
            "    }, \n" +
            "    \"parent\":\"a5092fa49bb747019847a676bc3a080a\", \n" +
            "    \"modified\":\"2019-07-30T12:04:00.817487Z\", \n" +
            "    \"icon\":\"\", \n" +
            "    \"path\":\"\", \n" +
            "    \"media_level_type\":\"\", \n" +
            "    \"desc\":\"\", \n" +
            "    \"icon_type\":\"\", \n" +
            "    \"cont_type\":\"\", \n" +
            "    \"type_content\":\"Catalog\", \n" +
            "    \"con_uuid\":\"\" \n" +
            "  },\n" +
            "  {\n" +
            "    \"active\":2,\n" +
            "    \"uuid\":\"a5092fa49bb747019847a676bc3a080i\", \n" +
            "    \"name\":\"ओईएलपी किट \",\n" +
            "    \"code\":1, \n" +
            "    \"order\":1, \n" +
            "    \"udf1\":{\n" +
            "      \"color_code\":\"#fbc464\"\n" +
            "    }, \n" +
            "    \"parent\":\"a5092fa49bb747019847a676bc3a080e\", \n" +
            "    \"modified\":\"2019-07-30T12:04:00.817487Z\", \n" +
            "    \"icon\":\"\", \n" +
            "    \"path\":\"uploads/videos/unit1/section1/1900125913_U001_V001.mp4\", \n" +
            "    \"media_level_type\":\"\", \n" +
            "    \"desc\":\"\", \n" +
            "    \"icon_type\":\"\", \n" +
            "    \"cont_type\":\"video\", \n" +
            "    \"type_content\":\"Catalog\", \n" +
            "    \"con_uuid\":\"\" \n" +
            "  },\n" +
            "  {\n" +
            "    \"active\":2,\n" +
            "    \"uuid\":\"a5092fa49bb747019847a676bc3a080j\", \n" +
            "    \"name\":\"कक्षा की व्यवस्था-1\",\n" +
            "    \"code\":1, \n" +
            "    \"order\":1, \n" +
            "    \"udf1\":{\n" +
            "      \"color_code\":\"#fbc464\"\n" +
            "    }, \n" +
            "    \"parent\":\"a5092fa49bb747019847a676bc3a080e\", \n" +
            "    \"modified\":\"2019-07-30T12:04:00.817487Z\", \n" +
            "    \"icon\":\"\", \n" +
            "    \"path\":\"uploads/videos/unit1/section1/1900125913_U001_V002.mp4\", \n" +
            "    \"media_level_type\":\"\", \n" +
            "    \"desc\":\"\", \n" +
            "    \"icon_type\":\"\", \n" +
            "    \"cont_type\":\"video\", \n" +
            "    \"type_content\":\"Catalog\", \n" +
            "    \"con_uuid\":\"\" \n" +
            "  },\n" +
            "  {\n" +
            "    \"active\":2,\n" +
            "    \"uuid\":\"a5092fa49bb747019847a676bc3a080k\", \n" +
            "    \"name\":\"कक्षा की व्यवस्था-2\",\n" +
            "    \"code\":1, \n" +
            "    \"order\":1, \n" +
            "    \"udf1\":{\n" +
            "      \"color_code\":\"#fbc464\"\n" +
            "    }, \n" +
            "    \"parent\":\"a5092fa49bb747019847a676bc3a080e\", \n" +
            "    \"modified\":\"2019-07-30T12:04:00.817487Z\", \n" +
            "    \"icon\":\"\", \n" +
            "    \"path\":\"uploads/videos/unit1/section1/1900125913_U001_V003.mp4\", \n" +
            "    \"media_level_type\":\"\", \n" +
            "    \"desc\":\"\", \n" +
            "    \"icon_type\":\"\", \n" +
            "    \"cont_type\":\"video\", \n" +
            "    \"type_content\":\"Catalog\", \n" +
            "    \"con_uuid\":\"\" \n" +
            "  },\n" +
            "  {\n" +
            "    \"active\":2,\n" +
            "    \"uuid\":\"a5092fa49bb747019847a676bc3a080l\", \n" +
            "    \"name\":\"दिनचर्या\",\n" +
            "    \"code\":1, \n" +
            "    \"order\":1, \n" +
            "    \"udf1\":{\n" +
            "      \"color_code\":\"#fbc464\"\n" +
            "    }, \n" +
            "    \"parent\":\"a5092fa49bb747019847a676bc3a080e\", \n" +
            "    \"modified\":\"2019-07-30T12:04:00.817487Z\", \n" +
            "    \"icon\":\"\", \n" +
            "    \"path\":\"uploads/videos/unit1/section1/1900125913_U001_V004.mp4\", \n" +
            "    \"media_level_type\":\"\", \n" +
            "    \"desc\":\"\", \n" +
            "    \"icon_type\":\"\", \n" +
            "    \"cont_type\":\"video\", \n" +
            "    \"type_content\":\"Catalog\", \n" +
            "    \"con_uuid\":\"\" \n" +
            "  },\n" +
            "  \n" +
            "  {\n" +
            "    \"active\":2,\n" +
            "    \"uuid\":\"a5092fa49bb747019847a676bc3a080m\", \n" +
            "    \"name\":\"जिम्मेदारी का कोना\",\n" +
            "    \"code\":1, \n" +
            "    \"order\":1, \n" +
            "    \"udf1\":{\n" +
            "      \"color_code\":\"#fbc464\"\n" +
            "    }, \n" +
            "    \"parent\":\"a5092fa49bb747019847a676bc3a080e\", \n" +
            "    \"modified\":\"2019-07-30T12:04:00.817487Z\", \n" +
            "    \"icon\":\"\", \n" +
            "    \"path\":\"uploads/videos/unit1/section1/1900125913_U001_V005.mp4\", \n" +
            "    \"media_level_type\":\"\", \n" +
            "    \"desc\":\"\", \n" +
            "    \"icon_type\":\"\", \n" +
            "    \"cont_type\":\"video\", \n" +
            "    \"type_content\":\"Catalog\", \n" +
            "    \"con_uuid\":\"\" \n" +
            "  }\n" +
            "  \n" +
            "    ]\n" +
            "}";
    public String groupJson="{\"status\":2,\n" +
            "\"message\":\"success\",\n" +
            "\"groups\":[{\"grp_name\":\"Android group\",\n" +
            "\"creation_key\":\"USYW-98765-JSYW\",\n" +
            "\"created_on\":\"2019-08-19 14:10:38\", \n" +
            "\"active\":2,\n" +
            "\"members\":[{\"uuid\":\"HIE2-JKSO-29H3-1204\"}, {\"uuid\":\"HIE2-JKSO-29H3-1205\"}]\n" +
            "},\n" +
            "{\"grp_name\":\"Android group1\",\n" +
            "\"creation_key\":\"USYW-98765-JSYM\",\n" +
            "\"created_on\":\"2019-08-19 14:10:38\", \n" +
            "\"active\":2,\n" +
            "\"members\":[{\"uuid\":\"HIE2-JKSO-29H3-1206\"}, {\"uuid\":\"HIE2-JKSO-29H3-1207\"}]\n" +
            "},\n" +
            "{\"grp_name\":\"Android group2\",\n" +
            "\"creation_key\":\"USYW-98765-JSYX\",\n" +
            "\"created_on\":\"2019-08-19 14:10:38\", \n" +
            "\"active\":2,\n" +
            "\"members\":[{\"uuid\":\"HIE2-JKSO-29H3-1208\"}, {\"uuid\":\"HIE2-JKSO-29H3-1209\"}]\n" +
            "},\n" +
            "{\"grp_name\":\"Android group3\",\n" +
            "\"creation_key\":\"USYW-98765-JSYZ\",\n" +
            "\"created_on\":\"2019-08-19 14:10:38\", \n" +
            "\"active\":2,\n" +
            "\"members\":[{\"uuid\":\"HIE2-JKSO-29H3-1210\"}, {\"uuid\":\"HIE2-JKSO-29H3-1211\"}]\n" +
            "}\n" +
            "]\n" +
            "}";
    public MutableLiveData<String> parentId = new MutableLiveData<>();
    public List<CatalogueDetailsModel> modelForCatalog = new ArrayList<>();
    private MutableLiveData<Long> catalogDataInserted = new MutableLiveData<>();

    public HomeViewModel(@NonNull Application application) {
        super(application);
        context = application;
        sharedPref = new MySharedPref(application);
        databaseHandlerClass = new DatabaseHandlerClass(application);
        showProgresBar.setValue(false);
        userId = sharedPref.readString(Constants.USER_ID, "");
        teacherLogin = sharedPref.readInt(Constants.USER_TYPE, Constants.USER_TEACHER) == Constants.USER_TEACHER;
        catalogApiCalled = sharedPref.readString(RetrofitConstant.CATALOGUE_URL, "").equalsIgnoreCase(AppUtils.getDate());
        questionApiCalled = sharedPref.readString(RetrofitConstant.QUESTION_LIST_URL, "").equalsIgnoreCase(AppUtils.getDate());
        questionChoicesApiCalled = sharedPref.readString(RetrofitConstant.QUESTION_CHOICES_LIST_URL, "").equalsIgnoreCase(AppUtils.getDate());
        groupApiCalled = sharedPref.readString(RetrofitConstant.GROUP_LIST_URL, "").equalsIgnoreCase(AppUtils.getDate());
        teacherApiCalled = sharedPref.readString(RetrofitConstant.TEACHER_LIST_URL, "").equalsIgnoreCase(AppUtils.getDate());
        dataInserted.setValue(null);
        callAllAPI();

        userType.setValue(sharedPref.readInt(Constants.USER_TYPE, Constants.USER_TEACHER));
        if (userType.getValue().equals(Constants.USER_TEACHER)) {
            unitsClick.setValue(true);
            groupsClick.setValue(false);
        } else {
            unitsClick.setValue(true);
            groupsClick.setValue(false);
        }

    }

    private void callAllAPI() {
        if (!catalogApiCalled)
            callApiForCatalogData(userId);
        if (!questionApiCalled)
            callApiQuestions(userId);
        if (!questionChoicesApiCalled)
            callApiForQuestionChoices(userId);
//        if (!groupApiCalled)
            callApiForGroupList(userId);
//            callApiForTeacherList(userId);
    }

    private void callApiForTeacherList(String userId) {
        ApiInterface apiInterface = RetrofitClass.getAPIService();
        Logger.logD(TAG, "URL :" + RetrofitConstant.BASE_URL + RetrofitConstant.TEACHER_LIST_URL + " Param : userId:" + userId);
        apiInterface.getTeacherList(userId).enqueue(new Callback<MobileVerificationResponseModel>() {
            @Override
            public void onResponse(Call<MobileVerificationResponseModel> call, Response<MobileVerificationResponseModel> response) {
                Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.TEACHER_LIST_URL + " Response :" + response.body());

                MobileVerificationResponseModel model = response.body();
                if (model != null) {
                    insertDataIntoTeacherTable(model.getTeachers());
                } else {
                    apiErrorMessage.setValue(context.getResources().getString(R.string.SOMETHING_WRONG));
                }

            }

            @Override
            public void onFailure(Call<MobileVerificationResponseModel> call, Throwable t) {
                Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.TEACHER_LIST_URL + " Response :" + t.getMessage());
                apiErrorMessage.setValue(t.getMessage());
            }
        });
    }

    private void insertDataIntoTeacherTable(List<TeacherModel> teachers) {
//        if (!teachers.isEmpty()) {
//            groupDataInsert.setValue(databaseHandlerClass.insertDatatoTeacherTable(teachers));
//            sharedPref.writeString(RetrofitConstant.GROUP_LIST_URL, AppUtils.getDate());
//        }
    }

    private void callApiForGroupList(String userId) {
        ApiInterface apiInterface = RetrofitClass.getAPIService();
        Logger.logD(TAG, "URL :" + RetrofitConstant.BASE_URL + RetrofitConstant.GROUP_LIST_URL + " Param : userId:" + userId);
        apiInterface.getGroupList(userId).enqueue(new Callback<MobileVerificationResponseModel>() {
            @Override
            public void onResponse(Call<MobileVerificationResponseModel> call, Response<MobileVerificationResponseModel> response) {
                Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.GROUP_LIST_URL + " Response :" + response.body());

                MobileVerificationResponseModel model = response.body();
                if (model != null) {
                    insertDataIntoGroupTable(model.getGroups());
                } else {
                    apiErrorMessage.setValue(context.getResources().getString(R.string.SOMETHING_WRONG));
                }

            }

            @Override
            public void onFailure(Call<MobileVerificationResponseModel> call, Throwable t) {
                Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.GROUP_LIST_URL + " Response :" + t.getMessage());
                apiErrorMessage.setValue(t.getMessage());
            }
        });

    }

    private MutableLiveData<Long> groupDataInsert = new MutableLiveData<>();

    public MutableLiveData<Long> getGroupInserted(){
        return groupDataInsert;
    }

    private void insertDataIntoGroupTable(List<GroupModel> groups) {
        if (!groups.isEmpty()) {
            groupDataInsert.setValue(databaseHandlerClass.insertDatatoGroupsTable(groups));
            sharedPref.writeString(RetrofitConstant.GROUP_LIST_URL, AppUtils.getDate());
        }
    }

    public void setDataInserted(Integer dataInserted) {
        this.dataInserted.setValue(dataInserted);
    }

    private void callApiForQuestionChoices(String userId) {
//        String modifiedDate = databaseHandlerClass.getModifiedDate(DBConstants.QUESTION_TABLE);
        String modifiedDate = "";
        ApiInterface apiInterface = RetrofitClass.getAPIService();
        Logger.logD(TAG, "URL :" + RetrofitConstant.BASE_URL + RetrofitConstant.QUESTION_CHOICES_LIST_URL + " Param : userId:" + userId + " modified_date:" + modifiedDate);
        apiInterface.getQuestionChoicesList(userId, modifiedDate).enqueue(new Callback<MobileVerificationResponseModel>() {
            @Override
            public void onResponse(Call<MobileVerificationResponseModel> call, Response<MobileVerificationResponseModel> response) {
                if (response.body() != null) {
                    insertQuestionChoicesToTable(response.body().getQuestionChoicesModelList());
                    Logger.logD(TAG, "URL :" + RetrofitConstant.BASE_URL + RetrofitConstant.QUESTION_CHOICES_LIST_URL + " Response :" + response.body().toString());

                } else {
                    apiErrorMessage.setValue(context.getResources().getString(R.string.SOMETHING_WRONG));
                }

            }

            @Override
            public void onFailure(Call<MobileVerificationResponseModel> call, Throwable t) {
                apiErrorMessage.setValue(t.getMessage());
                Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.QUESTION_CHOICES_LIST_URL + " Response :" + t.getMessage());
            }
        });
    }

    private void insertQuestionChoicesToTable(List<QuestionChoicesModel> questionChoicesModelList) {
        if (!questionChoicesModelList.isEmpty()) {
            databaseHandlerClass.insertDatatoQuestionChoicesTable(questionChoicesModelList);
            sharedPref.writeString(RetrofitConstant.QUESTION_CHOICES_LIST_URL, AppUtils.getDate());

        }
    }

    private void getListOfImageFromDb() {
        List<FileModel> imageList = databaseHandlerClass.getImageListFromTable();
        if (!imageList.isEmpty()) {
            checkOfflineAvailable(imageList);
        } else {
            dataInserted.setValue(2);
        }
        showProgresBar.setValue(false);
    }

    private void checkOfflineAvailable(List<FileModel> imageList) {
        List<FileModel> imagePathToRemove = new ArrayList<>();
        for (FileModel iconPath : imageList) {
            try {
                File file = new File(AppUtils.completePathInSDCard(Constants.IMAGE), AppUtils.getFileName(iconPath.getFileUrl()));
                if (file.exists()) {
                    imagePathToRemove.add(iconPath);
                }

            } catch (Exception ex) {
                Logger.logE(TAG, ex.getMessage(), ex);
            }
        }
        imageList.removeAll(imagePathToRemove);
        if (!imageList.isEmpty()) {
            listToDownloadImage.setValue(imageList);
        } else {
            dataInserted.setValue(3);
        }
    }

    public MutableLiveData<Integer> getDataInserted() {
        return dataInserted;
    }

    public MutableLiveData<List<FileModel>> getListOfImageToDownload() {
        return listToDownloadImage;
    }

    private void callApiQuestions(String userId) {
//        String modifiedDate = databaseHandlerClass.getModifiedDate(DBConstants.QUESTION_TABLE);
        String modifiedDate = "";
        ApiInterface apiInterface = RetrofitClass.getAPIService();
        Logger.logD(TAG, "URL :" + RetrofitConstant.BASE_URL + RetrofitConstant.QUESTION_LIST_URL + " Param : userId:" + userId + " modified_date:" + modifiedDate);
        apiInterface.getQuestionList(userId, modifiedDate).enqueue(new Callback<MobileVerificationResponseModel>() {
            @Override
            public void onResponse(Call<MobileVerificationResponseModel> call, Response<MobileVerificationResponseModel> response) {
                if (response.body() != null) {
                    insertQuestionToTable(response.body().getQuestionModelList());
                    Logger.logD(TAG, "URL :" + RetrofitConstant.BASE_URL + RetrofitConstant.QUESTION_LIST_URL + " Response :" + response.body().toString());

                } else {
                    apiErrorMessage.setValue(context.getResources().getString(R.string.SOMETHING_WRONG));
                }

            }

            @Override
            public void onFailure(Call<MobileVerificationResponseModel> call, Throwable t) {
                apiErrorMessage.setValue(t.getMessage());
                Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.QUESTION_LIST_URL + " Response :" + t.getMessage());
            }
        });
    }

    private void insertQuestionToTable(List<QuestionModel> questionModelList) {
        if (!questionModelList.isEmpty()) {
            databaseHandlerClass.insertDatatoQuestionTable(questionModelList);
            sharedPref.writeString(RetrofitConstant.QUESTION_LIST_URL, AppUtils.getDate());
        }

    }

    public void callApiForCatalogData(String userId) {
        showProgresBar.setValue(true);
        String modifiedDate = databaseHandlerClass.getModifiedDate(DBConstants.CAT_TABLE_NAME);
        ApiInterface apiInterface = RetrofitClass.getAPIService();
        Logger.logD(TAG, "URL :" + RetrofitConstant.BASE_URL + RetrofitConstant.CATALOGUE_URL + " Param : userId:" + userId + " modified_date:" + modifiedDate);
        apiInterface.catalogData(userId, "").enqueue(new Callback<MobileVerificationResponseModel>() {
            @Override
            public void onResponse(Call<MobileVerificationResponseModel> call, Response<MobileVerificationResponseModel> response) {
                Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.CATALOGUE_URL + " Response :" + response.body());

                MobileVerificationResponseModel model = response.body();
                if (model != null) {
                    insertDataIntoCatalogTable(model.getCatalogueDetailsModel());
                } else {
                    apiErrorMessage.setValue(context.getResources().getString(R.string.SOMETHING_WRONG));
                }
                getListOfImageFromDb();

            }

            @Override
            public void onFailure(Call<MobileVerificationResponseModel> call, Throwable t) {
                Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.CATALOGUE_URL + " Response :" + t.getMessage());
                apiErrorMessage.setValue(t.getMessage());
                getListOfImageFromDb();
            }
        });
    }

    private void insertDataIntoCatalogTable(List<CatalogueDetailsModel> catalogueDetailsModel) {
        if (catalogueDetailsModel != null && !catalogueDetailsModel.isEmpty()) {
            catalogDataInserted.setValue(databaseHandlerClass.insertDataToCatalogueTable(catalogueDetailsModel));
            sharedPref.writeString(RetrofitConstant.CATALOGUE_URL, AppUtils.getDate());
        }
    }

    public MutableLiveData<Long> getCatalogDataInserted(){
        return catalogDataInserted;
    }




    public MutableLiveData<String> getApiErrorMessage() {
        return apiErrorMessage;
    }


//    public void onHomeClick() {
//        if (!homeClick.getValue()) {
//            homeClick.setValue(true);
//            groupsClick.setValue(false);
//            unitsClick.setValue(false);
//        }
//    }


    public void onUnitsClick() {
        if (!showProgresBar.getValue()) {
            if (!unitsClick.getValue()) {
//                homeClick.setValue(false);
                unitsClick.setValue(true);
                groupsClick.setValue(false);
            }
        }

    }


    public void onGroupsClick() {
        if (!showProgresBar.getValue()) {

            if (!groupsClick.getValue()) {
//                homeClick.setValue(false);
                groupsClick.setValue(true);
                unitsClick.setValue(false);
            }
        }
    }


    public List<CatalogueDetailsModel> getCatalogData(String parentId) {
        modelForCatalog = databaseHandlerClass.getCatalogData(parentId);
        return modelForCatalog;
    }

    public List<GroupModel> getGroupList(){
        return databaseHandlerClass.getGroupList();
    }
}
