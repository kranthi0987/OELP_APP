package mahiti.org.oelp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by RAJ ARYAN on 02/08/19.
 */
public class LocationModel {


    @SerializedName("status")
    @Expose
    private Integer status;

    @SerializedName("content")
    @Expose
    private ArrayList<LocationContent> locationContent;

    public ArrayList<LocationContent> getLocationContent() {
        return locationContent;
    }

    public void setLocationContent(ArrayList<LocationContent> locationContent) {
        this.locationContent = locationContent;
    }

    public Integer getStatus () {
        return status;
    }

    public void setStatus (Integer status) {
        this.status = status;
    }


}
