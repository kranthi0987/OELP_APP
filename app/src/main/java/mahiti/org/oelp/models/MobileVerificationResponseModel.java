package mahiti.org.oelp.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import mahiti.org.oelp.utils.Action;


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

    @SerializedName("user_uuid")
    @Expose
    private String userid;

    @SerializedName("userDetails")
    @Expose
    private UserDetailsModel userDetails;

    private Action mAction;

    @SerializedName("content")
    @Expose
    private List<CatalogueDetailsModel> catalogueDetailsModel;

    @SerializedName("Question")
    @Expose
    private List<QuestionModel> questionModelList;

    @SerializedName("Choices")
    @Expose
    private List<QuestionChoicesModel> questionChoicesModelList;

    @SerializedName("groups")
    @Expose
    private List<GroupModel> groups;

    @SerializedName("teachers")
    @Expose
    private List<TeacherModel> teachers;


    public List<TeacherModel> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<TeacherModel> teachers) {
        this.teachers = teachers;
    }

    public MobileVerificationResponseModel() {
    }

    public List<GroupModel> getGroups() {
        return groups;
    }

    public void setGroups(List<GroupModel> groups) {
        this.groups = groups;
    }

    public MobileVerificationResponseModel(Integer status, String message) {
        this.message = message;
        this.status = status;
    }

    public List<QuestionModel> getQuestionModelList() {
        return questionModelList;
    }

    public void setQuestionModelList(List<QuestionModel> questionModelList) {
        this.questionModelList = questionModelList;
    }

    public List<QuestionChoicesModel> getQuestionChoicesModelList() {
        return questionChoicesModelList;
    }

    public void setQuestionChoicesModelList(List<QuestionChoicesModel> questionChoicesModelList) {
        this.questionChoicesModelList = questionChoicesModelList;
    }

    public List<CatalogueDetailsModel> getCatalogueDetailsModel() {
        return catalogueDetailsModel;
    }

    public void setCatalogueDetailsModel(List<CatalogueDetailsModel> catalogueDetailsModel) {
        this.catalogueDetailsModel = catalogueDetailsModel;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
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
