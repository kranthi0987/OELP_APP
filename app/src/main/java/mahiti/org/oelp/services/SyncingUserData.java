package mahiti.org.oelp.services;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import mahiti.org.oelp.database.DAOs.MediaContentDao;
import mahiti.org.oelp.database.DAOs.SurveyResponseDao;
import mahiti.org.oelp.database.DBConstants;
import mahiti.org.oelp.models.MobileVerificationResponseModel;
import mahiti.org.oelp.models.SharedMediaModel;
import mahiti.org.oelp.models.SubmittedAnswerResponse;
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
public class SyncingUserData {

    private static final String TAG = SyncingUserData.class.getSimpleName();
    private final MySharedPref mySharedPref;
    private Context mContext;
    private MediaContentDao mediaContentDao;
    private SurveyResponseDao surveyResponseDao;
    private String userUUID;

    public SyncingUserData(Context mContext) {
        this.mContext = mContext;
        mediaContentDao = new MediaContentDao(mContext);
        surveyResponseDao = new SurveyResponseDao(mContext);
        mySharedPref = new MySharedPref(mContext);
        userUUID = mySharedPref.readString(Constants.USER_ID, "");
    }

    public void uploadMedia() {

        List<SharedMediaModel> sharedMediaModelList = mediaContentDao.fetchSharedMedia("", "", false, 1);

        if (sharedMediaModelList != null && !sharedMediaModelList.isEmpty()) {
            for (SharedMediaModel sharedMediaModel : sharedMediaModelList) {

                if (sharedMediaModel.getMediaUuid() != null || !sharedMediaModel.getMediaUuid().isEmpty()) {


                    AsyncHttpClient client = new AsyncHttpClient();
                    RequestParams paramMap = new RequestParams();
                    paramMap.put("user_uuid", userUUID);
                    paramMap.put("media_uuid", sharedMediaModel.getMediaUuid());
                    paramMap.put("group_uuid", sharedMediaModel.getGroupUuid());
                    paramMap.put("media_type", sharedMediaModel.getMediaType());
                    paramMap.put("media_title", sharedMediaModel.getMediaTitle());
                    paramMap.put("submission_time", sharedMediaModel.getSubmissionTime());
                    try {
                        int mediaType = AppUtils.getFileType(sharedMediaModel.getMediaFile());
                        File basepath =null;
                        if (mediaType==Constants.IMAGE){
                           basepath = AppUtils.completePathInSDCard(Constants.IMAGE) ;
                        }else {
                            basepath = AppUtils.completePathInSDCard(Constants.VIDEO) ;
                        }
                        File f = new File(basepath, AppUtils.getFileName(sharedMediaModel.getMediaFile()));
                        paramMap.put("media_file", f);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        continue;
                    }


                    client.post(mContext, RetrofitConstant.BASE_URL + RetrofitConstant.UPLAOD_MEDIA_SHARED, paramMap, new AsyncHttpResponseHandler() {
                        @Override
                        public void onStart() {
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                            /*Toast.makeText(mContext, "Media Shared Success", Toast.LENGTH_SHORT).show();*/
                            mediaContentDao.updateSyncData(sharedMediaModel.getMediaUuid(), DBConstants.SYNC_STATUS);
                            mySharedPref.writeBoolean(Constants.MEDIACONTENTCHANGE, false);


                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                            Logger.logE(TAG, e.getMessage(), null);
//                        mediaContentDao.updateSyncData(sharedMediaModel.getMediaUuid(), DBConstants.SYNC_STATUS);
                            /*Toast.makeText(mContext, "Media Shared Failure", Toast.LENGTH_SHORT).show();*/

                        }

                        @Override
                        public void onRetry(int retryNo) {
                            Log.d("Tag", "retry");
                        }
                    });


                }
            }
        }
    }

