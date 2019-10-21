package mahiti.org.oelp.services;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import mahiti.org.oelp.R;
import mahiti.org.oelp.database.DBConstants;
import mahiti.org.oelp.interfaces.FetchDataFromApiListener;
import mahiti.org.oelp.models.MobileVerificationResponseModel;
import mahiti.org.oelp.utils.Constants;
import mahiti.org.oelp.utils.Logger;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by RAJ ARYAN on 14/09/19.
 */
public class CallAPIServicesData {

    private Context mContetx;
    private static final String TAG = CallAPIServicesData.class.getSimpleName();
    FetchDataFromApiListener listener;

    public CallAPIServicesData(Context context) {
        mContetx = context;
        listener = (FetchDataFromApiListener) context;
    }

    public void getSharedMediaList(String userUUID) {
        /*final MobileVerificationResponseModel[] model = {null};
        ApiInterface apiInterface = RetrofitClass.getAPIService();
        Logger.logD(TAG, "URL :" + RetrofitConstant.BASE_URL + RetrofitConstant.FETCH_MEDIA_SHARED + " Param : user_uuid:" + userUUID);
        apiInterface.getMediaShared(userUUID).enqueue(new Callback<MobileVerificationResponseModel>() {
            @Override
            public void onResponse(Call<MobileVerificationResponseModel> call, Response<MobileVerificationResponseModel> response) {
                Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.FETCH_MEDIA_SHARED + " Response :" + response.body());
                listener.onFetchDataFromApi(response.body(), "media");


            }

            @Override
            public void onFailure(Call<MobileVerificationResponseModel> call, Throwable t) {
                Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.FETCH_MEDIA_SHARED + " Response :" + t.getMessage());
                MobileVerificationResponseModel model1 = new MobileVerificationResponseModel();
                model1.setMessage(t.getMessage());
                model1.setStatus(Constants.Api_FAilure);
                listener.onFetchDataFromApi(model1, "media");
            }
        });*/
    }

    public List<String> shareMediaGlobally(String userUUID, String data) {
        /*List<String> responseList = new ArrayList<>();
        final MobileVerificationResponseModel[] model = {null};
        ApiInterface apiInterface = RetrofitClass.getAPIService();
        Logger.logD(TAG, "URL :" + RetrofitConstant.BASE_URL + RetrofitConstant.SHARED_MEDIA_GLOBALLY + " Param : user_uuid:" + userUUID);
        apiInterface.shareMediaGlobally(userUUID, data).enqueue(new Callback<MobileVerificationResponseModel>() {
            @Override
            public void onResponse(Call<MobileVerificationResponseModel> call, Response<MobileVerificationResponseModel> response) {
                Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.SHARED_MEDIA_GLOBALLY + " Response :" + response.body());
               listener.onFetchDataFromApi(response.body(), "global");


            }

            @Override
            public void onFailure(Call<MobileVerificationResponseModel> call, Throwable t) {
                Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.SHARED_MEDIA_GLOBALLY + " Response :" + t.getMessage());
                MobileVerificationResponseModel model1 = new MobileVerificationResponseModel();
                model1.setMessage(t.getMessage());
                model1.setStatus(0);
                listener.onFetchDataFromApi(model1, "global");
            }
        });
        return responseList;*/
        return null;
    }
}
