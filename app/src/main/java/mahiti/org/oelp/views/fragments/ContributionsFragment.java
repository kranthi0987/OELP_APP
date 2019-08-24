package mahiti.org.oelp.views.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import mahiti.org.oelp.R;
import mahiti.org.oelp.utils.GridSpacingItemDecoration;
import mahiti.org.oelp.utils.Utils;
import mahiti.org.oelp.views.adapters.ContributionAdapter;
import mahiti.org.oelp.views.adapters.NewTeacherAdapter;

public class ContributionsFragment extends Fragment {

    private View rootView;
    private RecyclerView recyclerView;
    private Button buttonShareGlobally;
    private ContributionAdapter contributionAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_contributions, container, false);
        initViews();
        setHasOptionsMenu(true);

        return rootView;
    }

    private void initViews() {

        buttonShareGlobally = (Button)rootView.findViewById(R.id.buttonShareGlobally);
        buttonShareGlobally.setVisibility(View.VISIBLE);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        /*LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new MyContAdapter(getActivity(), 7));*/

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);
        int spacing = (int) Utils.DpToPixel(getActivity(), 11); // 40px

        GridSpacingItemDecoration itemDecoration = new GridSpacingItemDecoration(2, spacing, true);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setNestedScrollingEnabled(false);

        recyclerView.setAdapter(new NewTeacherAdapter(getActivity(),4));


    }

}
