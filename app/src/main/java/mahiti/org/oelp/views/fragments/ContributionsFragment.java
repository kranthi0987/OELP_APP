package mahiti.org.oelp.views.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
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

import com.iceteck.silicompressorr.SiliCompressor;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import mahiti.org.oelp.R;
import mahiti.org.oelp.database.DAOs.MediaContentDao;
import mahiti.org.oelp.fileandvideodownloader.DownloadConstant;
import mahiti.org.oelp.fileandvideodownloader.DownloadUtility;
import mahiti.org.oelp.fileandvideodownloader.ToastUtils;
import mahiti.org.oelp.models.MobileVerificationResponseModel;
import mahiti.org.oelp.models.SharedMediaModel;
import mahiti.org.oelp.services.ApiInterface;
import mahiti.org.oelp.services.RetrofitClass;
import mahiti.org.oelp.services.RetrofitConstant;
import mahiti.org.oelp.utils.AppUtils;
import mahiti.org.oelp.utils.CheckNetwork;
import mahiti.org.oelp.utils.Constants;
import mahiti.org.oelp.utils.Logger;
import mahiti.org.oelp.utils.MySharedPref;
import mahiti.org.oelp.views.adapters.MyContAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Url;

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
    private ProgressBar progressBar;
    private String userUUId;
    private boolean folderToSaveVideo;
    private boolean folderToSaveVideo1;
    private String mediaCreationUUID;


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

        return rootView;
    }

    @SuppressLint("RestrictedApi")
    private void initViews(View view) {

        sharedPref = new MySharedPref(getActivity());
        userUUId = sharedPref.readString(Constants.USER_ID, "");

        fab = view.findViewById(R.id.fab);

        progressBar = view.findViewById(R.id.progressBar);

        ivTeacherContribution = view.findViewById(R.id.ivTeacherContribution);
        ivGroupContribution = view.findViewById(R.id.ivGroupContribution);

        llMain = view.findViewById(R.id.llMain);
        llSwitch = view.findViewById(R.id.llSwitch);
        llSwitch.setVisibility(View.VISIBLE);

        tvError = view.findViewById(R.id.tvError);
        switchContribution = view.findViewById(R.id.switchContribution);
        switchContribution.setOnCheckedChangeListener(this);
        recyclerView = view.findViewById(R.id.recyclerView);


        GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        adapter = new MyContAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setFocusable(false);

//        setViewForSwitch(false);

        fab.setOnClickListener(view1 -> {
            showDocumentsView();
        });

    }

    public void showDocumentsView() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        String[] mimeTypes =
                {"image/*", "video/mp4"};

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*"

        startActivityForResult(intent, Constants.READ_REQUEST_CODE);

    }

    private void checkInternetAndCAllApi() {

//        if (CheckNetwork.checkNet(getActivity())) {
//            callApiForMediaSharedList(userUUId);
//        } else {
            fetchDataFromDb(switchContribution.isChecked());
//        }
    }


    private void fetchDataFromDb(boolean checked) {
        progressBar.setVisibility(View.VISIBLE);
        sharedMediaList = new ArrayList<>();
        MediaContentDao mediaContentDao = new MediaContentDao(getActivity());
        String uuid;
        setViewForSwitch(checked);
        if (checked) {
            uuid = groupUUID;
        } else {
            uuid = userUUId;
        }
        sharedMediaList = mediaContentDao.getSharedMedia(uuid, switchContribution.isChecked());
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

    public void callApiForMediaSharedList(String userUUID) {
        progressBar.setVisibility(View.VISIBLE);
       /* final MobileVerificationResponseModel[] model = {null};
        ApiInterface apiInterface = RetrofitClass.getAPIService();
        Logger.logD(TAG, "URL :" + RetrofitConstant.BASE_URL + RetrofitConstant.FETCH_MEDIA_SHARED + " Param : user_uuid:" + userUUID);
        apiInterface.getMediaShared(userUUID).enqueue(new Callback<MobileVerificationResponseModel>() {
            @Override
            public void onResponse(Call<MobileVerificationResponseModel> call, Response<MobileVerificationResponseModel> response) {
                if (response.body() != null) {
                    Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.FETCH_MEDIA_SHARED + " Response :" + response.body());
                    insertDatatoTable(response.body());
                } else {
                    fetchDataFromDb(switchContribution.isChecked());
                }
            }

            @Override
            public void onFailure(Call<MobileVerificationResponseModel> call, Throwable t) {
                Logger.logD(TAG, "URL " + RetrofitConstant.BASE_URL + RetrofitConstant.FETCH_MEDIA_SHARED + " Response :" + t.getMessage());
                fetchDataFromDb(switchContribution.isChecked());
            }
        });*/
    }

    private void insertDatatoTable(MobileVerificationResponseModel body) {
        if (body.getData() != null && !body.getData().isEmpty()) {
            MediaContentDao mediaContentDao = new MediaContentDao(getActivity());
            mediaContentDao.insertSharedMedia(body.getData());
            if (body.getGlobally() != null && !body.getGlobally().isEmpty()) {
                mediaContentDao.updateGloabllyShareMediaUUID(body.getGlobally());
            }
            fetchDataFromDb(switchContribution.isChecked());
        }
    }


    public void showDialogForUpload(String filePath) {
        File mediaFile=null;
        try{
           mediaFile = new File(filePath);
        }catch (Exception ex){
            Logger.logE(TAG, ex.getMessage(), ex);
        }
        if (!mediaFile.exists()){

            Toast.makeText(getActivity(), "File not found", Toast.LENGTH_SHORT).show();
            return;
        }
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
            fileName = mediaFile.getAbsolutePath();
        } catch (Exception ex) {
            Logger.logE("Exception", "Exc " + ex.getMessage(), ex);
        }

        tvMediaFileName.setText(AppUtils.getFileName(fileName));
        fileType = AppUtils.getFileType(fileName);
        if (fileType == Constants.VIDEO) {
            ivPlayButton.setVisibility(View.VISIBLE);
            ivProfileImage.setBackgroundColor(getResources().getColor(R.color.blackOpaque));
        } else {
            showUIForImage(mediaFile, ivProfileImage);
            ivPlayButton.setVisibility(View.GONE);
        }

        Button btnUpload = dialogView.findViewById(R.id.btnUpload);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        File finalMediaFile = mediaFile;
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                validateAndProceed(etMediaName.getText().toString(), finalMediaFile);
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

    String fileName = "";

    private void validateAndProceed(String s, File imagesFiles) {
        fileName = s;

        if (s.isEmpty()) {
            Toast.makeText(getActivity(), "Enter File Name", Toast.LENGTH_SHORT).show();
        } else if (imagesFiles == null) {
            alertDialog.dismiss();
            Toast.makeText(getActivity(), "Please select file", Toast.LENGTH_SHORT).show();
        } else {

            alertDialog.dismiss();
            File fileDestination = null;
            if (CheckNetwork.checkNet(getActivity())) {

                if (fileType == Constants.IMAGE) {
                    fileDestination = AppUtils.completeInternalStoragePath(getActivity(), Constants.IMAGE);
                    new ImageCompressionAsyncTask(getActivity()).execute(imagesFiles.getAbsolutePath(), fileDestination.getAbsolutePath());
                } else {
                    fileDestination = AppUtils.completeInternalStoragePath(getActivity(), Constants.VIDEO);
                    new VideoCompressAsyncTask(getActivity()).execute(imagesFiles.getAbsolutePath(), fileDestination.getAbsolutePath());
                }
            } else {
                Toast.makeText(getActivity(), getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
            }
            /*if (CheckNetwork.checkNet(getActivity())) {
                uplaodImageToServer(s, imagesFiles.get(0));

            } else {
                Toast.makeText(getActivity(), getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
            }*/


        }
    }


    private void uplaodImageToServer(String fileNAme, File file) {
        progressBar.setVisibility(View.VISIBLE);
        String userUUid = sharedPref.readString(Constants.USER_ID, "");

        String date = AppUtils.getDateTime();


        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams paramMap = new RequestParams();
        paramMap.put("user_uuid", userUUid);
        paramMap.put("media_uuid", mediaCreationUUID);
        paramMap.put("group_uuid", groupUUID);
        paramMap.put("media_type", String.valueOf(fileType));
        paramMap.put("media_title", fileNAme);
        paramMap.put("submission_time", date);
        try {
            paramMap.put("media_file", file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        MobileVerificationResponseModel model = new MobileVerificationResponseModel();
        List<SharedMediaModel> dataList = new ArrayList<>();
        SharedMediaModel data = new SharedMediaModel();
        data.setMediaUuid(mediaCreationUUID);
        data.setUserUuid(userUUid);
        data.setGroupUuid(groupUUID);
        data.setMediaType(String.valueOf(fileType));
        data.setMediaTitle(fileNAme);
        data.setSubmissionTime(date);
        data.setMediaFile(file.getAbsolutePath());
        data.setUserName(sharedPref.readString(Constants.USER_NAME, ""));
        dataList.add(data);
        model.setData(dataList);


        client.post(getActivity(), RetrofitConstant.BASE_URL + RetrofitConstant.UPLAOD_MEDIA_SHARED, paramMap, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Media Shared Success", Toast.LENGTH_SHORT).show();
                insertDatatoTable(model);

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


    private void setAdapters(List<SharedMediaModel> sharedMediaList) {

        if (sharedMediaList != null && !sharedMediaList.isEmpty()) {
            tvError.setVisibility(View.GONE);
            llMain.setVisibility(View.VISIBLE);
            adapter.setList(sharedMediaList);
        } else {

            tvError.setVisibility(View.VISIBLE);
            llMain.setVisibility(View.GONE);
        }
        progressBar.setVisibility(View.GONE);
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
            String filePath = getPath(result);
            showDialogForUpload(filePath);
        }


    }




    public void createUriPath(Intent data) {
        Uri result = data.getData();
        ClipData resultMulti = data.getClipData();

        List<Uri> list = new ArrayList<>();

        if (resultMulti == null) {
            Uri filePath = result;
            list.add(filePath);
        } else {
            for (int i = 0; i < resultMulti.getItemCount(); i++) {
                ClipData.Item path = resultMulti.getItemAt(i);
                Uri fileUti = path.getUri();
                list.add(fileUti);
            }
        }

        try {

//            fileList = changeUriToFile(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
       /* File fileDestination=null;

        if (fileType==Constants.IMAGE){
            fileDestination = AppUtils.completeInternalStoragePath(getActivity(), Constants.IMAGE);
            new ImageCompressionAsyncTask(getActivity()).execute(fileList.get(0).getAbsolutePath(), fileDestination.getAbsolutePath());
        }else {
            fileDestination = AppUtils.completeInternalStoragePath(getActivity(), Constants.VIDEO);
            new VideoCompressAsyncTask(getActivity()).execute(fileList.get(0).getAbsolutePath(), fileDestination.getAbsolutePath());
        }*/

//        showDialogForUpload(fileList);
    }



    public String  getPath(Uri uri) {
        String[] projection = { MediaStore.Video.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    public File changeUriToFile(Uri fileUri) throws IOException {
        /*uploadFile = new ArrayList<>();
        List<File> oelpFileList = new ArrayList<>();
        for (Uri mediaUri : uriList) {
            String fileNameFromUri = getFileNameFromUri(mediaUri);
            fileType = AppUtils.getFileType(fileNameFromUri);
            File fileSource = null;
            File fileDes = null;
            if (fileType == Constants.VIDEO) {
                fileSource = AppUtils.completePathInSDCard(Constants.VIDEO);
                fileDes = AppUtils.completeInternalStoragePath(getActivity(), Constants.VIDEO);
            } else {
                fileSource = AppUtils.completePathInSDCard(Constants.IMAGE);
            }
            folderToSaveVideo = fileSource.exists() || fileSource.mkdirs();
            folderToSaveVideo1 = fileDes.exists() || fileDes.mkdirs();
            if (!folderToSaveVideo) {
                Logger.logD(TAG, getString(R.string.couldnot_create_file));
                return null;
            }
            mediaCreationUUID = AppUtils.getUUID();
            String lastExtension = fileNameFromUri.substring(fileNameFromUri.lastIndexOf("."));
            String fileNAmeNew = mediaCreationUUID + lastExtension;
            File oelpFileS = new File(fileSource, fileNAmeNew);

            try {

                InputStream in = getActivity().getContentResolver().openInputStream(mediaUri);
                OutputStream out = new FileOutputStream(oelpFileS);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
                oelpFileList.add(oelpFileS);
                oelpFileS.createNewFile();

            } catch (Exception e) {
                Logger.logE(TAG, " checkFileExits ", e);
            }

        }
        return oelpFileList;*/
        return null;
    }


    public String getFileNameFromUri(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    class ImageCompressionAsyncTask extends AsyncTask<String, Void, String> {

        Context mContext;

        public ImageCompressionAsyncTask(Context context) {
            mContext = context;
        }

        @Override
        protected String doInBackground(String... params) {

            String filePath = SiliCompressor.with(mContext).compress(params[0], new File(params[1]));
            return filePath;

        }

        @Override
        protected void onPostExecute(String s) {


            File imageFile = new File(s);
            uplaodImageToServer(fileName, imageFile);

        }


    }

    class VideoCompressAsyncTask extends AsyncTask<String, String, String> {

        Context mContext;

        public VideoCompressAsyncTask(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            imageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_photo_camera_white_48px));
//            compressionMsg.setVisibility(View.VISIBLE);
//            picDescription.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... paths) {
            String filePath = null;
            try {

                filePath = SiliCompressor.with(mContext).compressVideo(paths[0], paths[1]);

            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return filePath;

        }


        @Override
        protected void onPostExecute(String compressedFilePath) {
            super.onPostExecute(compressedFilePath);
            File imageFile = new File(compressedFilePath);
            float length = imageFile.length() / 1024f; // Size in KB
            Log.i("Sizr", "Path: " + length);
            uplaodImageToServer(fileName, imageFile);
        }
    }
}
