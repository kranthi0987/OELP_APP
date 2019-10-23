package mahiti.org.oelp.views.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatException;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.xdata.Form;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.jid.util.JidUtil;
import org.jxmpp.stringprep.XmppStringprepException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import mahiti.org.oelp.MyApplication;
import mahiti.org.oelp.R;
import mahiti.org.oelp.chat.Constants;
import mahiti.org.oelp.chat.ConversationActivity;
import mahiti.org.oelp.chat.service.XMPP;
import mahiti.org.oelp.chat.utilies.ConnectionUtils;
import mahiti.org.oelp.databinding.AdapterGroupViewBinding;
import mahiti.org.oelp.interfaces.ItemClickListerner;
import mahiti.org.oelp.models.GroupModel;
import mahiti.org.oelp.utils.CheckNetwork;
import mahiti.org.oelp.utils.MySharedPref;
import mahiti.org.oelp.viewmodel.HomeViewModel;

/**
 * Created by RAJ ARYAN on 21/08/19.
 */
public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.LayoutView> {
    AdapterGroupViewBinding binding;
    private List<GroupModel> modelList = new ArrayList<>();
    private Context mContext;
    private ItemClickListerner listener;
    String TAG = GroupAdapter.class.getSimpleName();
    XMPPTCPConnection connection = null;
    MySharedPref sharedPref;
    private ConnectionUtils connectionUtils;

    public GroupAdapter(Context mContext) {
        this.mContext = mContext;
        connectionUtils = new ConnectionUtils();
        sharedPref = new MySharedPref(mContext);
    }

    @NonNull
    @Override
    public LayoutView onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.adapter_group_view, viewGroup, false);
        return new LayoutView(binding, viewGroup.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull LayoutView layout, int i) {
        HomeViewModel viewModel = ViewModelProviders.of((FragmentActivity) layout.getContext()).get(HomeViewModel.class);
        layout.setViewModel(viewModel);
        GroupModel groupModel = modelList.get(i);
        setValues(groupModel, layout, i);
        layout.binding.llRecyclerView.setOnClickListener(v -> listener.onItemClick(groupModel));
        layout.binding.ivChat.setOnClickListener(v -> checkInternetAndzproceed(groupModel.getGroupUUID(), groupModel.getGroupName()));
    }

    private void checkInternetAndzproceed(String groupUuid, String groupName) {
        if (CheckNetwork.checkNet(mContext)) {
            if (XMPP.getInstance().isReconnectNeeded()) {
                if (join(groupUuid, groupName))
                    movetoChatConerstation(groupName, groupUuid);
            } else {
                if (XMPP.getInstance().reconnectAndLogin(sharedPref.readString(mahiti.org.oelp.utils.Constants.USER_ID, "")))
                    if (join(groupUuid, groupName))
                        movetoChatConerstation(groupName, groupUuid);
            }
        } else {
            Toast.makeText(mContext, mContext.getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public int getItemCount() {
        return modelList == null ? 0 : modelList.size();
    }

    public class LayoutView extends RecyclerView.ViewHolder {
        private Context mContext;
        private HomeViewModel homeViewModel;
        private AdapterGroupViewBinding binding;

        public LayoutView(AdapterGroupViewBinding binding, Context context) {
            super(binding.getRoot());
            mContext = context;
            this.binding = binding;
            mContext = context;
        }

        public Context getContext() {
            return mContext;
        }

        public void setViewModel(HomeViewModel viewModel) {
            this.homeViewModel = viewModel;
            binding.setHomeViewModel(viewModel);
        }
    }

    public void setList(List<GroupModel> list, Context context) {
        if (modelList != null)
            this.modelList.clear();
        modelList.addAll(list);
        listener = (ItemClickListerner) context;
        mContext = context;
        this.notifyDataSetChanged();
    }

    private void setValues(GroupModel catalogueDetailsModel, LayoutView layout, int position) {
        layout.binding.tvGroupName.setText(catalogueDetailsModel.getGroupName());
        layout.binding.tvMemberCount.setText("" + catalogueDetailsModel.getMembersCount());
//        layout.binding.tvMemberCount.setText("3");
//        layout.binding.tvMessageCount.setText(String.valueOf(4));
    }

    public boolean CreateMuc(String roomName, String groupName) {
        sharedPref = new MySharedPref(MyApplication.getContext());
        if (connection == null)
            connection = connectionUtils.getXmptcConnection();
        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
        String str = roomName.replace(" ", "");
        try {
            EntityBareJid mucJid = JidCreate.entityBareFrom(str + "@conference." + Constants.HOST);
            MultiUserChat muc = manager.getMultiUserChat(mucJid);
//            userutils.setUserNickName("admin2");
//            String nicknames = userutils.getUserNickname();
            Resourcepart nickname = Resourcepart.from(sharedPref.readString(mahiti.org.oelp.utils.Constants.USER_NAME, ""));
            DiscussionHistory history = new DiscussionHistory();
            history.setMaxStanzas(Integer.MAX_VALUE);
            // Jid jid = JidCreate.bareFrom("testq@" + Constants.HOST);
            setConfig(muc);
            muc.createOrJoin(nickname);
            //muc.join(nickname, "", history, SmackConfiguration.getDefaultPacketReplyTimeout());
            Log.d(TAG, "CreateOrJoin: " + history);
            // muc.banUser(jid, "banned the user");
            muc.sendMessage("HI " + nickname + " joined to this group " + groupName);
            return true;
        } catch (SmackException.NotConnectedException e1) {
            e1.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (MultiUserChatException.NotAMucServiceException e) {
            e.printStackTrace();
        } catch (MultiUserChatException.MucAlreadyJoinedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void movetoChatConerstation(String groupName, String groupUuid) {
        Intent i = new Intent(mContext, ConversationActivity.class);
        i.putExtra("group_name", groupName);
        i.putExtra("group_uuid", groupUuid);
        i.putExtra("username", "");
        mContext.startActivity(i);
    }

    public boolean join(String roomName, String groupName) {
        sharedPref = new MySharedPref(MyApplication.getContext());
        if (connection == null)
            connection = connectionUtils.getXmptcConnection();
        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
        String str = roomName.replace(" ", "");
        try {
            EntityBareJid mucJid = JidCreate.entityBareFrom(str + "@conference." + Constants.HOST);
            MultiUserChat muc = manager.getMultiUserChat(mucJid);
//            userutils.setUserNickName("admin2");
//            String nicknames = userutils.getUserNickname();
            Resourcepart nickname = Resourcepart.from(sharedPref.readString(mahiti.org.oelp.utils.Constants.USER_NAME, ""));
            DiscussionHistory history = new DiscussionHistory();
            history.setMaxStanzas(Integer.MAX_VALUE);
            // Jid jid = JidCreate.bareFrom("testq@" + Constants.HOST);
            setConfig(muc);
//            muc.join(nickname);
            muc.join(nickname, "", history, SmackConfiguration.getDefaultPacketReplyTimeout());
            Log.d(TAG, "Join: " + history);
            // muc.banUser(jid, "banned the user");
//            muc.sendMessage("HI " + nickname + " joined to this group " + groupName);
            return true;
        } catch (SmackException.NotConnectedException e1) {
            e1.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (MultiUserChatException.NotAMucServiceException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void setConfig(MultiUserChat multiUserChat) {

        try {
            Form form = multiUserChat.getConfigurationForm();
            Form submitForm = form.createAnswerForm();
            submitForm.setTitle(multiUserChat.getReservedNickname());
            Set<Jid> owners = JidUtil.jidSetFrom(new String[]{sharedPref.readString(mahiti.org.oelp.utils.Constants.USER_ID, "") + "@206.189.136.186"});
            submitForm.setAnswer("muc#roomconfig_roomowners", owners.toString());
            submitForm.setAnswer("muc#roomconfig_publicroom", true);
            submitForm.setAnswer("muc#roomconfig_persistentroom", true);
            multiUserChat.sendConfigurationForm(submitForm);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
