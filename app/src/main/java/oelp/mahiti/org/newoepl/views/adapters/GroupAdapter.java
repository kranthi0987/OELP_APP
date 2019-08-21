package oelp.mahiti.org.newoepl.views.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import oelp.mahiti.org.newoepl.R;
import oelp.mahiti.org.newoepl.databinding.AdapterGroupViewBinding;
import oelp.mahiti.org.newoepl.models.CatalogueDetailsModel;
import oelp.mahiti.org.newoepl.viewmodel.HomeViewModel;

/**
 * Created by RAJ ARYAN on 21/08/19.
 */
public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.Layout> {
    AdapterGroupViewBinding binding;
    @NonNull
    @Override
    public Layout onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.adapter_group_view, viewGroup, false);
        return new Layout(binding, viewGroup.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull Layout layout, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class Layout extends RecyclerView.ViewHolder {
        private Context mContext;
        private HomeViewModel homeViewModel;

        public Layout(AdapterGroupViewBinding binding, Context context) {
            super(binding.getRoot());
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

    public void setList(List<CatalogueDetailsModel> list, Context context) {
//        this.modelList = list;
//        listener = (ItemClickListerner) context;
//        mContext = context;
//        notifyDataSetChanged();
    }

    private void setValues(CatalogueDetailsModel catalogueDetailsModel, UnitsVideoAdpater.Layout layout) {
//        layout.binding.tvCatalog.setText(catalogueDetailsModel.getName());
    }

}
