package oelp.mahiti.org.newoepl.models;

/**
 * Created by sandeep HR on 27/12/18.
 */
public class VideosModel {
    private Integer id;
    private Integer introSummeryType;
    private Integer active;
    private Integer downloadStatus;
    private Integer sectionId;
    private String videoName;
    private String path;
    private String modified;
    private long fileSize;

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public Integer getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(Integer downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIntroSummeryType() {
        return introSummeryType;
    }

    public void setIntroSummeryType(Integer introSummeryType) {
        this.introSummeryType = introSummeryType;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public Integer getSectionId() {
        return sectionId;
    }

    public void setSectionId(Integer sectionId) {
        this.sectionId = sectionId;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }
}
