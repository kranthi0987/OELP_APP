package mahiti.org.oelp.services;


import mahiti.org.oelp.models.LocationModel;
import mahiti.org.oelp.models.MobileVerificationResponseModel;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiInterface {
//    @Headers({
//            "un:oelp2018",
//            "pw:@!(^$o$#e!l#@p"
//
//    })
    @FormUrlEncoded
    @POST(RetrofitConstant.BASE_URL+RetrofitConstant.MOBILE_VALIDATION_URL)
    Call<MobileVerificationResponseModel> mobileValidation(@Field("mobile_number") String mobileNumber);
//
//    @Headers({
//            "un:oelp2018",
//            "pw:@!(^$o$#e!l#@p"
//    })
    @FormUrlEncoded
    @POST(RetrofitConstant.BASE_URL+RetrofitConstant.USER_REGISTRATION_URL)
    Call<MobileVerificationResponseModel> userRegistration(@Field("userDetails") String userDetails);

    @FormUrlEncoded
    @POST(RetrofitConstant.BASE_URL+RetrofitConstant.OTP_VALIDATION_URL)
    Call<MobileVerificationResponseModel> otpValidation(@Field("otp") String otp, @Field("mobile_number") String mobileNumber);

    @FormUrlEncoded
    @POST(RetrofitConstant.BASE_URL+RetrofitConstant.CHANGE_MOBILE_NO_URL)
    Call<MobileVerificationResponseModel> changeMobileNumber(@Field("current_mobile_number") String currentMobileNo, @Field("new_mobile_number") String newMobileNo);

    @GET(RetrofitConstant.BASE_URL+RetrofitConstant.LOCATION_LIST_URL)
    Call<LocationModel> getLocationData();

    @FormUrlEncoded
    @POST(RetrofitConstant.BASE_URL2+RetrofitConstant.CATALOGUE_URL)
    Call<MobileVerificationResponseModel> catalogData(@Field("user_uuid") String userId, @Field("modified_date") String modifiedDate);

    @FormUrlEncoded
    @POST(RetrofitConstant.BASE_URL+RetrofitConstant.GROUP_LIST_URL)
    Call<MobileVerificationResponseModel> getGroupList(@Field("user_uuid") String userId);

    @FormUrlEncoded
    @POST(RetrofitConstant.BASE_URL+RetrofitConstant.TEACHER_LIST_URL)
    Call<MobileVerificationResponseModel> getTeacherList(@Field("user_uuid") String userId);


    @FormUrlEncoded
    @POST(RetrofitConstant.BASE_URL+RetrofitConstant.QUESTION_LIST_URL)
    Call<MobileVerificationResponseModel> getQuestionList(@Field("userId") String userId, @Field("modified_date") String modifiedDate);

    @FormUrlEncoded
    @POST(RetrofitConstant.BASE_URL2+RetrofitConstant.QUESTION_CHOICES_LIST_URL)
    Call<MobileVerificationResponseModel> getQuestionChoicesList(@Field("userId") String userId, @Field("modified_date") String modifiedDate);

    @FormUrlEncoded
    @POST(RetrofitConstant.BASE_URL+RetrofitConstant.CREATE_GROUP_URL)
    Call<MobileVerificationResponseModel> createGroup(@Field("user_uuid") String userId, @Field("name") String groupName, @Field("creation_key") String groupCreationKey, @Field("members") String members);

    @FormUrlEncoded
    @POST(RetrofitConstant.BASE_URL2+RetrofitConstant.SUBMIT_ANSWER)
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
    @POST(RetrofitConstant.BASE_URL2+RetrofitConstant.SUBMITTED_ANSWER_RESPONSE_URL)
    Call<MobileVerificationResponseModel> getSubmittedAnswerResponse(@Field("user_uuid") String userId, @Field("modified_date") String modifiedDate);



//
//    @Headers({
//            "un:oelp2018",
//            "pw:@!(^$o$#e!l#@p"
//    })
//    @FormUrlEncoded
//    @POST(RetrofitConstant.DBPATH_URL)
//    Call<MobileVerificationResponseModel> getDatabaseDownloadlink(@Field("version") String appVersion,
//                                                                  @Field("user_id") String userId);
//
//    @Headers({
//            "un:oelp2018",
//            "pw:@!(^$o$#e!l#@p"
//    })
//    @FormUrlEncoded
//    @POST(RetrofitConstant.USER_DETAILS_URL)
//    Call<UserRegistrationResponseModel> callToUserRegistration(@Field("user_id") String userId,
//                                                               @Field("name") String name,
//                                                               @Field("mobile") String mobile,
//                                                               @Field("block_id") String blockId,
//                                                               @Field("school_name") String schoolName,
//                                                               @Field("position_id") String positionId);
//
//    @Headers({
//            "un:oelp2018",
//            "pw:@!(^$o$#e!l#@p"
//    })
//    @FormUrlEncoded
//    @POST(RetrofitConstant.REGISTER_USER_DETAILS)
//    Call<UserRegistrationResponseModel> getUserDetails(@Field("user_id") String userId);
//
//    @Headers({
//            "un:oelp2018",
//            "pw:@!(^$o$#e!l#@p"})
//    @FormUrlEncoded
//    @POST(RetrofitConstant.QUESTION_AND_ANSWER_URL)
//    Call<QuestionAndAnswerResponsemodel> questionAndAnswerResponseModelCall(@Field("user_id") String userId,
//                                                                            @Field("video_id") String unitId,
//                                                                            @Field("ans") JSONArray answer,
//                                                                            @Field("submit_date") String dateTime);
}
