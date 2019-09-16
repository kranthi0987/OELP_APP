package mahiti.org.oelp.views.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mahiti.org.oelp.R;
import mahiti.org.oelp.models.UserDetailsModel;


/**
 * Created by RAJ ARYAN on 24/08/19.
 */
public class AddTeacherToGroupAdapter extends RecyclerView.Adapter<AddTeacherToGroupAdapter.LayoutView> {
    private List<UserDetailsModel> modelList;
    private static List<UserDetailsModel> userDetailList = new ArrayList<>();

    @NonNull
    @Override
    public LayoutView onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_teacher_view, viewGroup, false);
        return new LayoutView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddTeacherToGroupAdapter.LayoutView layoutView, int i) {
        UserDetailsModel userDetails = modelList.get(i);
        CheckBox checkBox = layoutView.checkBox;
        layoutView.tvName.setText(userDetails.getName());
        layoutView.tvMobileNo.setText(userDetails.getMobile_number());
        checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
            if (!userDetails.isCheckBoxChecked()){
               userDetails.setCheckBoxChecked(true);
               userDetailList.add(userDetails);
            }else {
                userDetails.setCheckBoxChecked(false);
                userDetailList.remove(userDetails);
            }
        });

    }

    public  static List<UserDetailsModel> getUserDetailsList(){
        return userDetailList;
    }

    public void setList(List<UserDetailsModel> list) {
        this.modelList = list;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return modelList == null ? 0 : modelList.size();
    }

    public class LayoutView extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView tvName;
        TextView tvMobileNo;
        public LayoutView(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkBox);
            tvName = itemView.findViewById(R.id.tvName);
            tvMobileNo = itemView.findViewById(R.id.tvMobileNo);
        }
    }
}
