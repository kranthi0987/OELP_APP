package mahiti.org.oelp.viewmodel;

import android.app.Application;
import android.content.Context;

import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import mahiti.org.oelp.database.DAOs.CatalogDao;
import mahiti.org.oelp.database.DAOs.GroupDao;
import mahiti.org.oelp.database.DAOs.MediaContentDao;
import mahiti.org.oelp.fileandvideodownloader.FileModel;
import mahiti.org.oelp.models.CatalogueDetailsModel;
import mahiti.org.oelp.models.GroupModel;
import mahiti.org.oelp.services.FetchUpdateddata;
import mahiti.org.oelp.services.SyncingUserData;
import mahiti.org.oelp.utils.AppUtils;
import mahiti.org.oelp.utils.CheckNetwork;
import mahiti.org.oelp.utils.Constants;
import mahiti.org.oelp.utils.Logger;
import mahiti.org.oelp.utils.MySharedPref;

/**
 * Created by RAJ ARYAN on 07/08/19.
 */
public class HomeViewModel extends AndroidViewModel {

    private static final String TAG = AndroidViewModel.class.getSimpleName();
    private final Context context;
    private final String userId;
    private final SyncingUserData data;
    public boolean teacherLogin;
    public MutableLiveData<Boolean> unitsClick = new MutableLiveData<>();
    public MutableLiveData<Boolean> groupsClick = new MutableLiveData<>();
    public MutableLiveData<Integer> userType = new MutableLiveData<>();
    public MutableLiveData<String> title = new MutableLiveData<>();
    private MutableLiveData<Integer> dataInserted = new MutableLiveData<>();
    private MutableLiveData<List<FileModel>> listToDownloadImage = new MutableLiveData<>();

    private MySharedPref sharedPref;
    public MutableLiveData<String> parentId = new MutableLiveData<>();
    public List<CatalogueDetailsModel> modelForCatalog = new ArrayList<>();
    public MutableLiveData<Integer> apiCountMutable = new MutableLiveData<>();
    public MutableLiveData<Long> insertLong = new MutableLiveData();
    private MutableLiveData<List<FileModel>> imageListToDownload = new MutableLiveData<>();


    public HomeViewModel(@NonNull Application application) {
        super(application);
        context = application;
        sharedPref = new MySharedPref(application);
        catalogDao = new CatalogDao(context);
        userId = sharedPref.readString(Constants.USER_ID, "");
        teacherLogin = sharedPref.readInt(Constants.USER_TYPE, Constants.USER_TEACHER) == Constants.USER_TEACHER;
        dataInserted.setValue(null);
        data = new SyncingUserData(context);

        if (CheckNetwork.checkNet(context)){
            new FetchUpdateddata(context);
            updateSharedMedia();
            updateDeleteMedia();
            updateGlobalShareMedia();
            postQA();
            getListOfImageFromDb();
        }

        userType.setValue(sharedPref.readInt(Constants.USER_TYPE, Constants.USER_TEACHER));

        if (userType.getValue().equals(Constants.USER_TEACHER)) {
            unitsClick.setValue(true);
            groupsClick.setValue(false);
        } else {
            unitsClick.setValue(true);
            groupsClick.setValue(false);
        }

    }

    public void updateSharedMedia(){
        data.uploadMedia();
    }

    public void updateDeleteMedia(){
        data.deleteMedia();
    }

    public void updateGlobalShareMedia(){
        data.shareMediaGlobally();
    }

    public void postQA(){
        data.postQA();
    }

    public void setDataInserted(Integer dataInserted) {
        this.dataInserted.setValue(dataInserted);
    }

    public void getListOfImageFromDb() {
        List<FileModel> imageList = catalogDao.getImageListFromTable();
        if (!imageList.isEmpty()) {
            checkOfflineAvailable(imageList);
        } else {
            dataInserted.setValue(2);
        }
    }

    private void getListOfImageFromMediaTable() {
        List<FileModel> imageList = new MediaContentDao(context).getImageListFromMediaTable();
        if (imageList!=null && !imageList.isEmpty()) {
            checkOfflineAvailable1(imageList);
        }
    }
    private void checkOfflineAvailable1(List<FileModel> imageList) {
        List<FileModel> imagePathToRemove = new ArrayList<>();
        for (FileModel iconPath : imageList) {
            try {
                File file = new File(AppUtils.completeInternalStoragePath(context, Constants.IMAGE), AppUtils.getFileName(iconPath.getFileUrl()));
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

    public MutableLiveData<List<FileModel>> getListOfImageToDownload1() {
        return imageListToDownload;
    }

    private void checkOfflineAvailable(List<FileModel> imageList) {
        List<FileModel> imagePathToRemove = new ArrayList<>();
        for (FileModel iconPath : imageList) {
            try {
//                File file = new File(AppUtils.completePathInSDCard(Constants.IMAGE), AppUtils.getFileName(iconPath.getFileUrl()));
                File file = new File(AppUtils.completeInternalStoragePath(context, Constants.IMAGE), AppUtils.getFileName(iconPath.getFileUrl()));
                if (file.exists()) {
                    imagePathToRemove.add(iconPath);
                }

            } catch (Exception ex) {
                Logger.logE(TAG, ex.getMessage(), ex);
            }
        }
        imageList.removeAll(imagePathToRemove);
        if (!imageList.isEmpty()) {
            listToDownloadImage.setValue(imageList);
        } else {
            dataInserted.setValue(3);
        }
    }

    public MutableLiveData<Integer> getDataInserted() {
        return dataInserted;
    }

    public MutableLiveData<List<FileModel>> getListOfImageToDownload() {
        return listToDownloadImage;
    }

    CatalogDao catalogDao;

    public void onUnitsClick() {
        if (!unitsClick.getValue()) {
            unitsClick.setValue(true);
            groupsClick.setValue(false);
        }
    }

    public void onGroupsClick() {
        if (!groupsClick.getValue()) {
            unitsClick.setValue(false);
            groupsClick.setValue(true);
        }
    }


    public List<GroupModel> getGroupList() {
        return new GroupDao(context).getGroupList();
    }

}
