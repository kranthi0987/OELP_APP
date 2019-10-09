package mahiti.org.oelp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by RAJ ARYAN on 21/08/19.
 */
public class GroupModel {

    @SerializedName("userroles")
    @Expose
    private String userUUID;

    @SerializedName("creation_key")
    @Expose
    private String groupUUID;

    @SerializedName("name")
    @Expose
    private String groupName;

    @SerializedName("active")
    @Expose
    private Integer active;

    @SerializedName("members")
    @Expose
    private List<Member> members;

    private int membersCount;

    public GroupModel() {
    }

    public int getMembersCount() {
        return membersCount;
    }

    public void setMembersCount(int membersCount) {
        this.membersCount = membersCount;
    }

    public String getUserUUID() {
        return userUUID;
    }

    public void setUserUUID(String userUUID) {
        this.userUUID = userUUID;
    }

    public String getGroupUUID() {
        return groupUUID;
    }

    public void setGroupUUID(String groupUUID) {
        this.groupUUID = groupUUID;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
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
