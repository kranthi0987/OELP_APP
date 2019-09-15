package mahiti.org.oelp.views.fragments;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import mahiti.org.oelp.R;
import mahiti.org.oelp.database.DAOs.MediaContentDao;
import mahiti.org.oelp.databinding.FragmentContributionsBinding;
import mahiti.org.oelp.models.MobileVerificationResponseModel;
import mahiti.org.oelp.models.SharedMediaModel;
import mahiti.org.oelp.services.ApiInterface;
import mahiti.org.oelp.services.RetrofitClass;
import mahiti.org.oelp.services.RetrofitConstant;
import mahiti.org.oelp.utils.AppUtils;
import mahiti.org.oelp.utils.CheckNetwork;
import mahiti.org.oelp.utils.Constants;
import mahiti.org.oelp.utils.ImageCaptureChooseFromGallery;
import mahiti.org.oelp.utils.Logger;
import mahiti.org.oelp.utils.MySharedPref;
import mahiti.org.oelp.viewmodel.ContributionsViewModel;
import mahiti.org.oelp.views.activities.ChatAndContributionActivity;
import mahiti.org.oelp.views.adapters.MyContAdapter;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContributionsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private static final String TAG = ContributionsFragment.class.getSimpleName();
    private View rootView;
    private RecyclerView recyclerView;
    private ContributionsViewModel viewModel;
    private FragmentContributionsBinding binding;
    private MyContAdapter adapter;
    ImageView ivTeacherContribution;
    ImageView ivGroupContribution;
    Switch switchContribution;
    private MySharedPref sharedPref;
    private boolean groupSwitch = false;
    private List<SharedMediaModel> sharedMediaList;
    private String groupUUID;
    ProgressBar progressBar;
    LinearLayout llMain;
    TextView tvError;
    private FloatingActionButton fab;
    private AlertDialog alertDialog;
    private boolean alertDialogWasOpen = false;
    private int fileType;
    private RelativeLayout llSwitch;



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
        groupUUID = getActivity().getIntent().getStringExtra("groupUUID");
        initViews(binding);

        rootView = binding.getRoot();
        setHasOptionsMenu(true);

        return rootView;
    }

    @SuppressLint("RestrictedApi")
    private void initViews(FragmentContributionsBinding binding) {

        sharedPref = new MySharedPref(getActivity());
        groupSwitch = sharedPref.readBoolean(Constants.Group_Switch, false);
        recyclerView = binding.recyclerView;
        fab = binding.fab;
        if (getActivity() instanceof ChatAndContributionActivity)
            fab.setVisibility(View.VISIBLE);
        else
            fab.setVisibility(View.GONE);
        ivTeacherContribution = binding.ivTeacherContribution;
        progressBar = binding.progressBar;
        llMain = binding.llMain;
        llSwitch = binding.llSwitch;
        tvError = binding.tvError;
        ivGroupContribution = binding.ivGroupContribution;
        switchContribution = binding.switchContribution;
        switchContribution.setOnCheckedChangeListener(this);


        GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        adapter = new MyContAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setFocusable(false);


        setViewAndDataForGroup(1);


        fab.setOnClickListener(view -> {
            new ImageCaptureChooseFromGallery(Constants.CHOOSE_GALLERY, ContributionsFragment.this, getActivity());
        });


    }

    public void showDialogForUpload(List<File> imagesFiles) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        View dialogView = getLayoutInflater().inflate(R.layout.profile_layout, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(true);
        String fileName = "";

        TextView tvMediaFileName = dialogView.findViewById(R.id.tvMediaFileName);
        ImageView ivProfileImage = dialogView.findViewById(R.id.ivProfileImage);
        ImageView ivPlayButton = dialogView.findViewById(R.id.ivPlayButton);
        EditText etMediaName = dialogView.findViewById(R.id.etMediaName);

        try {
            fileName = imagesFiles.get(0).getAbsolutePath();
        } catch (Exception ex) {
            Logger.logE("Exception", "Exc " + ex.getMessage(), ex);
        }

        tvMediaFileName.setText(fileName);
        fileType = AppUtils.getFileType(fileName);
        if (fileType == Constants.VIDEO) {
            ivPlayButton.setVisibility(View.VISIBLE);
            ivProfileImage.setBackgroundColor(getResources().getColor(R.color.blackOpaque));
        } else {

            showUIForImage(imagesFiles.get(0), ivProfileImage);
            ivPlayButton.setVisibility(View.GONE);
        }

        Button btnUpload = dialogView.findViewById(R.id.btnUpload);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateAndProceed(etMediaName.getText().toString(), imagesFiles);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });
        alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    public void showUIForImage(File file, ImageView ivPlayButton) {
        if (file.exists()) {
            Picasso.get()
                    .load("file://" + file)
                    .fit()
                    .into(ivPlayButton);
        }
    }

    private void validateAndProceed(String s, List<File> imagesFiles) {

        if (s.isEmpty()) {
            Toast.makeText(getActivity(), "Enter File Name", Toast.LENGTH_SHORT).show();
        } else if (imagesFiles == null) {
            alertDialog.dismiss();
            Toast.makeText(getActivity(), "Please select file", Toast.LENGTH_SHORT).show();
        } else if (imagesFiles.isEmpty()) {
            alertDialog.dismiss();
            Toast.makeText(getActivity(), "Please select file", Toast.LENGTH_SHORT).show();
        } else {
            alertDialog.dismiss();
            if (CheckNetwork.checkNet(getActivity())) {
                uplaodImageToServer(s, imagesFiles.get(0));

            } else {
                Toast.makeText(getActivity(), getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void uplaodImageToServer(String fileNAme, File file) {
        progressBar.setVisibility(View.VISIBLE);
        String userUUid = sharedPref.readString(Constants.USER_ID, "");
        String mediaUUid = AppUtils.getUUID();
        String date = AppUtils.getDateTime();


        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams paramMap = new RequestParams();
        paramMap.put("user_uuid", userUUid);
        paramMap.put("media_uuid", mediaUUid);
        paramMap.put("group_uuid", groupUUID);
        paramMap.put("media_type", fileType);
        paramMap.put("media_title", fileNAme);
        paramMap.put("submission_time", date);
        try {
            paramMap.put("media_file", file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        client.post(getActivity(), RetrofitConstant.BASE_URL2 + RetrofitConstant.UPLAOD_MEDIA_SHARED, paramMap, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Media Shared Success", Toast.LENGTH_SHORT).show();
                getTeacherList(sharedPref.readString(Constants.USER_ID, ""));

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Media Shared Failure", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onRetry(int retryNo) {
                Log.d("Tag", "retry");
            }
        });
    }

    public void getTeacherList(String userUUID) {
        final MobileVerificationResponseModel[] model = {null};
        ApiInterface apiInterface = RetrofitClass.getAPIService();
        Logger.logD(TAG, "URL :" + RetrofitConstant.BASE_URL2 + RetrofitConstant.FETCH_MEDIA_SHARED + " Param : user_uuid:" + userUUID);
        apiInterface.getMediaShared(userUUID).enqueue(new Callback<MobileVerificationResponseModel>() {
            @Override
            public void onResponse(Call<MobileVerificationResponseModel> call, Response<MobileVerificationResponseModel> response) {
                Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL2 + RetrofitConstant.FETCH_MEDIA_SHARED + " Response :" + response.body());
                insertDatatoTable(response.body());


            }

            @Override
            public void onFailure(Call<MobileVerificationResponseModel> call, Throwable t) {
                Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL2 + RetrofitConstant.FETCH_MEDIA_SHARED + " Response :" + t.getMessage());

            }
        });
    }

    private void insertDatatoTable(MobileVerificationResponseModel body) {
        if (body.getData() != null && !body.getData().isEmpty()) {
            MediaContentDao mediaContentDao = new MediaContentDao(getActivity());
            mediaContentDao.insertSharedMedia(body.getData());
            if (body.getGlobally() != null && !body.getGlobally().isEmpty()) {
                long updateLong = mediaContentDao.updateGloabllyShareMediaUUID(body.getGlobally());
                fetchDataFromDb(groupSwitch, 0);
            }
        }
    }


    private void fetchDataFromDb(boolean forGroup, int i) {
        MediaContentDao mediaContentDao = new MediaContentDao(getActivity());
        sharedMediaList = mediaContentDao.getSharedMedia(groupUUID, forGroup, sharedPref.readString(Constants.USER_ID, ""));
        progressBar.setVisibility(View.GONE);

        if (sharedMediaList != null && !sharedMediaList.isEmpty()) {
            if (i==1){
                llSwitch.setVisibility(View.VISIBLE);
            }
            adapter.setList(sharedMediaList);
            tvError.setVisibility(View.GONE);
            llMain.setVisibility(View.VISIBLE);
        } else {
            if (i==1){
                llSwitch.setVisibility(View.GONE);
            }
            tvError.setVisibility(View.VISIBLE);
            llMain.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
        if (checked) {
            sharedPref.writeBoolean(Constants.Group_Switch, true);
            setViewAndDataForGroup(0);
        } else {
            sharedPref.writeBoolean(Constants.Group_Switch, false);
            setViewAndDataForMember(0);
        }
    }

    /*
     * Switch button for Group */

    private void setViewAndDataForGroup(int i) {
        switchContribution.setChecked(true);
        ivTeacherContribution.setVisibility(View.INVISIBLE);
        ivGroupContribution.setVisibility(View.VISIBLE);
        switchContribution.getTrackDrawable().setColorFilter(ContextCompat.getColor(getActivity(), R.color.green), PorterDuff.Mode.SRC_IN);

        progressBar.setVisibility(View.VISIBLE);
        fetchDataFromDb(true, i);

    }

    /*
     * Switch button for Group */

    private void setViewAndDataForMember(int i) {
        switchContribution.setChecked(false);
        ivTeacherContribution.setVisibility(View.VISIBLE);
        ivGroupContribution.setVisibility(View.INVISIBLE);
        switchContribution.getTrackDrawable().setColorFilter(ContextCompat.getColor(getActivity(), R.color.red), PorterDuff.Mode.SRC_IN);

        progressBar.setVisibility(View.VISIBLE);
        fetchDataFromDb(false, i);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        EasyImage.handleActivityResult(requestCode, resultCode, data, getActivity(), new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
            }

            @Override
            public void onImagesPicked(List<File> imagesFiles, EasyImage.ImageSource source, int type) {
                //Handle the images
                if (!imagesFiles.isEmpty()) {
                    List<File> imageFileList = imagesFiles;
                    if (imageFileList.get(0).length() > 20971520) {
                        Toast.makeText(getActivity(), "File size exceeding 20 MB", Toast.LENGTH_SHORT).show();
                    } else {
                        AppUtils.checkImageFileLocation(getActivity(), imagesFiles);
                        showDialogForUpload(imagesFiles);
                    }
                }
            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                // Cancel handling, you might wanna remove taken photo if it was canceled
                if (source == EasyImage.ImageSource.CAMERA_IMAGE) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(getActivity());
                    if (photoFile != null) photoFile.delete();
                }
            }
        });
    }
}
