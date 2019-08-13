package oelp.mahiti.org.newoepl.views.fragments;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import oelp.mahiti.org.newoepl.R;
import oelp.mahiti.org.newoepl.databinding.FragmentUnitsBinding;
import oelp.mahiti.org.newoepl.models.CatalogueDetailsModel;
import oelp.mahiti.org.newoepl.viewmodel.HomeViewModel;
import oelp.mahiti.org.newoepl.views.activities.HomeActivity;
import oelp.mahiti.org.newoepl.views.adapters.UnitsVideoAdpater;

/**
 * Created by RAJ ARYAN on 07/08/19.
 */
public class UnitsFragment extends Fragment {

    HomeViewModel homeViewModel;
    RecyclerView recyclerView;
    UnitsVideoAdpater adapter;
    private HomeActivity mContext;
    private String title;
    private String parentId;
    private MutableLiveData<List<CatalogueDetailsModel>> catalogueDetailsModels;
    FragmentUnitsBinding binding;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeViewModel = ViewModelProviders.of(getActivity()).get(HomeViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         binding = DataBindingUtil.inflate(inflater, R.layout.fragment_units, container, false);
        View view = binding.getRoot();
        binding.setHomeViewModel(homeViewModel);
        recyclerView = binding.recyclerView;

        GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        adapter = new UnitsVideoAdpater();
        recyclerView.setAdapter(adapter);
        getArgumentsData();


        catalogueDetailsModels = homeViewModel.getCatalogData(parentId);
        if (!catalogueDetailsModels.getValue().isEmpty() && catalogueDetailsModels.getValue() != null) {
            adapter.setList(catalogueDetailsModels.getValue(), getActivity());
        }
        return view;
    }

    private void getArgumentsData() {
        Bundle bundle = this.getArguments();
        title = bundle.getString("Title");
        parentId = bundle.getString("ParentId");
        binding.tvTitle.setText(title);
    }

}
