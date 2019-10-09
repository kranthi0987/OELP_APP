package mahiti.org.oelp.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import mahiti.org.oelp.database.DAOs.MediaContentDao;
import mahiti.org.oelp.fileandvideodownloader.DownloadClass;
import mahiti.org.oelp.fileandvideodownloader.FileModel;
import mahiti.org.oelp.models.MobileVerificationResponseModel;
import mahiti.org.oelp.services.ApiInterface;
import mahiti.org.oelp.services.RetrofitClass;
import mahiti.org.oelp.services.RetrofitConstant;
import mahiti.org.oelp.utils.AppUtils;
import mahiti.org.oelp.utils.Constants;
import mahiti.org.oelp.utils.Logger;
import mahiti.org.oelp.utils.MySharedPref;
import mahiti.org.oelp.views.activities.HomeActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by RAJ ARYAN on 09/09/19.
 */
public class ChatAndContributionViewModel extends AndroidViewModel {

    private static final String TAG = ChatAndContributionViewModel.class.getSimpleName();
    private Context mContext;
    public MutableLiveData<Long> insertLong = new MutableLiveData<>();
    private MediaContentDao mediaContentDao;
    private MutableLiveData<List<FileModel>> imageListToDownload = new MutableLiveData<>();

    public ChatAndContributionViewModel(@NonNull Application application) {
        super(application);
        mContext = application;
        mediaContentDao = new MediaContentDao(mContext);
        MySharedPref pre = new MySharedPref(mContext);
        getListOfImageFromMediaTable();
        /*callApiForMediaSharedList(pre.readString(Constants.USER_ID,""));*/

    }

    public void callApiForMediaSharedList(String userUUID) {
       /* final MobileVerificationResponseModel[] model = {null};
        ApiInterface apiInterface = RetrofitClass.getAPIService();
        Logger.logD(TAG, "URL :" + RetrofitConstant.BASE_URL + RetrofitConstant.FETCH_MEDIA_SHARED + " Param : user_uuid:" + userUUID);
        apiInterface.getMediaShared(userUUID).enqueue(new Callback<MobileVerificationResponseModel>() {
            @Override
            public void onResponse(Call<MobileVerificationResponseModel> call, Response<MobileVerificationResponseModel> response) {
                if (response.body() != null) {
                    Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.FETCH_MEDIA_SHARED + " Response :" + response.body());
                    insertDatatoTable(response.body());
                } else {
                    getListOfImageFromMediaTable();
                }
            }

            @Override
            public void onFailure(Call<MobileVerificationResponseModel> call, Throwable t) {
                Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.FETCH_MEDIA_SHARED + " Response :" + t.getMessage());
                getListOfImageFromMediaTable();
            }
        });*/
    }



    private void getListOfImageFromMediaTable() {
        List<FileModel> imageList = mediaContentDao.getImageListFromMediaTable();
        if (imageList!=null && !imageList.isEmpty()) {
            checkOfflineAvailable(imageList);
        }
    }

    private void checkOfflineAvailable(List<FileModel> imageList) {
        List<FileModel> imagePathToRemove = new ArrayList<>();
        for (FileModel iconPath : imageList) {
            try {
                File file = new File(AppUtils.completePathInSDCard(Constants.IMAGE), AppUtils.getFileName(iconPath.getFileUrl()));
                if (file.exists()) {
                    imagePathToRemove.add(iconPath);
                }

            } catch (Exception ex) {
                Logger.logE(TAG, ex.getMessage(), ex);
            }
        }
        imageList.removeAll(imagePathToRemove);
        if (!imageList.isEmpty()) {
            imageListToDownload.setValue(imageList);
        }
    }


    public MutableLiveData<List<FileModel>> getListOfImageToDownload() {
        return imageListToDownload;
    }


}
