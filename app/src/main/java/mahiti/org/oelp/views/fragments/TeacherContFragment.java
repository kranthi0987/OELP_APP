package mahiti.org.oelp.views.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mahiti.org.oelp.R;
import mahiti.org.oelp.views.adapters.TeacherContributionAdapter;

public class TeacherContFragment extends Fragment {

        private RecyclerView recyclerView;
        private TeacherContributionAdapter teacherContributionAdapter;

        private View rootView;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            rootView = inflater.inflate(R.layout.fragment_teacher_cont, container, false);

            initViews();

            return rootView;
        }



        private void initViews() {

            recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(new TeacherContributionAdapter(getActivity(), 15));

        }
}
