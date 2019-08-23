package oelp.mahiti.org.newoepl.views.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import oelp.mahiti.org.newoepl.R;
import oelp.mahiti.org.newoepl.databinding.FragmentGroupsBinding;
import oelp.mahiti.org.newoepl.models.GroupModel;
import oelp.mahiti.org.newoepl.viewmodel.HomeViewModel;
import oelp.mahiti.org.newoepl.views.adapters.GroupAdapter;

/**
 * Created by RAJ ARYAN on 07/08/19.
 */
public class GroupsFragment extends Fragment {

    HomeViewModel homeViewModel;
    FragmentGroupsBinding binding;
    RecyclerView recyclerView;
    List<GroupModel> groupModelList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeViewModel = ViewModelProviders.of(getActivity()).get(HomeViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_groups, container, false);
        View view = binding.getRoot();
        recyclerView = binding.recyclerView;
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        GroupAdapter adapter = new GroupAdapter();
//        recyclerView.setAdapter(adapter);
        recyclerView.setFocusable(false);

//        homeViewModel.getGroupInserted().observe(getActivity(), aLong -> {
//            if (aLong!=null){
                groupModelList = homeViewModel.getGroupList().getValue();
//            }
//        });

//        if (!groupModelList.isEmpty()) {
//            adapter.setList(groupModelList, getActivity());
//        }

        return view;
    }
}
