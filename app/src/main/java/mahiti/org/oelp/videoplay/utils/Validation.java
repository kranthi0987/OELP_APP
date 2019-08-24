package mahiti.org.oelp.videoplay.utils;;

/**
 * Created by mahiti on 13/5/16.
 */
public class Validation {

    int status;
    String message;
    int primaryId;
    int videoId;
    String videoName;
    String key;
    String updateTime;


    /**
     *
     * @return
     */
    public int getStatus() {
        return status;
    }

    /**
     *
     * @param status
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     *
     * @return
     */
    public String getMessage() {
        return message;
    }

    /**
     *
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     *
     * @return
     */
    public int getVideoId() {
        return videoId;
    }

    /**
     *
     * @param videoId
     */
    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }

    /**
     *
     * @return
     */
    public int getPrimaryId() {
        return primaryId;
    }

    /**
     *
     * @param primaryId
     */
    public void setPrimaryId(int primaryId) {
        this.primaryId = primaryId;
    }

    /**
     *
     * @return
     */
    public String getKey() {
        return key;
    }

    /**
     *
     * @param key
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     *
     * @return
     */
    public String getVideoName() {
        return videoName;
    }

    /**
     *
     * @param videoName
     */
    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    /**
     *
     * @return
     */
    public String getUpdatTime() {
        return updateTime;
    }

    /**
     *
     * @param updatTime
     */
    public void setUpdatTime(String updatTime) {
        this.updateTime = updatTime;
    }


}
