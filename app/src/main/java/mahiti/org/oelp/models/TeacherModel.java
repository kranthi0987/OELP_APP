package mahiti.org.oelp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TeacherModel {

    @SerializedName("last_active")
    @Expose
    private String lastActive;
    @SerializedName("group_name")
    @Expose
    private String groupName;
    @SerializedName("mobile_number")
    @Expose
    private String mobileNumber;
    @SerializedName("active")
    @Expose
    private Integer active;
    @SerializedName("group_uuid")
    @Expose
    private String groupUuid;
    @SerializedName("blockIds")
    @Expose
    private Integer blockIds;
    @SerializedName("school")
    @Expose
    private String school;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("stateId")
    @Expose
    private Integer stateId;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("districtId")
    @Expose
    private Integer districtId;
    @SerializedName("video_covered_count")
    @Expose
    private String videoCoveredCount;

    public String getBlockName() {
        return blockName;
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }

    private String blockName;



    public Integer getIsTrainer() {
        return isTrainer;
    }

    public void setIsTrainer(Integer isTrainer) {
        this.isTrainer = isTrainer;
    }

    public String getLastLoggedIn() {
        return lastLoggedIn;
    }

    public void setLastLoggedIn(String lastLoggedIn) {
        this.lastLoggedIn = lastLoggedIn;
    }

    @SerializedName("isTrainer")
    @Expose
    private Integer isTrainer;
    @SerializedName("user_uuid")
    @Expose
    private String userUuid;
    @SerializedName("last_logged_in")
    @Expose
    private String lastLoggedIn;

    public String getLastActive() {
        return lastActive;
    }

    public void setLastActive(String lastActive) {
        this.lastActive = lastActive;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public String getGroupUuid() {
        return groupUuid;
    }

    public void setGroupUuid(String groupUuid) {
        this.groupUuid = groupUuid;
    }

    public Integer getBlockIds() {
        return blockIds;
    }

    public void setBlockIds(Integer blockIds) {
        this.blockIds = blockIds;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStateId() {
        return stateId;
    }

    public void setStateId(Integer stateId) {
        this.stateId = stateId;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public Integer getDistrictId() {
        return districtId;
    }

    public void setDistrictId(Integer districtId) {
        this.districtId = districtId;
    }

    public String getVideoCoveredCount() {
        return videoCoveredCount;
    }

    public void setVideoCoveredCount(String videoCoveredCount) {
        this.videoCoveredCount = videoCoveredCount;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }



}
