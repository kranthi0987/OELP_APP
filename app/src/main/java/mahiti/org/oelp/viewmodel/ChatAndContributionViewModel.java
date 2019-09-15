package mahiti.org.oelp.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import mahiti.org.oelp.R;
import mahiti.org.oelp.database.DAOs.MediaContentDao;
import mahiti.org.oelp.models.MobileVerificationResponseModel;
import mahiti.org.oelp.models.SharedMediaModel;
import mahiti.org.oelp.services.CallAPIServicesData;
import mahiti.org.oelp.services.RetrofitConstant;
import mahiti.org.oelp.utils.CheckNetwork;
import mahiti.org.oelp.utils.Constants;
import mahiti.org.oelp.utils.Logger;
import mahiti.org.oelp.utils.MySharedPref;

/**
 * Created by RAJ ARYAN on 09/09/19.
 */
public class ChatAndContributionViewModel extends AndroidViewModel {

    private static final String TAG = ChatAndContributionViewModel.class.getSimpleName();
    private Context mContext;
    public MutableLiveData<Long> insertLong = new MutableLiveData<>();

    public ChatAndContributionViewModel(@NonNull Application application) {
        super(application);
        mContext = application;

    }

}
