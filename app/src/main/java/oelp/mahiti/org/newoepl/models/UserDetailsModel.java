package oelp.mahiti.org.newoepl.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by RAJ ARYAN on 31/07/19.
 */
public class UserDetailsModel implements Parcelable{
    @SerializedName("school")
    @Expose
    private String school;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("userid")
    @Expose
    private Integer userid;

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

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        this.school = parcel.readString();
        this.name = parcel.readString();
        this.userid = parcel.readInt();
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

    public UserDetailsModel(Parcel in){
        this.school = in.readString();
        this.name = in.readString();
        this.userid =  in.readInt();
        this.isTrainer =  in.readInt();
        this.mobile_number =  in.readString();
        this.active =  in.readInt();
        this.blockIds =  in.readInt();
    }

}
