package oelp.mahiti.org.newoepl.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import oelp.mahiti.org.newoepl.database.DatabaseHandlerClass;
import oelp.mahiti.org.newoepl.models.QuestionAnswerModel;
import oelp.mahiti.org.newoepl.models.QuestionChoicesModel;
import oelp.mahiti.org.newoepl.models.QuestionModel;
import oelp.mahiti.org.newoepl.utils.Constants;

/**
 * Created by RAJ ARYAN on 14/08/19.
 */
public class QuestionAnswerViewModel extends AndroidViewModel {
    private String sectionUUID;
    DatabaseHandlerClass handlerClass;
    private List<QuestionModel> questionModelList;
    private MutableLiveData<Boolean> showDialog = new MutableLiveData<>();
    private MutableLiveData<Boolean> onSubmitClick = new MutableLiveData<>();
    private List<QuestionChoicesModel> choicesModelList = new ArrayList<>();
    HashMap<String, ArrayList<String>> answerhashmap = new HashMap<String, ArrayList<String>>();
    private MutableLiveData<List<QuestionAnswerModel>> quesAnsModelList = new MutableLiveData<>();


    public QuestionAnswerViewModel(@NonNull Application application) {
        super(application);
        handlerClass = new DatabaseHandlerClass(application);
    }

    public void onSubmitClick() {
       onSubmitClick.setValue(true);
    }


    public void setSectionUUID(String sectionUUID) {
        this.sectionUUID = sectionUUID;
        parseListOfQuestion(sectionUUID);
    }

    private void parseListOfQuestion(String sectionUUID) {
        questionModelList = handlerClass.getQuestion("", sectionUUID, Constants.QA);
        Log.i("QuestionList", String.valueOf(questionModelList.size()));
        if (questionModelList.size() == 0) {
            showDialog.setValue(true);
        } else {
            getQuestionAnswer();
        }

    }

    private void getQuestionAnswer() {
        List<QuestionAnswerModel> modelList = new ArrayList<>();
        QuestionAnswerModel model;
        for (int i = 0; i < questionModelList.size(); i++) {
            choicesModelList = parseDataFromDBChoices(questionModelList.get(i).getId());
            model = new QuestionAnswerModel();
            model.setQuestionModel(questionModelList.get(i));
            model.setChoicesModelList(choicesModelList);
            modelList.add(model);
        }
        quesAnsModelList.setValue(modelList);
    }

    public MutableLiveData<Boolean> getShowDialog() {
        return showDialog;
    }

    public MutableLiveData<Boolean> getSubmitClick() {
        return onSubmitClick;
    }


    public MutableLiveData<List<QuestionAnswerModel>> getQuestionAnswerModel() {
        return quesAnsModelList;
    }

    private List<QuestionChoicesModel> parseDataFromDBChoices(int Questionid) {
        return handlerClass.getChoices(Questionid);
    }


}
