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

    private String stateName;
    private String districtname;
    private String blockName;
    private String villageName;

    protected UserDetailsModel(Parcel in) {
        lastActive = in.readString();
        userGroup = in.createStringArrayList();
        videoCoveredCount = in.readString();
        lastLoggedIn = in.readString();
        school = in.readString();
        name = in.readString();
        userid = in.readString();
        if (in.readByte() == 0) {
            active = null;
        } else {
            active = in.readInt();
        }
        if (in.readByte() == 0) {
            blockIds = null;
        } else {
            blockIds = in.readInt();
        }
        if (in.readByte() == 0) {
            isTrainer = null;
        } else {
            isTrainer = in.readInt();
        }
        mobile_number = in.readString();
        isCheckBoxChecked = in.readByte() != 0;
        stateName = in.readString();
        districtname = in.readString();
        blockName = in.readString();
        villageName = in.readString();
    }

    public static final Creator<UserDetailsModel> CREATOR = new Creator<UserDetailsModel>() {
        @Override
        public UserDetailsModel createFromParcel(Parcel in) {
            return new UserDetailsModel(in);
        }

        @Override
        public UserDetailsModel[] newArray(int size) {
            return new UserDetailsModel[size];
        }
    };

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getDistrictname() {
        return districtname;
    }

    public void setDistrictname(String districtname) {
        this.districtname = districtname;
    }

    public String getBlockName() {
        return blockName;
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }

    public String getVillageName() {
        return villageName;
    }

    public void setVillageName(String villageName) {
        this.villageName = villageName;
    }

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



    public UserDetailsModel() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(lastActive);
        parcel.writeStringList(userGroup);
        parcel.writeString(videoCoveredCount);
        parcel.writeString(lastLoggedIn);
        parcel.writeString(school);
        parcel.writeString(name);
        parcel.writeString(userid);
        if (active == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(active);
        }
        if (blockIds == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(blockIds);
        }
        if (isTrainer == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(isTrainer);
        }
        parcel.writeString(mobile_number);
        parcel.writeByte((byte) (isCheckBoxChecked ? 1 : 0));
        parcel.writeString(stateName);
        parcel.writeString(districtname);
        parcel.writeString(blockName);
        parcel.writeString(villageName);
    }

}
