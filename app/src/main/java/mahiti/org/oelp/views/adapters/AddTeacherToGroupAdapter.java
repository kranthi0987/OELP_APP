package mahiti.org.oelp.views.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by RAJ ARYAN on 24/08/19.
 */
public class AddTeacherToGroupAdapter extends RecyclerView.Adapter<AddTeacherToGroupAdapter.LayoutView> {
    @NonNull
    @Override
    public LayoutView onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//        binding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.adapter_teacher_view, viewGroup, false);
//        return new GroupAdapter.LayoutView(binding, viewGroup.getContext());
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull AddTeacherToGroupAdapter.LayoutView layoutView, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class LayoutView extends RecyclerView.ViewHolder {
        public LayoutView(@NonNull View itemView) {
            super(itemView);
        }
    }
}
