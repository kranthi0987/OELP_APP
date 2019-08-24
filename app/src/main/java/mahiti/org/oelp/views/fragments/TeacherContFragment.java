package mahiti.org.oelp.views.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import mahiti.org.oelp.R;
import mahiti.org.oelp.database.DAOs.TeacherDao;
import mahiti.org.oelp.models.TeacherModel;
import mahiti.org.oelp.views.adapters.TeacherContributionAdapter;

public class TeacherContFragment extends Fragment {

        private RecyclerView recyclerView;
        private TeacherContributionAdapter teacherContributionAdapter;
        private View rootView;
        String groupUUID = "60469fa5-d2a9-433c-a3de-fbfd364b1378";

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
            List<TeacherModel> teachersList;
            TeacherDao teacher = new TeacherDao(getActivity());
            teachersList = teacher.getTeachers(groupUUID, 1);
            recyclerView.setAdapter(new TeacherContributionAdapter(getActivity(), teachersList));

        }
}
