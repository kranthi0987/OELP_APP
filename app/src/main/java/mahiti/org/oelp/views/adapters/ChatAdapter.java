package mahiti.org.oelp.views.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

import mahiti.org.oelp.R;
import mahiti.org.oelp.models.ChatModelClass;

/**
 * Created by RAJ ARYAN on 25/08/19.
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatView> {
    private List<ChatModelClass> modelList;
    private Context context;

    public ChatAdapter(List<ChatModelClass> modelList, Context context) {
        this.modelList = modelList;
        this.context = context;
    }

    @NonNull
    @Override
    public ChatView onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_chat, viewGroup, false);
        return new ChatView(view);
    }

    public void setList(List<ChatModelClass> modelList, Context context) {
        this.modelList = modelList;
        this.context = context;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ChatView chatView, int i) {
        ChatModelClass model = modelList.get(i);
        setViewToLayout(model, chatView);
    }

    private void setViewToLayout(ChatModelClass model, ChatView chatView) {
        if (model.getChatType() == 0) {
            chatView.llLeftView.setVisibility(View.VISIBLE);
            chatView.llRightView.setVisibility(View.GONE);
            chatView.llRightViewVideo.setVisibility(View.GONE);

            chatView.tvDate.setText(model.getDate());
            chatView.tvMessage.setText(model.getMessage());
            chatView.tvNameInitials.setText(model.getNameInitials());
            chatView.textViewBack.setBackgroundColor(Color.parseColor(model.getColorcode()));

        } else if (model.getChatType() == 1) {
            chatView.llLeftView.setVisibility(View.GONE);
            chatView.llRightView.setVisibility(View.VISIBLE);
            chatView.llRightViewVideo.setVisibility(View.GONE);

            chatView.tvDateR.setText(model.getDate());
            chatView.tvMessageR.setText(model.getMessage());
            chatView.tvNameInitialsR.setText(model.getNameInitials());
            chatView.textViewBackR.setBackgroundColor(Color.parseColor(model.getColorcode()));

        } else {
            chatView.llLeftView.setVisibility(View.GONE);
            chatView.llRightView.setVisibility(View.GONE);
            chatView.llRightViewVideo.setVisibility(View.VISIBLE);

            chatView.tvDateRVideo.setText(model.getDate());
            chatView.tvMessageRVideo.setText(model.getMessage());
            chatView.tvNameInitialsRVideo.setText(model.getNameInitials());
            chatView.textViewBackRVideo.setBackgroundColor(Color.parseColor(model.getColorcode()));
        }
    }


    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class ChatView extends RecyclerView.ViewHolder {

        LinearLayout llLeftView;
        LinearLayout llRightView;
        LinearLayout llRightViewVideo;

        TextView tvNameInitials;
        TextView tvMessage;
        TextView tvDate;

        TextView tvNameInitialsR;
        TextView tvMessageR;
        TextView tvDateR;

        TextView tvNameInitialsRVideo;
        TextView tvMessageRVideo;
        TextView tvDateRVideo;

        RoundedImageView textViewBack;
        RoundedImageView textViewBackR;
        RoundedImageView textViewBackRVideo;


        public ChatView(@NonNull View itemView) {
            super(itemView);

            llLeftView = itemView.findViewById(R.id.llLeftView);
            llRightView = itemView.findViewById(R.id.llRightView);
            llRightViewVideo = itemView.findViewById(R.id.llRightViewVideo);

            tvNameInitials = itemView.findViewById(R.id.tvNameInitials);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvDate = itemView.findViewById(R.id.tvDate);

            tvNameInitialsR = itemView.findViewById(R.id.tvNameInitialsR);
            tvMessageR = itemView.findViewById(R.id.tvMessageR);
            tvDateR = itemView.findViewById(R.id.tvDateR);

            tvNameInitialsRVideo = itemView.findViewById(R.id.tvNameInitialsRVideo);
            tvMessageRVideo = itemView.findViewById(R.id.tvMessageRVideo);
            tvDateRVideo = itemView.findViewById(R.id.tvDateRVideo);

            textViewBack = itemView.findViewById(R.id.textViewBack);
            textViewBackR = itemView.findViewById(R.id.textViewBackR);
            textViewBackRVideo = itemView.findViewById(R.id.textViewBackRVideo);

        }
    }
}
