package mahiti.org.oelp.views.adapters;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import mahiti.org.oelp.R;
import mahiti.org.oelp.databinding.AdapterUnitsVideoBinding;
import mahiti.org.oelp.interfaces.ItemClickListerner;
import mahiti.org.oelp.models.CatalogueDetailsModel;
import mahiti.org.oelp.services.RetrofitConstant;
import mahiti.org.oelp.utils.AppUtils;
import mahiti.org.oelp.utils.Constants;
import mahiti.org.oelp.utils.Logger;
import mahiti.org.oelp.viewmodel.HomeViewModel;

/**
 * Created by RAJ ARYAN on 09/08/19.
 */
public class UnitsVideoAdpater extends RecyclerView.Adapter<UnitsVideoAdpater.Layout> {


    private List<CatalogueDetailsModel> modelList;
    private ItemClickListerner listener;
    private AdapterUnitsVideoBinding binding;
    private Context mContext;
    private File imageFile;

    public UnitsVideoAdpater() {
    }


    public void setList(List<CatalogueDetailsModel> list, Context context) {
        this.modelList = list;
        listener = (ItemClickListerner) context;
        mContext = context;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Layout onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        binding = DataBindingUtil
                .inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.adapter_units_video, viewGroup, false);
        return new Layout(binding, viewGroup.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull Layout layout, int i) {
        HomeViewModel vm = ViewModelProviders.of((FragmentActivity) layout.getContext()).get(HomeViewModel.class);
        layout.setViewModel(vm);
        CatalogueDetailsModel model = modelList.get(i);
//        setImageFoCard(model, layout);
        binding.ivPlayButton.setVisibility(View.GONE);
        if (model.getContType() != null) {
            if (model.getContType().equalsIgnoreCase("video"))
                binding.ivPlayButton.setVisibility(View.VISIBLE);
            else
                binding.ivPlayButton.setVisibility(View.GONE);

        }

        if (i == 0) {
            modelList.get(i).setCompleted(RetrofitConstant.STATUS_TRUE);
        } else if (i == 1) {
            modelList.get(i).setCompleted(RetrofitConstant.STATUS_FALSE);
        } else {
            modelList.get(i).setCompleted(null);
        }
        setValues(modelList.get(i), layout);

        CatalogueDetailsModel finalModel1 = model;
        layout.binding.llRecycler.setOnClickListener(v -> listener.onItemClick(finalModel1));

    }


    private void setValues(CatalogueDetailsModel catalogueDetailsModel, Layout layout) {
        layout.binding.tvCatalog.setText(catalogueDetailsModel.getName());
        setBackgroundForCardView(layout, catalogueDetailsModel);
    }

    private void setBackgroundForCardView(Layout layout, CatalogueDetailsModel catalogueDetailsModel) {

        if (catalogueDetailsModel.getCompleted() == null) {
            layout.binding.ivTickMark.setVisibility(View.GONE);
            layout.binding.ivTickMark.setVisibility(View.GONE);
//            layout.binding.rlEnableDisable.setBackgroundColor(layout.getContext().getResources().getColor(R.color.blackOpaque));
        } else if (catalogueDetailsModel.getCompleted().equals(RetrofitConstant.STATUS_TRUE)) {
//            layout.binding.ivTickMark.setVisibility(View.VISIBLE);
//            layout.binding.ivTickMark.setVisibility(View.VISIBLE);
//            layout.binding.rlEnableDisable.setBackgroundColor(layout.getContext().getResources().getColor(R.color.greenOpaque));
        } else {
//            layout.binding.ivTickMark.setVisibility(View.GONE);
//            layout.binding.ivTickMark.setVisibility(View.GONE);
//            layout.binding.rlEnableDisable.setBackgroundColor(layout.getContext().getResources().getColor(R.color.yellowOpaque));
        }

//        String iconCompletePath = AppUtils.completePathInSDCard(Constants.IMAGE).getAbsolutePath() + "/" + AppUtils.getFileName(catalogueDetailsModel.getIcon());
        String iconCompletePath = AppUtils.completeInternalStoragePath(mContext, Constants.IMAGE).getAbsolutePath() + "/" + AppUtils.getFileName(catalogueDetailsModel.getIcon());
        Logger.logD("ADAPTER", "HomeScreen Image : " + iconCompletePath);

        try {
            File imageFile = new File(iconCompletePath);
            Log.i("ADAPTER", "Image path in device : " + imageFile.getPath());
            if (imageFile.exists()) {
                Picasso.get()
                        .load("file://" + imageFile.getPath())
                        .fit()
                        .into(layout.binding.roundedImageView);
            } else {
                binding.roundedImageView.setBackgroundResource(R.drawable.image3);
            }
        } catch (Exception ex) {
            Logger.logE("", ex.getMessage(), ex);
        }
    }

    @Override
    public int getItemCount() {
        return modelList == null ? 0 : modelList.size();
    }

    public class Layout extends RecyclerView.ViewHolder {
        private Context mContext;
        private HomeViewModel homeViewModel;
        final AdapterUnitsVideoBinding binding;

        public Layout(AdapterUnitsVideoBinding binding, Context context) {
            super(binding.getRoot());
            this.binding = binding;
            mContext = context;
        }

        public Context getContext() {
            return mContext;
        }

        public void setViewModel(HomeViewModel viewModel) {
            this.homeViewModel = viewModel;
            binding.setAdapterViewModel(viewModel);
        }
    }
}
