package mahiti.org.oelp.views.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.iceteck.silicompressorr.SiliCompressor;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cz.msebera.android.httpclient.Header;
import mahiti.org.oelp.R;
import mahiti.org.oelp.database.DAOs.MediaContentDao;
import mahiti.org.oelp.interfaces.ListRefresh;
import mahiti.org.oelp.models.MobileVerificationResponseModel;
import mahiti.org.oelp.models.SharedMediaModel;
import mahiti.org.oelp.services.ApiInterface;
import mahiti.org.oelp.services.RetrofitClass;
import mahiti.org.oelp.services.RetrofitConstant;
import mahiti.org.oelp.utils.AppUtils;
import mahiti.org.oelp.utils.Constants;
import mahiti.org.oelp.utils.FileUtils;
import mahiti.org.oelp.utils.Logger;
import mahiti.org.oelp.utils.MySharedPref;
import mahiti.org.oelp.views.activities.ChatAndContributionActivity;
import mahiti.org.oelp.views.adapters.MyContAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContributionsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private static final String TAG = ContributionsFragment.class.getSimpleName();
    private View rootView;
    private RecyclerView recyclerView;
    private MyContAdapter adapter;
    ImageView ivTeacherContribution;
    ImageView ivGroupContribution;
    Switch switchContribution;
    private MySharedPref sharedPref;
    private List<SharedMediaModel> sharedMediaList;
    private String groupUUID;
    LinearLayout llMain;
    TextView tvError;
    private FloatingActionButton fab;
    private AlertDialog alertDialog;
    private boolean alertDialogWasOpen = false;
    private int fileType;
    private RelativeLayout llSwitch;
    private String userUUID;
    private String date;
    private File fileDestination = null;
    private GridLayoutManager manager;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_contributions, container, false);
        groupUUID = getActivity().getIntent().getStringExtra("groupUUID");

        initViews(rootView);
        setHasOptionsMenu(true);
        checkInternetAndCAllApi();

        ((ChatAndContributionActivity) getActivity()).setFragmentRefreshListener(new ListRefresh() {
            @Override
            public void onRefresh(int position, boolean isDelete) {

                if (isDelete) {
                    checkInternetAndCAllApi();
                } else {
                    Toast.makeText(getActivity(), "Share Media Global Success", Toast.LENGTH_SHORT).show();
                    checkInternetAndCAllApi();
                }
            }
        });

        return rootView;
    }

    @SuppressLint("RestrictedApi")
    private void initViews(View view) {

        sharedPref = new MySharedPref(getActivity());
        userUUID = sharedPref.readString(Constants.USER_ID, "");

        fab = view.findViewById(R.id.fab);


        ivTeacherContribution = view.findViewById(R.id.ivTeacherContribution);
        ivGroupContribution = view.findViewById(R.id.ivGroupContribution);

        llMain = view.findViewById(R.id.llMain);
        llSwitch = view.findViewById(R.id.llSwitch);
        llSwitch.setVisibility(View.VISIBLE);

        tvError = view.findViewById(R.id.tvError);
        switchContribution = view.findViewById(R.id.switchContribution);
        switchContribution.setOnCheckedChangeListener(this);
        recyclerView = view.findViewById(R.id.recyclerView);


        manager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setFocusable(false);
        adapter = new MyContAdapter(getActivity());
        recyclerView.setAdapter(adapter);


        fab.setOnClickListener(view1 -> {
            showDocumentsView();
        });

    }

    public void showFileSelectionDialog(){

        new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme)
                .setTitle(R.string.media_type_selection)
                .setItems(R.array.picker_array, (dialog, which) -> {
                    if (which == 0) {
                        /*deleteData(model, position);*/
                    } else if (which==1) {
                        /*shareDataGlobally(model, position);*/
                    }else{
                        /*shareDataGlobally(model, position);*/
                    }

                    dialog.dismiss();

                })
                .show();
    }

    public void showDocumentsView() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        String[] mimeTypes =
                {"image/*", "video/*", "application/pdf"};

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        intent.setType("image/*, video/*, application/pdf");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*"

        startActivityForResult(intent, Constants.READ_REQUEST_CODE);

    }

    private void checkInternetAndCAllApi() {
//        if (CheckNetwork.checkNet(getActivity())) {
//            callMediaSharedApi(userUUID, sharedPref.readString(Constants.GROUP_UUID_LIST,""));
//        } else {
            fetchDataFromDb(switchContribution.isChecked());
//        }
    }

    private void callMediaSharedApi(String userId, String usergroup1) {

        if (usergroup1.isEmpty() || usergroup1.equalsIgnoreCase("[]"))
            return;

        String usergroup = AppUtils.makeJsonArray(usergroup1);
        if (usergroup.isEmpty())
            return;

        ApiInterface apiInterface = RetrofitClass.getAPIService();
        Logger.logD(TAG, "URL :" + RetrofitConstant.BASE_URL + RetrofitConstant.FETCH_MEDIA_SHARED + " Param : user_uuid:" + userId+" group_uuid "+usergroup);
        apiInterface.getMediaShared(userId, usergroup).enqueue(new Callback<MobileVerificationResponseModel>() {
            @Override
            public void onResponse(Call<MobileVerificationResponseModel> call, Response<MobileVerificationResponseModel> response) {
                if (response.body() != null) {
                    new MediaContentDao(getActivity()).insertSharedData(response.body().getData(), 0);
                    fetchDataFromDb(switchContribution.isChecked());
                }
                Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.FETCH_MEDIA_SHARED + " Response :" + response.body());
            }

            @Override
            public void onFailure(Call<MobileVerificationResponseModel> call, Throwable t) {
                Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.FETCH_MEDIA_SHARED + " Response :" + t.getMessage());
            }
        });
    }


    private void fetchDataFromDb(boolean checked) {
        sharedMediaList = new ArrayList<>();
        MediaContentDao mediaContentDao = new MediaContentDao(getActivity());
        setViewForSwitch(checked);
        sharedMediaList = mediaContentDao.fetchSharedMedia(userUUID, groupUUID, checked, 0);
        setAdapters(sharedMediaList);
    }

    private void setViewForSwitch(boolean checked) {
        if (checked) {
            switchContribution.setChecked(true);
            ivTeacherContribution.setVisibility(View.INVISIBLE);
            ivGroupContribution.setVisibility(View.VISIBLE);
            switchContribution.getTrackDrawable().setColorFilter(ContextCompat.getColor(getActivity(), R.color.green), PorterDuff.Mode.SRC_IN);
        } else {
            switchContribution.setChecked(false);
            ivTeacherContribution.setVisibility(View.VISIBLE);
            ivGroupContribution.setVisibility(View.INVISIBLE);
            switchContribution.getTrackDrawable().setColorFilter(ContextCompat.getColor(getActivity(), R.color.red), PorterDuff.Mode.SRC_IN);
        }
    }


    public void showDialogForUpload(String filePath) {
        File mediaFile = null;
        try {
            mediaFile = new File(filePath);
            Log.d(TAG, filePath + " size before compression : " + mediaFile.length());
        } catch (Exception ex) {
            Logger.logE(TAG, ex.getMessage(), ex);
        }
        if (mediaFile!=null && !mediaFile.exists()) {

            Toast.makeText(getActivity(), "File not found", Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        View dialogView = getLayoutInflater().inflate(R.layout.upload_layout, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(true);
        String fileName = "";

        TextView tvMediaFileName = dialogView.findViewById(R.id.tvMediaFileName);
        ImageView ivProfileImage = dialogView.findViewById(R.id.ivProfileImage);
        EditText etMediaName = dialogView.findViewById(R.id.etMediaName);

        try {
            fileName = mediaFile.getAbsolutePath();
        } catch (Exception ex) {
            Logger.logE("Exception", "Exc " + ex.getMessage(), ex);
        }
        tvMediaFileName.setText(AppUtils.getFileName(fileName));
        fileType = AppUtils.getFileType(fileName);
        if (fileType == Constants.VIDEO) {
            ivProfileImage.setBackgroundResource(R.drawable.videoicon);
        } else if (fileType== Constants.PDF){
            ivProfileImage.setBackgroundResource(R.drawable.pdficon);
        }else {
            showUIForImage(mediaFile, ivProfileImage);
        }

        Button btnUpload = dialogView.findViewById(R.id.btnUpload);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        File finalMediaFile = mediaFile;
        btnUpload.setOnClickListener(view -> validateAndProceed(etMediaName.getText().toString(), finalMediaFile));
        btnUpload.setOnClickListener(view -> validateAndProceed(etMediaName.getText().toString(), finalMediaFile));

        btnCancel.setOnClickListener(view -> alertDialog.cancel());
        alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    public void showUIForImage(File file, ImageView ivPlayButton) {
        if (file.exists()) {
            RequestOptions myOptions = new RequestOptions()
                    .centerCrop();
            Glide.with(getActivity())
                    .load("file://" + file)
                    .apply(myOptions)
                    .into(ivPlayButton);
        }
    }

    String fileName = "";

    private void validateAndProceed(String fileName, File imagesFiles) {
        this.fileName = fileName;
        if (fileName.isEmpty()) {
            Toast.makeText(getActivity(), "Enter File Name", Toast.LENGTH_SHORT).show();
        } else if (imagesFiles == null) {
            alertDialog.dismiss();
            Toast.makeText(getActivity(), "Please select file", Toast.LENGTH_SHORT).show();
        } else {
            alertDialog.dismiss();
            if (fileType == Constants.IMAGE) {
                fileDestination = AppUtils.completePathInSDCard(Constants.IMAGE);
                new ImageCompressionAsyncTask(getActivity()).execute(imagesFiles.getAbsolutePath(), fileDestination.getAbsolutePath());
            } else if (fileType== Constants.VIDEO){
                fileDestination = AppUtils.completePathInSDCard(Constants.VIDEO);
                new VideoCompressAsyncTask(getActivity()).execute(imagesFiles.getAbsolutePath(), fileDestination.getAbsolutePath());
            }else {
                createDataToUpload(fileName, imagesFiles);
            }


        }
    }


    private void setAdapters(List<SharedMediaModel> sharedMediaList) {

        if (sharedMediaList != null && !sharedMediaList.isEmpty()) {
            tvError.setVisibility(View.GONE);
            llMain.setVisibility(View.VISIBLE);
            adapter.setList(sharedMediaList);
        } else {

            tvError.setVisibility(View.VISIBLE);
            llMain.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
        if (checked) {
            fetchDataFromDb(checked);
        } else {
            fetchDataFromDb(checked);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != Constants.READ_REQUEST_CODE || resultCode == Activity.RESULT_CANCELED)
            return;
        else {
            Uri result = data.getData();
//            String filePath = getPath(result);
            String filePath = FileUtils.getPath(getActivity(), result);
            String groupName = data.getStringExtra(Constants.GROUP_NAME);
            ((ChatAndContributionActivity)getActivity()).setGroupName(groupName);
            if (filePath!=null){
                if (new File(filePath).length()/(1024*1024)>20){
                    Toast.makeText(getActivity(), "File size exceeds, Please select file with size of 20 MB or less", Toast.LENGTH_SHORT).show();
                }else {
                    showDialogForUpload(filePath);

                }

            }
        }
    }

    public String getPath(Uri uri) {
        String path;
        int currentVersion = android.os.Build.VERSION.SDK_INT;
        if(currentVersion>Build.VERSION_CODES.KITKAT)
        {
            // Android OS above sdk version 19.
            path = AppUtils.getUriRealPathAboveKitkat(getActivity(), uri);
        }else
        {
            // Android OS below sdk version 19
            path = AppUtils.getImageRealPath(getActivity().getContentResolver(), uri, null, getActivity());
        }

        return path;
    }

    public class ImageCompressionAsyncTask extends AsyncTask<String, Void, String> {

        Context mContext;
        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Compressing Media, please wait.");
            dialog.show();
        }

        public ImageCompressionAsyncTask(Context context) {
            mContext = context;
            dialog = new ProgressDialog(context);
        }

        @Override
        protected String doInBackground(String... params) {
            String filePath = SiliCompressor.with(mContext).compress(params[0], new File(params[1]));
            return filePath;
        }

        @Override
        protected void onPostExecute(String s) {
            File imageFile = new File(s);
            Log.d(TAG, imageFile + " size after compression : " + imageFile.length());
            createDataToUpload(fileName, imageFile);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

        }


    }

    class VideoCompressAsyncTask extends AsyncTask<String, String, String> {

        Context mContext;
        private ProgressDialog dialog;

        public VideoCompressAsyncTask(Context context) {
            mContext = context;
            dialog = new ProgressDialog(context);


        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            dialog.setMessage("Compressing Media, please wait.");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... paths) {
            String filePath = null;
            try {

                AppUtils.createDir(new File(paths[1]));


                filePath = SiliCompressor.with(mContext).compressVideo(paths[0], paths[1]);
                Log.d(TAG, filePath + " size after compression : " + filePath.length());

            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return filePath;

        }


        @Override
        protected void onPostExecute(String compressedFilePath) {
            super.onPostExecute(compressedFilePath);
            File imageFile = new File(compressedFilePath);
            createDataToUpload(fileName, imageFile);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    private void createDataToUpload(String fileNAme, File file) {

        date = AppUtils.getDateTime();
        String mediaCreationUUID = AppUtils.getUUID();

        int syncStatus = 1;
        MobileVerificationResponseModel model = new MobileVerificationResponseModel();
        List<SharedMediaModel> dataList = new ArrayList<>();
        SharedMediaModel data = new SharedMediaModel();
        data.setMediaUuid(mediaCreationUUID);
        data.setUserUuid(userUUID);
        data.setGroupUuid(groupUUID);
        data.setMediaType(String.valueOf(fileType));

        /*Changing file name to uuid.mp4  aor uuid to jpg*/

        /*File oldFile = file;
        File newFile = null;
        if (fileType == Constants.IMAGE)
            newFile = new File(fileDestination, mediaCreationUUID + ".jpg");
        else
            newFile = new File(fileDestination, mediaCreationUUID + ".mp4");
        file = AppUtils.renameFileName(oldFile, newFile) ?newFile:oldFile;
*/
        data.setActive(2);
        data.setMediaTitle(fileNAme);
        data.setSubmissionTime(date);
        data.setMediaFile(file.getAbsolutePath());
        data.setUserName(sharedPref.readString(Constants.USER_NAME, ""));
        data.setSyncStatus(syncStatus);
        dataList.add(data);
        model.setData(dataList);

        /*
         * Adding new data to list to reflect in view*/
//        sharedMediaList.add(0,data);
//       adapter = new MyContAdapter(getActivity(), sharedMediaList);
//        recyclerView.setAdapter(adapter);
        /*recyclerView.setLayoutManager(manager);*/
//        adapter.setList(sharedMediaList);
        insertData(model);
    }


    private void insertData(MobileVerificationResponseModel body) {
        if (body.getData() != null && !body.getData().isEmpty()) {
            MediaContentDao mediaContentDao = new MediaContentDao(getActivity());
            mediaContentDao.insertSharedData(body.getData(), 1);
            sharedPref.writeBoolean(Constants.MEDIACONTENTCHANGE, true);
            fetchDataFromDb(switchContribution.isChecked());
        }
    }


}
