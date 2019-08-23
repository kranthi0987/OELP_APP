package oelp.mahiti.org.newoepl.views.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import oelp.mahiti.org.newoepl.R;
import oelp.mahiti.org.newoepl.views.activities.TeacherInfoTabActivity;

public class TeacherContributionAdapter extends RecyclerView.Adapter<TeacherContributionAdapter.ViewHolder> {

    private Activity context;
    private int size;

    public TeacherContributionAdapter(Activity context, int size) {
        this.context = context;
        this.size = size;

    }


    @Override
    public TeacherContributionAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_teacher_cont, viewGroup, false);
        TeacherContributionAdapter.ViewHolder viewHolder = new TeacherContributionAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TeacherContributionAdapter.ViewHolder viewHolder, final int position) {

        viewHolder.linearLayoutTeacherAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, TeacherInfoTabActivity.class);
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return size;
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
