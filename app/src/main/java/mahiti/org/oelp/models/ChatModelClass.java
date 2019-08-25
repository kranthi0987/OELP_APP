package mahiti.org.oelp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by RAJ ARYAN on 25/08/19.
 */
public class ChatModelClass {

    @SerializedName("chatType")
    @Expose
    private Integer chatType;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("date")
    @Expose
    private String date;

    @SerializedName("nameInitials")
    @Expose
    private String nameInitials;

    @SerializedName("colorcode")
    @Expose
    private String colorcode;

    public Integer getChatType() {
        return chatType;
    }

    public void setChatType(Integer chatType) {
        this.chatType = chatType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNameInitials() {
        return nameInitials;
    }

    public void setNameInitials(String nameInitials) {
        this.nameInitials = nameInitials;
    }

    public String getColorcode() {
        return colorcode;
    }

    public void setColorcode(String colorcode) {
        this.colorcode = colorcode;
    }
}
