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

    private Integer syncStatus=0;  //syncStatus=1 (to Sync), syncStatus=0 (Synced)

    private Integer deleteStatus=1;  //deleteStatus=1 (to delete), deleteStatus=0 (Sync)

    private int sharedGlobally;

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

    public int getSharedGlobally() {
        return sharedGlobally;
    }

    public void setSharedGlobally(int sharedGlobally) {
        this.sharedGlobally = sharedGlobally;
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
