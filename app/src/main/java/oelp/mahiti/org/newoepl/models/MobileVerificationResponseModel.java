package oelp.mahiti.org.newoepl.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import oelp.mahiti.org.newoepl.utils.Action;


/**
 * Created by sandeep HR on 18/12/18.
 */
public class MobileVerificationResponseModel {


    @SerializedName("status")
    @Expose
    private Integer status;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("userid")
    @Expose
    private Integer userid;

    @SerializedName("userDetails")
    @Expose
    private UserDetailsModel userDetails;

    private Action mAction;

    @SerializedName("content")
    @Expose
    private List<CatalogueDetailsModel> catalogueDetailsModel;


    public MobileVerificationResponseModel() {
    }

    public MobileVerificationResponseModel(Integer status, String message) {
        this.message = message;
        this.status = status;
    }

    public List<CatalogueDetailsModel> getCatalogueDetailsModel() {
        return catalogueDetailsModel;
    }

    public void setCatalogueDetailsModel(List<CatalogueDetailsModel> catalogueDetailsModel) {
        this.catalogueDetailsModel = catalogueDetailsModel;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public UserDetailsModel getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetailsModel userDetails) {
        this.userDetails = userDetails;
    }


    public Action getmAction() {
        return mAction;
    }

    public void setmAction(Action mAction) {
        this.mAction = mAction;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
