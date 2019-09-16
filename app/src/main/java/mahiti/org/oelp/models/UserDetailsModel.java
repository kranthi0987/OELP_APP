package mahiti.org.oelp.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by RAJ ARYAN on 31/07/19.
 */
public class UserDetailsModel implements Parcelable{
    @SerializedName("last_active")
    @Expose
    private String lastActive;

    @SerializedName("user_group")
    @Expose
    private List<String> userGroup;

    @SerializedName("video_covered_count")
    @Expose
    private String videoCoveredCount;

    @SerializedName("last_logged_in")
    @Expose
    private String lastLoggedIn;

    @SerializedName("school")
    @Expose
    private String school;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("user_uuid")
    @Expose
    private String userid;

    @SerializedName("active")
    @Expose
    private Integer active;

    @SerializedName("blockIds")
    @Expose
    private Integer blockIds;

    @SerializedName("isTrainer")
    @Expose
    private Integer isTrainer;

    @SerializedName("mobile_number")
    @Expose
    private String mobile_number;

    private boolean isCheckBoxChecked;

    public boolean isCheckBoxChecked() {
        return isCheckBoxChecked;
    }

    public void setCheckBoxChecked(boolean checkBoxChecked) {
        isCheckBoxChecked = checkBoxChecked;
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

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public Integer getBlockIds() {
        return blockIds;
    }

    public void setBlockIds(Integer blockIds) {
        this.blockIds = blockIds;
    }

    public Integer getIsTrainer() {
        return isTrainer;
    }

    public void setIsTrainer(Integer isTrainer) {
        this.isTrainer = isTrainer;
    }

    public String getMobile_number() {
        return mobile_number;
    }

    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
    }

    public String getLastActive() {
        return lastActive;
    }

    public void setLastActive(String lastActive) {
        this.lastActive = lastActive;
    }

    public List<String> getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(List<String> userGroup) {
        this.userGroup = userGroup;
    }

    public String getVideoCoveredCount() {
        return videoCoveredCount;
    }

    public void setVideoCoveredCount(String videoCoveredCount) {
        this.videoCoveredCount = videoCoveredCount;
    }

    public String getLastLoggedIn() {
        return lastLoggedIn;
    }

    public void setLastLoggedIn(String lastLoggedIn) {
        this.lastLoggedIn = lastLoggedIn;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        this.school = parcel.readString();
        this.name = parcel.readString();
        this.userid = parcel.readString();
        this.isTrainer = parcel.readInt();
        this.mobile_number = parcel.readString();
        this.active = parcel.readInt();
        this.blockIds = parcel.readInt();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public UserDetailsModel createFromParcel(Parcel in) {
            return new UserDetailsModel(in);
        }

        public UserDetailsModel[] newArray(int size) {
            return new UserDetailsModel[size];
        }
    };

    public UserDetailsModel() {
    }

    public UserDetailsModel(Parcel in){
        this.school = in.readString();
        this.name = in.readString();
        this.userid =  in.readString();
        this.isTrainer =  in.readInt();
        this.mobile_number =  in.readString();
        this.active =  in.readInt();
        this.blockIds =  in.readInt();
    }

}
