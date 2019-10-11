package mahiti.org.oelp.ui;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.view.inputmethod.InputConnectionCompat;
import androidx.core.view.inputmethod.InputContentInfoCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.java.otr4j.session.SessionStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;
import mahiti.org.oelp.R;
import mahiti.org.oelp.Config;
import mahiti.org.oelp.crypto.axolotl.AxolotlService;
import mahiti.org.oelp.crypto.axolotl.FingerprintStatus;
import mahiti.org.oelp.entities.Account;
import mahiti.org.oelp.entities.Blockable;
import mahiti.org.oelp.entities.Contact;
import mahiti.org.oelp.entities.Conversation;
import mahiti.org.oelp.entities.Conversational;
import mahiti.org.oelp.entities.DownloadableFile;
import mahiti.org.oelp.entities.Message;
import mahiti.org.oelp.entities.MucOptions;
import mahiti.org.oelp.entities.Presence;
import mahiti.org.oelp.entities.ReadByMarker;
import mahiti.org.oelp.entities.Transferable;
import mahiti.org.oelp.entities.TransferablePlaceholder;
import mahiti.org.oelp.http.HttpDownloadConnection;
import mahiti.org.oelp.persistance.FileBackend;
import mahiti.org.oelp.services.MessageArchiveService;
import mahiti.org.oelp.services.XmppConnectionService;
import mahiti.org.oelp.ui.adapter.MediaPreviewAdapter;
import mahiti.org.oelp.ui.adapter.MessageAdapter;
import mahiti.org.oelp.ui.util.ActivityResult;
import mahiti.org.oelp.ui.util.Attachment;
import mahiti.org.oelp.ui.util.ConversationMenuConfigurator;
import mahiti.org.oelp.ui.util.DateSeparator;
import mahiti.org.oelp.ui.util.EditMessageActionModeCallback;
import mahiti.org.oelp.ui.util.ListViewUtils;
import mahiti.org.oelp.ui.util.MucDetailsContextMenuHelper;
import mahiti.org.oelp.ui.util.PendingItem;
import mahiti.org.oelp.ui.util.PresenceSelector;
import mahiti.org.oelp.ui.util.ScrollState;
import mahiti.org.oelp.ui.util.SendButtonAction;
import mahiti.org.oelp.ui.util.SendButtonTool;
import mahiti.org.oelp.ui.util.ShareUtil;
import mahiti.org.oelp.ui.util.SoftKeyboardUtils;
import mahiti.org.oelp.ui.util.ViewUtil;
import mahiti.org.oelp.ui.widget.EditMessage;
import mahiti.org.oelp.ui.widget.UnreadCountCustomView;
import mahiti.org.oelp.utils.Compatibility;
import mahiti.org.oelp.utils.GeoHelper;
import mahiti.org.oelp.utils.MenuDoubleTabUtil;
import mahiti.org.oelp.utils.MessageUtils;
import mahiti.org.oelp.utils.NickValidityChecker;
import mahiti.org.oelp.utils.Patterns;
import mahiti.org.oelp.utils.PermissionUtils;
import mahiti.org.oelp.utils.QuickLoader;
import mahiti.org.oelp.utils.StylingHelper;
import mahiti.org.oelp.utils.UIHelper;
import mahiti.org.oelp.xmpp.Patches;
import mahiti.org.oelp.xmpp.XmppConnection;
import mahiti.org.oelp.xmpp.chatstate.ChatState;
import mahiti.org.oelp.xmpp.jingle.JingleConnection;
import rocks.xmpp.addr.Jid;

import static mahiti.org.oelp.ui.SettingsActivity.WARN_UNENCRYPTED_CHAT;
import static mahiti.org.oelp.ui.XmppActivity.EXTRA_ACCOUNT;
import static mahiti.org.oelp.ui.XmppActivity.REQUEST_INVITE_TO_CONVERSATION;

public class ConversationFragment extends XmppFragment implements EditMessage.KeyboardListener, MessageAdapter.OnContactPictureLongClicked, MessageAdapter.OnContactPictureClicked {

    public static final int REQUEST_SEND_MESSAGE = 0x0201;
    public static final int REQUEST_DECRYPT_PGP = 0x0202;
    public static final int REQUEST_ENCRYPT_MESSAGE = 0x0207;
    public static final int REQUEST_TRUST_KEYS_TEXT = 0x0208;
    public static final int REQUEST_TRUST_KEYS_ATTACHMENTS = 0x0209;
    public static final int REQUEST_START_DOWNLOAD = 0x0210;
    public static final int REQUEST_ADD_EDITOR_CONTENT = 0x0211;
    public static final int REQUEST_COMMIT_ATTACHMENTS = 0x0212;
    public static final int ATTACHMENT_CHOICE = 0x0300;
    public static final int ATTACHMENT_CHOICE_CHOOSE_IMAGE = 0x0301;
    public static final int ATTACHMENT_CHOICE_TAKE_PHOTO = 0x0302;
    public static final int ATTACHMENT_CHOICE_CHOOSE_FILE = 0x0303;
    public static final int ATTACHMENT_CHOICE_RECORD_VOICE = 0x0304;
    public static final int ATTACHMENT_CHOICE_LOCATION = 0x0305;
    public static final int ATTACHMENT_CHOICE_CHOOSE_VIDEO = 0x0306;
    public static final int ATTACHMENT_CHOICE_RECORD_VIDEO = 0x0307;
    public static final int ATTACHMENT_CHOICE_INVALID = 0x0399;

    public static final String RECENTLY_USED_QUICK_ACTION = "recently_used_quick_action";
    public static final String STATE_CONVERSATION_UUID = ConversationFragment.class.getName() + ".uuid";
    public static final String STATE_SCROLL_POSITION = ConversationFragment.class.getName() + ".scroll_position";
    public static final String STATE_PHOTO_URI = ConversationFragment.class.getName() + ".media_previews";
    public static final String STATE_MEDIA_PREVIEWS = ConversationFragment.class.getName() + ".take_photo_uri";

    private static final String STATE_LAST_MESSAGE_UUID = "state_last_message_uuid";

    private final List<Message> messageList = new ArrayList<>();
    private final PendingItem<ActivityResult> postponedActivityResult = new PendingItem<>();
    private final PendingItem<String> pendingConversationsUuid = new PendingItem<>();
    private final PendingItem<ArrayList<Attachment>> pendingMediaPreviews = new PendingItem<>();
    private final PendingItem<Bundle> pendingExtras = new PendingItem<>();
    private final PendingItem<Uri> pendingTakePhotoUri = new PendingItem<>();
    private final PendingItem<Uri> pendingTakeVideoUri = new PendingItem<>();
    private final PendingItem<ScrollState> pendingScrollState = new PendingItem<>();
    private final PendingItem<String> pendingLastMessageUuid = new PendingItem<>();
    private final PendingItem<Message> pendingMessage = new PendingItem<>();
    public Uri mPendingEditorContent = null;
    public ViewDataBinding binding;
    protected MessageAdapter messageListAdapter;
    private String lastMessageUuid = null;
    private Conversation conversation;
    private Toast messageLoaderToast;
    private ConversationsActivity activity;
    private Menu mOptionsMenu;
    protected OnClickListener clickToVerify = new OnClickListener() {
        @Override
        public void onClick(View v) {
            activity.verifyOtrSessionDialog(conversation, v);
        }
    };

    private boolean reInitRequiredOnStart = true;
    private MediaPreviewAdapter mediaPreviewAdapter;
    private OnClickListener clickToMuc = new OnClickListener() {

        @Override
        public void onClick(View v) {
//            Intent intent = new Intent(getActivity(), ConferenceDetailsActivity.class);
//            intent.setAction(ConferenceDetailsActivity.ACTION_VIEW_MUC);
//            intent.putExtra("uuid", conversation.getUuid());
//            startActivity(intent);
//            activity.overridePendingTransition(R.animator.fade_in, R.animator.fade_out);
        }
    };

    private OnClickListener leaveMuc = new OnClickListener() {

        @Override
        public void onClick(View v) {
            activity.xmppConnectionService.archiveConversation(conversation);
        }
    };

    private OnClickListener joinMuc = new OnClickListener() {

        @Override
        public void onClick(View v) {
            activity.xmppConnectionService.joinMuc(conversation);
        }
    };

    private OnClickListener acceptJoin = new OnClickListener() {
        @Override
        public void onClick(View v) {
            conversation.setAttribute("accept_non_anonymous", true);
            activity.xmppConnectionService.updateConversation(conversation);
            activity.xmppConnectionService.joinMuc(conversation);
        }
    };

    private OnClickListener enterPassword = new OnClickListener() {

        @Override
        public void onClick(View v) {
            MucOptions muc = conversation.getMucOptions();
            String password = muc.getPassword();
            if (password == null) {
                password = "";
            }
            activity.quickPasswordEdit(password, value -> {
                activity.xmppConnectionService.providePasswordForMuc(conversation, value);
                return null;
            });
        }
    };

    private OnScrollListener mOnScrollListener = new OnScrollListener() {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (AbsListView.OnScrollListener.SCROLL_STATE_IDLE == scrollState) {
                fireReadEvent();
            }
        }

