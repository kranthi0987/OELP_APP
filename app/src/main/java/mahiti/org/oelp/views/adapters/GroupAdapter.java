package mahiti.org.oelp.views.adapters;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import mahiti.org.oelp.R;
import mahiti.org.oelp.databinding.AdapterGroupViewBinding;
import mahiti.org.oelp.interfaces.ItemClickListerner;
import mahiti.org.oelp.models.GroupModel;
import mahiti.org.oelp.viewmodel.HomeViewModel;

/**
 * Created by RAJ ARYAN on 21/08/19.
 */
public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.LayoutView> {
    AdapterGroupViewBinding binding;
    private List<GroupModel> modelList;
    private Context mContext;
    private ItemClickListerner listener;

    public GroupAdapter() {
    }

    @NonNull
    @Override
    public LayoutView onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.adapter_group_view, viewGroup, false);
        return new LayoutView(binding, viewGroup.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull LayoutView layout, int i) {
        HomeViewModel viewModel = ViewModelProviders.of((FragmentActivity) layout.getContext()).get(HomeViewModel.class);
        layout.setViewModel(viewModel);
        GroupModel groupModel = modelList.get(i);
        setValues(groupModel, layout, i);
        layout.binding.llRecyclerView.setOnClickListener(v -> listener.onItemClick(groupModel));
    }


    @Override
    public int getItemCount() {
        return modelList == null ? 0 : modelList.size();
    }

    public class LayoutView extends RecyclerView.ViewHolder {
        private Context mContext;
        private HomeViewModel homeViewModel;
        private AdapterGroupViewBinding binding;

        public LayoutView(AdapterGroupViewBinding binding, Context context) {
            super(binding.getRoot());
            mContext = context;
            this.binding = binding;
            mContext = context;
        }

        public Context getContext() {
            return mContext;
        }

        public void setViewModel(HomeViewModel viewModel) {
            this.homeViewModel = viewModel;
            binding.setHomeViewModel(viewModel);
        }
    }

    public void setList(List<GroupModel> list, Context context) {
        this.modelList = list;
        listener = (ItemClickListerner) context;
        mContext = context;
        notifyDataSetChanged();
    }

    private void setValues(GroupModel catalogueDetailsModel, LayoutView layout, int position) {
        layout.binding.tvGroupName.setText(catalogueDetailsModel.getGroupName());
        layout.binding.tvMemberCount.setText(""+catalogueDetailsModel.getMembersCount());
//        layout.binding.tvMemberCount.setText("3");
//        layout.binding.tvMessageCount.setText(String.valueOf(4));
    }

}
