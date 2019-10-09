package mahiti.org.oelp.views.adapters;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import mahiti.org.oelp.R;

public class NewTeacherAdapter extends RecyclerView.Adapter<NewTeacherAdapter.ViewHolder> {

    private Activity context;
    private int size;

    public NewTeacherAdapter(Activity context, int size) {
        this.context = context;
        this.size = size;

    }


    @Override
    public NewTeacherAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_my_cont, viewGroup, false);
        NewTeacherAdapter.ViewHolder viewHolder = new NewTeacherAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NewTeacherAdapter.ViewHolder viewHolder, final int position) {

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

         /*   textViewDate = (TextView)itemView.findViewById(R.id.textViewDate);
            imageViewRound = (ImageView)itemView.findViewById(R.id.imageViewRound);
            imageViewPlayButton = (ImageView)itemView.findViewById(R.id.imageViewPlayButton);*/
           // cardView = (CardView)itemView.findViewById(R.id.cardView);

        }
    }
}
