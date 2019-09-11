package mahiti.org.oelp.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mahiti.org.oelp.R;
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
    private String dcfId;
    DatabaseHandlerClass handlerClass;
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
        handlerClass = new DatabaseHandlerClass(application);
    }

    public void onSubmitClick() {
        onSubmitClick.setValue(true);
    }


    public void setDCFId(String dcfId) {
        this.dcfId = dcfId;
        parseListOfQuestion(dcfId);
    }

    private void parseListOfQuestion(String dcfId) {
//        questionModelList = handlerClass.getQuestion(dcfId, "", Constants.QA);
        questionModelList = handlerClass.getQuestion("", "", Constants.QA, dcfId);
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


    public void saveValueToDb(JSONArray arrayServer, JSONArray arrayPreview, String mediaUUID, List<String> score, String testUUID, String sectionUUID, String unitUUID) {
//        String unitUUID = "";
        int attempt = 0;
        List<SubmittedAnswerResponse> arrayList = new ArrayList<>();
        handlerClass.insertAnsweredQuestion(testUUID, mediaUUID, sectionUUID, unitUUID, arrayServer.toString(), AppUtils.getDateTime(), score, attempt, 0, arrayPreview.toString());  //0 Sync, 1 Async
        new MySharedPref(context).writeBoolean(Constants.VALUE_CHANGED, true);

        if (CheckNetwork.checkNet(context)) {
            arrayList = handlerClass.getAnsweredQuestion("", 1);
            if (arrayList.size() != 0) {
                for (SubmittedAnswerResponse model : arrayList) {
                    checkInternetAndCall(model);
                }
            } else {
                Toast.makeText(context, context.getString(R.string.you_are_offline), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkInternetAndCall(SubmittedAnswerResponse model) {
        if (CheckNetwork.checkNet(context))
            postQA(model);
        else
            Toast.makeText(context, context.getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
    }

    public void postQA(SubmittedAnswerResponse model) {
        String response = "";
        Gson gson = new Gson();
        try {
            response = gson.toJson(model.getResponse());
        } catch (Exception ex) {
            Logger.logE(TAG, "Exception in model to json :" + ex.getMessage(), ex);
        }
        ApiInterface apiService = RetrofitClass.getAPIService();
        String userId = new MySharedPref(context).readString(Constants.USER_ID, "");
        Call<MobileVerificationResponseModel> call = apiService.submitAnswer(userId, model.getCreationKey(), model.getSectionUUID(), model.getUnitUUID(), model.getSubmissionDate(), model.getMediacontent(),
                model.getScore(), model.getAttempts(), response);
        Logger.logD(TAG, "QUESTION_AND_ANSWER_URL : " + RetrofitConstant.BASE_URL2 + RetrofitConstant.SUBMIT_ANSWER);
        call.enqueue(new Callback<MobileVerificationResponseModel>() {
            @Override
            public void onResponse(Call<MobileVerificationResponseModel> call, Response<MobileVerificationResponseModel> response) {
                Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                handlerClass.updateSyncStatus();
            }

            @Override
            public void onFailure(Call<MobileVerificationResponseModel> call, Throwable t) {


            }
        });
    }

    public int getTestAttemptCount(String mediaUUID) {
        return handlerClass.getAttemptFromDb(mediaUUID, 0);
    }


}
