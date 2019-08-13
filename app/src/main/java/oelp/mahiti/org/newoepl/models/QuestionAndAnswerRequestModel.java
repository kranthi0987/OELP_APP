
package oelp.mahiti.org.newoepl.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class QuestionAndAnswerRequestModel {

    @SerializedName("q_id")
    @Expose
    private String qId;
    @SerializedName("res_id")
    @Expose
    private String resId;

    public String getQId() {
        return qId;
    }

    public void setQId(String qId) {
        this.qId = qId;
    }

    public String getResId() {
        return resId;
    }

    public void setResId(String resId) {
        this.resId = resId;
    }

}
