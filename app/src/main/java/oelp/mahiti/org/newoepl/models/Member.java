package oelp.mahiti.org.newoepl.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by RAJ ARYAN on 21/08/19.
 */
public class Member {

    @SerializedName("creation_key")
    @Expose
    private String groupMemberUUID;

    public Member() {
    }

    public String getGroupMemberUUID() {
        return groupMemberUUID;
    }

    public void setGroupMemberUUID(String groupMemberUUID) {
        this.groupMemberUUID = groupMemberUUID;
    }
}
