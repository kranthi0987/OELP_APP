package mahiti.org.oelp.views.fragments;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.List;

import mahiti.org.oelp.R;
import mahiti.org.oelp.database.DAOs.MediaContentDao;
import mahiti.org.oelp.databinding.FragmentContributionsBinding;
import mahiti.org.oelp.models.SharedMediaModel;
import mahiti.org.oelp.utils.Constants;
import mahiti.org.oelp.utils.MySharedPref;
import mahiti.org.oelp.viewmodel.ContributionsViewModel;
import mahiti.org.oelp.views.adapters.MyContAdapter;

public class MyContFragment extends Fragment {


    private static final String TAG = ContributionsFragment.class.getSimpleName();
    private View rootView;
    private RecyclerView recyclerView;
    private ContributionsViewModel viewModel;
    private FragmentContributionsBinding binding;
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
        viewModel = ViewModelProviders.of(getActivity()).get(ContributionsViewModel.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_contributions, container, false);
        binding.setContributionsViewModel(viewModel);
        binding.setLifecycleOwner(this);
        groupUUID = getActivity().getIntent().getStringExtra("groupUUID");
        teacherUUID = getActivity().getIntent().getStringExtra(Constants.TEACHER_UUID);
        initViews(binding);

        rootView = binding.getRoot();
        setHasOptionsMenu(true);

        return rootView;
    }

    @SuppressLint("RestrictedApi")
    private void initViews(FragmentContributionsBinding binding) {

        recyclerView = binding.recyclerView;

        progressBar = binding.progressBar;
        llMain = binding.llMain;
        tvError = binding.tvError;


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
        sharedMediaList = mediaContentDao.getSharedMedia(groupUUID, false, teacherUUID);
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
