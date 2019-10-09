package mahiti.org.oelp.views.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mahiti.org.oelp.R;
import mahiti.org.oelp.models.QuestionAnswerModel;
import mahiti.org.oelp.models.SubmittedAnswerResponse;
import mahiti.org.oelp.utils.Logger;

/**
 * Created by RAJ ARYAN on 27/08/19.
 */
public class PreviewAdapter extends RecyclerView.Adapter<PreviewAdapter.PreviewLayout> {
    JSONArray modelList;
    private String question;
    private String choice;
    private String exp;
    private boolean isCorrect;

    @NonNull
    @Override
    public PreviewLayout onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.preview_adapter,viewGroup, false);
        return new PreviewLayout(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PreviewLayout previewLayout, int i) {
        JSONObject model = null;
        try {
            model = modelList.getJSONObject(i);
            question = model.getString("q_text");
            choice = model.getString("q_text");
            exp = model.getString("q_text");
            isCorrect = model.getBoolean("q_text");


            if (model.getBoolean("correct")) {
                previewLayout.tvAnswerText.setTextColor(Color.GREEN);
                previewLayout.tvExplainationText.setVisibility(View.VISIBLE);
                previewLayout.tvExplainationText.setText(exp);
            } else {
                previewLayout.tvAnswerText.setTextColor(Color.RED);
                previewLayout.tvExplainationText.setVisibility(View.GONE);
            }
            previewLayout.tvQuestionText.setText(question);
            previewLayout.tvAnswerText.setText(choice);

        } catch (JSONException e) {
            e.printStackTrace();
        }





    }
     public void setList(JSONArray modelList){
        this.modelList = modelList;
        notifyDataSetChanged();
     }

    @Override
    public int getItemCount() {
        return modelList ==null|| modelList.length()==0 ?0: modelList.length();
    }

    public class PreviewLayout extends RecyclerView.ViewHolder {
        TextView tvQuestionText;
        TextView tvAnswerText;
        TextView tvExplainationText;
        public PreviewLayout(@NonNull View itemView) {
            super(itemView);
            tvAnswerText = itemView.findViewById(R.id.tvAnswerText);
            tvQuestionText = itemView.findViewById(R.id.tvQuestionText);
            tvExplainationText = itemView.findViewById(R.id.tvExplainationText);
        }
    }
}
