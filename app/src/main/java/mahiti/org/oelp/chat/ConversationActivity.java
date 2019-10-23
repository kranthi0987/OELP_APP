/*
 * Copyright (c) 2019.
 * Project created and maintained by sanjay kranthi kumar
 * if need to contribute contact us on
 * kranthi0987@gmail.com
 */

package mahiti.org.oelp.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import co.intentservice.chatui.ChatView;
import co.intentservice.chatui.models.ChatMessage;
import mahiti.org.oelp.R;
import mahiti.org.oelp.chat.database.dao.MessagesDTO;
import mahiti.org.oelp.chat.database.dao.MessagesDao;
import mahiti.org.oelp.chat.exceptions.OelpException;
import mahiti.org.oelp.chat.service.XMPP;
import mahiti.org.oelp.chat.utilies.ConnectionUtils;
import mahiti.org.oelp.chat.utilies.DateandTimeUtils;
import mahiti.org.oelp.utils.MySharedPref;

public class ConversationActivity extends AppCompatActivity {
    XMPPTCPConnection connection = null;
    ConnectionUtils connectionUtils = new ConnectionUtils();
    String roomName = "";
    MySharedPref mySharedPref;
    DateandTimeUtils dateandTimeUtils;
    TextView tvTitle;
    private String contactJid;
    private ChatView mChatView;
    private BroadcastReceiver mBroadcastReceiver;
    private String TAG = ConversationActivity.class.getSimpleName();
    private String groupUuid = null;
    private String username = null;
    private String groupName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        connection = connectionUtils.getXmptcConnection();
        dateandTimeUtils = new DateandTimeUtils();
        mySharedPref = new MySharedPref(this);
        tvTitle = findViewById(R.id.tvTitle);
        Intent intent = getIntent();
        if (intent != null) {
            groupName = intent.getStringExtra("group_name");
            groupUuid = intent.getStringExtra("group_uuid");
            username = intent.getStringExtra("username");
        }
        roomName = groupUuid + "@conference." + Constants.HOST;
        mChatView = findViewById(R.id.rooster_chat_view);
        mChatView.setOnSentMessageListener(new ChatView.OnSentMessageListener() {
            @Override
            public boolean sendMessage(ChatMessage chatMessage) {
                // perform actual message sending
                if (XMPP.getInstance().isConnected()) {
                    Log.d(TAG, "The client is connected to the server,Sending Message");
                    //Send the message to the server
                    Intent intent = new Intent(Constants.SEND_MESSAGE);
                    intent.putExtra(Constants.BUNDLE_MESSAGE_BODY, mChatView.getTypedMessage());
                    intent.putExtra(Constants.BUNDLE_TO, contactJid);

                    sendBroadcast(intent);
                    sendMessagetochat(chatMessage.getMessage(), roomName);

                } else {
                    Toast.makeText(getApplicationContext(),
                            "Client not connected to server ,Message not sent!", Toast.LENGTH_LONG).show();
                }
                //message sending ends here
                return true;
            }
        });
        mChatView.setTypingListener(new ChatView.TypingListener() {
            @Override
            public void userStartedTyping() {
                Log.d(TAG, "userStartedTyping: ");
            }

            @Override
            public void userStoppedTyping() {
                Log.d(TAG, "userStoppedTyping: ");
            }
        });
//        Intent intent1 = getIntent();
//        contactJid = username;
//        setTitle(contactJid);
        tvTitle.setText(groupName);
        getRetrivedMessages();
    }

    private void sendMessagetochat(String body, String roomName) {
        Log.d(TAG, "Sending message to :" + roomName);
        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);

        Resourcepart nickname = null;
        try {
            EntityBareJid mucJid = JidCreate.entityBareFrom(roomName);
            MultiUserChat muc = manager.getMultiUserChat(mucJid);
            nickname = Resourcepart.from(mySharedPref.readString(mahiti.org.oelp.utils.Constants.USER_NAME, ""));
            DiscussionHistory history = new DiscussionHistory();
            history.setMaxStanzas(Integer.MAX_VALUE);
//            muc.join(nickname);
            muc.sendMessage(body);
        } catch (SmackException.NotConnectedException | XmppStringprepException | InterruptedException e1) {
            e1.printStackTrace();
        }
        MessagesDao messagesDao = new MessagesDao();
        try {
            messagesDao.insertMessages(UUID.randomUUID().toString(),
                    body,
                    String.valueOf(nickname),
                    roomName,
                    dateandTimeUtils.currentDateTime(), "SENT",
                    "0", groupName, roomName);
        } catch (OelpException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        connection = connectionUtils.getXmptcConnection();

        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);

        try {
            EntityBareJid mucJid = JidCreate.entityBareFrom(roomName);
            MultiUserChat muc = manager.getMultiUserChat(mucJid);
            muc.addMessageListener(new MessageListener() {
                @Override
                public void processMessage(Message message) {
                    Log.d(TAG, "message.getBody() :" + message.getBody());
                    Log.d(TAG, "message.getFrom() :" + message.getFrom());

                    String from = message.getFrom().toString();

                    String contactJid = "";
                    if (from.contains("/")) {
                        contactJid = from.split("/")[0];
                        Log.d(TAG, "The real jid is :" + contactJid);
                        Log.d(TAG, "The message is from :" + from);
                    } else {
                        contactJid = from;
                    }
                    //Bundle up the intent and send the broadcast.
                    Intent intent = new Intent(Constants.NEW_MESSAGE);
                    intent.setPackage(getApplication().getPackageName());
                    intent.putExtra(Constants.BUNDLE_FROM_JID, contactJid);
                    intent.putExtra(Constants.BUNDLE_MESSAGE_BODY, message.getBody());
                    getApplication().sendBroadcast(intent);
                    Log.d(TAG, "Received message from :" + contactJid + " broadcast sent.");

                    ChatMessage chatMessage = new ChatMessage(message.getBody(), System.currentTimeMillis(), ChatMessage.Type.RECEIVED, contactJid);
                    mChatView.addMessage(chatMessage);

                    MessagesDao messagesDao = new MessagesDao();

                    try {
                        messagesDao.insertMessages(UUID.randomUUID().toString(),
                                message.getBody(),
                                message.getFrom().toString(),
                                roomName,
                                dateandTimeUtils.currentDateTime(), "RECEIVED",
                                "0", groupName, roomName);
                    } catch (OelpException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (XmppStringprepException e1) {
            e1.printStackTrace();
        }


        ReconnectionManager reconnectionManager = null;
        reconnectionManager = ReconnectionManager.getInstanceFor(connection);
        ReconnectionManager.setEnabledPerDefault(true);
        reconnectionManager.enableAutomaticReconnection();

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                switch (action) {
                    case Constants.NEW_MESSAGE:
                        String from = intent.getStringExtra(Constants.BUNDLE_FROM_JID);
                        String body = intent.getStringExtra(Constants.BUNDLE_MESSAGE_BODY);
                        if (from.equals(contactJid)) {
                            ChatMessage chatMessage = new ChatMessage(body, System.currentTimeMillis(), ChatMessage.Type.RECEIVED, from);
                            mChatView.addMessage(chatMessage);
                        } else {
                            Log.d(TAG, "Got a message from jid :" + from);
                        }
                        return;
                }
            }
        };
        IntentFilter filter = new IntentFilter(Constants.NEW_MESSAGE);
        registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mBroadcastReceiver);
    }

    public void getRetrivedMessages() {
        MessagesDao messagesDao = new MessagesDao();
//        List<MessagesDTO> messagesRecievedDTOList = new ArrayList<>();
//        List<MessagesDTO> messagesSendDTOList = new ArrayList<>();
        List<MessagesDTO> messagesAllDTOList = new ArrayList<>();
        try {
//            messagesRecievedDTOList = messagesDao.getRecievedMessages(roomName);
//            messagesSendDTOList = messagesDao.getSentMessages(roomName);
            messagesAllDTOList = messagesDao.getAllMessages(roomName);
        } catch (OelpException e) {
            e.printStackTrace();
        }
        if (messagesAllDTOList.size() == 0) {
            ChatMessage chatMessage = new ChatMessage("No history found", System.currentTimeMillis(), ChatMessage.Type.RECEIVED);
            mChatView.addMessage(chatMessage);
        }
        for (MessagesDTO m : messagesAllDTOList) {
            if (m.getMessage_type().equalsIgnoreCase("SENT")) {
                ChatMessage chatMessage = new ChatMessage(m.getMessage(), dateandTimeUtils.getTimeInMilliSec(m.getMessage_date()), ChatMessage.Type.SENT, m.getFrom_user());
                mChatView.addMessage(chatMessage);
            } else {
                ChatMessage chatMessage = new ChatMessage(m.getMessage(), dateandTimeUtils.getTimeInMilliSec(m.getMessage_date()), ChatMessage.Type.RECEIVED, m.getTo_user());
                mChatView.addMessage(chatMessage);
            }
        }
//        for (MessagesDTO m : messagesRecievedDTOList) {
//            ChatMessage chatMessage = new ChatMessage(m.getMessage(), dateandTimeUtils.getTimeInMilliSec(m.getMessage_date()), ChatMessage.Type.RECEIVED);
//            mChatView.addMessage(chatMessage);
//        }
//        for (MessagesDTO m : messagesSendDTOList) {
//            ChatMessage chatMessage = new ChatMessage(m.getMessage(), dateandTimeUtils.getTimeInMilliSec(m.getMessage_date()), ChatMessage.Type.SENT);
//            mChatView.addMessage(chatMessage);
//        }

    }

    public void leaveMuc() {
        Log.d(TAG, "leaving group to :" + roomName);
        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
        try {
            EntityBareJid mucJid = JidCreate.entityBareFrom(roomName);
            MultiUserChat muc = manager.getMultiUserChat(mucJid);
            muc.leave();
        } catch (SmackException.NotConnectedException e1) {
            e1.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // leaveMuc();
    }
}
