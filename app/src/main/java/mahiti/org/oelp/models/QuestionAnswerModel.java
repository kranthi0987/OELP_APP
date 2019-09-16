package mahiti.org.oelp.models;

import java.util.List;


public class QuestionAnswerModel {

    private QuestionModel questionModel;
    private List<QuestionChoicesModel> choicesModelList;

    private int id;
    private String offlineData;
    private int syncStatus;
    private String videoId;
    private String previewText;
    private String dateTime;

    private String quetsionText;
    private String answerExplain;
    private int quetsionId;
    private String choiceText;
    private int choiceId;
    private int score;
    private int testAttempt;
    private int totalMarks;
    private int marksObtain;
    private String modifiedDate;
    private boolean isCorrect;


    private String sectionUUID;
    private String unitUUID;
    private Integer attempt;

    public void setScore(int score) {
        this.score = score;
    }

    public String getSectionUUID() {
        return sectionUUID;
    }

    public void setSectionUUID(String sectionUUID) {
        this.sectionUUID = sectionUUID;
    }

    public String getUnitUUID() {
        return unitUUID;
    }

    public void setUnitUUID(String unitUUID) {
        this.unitUUID = unitUUID;
    }

    public Integer getAttempt() {
        return attempt;
    }

    public void setAttempt(Integer attempt) {
        this.attempt = attempt;
    }

    public QuestionAnswerModel(String quetsionText, int quetsionId, String choiceText, int choiceId, int score, boolean isCorrect, String answerExplain) {
        this.quetsionText = quetsionText;
        this.quetsionId = quetsionId;
        this.choiceText = choiceText;
        this.choiceId = choiceId;
        this.score = score;
        this.isCorrect = isCorrect;
        this.answerExplain = answerExplain;
    }

    public String getAnswerExplain() {
        return answerExplain;
    }

    public QuestionAnswerModel(int marksObtain, int totalMarks, int testAttempt) {
        this.marksObtain = marksObtain;
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

    public QuestionAnswerModel(int id, String offlineData, int syncStatus, String videoId, String previewText,String dateTime, int marksObtain, int totalMarks, String modifiedDate) {
        this.id = id;
        this.offlineData = offlineData;
        this.syncStatus = syncStatus;
        this.videoId = videoId;
        this.previewText = previewText;
        this.dateTime = dateTime;
        this.totalMarks = totalMarks;
        this.marksObtain = marksObtain;
        this.modifiedDate = modifiedDate;
    }

    public void setQuetsionText(String quetsionText) {
        this.quetsionText = quetsionText;
    }

    public void setAnswerExplain(String answerExplain) {
        this.answerExplain = answerExplain;
    }

    public void setQuetsionId(int quetsionId) {
        this.quetsionId = quetsionId;
    }

    public void setChoiceText(String choiceText) {
        this.choiceText = choiceText;
    }

    public void setChoiceId(int choiceId) {
        this.choiceId = choiceId;
    }


    public void setTestAttempt(int testAttempt) {
        this.testAttempt = testAttempt;
    }

    public void setTotalMarks(int totalMarks) {
        this.totalMarks = totalMarks;
    }

    public int getMarksObtain() {
        return marksObtain;
    }

    public void setMarksObtain(int marksObtain) {
        this.marksObtain = marksObtain;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public String getPreviewText() {
        return previewText;
    }

    public void setPreviewText(String previewText) {
        this.previewText = previewText;
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

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }
}
