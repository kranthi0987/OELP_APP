package oelp.mahiti.org.newoepl.fileandvideodownloader;

/**
 * Created by sandeep HR on 06/05/19.
 */
public class FileModel {
    private  String fileName;
    private  int fileId;
    private  String fileUrl;
    private  String uuid;
    private int position;

    public FileModel(String fileName, String fileUrl) {
        this.fileName = fileName;
        this.fileUrl = fileUrl;
    }

    public FileModel(String fileName, String fileUrl, String uuid) {
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.uuid = uuid;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }



    public FileModel(String fileName, int fileId, String fileUrl, String uuid) {
        this.fileName = fileName;
        this.fileId = fileId;
        this.fileUrl = fileUrl;
        this.uuid = uuid;
    }

    public FileModel(String fileName, int fileId, String fileUrl) {
        this.fileName = fileName;
        this.fileId = fileId;
        this.fileUrl = fileUrl;
    }



    public FileModel() {

    }



    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}
