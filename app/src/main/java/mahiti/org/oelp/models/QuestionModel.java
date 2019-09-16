package mahiti.org.oelp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class QuestionModel {

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("active")
    @Expose
    private Integer active;

    @SerializedName("dcf")
    @Expose
    private Integer dcf;

    @SerializedName("mediacontent")
    @Expose
    private String mediacontent;

    @SerializedName("qtype")
    @Expose
    private String qtype;

    @SerializedName("help_text")
    @Expose
    private String helpText;

    @SerializedName("text")
    @Expose
    private String text;

    @SerializedName("modified")
    @Expose
    private String modified;

    public QuestionModel() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public Integer getDcf() {
        return dcf;
    }

    public void setDcf(Integer dcf) {
        this.dcf = dcf;
    }


    public String getQtype() {
        return qtype;
    }

    public void setQtype(String qtype) {
        this.qtype = qtype;
    }

    public String getHelpText() {
        return helpText;
    }

    public void setHelpText(String helpText) {
        this.helpText = helpText;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }
}