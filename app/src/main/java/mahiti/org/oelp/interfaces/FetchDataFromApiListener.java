package mahiti.org.oelp.interfaces;

import mahiti.org.oelp.models.MobileVerificationResponseModel;

/**
 * Created by RAJ ARYAN on 14/09/19.
 */
public interface FetchDataFromApiListener {

    public void onFetchDataFromApi(MobileVerificationResponseModel model, String type);
}
