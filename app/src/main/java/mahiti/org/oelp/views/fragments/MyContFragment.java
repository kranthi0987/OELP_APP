package mahiti.org.oelp.views.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import mahiti.org.oelp.R;
import mahiti.org.oelp.database.DAOs.MediaContentDao;
import mahiti.org.oelp.interfaces.ListRefresh;
import mahiti.org.oelp.models.SharedMediaModel;
import mahiti.org.oelp.utils.Constants;
import mahiti.org.oelp.views.activities.ChatAndContributionActivity;
import mahiti.org.oelp.views.activities.TeacherInfoTabActivity;
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

        ((TeacherInfoTabActivity)getActivity()).setFragmentRefreshListener(new ListRefresh() {
            @Override
            public void onRefresh(int position, boolean isDelete) {

                if (isDelete) {
                    sharedMediaList.remove(position);
                    adapter.notifyDataSetChanged();
                }else {
                    fetchDataFromDb();
                }
            }
        });

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


    }

    private void fetchDataFromDb() {
        MediaContentDao mediaContentDao = new MediaContentDao(getActivity());
        sharedMediaList = mediaContentDao.fetchSharedMedia(teacherUUID,groupUUID ,false, 0);
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

}
