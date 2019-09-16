package mahiti.org.oelp.views.fragments;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.List;

import mahiti.org.oelp.R;
import mahiti.org.oelp.database.DAOs.MediaContentDao;
import mahiti.org.oelp.models.SharedMediaModel;
import mahiti.org.oelp.utils.Constants;
import mahiti.org.oelp.views.adapters.MyContAdapter;

public class MyContFragment extends Fragment {


    private static final String TAG = ContributionsFragment.class.getSimpleName();
    private View rootView;
    private RecyclerView recyclerView;
    private MyContAdapter adapter;
    private List<SharedMediaModel> sharedMediaList;
    private String groupUUID;
    ProgressBar progressBar;
    LinearLayout llMain;
    TextView tvError;
    private String teacherUUID;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_my_cont, container, false);

        groupUUID = getActivity().getIntent().getStringExtra("groupUUID");
        teacherUUID = getActivity().getIntent().getStringExtra(Constants.TEACHER_UUID);
        initViews(rootView);

        setHasOptionsMenu(true);

        return rootView;
    }

    @SuppressLint("RestrictedApi")
    private void initViews(View view) {

        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        llMain = view.findViewById(R.id.llMain);
        tvError = view.findViewById(R.id.tvError);



        GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        adapter = new MyContAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setFocusable(false);

        setViewAndDataForMember(1);

    }


    private void fetchDataFromDb(boolean forGroup, int i) {
        MediaContentDao mediaContentDao = new MediaContentDao(getActivity());
        sharedMediaList = mediaContentDao.getSharedMedia(teacherUUID, false);
        progressBar.setVisibility(View.GONE);
        if (sharedMediaList != null && !sharedMediaList.isEmpty()) {
            adapter.setList(sharedMediaList);
            tvError.setVisibility(View.GONE);
            llMain.setVisibility(View.VISIBLE);
        } else {
            tvError.setVisibility(View.VISIBLE);
            llMain.setVisibility(View.GONE);
        }
    }





    private void setViewAndDataForMember(int i) {
        progressBar.setVisibility(View.VISIBLE);
        fetchDataFromDb(false, i);

    }

}
