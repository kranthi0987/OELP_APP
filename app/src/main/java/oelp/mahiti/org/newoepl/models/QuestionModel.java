package oelp.mahiti.org.newoepl.models;

public class QuestionModel {

    Integer id;
    String name;
    String hint;
    Integer active;
    String modified;
    Integer unitID;
    Integer faqType;
    Integer videoID;


    public Integer getVideoID() {
        return videoID;
    }

    public void setVideoID(Integer videoID) {
        this.videoID = videoID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public Integer getUnitID() {
        return unitID;
    }

    public void setUnitID(Integer unitID) {
        this.unitID = unitID;
    }

    public Integer getFaqType() {
        return faqType;
    }

    public void setFaqType(Integer faqType) {
        this.faqType = faqType;
    }
}