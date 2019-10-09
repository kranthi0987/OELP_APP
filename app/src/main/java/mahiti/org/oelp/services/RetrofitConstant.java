package mahiti.org.oelp.services;

/**
 * Created by sandeep HR on 22/12/18.
 */
public class RetrofitConstant {


    public static final String QUESTION_CHOICES_LIST_URL = "/api/choice-list/";
    public static final String GROUP_LIST_URL = "/api/group-list/";
    public static final String TEACHER_LIST_URL = "/api/trainer-teachers-list/";
    public static final String CREATE_GROUP_URL = "/api/group-create-update/";
    public static final String MEDIA_TRACKER_API = "/api/media-tracker/";
    public static final String SUBMIT_ANSWER = "/api/question-answer/";
    public static final String FETCH_MEDIA_SHARED = "/api/share/media/list/";
    public static final String SHARED_MEDIA_GLOBALLY = "/api/share/media/globally/";
    public static final String DELETE_SHARED_MEDIA = "/api/share/media/globally/";
    public static final String UPLAOD_MEDIA_SHARED = "/api/share/media/upload/";

    private RetrofitConstant() {
        throw new IllegalStateException("RetrofitConstant Class");
    }


//    public static final String BASE_URL = "https://oelpbackend.mahiti.org";
    public static final String BASE_URL = "http://oelpbackenddemo.mahiti.org";
    public static final String BASE_URL_VIDEO = "https://oelp.mahiti.org/";
    public static final String OTP_VALIDATION_URL = "/api/otp-validation/";
    public static final String MOBILE_VALIDATION_URL = "/api/mobile-validation/";
    public static final String USER_REGISTRATION_URL = "/api/user-registration/";
    public static final String CHANGE_MOBILE_NO_URL = "api/change-mobile-number/";
    public static final String LOCATION_LIST_URL = "/api/location-list/";
    public static final String CATALOGUE_URL = "/api/catalogue/";
    public static final String SUBMITTED_ANSWER_RESPONSE_URL = "/api/response/";
    public static final String QUESTION_LIST_URL = "/api/question-list/";
    public static final Integer STATUS_TRUE = 2;
    public static final Integer STATUS_FALSE = 0;


}

