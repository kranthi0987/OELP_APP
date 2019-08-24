package oelp.mahiti.org.newoepl.views.adapters;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import oelp.mahiti.org.newoepl.R;

public class ContributionAdapter extends RecyclerView.Adapter<ContributionAdapter.ViewHolder> {

    private Activity context;
    private int size;

    public ContributionAdapter(Activity context, int size) {
        this.context = context;
        this.size = size;

    }


    @Override
    public ContributionAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_my_cont, viewGroup, false);
        ContributionAdapter.ViewHolder viewHolder = new ContributionAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ContributionAdapter.ViewHolder viewHolder, final int position) {

      /*  viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, TeacherInfoTabActivity.class);
                context.startActivity(intent);
            }
        });*/


    }

    @Override
    public int getItemCount() {
        return size;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        private TextView textViewDate;
        private ImageView imageViewRound;
        private ImageView imageViewPlayButton;
       // private CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewDate = (TextView)itemView.findViewById(R.id.textViewDate);
            imageViewRound = (ImageView)itemView.findViewById(R.id.imageViewRound);
            imageViewPlayButton = (ImageView)itemView.findViewById(R.id.imageViewPlayButton);
           // cardView = (CardView)itemView.findViewById(R.id.cardView);

        }
    }
}
