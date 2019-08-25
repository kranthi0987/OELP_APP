package mahiti.org.oelp.views.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mahiti.org.oelp.R;
import mahiti.org.oelp.database.DAOs.TeacherDao;
import mahiti.org.oelp.models.TeacherModel;
import mahiti.org.oelp.utils.Constants;

public class TeacherInfoFragment extends Fragment {

    private View rootView;

    String teacherUUid = "";
    List<TeacherModel> teacherModelList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        teacherUUid = getActivity().getIntent().getStringExtra(Constants.TEACHER_UUID);

        TeacherDao teacherDao = new TeacherDao(getActivity());
        teacherModelList = teacherDao.getTeachers(teacherUUid, 2);

        rootView = inflater.inflate(R.layout.fragment_teacher_info, container, false);

        displayTeacherInfo();
        return rootView;
    }

    public void displayTeacherInfo() {

        if (teacherModelList.isEmpty())
            return;

        TextView teacherName=rootView.findViewById(R.id.textViewTeacherName);
        TextView teacherPhoneNumber=rootView.findViewById(R.id.textViewPhoneno);
        TextView teacherDistrict=rootView.findViewById(R.id.textViewDistrict);
        TextView teacherBlock=rootView.findViewById(R.id.textViewBlock);
        TextView teacherNoContributions=rootView.findViewById(R.id.textViewNoofContri);
        TextView teacherGroupName=rootView.findViewById(R.id.textViewGroupName);
        TextView teacherSubUnits=rootView.findViewById(R.id.textViewSubunits);
        TextView teacherLastActive=rootView.findViewById(R.id.textViewLastActive);
        TextView teacherLastLoggedIn=rootView.findViewById(R.id.textViewLastLoggedin);
        TextView teacherViewSchool=rootView.findViewById(R.id.textViewSchool);

        teacherName.setText(teacherModelList.get(0).getName());
        teacherPhoneNumber.setText(teacherModelList.get(0).getMobileNumber());
        teacherDistrict.setText(teacherModelList.get(0).getBlockName());
        teacherBlock.setText(teacherModelList.get(0).getBlockName());
        teacherNoContributions.setText("0");
        teacherGroupName.setText(teacherModelList.get(0).getGroupName());
        teacherSubUnits.setText(teacherModelList.get(0).getVideoCoveredCount());
        teacherLastActive.setText(teacherModelList.get(0).getLastActive());
        teacherLastLoggedIn.setText(teacherModelList.get(0).getLastLoggedIn());
        teacherViewSchool.setText(teacherModelList.get(0).getSchool());



    }

}
