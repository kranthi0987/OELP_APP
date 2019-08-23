package oelp.mahiti.org.newoepl.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by RAJ ARYAN on 21/08/19.
 */
public class Member {

    @SerializedName("uuid")
    @Expose
    private String uuid;

    public Member(String string) {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