        @Override
        public void onScroll(final AbsListView view, int firstVisibleItem, int visibleItemCount, final int totalItemCount) {
            toggleScrollDownButton(view);
            synchronized (ConversationFragment.this.messageList) {
                if (firstVisibleItem < 5 && conversation != null && conversation.messagesLoaded.compareAndSet(true, false) && messageList.size() > 0) {
                    long timestamp;
                    if (messageList.get(0).getType() == Message.TYPE_STATUS && messageList.size() >= 2) {
                        timestamp = messageList.get(1).getTimeSent();
                    } else {
                        timestamp = messageList.get(0).getTimeSent();
                    }
                    activity.xmppConnectionService.loadMoreMessages(conversation, timestamp, new XmppConnectionService.OnMoreMessagesLoaded() {
                        @Override
                        public void onMoreMessagesLoaded(final int c, final Conversation conversation) {
                            if (ConversationFragment.this.conversation != conversation) {
                                conversation.messagesLoaded.set(true);
                                return;
                            }
                            runOnUiThread(() -> {
                                synchronized (messageList) {
                                    final int oldPosition = messagesView.getFirstVisiblePosition();
                                    Message message = null;
                                    int childPos;
                                    for (childPos = 0; childPos + oldPosition < messageList.size(); ++childPos) {
                                        message = messageList.get(oldPosition + childPos);
                                        if (message.getType() != Message.TYPE_STATUS) {
                                            break;
                                        }
                                    }
                                    final String uuid = message != null ? message.getUuid() : null;
                                    View v = messagesView.getChildAt(childPos);
                                    final int pxOffset = (v == null) ? 0 : v.getTop();
                                    ConversationFragment.this.conversation.populateWithMessages(ConversationFragment.this.messageList);
                                    try {
                                        updateStatusMessages();
                                    } catch (IllegalStateException e) {
                                        Log.d(mahiti.org.oelp.Config.LOGTAG, "caught illegal state exception while updating status messages");
                                    }
                                    messageListAdapter.notifyDataSetChanged();
                                    int pos = Math.max(getIndexOf(uuid, messageList), 0);
                                    messagesView.setSelectionFromTop(pos, pxOffset);
                                    if (messageLoaderToast != null) {
                                        messageLoaderToast.cancel();
                                    }
                                    conversation.messagesLoaded.set(true);
                                }
                            });
                        }

                        @Override
                        public void informUser(final int resId) {

                            runOnUiThread(() -> {
                                if (messageLoaderToast != null) {
                                    messageLoaderToast.cancel();
                                }
                                if (ConversationFragment.this.conversation != conversation) {
                                    return;
                                }
                                messageLoaderToast = Toast.makeText(view.getContext(), resId, Toast.LENGTH_LONG);
                                messageLoaderToast.show();
                            });
                        }
                    });
                }
            }
        }
    };

    private EditMessage.OnCommitContentListener mEditorContentListener = new EditMessage.OnCommitContentListener() {
        @Override
        public boolean onCommitContent(InputContentInfoCompat inputContentInfo, int flags, Bundle opts, String[] contentMimeTypes) {
            // try to get permission to read the image, if applicable
            if ((flags & InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION) != 0) {
                try {
                    inputContentInfo.requestPermission();
                } catch (Exception e) {
                    Log.e(mahiti.org.oelp.Config.LOGTAG, "InputContentInfoCompat#requestPermission() failed.", e);
                    Toast.makeText(getActivity(), activity.getString(R.string.no_permission_to_access_x, inputContentInfo.getDescription()), Toast.LENGTH_LONG
                    ).show();
                    return false;
                }
            }
            if (hasPermissions(REQUEST_ADD_EDITOR_CONTENT, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                attachEditorContentToConversation(inputContentInfo.getContentUri());
            } else {
                mPendingEditorContent = inputContentInfo.getContentUri();
            }
            return true;
        }
    };
    private Message selectedMessage;
    private OnClickListener mEnableAccountListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            final Account account = conversation == null ? null : conversation.getAccount();
            if (account != null) {
                account.setOption(Account.OPTION_DISABLED, false);
                activity.xmppConnectionService.updateAccount(account);
            }
        }
    };
    private OnClickListener mUnblockClickListener = new OnClickListener() {
        @Override
        public void onClick(final View v) {
            v.post(() -> v.setVisibility(View.INVISIBLE));
            if (conversation.isDomainBlocked()) {
                BlockContactDialog.show(activity, conversation);
            } else {
                unblockConversation(conversation);
            }
        }
    };
    private OnClickListener mBlockClickListener = this::showBlockSubmenu;

    private OnClickListener mAddBackClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            final Contact contact = conversation == null ? null : conversation.getContact();
            if (contact != null) {
                activity.xmppConnectionService.createContact(contact, true);
                activity.switchToContactDetails(contact);
            }
        }
    };
    private View.OnLongClickListener mLongPressBlockListener = v -> {
        showBlockSubmenu(v);
        return true;
    };

    private OnClickListener mHideUnencryptionHint = v -> enableMessageEncryption();

    private void enableMessageEncryption() {
        if (mahiti.org.oelp.Config.supportOmemo() && Conversation.suitableForOmemoByDefault(conversation) && conversation.isSingleOrPrivateAndNonAnonymous()) {
            conversation.setNextEncryption(Message.ENCRYPTION_AXOLOTL);
            activity.xmppConnectionService.updateConversation(conversation);
            activity.refreshUi();
        }
        hideSnackbar();
    }

    private OnClickListener mAllowPresenceSubscription = new OnClickListener() {
        @Override
        public void onClick(View v) {
            final Contact contact = conversation == null ? null : conversation.getContact();
            if (contact != null) {
                activity.xmppConnectionService.sendPresencePacket(contact.getAccount(),
                        activity.xmppConnectionService.getPresenceGenerator()
                                .sendPresenceUpdatesTo(contact));
                hideSnackbar();
            }
        }
    };
    private OnClickListener mAnswerSmpClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(activity, VerifyOTRActivity.class);
            intent.setAction(VerifyOTRActivity.ACTION_VERIFY_CONTACT);
            intent.putExtra(EXTRA_ACCOUNT, conversation.getAccount().getJid().asBareJid().toString());
            intent.putExtra(VerifyOTRActivity.EXTRA_ACCOUNT, conversation.getAccount().getJid().asBareJid().toString());
            intent.putExtra("mode", VerifyOTRActivity.MODE_ANSWER_QUESTION);
            startActivity(intent);
            activity.overridePendingTransition(R.animator.fade_in, R.animator.fade_out);
        }
    };

    protected OnClickListener clickToDecryptListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            PendingIntent pendingIntent = conversation.getAccount().getPgpDecryptionService().getPendingIntent();
            if (pendingIntent != null) {
                try {
                    getActivity().startIntentSenderForResult(pendingIntent.getIntentSender(),
                            REQUEST_DECRYPT_PGP,
                            null,
                            0,
                            0,
                            0);
                } catch (SendIntentException e) {
                    Toast.makeText(getActivity(), R.string.unable_to_connect_to_keychain, Toast.LENGTH_SHORT).show();
                    conversation.getAccount().getPgpDecryptionService().continueDecryption(true);
                }
            }
            updateSnackBar(conversation);
        }
    };
    private AtomicBoolean mSendingPgpMessage = new AtomicBoolean(false);
    private OnEditorActionListener mEditorActionListener = (v, actionId, event) -> {
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null && imm.isFullscreenMode()) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
            sendMessage();
            return true;
        } else {
            return false;
        }
    };

    private OnClickListener mScrollButtonListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            stopScrolling();
            setSelection(messagesView.getCount() - 1, true);
        }
    };

    private OnClickListener mRecordVoiceButtonListener = v -> attachFile(ATTACHMENT_CHOICE_RECORD_VOICE);

    private OnClickListener mSendButtonListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            Object tag = v.getTag();
            if (tag instanceof SendButtonAction) {
                SendButtonAction action = (SendButtonAction) tag;
                switch (action) {
                    case CHOOSE_ATTACHMENT:
                        choose_attachment(v);
                    case TAKE_PHOTO:
                    case RECORD_VIDEO:
                    case SEND_LOCATION:
                    case RECORD_VOICE:
                    case CHOOSE_PICTURE:
                        attachFile(action.toChoice());
                        break;
                    case CANCEL:
                        if (conversation != null) {
                            if (conversation.setCorrectingMessage(null)) {
                                textinput.setText("");
                                textinput.append(conversation.getDraftMessage());
                                conversation.setDraftMessage(null);
                            } else if (conversation.getMode() == Conversation.MODE_MULTI) {
                                conversation.setNextCounterpart(null);
                                textinput.setText("");
                            } else {
                                textinput.setText("");
                            }
                            updateSendButton();
                            updateEditablity();
                        }
                        break;
                    default:
                        sendMessage();
                }
            } else {
                sendMessage();
            }
        }
    };
    private View.OnLongClickListener mSendButtonLongListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            final String body = textinput.getText().toString();
            if (body.length() == 0) {
                textinput.getText().insert(0, Message.ME_COMMAND + " ");
            }
            return true;
        }
    };
    private int completionIndex = 0;
    private int lastCompletionLength = 0;
    private String incomplete;
    private int lastCompletionCursor;
    private boolean firstWord = false;
    private Message mPendingDownloadableMessage;

    private static ConversationFragment findConversationFragment(Activity activity) {
        Fragment fragment = activity.getFragmentManager().findFragmentById(R.id.main_fragment);
        if (fragment instanceof ConversationFragment) {
            return (ConversationFragment) fragment;
        }
        fragment = activity.getFragmentManager().findFragmentById(R.id.secondary_fragment);
        if (fragment instanceof ConversationFragment) {
            return (ConversationFragment) fragment;
        }
        return null;
    }

    public static void startStopPending(Activity activity) {
        ConversationFragment fragment = findConversationFragment(activity);
        if (fragment != null) {
            fragment.messageListAdapter.startStopPending();
        }
    }

    public static void downloadFile(Activity activity, Message message) {
        ConversationFragment fragment = findConversationFragment(activity);
        if (fragment != null) {
            fragment.startDownloadable(message);
        }
    }

    public static void registerPendingMessage(Activity activity, Message message) {
        ConversationFragment fragment = findConversationFragment(activity);
        if (fragment != null) {
            fragment.pendingMessage.push(message);
        }
    }

    public static void openPendingMessage(Activity activity) {
        ConversationFragment fragment = findConversationFragment(activity);
        if (fragment != null) {
            Message message = fragment.pendingMessage.pop();
            if (message != null) {
                fragment.messageListAdapter.openDownloadable(message);
            }
        }
    }

    public static Conversation getConversation(Activity activity) {
        return getConversation(activity, R.id.secondary_fragment);
    }

    private static Conversation getConversation(Activity activity, @IdRes int res) {
        final Fragment fragment = activity.getFragmentManager().findFragmentById(res);
        if (fragment != null && fragment instanceof ConversationFragment) {
            return ((ConversationFragment) fragment).getConversation();
        } else {
            return null;
        }
    }

    public static ConversationFragment get(Activity activity) {
        FragmentManager fragmentManager = activity.getFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.main_fragment);
        if (fragment != null && fragment instanceof ConversationFragment) {
            return (ConversationFragment) fragment;
        } else {
            fragment = fragmentManager.findFragmentById(R.id.secondary_fragment);
            return fragment != null && fragment instanceof ConversationFragment ? (ConversationFragment) fragment : null;
        }
    }

    public static Conversation getConversationReliable(Activity activity) {
        final Conversation conversation = getConversation(activity, R.id.secondary_fragment);
        if (conversation != null) {
            return conversation;
        }
        return getConversation(activity, R.id.main_fragment);
    }

    private static boolean scrolledToBottom(AbsListView listView) {
        final int count = listView.getCount();
        if (count == 0) {
            return true;
        } else if (listView.getLastVisiblePosition() == count - 1) {
            final View lastChild = listView.getChildAt(listView.getChildCount() - 1);
            return lastChild != null && lastChild.getBottom() <= listView.getHeight();
        } else {
            return false;
        }
    }

    @SuppressLint("RestrictedApi")
    private void choose_attachment(View v) {
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(activity);
        final boolean hideVoice = p.getBoolean("show_record_voice_btn", activity.getResources().getBoolean(R.bool.show_record_voice_btn));
        PopupMenu popup = new PopupMenu(activity, v);
        popup.inflate(R.menu.choose_attachment);
        final Menu menu = popup.getMenu();
        ConversationMenuConfigurator.configureQuickShareAttachmentMenu(conversation, menu, hideVoice);
        popup.setOnMenuItemClickListener(attachmentItem -> {
            switch (attachmentItem.getItemId()) {
                case R.id.attach_choose_picture:
                case R.id.attach_take_picture:
                case R.id.attach_record_video:
                case R.id.attach_choose_file:
                case R.id.attach_record_voice:
                case R.id.attach_location:
                    handleAttachmentSelection(attachmentItem);
                default:
                    return false;
            }
        });
        MenuPopupHelper menuHelper = new MenuPopupHelper(getActivity(), (MenuBuilder) menu, v);
        menuHelper.setForceShowIcon(true);
        menuHelper.show();
    }

    private void toggleScrollDownButton() {
        toggleScrollDownButton(messagesView);
    }

    private void toggleScrollDownButton(AbsListView listView) {
        if (conversation == null) {
            return;
        }
        if (scrolledToBottom(listView)) {
            lastMessageUuid = null;
            hideUnreadMessagesCount();
        } else {
            scrollToBottomButton.setEnabled(true);
            scrollToBottomButton.show();
            if (lastMessageUuid == null) {
                new Thread(lastMessageUuid = conversation.getLatestMessage().getUuid()).start();
            }
            if (conversation.getReceivedMessagesCountSinceUuid(lastMessageUuid) > 0) {
                unreadCountCustomView.setVisibility(View.VISIBLE);
            }
        }
    }

    private int getIndexOf(String uuid, List<Message> messages) {
        if (uuid == null) {
            return messages.size() - 1;
        }
        for (int i = 0; i < messages.size(); ++i) {
            if (uuid.equals(messages.get(i).getUuid())) {
                return i;
            } else {
                Message next = messages.get(i);
                while (next != null && next.wasMergedIntoPrevious()) {
                    if (uuid.equals(next.getUuid())) {
                        return i;
                    }
                    next = next.next();
                }

            }
        }
        return -1;
    }

    private ScrollState getScrollPosition() {
        final ListView listView = this.binding == null ? null : this.messagesView;
        if (listView == null || listView.getCount() == 0 || listView.getLastVisiblePosition() == listView.getCount() - 1) {
            return null;
        } else {
            final int pos = listView.getFirstVisiblePosition();
            final View view = listView.getChildAt(0);
            if (view == null) {
                return null;
            } else {
                return new ScrollState(pos, view.getTop());
            }
        }
    }

    private void setScrollPosition(ScrollState scrollPosition, String lastMessageUuid) {
        if (scrollPosition != null) {
            this.lastMessageUuid = lastMessageUuid;
            if (lastMessageUuid != null) {
                unreadCountCustomView.setUnreadCount(conversation.getReceivedMessagesCountSinceUuid(lastMessageUuid));
            }
            //TODO maybe this needs a 'post'
            this.messagesView.setSelectionFromTop(scrollPosition.position, scrollPosition.offset);
            toggleScrollDownButton();
        }
    }

    private void attachLocationToConversation(Conversation conversation, Uri uri) {
        if (conversation == null) {
            return;
        }
        activity.xmppConnectionService.attachLocationToConversation(conversation, uri, new UiCallback<Message>() {

            @Override
            public void success(Message message) {

            }

            @Override
            public void error(int errorCode, Message object) {
                //TODO show possible pgp error
            }

            @Override
            public void userInputRequired(PendingIntent pi, Message object) {

            }
        });
    }

    private void attachFileToConversation(Conversation conversation, Uri uri, String type) {
        if (conversation == null) {
            return;
        }
        final Toast prepareFileToast = Toast.makeText(getActivity(), getText(R.string.preparing_file), Toast.LENGTH_LONG);
        prepareFileToast.show();
        activity.delegateUriPermissionsToService(uri);
        activity.xmppConnectionService.attachFileToConversation(conversation, uri, type, new UiInformableCallback<Message>() {
            @Override
            public void inform(final String text) {
                hidePrepareFileToast(prepareFileToast);
                runOnUiThread(() -> activity.replaceToast(text));
            }

            @Override
            public void success(Message message) {
                runOnUiThread(() -> activity.hideToast());
                hidePrepareFileToast(prepareFileToast);
            }

            @Override
            public void error(final int errorCode, Message message) {
                hidePrepareFileToast(prepareFileToast);
                runOnUiThread(() -> activity.replaceToast(getString(errorCode)));

            }

            @Override
            public void userInputRequired(PendingIntent pi, Message message) {
                hidePrepareFileToast(prepareFileToast);
            }
        });
    }

    private void attachPhotoToConversation(Conversation conversation, Uri uri) {
        if (conversation == null) {
            return;
        }
        final Toast prepareFileToast = Toast.makeText(getActivity(), getText(R.string.preparing_image), Toast.LENGTH_LONG);
        prepareFileToast.show();
        activity.delegateUriPermissionsToService(uri);
        activity.xmppConnectionService.attachImageToConversation(conversation, uri,
                new UiCallback<Message>() {

                    @Override
                    public void userInputRequired(PendingIntent pi, Message object) {
                        hidePrepareFileToast(prepareFileToast);
                    }

                    @Override
                    public void success(Message message) {
                        hidePrepareFileToast(prepareFileToast);
                    }

                    @Override
                    public void error(final int error, Message message) {
                        hidePrepareFileToast(prepareFileToast);
                        activity.runOnUiThread(() -> activity.replaceToast(getString(error)));
                    }
                });
    }

    private void attachImagesToConversation(Conversation conversation, Uri uri) {
        if (conversation == null) {
            return;
        }
        final Toast prepareFileToast = Toast.makeText(getActivity(), getText(R.string.preparing_image), Toast.LENGTH_LONG);
        prepareFileToast.show();
        activity.delegateUriPermissionsToService(uri);
        activity.xmppConnectionService.attachImageToConversation(conversation, uri,
                new UiCallback<Message>() {

                    @Override
                    public void userInputRequired(PendingIntent pi, Message object) {
                        hidePrepareFileToast(prepareFileToast);
                    }

                    @Override
                    public void success(Message message) {
                        hidePrepareFileToast(prepareFileToast);
                    }

                    @Override
                    public void error(final int error, Message message) {
                        hidePrepareFileToast(prepareFileToast);
                        activity.runOnUiThread(() -> activity.replaceToast(getString(error)));
                    }
                });
    }

    public void attachEditorContentToConversation(Uri uri) {
        mediaPreviewAdapter.addMediaPreviews(Attachment.of(getActivity(), uri, Attachment.Type.FILE));
        toggleInputMethod();
    }

    private void attachImageToConversation(Conversation conversation, Uri uri) {
        if (conversation == null) {
            return;
        }
        final Toast prepareFileToast = Toast.makeText(getActivity(), getText(R.string.preparing_image), Toast.LENGTH_LONG);
        prepareFileToast.show();
        activity.delegateUriPermissionsToService(uri);
        activity.xmppConnectionService.attachImageToConversation(conversation, uri,
                new UiCallback<Message>() {
                    @Override
                    public void userInputRequired(PendingIntent pi, Message object) {
                        hidePrepareFileToast(prepareFileToast);
                    }

                    @Override
                    public void success(Message message) {
                        hidePrepareFileToast(prepareFileToast);
                    }

                    @Override
                    public void error(final int error, Message message) {
                        hidePrepareFileToast(prepareFileToast);
                        activity.runOnUiThread(() -> activity.replaceToast(getString(error)));
                    }
                });
    }

    private void hidePrepareFileToast(final Toast prepareFileToast) {
        if (prepareFileToast != null && activity != null) {
            activity.runOnUiThread(prepareFileToast::cancel);
        }
    }

    private void sendMessage() {
        if (mediaPreviewAdapter.hasAttachments()) {
            commitAttachments();
            return;
        }
        final Editable text = this.textinput.getText();
        final String body = text == null ? "" : text.toString();
        final Conversation conversation = this.conversation;
        if (body.length() == 0 || conversation == null) {
            return;
        }
        if (conversation.getNextEncryption() == Message.ENCRYPTION_AXOLOTL && trustKeysIfNeeded(REQUEST_TRUST_KEYS_TEXT)) {
            return;
        }
        final Message message;
        if (conversation.getCorrectingMessage() == null) {
            message = new Message(conversation, body, conversation.getNextEncryption());
            Message.configurePrivateMessage(message);
        } else {
            message = conversation.getCorrectingMessage();
            message.setBody(body);
            message.setEdited(message.getUuid());
            message.setUuid(UUID.randomUUID().toString());
        }
        switch (conversation.getNextEncryption()) {
            case Message.ENCRYPTION_OTR:
                sendOtrMessage(message);
                break;
            case Message.ENCRYPTION_PGP:
                sendPgpMessage(message);
                break;
            default:
                sendMessage(message);
        }
    }

    protected boolean trustKeysIfNeeded(int requestCode) {
        AxolotlService axolotlService = conversation.getAccount().getAxolotlService();
        final List<Jid> targets = axolotlService.getCryptoTargets(conversation);
        boolean hasUnaccepted = !conversation.getAcceptedCryptoTargets().containsAll(targets);
        boolean hasUndecidedOwn = !axolotlService.getKeysWithTrust(FingerprintStatus.createActiveUndecided()).isEmpty();
        boolean hasUndecidedContacts = !axolotlService.getKeysWithTrust(FingerprintStatus.createActiveUndecided(), targets).isEmpty();
        boolean hasPendingKeys = !axolotlService.findDevicesWithoutSession(conversation).isEmpty();
        boolean hasNoTrustedKeys = axolotlService.anyTargetHasNoTrustedKeys(targets);
        boolean downloadInProgress = axolotlService.hasPendingKeyFetches(targets);
        if (hasUndecidedOwn || hasUndecidedContacts || hasPendingKeys || hasNoTrustedKeys || hasUnaccepted || downloadInProgress) {
            axolotlService.createSessionsIfNeeded(conversation);
            Intent intent = new Intent(getActivity(), TrustKeysActivity.class);
            String[] contacts = new String[targets.size()];
            for (int i = 0; i < contacts.length; ++i) {
                contacts[i] = targets.get(i).toString();
            }
            intent.putExtra("contacts", contacts);
            intent.putExtra(EXTRA_ACCOUNT, conversation.getAccount().getJid().asBareJid().toString());
            intent.putExtra("conversation", conversation.getUuid());
            startActivityForResult(intent, requestCode);
            return true;
        } else {
            return false;
        }
    }

    public void updateChatMsgHint() {
        final boolean multi = conversation.getMode() == Conversation.MODE_MULTI;
        if (conversation.getCorrectingMessage() != null) {
            this.textInputHint.setVisibility(View.VISIBLE);
            this.textInputHint.setText(R.string.send_corrected_message);
            this.textinput.setHint(R.string.send_corrected_message);
        } else if (multi && conversation.getNextCounterpart() != null) {
            this.textinput.setHint(R.string.send_unencrypted_message);
            this.textInputHint.setVisibility(View.VISIBLE);
            this.textInputHint.setText(getString(
                    R.string.send_private_message_to,
                    conversation.getNextCounterpart().getResource()));
        } else if (multi && !conversation.getMucOptions().participating()) {
            this.textInputHint.setVisibility(View.GONE);
            this.textinput.setHint(R.string.you_are_not_participating);
        } else {
            this.textInputHint.setVisibility(View.GONE);
            this.textinput.setHint(UIHelper.getMessageHint(getActivity(), conversation));
            getActivity().invalidateOptionsMenu();
        }
    }

    public void setupIme() {
        this.textinput.refreshIme();
    }

    private void handleActivityResult(ActivityResult activityResult) {
        if (activityResult.resultCode == Activity.RESULT_OK) {
            handlePositiveActivityResult(activityResult.requestCode, activityResult.data);
        } else {
            handleNegativeActivityResult(activityResult.requestCode);
        }
    }

    private void handlePositiveActivityResult(int requestCode, final Intent data) {
        switch (requestCode) {
            case REQUEST_TRUST_KEYS_TEXT:
                sendMessage();
                break;
            case REQUEST_TRUST_KEYS_ATTACHMENTS:
                commitAttachments();
                break;
            case ATTACHMENT_CHOICE_CHOOSE_IMAGE:
                final List<Attachment> imageUris = Attachment.extractAttachments(getActivity(), data, Attachment.Type.IMAGE);
                mediaPreviewAdapter.addMediaPreviews(imageUris);
                toggleInputMethod();
                break;
            case ATTACHMENT_CHOICE_TAKE_PHOTO:
                final Uri takePhotoUri = pendingTakePhotoUri.pop();
                if (takePhotoUri != null) {
                    mediaPreviewAdapter.addMediaPreviews(Attachment.of(getActivity(), takePhotoUri, Attachment.Type.IMAGE));
                    toggleInputMethod();
                } else {
                    Log.d(mahiti.org.oelp.Config.LOGTAG, "lost take photo uri. unable to to attach");
                }
                break;
            case ATTACHMENT_CHOICE_CHOOSE_FILE:
            case ATTACHMENT_CHOICE_RECORD_VIDEO:
            case ATTACHMENT_CHOICE_RECORD_VOICE:
                final Attachment.Type type = requestCode == ATTACHMENT_CHOICE_RECORD_VOICE ? Attachment.Type.RECORDING : Attachment.Type.FILE;
                final List<Attachment> fileUris = Attachment.extractAttachments(getActivity(), data, type);
                mediaPreviewAdapter.addMediaPreviews(fileUris);
                toggleInputMethod();
                break;
            case ATTACHMENT_CHOICE_LOCATION:
                double latitude = data.getDoubleExtra("latitude", 0);
                double longitude = data.getDoubleExtra("longitude", 0);
                Uri geo = Uri.parse("geo:" + String.valueOf(latitude) + "," + String.valueOf(longitude));
                mediaPreviewAdapter.addMediaPreviews(Attachment.of(getActivity(), geo, Attachment.Type.LOCATION));
                toggleInputMethod();
                break;
            case REQUEST_INVITE_TO_CONVERSATION:
                XmppActivity.ConferenceInvite invite = XmppActivity.ConferenceInvite.parse(data);
                if (invite != null) {
                    if (invite.execute(activity)) {
                        activity.mToast = Toast.makeText(activity, R.string.creating_conference, Toast.LENGTH_LONG);
                        activity.mToast.show();
                    }
                }
                break;
        }
    }

    private void commitAttachments() {
        if (!hasPermissions(REQUEST_COMMIT_ATTACHMENTS, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            return;
        }
        if (conversation.getNextEncryption() == Message.ENCRYPTION_AXOLOTL && trustKeysIfNeeded(REQUEST_TRUST_KEYS_ATTACHMENTS)) {
            return;
        }
        final List<Attachment> attachments = mediaPreviewAdapter.getAttachments();
        final PresenceSelector.OnPresenceSelected callback = () -> {
            for (Iterator<Attachment> i = attachments.iterator(); i.hasNext(); i.remove()) {
                final Attachment attachment = i.next();
                if (attachment.getType() == Attachment.Type.LOCATION) {
                    attachLocationToConversation(conversation, attachment.getUri());
                } else if (attachment.getType() == Attachment.Type.IMAGE) {
                    Log.d(mahiti.org.oelp.Config.LOGTAG, "ConversationsActivity.commitAttachments() - attaching image to conversations. CHOOSE_IMAGE");
                    attachImageToConversation(conversation, attachment.getUri());
                } else {
                    Log.d(mahiti.org.oelp.Config.LOGTAG, "ConversationsActivity.commitAttachments() - attaching file to conversations. CHOOSE_FILE/RECORD_VOICE/RECORD_VIDEO");
                    attachFileToConversation(conversation, attachment.getUri(), attachment.getMime());
                }
            }
            mediaPreviewAdapter.notifyDataSetChanged();
            toggleInputMethod();
        };
        if (conversation == null
                || conversation.getMode() == Conversation.MODE_MULTI
                || Attachment.canBeSendInband(attachments)
                || (conversation.getAccount().httpUploadAvailable() && FileBackend.allFilesUnderSize(getActivity(), attachments, getMaxHttpUploadSize(conversation)))) {
            callback.onPresenceSelected();
        } else {
            activity.selectPresence(conversation, callback);
        }
    }

    public void toggleInputMethod() {
        boolean hasAttachments = mediaPreviewAdapter.hasAttachments();
        textinput.setVisibility(hasAttachments ? View.GONE : View.VISIBLE);
        mediaPreview.setVisibility(hasAttachments ? View.VISIBLE : View.GONE);
        if (mOptionsMenu != null) {
            ConversationMenuConfigurator.configureAttachmentMenu(conversation, mOptionsMenu, activity.xmppConnectionService.getAttachmentChoicePreference(), hasAttachments);
        }
        updateSendButton();
    }

    private void handleNegativeActivityResult(int requestCode) {
        switch (requestCode) {
            case ATTACHMENT_CHOICE_TAKE_PHOTO:
                if (pendingTakePhotoUri.clear()) {
                    Log.d(mahiti.org.oelp.Config.LOGTAG, "cleared pending photo uri after negative activity result");
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ActivityResult activityResult = ActivityResult.of(requestCode, resultCode, data);
        if (activity != null && activity.xmppConnectionService != null) {
            handleActivityResult(activityResult);
        } else {
            this.postponedActivityResult.push(activityResult);
        }
    }

    public void unblockConversation(final Blockable conversation) {
        activity.xmppConnectionService.sendUnblockRequest(conversation);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(mahiti.org.oelp.Config.LOGTAG, "ConversationFragment.onAttach()");
        if (activity instanceof ConversationsActivity) {
            this.activity = (ConversationsActivity) activity;
        } else {
            throw new IllegalStateException("Trying to attach fragment to activity that is not the ConversationsActivity");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.activity = null; //TODO maybe not a good idea since some callbacks really need it
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        mOptionsMenu = menu;
//        boolean hasAttachments = mediaPreviewAdapter != null && mediaPreviewAdapter.hasAttachments();
//        menuInflater.inflate(R.menu.fragment_conversation, menu);
//        final MenuItem menuInviteContact = menu.findItem(R.id.action_invite);
//        final MenuItem menuNeedHelp = menu.findItem(R.id.action_create_issue);
//        final MenuItem menuSearchUpdates = menu.findItem(R.id.action_check_updates);
//        final MenuItem menuArchiveChat = menu.findItem(R.id.action_archive_chat);
//        final MenuItem menuGroupDetails = menu.findItem(R.id.action_group_details);
//        final MenuItem menuContactDetails = menu.findItem(R.id.action_contact_details);
//        final MenuItem menuMediaBrowser = menu.findItem(R.id.action_mediabrowser);
//
//        if (conversation != null) {
//            if (conversation.getMode() == Conversation.MODE_MULTI) {
//                menuInviteContact.setVisible(conversation.getMucOptions().canInvite());
//                menuArchiveChat.setTitle(R.string.action_end_conversation_muc);
//            } else {
//                menuInviteContact.setVisible(false);
//                menuArchiveChat.setTitle(R.string.action_end_conversation);
//            }
//            Fragment secondaryFragment = getFragmentManager().findFragmentById(R.id.secondary_fragment);
//            if (secondaryFragment instanceof ConversationFragment) {
//                if (conversation.getMode() == Conversation.MODE_MULTI) {
//                    menuGroupDetails.setTitle(conversation.getMucOptions().isPrivateAndNonAnonymous() ? R.string.action_group_details : R.string.channel_details);
//                    menuGroupDetails.setVisible(true);
//                    menuContactDetails.setVisible(false);
//                } else {
//                    menuGroupDetails.setVisible(false);
//                    menuContactDetails.setVisible(!this.conversation.withSelf());
//                }
//            } else {
//                menuGroupDetails.setVisible(false);
//                menuContactDetails.setVisible(false);
//            }
//            menuMediaBrowser.setVisible(true);
//            menuNeedHelp.setVisible(true);
//            menuSearchUpdates.setVisible(false);
//            ConversationMenuConfigurator.configureAttachmentMenu(conversation, menu, activity.xmppConnectionService.getAttachmentChoicePreference(), hasAttachments);
//            ConversationMenuConfigurator.configureEncryptionMenu(conversation, menu);
//        } else {
//            menuNeedHelp.setVisible(false);
//            menuSearchUpdates.setVisible(true);
//            menuInviteContact.setVisible(false);
//            menuGroupDetails.setVisible(false);
//            menuContactDetails.setVisible(false);
//            menuMediaBrowser.setVisible(false);
//        }
        super.onCreateOptionsMenu(menu, menuInflater);
    }
    View view;
    EditMessage textinput;
    LinearLayout messageInputBox;
    ImageButton textSendButton;
    ImageButton recordVoiceButton;
    FloatingActionButton scrollToBottomButton;
    ListView messagesView;
    RecyclerView mediaPreview;
    TextView textInputHint;
    UnreadCountCustomView unreadCountCustomView;
    RelativeLayout snackbar;
    TextView snackbarMessage;
    TextView snackbarAction;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.binding = DataBindingUtil.inflate(inflater, R.layout.fragment_conversation, container, false);
        
        view = binding.getRoot();
        binding.getRoot().setOnClickListener(null); //TODO why the fuck did we do this?
        messageInputBox = view.findViewById(R.id.message_input_box);
        textSendButton = view.findViewById(R.id.textSendButton);
        scrollToBottomButton = view.findViewById(R.id.scroll_to_bottom_button);
        recordVoiceButton = view.findViewById(R.id.recordVoiceButton);
        messagesView = view.findViewById(R.id.messages_view);
        mediaPreview = view.findViewById(R.id.media_preview);
        textInputHint = view.findViewById(R.id.text_input_hint);
        textinput = view.findViewById(R.id.textinput);
        unreadCountCustomView = view.findViewById(R.id.unread_count_custom_view);
        snackbar = view.findViewById(R.id.snackbar);
        snackbarMessage = view.findViewById(R.id.snackbar_message);
        snackbarAction = view.findViewById(R.id.snackbar_action);

        textinput.addTextChangedListener(new StylingHelper.MessageEditorStyler(textinput));
        textinput.setOnEditorActionListener(mEditorActionListener);
        textinput.setRichContentListener(new String[]{"image/*"}, mEditorContentListener);
        messageInputBox.setBackgroundResource(messageInputBubble());

        textSendButton.setOnClickListener(this.mSendButtonListener);
        textSendButton.setOnLongClickListener(this.mSendButtonLongListener);
        scrollToBottomButton.setOnClickListener(this.mScrollButtonListener);
        recordVoiceButton.setOnClickListener(this.mRecordVoiceButtonListener);

        messagesView.setOnScrollListener(mOnScrollListener);
        messagesView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        mediaPreviewAdapter = new MediaPreviewAdapter(this);
        mediaPreview.setAdapter(mediaPreviewAdapter);
        messageListAdapter = new MessageAdapter((XmppActivity) getActivity(), this.messageList);
        messageListAdapter.setOnContactPictureClicked(this);
        messageListAdapter.setOnContactPictureLongClicked(this);
        messageListAdapter.setOnQuoteListener(text -> quoteText(text, conversation.getContact().getDisplayName()));
        messagesView.setAdapter(messageListAdapter);

        registerForContextMenu(messagesView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.textinput.setCustomInsertionActionModeCallback(new EditMessageActionModeCallback(this.textinput));
        }

        return binding.getRoot();
    }

    @Override
    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int animator = enter ? R.animator.fade_right_in : R.animator.fade_right_out;
            return AnimatorInflater.loadAnimator(getActivity(), animator);
        } else {
            return null;
        }
    }

    private void quoteText(String text, String user) {
        if (textinput.isEnabled()) {
            String username = "";
            if (user != null && user.length() > 0) {
                String res = "*" + user + "*";
                username = getString(R.string.x_has_written, res) + System.getProperty("line.separator");
            }
            textinput.insertAsQuote(username + text);
            textinput.requestFocus();
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.showSoftInput(textinput, InputMethodManager.SHOW_IMPLICIT);
            }
        }
    }

    private void showRecordVoiceButton() {
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(activity);
        final boolean ShowRecordVoiceButton = p.getBoolean("show_record_voice_btn", activity.getResources().getBoolean(R.bool.show_record_voice_btn));
        Log.d(mahiti.org.oelp.Config.LOGTAG, "Recorder " + ShowRecordVoiceButton);
        if (ShowRecordVoiceButton) {
            recordVoiceButton.setVisibility(View.GONE);
        } else {
            recordVoiceButton.setVisibility(View.GONE);
        }
        recordVoiceButton.setImageResource(activity.getThemeResource(R.attr.ic_send_voice_offline, R.drawable.ic_send_voice_offline));
    }

    private void quoteMessage(Message message, String user) {
        quoteText(MessageUtils.prepareQuote(message), user);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        synchronized (this.messageList) {
            super.onCreateContextMenu(menu, v, menuInfo);
            AdapterView.AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) menuInfo;
            this.selectedMessage = this.messageList.get(acmi.position);
            populateContextMenu(menu);
        }
    }

    private void populateContextMenu(ContextMenu menu) {
        final Message m = this.selectedMessage;
        final Transferable t = m.getTransferable();
        Message relevantForCorrection = m;
        while (relevantForCorrection.mergeable(relevantForCorrection.next())) {
            relevantForCorrection = relevantForCorrection.next();
        }
        if (m.getType() != Message.TYPE_STATUS) {

            if (m.getEncryption() == Message.ENCRYPTION_AXOLOTL_NOT_FOR_THIS_DEVICE || m.getEncryption() == Message.ENCRYPTION_AXOLOTL_FAILED) {
                return;
            }

            final boolean deleted = m.isFileDeleted();
            final boolean encrypted = m.getEncryption() == Message.ENCRYPTION_DECRYPTION_FAILED
                    || m.getEncryption() == Message.ENCRYPTION_PGP;
            final boolean receiving = m.getStatus() == Message.STATUS_RECEIVED && (t instanceof JingleConnection || t instanceof HttpDownloadConnection);
            activity.getMenuInflater().inflate(R.menu.message_context, menu);
            menu.setHeaderTitle(R.string.message_options);
            MenuItem openWith = menu.findItem(R.id.open_with);
            MenuItem copyMessage = menu.findItem(R.id.copy_message);
            MenuItem copyLink = menu.findItem(R.id.copy_link);
            MenuItem quoteMessage = menu.findItem(R.id.quote_message);
            MenuItem retryDecryption = menu.findItem(R.id.retry_decryption);
            MenuItem correctMessage = menu.findItem(R.id.correct_message);
            MenuItem deleteMessage = menu.findItem(R.id.delete_message);
            MenuItem shareWith = menu.findItem(R.id.share_with);
            MenuItem sendAgain = menu.findItem(R.id.send_again);
            MenuItem copyUrl = menu.findItem(R.id.copy_url);
            MenuItem cancelTransmission = menu.findItem(R.id.cancel_transmission);
            MenuItem downloadFile = menu.findItem(R.id.download_file);
            MenuItem deleteFile = menu.findItem(R.id.delete_file);
            MenuItem showErrorMessage = menu.findItem(R.id.show_error_message);
            final boolean showError = m.getStatus() == Message.STATUS_SEND_FAILED && m.getErrorMessage() != null && !Message.ERROR_MESSAGE_CANCELLED.equals(m.getErrorMessage());
            deleteMessage.setVisible(true);
            if (!m.isFileOrImage() && !encrypted && !m.isGeoUri() && !m.treatAsDownloadable()) {
                copyMessage.setVisible(true);
                quoteMessage.setVisible(!showError && MessageUtils.prepareQuote(m).length() > 0);
                String body = m.getMergedBody().toString();
                if (ShareUtil.containsXmppUri(body)) {
                    copyLink.setTitle(R.string.copy_jabber_id);
                    copyLink.setVisible(true);
                } else if (Patterns.AUTOLINK_WEB_URL.matcher(body).find()) {
                    copyLink.setVisible(true);
                }
            }
            if (m.getEncryption() == Message.ENCRYPTION_DECRYPTION_FAILED && !deleted) {
                retryDecryption.setVisible(true);
            }
            if (!showError
                    && relevantForCorrection.getType() == Message.TYPE_TEXT
                    && !m.isGeoUri()
                    && relevantForCorrection.isLastCorrectableMessage()
                    && m.getConversation() instanceof Conversation) {
                correctMessage.setVisible(true);
            }
            if ((m.isFileOrImage() && !deleted && !receiving) || (m.getType() == Message.TYPE_TEXT && !m.treatAsDownloadable())) {
                shareWith.setVisible(true);

            }
            if (m.getStatus() == Message.STATUS_SEND_FAILED) {
                sendAgain.setVisible(true);
            }
            if (m.hasFileOnRemoteHost()
                    || m.isGeoUri()
                    || m.isXmppUri()
                    || m.treatAsDownloadable()
                    || t instanceof HttpDownloadConnection) {
                copyUrl.setVisible(true);
            }
            if (m.isFileOrImage() && deleted && m.hasFileOnRemoteHost()) {
                downloadFile.setVisible(true);
                downloadFile.setTitle(activity.getString(R.string.download_x_file, UIHelper.getFileDescriptionString(activity, m)));
            }
            final boolean waitingOfferedSending = m.getStatus() == Message.STATUS_WAITING
                    || m.getStatus() == Message.STATUS_UNSEND
                    || m.getStatus() == Message.STATUS_OFFERED;
            final boolean cancelable = (t != null && !deleted) || waitingOfferedSending && m.needsUploading();
            if (cancelable) {
                cancelTransmission.setVisible(true);
            }
            if (m.isFileOrImage() && !deleted && !cancelable) {
                String path = m.getRelativeFilePath();
                Log.d(mahiti.org.oelp.Config.LOGTAG, "Path = " + path);
                if (path == null || !path.startsWith("/") || path.contains(FileBackend.getConversationsDirectory("null"))) {
                    deleteFile.setVisible(true);
                    deleteFile.setTitle(activity.getString(R.string.delete_x_file, UIHelper.getFileDescriptionString(activity, m)));
                }
            }
            if (showError) {
                showErrorMessage.setVisible(true);
            }
            final String mime = m.isFileOrImage() ? m.getMimeType() : null;
            if ((m.isGeoUri() && GeoHelper.openInOsmAnd(getActivity(), m)) || (mime != null && mime.startsWith("audio/"))) {
                openWith.setVisible(true);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        String user;
        try {
            final Contact contact = selectedMessage.getContact();
            if (conversation.getMode() == Conversation.MODE_MULTI) {
                if (contact != null) {
                    user = contact.getDisplayName();
                } else {
                    user = UIHelper.getDisplayedMucCounterpart(selectedMessage.getCounterpart());
                }
            } else {
                user = contact != null ? contact.getDisplayName() : null;
            }
            if (selectedMessage.getStatus() == Message.STATUS_SEND
                    || selectedMessage.getStatus() == Message.STATUS_SEND_FAILED
                    || selectedMessage.getStatus() == Message.STATUS_SEND_RECEIVED
                    || selectedMessage.getStatus() == Message.STATUS_SEND_DISPLAYED) {
                user = getString(R.string.me);
            }
        } catch (Exception e) {
            e.printStackTrace();
            user = null;
        }
        switch (item.getItemId()) {
            case R.id.share_with:
                ShareUtil.share(activity, selectedMessage, user);
                return true;
            case R.id.correct_message:
                correctMessage(selectedMessage);
                return true;
            case R.id.copy_message:
                ShareUtil.copyToClipboard(activity, selectedMessage);
                return true;
            case R.id.copy_link:
                ShareUtil.copyLinkToClipboard(activity, selectedMessage);
                return true;
            case R.id.quote_message:
                quoteMessage(selectedMessage, selectedMessage.getContact().getDisplayName());
                return true;
            case R.id.send_again:
                resendMessage(selectedMessage);
                return true;
            case R.id.copy_url:
                ShareUtil.copyUrlToClipboard(activity, selectedMessage);
                return true;
            case R.id.download_file:
                startDownloadable(selectedMessage);
                return true;
            case R.id.cancel_transmission:
                cancelTransmission(selectedMessage);
                return true;
            case R.id.retry_decryption:
                retryDecryption(selectedMessage);
                return true;
            case R.id.delete_message:
                deleteMessage(selectedMessage);
                return true;
            case R.id.delete_file:
                deleteFile(selectedMessage);
                return true;
            case R.id.show_error_message:
                showErrorMessage(selectedMessage);
                return true;
            case R.id.open_with:
                openWith(selectedMessage);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (MenuDoubleTabUtil.shouldIgnoreTap()) {
            return false;
        } else if (conversation == null) {
            return super.onOptionsItemSelected(item);
        }
        switch (item.getItemId()) {
            case R.id.encryption_choice_axolotl:
            case R.id.encryption_choice_otr:
            case R.id.encryption_choice_pgp:
            case R.id.encryption_choice_none:
                handleEncryptionSelection(item);
                break;
            case R.id.attach_choose_picture:
            case R.id.attach_take_picture:
            case R.id.attach_record_video:
            case R.id.attach_choose_file:
            case R.id.attach_record_voice:
            case R.id.attach_location:
                handleAttachmentSelection(item);
                break;
            case R.id.action_archive_chat:
                if (conversation.getMode() == Conversation.MODE_SINGLE) {
                    activity.xmppConnectionService.archiveConversation(conversation);
                } else {
                    activity.runOnUiThread(() -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle(getString(R.string.action_end_conversation_muc));
                        builder.setMessage(getString(R.string.leave_conference_warning));
                        builder.setNegativeButton(getString(R.string.cancel), null);
                        builder.setPositiveButton(getString(R.string.action_end_conversation_muc),
                                (dialog, which) -> {
                                    activity.xmppConnectionService.archiveConversation(conversation);
                                });
                        builder.create().show();
                    });
                }
                break;
            case R.id.action_invite:
                startActivityForResult(ChooseContactActivity.create(activity, conversation), REQUEST_INVITE_TO_CONVERSATION);
                break;
            case R.id.action_clear_history:
                clearHistoryDialog(conversation);
                break;
            case R.id.action_group_details:
//                Intent intent = new Intent(activity, ConferenceDetailsActivity.class);
//                intent.setAction(ConferenceDetailsActivity.ACTION_VIEW_MUC);
//                intent.putExtra("uuid", conversation.getUuid());
//                startActivity(intent);
                break;
            case R.id.action_contact_details:
                activity.switchToContactDetails(conversation.getContact());
                break;
            case R.id.action_mediabrowser:
                MediaBrowserActivity.launch(activity, conversation);
                break;
            case R.id.action_block:
            case R.id.action_unblock:
                final Activity activity = getActivity();
                if (activity instanceof XmppActivity) {
                    BlockContactDialog.show((XmppActivity) activity, conversation);
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleAttachmentSelection(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.attach_choose_picture:
                attachFile(ATTACHMENT_CHOICE_CHOOSE_IMAGE);
                break;
            case R.id.attach_take_picture:
                attachFile(ATTACHMENT_CHOICE_TAKE_PHOTO);
                break;
            case R.id.attach_record_video:
                attachFile(ATTACHMENT_CHOICE_RECORD_VIDEO);
                break;
            case R.id.attach_choose_file:
                attachFile(ATTACHMENT_CHOICE_CHOOSE_FILE);
                break;
            case R.id.attach_record_voice:
                attachFile(ATTACHMENT_CHOICE_RECORD_VOICE);
                break;
            case R.id.attach_location:
                attachFile(ATTACHMENT_CHOICE_LOCATION);
                break;
        }
    }

    private void handleEncryptionSelection(MenuItem item) {
        if (conversation == null) {
            return;
        }
        final boolean updated;
        switch (item.getItemId()) {
            case R.id.encryption_choice_none:
                updated = conversation.setNextEncryption(Message.ENCRYPTION_NONE);
                item.setChecked(true);
                break;
            case R.id.encryption_choice_otr:
                updated = conversation.setNextEncryption(Message.ENCRYPTION_OTR);
                item.setChecked(true);
                break;
            case R.id.encryption_choice_pgp:
                if (activity.hasPgp()) {
                    if (conversation.getAccount().getPgpSignature() != null) {
                        updated = conversation.setNextEncryption(Message.ENCRYPTION_PGP);
                        item.setChecked(true);
                    } else {
                        updated = false;
                        activity.announcePgp(conversation.getAccount(), conversation, null, activity.onOpenPGPKeyPublished);
                    }
                } else {
                    activity.showInstallPgpDialog();
                    updated = false;
                }
                break;
            case R.id.encryption_choice_axolotl:
                Log.d(mahiti.org.oelp.Config.LOGTAG, AxolotlService.getLogprefix(conversation.getAccount())
                        + "Enabled axolotl for Contact " + conversation.getContact().getJid());
                updated = conversation.setNextEncryption(Message.ENCRYPTION_AXOLOTL);
                item.setChecked(true);
                break;
            default:
                updated = conversation.setNextEncryption(Message.ENCRYPTION_NONE);
                break;
        }
        if (updated) {
            activity.xmppConnectionService.updateConversation(conversation);
        }
        updateChatMsgHint();
        getActivity().invalidateOptionsMenu();
        activity.refreshUi();
    }

    public void attachFile(final int attachmentChoice) {
        if (attachmentChoice == ATTACHMENT_CHOICE_RECORD_VOICE) {
            if (!hasPermissions(attachmentChoice, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)) {
                return;
            }
        } else if (attachmentChoice == ATTACHMENT_CHOICE_TAKE_PHOTO || attachmentChoice == ATTACHMENT_CHOICE_RECORD_VIDEO) {
            if (!hasPermissions(attachmentChoice, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)) {
                return;
            }
        } else if (attachmentChoice == ATTACHMENT_CHOICE_LOCATION) {
            if (!hasPermissions(attachmentChoice, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                return;
            }
        } else if (attachmentChoice == ATTACHMENT_CHOICE_CHOOSE_FILE || attachmentChoice == ATTACHMENT_CHOICE_CHOOSE_IMAGE || attachmentChoice == ATTACHMENT_CHOICE_CHOOSE_VIDEO) {
            if (!hasPermissions(attachmentChoice, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                return;
            }
        }
        try {
            activity.getPreferences().edit()
                    .putString(RECENTLY_USED_QUICK_ACTION, SendButtonAction.of(attachmentChoice).toString())
                    .apply();
        } catch (IllegalArgumentException e) {
            //just do not save
        }
        final int encryption = conversation.getNextEncryption();
        final int mode = conversation.getMode();
        if (encryption == Message.ENCRYPTION_PGP) {
            if (activity.hasPgp()) {
                if (mode == Conversation.MODE_SINGLE && conversation.getContact().getPgpKeyId() != 0) {
                    activity.xmppConnectionService.getPgpEngine().hasKey(
                            conversation.getContact(),
                            new UiCallback<Contact>() {

                                @Override
                                public void userInputRequired(PendingIntent pi, Contact contact) {
                                    startPendingIntent(pi, attachmentChoice);
                                }

                                @Override
                                public void success(Contact contact) {
                                    invokeAttachFileIntent(attachmentChoice);
                                }

                                @Override
                                public void error(int error, Contact contact) {
                                    activity.replaceToast(getString(error));
                                }
                            });
                } else if (mode == Conversation.MODE_MULTI && conversation.getMucOptions().pgpKeysInUse()) {
                    if (!conversation.getMucOptions().everybodyHasKeys()) {
                        getActivity().runOnUiThread(() -> {
                            Toast warning = Toast.makeText(activity, R.string.missing_public_keys, Toast.LENGTH_LONG);
                            warning.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                            warning.show();
                        });
                    }
                    invokeAttachFileIntent(attachmentChoice);
                } else {
                    showNoPGPKeyDialog(false, (dialog, which) -> {
                        conversation.setNextEncryption(Message.ENCRYPTION_NONE);
                        activity.xmppConnectionService.updateConversation(conversation);
                        invokeAttachFileIntent(attachmentChoice);
                    });
                }
            } else {
                activity.showInstallPgpDialog();
            }
        } else {
            invokeAttachFileIntent(attachmentChoice);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0) {
            if (PermissionUtils.allGranted(grantResults)) {
                switch (requestCode) {
                    case REQUEST_START_DOWNLOAD:
                        if (this.mPendingDownloadableMessage != null) {
                            startDownloadable(this.mPendingDownloadableMessage);
                        }
                        break;
                    case REQUEST_ADD_EDITOR_CONTENT:
                        if (this.mPendingEditorContent != null) {
                            attachEditorContentToConversation(this.mPendingEditorContent);
                        }
                        break;
                    case REQUEST_COMMIT_ATTACHMENTS:
                        commitAttachments();
                        break;
                    default:
                        attachFile(requestCode);
                        break;
                }
            } else {
                @StringRes int res;
                String firstDenied = PermissionUtils.getFirstDenied(grantResults, permissions);
                if (Manifest.permission.RECORD_AUDIO.equals(firstDenied)) {
                    res = R.string.no_microphone_permission;
                } else if (Manifest.permission.CAMERA.equals(firstDenied)) {
                    res = R.string.no_camera_permission;
                } else if (Manifest.permission.ACCESS_COARSE_LOCATION.equals(firstDenied)
                        || Manifest.permission.ACCESS_FINE_LOCATION.equals(firstDenied)) {
                    res = R.string.no_location_permission;
                } else {
                    res = R.string.no_storage_permission;
                }
                Toast.makeText(getActivity(), res, Toast.LENGTH_SHORT).show();
            }
        }
        if (PermissionUtils.writeGranted(grantResults, permissions)) {
            if (activity != null && activity.xmppConnectionService != null) {
                activity.xmppConnectionService.restartFileObserver();
            }
            refresh();
        }
    }

    public void startDownloadable(Message message) {
        if (!hasPermissions(REQUEST_START_DOWNLOAD, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            this.mPendingDownloadableMessage = message;
            return;
        }
        Transferable transferable = message.getTransferable();
        if (transferable != null) {
            if (transferable instanceof TransferablePlaceholder && message.hasFileOnRemoteHost()) {
                createNewConnection(message);
                return;
            }
            if (!transferable.start()) {
                Log.d(mahiti.org.oelp.Config.LOGTAG, "type: " + transferable.getClass().getName());
                Toast.makeText(getActivity(), R.string.not_connected_try_again, Toast.LENGTH_SHORT).show();
            }
        } else if (message.treatAsDownloadable() || message.hasFileOnRemoteHost()) {
            createNewConnection(message);
        }
    }

    private void createNewConnection(final Message message) {
        if (!activity.xmppConnectionService.getHttpConnectionManager().checkConnection(message)) {
            Toast.makeText(getActivity(), R.string.not_connected_try_again, Toast.LENGTH_SHORT).show();
            return;
        }
        activity.xmppConnectionService.getHttpConnectionManager().createNewDownloadConnection(message, true);
    }

    @SuppressLint("InflateParams")
    protected void clearHistoryDialog(final Conversation conversation) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.clear_conversation_history));
        final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_clear_history, null);
        final CheckBox endConversationCheckBox = dialogView.findViewById(R.id.end_conversation_checkbox);
        if (conversation.getMode() == Conversation.MODE_SINGLE) {
            endConversationCheckBox.setVisibility(View.VISIBLE);
            endConversationCheckBox.setChecked(true);
        }
        builder.setView(dialogView);
        builder.setNegativeButton(getString(R.string.cancel), null);
        builder.setPositiveButton(getString(R.string.confirm), (dialog, which) -> {
            this.activity.xmppConnectionService.clearConversationHistory(conversation);
            if (endConversationCheckBox.isChecked()) {
                this.activity.xmppConnectionService.archiveConversation(conversation);
            } else {
                activity.onConversationsListItemUpdated();
                refresh();
            }
        });
        builder.create().show();
    }

    private boolean hasPermissions(int requestCode, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final List<String> missingPermissions = new ArrayList<>();
            for (String permission : permissions) {
                if (mahiti.org.oelp.Config.ONLY_INTERNAL_STORAGE && permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    continue;
                }
                if (activity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    missingPermissions.add(permission);
                }
            }
            if (missingPermissions.size() == 0) {
                return true;
            } else {
                requestPermissions(missingPermissions.toArray(new String[missingPermissions.size()]), requestCode);
                return false;
            }
        } else {
            return true;
        }
    }

    protected void invokeAttachFileIntent(final int attachmentChoice) {
        Intent intent = new Intent();
        boolean chooser = false;
        switch (attachmentChoice) {
            case ATTACHMENT_CHOICE_CHOOSE_IMAGE:
                intent.setAction(Intent.ACTION_GET_CONTENT);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                }
                intent.setType("image/*");
                chooser = true;
                break;
            case ATTACHMENT_CHOICE_CHOOSE_VIDEO:
                chooser = true;
                intent.setType("video/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                break;
            case ATTACHMENT_CHOICE_RECORD_VIDEO:
                intent.setAction(MediaStore.ACTION_VIDEO_CAPTURE);
                break;
            case ATTACHMENT_CHOICE_TAKE_PHOTO:
                final Uri uri = activity.xmppConnectionService.getFileBackend().getTakePhotoUri();
                pendingTakePhotoUri.push(uri);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                break;
            case ATTACHMENT_CHOICE_CHOOSE_FILE:
                chooser = true;
                intent.setType("*/*");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                }
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                break;
            case ATTACHMENT_CHOICE_RECORD_VOICE:
                intent = new Intent(getActivity(), RecordingActivity.class);
                break;
            case ATTACHMENT_CHOICE_LOCATION:
                intent = new Intent(getActivity(), ShareLocationActivity.class);
                break;
        }
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            Log.d(mahiti.org.oelp.Config.LOGTAG, "Attachment: " + attachmentChoice);
            if (chooser) {
                startActivityForResult(
                        Intent.createChooser(intent, getString(R.string.perform_action_with)),
                        attachmentChoice);
                activity.overridePendingTransition(R.animator.fade_in, R.animator.fade_out);
            } else {
                startActivityForResult(intent, attachmentChoice);
                activity.overridePendingTransition(R.animator.fade_in, R.animator.fade_out);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        messagesView.post(this::fireReadEvent);
    }

    private void fireReadEvent() {
        if (activity != null && this.conversation != null) {
            String uuid = getLastVisibleMessageUuid();
            if (uuid != null) {
                activity.onConversationRead(this.conversation, uuid);
            }
        }
    }

    private String getLastVisibleMessageUuid() {
        if (binding == null) {
            return null;
        }
        synchronized (this.messageList) {
            int pos = messagesView.getLastVisiblePosition();
            if (pos >= 0) {
                Message message = null;
                for (int i = pos; i >= 0; --i) {
                    try {
                        message = (Message) messagesView.getItemAtPosition(i);
                    } catch (IndexOutOfBoundsException e) {
                        //should not happen if we synchronize properly. however if that fails we just gonna try item -1
                        continue;
                    }
                    if (message.getType() != Message.TYPE_STATUS) {
                        break;
                    }
                }
                if (message != null) {
                    while (message.next() != null && message.next().wasMergedIntoPrevious()) {
                        message = message.next();
                    }
                    return message.getUuid();
                }
            }
        }
        return null;
    }

    private void openWith(final Message message) {
        if (message.isGeoUri()) {
            GeoHelper.view(getActivity(), message);
        } else {
            final DownloadableFile file = activity.xmppConnectionService.getFileBackend().getFile(message);
            ViewUtil.view(activity, file);
        }
    }

    private void showErrorMessage(final Message message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.error_message);
        final String errorMessage = message.getErrorMessage();
        final String[] errorMessageParts = errorMessage == null ? new String[0] : errorMessage.split("\\u001f");
        final String displayError;
        if (errorMessageParts.length == 2) {
            displayError = errorMessageParts[1];
        } else {
            displayError = errorMessage;
        }
        builder.setMessage(displayError);
        builder.setNegativeButton(R.string.copy_to_clipboard, (dialog, which) -> {
            activity.copyTextToClipboard(displayError, R.string.error_message);
            Toast.makeText(activity, R.string.error_message_copied_to_clipboard, Toast.LENGTH_SHORT).show();
        });
        builder.setPositiveButton(R.string.ok, null);
        builder.create().show();
    }

    private void deleteMessage(Message message) {
        while (message.mergeable(message.next())) {
            message = message.next();
        }
        final Conversation conversation = (Conversation) message.getConversation();
        Message relevantForCorrection = message;
        while (relevantForCorrection.mergeable(relevantForCorrection.next())) {
            relevantForCorrection = relevantForCorrection.next();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setNegativeButton(R.string.cancel, null);
        builder.setTitle(R.string.delete_message_dialog);
        builder.setMessage(R.string.delete_message_dialog_msg);
        Message finalRelevantForCorrection = relevantForCorrection;
        Message finalMessage = message;
        builder.setPositiveButton(R.string.confirm, (dialog, which) -> {
            if (finalRelevantForCorrection.getType() == Message.TYPE_TEXT
                    && !finalMessage.isGeoUri()
                    && finalRelevantForCorrection.isLastCorrectableMessage()
                    && finalMessage.getConversation() instanceof Conversation
                    && (((Conversation) finalMessage.getConversation()).getMucOptions().nonanonymous() || finalMessage.getConversation().getMode() == Conversation.MODE_SINGLE)) {
                this.conversation.setCorrectingMessage(finalMessage);
                Message deletedmessage = conversation.getCorrectingMessage();
                deletedmessage.setBody(getString(R.string.message_deleted));
                deletedmessage.setEdited(deletedmessage.getUuid());
                deletedmessage.setUuid(UUID.randomUUID().toString());
                sendMessage(deletedmessage);
                activity.xmppConnectionService.deleteMessage(conversation, deletedmessage);
                refresh();
            }
            activity.xmppConnectionService.deleteMessage(conversation, finalMessage);
            activity.onConversationsListItemUpdated();
            refresh();
        });
        builder.create().show();
    }

    private void deleteFile(final Message message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setNegativeButton(R.string.cancel, null);
        builder.setTitle(R.string.delete_file_dialog);
        builder.setMessage(R.string.delete_file_dialog_msg);
        builder.setPositiveButton(R.string.confirm, (dialog, which) -> {
            if (activity.xmppConnectionService.getFileBackend().deleteFile(message)) {
                message.setFileDeleted(true);
                activity.xmppConnectionService.updateMessage(message, false);
                activity.onConversationsListItemUpdated();
                refresh();
            }
        });
        builder.create().show();
    }

    public void resendMessage(final Message message) {
        if (message != null && message.isFileOrImage()) {
            if (!(message.getConversation() instanceof Conversation)) {
                return;
            }
            final Conversation conversation = (Conversation) message.getConversation();
            final DownloadableFile file = activity.xmppConnectionService.getFileBackend().getFile(message);
            if ((file.exists() && file.canRead()) || message.hasFileOnRemoteHost()) {
                final XmppConnection xmppConnection = conversation.getAccount().getXmppConnection();
                if (!message.hasFileOnRemoteHost()
                        && xmppConnection != null
                        && conversation.getMode() == Conversational.MODE_SINGLE
                        && !xmppConnection.getFeatures().httpUpload(message.getFileParams().size)) {
                    activity.selectPresence(conversation, () -> {
                        message.setCounterpart(conversation.getNextCounterpart());
                        activity.xmppConnectionService.resendFailedMessages(message);
                        new Handler().post(() -> {
                            int size = messageList.size();
                            this.messagesView.setSelection(size - 1);
                        });
                    });
                    return;
                }
            } else if (!Compatibility.hasStoragePermission(getActivity())) {
                Toast.makeText(activity, R.string.no_storage_permission, Toast.LENGTH_SHORT).show();
                return;
            } else {
                Toast.makeText(activity, R.string.file_deleted, Toast.LENGTH_SHORT).show();
                message.setFileDeleted(true);
                activity.xmppConnectionService.updateMessage(message, false);
                activity.onConversationsListItemUpdated();
                refresh();
                return;
            }
        }
        activity.xmppConnectionService.resendFailedMessages(message);
        new Handler().post(() -> {
            int size = messageList.size();
            this.messagesView.setSelection(size - 1);
        });
    }

    private void copyUrl(Message message) {
        final String url;
        final int resId;
        if (message.isGeoUri()) {
            resId = R.string.location;
            url = message.getBody();
        } else if (message.isXmppUri()) {
            resId = R.string.contact;
            url = message.getBody();
        } else if (message.hasFileOnRemoteHost()) {
            resId = R.string.file_url;
            url = message.getFileParams().url.toString();
        } else {
            url = message.getBody().trim();
            resId = R.string.file_url;
        }
        if (activity.copyTextToClipboard(url, resId)) {
            Toast.makeText(getActivity(), R.string.url_copied_to_clipboard,
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void cancelTransmission(Message message) {
        Transferable transferable = message.getTransferable();
        if (transferable != null) {
            transferable.cancel();
        } else if (message.getStatus() != Message.STATUS_RECEIVED) {
            activity.xmppConnectionService.markMessage(message, Message.STATUS_SEND_FAILED, Message.ERROR_MESSAGE_CANCELLED);
        }
    }

    private void retryDecryption(Message message) {
        message.setEncryption(Message.ENCRYPTION_PGP);
        activity.onConversationsListItemUpdated();
        refresh();
        conversation.getAccount().getPgpDecryptionService().decrypt(message, false);
    }

    public void privateMessageWith(final Jid counterpart) {
        if (conversation.setOutgoingChatState(mahiti.org.oelp.Config.DEFAULT_CHATSTATE)) {
            activity.xmppConnectionService.sendChatState(conversation);
        }
        this.textinput.setText("");
        this.conversation.setNextCounterpart(counterpart);
        updateSendButton();
        updateEditablity();
    }

    private void correctMessage(Message message) {
        while (message.mergeable(message.next())) {
            message = message.next();
        }
        this.conversation.setCorrectingMessage(message);
        final Editable editable = textinput.getText();
        this.conversation.setDraftMessage(editable.toString());
        this.textinput.setText("");
        this.textinput.append(message.getBody());

    }

    private void highlightInConference(String nick) {
        final Editable editable = this.textinput.getText();
        String oldString = editable.toString().trim();
        final int pos = this.textinput.getSelectionStart();
        if (oldString.isEmpty() || pos == 0) {
            editable.insert(0, nick + ": ");
        } else {
            final char before = editable.charAt(pos - 1);
            final char after = editable.length() > pos ? editable.charAt(pos) : '\0';
            if (before == '\n') {
                editable.insert(pos, nick + ": ");
            } else {
                if (pos > 2 && editable.subSequence(pos - 2, pos).toString().equals(": ")) {
                    if (NickValidityChecker.check(conversation, Arrays.asList(editable.subSequence(0, pos - 2).toString().split(", ")))) {
                        editable.insert(pos - 2, ", " + nick);
                        return;
                    }
                }
                editable.insert(pos, (Character.isWhitespace(before) ? "" : " ") + nick + (Character.isWhitespace(after) ? "" : " "));
                if (Character.isWhitespace(after)) {
                    this.textinput.setSelection(this.textinput.getSelectionStart() + 1);
                }
            }
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        final Activity activity = getActivity();
        if (activity instanceof ConversationsActivity) {
            ((ConversationsActivity) activity).clearPendingViewIntent();
        }
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (conversation != null) {
            outState.putString(STATE_CONVERSATION_UUID, conversation.getUuid());
            outState.putString(STATE_LAST_MESSAGE_UUID, lastMessageUuid);
            final Uri uri = pendingTakePhotoUri.peek();
            if (uri != null) {
                outState.putString(STATE_PHOTO_URI, uri.toString());
            }
            final ScrollState scrollState = getScrollPosition();
            if (scrollState != null) {
                outState.putParcelable(STATE_SCROLL_POSITION, scrollState);
            }
            final ArrayList<Attachment> attachments = mediaPreviewAdapter == null ? new ArrayList<>() : mediaPreviewAdapter.getAttachments();
            if (attachments.size() > 0) {
                outState.putParcelableArrayList(STATE_MEDIA_PREVIEWS, attachments);
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState == null) {
            return;
        }
        String uuid = savedInstanceState.getString(STATE_CONVERSATION_UUID);
        ArrayList<Attachment> attachments = savedInstanceState.getParcelableArrayList(STATE_MEDIA_PREVIEWS);
        pendingLastMessageUuid.push(savedInstanceState.getString(STATE_LAST_MESSAGE_UUID, null));
        if (uuid != null) {
            QuickLoader.set(uuid);
            this.pendingConversationsUuid.push(uuid);
            if (attachments != null && attachments.size() > 0) {
                this.pendingMediaPreviews.push(attachments);
            }
            String takePhotoUri = savedInstanceState.getString(STATE_PHOTO_URI);
            if (takePhotoUri != null) {
                pendingTakePhotoUri.push(Uri.parse(takePhotoUri));
            }
            pendingScrollState.push(savedInstanceState.getParcelable(STATE_SCROLL_POSITION));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (this.reInitRequiredOnStart && this.conversation != null) {
            final Bundle extras = pendingExtras.pop();
            reInit(this.conversation, extras != null);
            if (extras != null) {
                processExtras(extras);
            }
        } else if (conversation == null && activity != null && activity.xmppConnectionService != null) {
            final String uuid = pendingConversationsUuid.pop();
            Log.d(mahiti.org.oelp.Config.LOGTAG, "ConversationFragment.onStart() - activity was bound but no conversation loaded. uuid=" + uuid);
            if (uuid != null) {
                findAndReInitByUuidOrArchive(uuid);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        final Activity activity = getActivity();
        messageListAdapter.unregisterListenerInAudioPlayer();
        if (activity == null || !activity.isChangingConfigurations()) {
            SoftKeyboardUtils.hideSoftKeyboard(activity);
            messageListAdapter.stopAudioPlayer();
        }
        if (this.conversation != null) {
            final String msg = this.textinput.getText().toString();
            storeNextMessage(msg);
            updateChatState(this.conversation, msg);
            this.activity.xmppConnectionService.getNotificationService().setOpenConversation(null);
        }
        this.reInitRequiredOnStart = true;
    }

    private void updateChatState(final Conversation conversation, final String msg) {
        ChatState state = msg.length() == 0 ? mahiti.org.oelp.Config.DEFAULT_CHATSTATE : ChatState.PAUSED;
        Account.State status = conversation.getAccount().getStatus();
        if (status == Account.State.ONLINE && conversation.setOutgoingChatState(state)) {
            activity.xmppConnectionService.sendChatState(conversation);
        }
    }

    private void saveMessageDraftStopAudioPlayer() {
        final Conversation previousConversation = this.conversation;
        if (this.activity == null || this.binding == null || previousConversation == null) {
            return;
        }
        Log.d(mahiti.org.oelp.Config.LOGTAG, "ConversationFragment.saveMessageDraftStopAudioPlayer()");
        final String msg = this.textinput.getText().toString();
        storeNextMessage(msg);
        updateChatState(this.conversation, msg);
        messageListAdapter.stopAudioPlayer();
        mediaPreviewAdapter.clearPreviews();
        toggleInputMethod();
    }

    public void reInit(Conversation conversation, Bundle extras) {
        QuickLoader.set(conversation.getUuid());
        this.saveMessageDraftStopAudioPlayer();
        this.clearPending();
        if (this.reInit(conversation, extras != null)) {
            if (extras != null) {
                processExtras(extras);
            }
            this.reInitRequiredOnStart = false;
        } else {
            this.reInitRequiredOnStart = true;
            pendingExtras.push(extras);
        }
        resetUnreadMessagesCount();
    }

    private void reInit(Conversation conversation) {
        reInit(conversation, false);
    }

    private boolean reInit(final Conversation conversation, final boolean hasExtras) {
        if (conversation == null) {
            return false;
        }
        this.conversation = conversation;
        //once we set the conversation all is good and it will automatically do the right thing in onStart()
        if (this.activity == null || this.binding == null) {
            return false;
        }

        if (!activity.xmppConnectionService.isConversationStillOpen(this.conversation)) {
            activity.onConversationArchived(this.conversation);
            return false;
        }

        stopScrolling();
        Log.d(mahiti.org.oelp.Config.LOGTAG, "reInit(hasExtras=" + Boolean.toString(hasExtras) + ")");

        if (this.conversation.isRead() && hasExtras) {
            Log.d(mahiti.org.oelp.Config.LOGTAG, "trimming conversation");
            this.conversation.trim();
        }

        setupIme();

        final boolean scrolledToBottomAndNoPending = this.scrolledToBottom() && pendingScrollState.peek() == null;

        this.textSendButton.setContentDescription(activity.getString(R.string.send_message_to_x, conversation.getName()));
        this.textinput.setKeyboardListener(null);
        this.textinput.setText("");
        showRecordVoiceButton();
        final boolean participating = conversation.getMode() == Conversational.MODE_SINGLE || conversation.getMucOptions().participating();
        if (participating) {
            this.textinput.append(this.conversation.getNextMessage());
        }
        this.textinput.setKeyboardListener(this);
        messageListAdapter.updatePreferences();
        refresh(false);
        this.conversation.messagesLoaded.set(true);

        Log.d(mahiti.org.oelp.Config.LOGTAG, "scrolledToBottomAndNoPending=" + Boolean.toString(scrolledToBottomAndNoPending));

        if (hasExtras || scrolledToBottomAndNoPending) {
            resetUnreadMessagesCount();
            synchronized (this.messageList) {
                Log.d(mahiti.org.oelp.Config.LOGTAG, "jump to first unread message");
                final Message first = conversation.getFirstUnreadMessage();
                final int bottom = Math.max(0, this.messageList.size() - 1);
                final int pos;
                final boolean jumpToBottom;
                if (first == null) {
                    pos = bottom;
                    jumpToBottom = true;
                } else {
                    int i = getIndexOf(first.getUuid(), this.messageList);
                    pos = i < 0 ? bottom : i;
                    jumpToBottom = false;
                }
                setSelection(pos, jumpToBottom);
            }
        }

        this.messagesView.post(this::fireReadEvent);
        //TODO if we only do this when this fragment is running on main it won't *bing* in tablet layout which might be unnecessary since we can *see* it
        activity.xmppConnectionService.getNotificationService().setOpenConversation(this.conversation);
        return true;
    }

    private void resetUnreadMessagesCount() {
        lastMessageUuid = null;
        hideUnreadMessagesCount();
    }

    private void hideUnreadMessagesCount() {
        if (this.binding == null) {
            return;
        }
        this.scrollToBottomButton.setEnabled(false);
        this.scrollToBottomButton.hide();
        this.unreadCountCustomView.setVisibility(View.GONE);
    }

    private void setSelection(int pos, boolean jumpToBottom) {
        ListViewUtils.setSelection(this.messagesView, pos, jumpToBottom);
        this.messagesView.post(() -> ListViewUtils.setSelection(this.messagesView, pos, jumpToBottom));
        this.messagesView.post(this::fireReadEvent);
    }

    private boolean scrolledToBottom() {
        return this.binding != null && scrolledToBottom(this.messagesView);
    }

    private void processExtras(Bundle extras) {
        final String downloadUuid = extras.getString(ConversationsActivity.EXTRA_DOWNLOAD_UUID);
        final String text = extras.getString(Intent.EXTRA_TEXT);
        final String nick = extras.getString(ConversationsActivity.EXTRA_NICK);
        final boolean asQuote = extras.getBoolean(ConversationsActivity.EXTRA_AS_QUOTE);
        final String user = extras.getString(ConversationsActivity.EXTRA_USER);
        final boolean pm = extras.getBoolean(ConversationsActivity.EXTRA_IS_PRIVATE_MESSAGE, false);
        final boolean doNotAppend = extras.getBoolean(ConversationsActivity.EXTRA_DO_NOT_APPEND, false);
        final List<Uri> uris = extractUris(extras);
        if (uris != null && uris.size() > 0) {
            if (uris.size() == 1 && "geo".equals(uris.get(0).getScheme())) {
                mediaPreviewAdapter.addMediaPreviews(Attachment.of(getActivity(), uris.get(0), Attachment.Type.LOCATION));
            } else {
                final List<Uri> cleanedUris = cleanUris(new ArrayList<>(uris));
                mediaPreviewAdapter.addMediaPreviews(Attachment.of(getActivity(), cleanedUris));
            }
            toggleInputMethod();
            return;
        }
        if (nick != null) {
            if (pm) {
                Jid jid = conversation.getJid();
                try {
                    Jid next = Jid.of(jid.getLocal(), jid.getDomain(), nick);
                    privateMessageWith(next);
                } catch (final IllegalArgumentException ignored) {
                    //do nothing
                }
            } else {
                final MucOptions mucOptions = conversation.getMucOptions();
                if (mucOptions.participating() || conversation.getNextCounterpart() != null) {
                    highlightInConference(nick);
                }
            }
        } else {
            if (text != null && GeoHelper.GEO_URI.matcher(text).matches()) {
                mediaPreviewAdapter.addMediaPreviews(Attachment.of(getActivity(), Uri.parse(text), Attachment.Type.LOCATION));
                toggleInputMethod();
                return;
            } else if (text != null && asQuote) {
                quoteText(text, user);
            } else {
                appendText(text, doNotAppend);
            }
        }
        final Message message = downloadUuid == null ? null : conversation.findMessageWithFileAndUuid(downloadUuid);
        if (message != null) {
            startDownloadable(message);
        }
    }

    private List<Uri> extractUris(Bundle extras) {
        final List<Uri> uris = extras.getParcelableArrayList(Intent.EXTRA_STREAM);
        if (uris != null) {
            return uris;
        }
        final Uri uri = extras.getParcelable(Intent.EXTRA_STREAM);
        if (uri != null) {
            return Collections.singletonList(uri);
        } else {
            return null;
        }
    }

    private List<Uri> cleanUris(List<Uri> uris) {
        Iterator<Uri> iterator = uris.iterator();
        while (iterator.hasNext()) {
            final Uri uri = iterator.next();
            if (FileBackend.weOwnFile(getActivity(), uri)) {
                iterator.remove();
                Toast.makeText(getActivity(), R.string.security_violation_not_attaching_file, Toast.LENGTH_SHORT).show();
            }
        }
        return uris;
    }

    private boolean showBlockSubmenu(View view) {
        final Jid jid = conversation.getJid();
        final boolean showReject = !conversation.isWithStranger() && conversation.getContact().getOption(Contact.Options.PENDING_SUBSCRIPTION_REQUEST);
        PopupMenu popupMenu = new PopupMenu(getActivity(), view);
        popupMenu.inflate(R.menu.block);
        popupMenu.getMenu().findItem(R.id.block_contact).setVisible(jid.getLocal() != null);
        popupMenu.getMenu().findItem(R.id.reject).setVisible(showReject);
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            Blockable blockable;
            switch (menuItem.getItemId()) {
                case R.id.reject:
                    activity.xmppConnectionService.stopPresenceUpdatesTo(conversation.getContact());
                    updateSnackBar(conversation);
                    return true;
                case R.id.block_domain:
                    blockable = conversation.getAccount().getRoster().getContact(Jid.ofDomain(jid.getDomain()));
                    break;
                default:
                    blockable = conversation;
            }
            BlockContactDialog.show(activity, blockable);
            return true;
        });
        popupMenu.show();
        return true;
    }

    private void updateSnackBar(final Conversation conversation) {
        final Account account = conversation.getAccount();
        final XmppConnection connection = account.getXmppConnection();
        final int mode = conversation.getMode();
        final Contact contact = mode == Conversation.MODE_SINGLE ? conversation.getContact() : null;
        if (conversation.getStatus() == Conversation.STATUS_ARCHIVED) {
            return;
        }
        if (account.getStatus() == Account.State.DISABLED) {
            showSnackbar(R.string.this_account_is_disabled, R.string.enable, this.mEnableAccountListener);
        } else if (conversation.isBlocked()) {
            showSnackbar(R.string.contact_blocked, R.string.unblock, this.mUnblockClickListener);
        } else if (contact != null && !contact.showInRoster() && contact.getOption(Contact.Options.PENDING_SUBSCRIPTION_REQUEST)) {
            showSnackbar(R.string.contact_added_you, R.string.add_back, this.mAddBackClickListener, this.mLongPressBlockListener);
        } else if (contact != null && contact.getOption(Contact.Options.PENDING_SUBSCRIPTION_REQUEST)) {
            showSnackbar(R.string.contact_asks_for_presence_subscription, R.string.allow, this.mAllowPresenceSubscription, this.mLongPressBlockListener);
        } else if (mode == Conversation.MODE_MULTI
                && !conversation.getMucOptions().online()
                && account.getStatus() == Account.State.ONLINE) {
            switch (conversation.getMucOptions().getError()) {
                case NICK_IN_USE:
                    showSnackbar(R.string.nick_in_use, R.string.edit, clickToMuc);
                    break;
                case NO_RESPONSE:
                    showSnackbar(R.string.joining_conference, 0, null);
                    break;
                case SERVER_NOT_FOUND:
                    if (conversation.receivedMessagesCount() > 0) {
                        showSnackbar(R.string.remote_server_not_found, R.string.try_again, joinMuc);
                    } else {
                        showSnackbar(R.string.remote_server_not_found, R.string.leave, leaveMuc);
                    }
                    break;
                case REMOTE_SERVER_TIMEOUT:
                    if (conversation.receivedMessagesCount() > 0) {
                        showSnackbar(R.string.remote_server_timeout, R.string.try_again, joinMuc);
                    } else {
                        showSnackbar(R.string.remote_server_timeout, R.string.leave, leaveMuc);
                    }
                    break;
                case PASSWORD_REQUIRED:
                    showSnackbar(R.string.conference_requires_password, R.string.enter_password, enterPassword);
                    break;
                case BANNED:
                    showSnackbar(R.string.conference_banned, R.string.leave, leaveMuc);
                    break;
                case MEMBERS_ONLY:
                    showSnackbar(R.string.conference_members_only, R.string.leave, leaveMuc);
                    break;
                case RESOURCE_CONSTRAINT:
                    showSnackbar(R.string.conference_resource_constraint, R.string.try_again, joinMuc);
                    break;
                case KICKED:
                    showSnackbar(R.string.conference_kicked, R.string.join, joinMuc);
                    break;
                case UNKNOWN:
                    showSnackbar(R.string.conference_unknown_error, R.string.join, joinMuc);
                    break;
                case INVALID_NICK:
                    showSnackbar(R.string.invalid_muc_nick, R.string.edit, clickToMuc);
                case SHUTDOWN:
                    showSnackbar(R.string.conference_shutdown, R.string.try_again, joinMuc);
                    break;
                case DESTROYED:
                    showSnackbar(R.string.conference_destroyed, R.string.leave, leaveMuc);
                    break;
                case NON_ANONYMOUS:
                    showSnackbar(R.string.group_chat_will_make_your_jabber_id_public, R.string.join, acceptJoin);
                    break;
                default:
                    hideSnackbar();
                    break;
            }
        } else if ((mode == Conversation.MODE_MULTI
                && !conversation.getMucOptions().participating())) {
            showSnackbar(R.string.no_write_access_in_public_muc, R.string.ok, clickToMuc);
        } else if (account.hasPendingPgpIntent(conversation)) {
            showSnackbar(R.string.openpgp_messages_found, R.string.decrypt, clickToDecryptListener);
        } else if (mode == Conversation.MODE_SINGLE
                && conversation.smpRequested()) {
            showSnackbar(R.string.smp_requested, R.string.verify, this.mAnswerSmpClickListener);
        } else if (mode == Conversation.MODE_SINGLE
                && conversation.hasValidOtrSession()
                && (conversation.getOtrSession().getSessionStatus() == SessionStatus.ENCRYPTED)
                && (!conversation.isOtrFingerprintVerified())) {
            showSnackbar(R.string.unknown_otr_fingerprint, R.string.verify, clickToVerify);
        } else if (connection != null
                && connection.getFeatures().blocking()
                && conversation.countMessages() != 0
                && !conversation.isBlocked()
                && conversation.isWithStranger()) {
            showSnackbar(R.string.received_message_from_stranger, R.string.block, mBlockClickListener);
        } else if (activity.xmppConnectionService.warnUnecryptedChat()) {
            if (conversation.getNextEncryption() == Message.ENCRYPTION_NONE && conversation.isSingleOrPrivateAndNonAnonymous() && ((mahiti.org.oelp.Config.supportOmemo() && Conversation.suitableForOmemoByDefault(conversation)) ||
                    (mahiti.org.oelp.Config.supportOpenPgp() && account.isPgpDecryptionServiceConnected()) || (
                    mode == Conversation.MODE_SINGLE && mahiti.org.oelp.Config.supportOtr()))) {
                if (Patches.ENCRYPTION_EXCEPTIONS.contains(conversation.getJid().toString()) || conversation.getJid().toString().equals(account.getJid().getDomain())) {
                    hideSnackbar();
                } else {
                    showSnackbar(R.string.conversation_unencrypted_hint, R.string.ok, showUnencryptionHintDialog);
                }
            } else {
                hideSnackbar();
            }
        } else {
            hideSnackbar();
        }
    }

    private OnClickListener showUnencryptionHintDialog = new OnClickListener() {
        @Override
        public void onClick(View v) {
            activity.runOnUiThread(() -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getString(R.string.message_encryption));
                builder.setMessage(getString(R.string.enable_message_encryption));
                builder.setNegativeButton(getString(R.string.cancel), null);
                builder.setPositiveButton(getString(R.string.enable),
                        (dialog, which) -> {
                            enableMessageEncryption();
                        });
                builder.setNeutralButton(getString(R.string.hide_warning),
                        (dialog, which) -> {
                            SharedPreferences preferences = activity.getPreferences();
                            preferences.edit().putBoolean(WARN_UNENCRYPTED_CHAT, false).apply();
                            hideSnackbar();
                        });
                builder.create().show();
            });
        }
    };

    @Override
    public void refresh() {
        if (this.binding == null) {
            Log.d(mahiti.org.oelp.Config.LOGTAG, "ConversationFragment.refresh() skipped updated because view binding was null");
            return;
        }
        if (this.conversation != null && this.activity != null && this.activity.xmppConnectionService != null) {
            if (!activity.xmppConnectionService.isConversationStillOpen(this.conversation)) {
                activity.onConversationArchived(this.conversation);
                return;
            }
        }
        this.refresh(true);
    }


    private void refresh(boolean notifyConversationRead) {
        synchronized (this.messageList) {
            if (this.conversation != null) {
                conversation.populateWithMessages(ConversationFragment.this.messageList);
                updateSnackBar(conversation);
                updateStatusMessages();
                if (conversation.getReceivedMessagesCountSinceUuid(lastMessageUuid) != 0) {
                    unreadCountCustomView.setVisibility(View.VISIBLE);
                    unreadCountCustomView.setUnreadCount(conversation.getReceivedMessagesCountSinceUuid(lastMessageUuid));
                }
                this.messageListAdapter.notifyDataSetChanged();
                updateChatMsgHint();
                if (notifyConversationRead && activity != null) {
                    messagesView.post(this::fireReadEvent);
                }
                updateSendButton();
                updateEditablity();
                activity.invalidateOptionsMenu();
            }
        }
    }

    protected void messageSent() {
        mSendingPgpMessage.set(false);
        this.textinput.setText("");
        if (conversation.setCorrectingMessage(null)) {
            this.textinput.append(conversation.getDraftMessage());
            conversation.setDraftMessage(null);
        }
        storeNextMessage();
        updateChatMsgHint();
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(activity);
        final boolean prefScrollToBottom = p.getBoolean("scroll_to_bottom", activity.getResources().getBoolean(R.bool.scroll_to_bottom));
        if (prefScrollToBottom || scrolledToBottom()) {
            new Handler().post(() -> {
                int size = messageList.size();
                this.messagesView.setSelection(size - 1);
            });
        }
    }

    private boolean storeNextMessage() {
        return storeNextMessage(this.textinput.getText().toString());
    }

    private boolean storeNextMessage(String msg) {
        final boolean participating = conversation.getMode() == Conversational.MODE_SINGLE || conversation.getMucOptions().participating();
        if (this.conversation.getStatus() != Conversation.STATUS_ARCHIVED && participating && this.conversation.setNextMessage(msg)) {
            this.activity.xmppConnectionService.updateConversation(this.conversation);
            return true;
        }
        return false;
    }

    public void doneSendingPgpMessage() {
        mSendingPgpMessage.set(false);
    }

    public long getMaxHttpUploadSize(Conversation conversation) {
        final XmppConnection connection = conversation.getAccount().getXmppConnection();
        return connection == null ? -1 : connection.getFeatures().getMaxHttpUploadSize();
    }

    private void updateEditablity() {
        boolean canWrite = this.conversation.getMode() == Conversation.MODE_SINGLE || this.conversation.getMucOptions().participating() || this.conversation.getNextCounterpart() != null;
        this.textinput.setFocusable(canWrite);
        this.textinput.setFocusableInTouchMode(canWrite);
        this.textSendButton.setEnabled(canWrite);
        this.textinput.setCursorVisible(canWrite);
        this.textinput.setEnabled(canWrite);
    }

    public void updateSendButton() {
        updateChatMsgHint();
        boolean hasAttachments = mediaPreviewAdapter != null && mediaPreviewAdapter.hasAttachments();
        boolean useSendButtonToIndicateStatus = activity != null && PreferenceManager.getDefaultSharedPreferences(activity).getBoolean("send_button_status", getResources().getBoolean(R.bool.send_button_status));
        final Conversation c = this.conversation;
        final Presence.Status status;
        final String text = this.textinput == null ? "" : this.textinput.getText().toString();
        final SendButtonAction action;
        if (hasAttachments) {
            action = SendButtonAction.TEXT;
        } else {
            action = SendButtonTool.getAction(getActivity(), c, text);
        }
        if (useSendButtonToIndicateStatus && c.getAccount().getStatus() == Account.State.ONLINE) {
            if (activity != null && activity.xmppConnectionService != null && activity.xmppConnectionService.getMessageArchiveService().isCatchingUp(c)) {
                status = Presence.Status.OFFLINE;
            } else if (c.getMode() == Conversation.MODE_SINGLE) {
                status = c.getContact().getShownStatus();
            } else {
                status = c.getMucOptions().online() ? Presence.Status.ONLINE : Presence.Status.OFFLINE;
            }
        } else {
            status = Presence.Status.OFFLINE;
        }
        this.textSendButton.setTag(action);
        final Activity activity = getActivity();
//        if (activity != null) {
//            this.textSendButton.setImageResource(SendButtonTool.getSendButtonImageResource(activity, action, status));
//        }
    }

    protected void updateStatusMessages() {
        DateSeparator.addAll(this.messageList);
        if (showLoadMoreMessages(conversation)) {
            this.messageList.add(0, Message.createLoadMoreMessage(conversation));
        }
        if (conversation.getMode() == Conversation.MODE_MULTI) {
            final MucOptions mucOptions = conversation.getMucOptions();
            final List<MucOptions.User> allUsers = mucOptions.getUsers();
            final Set<ReadByMarker> addedMarkers = new HashSet<>();
            ChatState state = ChatState.COMPOSING;
            List<MucOptions.User> users = conversation.getMucOptions().getUsersWithChatState(state, 5);
            if (users.size() == 0) {
                state = ChatState.PAUSED;
                users = conversation.getMucOptions().getUsersWithChatState(state, 5);
            }
            if (mucOptions.isPrivateAndNonAnonymous()) {
                for (int i = this.messageList.size() - 1; i >= 0; --i) {
                    final Set<ReadByMarker> markersForMessage = messageList.get(i).getReadByMarkers();
                    final List<MucOptions.User> shownMarkers = new ArrayList<>();
                    for (ReadByMarker marker : markersForMessage) {
                        if (!ReadByMarker.contains(marker, addedMarkers)) {
                            addedMarkers.add(marker); //may be put outside this condition. set should do dedup anyway
                            MucOptions.User user = mucOptions.findUser(marker);
                            if (user != null && !users.contains(user)) {
                                shownMarkers.add(user);
                            }
                        }
                    }
                    final ReadByMarker markerForSender = ReadByMarker.from(messageList.get(i));
                    final Message statusMessage;
                    final int size = shownMarkers.size();
                    if (size > 1) {
                        final String body;
                        if (size <= 4) {
                            body = getString(R.string.contacts_have_read_up_to_this_point, UIHelper.concatNames(shownMarkers));
                        } else if (ReadByMarker.allUsersRepresented(allUsers, markersForMessage, markerForSender)) {
                            body = getString(R.string.everyone_has_read_up_to_this_point);
                        } else {
                            body = getString(R.string.contacts_and_n_more_have_read_up_to_this_point, UIHelper.concatNames(shownMarkers, 3), size - 3);
                        }
                        statusMessage = Message.createStatusMessage(conversation, body);
                        statusMessage.setCounterparts(shownMarkers);
                    } else if (size == 1) {
                        statusMessage = Message.createStatusMessage(conversation, getString(R.string.contact_has_read_up_to_this_point, UIHelper.getDisplayName(shownMarkers.get(0))));
                        statusMessage.setCounterpart(shownMarkers.get(0).getFullJid());
                        statusMessage.setTrueCounterpart(shownMarkers.get(0).getRealJid());
                    } else {
                        statusMessage = null;
                    }
                    if (statusMessage != null) {
                        this.messageList.add(i + 1, statusMessage);
                    }
                    addedMarkers.add(markerForSender);
                    if (ReadByMarker.allUsersRepresented(allUsers, addedMarkers)) {
                        break;
                    }
                }
            }
        }
    }


    private void stopScrolling() {
        long now = SystemClock.uptimeMillis();
        MotionEvent cancel = MotionEvent.obtain(now, now, MotionEvent.ACTION_CANCEL, 0, 0, 0);
        messagesView.dispatchTouchEvent(cancel);
    }

    private boolean showLoadMoreMessages(final Conversation c) {
        if (activity == null || activity.xmppConnectionService == null) {
            return false;
        }
        final boolean mam = hasMamSupport(c) && !c.getContact().isBlocked();
        final MessageArchiveService service = activity.xmppConnectionService.getMessageArchiveService();
        return mam && (c.getLastClearHistory().getTimestamp() != 0 || (c.countMessages() == 0 && c.messagesLoaded.get() && c.hasMessagesLeftOnServer() && !service.queryInProgress(c)));
    }

    private boolean hasMamSupport(final Conversation c) {
        if (c.getMode() == Conversation.MODE_SINGLE) {
            final XmppConnection connection = c.getAccount().getXmppConnection();
            return connection != null && connection.getFeatures().mam();
        } else {
            return c.getMucOptions().mamSupport();
        }
    }

    protected void showSnackbar(final int message, final int action, final OnClickListener clickListener) {
        showSnackbar(message, action, clickListener, null);
    }

    protected void showSnackbar(final int message, final int action, final OnClickListener clickListener, final View.OnLongClickListener longClickListener) {
        this.snackbar.setVisibility(View.VISIBLE);
        this.snackbar.setOnClickListener(null);
        this.snackbarMessage.setText(message);
        this.snackbarMessage.setOnClickListener(null);
        this.snackbarAction.setVisibility(clickListener == null ? View.GONE : View.VISIBLE);
        if (action != 0) {
            this.snackbarAction.setText(action);
        }
        this.snackbarAction.setOnClickListener(clickListener);
        this.snackbarAction.setOnLongClickListener(longClickListener);
    }

    protected void hideSnackbar() {
        this.snackbar.setVisibility(View.GONE);
    }

    protected void sendMessage(Message message) {
        activity.xmppConnectionService.sendMessage(message);
        messageSent();
    }

    protected void sendPgpMessage(final Message message) {
        final XmppConnectionService xmppService = activity.xmppConnectionService;
        final Contact contact = message.getConversation().getContact();
        if (!activity.hasPgp()) {
            activity.showInstallPgpDialog();
            return;
        }
        if (conversation.getAccount().getPgpSignature() == null) {
            activity.announcePgp(conversation.getAccount(), conversation, null, activity.onOpenPGPKeyPublished);
            return;
        }
        if (!mSendingPgpMessage.compareAndSet(false, true)) {
            Log.d(mahiti.org.oelp.Config.LOGTAG, "sending pgp message already in progress");
        }
        if (conversation.getMode() == Conversation.MODE_SINGLE) {
            if (contact.getPgpKeyId() != 0) {
                xmppService.getPgpEngine().hasKey(contact,
                        new UiCallback<Contact>() {

                            @Override
                            public void userInputRequired(PendingIntent pi, Contact contact) {
                                startPendingIntent(pi, REQUEST_ENCRYPT_MESSAGE);
                            }

                            @Override
                            public void success(Contact contact) {
                                encryptTextMessage(message);
                            }

                            @Override
                            public void error(int error, Contact contact) {
                                activity.runOnUiThread(() -> Toast.makeText(activity,
                                        R.string.unable_to_connect_to_keychain,
                                        Toast.LENGTH_SHORT
                                ).show());
                                mSendingPgpMessage.set(false);
                            }
                        });

            } else {
                showNoPGPKeyDialog(false,
                        (dialog, which) -> {
                            conversation.setNextEncryption(Message.ENCRYPTION_NONE);
                            xmppService.updateConversation(conversation);
                            message.setEncryption(Message.ENCRYPTION_NONE);
                            xmppService.sendMessage(message);
                            messageSent();
                        });
            }
        } else {
            if (conversation.getMucOptions().pgpKeysInUse()) {
                if (!conversation.getMucOptions().everybodyHasKeys()) {
                    Toast warning = Toast
                            .makeText(getActivity(),
                                    R.string.missing_public_keys,
                                    Toast.LENGTH_LONG);
                    warning.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    warning.show();
                }
                encryptTextMessage(message);
            } else {
                showNoPGPKeyDialog(true,
                        (dialog, which) -> {
                            conversation.setNextEncryption(Message.ENCRYPTION_NONE);
                            message.setEncryption(Message.ENCRYPTION_NONE);
                            xmppService.updateConversation(conversation);
                            xmppService.sendMessage(message);
                            messageSent();
                        });
            }
        }
    }

    public void encryptTextMessage(Message message) {
        activity.xmppConnectionService.getPgpEngine().encrypt(message,
                new UiCallback<Message>() {

                    @Override
                    public void userInputRequired(PendingIntent pi, Message message) {
                        startPendingIntent(pi, REQUEST_SEND_MESSAGE);
                    }

                    @Override
                    public void success(Message message) {
                        //TODO the following two call can be made before the callback
                        getActivity().runOnUiThread(() -> messageSent());
                    }

                    @Override
                    public void error(final int error, Message message) {
                        getActivity().runOnUiThread(() -> {
                            doneSendingPgpMessage();
                            Toast.makeText(getActivity(), error == 0 ? R.string.unable_to_connect_to_keychain : error, Toast.LENGTH_SHORT).show();
                        });

                    }
                });
    }

    public void showNoPGPKeyDialog(boolean plural, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIconAttribute(android.R.attr.alertDialogIcon);
        if (plural) {
            builder.setTitle(getString(R.string.no_pgp_keys));
            builder.setMessage(getText(R.string.contacts_have_no_pgp_keys));
        } else {
            builder.setTitle(getString(R.string.no_pgp_key));
            builder.setMessage(getText(R.string.contact_has_no_pgp_key));
        }
        builder.setNegativeButton(getString(R.string.cancel), null);
        builder.setPositiveButton(getString(R.string.send_unencrypted), listener);
        builder.create().show();
    }

    protected void sendOtrMessage(final Message message) {
        final ConversationsActivity activity = (ConversationsActivity) getActivity();
        final XmppConnectionService xmppService = activity.xmppConnectionService;
        activity.selectPresence(conversation,
                () -> {
                    message.setCounterpart(conversation.getNextCounterpart());
                    xmppService.sendMessage(message);
                    messageSent();
                });
    }

    public void appendText(String text, final boolean doNotAppend) {
        if (text == null) {
            return;
        }
        final Editable editable = this.textinput.getText();
        String previous = editable == null ? "" : editable.toString();
        if (doNotAppend && !TextUtils.isEmpty(previous)) {
            Toast.makeText(getActivity(), R.string.already_drafting_message, Toast.LENGTH_LONG).show();
            return;
        }
        if (UIHelper.isLastLineQuote(previous)) {
            text = '\n' + text;
        } else if (previous.length() != 0 && !Character.isWhitespace(previous.charAt(previous.length() - 1))) {
            text = " " + text;
        }
        this.textinput.append(text);
    }

    @Override
    public boolean onEnterPressed() {
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final boolean enterIsSend = p.getBoolean("enter_is_send", getResources().getBoolean(R.bool.enter_is_send));
        if (enterIsSend) {
            sendMessage();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onTypingStarted() {
        final XmppConnectionService service = activity == null ? null : activity.xmppConnectionService;
        if (service == null) {
            return;
        }
        final boolean broadcastLastActivity = activity.xmppConnectionService.broadcastLastActivity();
        Account.State status = conversation.getAccount().getStatus();
        if (status == Account.State.ONLINE && conversation.setOutgoingChatState(ChatState.COMPOSING)) {
            service.sendChatState(conversation);
        }
        if (broadcastLastActivity) {
            //service.sendPresence(conversation.getAccount(), false); //send new presence but don't include idle because we are not
        }
        runOnUiThread(this::updateSendButton);
    }

    @Override
    public void onTypingStopped() {
        final XmppConnectionService service = activity == null ? null : activity.xmppConnectionService;
        if (service == null) {
            return;
        }
        Account.State status = conversation.getAccount().getStatus();
        if (status == Account.State.ONLINE && conversation.setOutgoingChatState(ChatState.PAUSED)) {
            service.sendChatState(conversation);
        }
    }

    @Override
    public void onTextDeleted() {
        final XmppConnectionService service = activity == null ? null : activity.xmppConnectionService;
        if (service == null) {
            return;
        }
        Account.State status = conversation.getAccount().getStatus();
        if (status == Account.State.ONLINE && conversation.setOutgoingChatState(mahiti.org.oelp.Config.DEFAULT_CHATSTATE)) {
            service.sendChatState(conversation);
        }
        runOnUiThread(() -> {
            if (activity == null) {
                return;
            }
            activity.onConversationsListItemUpdated();
        });
        runOnUiThread(this::updateSendButton);
    }

    @Override
    public void onTextChanged() {
        if (conversation != null && conversation.getCorrectingMessage() != null) {
            runOnUiThread(this::updateSendButton);
        }
    }

    @Override
    public boolean onTabPressed(boolean repeated) {
        if (conversation == null || conversation.getMode() == Conversation.MODE_SINGLE) {
            return false;
        }
        if (repeated) {
            completionIndex++;
        } else {
            lastCompletionLength = 0;
            completionIndex = 0;
            final String content = this.textinput.getText().toString();
            lastCompletionCursor = this.textinput.getSelectionEnd();
            int start = lastCompletionCursor > 0 ? content.lastIndexOf(" ", lastCompletionCursor - 1) + 1 : 0;
            firstWord = start == 0;
            incomplete = content.substring(start, lastCompletionCursor);
        }
        List<String> completions = new ArrayList<>();
        for (MucOptions.User user : conversation.getMucOptions().getUsers()) {
            String name = user.getName();
            if (name != null && name.startsWith(incomplete)) {
                completions.add(name + (firstWord ? ": " : " "));
            }
        }
        Collections.sort(completions);
        if (completions.size() > completionIndex) {
            String completion = completions.get(completionIndex).substring(incomplete.length());
            this.textinput.getEditableText().delete(lastCompletionCursor, lastCompletionCursor + lastCompletionLength);
            this.textinput.getEditableText().insert(lastCompletionCursor, completion);
            lastCompletionLength = completion.length();
        } else {
            completionIndex = -1;
            this.textinput.getEditableText().delete(lastCompletionCursor, lastCompletionCursor + lastCompletionLength);
            lastCompletionLength = 0;
        }
        return true;
    }

    private boolean messageContainsQuery(Message m, String q) {
        return m != null && m.getMergedBody().toString().toLowerCase().contains(q.toLowerCase());
    }

    private void startPendingIntent(PendingIntent pendingIntent, int requestCode) {
        try {
            getActivity().startIntentSenderForResult(pendingIntent.getIntentSender(), requestCode, null, 0, 0, 0);
        } catch (final SendIntentException ignored) {
        }
    }

    @Override
    public void onBackendConnected() {
        Log.d(mahiti.org.oelp.Config.LOGTAG, "ConversationFragment.onBackendConnected()");
        String uuid = pendingConversationsUuid.pop();
        if (uuid != null) {
            if (!findAndReInitByUuidOrArchive(uuid)) {
                return;
            }
        } else {
            if (!activity.xmppConnectionService.isConversationStillOpen(conversation)) {
                clearPending();
                activity.onConversationArchived(conversation);
                return;
            }
        }
        ActivityResult activityResult = postponedActivityResult.pop();
        if (activityResult != null) {
            handleActivityResult(activityResult);
        }
        clearPending();
    }

    private boolean findAndReInitByUuidOrArchive(@NonNull final String uuid) {
        Conversation conversation = activity.xmppConnectionService.findConversationByUuid(uuid);
        if (conversation == null) {
            clearPending();
            activity.onConversationArchived(null);
            return false;
        }
        reInit(conversation);
        ScrollState scrollState = pendingScrollState.pop();
        String lastMessageUuid = pendingLastMessageUuid.pop();
        List<Attachment> attachments = pendingMediaPreviews.pop();
        if (scrollState != null) {
            setScrollPosition(scrollState, lastMessageUuid);
        }
        if (attachments != null && attachments.size() > 0) {
            Log.d(mahiti.org.oelp.Config.LOGTAG, "had attachments on restore");
            mediaPreviewAdapter.addMediaPreviews(attachments);
            toggleInputMethod();
        }
        return true;
    }

    private void clearPending() {
        if (postponedActivityResult.clear()) {
            Log.e(mahiti.org.oelp.Config.LOGTAG, "cleared pending intent with unhandled result left");
        }
        if (pendingScrollState.clear()) {
            Log.e(mahiti.org.oelp.Config.LOGTAG, "cleared scroll state");
        }
        if (pendingTakePhotoUri.clear()) {
            Log.e(mahiti.org.oelp.Config.LOGTAG, "cleared pending photo uri");
        }
        if (pendingConversationsUuid.clear()) {
            Log.e(mahiti.org.oelp.Config.LOGTAG, "cleared pending conversations uuid");
        }
        if (pendingMediaPreviews.clear()) {
            Log.e(Config.LOGTAG, "cleared pending media previews");
        }
    }

    private int messageInputBubble() {
        return activity.isDarkTheme() ? R.drawable.message_bubble_sent_dark : R.drawable.message_bubble_sent;
    }

    public Conversation getConversation() {
        return conversation;
    }

    @Override
    public void onContactPictureLongClicked(View v, final Message message) {
        final String fingerprint;
        if (message.getEncryption() == Message.ENCRYPTION_PGP || message.getEncryption() == Message.ENCRYPTION_DECRYPTED) {
            fingerprint = "pgp";
        } else {
            fingerprint = message.getFingerprint();
        }
        final PopupMenu popupMenu = new PopupMenu(getActivity(), v);
        final Contact contact = message.getContact();
        if (message.getStatus() <= Message.STATUS_RECEIVED && (contact == null || !contact.isSelf())) {
            if (message.getConversation().getMode() == Conversation.MODE_MULTI) {
                final Jid cp = message.getCounterpart();
                if (cp == null || cp.isBareJid()) {
                    return;
                }
                final Jid tcp = message.getTrueCounterpart();
                final MucOptions.User userByRealJid = tcp != null ? conversation.getMucOptions().findOrCreateUserByRealJid(tcp, cp) : null;
                final MucOptions.User user = userByRealJid != null ? userByRealJid : conversation.getMucOptions().findUserByFullJid(cp);
                popupMenu.inflate(R.menu.muc_details_context);
                final Menu menu = popupMenu.getMenu();
                MucDetailsContextMenuHelper.configureMucDetailsContextMenu(activity, menu, conversation, user);
                popupMenu.setOnMenuItemClickListener(menuItem -> MucDetailsContextMenuHelper.onContextItemSelected(menuItem, user, activity, fingerprint));
            } else {
                popupMenu.inflate(R.menu.one_on_one_context);
                popupMenu.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.action_contact_details:
                            activity.switchToContactDetails(message.getContact(), fingerprint);
                            break;
                        case R.id.action_show_qr_code:
                            activity.showQrCode("xmpp:" + message.getContact().getJid().asBareJid().toEscapedString());
                            break;
                    }
                    return true;
                });
            }
        } else {
            popupMenu.inflate(R.menu.account_context);
            final Menu menu = popupMenu.getMenu();
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.action_show_qr_code:
                        activity.showQrCode(conversation.getAccount().getShareableUri());
                        break;
                    case R.id.action_account_details:
                        activity.switchToAccount(message.getConversation().getAccount(), fingerprint);
                        break;
                }
                return true;
            });
        }
        popupMenu.show();
    }

    @Override
    public void onContactPictureClicked(Message message) {
        String fingerprint;
        if (message.getEncryption() == Message.ENCRYPTION_PGP || message.getEncryption() == Message.ENCRYPTION_DECRYPTED) {
            fingerprint = "pgp";
        } else {
            fingerprint = message.getFingerprint();
        }
        final boolean received = message.getStatus() <= Message.STATUS_RECEIVED;
        if (received) {
            if (message.getConversation() instanceof Conversation && message.getConversation().getMode() == Conversation.MODE_MULTI) {
                Jid tcp = message.getTrueCounterpart();
                Jid user = message.getCounterpart();
                if (user != null && !user.isBareJid()) {
                    final MucOptions mucOptions = ((Conversation) message.getConversation()).getMucOptions();
                    if (mucOptions.participating() || ((Conversation) message.getConversation()).getNextCounterpart() != null) {
                        if (!mucOptions.isUserInRoom(user) && mucOptions.findUserByRealJid(tcp == null ? null : tcp.asBareJid()) == null) {
                            Toast.makeText(getActivity(), activity.getString(R.string.user_has_left_conference, user.getResource()), Toast.LENGTH_SHORT).show();
                        }
                        highlightInConference(user.getResource());
                    } else {
                        Toast.makeText(getActivity(), R.string.you_are_not_participating, Toast.LENGTH_SHORT).show();
                    }
                }
                return;
            } else {
                if (!message.getContact().isSelf()) {
                    activity.switchToContactDetails(message.getContact(), fingerprint);
                    return;
                }
            }
        }
        activity.switchToAccount(message.getConversation().getAccount(), fingerprint);
    }
}