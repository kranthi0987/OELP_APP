package mahiti.org.oelp.views.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import mahiti.org.oelp.R;
import mahiti.org.oelp.databinding.ActivityTestBinding;
import mahiti.org.oelp.models.QuestionAnswerIdModel;
import mahiti.org.oelp.models.QuestionAnswerModel;
import mahiti.org.oelp.models.QuestionChoicesModel;
import mahiti.org.oelp.models.SubmittedAnswerResponse;
import mahiti.org.oelp.utils.AppUtils;
import mahiti.org.oelp.utils.CheckNetwork;
import mahiti.org.oelp.utils.Constants;
import mahiti.org.oelp.utils.MySharedPref;
import mahiti.org.oelp.viewmodel.TestViewModel;


public class TestActivity extends AppCompatActivity {

    private static final String TAG = TestActivity.class.getSimpleName();
    private TestViewModel testViewModel;
    ActivityTestBinding binding;
    private Toolbar toolbar;
    private AlertDialog dialog;
    private LinearLayout questionsAnswersRadioLayout;
    private List<String> questionsStringList = new ArrayList<>();
    private HashMap<String, RadioGroup> radioGroupMap = new HashMap<>();
    private List<QuestionAnswerModel> answeredList = new ArrayList<>();
    private String mediaUUID;
    private String sectionUUID;
    private String unitUUID;
    private int dcfId=0;
    private String videoTitle;
    private String uriPath;
    private String userId;
    private String mediaTrackerApi;
    private ScrollView scrollView;
    private List<QuestionAnswerModel> questionAnswerModelLsit;
    private List<QuestionAnswerModel> scoreAndAttempt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_test);
        testViewModel = ViewModelProviders.of(this).get(TestViewModel.class);
        binding.setTestViewModel(testViewModel);
        binding.setLifecycleOwner(this);
        toolbar = findViewById(R.id.white_toolbar);
        toolbar = findViewById(R.id.white_toolbar);
        scrollView = findViewById(R.id.scrollView);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }
        questionsAnswersRadioLayout = binding.questionsAnswersRadioLayout;
        toolbar.inflateMenu(R.menu.teacher_menu);
        getIntentData();
        testViewModel.getShowDialog().observe(this, aBoolean -> {
            if (aBoolean != null) {
                showDialog();
                scrollView.setVisibility(View.GONE);
            }

        });

        testViewModel.getSubmitClick().observe(this, aBoolean -> {
            if (aBoolean != null)
                if (validationForRadioButton()) {
                    prepareDataToSubmit();
                    Toast.makeText(this, "Submitted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getResources().getString(R.string.select_all_question), Toast.LENGTH_SHORT).show();
                }
        });
        testViewModel.getQuestionAnswerModel().observe(this, questionAnswerModels -> {
            if (questionAnswerModels != null && !questionAnswerModels.isEmpty()) {
                questionAnswerModelLsit = questionAnswerModels;
                setLayoutForQuestionAnswer(questionAnswerModels);
                scrollView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void prepareDataToSubmit() {
        List<QuestionAnswerModel> finalModelLsit = new ArrayList<>();
        String questionText;
        String choiceText;
        String explainText;
        int choiceId;
        int questionId;
        int score;
        boolean isCorrect;
        RadioGroup radioGroup;
        QuestionAnswerModel questionAnswerModel;
        for (int i = 0; i < questionsStringList.size(); i++) {
            radioGroup = radioGroupMap.get(questionsStringList.get(i));
            questionText = questionAnswerModelLsit.get(i).getQuestionModel().getText();
            questionId = questionAnswerModelLsit.get(i).getQuestionModel().getId();
            QuestionChoicesModel choicesModel = getChoiceModel(questionAnswerModelLsit.get(i).getChoicesModelList(), radioGroup.getCheckedRadioButtonId());
            choiceId = choicesModel.getId();
            choiceText = choicesModel.getText();
//            score  = choicesModel.getScore();
            score  = 2;
            isCorrect = choicesModel.getCorrect();
            if (isCorrect) {
                explainText = choicesModel.getAnswerExplaination();
            } else {
                explainText = "No";
            }
            questionAnswerModel = new QuestionAnswerModel(questionText, questionId, choiceText, choiceId, score, isCorrect, explainText);
            finalModelLsit.add(questionAnswerModel);
        }
        submitAnswerToTable(finalModelLsit);
    }



    private QuestionChoicesModel getChoiceModel(List<QuestionChoicesModel> choicesModelList, int checkedRadioButtonId) {
        QuestionChoicesModel questionChoicesModel = null;
        for (int i = 0; i < choicesModelList.size(); i++) {
            if (checkedRadioButtonId == choicesModelList.get(i).getRadioButtonId()) {
                questionChoicesModel = choicesModelList.get(i);
            }
        }
        return questionChoicesModel;
    }

    private void submitAnswerToTable(List<QuestionAnswerModel> finalModelLsit) {
        List<String> scoreList = calculateScore(finalModelLsit);
        JSONArray arrayPreview = new JSONArray();
        JSONArray arrayServer = new JSONArray();
        try {
            for (int i = 0; i < finalModelLsit.size(); i++) {
                JSONObject jsonObjectPreviewText = new JSONObject();
                JSONObject jsonObjectServerText = new JSONObject();
                try {
                    /*
                     * Preparing JSon for Preview to be shown first we are saving in Db with QuestionText, AnswerText and Explain Text
                     * */

                    jsonObjectPreviewText.put("q_text", finalModelLsit.get(i).getQuetsionText());
                    jsonObjectPreviewText.put("a_text", finalModelLsit.get(i).getChoiceText());
                    jsonObjectPreviewText.put("exp_text", finalModelLsit.get(i).getAnswerExplain());
                    boolean isCorrect = finalModelLsit.get(0).isCorrect();
                    jsonObjectPreviewText.put("correct", isCorrect);
                    /*
                     * Preparing Json for server side with question id and answer id
                     * */

                    jsonObjectServerText.put("q_id", finalModelLsit.get(i).getQuetsionId());
                    jsonObjectServerText.put("res_id", finalModelLsit.get(i).getChoiceId());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                arrayServer.put(jsonObjectServerText);
                arrayPreview.put(jsonObjectPreviewText);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG, "server Json: " + arrayServer);
        Log.i(TAG, "preview Json: " + arrayPreview);

        SubmittedAnswerResponse model = new SubmittedAnswerResponse();
        model.setCreationKey(AppUtils.getUUID());
        model.setMediacontent(mediaUUID);
        model.setSectionUUID(sectionUUID);
        model.setUnitUUID(unitUUID);
        /*
         * Coverting Json Array to Lsit*/

        List<QuestionAnswerIdModel> responses;
        Type listType = new TypeToken<List<QuestionAnswerIdModel>>() {}.getType();
        responses= new Gson().fromJson(String.valueOf(arrayServer), listType);
        model.setResponse(responses);
        model.setSubmissionDate(AppUtils.getDateTime());
        model.setAttempts(0);
        model.setScore(Float.parseFloat(scoreList.get(0)));
        model.setTotal(scoreList.get(1));
        model.setSyncStatus(1);
        List<SubmittedAnswerResponse> list = new ArrayList<>();
        list.add(model);

        testViewModel.saveValueToDb(list);

        moveToNextActivity();


    }

    private void moveToNextActivity() {
        Intent intent = new Intent(this, PreviewActivity.class);
        intent.putExtra("mediaUUID", mediaUUID);
        intent.putExtra("sectionUUID", sectionUUID);
        intent.putExtra("videoTitle", videoTitle);
        intent.putExtra("userId", userId);
        intent.putExtra("uriPath", uriPath);
        intent.putExtra("mediaTrackerApi", mediaTrackerApi);
        startActivity(intent);
        this.finish();
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
    }

    private List<String> calculateScore(List<QuestionAnswerModel> finalModelLsit) {
        List<String> scoreList = new ArrayList<>();
        int score = 0;
        int totalScore = 0;
        for (QuestionAnswerModel questionAnswerModel : finalModelLsit) {
            if (questionAnswerModel.isCorrect()) {
                score = score + questionAnswerModel.getScore();
            }
            totalScore++;

        }
        scoreList.add(String.valueOf(score));
        scoreList.add(String.valueOf(totalScore));
        return scoreList;
    }


    public boolean validationForRadioButton() {
        boolean status = true;
        for (int i = 0; i < questionsStringList.size(); i++) {
            RadioGroup rg = radioGroupMap.get(questionsStringList.get(i));
            if (rg.getCheckedRadioButtonId() == -1) {
                status = false;
            }
        }
        return status;
    }


    private void setLayoutForQuestionAnswer(List<QuestionAnswerModel> questionAnswerModels) {
        TextView tvQuestionText;
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int ij = 1;
        for (int i = 0; i < questionAnswerModels.size(); i++) {
            View v = inflater.inflate(R.layout.subquestions, null);
            RadioGroup radioGroup = v.findViewById(R.id.radioGroup);
            radioGroup.setId(questionAnswerModels.get(i).getQuestionModel().getId());
            tvQuestionText = v.findViewById(R.id.questionText);
            String questionText = i + 1 + ". " + questionAnswerModels.get(i).getQuestionModel().getText();
            tvQuestionText.setText(questionText);
            questionsStringList.add(questionAnswerModels.get(i).getQuestionModel().getId().toString());
            List<QuestionChoicesModel> choice = questionAnswerModels.get(i).getChoicesModelList();
            for (int j = 0; j < choice.size(); j++) {
                AppCompatRadioButton questionChoiceRadioButton = new AppCompatRadioButton(this);
                RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 5, 0, 0);
                questionChoiceRadioButton.setLayoutParams(params);
                questionChoiceRadioButton.setId(ij);
                choice.get(j).setRadioButtonId(ij);
                choice.get(j).setRadioCheckedPosition(j);
                if (Build.VERSION.SDK_INT >= 21) {
                    ColorStateList colorStateList = new ColorStateList(
                            new int[][]{

                                    new int[]{-android.R.attr.state_checked},
                                    new int[]{android.R.attr.state_checked} //enabled
                            },
                            new int[]{

                                    getResources().getColor(R.color.grey) //disabled
                                    , getResources().getColor(R.color.red) //enabled

                            }
                    );
                    questionChoiceRadioButton.setButtonTintList(colorStateList);//set the color tint list
//                    questionChoiceRadioButton.invalidate(); //could not be necessary
                }
                questionChoiceRadioButton.setText(choice.get(j).getText());
                questionChoiceRadioButton.setTextColor(getResources().getColor(R.color.black));
                radioGroup.addView(questionChoiceRadioButton);
                ij++;
            }
            radioGroupMap.put(questionAnswerModels.get(i).getQuestionModel().getId().toString(), radioGroup);

            radioGroup.setOnCheckedChangeListener((radioGroup1, checkedId) -> {

            });
            questionsAnswersRadioLayout.addView(v);
        }
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
        builder.setMessage(getResources().getString(R.string.there_is_no_question_to_display));
        builder.setNegativeButton(R.string.ok, (dialog, id) -> onBackPressed());
        dialog = builder.create();
        dialog.show();
    }

    private void getIntentData() {

        mediaUUID = getIntent().getStringExtra("mediaUUID");
        sectionUUID = getIntent().getStringExtra("sectionUUID");
        unitUUID = getIntent().getStringExtra("unitUUID");
        dcfId = getIntent().getIntExtra("dcfId",0);
        videoTitle = getIntent().getStringExtra("videoTitle");
        uriPath = getIntent().getStringExtra("uriPath");
        userId = getIntent().getStringExtra("userId");
        mediaTrackerApi = getIntent().getStringExtra("mediaTrackerApi");
        testViewModel.setDCFId(dcfId);
        /*testViewModel.setDCFId(sectionUUID);*/
        String completeTilte = videoTitle.concat(getResources().getString(R.string.hiphen_question));
        binding.tvTitle.setText(completeTilte);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.aboutus:
                ShowAboutUsActivity();
                return true;
            case R.id.logout:
                AppUtils.makeUserLogout(this);
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void ShowAboutUsActivity() {
        AppUtils.showAboutUsActivity(this);
    }

    @Override
    public void onBackPressed() {
        TestActivity.this.finish();
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
    }
}
