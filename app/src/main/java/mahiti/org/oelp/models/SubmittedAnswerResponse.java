package mahiti.org.oelp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by RAJ ARYAN on 11/09/19.
 */
public class SubmittedAnswerResponse {

    @SerializedName("submission_date")
    @Expose
    private String submissionDate;

    @SerializedName("creation_key")
    @Expose
    private String creationKey;

    @SerializedName("attempts")
    @Expose
    private Integer attempts;

    @SerializedName("score")
    @Expose
    private Float score;

    @SerializedName("mediacontent")
    @Expose
    private String mediacontent;

    @SerializedName("user_uuid")
    @Expose
    private String userUuid;

    @SerializedName("sectionUUID")
    @Expose
    private String sectionUUID;

    @SerializedName("unitUUID")
    @Expose
    private String unitUUID;

    @SerializedName("response")
    @Expose
    private List<QuestionAnswerIdModel> response;

    private String previewString;

    private String serverString;

    private String total;

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getPreviewString() {
        return previewString;
    }

    public void setPreviewString(String previewString) {
        this.previewString = previewString;
    }

    public String getServerString() {
        return serverString;
    }

    public void setServerString(String serverString) {
        this.serverString = serverString;
    }

    public String getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(String submissionDate) {
        this.submissionDate = submissionDate;
    }

    public String getCreationKey() {
        return creationKey;
    }

    public void setCreationKey(String creationKey) {
        this.creationKey = creationKey;
    }

    public Integer getAttempts() {
        return attempts;
    }

    public void setAttempts(Integer attempts) {
        this.attempts = attempts;
    }

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }

    public String getMediacontent() {
        return mediacontent;
    }

    public void setMediacontent(String mediacontent) {
        this.mediacontent = mediacontent;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public String getSectionUUID() {
        return sectionUUID;
    }

    public void setSectionUUID(String sectionUUID) {
        this.sectionUUID = sectionUUID;
    }

    public String getUnitUUID() {
        return unitUUID;
    }

    public void setUnitUUID(String unitUUID) {
        this.unitUUID = unitUUID;
    }

    public List<QuestionAnswerIdModel> getResponse() {
        return response;
    }

    public void setResponse(List<QuestionAnswerIdModel> response) {
        this.response = response;
    }
}
