package oelp.mahiti.org.newoepl.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.util.List;

import oelp.mahiti.org.newoepl.database.DatabaseHandlerClass;
import oelp.mahiti.org.newoepl.models.CatalogueDetailsModel;
import oelp.mahiti.org.newoepl.models.MobileVerificationResponseModel;
import oelp.mahiti.org.newoepl.utils.Constants;
import oelp.mahiti.org.newoepl.utils.MySharedPref;

/**
 * Created by RAJ ARYAN on 07/08/19.
 */
public class HomeViewModel extends AndroidViewModel {

    private static final String TAG = AndroidViewModel.class.getSimpleName();
    private final Context context;
    private final int userId;
    public MutableLiveData<Boolean> homeClick = new MutableLiveData<>();
    public MutableLiveData<Boolean> unitsClick = new MutableLiveData<>();
    public MutableLiveData<Boolean> groupsClick = new MutableLiveData<>();
    public MutableLiveData<Boolean> schoolsClick = new MutableLiveData<>();
    public MutableLiveData<Integer> userType = new MutableLiveData<>();
    public MutableLiveData<Boolean> showProgresBar = new MutableLiveData<>();
    public MutableLiveData<String> errorMessage = new MutableLiveData<>();
    public MutableLiveData<Long> insertLong = new MutableLiveData<>();

    public MutableLiveData<Boolean> moduleCompleted = new MutableLiveData<>();
    public MutableLiveData<Boolean> playButton = new MutableLiveData<>();

    public MutableLiveData<List<CatalogueDetailsModel>> catalogDataList = new MutableLiveData<>();
//    public MutableLiveData<String> parentId = new MutableLiveData<>();
    public MutableLiveData<String> title = new MutableLiveData<>();

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
    private MutableLiveData<Boolean> dataInserted = new MutableLiveData<>();
    public MutableLiveData<String> parentId = new MutableLiveData<>();
    public MutableLiveData<List<CatalogueDetailsModel>> modelForCatalog = new MutableLiveData<>();

    public HomeViewModel(@NonNull Application application) {
        super(application);
        context = application;
        sharedPref = new MySharedPref(application);
        databaseHandlerClass = new DatabaseHandlerClass(application);
        showProgresBar.setValue(false);
        userId = sharedPref.readInt(Constants.USER_ID, 0);
        errorMessage.setValue("");
        callApiForCatalogData();
        if (parentId.getValue()!=null)
            getCatalogData(parentId.getValue());
        userType.setValue(sharedPref.readInt(Constants.USER_TYPE, Constants.USER_TEACHER));
        if (userType.getValue().equals(Constants.USER_TEACHER)) {
            unitsClick.setValue(true);
            homeClick.setValue(false);
            groupsClick.setValue(false);
        } else {
            unitsClick.setValue(true);
            schoolsClick.setValue(false);
            groupsClick.setValue(false);
        }

    }

    public void onCategoryClick(CatalogueDetailsModel model){
        onUnitsClick();
        parentId.setValue(model.getUuid());
        getCatalogData(parentId.getValue());
    }

    public void callApiForCatalogData() {
//        String modifiedDate = databaseHandlerClass.getModifiedDate();
//        ApiInterface apiInterface = RetrofitClass.getAPIService();
//        apiInterface.catalogData(userId, modifiedDate).enqueue(new Callback<MobileVerificationResponseModel>() {
//            @Override
//            public void onResponse(Call<MobileVerificationResponseModel> call, Response<MobileVerificationResponseModel> response) {
//                Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.LOCATION_LIST_URL + " Response :" + response.body());
//                MobileVerificationResponseModel model = response.body();
//                if (model != null) {
        Gson gson = new Gson();
        MobileVerificationResponseModel model = gson.fromJson(catalogDummyJson, MobileVerificationResponseModel.class);
                    insertDataIntoCatalogTable(model.getCatalogueDetailsModel());
//                } else {
//                    errorMessage.setValue("Something went wrong");
//                    showProgresBar.setValue(false);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<MobileVerificationResponseModel> call, Throwable t) {
//                Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.LOCATION_LIST_URL + " Response :" + t.getMessage());
//                errorMessage.setValue(t.getMessage());
//                showProgresBar.setValue(false);
//
//            }
//        });
    }

    private void insertDataIntoCatalogTable(List<CatalogueDetailsModel> catalogueDetailsModel) {
//        if (catalogueDetailsModel != null && !catalogueDetailsModel.isEmpty()) {
            insertLong.setValue(databaseHandlerClass.insertDataToCatalogueTable(catalogueDetailsModel));
//        } else {
//            dataInserted.setValue(true);
//        }
    }

    public MutableLiveData<Long> getInsertLong() {
        return insertLong;
    }

    public MutableLiveData<Boolean> getDataInserted() {
        return dataInserted;
    }


    public void onHomeClick() {
        if (!homeClick.getValue()) {
            homeClick.setValue(true);
            groupsClick.setValue(false);
            unitsClick.setValue(false);
        }
    }


    public void onUnitsClick() {
        if (!unitsClick.getValue()) {
            homeClick.setValue(false);
            unitsClick.setValue(true);
            groupsClick.setValue(false);
        }
    }


    public void onGroupsClick() {
        if (!groupsClick.getValue()) {
            homeClick.setValue(false);
            groupsClick.setValue(true);
            unitsClick.setValue(false);
        }
    }

    public void onSchoolsClick() {
        if (!groupsClick.getValue()) {
            homeClick.setValue(false);
            groupsClick.setValue(true);
            unitsClick.setValue(false);
        }
    }


    public MutableLiveData<List<CatalogueDetailsModel>> getCatalogData(String parentId) {
        showProgresBar.setValue(false);
        modelForCatalog = databaseHandlerClass.getCatalogData(parentId);
//        if (modelForCatalog!=null && !modelForCatalog.getValue().isEmpty()) {
//            if (modelForCatalog.getValue().get(0).getOrder() == 1) {
//                title.setValue("Units");
//            } else {
//                title.setValue(modelForCatalog.getValue().get(0).getName());
//            }
//            if (parentId.isEmpty()) {
//                title.setValue(context.getResources().getString(R.string.units));
//            } else {
//                title.setValue(modelForCatalog.getValue().get(0).getName());
//            }
//        }
        return modelForCatalog;
    }
}
