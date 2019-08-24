package mahiti.org.oelp.models;

/**
 * Created by sandeep HR on 30/01/19.
 */
public class VideoDownlaodedModel {

    String downloadReturnMEssage;
    String videoPath;
    String videoName;
    int videoId;
    String topicName;

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public VideoDownlaodedModel(String downloadReturnMEssage, String videoPath, String videoName, int videoId, String topicName) {
        this.downloadReturnMEssage = downloadReturnMEssage;
        this.videoPath = videoPath;
        this.videoName = videoName;
        this.videoId = videoId;
        this.topicName = topicName;

    }

    public int getVideoId() {
        return videoId;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }

    public String getDownloadReturnMEssage() {
        return downloadReturnMEssage;
    }

    public void setDownloadReturnMEssage(String downloadReturnMEssage) {
        this.downloadReturnMEssage = downloadReturnMEssage;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }
}
