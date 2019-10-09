package mahiti.org.oelp.viewmodel;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mahiti.org.oelp.R;
import mahiti.org.oelp.database.DAOs.ChoicesDao;
import mahiti.org.oelp.database.DAOs.QuestionDao;
import mahiti.org.oelp.database.DAOs.SurveyResponseDao;
import mahiti.org.oelp.database.DatabaseHandlerClass;
import mahiti.org.oelp.models.MobileVerificationResponseModel;
import mahiti.org.oelp.models.QuestionAnswerIdModel;
import mahiti.org.oelp.models.QuestionAnswerModel;
import mahiti.org.oelp.models.QuestionChoicesModel;
import mahiti.org.oelp.models.QuestionModel;
import mahiti.org.oelp.models.SubmittedAnswerResponse;
import mahiti.org.oelp.services.ApiInterface;
import mahiti.org.oelp.services.RetrofitClass;
import mahiti.org.oelp.services.RetrofitConstant;
import mahiti.org.oelp.utils.AppUtils;
import mahiti.org.oelp.utils.CheckNetwork;
import mahiti.org.oelp.utils.Constants;
import mahiti.org.oelp.utils.Logger;
import mahiti.org.oelp.utils.MySharedPref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by RAJ ARYAN on 14/08/19.
 */
public class TestViewModel extends AndroidViewModel {
    private static final String TAG = TestViewModel.class.getSimpleName();
    private int dcfId;
    SurveyResponseDao handlerClass;
    private List<QuestionModel> questionModelList;
    private MutableLiveData<Boolean> showDialog = new MutableLiveData<>();
    private MutableLiveData<Boolean> onSubmitClick = new MutableLiveData<>();
    private List<QuestionChoicesModel> choicesModelList = new ArrayList<>();
    HashMap<String, ArrayList<String>> answerhashmap = new HashMap<String, ArrayList<String>>();
    private MutableLiveData<List<QuestionAnswerModel>> quesAnsModelList = new MutableLiveData<>();
    private Context context;


    public TestViewModel(@NonNull Application application) {
        super(application);
        context = application;
        handlerClass = new SurveyResponseDao(application);
    }

    public void onSubmitClick() {
        onSubmitClick.setValue(true);
    }


    public void setDCFId(int dcfId) {
        this.dcfId = dcfId;
        parseListOfQuestion(dcfId);
    }

    private void parseListOfQuestion(int dcfId) {
//        questionModelList = handlerClass.getQuestion(dcfId, "", Constants.QA);
        questionModelList = new QuestionDao(context).getQuestion("", "", Constants.QA, dcfId);
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
        return new ChoicesDao(context).getChoices(Questionid);
    }


    public void saveValueToDb(List<SubmittedAnswerResponse> modelList) {
        if (!modelList.isEmpty()) {
            handlerClass.insertAnsweredQuestion(modelList);
            new MySharedPref(context).writeBoolean(Constants.VALUE_CHANGED, true);
        }
    }


    public int getTestAttemptCount(String mediaUUID) {
        return handlerClass.getAttemptFromDb(mediaUUID, 0);
    }


}
