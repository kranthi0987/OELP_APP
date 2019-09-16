package mahiti.org.oelp.views.activities;

import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.res.ColorStateList;
import androidx.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mahiti.org.oelp.R;
import mahiti.org.oelp.databinding.ActivityQuestionAnswerBinding;
import mahiti.org.oelp.models.QuestionAnswerModel;
import mahiti.org.oelp.models.QuestionChoicesModel;
import mahiti.org.oelp.utils.AppUtils;
import mahiti.org.oelp.viewmodel.QuestionAnswerViewModel;


public class QuestionAnswerActivity extends AppCompatActivity {

    private static final String TAG = QuestionAnswerActivity.class.getSimpleName();
    private QuestionAnswerViewModel viewModel;
    ActivityQuestionAnswerBinding binding;
    private String videoTitle;
    private Toolbar toolbar;
    private AlertDialog dialog;
    private LinearLayout questionsAnswersRadioLayout;
    private List<String> questionsStringList = new ArrayList<>();
    private HashMap<String, RadioGroup> radioGroupMap = new HashMap<>();
    private List<QuestionAnswerModel> answeredList = new ArrayList<>();
    private String mediaUUID;
    private String sectionUUID;
    private List<QuestionAnswerModel> questionAnswerModelLsit;
    private int testAttemptCount=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_question_answer);
        viewModel = ViewModelProviders.of(this).get(QuestionAnswerViewModel.class);
        binding.setQuestionAnswerViewModel(viewModel);
        binding.setLifecycleOwner(this);
        toolbar = findViewById(R.id.white_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }
        questionsAnswersRadioLayout = binding.questionsAnswersRadioLayout;
        toolbar.inflateMenu(R.menu.teacher_menu);
        getIntentData();
        viewModel.getShowDialog().observe(this, aBoolean -> {
            if (aBoolean != null)
                showDialog();
        });

        viewModel.getSubmitClick().observe(this, aBoolean -> {
            if (aBoolean != null)
                if (validationForRadioButton()) {
                this.finish();
//                prepareDataToSubmit();
//                    submitAnswerToTable(finalModelLsit);
                } else {
                    Toast.makeText(this, getResources().getString(R.string.select_all_question), Toast.LENGTH_SHORT).show();
                }
        });
        viewModel.getQuestionAnswerModel().observe(this, questionAnswerModels -> {
            if (questionAnswerModels != null && !questionAnswerModels.isEmpty()) {
                questionAnswerModelLsit = questionAnswerModels;
                setLayoutForQuestionAnswer(questionAnswerModels);
            }
        });
    }

    private void prepareDataToSubmit() {
        List<QuestionAnswerModel> finalModelLsit = new ArrayList<>();
        String questionText;
        String choiceText;
        int choiceId;
        int questionId;
        int score;
        boolean isCorrect;
        RadioGroup radioGroup;
        QuestionAnswerModel questionAnswerModel;
        for (int i =0; i<questionsStringList.size();i++){
            radioGroup = radioGroupMap.get(questionsStringList.get(i));
            questionText = questionAnswerModelLsit.get(i).getQuestionModel().getText();
            questionId = questionAnswerModelLsit.get(i).getQuestionModel().getId();
            QuestionChoicesModel choicesModel = getChoiceModel(questionAnswerModelLsit.get(i).getChoicesModelList(), radioGroup.getCheckedRadioButtonId());
            choiceId = choicesModel.getId();
            choiceText = choicesModel.getText();
            score = choicesModel.getScore();
            isCorrect = choicesModel.getCorrect();

            questionAnswerModel= new QuestionAnswerModel(questionText,questionId,choiceText,choiceId,score,isCorrect);
            finalModelLsit.add(questionAnswerModel);
        }
        submitAnswerToTable(finalModelLsit);
    }

    private QuestionChoicesModel getChoiceModel(List<QuestionChoicesModel> choicesModelList, int checkedRadioButtonId) {
        QuestionChoicesModel questionChoicesModel=null;
        for (int i = 0;i<choicesModelList.size(); i++){
            if (checkedRadioButtonId==choicesModelList.get(i).getRadioButtonId()){
                questionChoicesModel = choicesModelList.get(i);
            }
        }
        return questionChoicesModel;
    }

    private void submitAnswerToTable(List<QuestionAnswerModel> finalModelLsit) {
        JSONArray array = new JSONArray();
        try {
            for (int i = 0; i < finalModelLsit.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("q_id", finalModelLsit.get(i).getQuetsionId());
                    jsonObject.put("res_id", finalModelLsit.get(i).getChoiceId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                array.put(jsonObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG, "onClick: " + array);
        testAttemptCount = viewModel.getTestAttemptCount(mediaUUID);
//        getTestFinalScore();
        viewModel.saveValueToDb(array, mediaUUID);
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
        videoTitle = getIntent().getStringExtra("videoTitle");
        viewModel.setMediaUUID(sectionUUID);
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
        QuestionAnswerActivity.this.finish();
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
    }
}
