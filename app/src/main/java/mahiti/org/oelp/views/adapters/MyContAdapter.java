package mahiti.org.oelp.views.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import mahiti.org.oelp.R;
import mahiti.org.oelp.interfaces.SharedMediaClickListener;
import mahiti.org.oelp.models.SharedMediaModel;

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
        if (model.getMediaType().equals("3"))
            viewHolder.ivPlayButton.setVisibility(View.VISIBLE);

        viewHolder.llRecycler.setOnClickListener(view -> {
            if ((model.getMediaType()).equals("3"))
                clickListener.onSharedMediaClick(model, false);
        });

        viewHolder.llRecycler.setOnLongClickListener((View.OnLongClickListener) view -> {
            clickListener.onSharedMediaClick(model, true);
            return false;
        });

    }

    @Override
    public int getItemCount() {
        return sharedMediaList.isEmpty()? 0:sharedMediaList.size();
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

        public ViewHolder(View itemView) {
            super(itemView);

            tvMediaName = itemView.findViewById(R.id.tvMediaName);
            tvUploadedBy = itemView.findViewById(R.id.tvUploadedBy);
            llRecycler = itemView.findViewById(R.id.llRecycler);
            ivPlayButton = itemView.findViewById(R.id.ivPlayButton);

        }
    }
}
