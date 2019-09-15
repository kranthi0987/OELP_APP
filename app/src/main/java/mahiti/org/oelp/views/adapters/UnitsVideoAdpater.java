package mahiti.org.oelp.views.adapters;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
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
import mahiti.org.oelp.utils.AppUtils;
import mahiti.org.oelp.utils.Constants;
import mahiti.org.oelp.utils.Logger;
import mahiti.org.oelp.utils.MySharedPref;
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
//        setImageFoCard(modelList, layout);
        if (model.getContType() != null) {
            if (model.getContType().equalsIgnoreCase("video"))
                binding.ivPlayButton.setVisibility(View.VISIBLE);
            else
                binding.ivPlayButton.setVisibility(View.GONE);

        }

        setValues(modelList.get(i), layout);
        setTickMark(layout, model, i);

        CatalogueDetailsModel finalModel1 = model;
        layout.binding.llRecycler.setOnClickListener(v -> {
           /* if (layout.binding.ivLock.getVisibility()==View.VISIBLE) {
                Toast.makeText(mContext, "First You need to unlock preceding module", Toast.LENGTH_SHORT).show();
            } else {*/
            listener.onItemClick(finalModel1, i);
            /* }*/

        });

    }

    CatalogueDetailsModel model1;

    private void setTickMark(Layout layout, CatalogueDetailsModel model, int position) {
        if (position != 0)
            model1 = modelList.get(position - 1);
        else
            model1 = model;

        if (model.getWatchStatus() == 1) {
            layout.binding.ivTickMark.setVisibility(View.VISIBLE);
        } else {
            layout.binding.ivTickMark.setVisibility(View.GONE);
        }
        boolean loginType = new MySharedPref(mContext).readInt(Constants.USER_TYPE, Constants.USER_TEACHER) == Constants.USER_TEACHER;
        boolean lockFlag = false;

        if (lockFlag && loginType) {
            setLockMark(layout, model, position);
        }

    }

    private void setLockMark(Layout layout, CatalogueDetailsModel model, int position) {
        if (model.getMediaLevelType() == 2) {

            if (model.getOrder() == 1 || model.getOrder() == 2) {
                layout.binding.ivLock.setVisibility(View.GONE);
            } else if (model1.getWatchStatus() == 1) {
                layout.binding.ivLock.setVisibility(View.GONE);
            } else {
                layout.binding.ivLock.setVisibility(View.VISIBLE);
            }

        } else {
            if (model.getOrder() == 1) {
                layout.binding.ivLock.setVisibility(View.GONE);
            } else if (model1.getWatchStatus() == 1) {
                layout.binding.ivLock.setVisibility(View.GONE);
            } else {
                layout.binding.ivLock.setVisibility(View.VISIBLE);
            }
        }
    }


    private void setValues(CatalogueDetailsModel catalogueDetailsModel, Layout layout) {
        layout.binding.tvCatalog.setText(catalogueDetailsModel.getName());
        setBackgroundForCardView(layout, catalogueDetailsModel);
    }

    private void setBackgroundForCardView(Layout layout, CatalogueDetailsModel catalogueDetailsModel) {

//        if (catalogueDetailsModel.getCompleted() == 1) {
//            layout.binding.ivTickMark.setVisibility(View.VISIBLE);
////            layout.binding.rlEnableDisable.setBackgroundColor(layout.getContext().getResources().getColor(R.color.blackOpaque));
//        } else if (catalogueDetailsModel.getCompleted()==0) {
//            layout.binding.ivTickMark.setVisibility(View.GONE);
//        }

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
