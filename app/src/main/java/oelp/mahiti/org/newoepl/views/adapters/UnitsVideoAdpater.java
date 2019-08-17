package oelp.mahiti.org.newoepl.views.adapters;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import oelp.mahiti.org.newoepl.R;
import oelp.mahiti.org.newoepl.databinding.AdapterUnitsVideoBinding;
import oelp.mahiti.org.newoepl.interfaces.ItemClickListerner;
import oelp.mahiti.org.newoepl.models.CatalogueDetailsModel;
import oelp.mahiti.org.newoepl.services.RetrofitConstant;
import oelp.mahiti.org.newoepl.utils.AppUtils;
import oelp.mahiti.org.newoepl.utils.Constants;
import oelp.mahiti.org.newoepl.utils.Logger;
import oelp.mahiti.org.newoepl.viewmodel.HomeViewModel;

/**
 * Created by RAJ ARYAN on 09/08/19.
 */
public class UnitsVideoAdpater extends RecyclerView.Adapter<UnitsVideoAdpater.Layout> {


    private List<CatalogueDetailsModel> modelList;
    private ItemClickListerner listener;


//    Integer[] imageArray = {R.drawable.image1, R.drawable.image2, R.drawable.image3, R.drawable.image11, R.drawable.image5,
//            R.drawable.image6, R.drawable.image7, R.drawable.image8, R.drawable.image9, R.drawable.image10, R.drawable.image4};

    private AdapterUnitsVideoBinding binding;
    private Context mContext;
    private File imageFile;


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
        CatalogueDetailsModel model = new CatalogueDetailsModel();
        model = modelList.get(i);
        try {
            imageFile = new File(AppUtils.completePathInSDCard(Constants.IMAGE), AppUtils.getFileName(model.getIcon()));
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

        if (model.getContType() != null) {
            if (model.getContType().equalsIgnoreCase("video"))
                vm.playButton.setValue(true);
            else
                vm.playButton.setValue(false);
        }

        if (i == 0) {
            modelList.get(i).setCompleted(RetrofitConstant.STATUS_TRUE);
//            vm.moduleCompleted.setValue(true);
        } else if (i == 1) {
            modelList.get(i).setCompleted(RetrofitConstant.STATUS_TRUE);
//            vm.moduleCompleted.setValue(false);
        } else {
            modelList.get(i).setCompleted(null);
//            vm.moduleCompleted.setValue(null);
        }
        setValues(modelList.get(i), layout);

        CatalogueDetailsModel finalModel = model;
        layout.binding.llRecycler.setOnClickListener(v -> listener.onItemClick(finalModel));

    }

    private void setValues(CatalogueDetailsModel catalogueDetailsModel, Layout layout) {
        layout.binding.tvCatalog.setText(catalogueDetailsModel.getName());
        setBackgroundForCardView(layout, catalogueDetailsModel);
    }

    private void setBackgroundForCardView(Layout layout, CatalogueDetailsModel catalogueDetailsModel) {

//        if (catalogueDetailsModel.getActive().equals(RetrofitConstant.STATUS_TRUE)) {
        if (catalogueDetailsModel.getCompleted() == null) {
            layout.binding.rlEnableDisable.setBackgroundColor(layout.getContext().getResources().getColor(R.color.blackOpaque));
        } else if (catalogueDetailsModel.getCompleted().equals(RetrofitConstant.STATUS_TRUE)) {
            layout.binding.rlEnableDisable.setBackgroundColor(layout.getContext().getResources().getColor(R.color.greenOpaque));
        } else {
            layout.binding.rlEnableDisable.setBackgroundColor(layout.getContext().getResources().getColor(R.color.yellowOpaque));
        }
//        }else {
//            layout.binding.rlEnableDisable.setBackgroundColor(layout.getContext().getResources().getColor(R.color.blackOpaque));
//
//        }

        String iconCompletePath = AppUtils.completePathInSDCard(Constants.IMAGE).getAbsolutePath() + "/" + AppUtils.getFileName(catalogueDetailsModel.getIcon());
        Logger.logD("ADAPTER", "HomeScreen Image : " + iconCompletePath);
        File imageFile = new File(iconCompletePath);
        Log.i("ADAPTER", "Image path in device : " + imageFile.getPath());
        BitmapFactory.Options options;
        Bitmap bitmap = null;
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeFile(imageFile.getPath()); //
        }

        if (bitmap != null) {
            if (bitmap != null) {
//            if (!isRound) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    imageView.setBackground(new BitmapDrawable(bitmap));
//                } else {
//                    RoundCornersDrawable round = new RoundCornersDrawable(bitmap, context.getResources().getDimension(R.dimen.card_radius), 0); //or your custom radius
//                    imageView.setBackground(round);
//                }
//            } else {
                Picasso.get()
                        .load("file://" + imageFile.getPath())
                        .fit()
                        .into(layout.binding.roundedImageView);
//            }
            }
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
