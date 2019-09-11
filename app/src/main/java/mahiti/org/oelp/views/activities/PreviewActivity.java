package mahiti.org.oelp.views.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.List;

import mahiti.org.oelp.R;
import mahiti.org.oelp.database.DatabaseHandlerClass;
import mahiti.org.oelp.models.CatalogueDetailsModel;
import mahiti.org.oelp.models.QuestionAnswerModel;
import mahiti.org.oelp.models.SubmittedAnswerResponse;
import mahiti.org.oelp.services.RetrofitConstant;
import mahiti.org.oelp.utils.Logger;
import mahiti.org.oelp.videoplay.activity.VideoViewActivity;
import mahiti.org.oelp.views.adapters.PreviewAdapter;

public class PreviewActivity extends AppCompatActivity implements View.OnClickListener {
    DatabaseHandlerClass handlerClass;
    private TextView tvTitle;
    private Button btnOk;
    private TextView tvMarksObtain;
    private TextView tvTotalMarks;
    private LinearLayout llScoreView;
    private Button btnTakeTest;
    private RecyclerView recyclerView;
    private String mediaUUID;
    private List<SubmittedAnswerResponse> scoreAndAttempt;
    private String videoTitle;
    private String uriPath;
    private String userId;
    private String mediaTrackerApi;
    private String sectionUUID;
    private String dcfId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        findViewsForLayout();
        handlerClass = new DatabaseHandlerClass(this);
        getIntentData();
        scoreAndAttempt = handlerClass.getAnsweredQuestion(mediaUUID,0);
        showPreviewForAnswer(scoreAndAttempt);
    }

    private void getIntentData() {
        uriPath = getIntent().getStringExtra("uriPath");
        userId = getIntent().getStringExtra("userId");
        videoTitle = getIntent().getStringExtra("videoTitle");
        mediaUUID = getIntent().getStringExtra("mediaUUID");
        dcfId = getIntent().getStringExtra("dcfId");
        mediaTrackerApi = getIntent().getStringExtra("mediaTrackerApi");
        sectionUUID = getIntent().getStringExtra("sectionUUID");
        tvTitle.setText(videoTitle);
    }

    private void findViewsForLayout() {
        tvTitle = findViewById(R.id.tvTitle);

        btnOk = findViewById(R.id.btnOk);
        tvMarksObtain = findViewById(R.id.tvMarksObtain);
        tvTotalMarks = findViewById(R.id.tvTotalMarks);
        llScoreView = findViewById(R.id.llScoreView);
        btnTakeTest = findViewById(R.id.btnRetakeTest);
        recyclerView = findViewById(R.id.recyclerView);

        btnOk.setOnClickListener(this);
        btnTakeTest.setOnClickListener(this);
    }


    private void showPreviewForAnswer(List<SubmittedAnswerResponse> scoreAndAttempt) {

        if (scoreAndAttempt != null && !scoreAndAttempt.isEmpty()) {

            llScoreView.setVisibility(View.VISIBLE);
            tvMarksObtain.setText(String.valueOf(scoreAndAttempt.get(0).getScore()));
            tvTotalMarks.setText(String.valueOf(scoreAndAttempt.get(0).getTotal()));

            PreviewAdapter adapter = new PreviewAdapter();
            RecyclerView.LayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            recyclerView.setHasFixedSize(true);
            recyclerView.setFocusable(false);
            recyclerView.setLayoutManager(manager);
            recyclerView.setAdapter(adapter);

           if (!scoreAndAttempt.get(0).getPreviewString().isEmpty()){
               try {
                   adapter.setList(new JSONArray(scoreAndAttempt.get(0).getPreviewString()));
               }catch (Exception xe){
                   Logger.logE("Exception", xe.getMessage(), xe);
               }
           }

            setVisibilityForButton(scoreAndAttempt);

        } else {
            Toast.makeText(this, getString(R.string.SOMETHING_WRONG), Toast.LENGTH_SHORT).show();
        }
    }

    private void setVisibilityForButton(List<SubmittedAnswerResponse> scoreAndAttempt) {
        if (scoreAndAttempt.size() > 1) {
            btnTakeTest.setVisibility(View.GONE);
        } else if (scoreAndAttempt.get(0).getScore() == Float.parseFloat(scoreAndAttempt.get(0).getTotal())) {
            btnTakeTest.setVisibility(View.GONE);
        } else {
            btnTakeTest.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnRetakeTest:
                Intent i = new Intent(this, VideoViewActivity.class);
                i.putExtra("uriPath", uriPath);
                i.putExtra("userId", userId);
                i.putExtra("videoTitle", videoTitle);
                i.putExtra("mediaUUID", mediaUUID);
                i.putExtra("dcfId", dcfId);
                i.putExtra("mediaTrackerApi", RetrofitConstant.BASE_URL+RetrofitConstant.MEDIA_TRACKER_API);
                i.putExtra("sectionUUID", sectionUUID);
                i.putExtra("takeRetest", true);
                startActivity(i);
                this.finish();
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
                break;
            case R.id.btnOk:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        this.finish();
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);

    }
}
