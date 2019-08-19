package oelp.mahiti.org.newoepl.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by RAJ ARYAN on 08/08/19.
 */
public class CatalogueDetailsModel implements Parcelable{

    @SerializedName("active")
    @Expose
    private Integer active;

//    @SerializedName("completed")
//    @Expose
    private Integer completed;

    @SerializedName("uuid")
    @Expose
    private String uuid;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("code")
    @Expose
    private String code;

    @SerializedName("order")
    @Expose
    private Integer order;

    @SerializedName("udf1")
    @Expose
    private UDF1 udf1;

    @SerializedName("parent")
    @Expose
    private String parent;

    @SerializedName("modified")
    @Expose
    private String modified;

    @SerializedName("icon")
    @Expose
    private String icon;

    @SerializedName("path")
    @Expose
    private String path;

    @SerializedName("media_level_type")
    @Expose
    private String mediaLevelType;

    @SerializedName("desc")
    @Expose
    private String desc;

    @SerializedName("icon_type")
    @Expose
    private String iconType;

    @SerializedName("cont_type")
    @Expose
    private String contType;

    @SerializedName("type_content")
    @Expose
    private String typeContent;

    @SerializedName("con_uuid")
    @Expose
    private String conUuid;


    public CatalogueDetailsModel() {
    }

    protected CatalogueDetailsModel(Parcel in) {
        if (in.readByte() == 0) {
            active = null;
        } else {
            active = in.readInt();
        }
        if (in.readByte() == 0) {
            completed = null;
        } else {
            completed = in.readInt();
        }
        uuid = in.readString();
        name = in.readString();
        code = in.readString();
        if (in.readByte() == 0) {
            order = null;
        } else {
            order = in.readInt();
        }
        parent = in.readString();
        modified = in.readString();
        icon = in.readString();
        path = in.readString();
        mediaLevelType = in.readString();
        desc = in.readString();
        iconType = in.readString();
        contType = in.readString();
        typeContent = in.readString();
        conUuid = in.readString();
    }

    public static final Creator<CatalogueDetailsModel> CREATOR = new Creator<CatalogueDetailsModel>() {
        @Override
        public CatalogueDetailsModel createFromParcel(Parcel in) {
            return new CatalogueDetailsModel(in);
        }

        @Override
        public CatalogueDetailsModel[] newArray(int size) {
            return new CatalogueDetailsModel[size];
        }
    };

    public Integer getCompleted() {
        return completed;
    }

    public void setCompleted(Integer completed) {
        this.completed = completed;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public UDF1 getUdf1() {
        return udf1;
    }

    public void setUdf1(UDF1 udf1, String colorCode) {
        this.udf1 = udf1;
        if (udf1!=null)
            udf1.setColorCode(colorCode);
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMediaLevelType() {
        return mediaLevelType;
    }

    public void setMediaLevelType(String mediaLevelType) {
        this.mediaLevelType = mediaLevelType;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getIconType() {
        return iconType;
    }

    public void setIconType(String iconType) {
        this.iconType = iconType;
    }

    public String getContType() {
        return contType;
    }

    public void setContType(String contType) {
        this.contType = contType;
    }

    public String getTypeContent() {
        return typeContent;
    }

    public void setTypeContent(String typeContent) {
        this.typeContent = typeContent;
    }

    public String getConUuid() {
        return conUuid;
    }

    public void setConUuid(String conUuid) {
        this.conUuid = conUuid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        if (active == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(active);
        }
        if (completed == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(completed);
        }
        parcel.writeString(uuid);
        parcel.writeString(name);
        parcel.writeString(code);
        if (order == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(order);
        }
        parcel.writeString(parent);
        parcel.writeString(modified);
        parcel.writeString(icon);
        parcel.writeString(path);
        parcel.writeString(mediaLevelType);
        parcel.writeString(desc);
        parcel.writeString(iconType);
        parcel.writeString(contType);
        parcel.writeString(typeContent);
        parcel.writeString(conUuid);
    }

//    public void setColor(String string) {
//        if (udf1!=null)
//            udf1.setColorCode(string);
//    }

    public class UDF1 {
        @SerializedName("color_code")
        @Expose
        private String colorCode;

        public UDF1() {
        }

        public String getColorCode() {
            return colorCode;
        }

        public void setColorCode(String colorCode) {
            this.colorCode = colorCode;
        }
    }
}
