package mahiti.org.oelp.services;


import mahiti.org.oelp.models.LocationModel;
import mahiti.org.oelp.models.MobileVerificationResponseModel;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiInterface {

    @FormUrlEncoded
    @POST(RetrofitConstant.MOBILE_VALIDATION_URL)
    Call<MobileVerificationResponseModel> mobileValidation(@Field("mobile_number") String mobileNumber);

    @FormUrlEncoded
    @POST(RetrofitConstant.USER_REGISTRATION_URL)
    Call<MobileVerificationResponseModel> userRegistration(@Field("userDetails") String userDetails);

    @FormUrlEncoded
    @POST(RetrofitConstant.OTP_VALIDATION_URL)
    Call<MobileVerificationResponseModel> otpValidation(@Field("otp") String otp, @Field("mobile_number") String mobileNumber);

    @FormUrlEncoded
    @POST(RetrofitConstant.CHANGE_MOBILE_NO_URL)
    Call<MobileVerificationResponseModel> changeMobileNumber(@Field("current_mobile_number") String currentMobileNo, @Field("new_mobile_number") String newMobileNo);

    @GET(RetrofitConstant.LOCATION_LIST_URL)
    Call<LocationModel> getLocationData();

    @FormUrlEncoded
    @POST(RetrofitConstant.CATALOGUE_URL)
    Call<MobileVerificationResponseModel> catalogData(@Field("user_uuid") String userId, @Field("modified_date") String modifiedDate);

    @FormUrlEncoded
    @POST(RetrofitConstant.GROUP_LIST_URL)
    Call<MobileVerificationResponseModel> getGroupList(@Field("user_uuid") String userId);

    @FormUrlEncoded
    @POST(RetrofitConstant.TEACHER_LIST_URL)
    Call<MobileVerificationResponseModel> getTeacherList(@Field("user_uuid") String userId);


    @FormUrlEncoded
    @POST(RetrofitConstant.QUESTION_LIST_URL)
    Call<MobileVerificationResponseModel> getQuestionList(@Field("userId") String userId, @Field("modified_date") String modifiedDate);

    @FormUrlEncoded
    @POST(RetrofitConstant.QUESTION_CHOICES_LIST_URL)
    Call<MobileVerificationResponseModel> getQuestionChoicesList(@Field("userId") String userId, @Field("modified_date") String modifiedDate);

    @FormUrlEncoded
    @POST(RetrofitConstant.CREATE_GROUP_URL)
    Call<MobileVerificationResponseModel> createGroup(@Field("user_uuid") String userId, @Field("name") String groupName, @Field("creation_key") String groupCreationKey, @Field("members") String members);

    @FormUrlEncoded
    @POST(RetrofitConstant.SUBMIT_ANSWER)
    Call<MobileVerificationResponseModel> submitAnswer(@Field("user_uuid") String userId,
                                                       @Field("creation_key") String creation_key,
                                                       @Field("section_uuid") String section_uuid,
                                                       @Field("unit_uuid") String unit_uuid,
                                                       @Field("submission_date") String submission_date,
                                                       @Field("mediacontent") String mediacontent,
                                                       @Field("score") float score,
                                                       @Field("attempts") int attempts,
                                                       @Field("response") String response
                                                       );


    @FormUrlEncoded
    @POST(RetrofitConstant.SUBMITTED_ANSWER_RESPONSE_URL)
    Call<MobileVerificationResponseModel> getSubmittedAnswerResponse(@Field("user_uuid") String userId, @Field("modified_date") String modifiedDate);


    @FormUrlEncoded
    @POST(RetrofitConstant.FETCH_MEDIA_SHARED)
    Call<MobileVerificationResponseModel> getMediaShared(@Field("user_uuid") String userId);

    @FormUrlEncoded
    @POST(RetrofitConstant.SHARED_MEDIA_GLOBALLY)
    Call<MobileVerificationResponseModel> shareMediaGlobally(@Field("user_uuid") String userUUID, @Field("data") String data);

    @FormUrlEncoded
    @POST(RetrofitConstant.DELETE_SHARED_MEDIA)
    Call<MobileVerificationResponseModel> deleteMedia(@Field("media_uuid") String data);



}
