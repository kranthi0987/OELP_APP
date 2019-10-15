package mahiti.org.oelp.services;

import android.content.Context;

import org.json.JSONObject;

import mahiti.org.oelp.database.DAOs.CatalogDao;
import mahiti.org.oelp.database.DAOs.ChoicesDao;
import mahiti.org.oelp.database.DAOs.GroupDao;
import mahiti.org.oelp.database.DAOs.LocationDao;
import mahiti.org.oelp.database.DAOs.MediaContentDao;
import mahiti.org.oelp.database.DAOs.QuestionDao;
import mahiti.org.oelp.database.DAOs.SurveyResponseDao;
import mahiti.org.oelp.database.DAOs.TeacherDao;
import mahiti.org.oelp.database.DBConstants;
import mahiti.org.oelp.database.DatabaseHandlerClass;
import mahiti.org.oelp.models.LocationModel;
import mahiti.org.oelp.models.MobileVerificationResponseModel;
import mahiti.org.oelp.utils.AppUtils;
import mahiti.org.oelp.utils.Constants;
import mahiti.org.oelp.utils.Logger;
import mahiti.org.oelp.utils.MySharedPref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by RAJ ARYAN on 2019-10-03.
 */
public class FetchUpdateddata {

    private static final String TAG = FetchUpdateddata.class.getSimpleName();
    private CatalogDao catalogDao;
    private GroupDao groupDao;
    private TeacherDao teacherDao;
    private QuestionDao questionDao;
    private ChoicesDao choicesDao;
    private MediaContentDao mediaContentDao;
    private SurveyResponseDao surveyResponseDao;
    private LocationDao locationDao;
    private DatabaseHandlerClass databaseHandlerClass;
    private Context mContext;
    private MySharedPref sharedPref;

    public FetchUpdateddata(Context mContext) {
        this.mContext = mContext;


        sharedPref = new MySharedPref(mContext);
        int userType = sharedPref.readInt(Constants.USER_TYPE, Constants.USER_TEACHER);
        String userUUID = sharedPref.readString(Constants.USER_ID, "");

        catalogDao = new CatalogDao(mContext);
        groupDao = new GroupDao(mContext);
        teacherDao = new TeacherDao(mContext);
        questionDao = new QuestionDao(mContext);
        choicesDao = new ChoicesDao(mContext);
        mediaContentDao = new MediaContentDao(mContext);
        surveyResponseDao = new SurveyResponseDao(mContext);
        locationDao = new LocationDao(mContext);
        databaseHandlerClass = new DatabaseHandlerClass(mContext);

        callGroupApi(userUUID);
        callMediaSharedApi(userUUID, sharedPref.readString(Constants.GROUP_UUID_LIST,""));
        callTeacherApi(userUUID);
        callApiForLocation();
        if (userType == Constants.USER_TEACHER)
            callSubmittedAnswerApi(userUUID);


    }

    private String getGroupUUID() {
        String userGroup = "";
        String userData = sharedPref.readString(Constants.GROUP_UUID_LIST, "");
        if (!userData.isEmpty()) {
            try {
                JSONObject obj = new JSONObject(userData);
                userGroup = obj.getString("user_group");
            } catch (Exception ex) {
                Logger.logE(TAG, ex.getMessage(), ex);
            }
        }
        return userGroup;
    }

