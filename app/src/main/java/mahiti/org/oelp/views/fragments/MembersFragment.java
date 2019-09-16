package mahiti.org.oelp.views.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import mahiti.org.oelp.R;
import mahiti.org.oelp.database.CreateGroupActivity;
import mahiti.org.oelp.database.DAOs.TeacherDao;
import mahiti.org.oelp.database.DatabaseHandlerClass;
import mahiti.org.oelp.models.MobileVerificationResponseModel;
import mahiti.org.oelp.models.TeacherModel;
import mahiti.org.oelp.services.ApiInterface;
import mahiti.org.oelp.services.RetrofitClass;
import mahiti.org.oelp.services.RetrofitConstant;
import mahiti.org.oelp.utils.AppUtils;
import mahiti.org.oelp.utils.CheckNetwork;
import mahiti.org.oelp.utils.Constants;
import mahiti.org.oelp.utils.Logger;
import mahiti.org.oelp.utils.MySharedPref;
import mahiti.org.oelp.views.activities.ChatAndContributionActivity;
import mahiti.org.oelp.views.adapters.MembersAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MembersFragment extends Fragment {

    private static final String TAG = MembersFragment.class.getSimpleName();
    private RecyclerView recyclerView;
    private TextView tvSpinnerItem;
    private MembersAdapter membersAdapter;
    private View rootView;
    String groupUUID = "";
    String groupName = "";
    private ProgressBar progressBar;
    private MySharedPref sharedPref;
    private String userUUiD;
    private TeacherDao teacherDao;
    private List<TeacherModel> teacherList;
    private FloatingActionButton fab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        groupUUID = getActivity().getIntent().getStringExtra("groupUUID");
        groupName = getActivity().getIntent().getStringExtra("groupName");
        rootView = inflater.inflate(R.layout.fragment_members, container, false);
        initViews();
        checkInternetAndCAllApi();

        return rootView;
    }

    private void checkInternetAndCAllApi() {
        progressBar.setVisibility(View.VISIBLE);
        if (CheckNetwork.checkNet(getActivity())) {
            callApiForTeacherList(userUUiD);
        } else {
            fetchTeacherList();
        }
    }

    private void callApiForTeacherList(String userId) {
        ApiInterface apiInterface = RetrofitClass.getAPIService();
        Logger.logD(TAG, "URL :" + RetrofitConstant.BASE_URL + RetrofitConstant.TEACHER_LIST_URL + " Param : userId:" + userId);
        apiInterface.getTeacherList(userId).enqueue(new Callback<MobileVerificationResponseModel>() {
            @Override
            public void onResponse(Call<MobileVerificationResponseModel> call, Response<MobileVerificationResponseModel> response) {

                MobileVerificationResponseModel model = response.body();
                if (model != null) {
                    Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.TEACHER_LIST_URL + " Response :" + response.body());
                    insertDataIntoTeacherTable(model.getTeachers());
                } else {
                    fetchTeacherList();
                }

            }

            @Override
            public void onFailure(Call<MobileVerificationResponseModel> call, Throwable t) {
                Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.TEACHER_LIST_URL + " Response :" + t.getMessage());
                fetchTeacherList();
            }
        });
    }

    private void insertDataIntoTeacherTable(List<TeacherModel> teachers) {
        if (!teachers.isEmpty()) {
            teacherDao.insertTeacherDataToDB(teachers);
            teacherList = teacherDao.getTeachers(groupUUID, 1);
            fetchTeacherList();
        }
    }

    private void fetchTeacherList() {
        teacherList = teacherDao.getTeachers(groupUUID, 1);
        setDataToAdapter(teacherList);
        progressBar.setVisibility(View.GONE);
    }

    private void setDataToAdapter(List<TeacherModel> teacherList) {
        if (teacherList!=null)
            membersAdapter.setList(teacherList);
    }


    @SuppressLint("RestrictedApi")
    private void initViews() {

        recyclerView = rootView.findViewById(R.id.recyclerView);
        progressBar = rootView.findViewById(R.id.progressBar);
        tvSpinnerItem = rootView.findViewById(R.id.tvSpinnerItem);
        fab = rootView.findViewById(R.id.fab);


        sharedPref = new MySharedPref(getActivity());
        userUUiD = sharedPref.readString(Constants.USER_ID, "");
        teacherDao = new TeacherDao(getActivity());

        if (sharedPref.readInt(Constants.USER_TYPE, Constants.USER_TEACHER)==Constants.USER_TRAINER){
            fab.setVisibility(View.VISIBLE);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        membersAdapter = new MembersAdapter(getActivity());
        recyclerView.setAdapter(membersAdapter);

        fab.setOnClickListener(view -> moveToGroupActivity());

    }

    public void moveToGroupActivity() {
        Intent intent = new Intent(getActivity(), CreateGroupActivity.class);
        intent.putExtra("groupUUID", groupUUID);
        intent.putExtra("groupName", groupName);
        startActivityForResult(intent, 103);
        getActivity().overridePendingTransition(R.anim.anim_slide_in_left,
                R.anim.anim_slide_out_left);
    }


}
