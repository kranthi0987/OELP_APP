package mahiti.org.oelp.views.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import mahiti.org.oelp.R;
import mahiti.org.oelp.models.MobileVerificationResponseModel;
import mahiti.org.oelp.views.adapters.ChatAdapter;

public class ChatFragment extends Fragment  {
    private View rootView;
    RecyclerView recyclerView;
    private String chatModel = "{\n" +
            "  \"status\":2,\n" +
            "  \"message\":\"Successful\",\n" +
            "  \"chat\":[{\n" +
            "  \"chatType\":1,\n" +
            "  \"message\":\"Hi All Hi are you\",\n" +
            "  \"date\":\"Sun 3:02 PM\",\n" +
            "  \"nameInitials\":\"V\",\n" +
            "  \"colorcode\":\"#9B3C32\"\n" +
            "  \n" +
            "},\n" +
            "{\n" +
            "  \"chatType\":2,\n" +
            "  \"message\":\"I am Good Sir\",\n" +
            "  \"date\":\"Sun 3:04 PM\",\n" +
            "  \"nameInitials\":\"A\",\n" +
            "  \"colorcode\":\"#2A9C49\"\n" +
            "  \n" +
            "},\n" +
            "{\n" +
            "  \"chatType\":2,\n" +
            "  \"message\":\"I am Good Sir\",\n" +
            "  \"date\":\"Sun 3:05 PM\",\n" +
            "  \"nameInitials\":\"K\",\n" +
            "  \"colorcode\":\"#253678\"\n" +
            "  \n" +
            "},\n" +
            "{\n" +
            "  \"chatType\":1,\n" +
            "  \"message\":\"How is work going\",\n" +
            "  \"date\":\"Sun 3:06 PM\",\n" +
            "  \"nameInitials\":\"V\",\n" +
            "  \"colorcode\":\"#9B3C32\"\n" +
            "  \n" +
            "},\n" +
            "{\n" +
            "  \"chatType\":2,\n" +
            "  \"message\":\"Sir we are working .. hope so we will deliver\",\n" +
            "  \"date\":\"Sun 3:07 PM\",\n" +
            "  \"nameInitials\":\"A\",\n" +
            "  \"colorcode\":\"#2A9C49\"\n" +
            "  \n" +
            "},\n" +
            "{\n" +
            "  \"chatType\":2,\n" +
            "  \"message\":\"Yeah Sir Working\",\n" +
            "  \"date\":\"Sun 3:07 PM\",\n" +
            "  \"nameInitials\":\"K\",\n" +
            "  \"colorcode\":\"#253678\"\n" +
            "  \n" +
            "},\n" +
            "{\n" +
            "  \"chatType\":1,\n" +
            "  \"message\":\"Don;t worry .. I am also working\",\n" +
            "  \"date\":\"Sun 3:10 PM\",\n" +
            "  \"nameInitials\":\"V\",\n" +
            "  \"colorcode\":\"#9B3C32\"\n" +
            "  \n" +
            "},\n" +
            "{\n" +
            "  \"chatType\":2,\n" +
            "  \"message\":\"Good Sir ..\",\n" +
            "  \"date\":\"Sun 3:11 PM\",\n" +
            "  \"nameInitials\":\"A\",\n" +
            "  \"colorcode\":\"#2A9C49\"\n" +
            "  \n" +
            "},\n" +
            "{\n" +
            "  \"chatType\":2,\n" +
            "  \"message\":\"Great..Sir\",\n" +
            "  \"date\":\"Sun 3:12 PM\",\n" +
            "  \"nameInitials\":\"K\",\n" +
            "  \"colorcode\":\"#253678\"\n" +
            "  \n" +
            "},\n" +
            "{\n" +
            "  \"chatType\":3,\n" +
            "  \"message\":\"This is good\",\n" +
            "  \"date\":\"Sun 3:12 PM\",\n" +
            "  \"nameInitials\":\"K\",\n" +
            "  \"colorcode\":\"#253678\"\n" +
            "  \n" +
            "}\n" +
            "]\n" +
            "}";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = rootView.findViewById(R.id.rvMessage);
        Gson gson = new Gson();
        MobileVerificationResponseModel model = gson.fromJson(chatModel, MobileVerificationResponseModel.class);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setFocusable(false);
        ChatAdapter adapter = new ChatAdapter(model.getChat(), getActivity());
        recyclerView.setAdapter(adapter);
        return rootView;
    }



}
