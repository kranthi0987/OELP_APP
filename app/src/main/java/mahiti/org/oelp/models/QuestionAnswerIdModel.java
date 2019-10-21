package mahiti.org.oelp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by RAJ ARYAN on 11/09/19.
 */
public class QuestionAnswerIdModel {

    @SerializedName("res_id")
    @Expose
    private Integer resId;

    @SerializedName("q_id")
    @Expose
    private Integer qId;

    public Integer getResId() {
        return resId;
    }

    public void setResId(Integer resId) {
        this.resId = resId;
    }

    public Integer getqId() {
        return qId;
    }

    public void setqId(Integer qId) {
        this.qId = qId;
    }
}
