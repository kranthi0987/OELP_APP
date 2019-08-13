package oelp.mahiti.org.newoepl.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class UserRegistrationResponseModel {
    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private UserDetails data;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UserDetails getData() {
        return data;
    }

    public void setData(UserDetails data) {
        this.data = data;
    }


}


