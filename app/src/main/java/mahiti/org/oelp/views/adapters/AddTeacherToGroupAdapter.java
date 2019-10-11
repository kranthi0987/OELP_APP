package mahiti.org.oelp.views.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import mahiti.org.oelp.R;
import mahiti.org.oelp.models.UserDetailsModel;
import mahiti.org.oelp.utils.Constants;


/**
 * Created by RAJ ARYAN on 24/08/19.
 */
public class AddTeacherToGroupAdapter extends RecyclerView.Adapter<AddTeacherToGroupAdapter.LayoutView> {
    private List<UserDetailsModel> modelList=new ArrayList<>();
    private Integer type;

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
        if (userDetails.isCheckBoxChecked()){
            layoutView.checkBox.setChecked(true);
        }else {
            layoutView.checkBox.setChecked(false);
        }
        layoutView.tvName.setText(userDetails.getName());
        layoutView.tvMobileNo.setText(userDetails.getMobile_number());

        checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
            if (!userDetails.isCheckBoxChecked()){
               userDetails.setCheckBoxChecked(true);
            }else {
                userDetails.setCheckBoxChecked(false);
            }
        });
    }

    public List<UserDetailsModel> getUserDetailsList(){
        List<UserDetailsModel> modelData = new ArrayList<>();

        for (UserDetailsModel model :modelList){
            if (model.isCheckBoxChecked()){
                modelData.add(model);
            }
        }
        return modelData;
    }


    public void setList(List<UserDetailsModel> list, Integer type) {
        this.modelList.clear();
        modelList.addAll(list);
        this.notifyDataSetChanged();
        this.type = type;
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
