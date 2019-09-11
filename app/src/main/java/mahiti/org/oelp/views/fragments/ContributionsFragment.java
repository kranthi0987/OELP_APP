package mahiti.org.oelp.views.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import mahiti.org.oelp.R;
import mahiti.org.oelp.databinding.FragmentContributionsBinding;
import mahiti.org.oelp.utils.GridSpacingItemDecoration;
import mahiti.org.oelp.utils.Utils;
import mahiti.org.oelp.viewmodel.ContributionsViewModel;
import mahiti.org.oelp.views.adapters.ContributionAdapter;
import mahiti.org.oelp.views.adapters.NewTeacherAdapter;
import mahiti.org.oelp.views.adapters.UnitsVideoAdpater;

public class ContributionsFragment extends Fragment {

    private View rootView;
    private RecyclerView recyclerView;
    private ContributionAdapter contributionAdapter;
    private ContributionsViewModel viewModel;
    private FragmentContributionsBinding binding;
    private UnitsVideoAdpater adapter;

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
        initViews(binding);
        rootView = binding.getRoot();
        setHasOptionsMenu(true);

        return rootView;
    }

    private void initViews(FragmentContributionsBinding binding) {

        recyclerView = binding.recyclerView;
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        adapter = new UnitsVideoAdpater();
        recyclerView.setAdapter(adapter);
        recyclerView.setFocusable(false);

    }

}
