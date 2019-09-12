package mahiti.org.oelp.views.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import mahiti.org.oelp.R;
import mahiti.org.oelp.database.DAOs.TeacherDao;
import mahiti.org.oelp.models.TeacherModel;
import mahiti.org.oelp.views.adapters.TeacherContributionAdapter;

public class TeacherContFragment extends Fragment {

        private RecyclerView recyclerView;
        private TextView tvSpinnerItem;
        private RelativeLayout rlSpinner;
        private TeacherContributionAdapter teacherContributionAdapter;
        private View rootView;
        String groupUUID = "";
        Spinner spnGroup;
        private int spinnerClickCount = 0;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            groupUUID=getActivity().getIntent().getStringExtra("groupUUID");
            rootView = inflater.inflate(R.layout.fragment_teacher_cont, container, false);

            initViews();

            return rootView;
        }



        private void initViews() {

            recyclerView = rootView.findViewById(R.id.recyclerView);
            tvSpinnerItem = rootView.findViewById(R.id.tvSpinnerItem);
            rlSpinner = rootView.findViewById(R.id.rlSpinner);
            spnGroup =  rootView.findViewById(R.id.spnGroup);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);
            List<TeacherModel> teachersList;
            TeacherDao teacher = new TeacherDao(getActivity());
            teachersList = teacher.getTeachers(groupUUID, 1);
            recyclerView.setAdapter(new TeacherContributionAdapter(getActivity(), teachersList));


            rlSpinner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    spnGroup.performClick();
                }
            });
            spnGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (spinnerClickCount!=0){
                        tvSpinnerItem.setText(adapterView.getSelectedItem().toString());
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

        }
}
