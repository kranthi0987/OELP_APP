package mahiti.org.oelp.views.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import mahiti.org.oelp.R;
import mahiti.org.oelp.database.DatabaseHandlerClass;
import mahiti.org.oelp.models.CatalogueDetailsModel;
import mahiti.org.oelp.views.activities.HomeActivity;
import mahiti.org.oelp.views.adapters.UnitsVideoAdpater;

/**
 * Created by RAJ ARYAN on 07/08/19.
 */
public class UnitsFragment extends Fragment {

    /*HomeViewModel homeViewModel;*/
    RecyclerView recyclerView;
    UnitsVideoAdpater adapter;
    private HomeActivity mContext;
    private String title;
    private String parentId;
    private List<CatalogueDetailsModel> catalogueDetailsModels = new ArrayList<>();
//    FragmentUnitsBinding binding;
    private DatabaseHandlerClass handlerClass;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        homeViewModel = ViewModelProviders.of(getActivity()).get(HomeViewModel.class);
        handlerClass = new DatabaseHandlerClass(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
/*//        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_units, container, false);*/
        View view = inflater.inflate(R.layout.fragment_units, container, false);
       /* View view = binding.getRoot();
        binding.setHomeViewModel(homeViewModel);
        recyclerView = binding.recyclerView;*/
        recyclerView = view.findViewById(R.id.recyclerView);
        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        adapter = new UnitsVideoAdpater();
        recyclerView.setAdapter(adapter);
        recyclerView.setFocusable(false);
        getArgumentsData();

/*        homeViewModel.getCatalogDataInserted().observe(this, aLong -> {
            if (aLong != null) {
                catalogueDetailsModels = homeViewModel.getCatalogData(parentId);
                if (catalogueDetailsModels != null && !catalogueDetailsModels.isEmpty()) {
                    adapter.setList(catalogueDetailsModels, getActivity());
                }
            }
        });
        catalogueDetailsModels = homeViewModel.getCatalogData(parentId);
        if (!catalogueDetailsModels.isEmpty()) {
            adapter.setList(catalogueDetailsModels, getActivity());
        }*/
        catalogueDetailsModels = handlerClass.getCatalogData(parentId);
        if (!catalogueDetailsModels.isEmpty()){
           adapter.setList(catalogueDetailsModels, getActivity());
           progressBar.setVisibility(View.GONE);
        }
//            Toast.makeText(mContext, getActivity().getResources().getString(R.string.data_not_found), Toast.LENGTH_SHORT).show();
        return view;
    }

    private void getArgumentsData() {
        Bundle bundle = this.getArguments();
        title = bundle.getString("Title");
        parentId = bundle.getString("ParentId");
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
