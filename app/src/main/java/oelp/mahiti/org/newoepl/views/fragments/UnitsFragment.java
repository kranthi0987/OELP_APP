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
import android.widget.RelativeLayout;

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
    RelativeLayout rlBanner;


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
        rlBanner = binding.rlBanner;
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        adapter = new UnitsVideoAdpater();
        recyclerView.setAdapter(adapter);
        recyclerView.setFocusable(false);
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//            }
//
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                if (dy>0){
//                    rlBanner.setVisibility(View.VISIBLE);
//                    rlBanner.setAlpha(0.0f);
//
//                // Start the animation
//                    rlBanner.animate()
//                            .translationY(rlBanner.getHeight())
//                            .alpha(1.0f)
//                            .setListener(null);
//                }else {
//                    rlBanner.animate()
//                            .translationY(0)
//                            .alpha(0.0f)
//                            .setListener(new AnimatorListenerAdapter() {
//                                @Override
//                                public void onAnimationEnd(Animator animation) {
//                                    super.onAnimationEnd(animation);
//                                    rlBanner.setVisibility(View.GONE);
//                                }
//                            });
//                }
//            }
//        });
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

    @Override
    public void onResume() {
        super.onResume();
//            isOpen = catalogDbHandler.getContentIsOpen(uuid);
//            if (isOpen)
//                catalogDbHandler.insertViewStatusToDatabase(uuid, "1");
//            if (activityName.equalsIgnoreCase(Constants.HorizontalScrollAndView)) {
//                if (levelcode != 0) {
//                    upDateSubmitButtonFunctionality(isOpen, levelcode);
//                }
//            }
    }
}
