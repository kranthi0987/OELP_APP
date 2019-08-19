package oelp.mahiti.org.newoepl.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by RAJ ARYAN on 02/08/19.
 */
public class LocationContent {

    @SerializedName("parent")
    @Expose
    private Integer parent;

    @SerializedName("boundary_level_type")
    @Expose
    private Integer boundaryLevelType;

    @SerializedName("created")
    @Expose
    private String created;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("active")
    @Expose
    private Integer active;

    @SerializedName("modified")
    @Expose
    private String modified;

    @SerializedName("id")
    @Expose
    private Integer id;

    public Integer getParent ()
{
    return parent;
}

    public void setParent (Integer parent)
    {
        this.parent = parent;
    }

    public Integer getBoundaryLevelType()
    {
        return boundaryLevelType;
    }

    public void setBoundaryLevelType(Integer boundaryLevelType)
    {
        this.boundaryLevelType = boundaryLevelType;
    }

    public String getCreated ()
    {
        return created;
    }

    public void setCreated (String created)
    {
        this.created = created;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public Integer getActive ()
    {
        return active;
    }

    public void setActive (Integer active)
    {
        this.active = active;
    }

    public String getModified ()
    {
        return modified;
    }

    public void setModified (String modified)
    {
        this.modified = modified;
    }

    public Integer getId ()
    {
        return id;
    }

    public void setId (Integer id)
    {
        this.id = id;
    }

    public LocationContent(Integer parent, Integer boundaryLevelType, String created, String name, Integer active, String modified, Integer id) {
        this.parent = parent;
        this.boundaryLevelType = boundaryLevelType;
        this.created = created;
        this.name = name;
        this.active = active;
        this.modified = modified;
        this.id = id;
    }

    public LocationContent() {
    }

    @Override
    public String toString() {
        return name;
    }
}