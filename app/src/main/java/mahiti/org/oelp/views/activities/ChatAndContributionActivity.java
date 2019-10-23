package mahiti.org.oelp.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatException;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import mahiti.org.oelp.R;
import mahiti.org.oelp.chat.ConversationActivity;
import mahiti.org.oelp.chat.service.XMPP;
import mahiti.org.oelp.chat.utilies.ConnectionUtils;
import mahiti.org.oelp.database.DAOs.MediaContentDao;
import mahiti.org.oelp.databinding.ActivityChatAndContributionBinding;
import mahiti.org.oelp.fileandvideodownloader.DownloadClass;
import mahiti.org.oelp.fileandvideodownloader.DownloadUtility;
import mahiti.org.oelp.fileandvideodownloader.FileModel;
import mahiti.org.oelp.fileandvideodownloader.OnMediaDownloadListener;
import mahiti.org.oelp.interfaces.ListRefresh;
import mahiti.org.oelp.interfaces.SharedMediaClickListener;
import mahiti.org.oelp.models.SharedMediaModel;
import mahiti.org.oelp.services.CallAPIServicesData;
import mahiti.org.oelp.services.RetrofitConstant;
import mahiti.org.oelp.utils.AppUtils;
import mahiti.org.oelp.utils.CheckNetwork;
import mahiti.org.oelp.utils.Constants;
import mahiti.org.oelp.utils.Logger;
import mahiti.org.oelp.utils.MySharedPref;
import mahiti.org.oelp.viewmodel.ChatAndContributionViewModel;
import mahiti.org.oelp.views.fragments.ContributionsFragment;
import mahiti.org.oelp.views.fragments.MembersFragment;

public class ChatAndContributionActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, SharedMediaClickListener, OnMediaDownloadListener {


    private Toolbar toolbar;

    private FragmentManager fragManager;
    private ListRefresh refresh;

