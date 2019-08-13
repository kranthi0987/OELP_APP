package oelp.mahiti.org.newoepl.models;

/**
 * Created by sandeep HR on 22/01/19.
 */
public class QuestionAnswerModel {
    int id;
    String offlineData;
    int syncStatus;
    int videoId;
    String dateTime;

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public QuestionAnswerModel(int id, String offlineData, int syncStatus, int videoId, String dateTime) {
        this.id = id;
        this.offlineData = offlineData;
        this.syncStatus = syncStatus;
        this.videoId = videoId;
        this.dateTime = dateTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOfflineData() {
        return offlineData;
    }

    public void setOfflineData(String offlineData) {
        this.offlineData = offlineData;
    }

    public int getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(int syncStatus) {
        this.syncStatus = syncStatus;
    }

    public int getVideoId() {
        return videoId;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }
}
