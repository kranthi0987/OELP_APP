package mahiti.org.oelp.models;

import java.util.List;

/**
 * Created by sandeep HR on 22/01/19.
 */
public class QuestionAnswerModel {

    private QuestionModel questionModel;
    private List<QuestionChoicesModel> choicesModelList;

    private int id;
    private String offlineData;
    private int syncStatus;
    private int videoId;
    private String dateTime;

    private String quetsionText;
    private int quetsionId;
    private String choiceText;
    private int choiceId;
    private int score;
    private int testAttempt;
    private int totalMarks;
    private int scoredObtain;
    private boolean isCorrect;


    public QuestionAnswerModel(String quetsionText, int quetsionId, String choiceText, int choiceId, int score, boolean isCorrect) {
        this.quetsionText = quetsionText;
        this.quetsionId = quetsionId;
        this.choiceText = choiceText;
        this.choiceId = choiceId;
        this.score = score;
        this.isCorrect = isCorrect;
    }

    public QuestionAnswerModel(int scoredObtain, int totalMarks, int testAttempt) {
        this.scoredObtain = scoredObtain;
        this.totalMarks = totalMarks;
        this.testAttempt = testAttempt;
    }

    public int getTestAttempt() {
        return testAttempt;
    }

    public int getTotalMarks() {
        return totalMarks;
    }

    public String getQuetsionText() {
        return quetsionText;
    }

    public int getQuetsionId() {
        return quetsionId;
    }

    public String getChoiceText() {
        return choiceText;
    }

    public int getChoiceId() {
        return choiceId;
    }

    public int getScore() {
        return score;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public QuestionAnswerModel() {
    }

    public QuestionModel getQuestionModel() {
        return questionModel;
    }

    public void setQuestionModel(QuestionModel questionModel) {
        this.questionModel = questionModel;
    }

    public List<QuestionChoicesModel> getChoicesModelList() {
        return choicesModelList;
    }

    public void setChoicesModelList(List<QuestionChoicesModel> choicesModelList) {
        this.choicesModelList = choicesModelList;
    }



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
