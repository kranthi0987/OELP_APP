package mahiti.org.oelp.views.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import mahiti.org.oelp.R;
import mahiti.org.oelp.models.TeacherModel;
import mahiti.org.oelp.utils.Constants;
import mahiti.org.oelp.utils.MySharedPref;
import mahiti.org.oelp.views.activities.TeacherInfoTabActivity;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.ViewHolder> {

    private Activity context;
    List<TeacherModel> teachersList;
    private int userType;


    public MembersAdapter(Activity context) {
        this.context = context;
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
        viewHolder.tvMediaCount.setText(""+teachersList.get(position).getMediaCount());
        MySharedPref sharedPref = new MySharedPref(context);
        userType = sharedPref.readInt(Constants.USER_TYPE, Constants.USER_TEACHER);

        viewHolder.cvMembers.setOnClickListener(v -> {
            if (userType == Constants.USER_TRAINER) {
                Intent intent = new Intent(context, TeacherInfoTabActivity.class);
                intent.putExtra(Constants.TEACHER_UUID, teachersList.get(position).getUserUuid());
                intent.putExtra("teacherName", teachersList.get(position).getName());
                context.startActivity(intent);
                context.overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);
            }
        });
    }


    @Override
    public int getItemCount() {
        return teachersList==null ?0: teachersList.size();
    }

    public void setList(List<TeacherModel> teacherList) {
        this.teachersList = teacherList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvMemberName;
        private TextView tvSchoolName;
        private TextView tvBlock;
        private TextView tvMediaCount;
        private CardView cvMembers;

        public ViewHolder(View itemView) {
            super(itemView);

            tvMemberName = (TextView) itemView.findViewById(R.id.tvMemberName);
            tvSchoolName = (TextView) itemView.findViewById(R.id.tvSchoolName);
            tvBlock = (TextView) itemView.findViewById(R.id.tvBlock);
            tvMediaCount = (TextView) itemView.findViewById(R.id.tvMediaCount);
            cvMembers = itemView.findViewById(R.id.cvMembers);

        }
    }
}
