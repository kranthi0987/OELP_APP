package mahiti.org.oelp.views.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import mahiti.org.oelp.R;
import mahiti.org.oelp.models.TeacherModel;
import mahiti.org.oelp.views.activities.TeacherInfoTabActivity;

public class TeacherContributionAdapter extends RecyclerView.Adapter<TeacherContributionAdapter.ViewHolder> {

    private Activity context;
    List<TeacherModel> teachersList;

    public TeacherContributionAdapter(Activity context, List<TeacherModel> teachers) {
        this.context = context;
        this.teachersList = teachers;
    }


    @Override
    public TeacherContributionAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_teacher_cont, viewGroup, false);
        TeacherContributionAdapter.ViewHolder viewHolder = new TeacherContributionAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TeacherContributionAdapter.ViewHolder viewHolder, final int position) {
        viewHolder.textViewBlock.setText(teachersList.get(position).getBlockName());
        viewHolder.textViewTeacherName.setText(teachersList.get(position).getName());
        viewHolder.textViewSchool.setText(teachersList.get(position).getSchool());
        viewHolder.textViewCount.setText(""+position);

        viewHolder.linearLayoutTeacherAdapter.setOnClickListener(v -> {
            Intent intent = new Intent(context, TeacherInfoTabActivity.class);
            context.startActivity(intent);
        });


    }

    @Override
    public int getItemCount() {
        return teachersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewTeacherName;
        private TextView textViewSchool;
        private TextView textViewBlock;
        private TextView textViewCount;
        private ImageView imageViewTeacher;
        private LinearLayout linearLayoutTeacherAdapter;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewTeacherName = (TextView)itemView.findViewById(R.id.textViewTeacherName);
            textViewSchool = (TextView)itemView.findViewById(R.id.textViewSchool);
            textViewBlock = (TextView)itemView.findViewById(R.id.textViewBlock);
            textViewCount = (TextView)itemView.findViewById(R.id.textViewCount);
            imageViewTeacher = (ImageView)itemView.findViewById(R.id.imageViewTeacher);
            linearLayoutTeacherAdapter = (LinearLayout)itemView.findViewById(R.id.linearLayoutTeacherAdapter);

        }
    }
}
