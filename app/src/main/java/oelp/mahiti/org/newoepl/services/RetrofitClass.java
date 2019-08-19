package oelp.mahiti.org.newoepl.services;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;

import oelp.mahiti.org.newoepl.models.MobileVerificationResponseModel;
import oelp.mahiti.org.newoepl.utils.MyOwnRuntime;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by sandeep HR on 20/12/18.
 */
public class RetrofitClass {


    private static Retrofit retrofit = null;
    private static RetrofitClass  retrofitClass = null;

    private RetrofitClass() {
//        throw new IllegalStateException("Retrofit Class");
    }

    public static RetrofitClass getRetrofitClass(){
        if (retrofitClass==null){
            retrofitClass = new RetrofitClass();
        }
        return retrofitClass;
    }


    public static Retrofit getRetroInstance() {
        OkHttpClient okHttpClient = null;
        try {
            okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();
        } catch (MyOwnRuntime e) {
            e.printStackTrace();
        }
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(RetrofitConstant.BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static ApiInterface getAPIService() {
        return getRetroInstance().create(ApiInterface.class);
    }

    public MutableLiveData<MobileVerificationResponseModel> getMobileVerified(String mobileNo){

        MutableLiveData<MobileVerificationResponseModel> model = new MutableLiveData<MobileVerificationResponseModel>();

        ApiInterface apiInterface = RetrofitClass.getAPIService();
        apiInterface.mobileValidation(mobileNo).enqueue(new Callback<MobileVerificationResponseModel>() {
            @Override
            public void onResponse(Call<MobileVerificationResponseModel> call, Response<MobileVerificationResponseModel> response) {
                if (response.body()!=null)
                    model.setValue(response.body());
                else
                    model.setValue(new MobileVerificationResponseModel(2, "Some Thing Went Wrong"));
            }

            @Override
            public void onFailure(Call<MobileVerificationResponseModel> call, Throwable t) {
               model.setValue(new MobileVerificationResponseModel(2, t.getMessage()));
            }
        });

        return model;

    }

    public MutableLiveData<MobileVerificationResponseModel> getOTPVerified(String mobileNo, String otpDigit, Context context){

        MutableLiveData<MobileVerificationResponseModel> model = new MutableLiveData<MobileVerificationResponseModel>();
//
//        ApiInterface apiInterface = RetrofitClass.getAPIService();
//        apiInterface.userRegistration(mobileNo, otpDigit).enqueue(new Callback<LiveData<MobileVerificationResponseModel>>() {
//            @Override
//            public void onResponse(Call<LiveData<MobileVerificationResponseModel>> call, Response<LiveData<MobileVerificationResponseModel>> response) {
//                if (response.body()!=null)
//                    model.setValue(response.body().getValue());
//            }
//
//            @Override
//            public void onFailure(Call<LiveData<MobileVerificationResponseModel>> call, Throwable t) {
//                model.setValue(new MobileVerificationResponseModel(2, t.getMessage()));
//            }
//        });

        return model;

    }

    public MutableLiveData<MobileVerificationResponseModel> changeMobileNumber(String currentMobileNo, String newMobileNo){

        MutableLiveData<MobileVerificationResponseModel> model = new MutableLiveData<MobileVerificationResponseModel>();

        ApiInterface apiInterface = RetrofitClass.getAPIService();
        apiInterface.changeMobileNumber(currentMobileNo, newMobileNo).enqueue(new Callback<MobileVerificationResponseModel>() {
            @Override
            public void onResponse(Call<MobileVerificationResponseModel> call, Response<MobileVerificationResponseModel> response) {
                if (response.body()!=null)
                    model.setValue(response.body());
            }

            @Override
            public void onFailure(Call<MobileVerificationResponseModel> call, Throwable t) {
                model.setValue(new MobileVerificationResponseModel(2, t.getMessage()));
            }
        });

        return model;

    }



}
