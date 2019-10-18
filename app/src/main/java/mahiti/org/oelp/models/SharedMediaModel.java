package mahiti.org.oelp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by RAJ ARYAN on 14/09/19.
 */
public class SharedMediaModel {

    @SerializedName("media_uuid")
    @Expose
    private String mediaUuid;

    @SerializedName("submission_time")
    @Expose
    private String submissionTime;

    @SerializedName("group_uuid")
    @Expose
    private String groupUuid;

    @SerializedName("media_title")
    @Expose
    private String mediaTitle;

    @SerializedName("media_file")
    @Expose
    private String mediaFile;

    @SerializedName("media_type")
    @Expose
    private String mediaType;

    @SerializedName("user_uuid")
    @Expose
    private String userUuid;

    @SerializedName("user_name")
    @Expose
    private String userName;

    @SerializedName("global_access")
    @Expose
    private boolean globalAccess;

    @SerializedName("modified_on")
    @Expose
    private String modifiedOn;

    @SerializedName("hash_key")
    @Expose
    private String hashKey;

    @SerializedName("active")
    @Expose
    private Integer active;


    public String getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(String modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public String getHashKey() {
        return hashKey;
    }

    public void setHashKey(String hashKey) {
        this.hashKey = hashKey;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public boolean getGlobalAccess() {
        return globalAccess;
    }

    public void setGlobalAccess(boolean globalAccess) {
        this.globalAccess = globalAccess;
    }

    private Integer syncStatus=0;  //syncStatus=1 (to Sync), syncStatus=0 (Synced)

    private Integer deleteStatus=1;  //deleteStatus=1 (to delete), deleteStatus=0 (Sync)

    private int sharedGloballySyncStatus;

    public Integer getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(Integer syncStatus) {
        this.syncStatus = syncStatus;
    }

    public Integer getDeleteStatus() {
        return deleteStatus;
    }

    public void setDeleteStatus(Integer deleteStatus) {
        this.deleteStatus = deleteStatus;
    }

    public int getSharedGloballySyncStatus() {
        return sharedGloballySyncStatus;
    }

    public void setSharedGloballySyncStatus(int sharedGloballySyncStatus) {
        this.sharedGloballySyncStatus = sharedGloballySyncStatus;
    }

    public String getMediaUuid() {
        return mediaUuid;
    }

    public void setMediaUuid(String mediaUuid) {
        this.mediaUuid = mediaUuid;
    }

    public String getSubmissionTime() {
        return submissionTime;
    }

    public void setSubmissionTime(String submissionTime) {
        this.submissionTime = submissionTime;
    }

    public String getGroupUuid() {
        return groupUuid;
    }

    public void setGroupUuid(String groupUuid) {
        this.groupUuid = groupUuid;
    }

    public String getMediaTitle() {
        return mediaTitle;
    }

    public void setMediaTitle(String mediaTitle) {
        this.mediaTitle = mediaTitle;
    }

    public String getMediaFile() {
        return mediaFile;
    }

    public void setMediaFile(String mediaFile) {
        this.mediaFile = mediaFile;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
