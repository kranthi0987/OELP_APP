package mahiti.org.oelp.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mahiti.org.oelp.R;
import mahiti.org.oelp.database.DatabaseHandlerClass;
import mahiti.org.oelp.models.QuestionAnswerModel;
import mahiti.org.oelp.models.QuestionChoicesModel;
import mahiti.org.oelp.models.QuestionModel;
import mahiti.org.oelp.utils.AppUtils;
import mahiti.org.oelp.utils.CheckNetwork;
import mahiti.org.oelp.utils.Constants;
import mahiti.org.oelp.utils.Logger;

/**
 * Created by RAJ ARYAN on 14/08/19.
 */
public class QuestionAnswerViewModel extends AndroidViewModel {
    private static final String TAG = QuestionAnswerViewModel.class.getSimpleName();
    private String mediaUUID;
    DatabaseHandlerClass handlerClass;
    private List<QuestionModel> questionModelList;
    private MutableLiveData<Boolean> showDialog = new MutableLiveData<>();
    private MutableLiveData<Boolean> onSubmitClick = new MutableLiveData<>();
    private List<QuestionChoicesModel> choicesModelList = new ArrayList<>();
    HashMap<String, ArrayList<String>> answerhashmap = new HashMap<String, ArrayList<String>>();
    private MutableLiveData<List<QuestionAnswerModel>> quesAnsModelList = new MutableLiveData<>();
    private Context context;


    public QuestionAnswerViewModel(@NonNull Application application) {
        super(application);
        context = application;
        handlerClass = new DatabaseHandlerClass(application);
    }

    public void onSubmitClick() {
       onSubmitClick.setValue(true);
    }


    public void setMediaUUID(String mediaUUID) {
        this.mediaUUID = mediaUUID;
        parseListOfQuestion(mediaUUID);
    }

    private void parseListOfQuestion(String mediaUUID) {
        questionModelList = handlerClass.getQuestion(mediaUUID, "", Constants.QA);
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


    public void saveValueToDb(JSONArray array, String mediaUUID) {
        List<QuestionAnswerModel> arrayList = new ArrayList<>();
        handlerClass.saveAnsweredQuestion(array.toString(), Constants.QUESTION_ANSWER_ASYNC, mediaUUID, AppUtils.getDateTime());  //0 Sync, 1 Async
        Logger.logD(TAG, "Data"+array.toString());
        if (CheckNetwork.checkNet(context)) {
            arrayList = handlerClass.getAnsweredQuestion(mediaUUID);
            if (arrayList.size() != 0) {
                try {
                    JSONArray ar = new JSONArray(arrayList.get(0).getOfflineData());
                    String datetime = arrayList.get(0).getDateTime();
                    checkInternetAndCall(ar, mediaUUID, datetime);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Toast.makeText(context, context.getString(R.string.you_are_offline), Toast.LENGTH_SHORT).show();
//            launchActivity();
        }
    }

    private void checkInternetAndCall(JSONArray ansJson, String mediaUUID, String dateTime) {
        if (CheckNetwork.checkNet(context))
            postQA(ansJson, mediaUUID, dateTime);
        else
            Toast.makeText(context, context.getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
    }

    public void postQA(JSONArray questionAndAnswerRequestModel, String videoId, String datetime) {

//        ApiInterface apiService = RetrofitClass.getAPIService();
//        Call<QuestionAndAnswerResponsemodel> call = apiService.questionAndAnswerResponseModelCall(new MySharedPreferences().getUserId().toString(), String.valueOf(videoId), questionAndAnswerRequestModel, datetime);
//        Logger.logD(QuestionsAndAnswersFragment.class.getSimpleName(), "QUESTION_AND_ANSWER_URL : " + RetrofitConstant.BASE_URL + RetrofitConstant.QUESTION_AND_ANSWER_URL);
//        call.enqueue(new Callback<QuestionAndAnswerResponsemodel>() {
//            @Override
//            public void onResponse(Call<QuestionAndAnswerResponsemodel> call, Response<QuestionAndAnswerResponsemodel> response) {
//                AppUtils.dismissProgressDialog();
//                if (response.body() != null) {
//                    if (response.body().getStatus()) {
//                        ToastUtils.displayToast(response.body().getMessage(), getActivity());
//                        manager.deleteSyncQuestionAns(videoId);
//                        launchActivity();
//                    } else {
//                        ToastUtils.displayToast(response.body().getMessage(), getActivity());
//                    }
//                } else {
//                    ToastUtils.displayToast(getString(R.string.SOMETHING_WRONG), getActivity());
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<QuestionAndAnswerResponsemodel> call, Throwable t) {
//                AppUtils.dismissProgressDialog();
//                ToastUtils.displayToast(t.getMessage(), getActivity());
//                Log.e(TAG, t.toString());
//
//            }
//        });
    }

    public int getTestAttemptCount(String mediaUUID) {
        handlerClass.getTestAttemptedCount(mediaUUID);
        return 0;
    }
}