    public void shareMediaGlobally() {
        String globalShareData = mediaContentDao.fetchGlobalSharedMedia();

        Logger.logD(TAG, "Global Share data :" + globalShareData);

        if (!globalShareData.isEmpty()) {
            ApiInterface apiInterface = RetrofitClass.getAPIService();
            Logger.logD(TAG, "URL :" + RetrofitConstant.BASE_URL + RetrofitConstant.SHARED_MEDIA_GLOBALLY + " Param : user_uuid:" + userUUID);
            apiInterface.shareMediaGlobally(userUUID, globalShareData).enqueue(new Callback<MobileVerificationResponseModel>() {
                @Override
                public void onResponse(Call<MobileVerificationResponseModel> call, Response<MobileVerificationResponseModel> response) {
                    Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.SHARED_MEDIA_GLOBALLY + " Response :" + response.body());
                    changeToArray(globalShareData, 0);
                    mySharedPref.writeBoolean(Constants.GLOBALSHARECHANGE, false);
                }

                @Override
                public void onFailure(Call<MobileVerificationResponseModel> call, Throwable t) {
                    Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.SHARED_MEDIA_GLOBALLY + " Response :" + t.getMessage());
                }
            });
        }
    }

    /**
     * @param globalShareData JSON String
     * @param type            0- update and 1 delete
     */
    private void changeToArray(String globalShareData, int type) {
        try {
            JSONArray jsonArray = new JSONArray(globalShareData);
            for (int a = 0; a < jsonArray.length(); a++) {
                JSONObject obj = jsonArray.getJSONObject(a);
                String mediaUUID = obj.optString("media_uuid");
                if (type == 0)
                    mediaContentDao.updateSyncData(mediaUUID, DBConstants.SHARED_GLOBALLY_SYNC_STATUS);
                else if (type == 1)
                    mediaContentDao.removeDeleteMedia(mediaUUID);
            }
        } catch (Exception ex) {
            Logger.logE(TAG, ex.getMessage(), ex);
        }
    }

    public void postQA() {
        List<SubmittedAnswerResponse> arrayList = surveyResponseDao.fetchAnsweredQuestion("", 1);
        if (arrayList != null && !arrayList.isEmpty()) {
            for (SubmittedAnswerResponse model : arrayList) {
                /*String response = "[]";
                Gson gson = new Gson();
                if (model.getResponse() != null) {
                    try {
                        response = gson.toJson(model.getResponse());
                    } catch (Exception ex) {
                        Logger.logE(TAG, "Exception in model to json :" + ex.getMessage(), ex);
                    }
                }*/

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
                        model.getServerString());
                Logger.logD(TAG, "QUESTION_AND_ANSWER_URL : " + RetrofitConstant.BASE_URL + RetrofitConstant.SUBMIT_ANSWER);
                call.enqueue(new Callback<MobileVerificationResponseModel>() {
                    @Override
                    public void onResponse(Call<MobileVerificationResponseModel> call, Response<MobileVerificationResponseModel> response) {
                        if (response.body().getStatus()==2) {
                            surveyResponseDao.updateSyncStatus(model.getCreationKey());
                            mySharedPref.writeBoolean(Constants.QACHANGED, false);
                        }
                    }

                    @Override
                    public void onFailure(Call<MobileVerificationResponseModel> call, Throwable t) {
                    }
                });
            }
        }

    }

    public void deleteMedia() { /// send comma separated uuid
        String deleteMediaData = mediaContentDao.fetchDeletedMedia();
        if (deleteMediaData.isEmpty() || deleteMediaData.equalsIgnoreCase("[]"))
            return;

        Logger.logD(TAG, "Delete data :" + deleteMediaData);

        ApiInterface apiInterface = RetrofitClass.getAPIService();
        Logger.logD(TAG, "URL :" + RetrofitConstant.BASE_URL + RetrofitConstant.SHARED_MEDIA_GLOBALLY + " Param : user_uuid:" + userUUID);
        apiInterface.deleteMedia(deleteMediaData).enqueue(new Callback<MobileVerificationResponseModel>() {
            @Override
            public void onResponse(Call<MobileVerificationResponseModel> call, Response<MobileVerificationResponseModel> response) {
                Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.SHARED_MEDIA_GLOBALLY + " Response :" + response.body());
                changeToArray(deleteMediaData, 1);
                mySharedPref.writeBoolean(Constants.DELETEDATACHANGE, false);
            }

            @Override
            public void onFailure(Call<MobileVerificationResponseModel> call, Throwable t) {
                Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.SHARED_MEDIA_GLOBALLY + " Response :" + t.getMessage());
                MobileVerificationResponseModel model1 = new MobileVerificationResponseModel();
                model1.setMessage(t.getMessage());
                model1.setStatus(0);
            }
        });
    }


}
