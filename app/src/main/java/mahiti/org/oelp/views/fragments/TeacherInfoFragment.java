package mahiti.org.oelp.views.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mahiti.org.oelp.R;
import mahiti.org.oelp.database.DAOs.CatalogDao;
import mahiti.org.oelp.database.DAOs.LocationDao;
import mahiti.org.oelp.database.DAOs.TeacherDao;
import mahiti.org.oelp.database.DatabaseHandlerClass;
import mahiti.org.oelp.models.TeacherModel;
import mahiti.org.oelp.utils.Constants;

public class TeacherInfoFragment extends Fragment {

    private View rootView;

    private String teacherUUid = "";
    private String teacherContri = "";
    List<TeacherModel> teacherModelList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        teacherUUid = getActivity().getIntent().getStringExtra(Constants.TEACHER_UUID);
        teacherContri = getActivity().getIntent().getStringExtra("teachercontri");

        TeacherDao teacherDao = new TeacherDao(getActivity());
        teacherModelList = teacherDao.getTeachers(teacherUUid, 2);

        rootView = inflater.inflate(R.layout.fragment_teacher_info, container, false);

        displayTeacherInfo();
        CatalogDao catalogDao = new CatalogDao(getActivity());
        return rootView;
    }

    public void displayTeacherInfo() {

        if (teacherModelList.isEmpty())
            return;

        TeacherModel model = teacherModelList.get(0);
        LocationDao locationDao = new LocationDao(getActivity());

        TextView tvName = rootView.findViewById(R.id.tvName);
        TextView tvPhone = rootView.findViewById(R.id.tvPhone);
        TextView tvSchool = rootView.findViewById(R.id.tvSchool);
        TextView tvBlock = rootView.findViewById(R.id.tvBlock);
        TextView tvDistrict = rootView.findViewById(R.id.tvDistrict);
        TextView tvState = rootView.findViewById(R.id.tvState);
        TextView tvContri = rootView.findViewById(R.id.tvContri);
        TextView tvGroupName = rootView.findViewById(R.id.tvGroupName);
        TextView tvVideosCovererd = rootView.findViewById(R.id.tvVideosCovererd);
        TextView tvLastActive = rootView.findViewById(R.id.tvLastActive);
        TextView tvLastLoggedin = rootView.findViewById(R.id.tvLastLoggedin);
        TextView tvSessionUsage = rootView.findViewById(R.id.tvSessionUsage);

        tvName.setText(model.getName());
        tvPhone.setText(model.getMobileNumber());
        tvSchool.setText(model.getSchool());
        if (model.getBlockIds()!=null) {
            String blockName = locationDao.getName(model.getBlockIds());
            tvBlock.setText(blockName);
        }
        if (model.getDistrictId()!=null) {
            String DisName = locationDao.getName(model.getDistrictId());
            tvDistrict.setText(DisName);
        }
        if (model.getStateId()!=null) {
            String stateName = locationDao.getName(model.getStateId());
            tvState.setText(stateName);
        }
        tvContri.setText(teacherContri);
        tvGroupName.setText(model.getGroupName());
        tvVideosCovererd.setText("");
        tvLastActive.setText(model.getLastActive());
        tvLastLoggedin.setText(model.getLastLoggedIn());
        tvSessionUsage.setText("");




    }

}
