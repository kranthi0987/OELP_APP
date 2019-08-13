package oelp.mahiti.org.newoepl.models;

public class ChoiceModel {

    Integer id;
    String name;
    Integer questionID;
    Integer active;
    String modified;

    public ChoiceModel(Integer id, String name, Integer questionID, Integer active, String modified) {
        this.id = id;
        this.name = name;
        this.questionID = questionID;
        this.active = active;
        this.modified = modified;
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

    public Integer getQuestionID() {
        return questionID;
    }

    public void setQuestionID(Integer questionID) {
        this.questionID = questionID;
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
}

