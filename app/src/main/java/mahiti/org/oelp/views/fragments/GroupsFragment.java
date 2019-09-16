package mahiti.org.oelp.views.fragments;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import mahiti.org.oelp.R;
import mahiti.org.oelp.databinding.FragmentGroupsBinding;
import mahiti.org.oelp.models.GroupModel;
import mahiti.org.oelp.utils.Constants;
import mahiti.org.oelp.utils.MySharedPref;
import mahiti.org.oelp.viewmodel.HomeViewModel;
import mahiti.org.oelp.views.activities.HomeActivity;
import mahiti.org.oelp.views.adapters.GroupAdapter;

/**
 * Created by RAJ ARYAN on 07/08/19.
 */
public class GroupsFragment extends Fragment {

    HomeViewModel homeViewModel;
    FragmentGroupsBinding binding;
    RecyclerView recyclerView;
    List<GroupModel> groupModelList = new ArrayList<>();
    private int userType;
    FloatingActionButton fab;
    private MySharedPref sharedPref;
    private GroupAdapter adapter;
    private ProgressBar progressBar;
    public MutableLiveData<Boolean> moveToCreateGroup = new MutableLiveData<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeViewModel = ViewModelProviders.of(getActivity()).get(HomeViewModel.class);
        sharedPref = new MySharedPref(getActivity());
        homeViewModel.insertLong.setValue(null);
        userType = sharedPref.readInt(Constants.USER_TYPE, Constants.USER_TEACHER);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_groups, container, false);
        View view = binding.getRoot();
        recyclerView = binding.recyclerView;
        progressBar = binding.progressBar;
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setFocusable(false);
        adapter = new GroupAdapter();
        setValueToAdapter();
        recyclerView.setAdapter(adapter);
        fab = binding.fab;

        fab.setOnClickListener(view1 -> {
            ((HomeActivity)getActivity()).onCallNextActivity();
        });


        if (userType == Constants.USER_TRAINER) {
            fab.setVisibility(View.VISIBLE);
            fab.setEnabled(true);
        } else {
            fab.setVisibility(View.GONE);
            fab.setEnabled(false);
        }

       /* homeViewModel.apiCountMutable.observe(getActivity(), integer -> {
            if (integer != null) {
                if (integer == 0) {
                    groupModelList = homeViewModel.getGroupList();
                    setValueToAdapter(groupModelList);
                }
            }
        });*/

       /* homeViewModel.insertLong.observe(getActivity(), new Observer<Long>() {
            @Override
            public void onChanged(@Nullable Long aLong) {
                if (aLong != null) {
                    homeViewModel.apiCountMutable.setValue(0);

                }
            }
        });*/

        return view;
    }


    public void setValueToAdapter() {
        groupModelList = homeViewModel.getGroupList();
        if (groupModelList != null && !groupModelList.isEmpty()) {
            adapter.setList(groupModelList, getActivity());
            progressBar.setVisibility(View.GONE);

        }else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getActivity(), "Data not found", Toast.LENGTH_SHORT).show();
        }
    }
}
