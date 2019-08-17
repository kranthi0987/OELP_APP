package oelp.mahiti.org.newoepl.views.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.List;

import oelp.mahiti.org.newoepl.R;
import oelp.mahiti.org.newoepl.databinding.ActivityQuestionAnswerBinding;
import oelp.mahiti.org.newoepl.models.QuestionAnswerModel;
import oelp.mahiti.org.newoepl.models.QuestionChoicesModel;
import oelp.mahiti.org.newoepl.utils.AppUtils;
import oelp.mahiti.org.newoepl.viewmodel.QuestionAnswerViewModel;


public class QuestionAnswerActivity extends AppCompatActivity {

    private QuestionAnswerViewModel viewModel;
    ActivityQuestionAnswerBinding binding;
    private String sectionUUID;
    private String videoTitle;
    private Toolbar toolbar;
    private AlertDialog dialog;
    private LinearLayout questionsAnswersRadioLayout;


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
                onBackPressed();
        });
        viewModel.getQuestionAnswerModel().observe(this, questionAnswerModels -> {
            if (questionAnswerModels!=null && !questionAnswerModels.isEmpty()){
                setLayoutForQuestionAnswer(questionAnswerModels);
            }
        });
    }

    private void setLayoutForQuestionAnswer(List<QuestionAnswerModel> questionAnswerModels) {
        TextView questionText;
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (int i = 0; i < questionAnswerModels.size(); i++) {

            View v = inflater.inflate(R.layout.subquestions, null);
            RadioGroup radioGroup = v.findViewById(R.id.radioGroup);
            radioGroup.setTag(questionAnswerModels.get(i).getQuestionModel().getId());
            questionText = v.findViewById(R.id.questionText);
            questionText.setText(i + 1 + ". " + questionAnswerModels.get(i).getQuestionModel().getText());
//            questionsStringList.add(questionlist.get(i).getId().toString());

//            questionAndAnswerRequestModelList.get(i).setQId(questionlist.get(i).getId().toString());

            List<QuestionChoicesModel> choice = questionAnswerModels.get(i).getChoicesModelList();
            for (int j = 0; j < choice.size(); j++) {
                RadioButton questionChoiceRadioButton = new RadioButton(this);
                questionChoiceRadioButton.setId(j);
                questionChoiceRadioButton.setTag(choice.get(j).getId());
                if (Build.VERSION.SDK_INT >= 21) {
                    ColorStateList colorStateList = new ColorStateList(
                            new int[][]{

                                    new int[]{-android.R.attr.state_enabled}, //disabled
                                    new int[]{android.R.attr.state_enabled} //enabled
                            },
                            new int[]{

                                    getResources().getColor(R.color.grey) //disabled
                                    , getResources().getColor(R.color.red) //enabled

                            }
                    );
                    questionChoiceRadioButton.setButtonTintList(colorStateList);//set the color tint list
                    questionChoiceRadioButton.invalidate(); //could not be necessary
                }
                questionChoiceRadioButton.setText(choice.get(j).getText());
                questionChoiceRadioButton.setTextColor(getResources().getColor(R.color.black));
                radioGroup.addView(questionChoiceRadioButton);
            }
//            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
//                    checkedId = radioGroup.getCheckedRadioButtonId();
//                    questionChoiceRadioButton = (RadioButton) radioGroup.findViewById(checkedId);
////                    questionAndAnswerRequestModelList.get(checkedId).setResId(questionChoiceRadioButton.toString());
//                    choices.add(choice.get(checkedId).getId().toString());
//                    Toast.makeText(getContext(), "radio checked: " + questionChoiceRadioButton.getText().toString(), Toast.LENGTH_SHORT).show();
//                }
//            });
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
        sectionUUID = getIntent().getStringExtra("sectionUUID");
        videoTitle = getIntent().getStringExtra("videoTitle");
        viewModel.setSectionUUID(sectionUUID);
        binding.tvTitle.setText(videoTitle);
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
//                ShowAboutUsActivity();
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
    }
}
