package mahiti.org.oelp.views.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import mahiti.org.oelp.R;
import mahiti.org.oelp.models.TeacherModel;
import mahiti.org.oelp.utils.Constants;
import mahiti.org.oelp.views.activities.TeacherInfoTabActivity;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.ViewHolder> {

    private Activity context;
    List<TeacherModel> teachersList;

    public MembersAdapter(Activity context, List<TeacherModel> teachers) {
        this.context = context;
        this.teachersList = teachers;
    }


    @Override
    public MembersAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_members, viewGroup, false);
        MembersAdapter.ViewHolder viewHolder = new MembersAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MembersAdapter.ViewHolder viewHolder, final int position) {
        viewHolder.tvBlock.setText(teachersList.get(position).getBlockName());
        viewHolder.tvMemberName.setText(teachersList.get(position).getName());
        viewHolder.tvSchoolName.setText(teachersList.get(position).getSchool());
        viewHolder.tvMediaCount.setText(""+position);

        viewHolder.cvMembers.setOnClickListener(v -> {
            Intent intent = new Intent(context, TeacherInfoTabActivity.class);
            intent.putExtra(Constants.TEACHER_UUID,teachersList.get(position).getUserUuid());
            intent.putExtra("teacherName",teachersList.get(position).getName());
            context.startActivity(intent);
            context.overridePendingTransition(R.anim.anim_slide_in_left,
                    R.anim.anim_slide_out_left);
        });


    }

    @Override
    public int getItemCount() {
        return teachersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvMemberName;
        private TextView tvSchoolName;
        private TextView tvBlock;
        private TextView tvMediaCount;
        private CardView cvMembers;

        public ViewHolder(View itemView) {
            super(itemView);

            tvMemberName = (TextView)itemView.findViewById(R.id.tvMemberName);
            tvSchoolName = (TextView)itemView.findViewById(R.id.tvSchoolName);
            tvBlock = (TextView)itemView.findViewById(R.id.tvBlock);
            tvMediaCount = (TextView)itemView.findViewById(R.id.tvMediaCount);
            cvMembers = itemView.findViewById(R.id.cvMembers);

        }
    }
}
