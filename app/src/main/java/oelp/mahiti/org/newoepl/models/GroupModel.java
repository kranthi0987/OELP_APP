package oelp.mahiti.org.newoepl.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by RAJ ARYAN on 21/08/19.
 */
public class GroupModel {

    @SerializedName("grp_name")
    @Expose
    private String groupName;

    @SerializedName("creation_key")
    @Expose
    private String creationkey;

    @SerializedName("created_on")
    @Expose
    private String createdOn;

    @SerializedName("active")
    @Expose
    private Integer active;

    @SerializedName("members")
    @Expose
    private List<Member> members;

    public GroupModel() {
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getCreationkey() {
        return creationkey;
    }

    public void setCreationkey(String creationkey) {
        this.creationkey = creationkey;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }
}