    ConnectionUtils connectionUtils;
    private MySharedPref sharedPref;
    private int userType;
    private String groupUUID;
    private String groupName;
    private String usertype;
    private TextView tvTitle;
    private TextView tvMember;
    private TextView tvContribution;
    private LinearLayout rlMembers;
    private LinearLayout rlContributions;
    private View lineMembers;
    private View lineContri;
    ChatAndContributionViewModel viewModel;
    ActivityChatAndContributionBinding binding;
    private ProgressBar progressBar;
    private String TAG = ChatAndContributionActivity.class.getSimpleName();
    private CallAPIServicesData servicesData;
    private AlertDialog alertDialogPop;
    private boolean membersClick = true;
    //    private ViewPager viewPager;
//    private TabLayout tabLayout;
    private ViewPagerAdapter adapter;
    private XMPPConnection connection = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat_and_contribution);
        viewModel = ViewModelProviders.of(this).get(ChatAndContributionViewModel.class);
        binding.setChatAndContributionViewModel(viewModel);
        binding.setLifecycleOwner(this);
        connectionUtils = new ConnectionUtils();

        tvTitle = binding.tvTitle;
        tvMember = binding.tvMember;
        tvContribution = binding.tvContribution;
        rlMembers = binding.rlMembers;
        lineMembers = binding.lineMembers;
        lineContri = binding.lineContri;
        rlContributions = binding.rlContributions;
        progressBar = binding.progressBar;


        sharedPref = new MySharedPref(this);
        userType = sharedPref.readInt(Constants.USER_TYPE, Constants.USER_TEACHER);
        toolbar = findViewById(R.id.white_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }
        if (sharedPref.readInt(Constants.USER_TYPE, Constants.USER_TEACHER) == Constants.USER_TEACHER) {
            binding.floatingActionButton.setVisibility(View.VISIBLE);
        }

        getIntentValues();
        initViews();
        setFragment(new MembersFragment());
        setTextColor(membersClick, 0);

        rlMembers.setOnClickListener(view -> {
            if (!membersClick) {
                setFragment(new MembersFragment());
                setTextColor(!membersClick, 1);
                membersClick = true;
            }

        });
        rlContributions.setOnClickListener(view -> {
            if (membersClick) {
                setFragment(new ContributionsFragment());
                setTextColor(!membersClick, 1);
                membersClick = false;
                binding.floatingActionButton.setVisibility(View.GONE);
            }
        });


        viewModel.getListOfImageToDownload().observe(this, imageListToDownload -> {
            if (imageListToDownload != null && !imageListToDownload.isEmpty()) {
                new DownloadClass(Constants.IMAGE, this, RetrofitConstant.BASE_URL, AppUtils.completePathInSDCard(Constants.IMAGE).getAbsolutePath(), imageListToDownload, "");
            }
        });

        binding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckNetwork.checkNet(ChatAndContributionActivity.this)) {
                    if (XMPP.getInstance().isReconnectNeeded()) {
                        if (joinGroup(groupUUID, sharedPref.readString(Constants.USER_NAME, ""))) {
                            Intent i = new Intent(ChatAndContributionActivity.this, ConversationActivity.class);
                            i.putExtra("group_name", groupName);
                            i.putExtra("group_uuid", groupUUID);
                            i.putExtra("username", sharedPref.readString(Constants.USER_NAME, ""));
//                            i.putExtra("group", groupUUID);
//                            i.putExtra("username", sharedPref.readString(Constants.USER_NAME, ""));
                            startActivity(i);
                        }
                    } else {
                        if (XMPP.getInstance().reconnectAndLogin(sharedPref.readString(mahiti.org.oelp.utils.Constants.USER_ID, "")))
                            if (joinGroup(groupUUID, sharedPref.readString(Constants.USER_NAME, ""))) {
                                Intent i = new Intent(ChatAndContributionActivity.this, ConversationActivity.class);
                                i.putExtra("group_name", groupName);
                                i.putExtra("group_uuid", groupUUID);
                                i.putExtra("username", sharedPref.readString(Constants.USER_NAME, ""));
                                startActivity(i);
                            }
                    }
                } else {
                    Toast.makeText(ChatAndContributionActivity.this, getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void setTextColor(boolean membersClicks, int type) {
        if (membersClicks) {
            if (type != 0) {
                lineMembers.setVisibility(View.VISIBLE);
                lineContri.setVisibility(View.INVISIBLE);

            }
            tvMember.setTextColor(getResources().getColor(R.color.black));
            tvContribution.setTextColor(getResources().getColor(R.color.grey));
        } else {
            if (type != 0) {
                lineContri.setVisibility(View.VISIBLE);
                lineMembers.setVisibility(View.INVISIBLE);
            }

            tvMember.setTextColor(getResources().getColor(R.color.grey));
            tvContribution.setTextColor(getResources().getColor(R.color.black));
        }
    }


    private void getIntentValues() {
        groupUUID = getIntent().getStringExtra("groupUUID");
        groupName = getIntent().getStringExtra("groupName");
        tvTitle.setText(groupName);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            /*case R.id.groupInfo:
                moveToGroupActivity();
                return true;*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onBackPressed() {
        ChatAndContributionActivity.this.finish();
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
    }

    private void initViews() {
//        viewPager = findViewById(R.id.viewpager);
//        setupViewPager(viewPager);

//        tabLayout = findViewById(R.id.tabs);
//        tabLayout.setupWithViewPager(viewPager);

        /*if (sharedPref.readBoolean(RetrofitConstant.FETCH_MEDIA_SHARED, false)) {
            progressBar.setVisibility(View.VISIBLE);
        }*/

//        viewPager.setOnPageChangeListener(this);

        /*viewModel.insertLong.observe(this, aLong -> {
            if (aLong != null) {
                progressBar.setVisibility(View.GONE);
            }
        });*/

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 103 && resultCode == RESULT_OK) {
            Intent intent = new Intent();
            intent.putExtra("result", true);
            setResult(RESULT_OK, intent);
            sharedPref.writeBoolean(Constants.IS_UPDATED, true);
            onBackPressed();


        }
    }

    public void setTitle(String groupName) {
        if (groupName != null && !groupName.isEmpty())
            tvTitle.setText(groupName);
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        Bundle bundle = new Bundle();
        bundle.putString("groupUUID", groupUUID);

        ContributionsFragment contributionsFragment;
        MembersFragment membersFragment;

        membersFragment = new MembersFragment();
        membersFragment.setArguments(bundle);
        contributionsFragment = new ContributionsFragment();
        contributionsFragment.setArguments(bundle);

        adapter.addFragment(membersFragment, getString(R.string.members));
        adapter.addFragment(contributionsFragment, getString(R.string.contributions));

        viewPager.setAdapter(adapter);
    }

    @Override
    public void onSharedMediaClick(SharedMediaModel mediaModel, boolean shareGlobally, int position) {
        if (!shareGlobally) {
            if (mediaModel.getMediaType().equalsIgnoreCase(String.valueOf(Constants.VIDEO))) {
                checkVideoAndDownload(new FileModel(mediaModel.getMediaTitle(), mediaModel.getMediaFile(), mediaModel.getMediaUuid(), 0));
            } else if (mediaModel.getMediaType().equalsIgnoreCase(String.valueOf(Constants.IMAGE))) {
                shoWImagePopUp(mediaModel);
            }
        } else {
            if (sharedPref.readInt(Constants.USER_TYPE, Constants.USER_TEACHER) == Constants.USER_TEACHER) {
                showPopupForDelete(mediaModel, position);
            } else {
                showPopForDeleteAndGlobalShare(mediaModel, position);
            }
        }

    }

    private void shoWImagePopUp(SharedMediaModel mediaModel) {
        if (mediaModel == null)
            return;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.imageview_popup, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(true);
        String fileName = "";

        TextView tvMediaName = dialogView.findViewById(R.id.tvMediaName);
        TextView tvSharedBy = dialogView.findViewById(R.id.tvSharedBy);
        TextView tvSharedOn = dialogView.findViewById(R.id.tvSharedOn);
        LinearLayout rlSharedBy = dialogView.findViewById(R.id.rlSharedBy);
        LinearLayout rlSharedOn = dialogView.findViewById(R.id.rlSharedOn);
        Button btnClose = dialogView.findViewById(R.id.btnClose);
        RoundedImageView ivSharedContent = dialogView.findViewById(R.id.ivSharedContent);

        if (!mediaModel.getMediaTitle().isEmpty()) {
            tvMediaName.setVisibility(View.VISIBLE);
            tvMediaName.setText(mediaModel.getMediaTitle());
        }

        if (!mediaModel.getUserName().isEmpty()) {
            rlSharedBy.setVisibility(View.VISIBLE);
            tvSharedBy.setText(mediaModel.getUserName());
        }

        if (!mediaModel.getSubmissionTime().isEmpty()) {
            rlSharedOn.setVisibility(View.VISIBLE);
            tvSharedOn.setText(mediaModel.getSubmissionTime());
        }


        btnClose.setOnClickListener(view -> {
            alertDialogPop.dismiss();
        });

        if (mediaModel.getMediaFile() != null && !mediaModel.getMediaFile().isEmpty()) {
            String fileName1 = AppUtils.getFileName(mediaModel.getMediaFile());
            File file = null;
            try {
                file = new File(AppUtils.completePathInSDCard(Constants.IMAGE), fileName1);
            } catch (Exception ex) {
                Logger.logE("Exce", ex.getMessage(), ex);
            }
            if (file.exists()) {
                Glide.with(this)
                        .load("file://" + file)
                        .into(ivSharedContent);
            }
        }


        alertDialogPop = dialogBuilder.create();
        alertDialogPop.show();
    }


    public void showPopForDeleteAndGlobalShare(SharedMediaModel model, int position) {
        new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setTitle(R.string.what_you_want_to_do_with_media)
                .setItems(R.array.option_array, (dialog, which) -> {
                    if (which == 0)
                        deleteData(model, position);
                    else
                        shareDataGlobally(model, position);
                    dialog.dismiss();

                })
                .show();
    }

    public ListRefresh getFragmentRefreshListener() {
        return refresh;
    }

    public void setFragmentRefreshListener(ListRefresh fragmentRefreshListener) {
        this.refresh = fragmentRefreshListener;
    }

    private void deleteData(SharedMediaModel model, int position) {
        MediaContentDao mediaContentDao = new MediaContentDao(this);
        long deletelong = mediaContentDao.deleteMediaUUID(model.getMediaUuid());
        sharedPref.writeBoolean(Constants.DELETEDATACHANGE, true);
        if (deletelong != -1)
            if (getFragmentRefreshListener() != null)
                getFragmentRefreshListener().onRefresh(position, true);
    }

    private void showPopupForDelete(SharedMediaModel mediaModel, int position) {

        if (sharedPref.readInt(Constants.USER_TYPE, Constants.USER_TEACHER) == Constants.USER_TEACHER)
            if (!sharedPref.readString(Constants.USER_ID, "").equals(mediaModel.getUserUuid()))
                return;

        new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setTitle("Delete Media")
                .setMessage("Delete " + mediaModel.getMediaTitle())
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteData(mediaModel, position);
                    dialog.dismiss();
                })

                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();

    }

    public void shareDataGlobally(SharedMediaModel model, int position) {
        MediaContentDao mediaContentDao = new MediaContentDao(this);
        long sharelong = mediaContentDao.updateGloballyShareMediaUUID(model.getMediaUuid());
        sharedPref.writeBoolean(Constants.GLOBALSHARECHANGE, true);
        if (sharelong != -1)
            if (getFragmentRefreshListener() != null)
                getFragmentRefreshListener().onRefresh(position, false);
    }

    private void checkVideoAndDownload(FileModel fileModel) {

        String userUUId = new MySharedPref(this).readString(Constants.USER_ID, "");
        if (videoAvailable(fileModel)) {
            String filePath = AppUtils.completePathInSDCard(Constants.VIDEO) + "/" + AppUtils.getFileName(fileModel.getFileUrl());
            DownloadUtility.playVideo(this, filePath, fileModel.getFileName(), userUUId, fileModel.getUuid(), "", fileModel.getDcfId(), "");
        } else {
            downloadVideo(fileModel);
        }
    }

    private void downloadVideo(FileModel fileModel) {
        if (CheckNetwork.checkNet(this)) {
            List<FileModel> fileModelList = new ArrayList<>();
            fileModelList.add(fileModel);
            new DownloadClass(Constants.VIDEO, this, RetrofitConstant.BASE_URL, AppUtils.completePathInSDCard(Constants.VIDEO).getAbsolutePath(), fileModelList, "");
        } else {
            Toast.makeText(this, getString(R.string.check_internet), Toast.LENGTH_SHORT).show();

        }
    }

    private boolean videoAvailable(FileModel fileModel) {
        try {
            File videoFile = new File(AppUtils.completePathInSDCard(Constants.VIDEO), AppUtils.getFileName(fileModel.getFileUrl()));
            if (videoFile.exists())
                return true;
        } catch (Exception ex) {
            Logger.logE(TAG, ex.getMessage(), ex);
        }
        return false;
    }

    @Override
    public void onMediaDownload(int type, String savedPath, String name, int position, String uuid, int dcfId, String unitUUID) {
        if (type == Constants.VIDEO) {
            if (savedPath != null && !savedPath.isEmpty()) {
                String userUUID = new MySharedPref(this).readString(Constants.USER_ID, "");
                DownloadUtility.playVideo(this, savedPath, name, userUUID, uuid, "", dcfId, unitUUID);
            }
        }/*else {
            setupViewPager(viewPager);
        }*/

    }

    public void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_content, fragment).addToBackStack(null).commitAllowingStateLoss();
    }

    public void setGroupName(String groupName) {
        if (groupName != null && !groupName.isEmpty())
            tvTitle.setText(groupName);
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {

    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    //
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.group_menu, menu);
//        return true;
//    }
    public boolean joinGroup(String roomName, String nickName) {
        if (connection == null)
            connection = connectionUtils.getXmptcConnection();
        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
        String str = roomName.replace(" ", "");
        try {
            // Create the XMPP address (JID) of the MUC.
            EntityBareJid mucJid = JidCreate.entityBareFrom(str + "@conference." + mahiti.org.oelp.chat.Constants.HOST);
            // Create a MultiUserChat using an XMPPConnection for a room
            MultiUserChat muc = manager.getMultiUserChat(mucJid);
            Log.d(TAG, "username" + sharedPref.readString(mahiti.org.oelp.utils.Constants.USER_NAME, ""));
            Resourcepart nickname = Resourcepart.from(sharedPref.readString(mahiti.org.oelp.utils.Constants.USER_NAME, ""));

            DiscussionHistory history = new DiscussionHistory();
            history.setMaxStanzas(Integer.MAX_VALUE);

            muc.join(nickname, "", history, SmackConfiguration.getDefaultPacketReplyTimeout());
            Toast.makeText(ChatAndContributionActivity.this, "joined to group", Toast.LENGTH_SHORT).show();
            return true;
        } catch (SmackException.NotConnectedException e1) {
            e1.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        } catch (MultiUserChatException.NotAMucServiceException e) {
            e.printStackTrace();
        }
        return false;
    }

}