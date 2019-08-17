package oelp.mahiti.org.newoepl.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by RAJ ARYAN on 14/08/19.
 */
public class QuestionChoicesModel {

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("is_correct")
    @Expose
    private Boolean isCorrect;

    @SerializedName("text")
    @Expose
    private String text;

    @SerializedName("question")
    @Expose
    private Integer questionId;

    @SerializedName("active")
    @Expose
    private Integer active;

    @SerializedName("modified")
    @Expose
    private String modifiedDate;

    @SerializedName("explain")
    @Expose
    private String answerExplaination;

    @SerializedName("score")
    @Expose
    private Integer score;

    public QuestionChoicesModel() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getCorrect() {
        return isCorrect;
    }

    public void setCorrect(Boolean correct) {
        isCorrect = correct;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getAnswerExplaination() {
        return answerExplaination;
    }

    public void setAnswerExplaination(String answerExplaination) {
        this.answerExplaination = answerExplaination;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}
