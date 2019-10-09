package mahiti.org.oelp.services;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import mahiti.org.oelp.database.DAOs.MediaContentDao;
import mahiti.org.oelp.database.DAOs.SurveyResponseDao;
import mahiti.org.oelp.models.MobileVerificationResponseModel;
import mahiti.org.oelp.models.SharedMediaModel;
import mahiti.org.oelp.models.SubmittedAnswerResponse;
import mahiti.org.oelp.utils.Constants;
import mahiti.org.oelp.utils.Logger;
import mahiti.org.oelp.utils.MySharedPref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by RAJ ARYAN on 2019-10-03.
 */
public class SyncingUserData {

    private static final String TAG = SyncingUserData.class.getSimpleName();
    private Context mContext;
    private MediaContentDao mediaContentDao;
    private SurveyResponseDao surveyResponseDao;
    private String userUUID;

    public SyncingUserData(Context mContext) {
        this.mContext = mContext;
        mediaContentDao = new MediaContentDao(mContext);
        surveyResponseDao = new SurveyResponseDao(mContext);
        MySharedPref mySharedPref = new MySharedPref(mContext);
        userUUID = mySharedPref.readString(Constants.USER_ID, "");
    }

    public void uploadMedia() {

        List<SharedMediaModel> sharedMediaModelList = mediaContentDao.fetchSharedMedia("", false, 1);

        if (sharedMediaModelList != null && !sharedMediaModelList.isEmpty()) {
            for (SharedMediaModel sharedMediaModel : sharedMediaModelList) {

                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams paramMap = new RequestParams();
                paramMap.put("user_uuid", userUUID);
                paramMap.put("media_uuid", sharedMediaModel.getMediaUuid());
                paramMap.put("group_uuid", sharedMediaModel.getGroupUuid());
                paramMap.put("media_type", sharedMediaModel.getMediaType());
                paramMap.put("media_title", sharedMediaModel.getMediaTitle());
                paramMap.put("submission_time", sharedMediaModel.getSubmissionTime());
                try {
                    paramMap.put("media_file", new File(sharedMediaModel.getMediaFile()));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }


                client.post(mContext, RetrofitConstant.BASE_URL + RetrofitConstant.UPLAOD_MEDIA_SHARED, paramMap, new AsyncHttpResponseHandler() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        Toast.makeText(mContext, "Media Shared Success", Toast.LENGTH_SHORT).show();
                        mediaContentDao.updateSyncData(sharedMediaModel.getMediaUuid());


                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                        Toast.makeText(mContext, "Media Shared Failure", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onRetry(int retryNo) {
                        Log.d("Tag", "retry");
                    }
                });


            }
        }


    }

    public void shareMediaGlobally() {
        String globalShareData = mediaContentDao.fetchGlobalSharedMedia();
        if (!globalShareData.isEmpty()) {
            ApiInterface apiInterface = RetrofitClass.getAPIService();
            Logger.logD(TAG, "URL :" + RetrofitConstant.BASE_URL + RetrofitConstant.SHARED_MEDIA_GLOBALLY + " Param : user_uuid:" + userUUID);
            apiInterface.shareMediaGlobally(userUUID, globalShareData).enqueue(new Callback<MobileVerificationResponseModel>() {
                @Override
                public void onResponse(Call<MobileVerificationResponseModel> call, Response<MobileVerificationResponseModel> response) {
                    Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.SHARED_MEDIA_GLOBALLY + " Response :" + response.body());
                }

                @Override
                public void onFailure(Call<MobileVerificationResponseModel> call, Throwable t) {
                    Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.SHARED_MEDIA_GLOBALLY + " Response :" + t.getMessage());
                }
            });
        }
    }

    public void postQA() {
        List<SubmittedAnswerResponse> arrayList = surveyResponseDao.fetchAnsweredQuestion("", 1);
        if (arrayList != null && !arrayList.isEmpty()) {
            for (SubmittedAnswerResponse model : arrayList) {
                String response = "";
                Gson gson = new Gson();
                if (model.getResponse() != null) {
                    try {
                        response = gson.toJson(model.getResponse());
                    } catch (Exception ex) {
                        Logger.logE(TAG, "Exception in model to json :" + ex.getMessage(), ex);
                    }
                }

                ApiInterface apiService = RetrofitClass.getAPIService();
                String userId = new MySharedPref(mContext).readString(Constants.USER_ID, "");
                Call<MobileVerificationResponseModel> call = apiService.submitAnswer(userId,
                        model.getCreationKey(),
                        model.getSectionUUID(),
                        model.getUnitUUID(),
                        model.getSubmissionDate(),
                        model.getMediacontent(),
                        model.getScore(),
                        model.getAttempts(),
                        response);
                Logger.logD(TAG, "QUESTION_AND_ANSWER_URL : " + RetrofitConstant.BASE_URL + RetrofitConstant.SUBMIT_ANSWER);
                call.enqueue(new Callback<MobileVerificationResponseModel>() {
                    @Override
                    public void onResponse(Call<MobileVerificationResponseModel> call, Response<MobileVerificationResponseModel> response) {
                        surveyResponseDao.updateSyncStatus(model.getCreationKey());
                    }

                    @Override
                    public void onFailure(Call<MobileVerificationResponseModel> call, Throwable t) {
                    }
                });
            }
        }

    }

    public List<String> deleteMedia(String userUUID, String deleteData) {
        List<String> responseList = new ArrayList<>();
        final MobileVerificationResponseModel[] model = {null};
        ApiInterface apiInterface = RetrofitClass.getAPIService();
        Logger.logD(TAG, "URL :" + RetrofitConstant.BASE_URL + RetrofitConstant.SHARED_MEDIA_GLOBALLY + " Param : user_uuid:" + userUUID);
        apiInterface.deleteMedia(userUUID, deleteData).enqueue(new Callback<MobileVerificationResponseModel>() {
            @Override
            public void onResponse(Call<MobileVerificationResponseModel> call, Response<MobileVerificationResponseModel> response) {
                Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.SHARED_MEDIA_GLOBALLY + " Response :" + response.body());


            }

            @Override
            public void onFailure(Call<MobileVerificationResponseModel> call, Throwable t) {
                Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.SHARED_MEDIA_GLOBALLY + " Response :" + t.getMessage());
                MobileVerificationResponseModel model1 = new MobileVerificationResponseModel();
                model1.setMessage(t.getMessage());
                model1.setStatus(0);
            }
        });
        return responseList;
    }


}
