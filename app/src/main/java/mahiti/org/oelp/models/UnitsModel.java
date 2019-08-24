package mahiti.org.oelp.models;

/**
 * Created by sandeep HR on 27/12/18.
 */
public class UnitsModel {

    private Integer id;
    private Integer parentId;
    private Integer active;
    private String unitName;
    private String dateTime;

    public UnitsModel(Integer id, Integer parentId, Integer active, String unitName, String dateTime) {
        this.id = id;
        this.parentId = parentId;
        this.active = active;
        this.unitName = unitName;
        this.dateTime = dateTime;
    }

    public UnitsModel() {
    }

    public UnitsModel(String unitName) {
        this.unitName = unitName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
