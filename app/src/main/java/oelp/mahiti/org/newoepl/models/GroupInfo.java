package oelp.mahiti.org.newoepl.models;

import java.util.ArrayList;
import java.util.List;

public class GroupInfo {
    private String name;
    private List<ChildInfo> list = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ChildInfo> getQuestionList() {
        return list;
    }

    public void setQuestionList(List<ChildInfo> list) {
        this.list = list;
    }
}
