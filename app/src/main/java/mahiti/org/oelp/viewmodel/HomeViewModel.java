package mahiti.org.oelp.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import mahiti.org.oelp.R;
import mahiti.org.oelp.database.DAOs.TeacherDao;
import mahiti.org.oelp.database.DBConstants;
import mahiti.org.oelp.database.DatabaseHandlerClass;
import mahiti.org.oelp.fileandvideodownloader.FileModel;
import mahiti.org.oelp.models.CatalogueDetailsModel;
import mahiti.org.oelp.models.GroupModel;
import mahiti.org.oelp.models.MobileVerificationResponseModel;
import mahiti.org.oelp.models.QuestionAnswerIdModel;
import mahiti.org.oelp.models.QuestionChoicesModel;
import mahiti.org.oelp.models.QuestionModel;
import mahiti.org.oelp.models.SubmittedAnswerResponse;
import mahiti.org.oelp.models.TeacherModel;
import mahiti.org.oelp.services.ApiInterface;
import mahiti.org.oelp.services.RetrofitClass;
import mahiti.org.oelp.services.RetrofitConstant;
import mahiti.org.oelp.utils.AppUtils;
import mahiti.org.oelp.utils.CheckNetwork;
import mahiti.org.oelp.utils.Constants;
import mahiti.org.oelp.utils.Logger;
import mahiti.org.oelp.utils.MySharedPref;
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
    private final boolean submittedAnswerResponseCalled;
    private final boolean questionApiCalled;
    private final boolean questionChoicesApiCalled;
    private final boolean groupApiCalled;
    private final boolean teacherApiCalled;
    //    public MutableLiveData<Boolean> homeClick = new MutableLiveData<>();
    public MutableLiveData<Boolean> unitsClick = new MutableLiveData<>();
    public MutableLiveData<Boolean> groupsClick = new MutableLiveData<>();
    public MutableLiveData<Integer> userType = new MutableLiveData<>();
    public MutableLiveData<Boolean> moduleCompleted = new MutableLiveData<>();
    public MutableLiveData<List<CatalogueDetailsModel>> catalogDataList = new MutableLiveData<>();
    public MutableLiveData<String> title = new MutableLiveData<>();
    private MutableLiveData<String> apiErrorMessage = new MutableLiveData<>();
    private MutableLiveData<Integer> dataInserted = new MutableLiveData<>();
    private MutableLiveData<List<FileModel>> listToDownloadImage = new MutableLiveData<>();

    private MySharedPref sharedPref;
    private DatabaseHandlerClass databaseHandlerClass;
    public MutableLiveData<String> parentId = new MutableLiveData<>();
    public List<CatalogueDetailsModel> modelForCatalog = new ArrayList<>();
    public MutableLiveData<Integer> apiCountMutable = new MutableLiveData<>();
    public int apiCount = 0;
    public MutableLiveData<Long> insertLong = new MutableLiveData();

    public HomeViewModel(@NonNull Application application) {
        super(application);
        context = application;
        sharedPref = new MySharedPref(application);
        databaseHandlerClass = new DatabaseHandlerClass(application);
        userId = sharedPref.readString(Constants.USER_ID, "");
        teacherLogin = sharedPref.readInt(Constants.USER_TYPE, Constants.USER_TEACHER) == Constants.USER_TEACHER;
        catalogApiCalled = sharedPref.readString(RetrofitConstant.CATALOGUE_URL, "").equalsIgnoreCase(AppUtils.getDate());
        submittedAnswerResponseCalled = sharedPref.readBoolean(RetrofitConstant.SUBMITTED_ANSWER_RESPONSE_URL, false);
        questionApiCalled = sharedPref.readString(RetrofitConstant.QUESTION_LIST_URL, "").equalsIgnoreCase(AppUtils.getDate());
        questionChoicesApiCalled = sharedPref.readString(RetrofitConstant.QUESTION_CHOICES_LIST_URL, "").equalsIgnoreCase(AppUtils.getDate());
        groupApiCalled = sharedPref.readString(RetrofitConstant.GROUP_LIST_URL, "").equalsIgnoreCase(AppUtils.getDate()) && !sharedPref.readBoolean(Constants.IS_UPDATED, true);
        teacherApiCalled = sharedPref.readString(RetrofitConstant.TEACHER_LIST_URL, "").equalsIgnoreCase(AppUtils.getDate());
        dataInserted.setValue(null);
        /*if (CheckNetwork.checkNet(context)) {
            callAllAPI();
            apiCountMutable.setValue(0);
            Logger.logD("TAG", "API COUNT :" + apiCountMutable.getValue());
        }*/
//        getListOfImageFromDb();
        callApiForGroupList(userId);
        callApiForTeacherList(userId);
        getListOfImageFromDb();

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


        callApiForGroupList(userId);


        callApiForTeacherList(userId);

        if (!submittedAnswerResponseCalled)
            callApiForSubmittedAnswerResponse(userId);
    }

    private void callApiForTeacherList(String userId) {
//        apiCountMutable.setValue(1);
        apiCountMutable.setValue(1);
        Logger.logD("TAG", "API COUNT :" + apiCountMutable.getValue());
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
//                    apiCountMutable.setValue(0);
                    apiCountMutable.setValue(0);
                    Logger.logD("TAG", "API COUNT :" + apiCountMutable.getValue());
                }

            }

            @Override
            public void onFailure(Call<MobileVerificationResponseModel> call, Throwable t) {
                Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.TEACHER_LIST_URL + " Response :" + t.getMessage());
                apiErrorMessage.setValue(t.getMessage());
