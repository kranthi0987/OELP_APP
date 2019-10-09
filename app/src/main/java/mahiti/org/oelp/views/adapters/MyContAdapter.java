package mahiti.org.oelp.views.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.util.List;

import mahiti.org.oelp.R;
import mahiti.org.oelp.interfaces.SharedMediaClickListener;
import mahiti.org.oelp.models.SharedMediaModel;
import mahiti.org.oelp.utils.AppUtils;
import mahiti.org.oelp.utils.Constants;
import mahiti.org.oelp.utils.Logger;

public class MyContAdapter extends RecyclerView.Adapter<MyContAdapter.ViewHolder> {

    private Activity context;
    private List<SharedMediaModel> sharedMediaList;
    private SharedMediaClickListener clickListener;

    public MyContAdapter(Activity context) {
        this.context = context;
        clickListener = (SharedMediaClickListener) context;

    }


    @Override
    public MyContAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_my_cont, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyContAdapter.ViewHolder viewHolder, final int position) {
        SharedMediaModel model = sharedMediaList.get(position);

        viewHolder.tvMediaName.setText(model.getMediaTitle());
        viewHolder.tvUploadedBy.setText(model.getUserName());
        if (model.getMediaType().equals("3")) {
            viewHolder.ivPlayButton.setVisibility(View.VISIBLE);
            viewHolder.roundedImageView.setBackgroundColor(context.getResources().getColor(R.color.blackOpaque));
        } else {
            showUIForImage(model.getMediaFile(), viewHolder);
        }

        viewHolder.llRecycler.setOnClickListener(view -> {
            clickListener.onSharedMediaClick(model, false, position);


        });

        if (model.getSharedGlobally()==1)
            viewHolder.ivTickMark.setVisibility(View.VISIBLE);
        else
            viewHolder.ivTickMark.setVisibility(View.GONE);

        viewHolder.llRecycler.setOnLongClickListener(view -> {
            clickListener.onSharedMediaClick(model, true, position);
            return false;
        });

    }

    public void showUIForImage(String path, ViewHolder viewHolder) {
        if (path == null)
            return;
        String fileName = AppUtils.getFileName(path);
        File file = null;
        try {
            file = new File(AppUtils.completePathInSDCard(Constants.IMAGE), fileName);
        } catch (Exception ex) {
            Logger.logE("Exce", ex.getMessage(), ex);
        }
        if (file.exists()) {
           /* Picasso.get()
                    .load("file://" + file)
                    .fit()
                    .into(viewHolder.roundedImageView);*/
            RequestOptions myOptions = new RequestOptions()
                    .centerCrop();
            Glide.with(context)
                    .load("file://" + file)
                    .apply(myOptions)
                    .into(viewHolder.roundedImageView);
        }
    }

    @Override
    public int getItemCount() {
        return sharedMediaList.isEmpty() ? 0 : sharedMediaList.size();
    }

    public void setList(List<SharedMediaModel> sharedMediaList) {
        this.sharedMediaList = sharedMediaList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        private TextView tvMediaName;
        private TextView tvUploadedBy;
        private LinearLayout llRecycler;
        private ImageView ivPlayButton;
        private ImageView ivTickMark;
        private ImageView roundedImageView;

        public ViewHolder(View itemView) {
            super(itemView);

            tvMediaName = itemView.findViewById(R.id.tvMediaName);
            tvUploadedBy = itemView.findViewById(R.id.tvUploadedBy);
            llRecycler = itemView.findViewById(R.id.llRecycler);
            ivPlayButton = itemView.findViewById(R.id.ivPlayButton);
            roundedImageView = itemView.findViewById(R.id.roundedImageView);
            ivTickMark = itemView.findViewById(R.id.ivTickMark);

        }
    }
}
