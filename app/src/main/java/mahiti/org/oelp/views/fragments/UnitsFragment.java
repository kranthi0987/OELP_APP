package mahiti.org.oelp.views.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import mahiti.org.oelp.R;
import mahiti.org.oelp.database.DAOs.CatalogDao;
import mahiti.org.oelp.database.DatabaseHandlerClass;
import mahiti.org.oelp.models.CatalogueDetailsModel;
import mahiti.org.oelp.utils.Constants;
import mahiti.org.oelp.utils.MySharedPref;
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
    private int mediaLevel;
    private List<CatalogueDetailsModel> catalogueDetailsModels = new ArrayList<>();
    //    FragmentUnitsBinding binding;
    private CatalogDao handlerClass;
    private List<CatalogueDetailsModel> list1;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        homeViewModel = ViewModelProviders.of(getActivity()).get(HomeViewModel.class);
        handlerClass = new CatalogDao(getActivity());
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
        calculateLockAndWatchStatus(parentId);

        catalogueDetailsModels = handlerClass.getCatalogData(parentId);
        if (!catalogueDetailsModels.isEmpty()) {
            mediaLevel = catalogueDetailsModels.get(0).getMediaLevelType();
            adapter.setList(catalogueDetailsModels, getActivity());
            progressBar.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
        return view;
    }

    private void calculateLockAndWatchStatus(String parentId) {
        handlerClass.getContentIsOpen(parentId);
    }

    private void getArgumentsData() {
        Bundle bundle = this.getArguments();
        title = bundle.getString("Title");
        parentId = bundle.getString("ParentId");
    }

    @Override
    public void onResume() {
        super.onResume();
        MySharedPref sharedPref = new MySharedPref(getActivity());
        if (sharedPref.readBoolean(Constants.VALUE_CHANGED, false) || mediaLevel<3) {
            calculateLockAndWatchStatus(parentId);
            catalogueDetailsModels = handlerClass.getCatalogData(parentId);
            adapter.setList(catalogueDetailsModels, getActivity());
            sharedPref.writeBoolean(Constants.VALUE_CHANGED, false);

        }

    }


}