//                apiCountMutable.setValue(0);
                apiCountMutable.setValue(0);
                Logger.logD("TAG", "API COUNT :" + apiCountMutable.getValue());
            }
        });
    }

    private void insertDataIntoTeacherTable(List<TeacherModel> teachers) {
        if (!teachers.isEmpty()) {
            new TeacherDao(context).insertTeacherDataToDB(teachers);
            sharedPref.writeString(RetrofitConstant.GROUP_LIST_URL, AppUtils.getDate());
            apiCountMutable.setValue(0);
            Logger.logD("TAG", "API COUNT :" + apiCountMutable.getValue());
        }
    }

    public void callApiForGroupList(String userId) {
//        apiCountMutable.setValue(1);
        apiCountMutable.setValue(0);
        Logger.logD("TAG", "API COUNT :" + apiCountMutable.getValue());
        ApiInterface apiInterface = RetrofitClass.getAPIService();
        Logger.logD(TAG, "URL :" + RetrofitConstant.BASE_URL + RetrofitConstant.GROUP_LIST_URL + " Param : userId:" + userId);
        apiInterface.getGroupList(userId).enqueue(new Callback<MobileVerificationResponseModel>() {
            @Override
            public void onResponse(Call<MobileVerificationResponseModel> call, Response<MobileVerificationResponseModel> response) {
                Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.GROUP_LIST_URL + " Response :" + response.body());
                sharedPref.writeBoolean(Constants.IS_UPDATED, false);
                MobileVerificationResponseModel model = response.body();
                if (model != null) {
                    insertDataIntoGroupTable(model.getGroups());
                } else {
                    apiErrorMessage.setValue(context.getResources().getString(R.string.SOMETHING_WRONG));
//                    apiCountMutable.setValue(0);
                    apiCountMutable.setValue(0);
                    Logger.logD("TAG", "API COUNT :" + apiCountMutable.getValue());
                }

            }

            @Override
            public void onFailure(Call<MobileVerificationResponseModel> call, Throwable t) {
                Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.GROUP_LIST_URL + " Response :" + t.getMessage());
                apiErrorMessage.setValue(t.getMessage());
//                apiCountMutable.setValue(0);
                apiCountMutable.setValue(0);
                Logger.logD("TAG", "API COUNT :" + apiCountMutable.getValue());
            }
        });

    }

    private void insertDataIntoGroupTable(List<GroupModel> groups) {
        if (!groups.isEmpty()) {
            insertLong.setValue(databaseHandlerClass.insertDatatoGroupsTable(groups));
            sharedPref.writeString(RetrofitConstant.GROUP_LIST_URL, AppUtils.getDate());
        }
        apiCountMutable.setValue(0);
        Logger.logD("TAG", "API COUNT :" + apiCountMutable.getValue());
    }


    public void setDataInserted(Integer dataInserted) {
        this.dataInserted.setValue(dataInserted);
    }

    private void callApiForQuestionChoices(String userId) {
        apiCountMutable.setValue(1);
        Logger.logD("TAG", "API COUNT :" + apiCountMutable.getValue());
        ;
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
                    apiCountMutable.setValue(0);
                    Logger.logD("TAG", "API COUNT :" + apiCountMutable.getValue());
                }

            }

            @Override
            public void onFailure(Call<MobileVerificationResponseModel> call, Throwable t) {
                apiErrorMessage.setValue(t.getMessage());
                Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.QUESTION_CHOICES_LIST_URL + " Response :" + t.getMessage());
                apiCountMutable.setValue(0);
                Logger.logD("TAG", "API COUNT :" + apiCountMutable.getValue());
            }
        });
    }

    private void insertQuestionChoicesToTable(List<QuestionChoicesModel> questionChoicesModelList) {
        if (!questionChoicesModelList.isEmpty()) {
            databaseHandlerClass.insertDatatoQuestionChoicesTable(questionChoicesModelList);
            sharedPref.writeString(RetrofitConstant.QUESTION_CHOICES_LIST_URL, AppUtils.getDate());
        }
        apiCountMutable.setValue(0);
        Logger.logD("TAG", "API COUNT :" + apiCountMutable.getValue());
    }

    public void getListOfImageFromDb() {
        List<FileModel> imageList = databaseHandlerClass.getImageListFromTable();
        if (!imageList.isEmpty()) {
            checkOfflineAvailable(imageList);
        } else {
            dataInserted.setValue(2);
        }
    }

    private void checkOfflineAvailable(List<FileModel> imageList) {
        List<FileModel> imagePathToRemove = new ArrayList<>();
        for (FileModel iconPath : imageList) {
            try {
//                File file = new File(AppUtils.completePathInSDCard(Constants.IMAGE), AppUtils.getFileName(iconPath.getFileUrl()));
                File file = new File(AppUtils.completeInternalStoragePath(context, Constants.IMAGE), AppUtils.getFileName(iconPath.getFileUrl()));
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
        apiCountMutable.setValue(1);
        Logger.logD("TAG", "API COUNT :" + apiCountMutable.getValue());
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
                    apiCountMutable.setValue(0);
                    Logger.logD("TAG", "API COUNT :" + apiCountMutable.getValue());
                    apiErrorMessage.setValue(context.getResources().getString(R.string.SOMETHING_WRONG));
                }

            }

            @Override
            public void onFailure(Call<MobileVerificationResponseModel> call, Throwable t) {
                apiCountMutable.setValue(0);
                Logger.logD("TAG", "API COUNT :" + apiCountMutable.getValue());
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
        apiCountMutable.setValue(0);
        Logger.logD("TAG", "API COUNT :" + apiCountMutable.getValue());

    }

    private void callApiForCatalogData(String userId) {
//        apiCountMutable.setValue(1);
        apiCountMutable.setValue(1);
        Logger.logD("TAG", "API COUNT :" + apiCountMutable.getValue());
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
                    apiCountMutable.setValue(0);
                    Logger.logD("TAG", "API COUNT :" + apiCountMutable.getValue());
                    getListOfImageFromDb();
                }

            }

            @Override
            public void onFailure(Call<MobileVerificationResponseModel> call, Throwable t) {
                Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.CATALOGUE_URL + " Response :" + t.getMessage());
                apiErrorMessage.setValue(t.getMessage());
                getListOfImageFromDb();
                apiCountMutable.setValue(0);
                Logger.logD("TAG", "API COUNT :" + apiCountMutable.getValue());
            }
        });
    }

    private void callApiForSubmittedAnswerResponse(String userId) {
//        getListOfImageFromDb();
        apiCountMutable.setValue(1);
        Logger.logD("TAG", "API COUNT :" + apiCountMutable.getValue());
        String modifiedDate = databaseHandlerClass.getModifiedDate(DBConstants.CAT_TABLE_NAME);
        ApiInterface apiInterface = RetrofitClass.getAPIService();
        Logger.logD(TAG, "URL :" + RetrofitConstant.BASE_URL + RetrofitConstant.CATALOGUE_URL + " Param : userId:" + userId + " modified_date:" + modifiedDate);
        apiInterface.getSubmittedAnswerResponse(userId, "").enqueue(new Callback<MobileVerificationResponseModel>() {
            @Override
            public void onResponse(Call<MobileVerificationResponseModel> call, Response<MobileVerificationResponseModel> response) {
                Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.CATALOGUE_URL + " Response :" + response.body());

                MobileVerificationResponseModel model = response.body();
                if (model != null) {
                    insertDataIntoQuestionAnswer(model.getResponsesData());

                } else {
                    apiErrorMessage.setValue(context.getResources().getString(R.string.SOMETHING_WRONG));
                    apiCountMutable.setValue(0);
                    Logger.logD("TAG", "API COUNT :" + apiCountMutable.getValue());
//                    getListOfImageFromDb();
                }

            }

            @Override
            public void onFailure(Call<MobileVerificationResponseModel> call, Throwable t) {
                Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.CATALOGUE_URL + " Response :" + t.getMessage());
                apiErrorMessage.setValue(t.getMessage());
//                getListOfImageFromDb();
                apiCountMutable.setValue(0);
                Logger.logD("TAG", "API COUNT :" + apiCountMutable.getValue());
            }
        });
    }

    private void insertDataIntoCatalogTable(List<CatalogueDetailsModel> catalogueDetailsModel) {
        if (catalogueDetailsModel != null && !catalogueDetailsModel.isEmpty()) {
            databaseHandlerClass.insertDataToCatalogueTable(catalogueDetailsModel);
            sharedPref.writeString(RetrofitConstant.CATALOGUE_URL, AppUtils.getDate());
            getListOfImageFromDb();
        }
        apiCountMutable.setValue(0);
        Logger.logD("TAG", "API COUNT :" + apiCountMutable.getValue());
    }

    /*public void insertAnsweredQuestion(String testUUID, String mediaUUID, String sectionUUID, String serverJSON, String previewJSON, String dateTime, List<Integer> score) {
     */

    private void insertDataIntoQuestionAnswer(List<SubmittedAnswerResponse> model1) {
        String testUUID;
        String mediaUUID;
        String sectionUUID;
        String unitUUID;
        String serverJSON;
        String dateTime;
        Integer attempt;
        List<String> score = new ArrayList<>();


        if (model1 != null && !model1.isEmpty()) {
            for (SubmittedAnswerResponse model : model1) {
                testUUID = model.getCreationKey();
                mediaUUID = model.getMediacontent();
                sectionUUID = model.getSectionUUID();
                unitUUID = model.getUnitUUID();
                serverJSON = convertModelToString(model.getResponse());
                dateTime = model.getSubmissionDate();
                score.add(String.valueOf(model.getScore()));
                score.add(String.valueOf(model.getResponse().size()));
                attempt = model.getAttempts();
                databaseHandlerClass.insertAnsweredQuestion(testUUID, mediaUUID, sectionUUID, unitUUID, serverJSON, dateTime, score, attempt, 1, "");

            }

            sharedPref.writeBoolean(RetrofitConstant.SUBMITTED_ANSWER_RESPONSE_URL, true);
        }
        apiCountMutable.setValue(0);
        Logger.logD("TAG", "API COUNT :" + apiCountMutable.getValue());

//        updateCatalogTable();
    }

    private void updateCatalogTable() {
        databaseHandlerClass.getAnsweredQuestion("", 2);
    }

    private String convertModelToString(List<QuestionAnswerIdModel> response) {
        String responseDat = "";
//        JSONArray idModel;
        Gson gson = new Gson();
//        if (response!=null && !response.isEmpty()) {
//            for (QuestionAnswerIdModel model : response){
        try {
            responseDat = gson.toJson(response);
        } catch (Exception ex) {
            Logger.logE(TAG, "Exception in converting model to json :" + ex.getMessage(), ex);
        }
//            }

//        }
        return responseDat;
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
        if (!unitsClick.getValue()) {
//                homeClick.setValue(false);
            unitsClick.setValue(true);
            groupsClick.setValue(false);
        }
    }


    public void onGroupsClick() {
        if (!groupsClick.getValue()) {
//              homeClick.setValue(false);
            unitsClick.setValue(false);
            groupsClick.setValue(true);
        }
    }


    public List<CatalogueDetailsModel> getCatalogData(String parentId) {
        modelForCatalog = databaseHandlerClass.getCatalogData(parentId);
        return modelForCatalog;
    }

    public List<GroupModel> getGroupList() {
        return databaseHandlerClass.getGroupList();
    }

}
