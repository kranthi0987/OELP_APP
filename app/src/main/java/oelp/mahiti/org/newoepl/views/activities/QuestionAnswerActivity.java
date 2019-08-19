package oelp.mahiti.org.newoepl.views.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import oelp.mahiti.org.newoepl.R;
import oelp.mahiti.org.newoepl.databinding.ActivityQuestionAnswerBinding;
import oelp.mahiti.org.newoepl.models.QuestionAnswerModel;
import oelp.mahiti.org.newoepl.models.QuestionChoicesModel;
import oelp.mahiti.org.newoepl.utils.AppUtils;
import oelp.mahiti.org.newoepl.viewmodel.QuestionAnswerViewModel;


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
//                    submitAnswerToTable();
                    onBackPressed();
                } else {
                    Toast.makeText(this, getResources().getString(R.string.select_all_question),Toast.LENGTH_SHORT).show();
                }
        });
        viewModel.getQuestionAnswerModel().observe(this, questionAnswerModels -> {
            if (questionAnswerModels!=null && !questionAnswerModels.isEmpty()){
                setLayoutForQuestionAnswer(questionAnswerModels);
            }
        });
    }

    private void submitAnswerToTable() {
        JSONArray array = new JSONArray();
        try {
            for (int i = 0; i < questionsStringList.size(); i++) {
                RadioGroup rg = radioGroupMap.get(questionsStringList.get(i));
                assert rg != null;
                RadioButton radioButton = rg.findViewById(rg.getCheckedRadioButtonId());
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("q_id", questionsStringList.get(i));
                    jsonObject.put("res_id", radioButton.getTag().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                array.put(jsonObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG, "onClick: " + array);
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
        TextView questionText;
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int ij=1;
        for (int i = 0; i < questionAnswerModels.size(); i++) {

            View v = inflater.inflate(R.layout.subquestions, null);
            RadioGroup radioGroup = v.findViewById(R.id.radioGroup);
//            radioGroup.setTag(questionAnswerModels.get(i).getQuestionModel().getId());
            radioGroup.setId(questionAnswerModels.get(i).getQuestionModel().getId());
            questionText = v.findViewById(R.id.questionText);
            questionText.setText(i + 1 + ". " + questionAnswerModels.get(i).getQuestionModel().getText());
            questionsStringList.add(questionAnswerModels.get(i).getQuestionModel().getId().toString());

            List<QuestionChoicesModel> choice = questionAnswerModels.get(i).getChoicesModelList();
            for (int j = 0; j < choice.size(); j++) {
                AppCompatRadioButton questionChoiceRadioButton = new AppCompatRadioButton(this);
                RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 5, 0, 0);
                questionChoiceRadioButton.setLayoutParams(params);
                questionChoiceRadioButton.setId(ij);
                questionChoiceRadioButton.setTag(choice.get(j).getId());
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

            final QuestionAnswerModel[] answerModel = new QuestionAnswerModel[1];
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
//                    answerModel[0] = new QuestionAnswerModel();
//                    answerModel[0].setQuestionModel(questionAnswerModels.get(radioGroup.getId()).getQuestionModel());
//                    answerModel[0].setChoicesModelList(questionAnswerModels.get(radioGroup.getId()).getChoicesModelList());
//                    answeredList.add(answerModel[0]);
                }
            });
            questionsAnswersRadioLayout.addView(v);
        }
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
        builder.setMessage("There is no question ");
        builder.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                onBackPressed();
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    private void getIntentData() {
        mediaUUID = getIntent().getStringExtra("mediaUUID");
        videoTitle = getIntent().getStringExtra("videoTitle");
        viewModel.setMediaUUID(mediaUUID);
        String completeTilte = videoTitle.concat(" - Questions");
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