    private void callApiForLocation() {
        ApiInterface apiInterface = RetrofitClass.getAPIService();
        apiInterface.getLocationData().enqueue(new Callback<LocationModel>() {
            @Override
            public void onResponse(Call<LocationModel> call, Response<LocationModel> response) {
                Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.LOCATION_LIST_URL + " Response :" + response.body());
                LocationModel locationModel = response.body();
                if (locationModel != null) {
                    locationDao.insertLocationDataToDB(locationModel);
                }
            }

            @Override
            public void onFailure(Call<LocationModel> call, Throwable t) {
                Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.LOCATION_LIST_URL + " Response :" + t.getMessage());

            }
        });
    }


    private void callSubmittedAnswerApi(String userId) {
        String modifiedDate = databaseHandlerClass.getModifiedDate(DBConstants.CAT_TABLE_NAME);
        ApiInterface apiInterface = RetrofitClass.getAPIService();
        Logger.logD(TAG, "URL :" + RetrofitConstant.BASE_URL + RetrofitConstant.SUBMITTED_ANSWER_RESPONSE_URL + " Param : userId:" + userId + " modified_date:" + modifiedDate);
        apiInterface.getSubmittedAnswerResponse(userId, "").enqueue(new Callback<MobileVerificationResponseModel>() {
            @Override
            public void onResponse(Call<MobileVerificationResponseModel> call, Response<MobileVerificationResponseModel> response) {
                Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.SUBMITTED_ANSWER_RESPONSE_URL + " Response :" + response.body());
                MobileVerificationResponseModel model = response.body();
                if (model != null && model.getResponsesData() != null) {
                    surveyResponseDao.insertAnsweredQuestion(model.getResponsesData());
                }
            }

            @Override
            public void onFailure(Call<MobileVerificationResponseModel> call, Throwable t) {
                Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.SUBMITTED_ANSWER_RESPONSE_URL + " Response :" + t.getMessage());
            }
        });
    }

    private void callMediaSharedApi(String userId, String usergroup1) {
        if (usergroup1.isEmpty() || usergroup1.equalsIgnoreCase("[]"))
            return;

        String usergroup = AppUtils.makeJsonArray(usergroup1);
        if (usergroup.isEmpty())
            return;

        ApiInterface apiInterface = RetrofitClass.getAPIService();
        Logger.logD(TAG, "URL :" + RetrofitConstant.BASE_URL + RetrofitConstant.FETCH_MEDIA_SHARED + " Param : user_uuid:" + userId+" group_uuid "+usergroup);
        apiInterface.getMediaShared(userId, usergroup).enqueue(new Callback<MobileVerificationResponseModel>() {
            @Override
            public void onResponse(Call<MobileVerificationResponseModel> call, Response<MobileVerificationResponseModel> response) {
                if (response.body() != null) {
                    mediaContentDao.insertSharedData(response.body().getData());
                }
                Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.FETCH_MEDIA_SHARED + " Response :" + response.body());
            }

            @Override
            public void onFailure(Call<MobileVerificationResponseModel> call, Throwable t) {
                Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.FETCH_MEDIA_SHARED + " Response :" + t.getMessage());
            }
        });
    }

    private void callChoicesApi(String userId) {

        String modifiedDate = "";
        ApiInterface apiInterface = RetrofitClass.getAPIService();
        Logger.logD(TAG, "URL :" + RetrofitConstant.BASE_URL + RetrofitConstant.QUESTION_CHOICES_LIST_URL + " Param : userId:" + userId + " modified_date:" + modifiedDate);
        apiInterface.getQuestionChoicesList(userId, modifiedDate).enqueue(new Callback<MobileVerificationResponseModel>() {
            @Override
            public void onResponse(Call<MobileVerificationResponseModel> call, Response<MobileVerificationResponseModel> response) {
                if (response.body() != null) {
                    choicesDao.insertDatatoQuestionChoicesTable(response.body().getQuestionChoicesModelList());
                    Logger.logD(TAG, "URL :" + RetrofitConstant.BASE_URL + RetrofitConstant.QUESTION_CHOICES_LIST_URL + " Response :" + response.body().toString());
                }

            }

            @Override
            public void onFailure(Call<MobileVerificationResponseModel> call, Throwable t) {
                Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.QUESTION_CHOICES_LIST_URL + " Response :" + t.getMessage());
            }
        });
    }

    private void callQuestionApi(String userId) {
        String modifiedDate = "";
        ApiInterface apiInterface = RetrofitClass.getAPIService();
        Logger.logD(TAG, "URL :" + RetrofitConstant.BASE_URL + RetrofitConstant.QUESTION_LIST_URL + " Param : userId:" + userId + " modified_date:" + modifiedDate);
        apiInterface.getQuestionList(userId, modifiedDate).enqueue(new Callback<MobileVerificationResponseModel>() {
            @Override
            public void onResponse(Call<MobileVerificationResponseModel> call, Response<MobileVerificationResponseModel> response) {
                if (response.body() != null) {
                    questionDao.insertDatatoQuestionTable(response.body().getQuestionModelList());
                    Logger.logD(TAG, "URL :" + RetrofitConstant.BASE_URL + RetrofitConstant.QUESTION_LIST_URL + " Response :" + response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<MobileVerificationResponseModel> call, Throwable t) {
                Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.QUESTION_LIST_URL + " Response :" + t.getMessage());
            }
        });
    }

    private void callTeacherApi(String userId) {
        ApiInterface apiInterface = RetrofitClass.getAPIService();
        Logger.logD(TAG, "URL :" + RetrofitConstant.BASE_URL + RetrofitConstant.TEACHER_LIST_URL + " Param : userId:" + userId);
        apiInterface.getTeacherList(userId).enqueue(new Callback<MobileVerificationResponseModel>() {
            @Override
            public void onResponse(Call<MobileVerificationResponseModel> call, Response<MobileVerificationResponseModel> response) {
                Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.TEACHER_LIST_URL + " Response :" + response.body());
                MobileVerificationResponseModel model = response.body();
                if (model != null) {
                    teacherDao.insertTeacherDataToDB(model.getTeachers());
                }

            }

            @Override
            public void onFailure(Call<MobileVerificationResponseModel> call, Throwable t) {
                Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.TEACHER_LIST_URL + " Response :" + t.getMessage());
            }
        });
    }

    private void callGroupApi(String userId) {
        ApiInterface apiInterface = RetrofitClass.getAPIService();
        Logger.logD(TAG, "URL :" + RetrofitConstant.BASE_URL + RetrofitConstant.GROUP_LIST_URL + " Param : userId:" + userId);
        apiInterface.getGroupList(userId).enqueue(new Callback<MobileVerificationResponseModel>() {
            @Override
            public void onResponse(Call<MobileVerificationResponseModel> call, Response<MobileVerificationResponseModel> response) {
                Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.GROUP_LIST_URL + " Response :" + response.body());
                MobileVerificationResponseModel model = response.body();
                if (model != null) {
                    groupDao.insertDataToGroupsTable(model.getGroups());
                }


            }

            @Override
            public void onFailure(Call<MobileVerificationResponseModel> call, Throwable t) {
                Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.GROUP_LIST_URL + " Response :" + t.getMessage());
                String groupUUIDString = getGroupUUID();
                callMediaSharedApi(userId, groupUUIDString);
            }
        });
    }

    private void callCatalogApi(String userId) {
        String modifiedDate = databaseHandlerClass.getModifiedDate(DBConstants.CAT_TABLE_NAME);
        ApiInterface apiInterface = RetrofitClass.getAPIService();
        Logger.logD(TAG, "URL :" + RetrofitConstant.BASE_URL + RetrofitConstant.CATALOGUE_URL + " Param : userId:" + userId + " modified_date:" + modifiedDate);
        apiInterface.catalogData(userId, "").enqueue(new Callback<MobileVerificationResponseModel>() {
            @Override
            public void onResponse(Call<MobileVerificationResponseModel> call, Response<MobileVerificationResponseModel> response) {
                Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.CATALOGUE_URL + " Response :" + response.body());
                MobileVerificationResponseModel model = response.body();
                if (model != null) {
                    catalogDao.insertDataToCatalogueTable(model.getCatalogueDetailsModel());

                }
            }

            @Override
            public void onFailure(Call<MobileVerificationResponseModel> call, Throwable t) {
                Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.CATALOGUE_URL + " Response :" + t.getMessage());
            }
        });
    }
}
