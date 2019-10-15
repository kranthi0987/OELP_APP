package mahiti.org.oelp.ui;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import android.graphics.PorterDuff;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.databinding.DataBindingUtil;
import mahiti.org.oelp.R;
import mahiti.org.oelp.Config;
import mahiti.org.oelp.databinding.ActivityMucDetailsBinding;
import mahiti.org.oelp.entities.Account;
import mahiti.org.oelp.entities.Bookmark;
import mahiti.org.oelp.entities.Conversation;
import mahiti.org.oelp.entities.MucOptions;
import mahiti.org.oelp.services.XmppConnectionService;
import mahiti.org.oelp.ui.adapter.MediaAdapter;
import mahiti.org.oelp.ui.adapter.UserPreviewAdapter;
import mahiti.org.oelp.ui.interfaces.OnMediaLoaded;
import mahiti.org.oelp.ui.util.Attachment;
import mahiti.org.oelp.ui.util.AvatarWorkerTask;
import mahiti.org.oelp.ui.util.GridManager;
import mahiti.org.oelp.ui.util.MucConfiguration;
import mahiti.org.oelp.ui.util.MucDetailsContextMenuHelper;
import mahiti.org.oelp.ui.util.MyLinkify;
import mahiti.org.oelp.ui.util.SoftKeyboardUtils;
import mahiti.org.oelp.utils.Compatibility;
import mahiti.org.oelp.utils.EmojiWrapper;
import mahiti.org.oelp.utils.MenuDoubleTabUtil;
import mahiti.org.oelp.utils.StringUtils;
import mahiti.org.oelp.utils.StylingHelper;
import mahiti.org.oelp.utils.TimeframeUtils;
import mahiti.org.oelp.utils.XmppUri;
import rocks.xmpp.addr.Jid;

public class ConferenceDetailsActivity extends XmppActivity implements XmppConnectionService.OnConversationUpdate, XmppConnectionService.OnMucRosterUpdate, XmppConnectionService.OnAffiliationChanged, XmppConnectionService.OnConfigurationPushed, TextWatcher, OnMediaLoaded {
    public static final String ACTION_VIEW_MUC = "view_muc";
    private Conversation mConversation;
    private OnClickListener destroyListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            final AlertDialog.Builder DestroyMucDialog = new AlertDialog.Builder(ConferenceDetailsActivity.this);
            DestroyMucDialog.setNegativeButton(getString(R.string.cancel), null);
            final boolean groupChat = mConversation != null && mConversation.isPrivateAndNonAnonymous();
            DestroyMucDialog.setTitle(groupChat ? R.string.destroy_room : R.string.destroy_channel);
            DestroyMucDialog.setMessage(getString(groupChat ? R.string.destroy_room_dialog : R.string.destroy_channel_dialog, mConversation.getName()));
            DestroyMucDialog.setPositiveButton(getString(R.string.delete), (dialogInterface, i) -> {
                Intent intent = new Intent(xmppConnectionService, ConversationsActivity.class);
                intent.setAction(ConversationsActivity.ACTION_DESTROY_MUC);
                intent.putExtra("MUC_UUID", mConversation.getUuid());
                Log.d(mahiti.org.oelp.Config.LOGTAG, "Sending DESTROY intent for " + mConversation.getName());
                startActivity(intent);
                overridePendingTransition(R.animator.fade_in, R.animator.fade_out);
                deleteBookmark();
                finish();
            });
            DestroyMucDialog.create().show();
        }
    };
    private ActivityMucDetailsBinding binding;
    private MediaAdapter mMediaAdapter;
    private UserPreviewAdapter mUserPreviewAdapter;
    private String uuid = null;

    private boolean mAdvancedMode = false;

    private UiCallback<Conversation> renameCallback = new UiCallback<Conversation>() {
        @Override
        public void success(Conversation object) {
            displayToast(getString(R.string.your_nick_has_been_changed));
            runOnUiThread(() -> {
                updateView();
            });

        }

        @Override
        public void error(final int errorCode, Conversation object) {
            displayToast(getString(errorCode));
        }

        @Override
        public void userInputRequired(PendingIntent pi, Conversation object) {

        }
    };

    private OnClickListener mNotifyStatusClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ConferenceDetailsActivity.this);
            builder.setTitle(R.string.pref_notification_settings);
            String[] choices = {
                    getString(R.string.notify_on_all_messages),
                    getString(R.string.notify_only_when_highlighted),
                    getString(R.string.notify_never)
            };
            final AtomicInteger choice;
            if (mConversation.getLongAttribute(Conversation.ATTRIBUTE_MUTED_TILL, 0) == Long.MAX_VALUE) {
                choice = new AtomicInteger(2);
            } else {
                choice = new AtomicInteger(mConversation.alwaysNotify() ? 0 : 1);
            }
            builder.setSingleChoiceItems(choices, choice.get(), (dialog, which) -> choice.set(which));
            builder.setNegativeButton(R.string.cancel, null);
            builder.setPositiveButton(R.string.ok, (dialog, which) -> {
                if (choice.get() == 2) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(ConferenceDetailsActivity.this);
                    builder1.setTitle(R.string.disable_notifications);
                    final int[] durations = getResources().getIntArray(R.array.mute_options_durations);
                    final CharSequence[] labels = new CharSequence[durations.length];
                    for (int i = 0; i < durations.length; ++i) {
                        if (durations[i] == -1) {
                            labels[i] = getString(R.string.until_further_notice);
                        } else {
                            labels[i] = TimeframeUtils.resolve(ConferenceDetailsActivity.this, 1000L * durations[i]);
                        }
                    }
                    builder1.setItems(labels, (dialog1, which1) -> {
                        final long till;
                        if (durations[which1] == -1) {
                            till = Long.MAX_VALUE;
                        } else {
                            till = System.currentTimeMillis() + (durations[which1] * 1000);
                        }
                        mConversation.setMutedTill(till);
                        xmppConnectionService.updateConversation(mConversation);
                        updateView();
                    });
                    builder1.create().show();
                } else {
                    mConversation.setMutedTill(0);
                    mConversation.setAttribute(Conversation.ATTRIBUTE_ALWAYS_NOTIFY, String.valueOf(choice.get() == 0));
                }
                xmppConnectionService.updateConversation(mConversation);
                updateView();
            });
            builder.create().show();
        }
    };

    private OnClickListener mChangeConferenceSettings = new OnClickListener() {
        @Override
        public void onClick(View v) {
            final MucOptions mucOptions = mConversation.getMucOptions();
            AlertDialog.Builder builder = new AlertDialog.Builder(ConferenceDetailsActivity.this);
            MucConfiguration configuration = MucConfiguration.get(ConferenceDetailsActivity.this, mAdvancedMode, mucOptions);
            builder.setTitle(configuration.title);
            final boolean[] values = configuration.values;
            builder.setMultiChoiceItems(configuration.names, values, (dialog, which, isChecked) -> values[which] = isChecked);
            builder.setNegativeButton(R.string.cancel, null);
            builder.setPositiveButton(R.string.confirm, (dialog, which) -> {
                Bundle options = configuration.toBundle(values);
                options.putString("muc#roomconfig_persistentroom", "1");
                xmppConnectionService.pushConferenceConfiguration(mConversation,
                        options,
                        ConferenceDetailsActivity.this);
            });
            builder.create().show();
        }
    };

    @Override
    public void onConversationUpdate() {
        refreshUi();
    }

    @Override
    public void onMucRosterUpdate() {
        refreshUi();
    }

    @Override
    protected void refreshUiReal() {
        updateView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_muc_details);
        this.binding.changeConferenceButton.setOnClickListener(this.mChangeConferenceSettings);
        this.binding.destroy.setVisibility(View.GONE);
        this.binding.destroy.setOnClickListener(destroyListener);
        this.binding.leaveMuc.setVisibility(View.GONE);
        this.binding.addContactButton.setVisibility(View.GONE);
        setSupportActionBar((Toolbar) binding.toolbar);
        configureActionBar(getSupportActionBar());
        this.binding.editNickButton.setOnClickListener(v -> quickEdit(mConversation.getMucOptions().getActualNick(),
                R.string.nickname,
                value -> {
                    if (xmppConnectionService.renameInMuc(mConversation, value, renameCallback)) {
                        return null;
                    } else {
                        return getString(R.string.invalid_muc_nick);
                    }
                }));
        this.binding.detailsMucAvatar.setOnClickListener(v -> {
            final MucOptions mucOptions = mConversation.getMucOptions();
            if (!mucOptions.hasVCards()) {
                Toast.makeText(this, R.string.host_does_not_support_group_chat_avatars, Toast.LENGTH_SHORT).show();
                return;
            }
            if (!mucOptions.getSelf().getAffiliation().ranks(MucOptions.Affiliation.OWNER)) {
                Toast.makeText(this, R.string.only_the_owner_can_change_group_chat_avatar, Toast.LENGTH_SHORT).show();
                return;
            }
            final Intent intent = new Intent(this, PublishGroupChatProfilePictureActivity.class);
            intent.putExtra("uuid", mConversation.getUuid());
            startActivity(intent);
        });
        this.mAdvancedMode = getPreferences().getBoolean("advanced_muc_mode", false);
        this.binding.mucInfoMore.setVisibility(this.mAdvancedMode ? View.VISIBLE : View.GONE);
        this.binding.notificationStatusButton.setOnClickListener(this.mNotifyStatusClickListener);

        this.binding.editMucNameButton.setOnClickListener(this::onMucEditButtonClicked);
        this.binding.mucEditTitle.addTextChangedListener(this);
        this.binding.mucEditSubject.addTextChangedListener(this);
        this.binding.mucEditSubject.addTextChangedListener(new StylingHelper.MessageEditorStyler(this.binding.mucEditSubject));
        this.binding.autojoinCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (mConversation != null) {
                final Bookmark bookmark = mConversation.getBookmark();
                if (bookmark != null) {
                    bookmark.setAutojoin(this.binding.autojoinCheckbox.isChecked());
                    xmppConnectionService.pushBookmarks(bookmark.getAccount());
                    updateView();
                }
            }
        });
        this.mMediaAdapter = new MediaAdapter(this, R.dimen.media_size);
        this.mUserPreviewAdapter = new UserPreviewAdapter();
        this.binding.media.setAdapter(mMediaAdapter);
        this.binding.users.setAdapter(mUserPreviewAdapter);
        GridManager.setupLayoutManager(this, this.binding.media, R.dimen.media_size);
        GridManager.setupLayoutManager(this, this.binding.users, R.dimen.media_size);
        this.binding.invite.setOnClickListener(v -> inviteToConversation(mConversation));
        this.binding.showUsers.setOnClickListener(v -> {
            Intent intent = new Intent(this, MucUsersActivity.class);
            intent.putExtra("uuid", mConversation.getUuid());
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        final int theme = findTheme();
        if (this.mTheme != theme) {
            recreate();
        }
        binding.mediaWrapper.setVisibility(Compatibility.hasStoragePermission(this) ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (MenuDoubleTabUtil.shouldIgnoreTap()) {
            return false;
        }
        switch (menuItem.getItemId()) {
//            case android.R.id.home:
//                finish();
//                break;
//            case R.id.action_share_http:
//                shareLink(true);
//                break;
//            case R.id.action_share_uri:
//                shareLink(false);
//                break;
//            case R.id.action_advanced_mode:
//                this.mAdvancedMode = !menuItem.isChecked();
//                menuItem.setChecked(this.mAdvancedMode);
//                getPreferences().edit().putBoolean("advanced_muc_mode", mAdvancedMode).apply();
//                final boolean online = mConversation != null && mConversation.getMucOptions().online();
//                this.binding.mucInfoMore.setVisibility(this.mAdvancedMode && online ? View.VISIBLE : View.GONE);
//                invalidateOptionsMenu();
//                updateView();
//                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final MucOptions.User user = mUserPreviewAdapter.getSelectedUser();
        if (user == null) {
            Toast.makeText(this, R.string.unable_to_perform_this_action, Toast.LENGTH_SHORT).show();
            return true;
        }
        if (!MucDetailsContextMenuHelper.onContextItemSelected(item, mUserPreviewAdapter.getSelectedUser(), this)) {
            return super.onContextItemSelected(item);
        }
        return true;
    }

    public void onMucEditButtonClicked(View v) {
        if (this.binding.mucEditor.getVisibility() == View.GONE) {
            final MucOptions mucOptions = mConversation.getMucOptions();
            this.binding.mucEditor.setVisibility(View.VISIBLE);
            this.binding.mucDisplay.setVisibility(View.GONE);
            this.binding.editMucNameButton.setImageResource(getThemeResource(R.attr.icon_cancel, R.drawable.ic_cancel_black_24dp));
            final String name = mucOptions.getName();
            this.binding.mucEditTitle.setText("");
            final boolean owner = mucOptions.getSelf().getAffiliation().ranks(MucOptions.Affiliation.OWNER);
            if (owner || Bookmark.printableValue(name)) {
                this.binding.mucEditTitle.setVisibility(View.VISIBLE);
                if (name != null) {
                    this.binding.mucEditTitle.append(EmojiWrapper.transform(name));
                }
            } else {
                this.binding.mucEditTitle.setVisibility(View.GONE);
            }
            this.binding.mucEditTitle.setEnabled(owner);
            final String subject = mucOptions.getSubject();
            this.binding.mucEditSubject.setText("");
            if (subject != null) {
                this.binding.mucEditSubject.append(EmojiWrapper.transform(subject));
            }
            this.binding.mucEditSubject.setEnabled(mucOptions.canChangeSubject());
            if (!owner) {
                this.binding.mucEditSubject.requestFocus();
            }
        } else {
            String subject = this.binding.mucEditSubject.isEnabled() ? this.binding.mucEditSubject.getEditableText().toString().trim() : null;
            String name = this.binding.mucEditTitle.isEnabled() ? this.binding.mucEditTitle.getEditableText().toString().trim() : null;
            onMucInfoUpdated(subject, name);
            SoftKeyboardUtils.hideSoftKeyboard(this);
            hideEditor();
        }
    }

    private void hideEditor() {
        this.binding.mucEditor.setVisibility(View.GONE);
        this.binding.mucDisplay.setVisibility(View.VISIBLE);
        this.binding.editMucNameButton.setImageResource(getThemeResource(R.attr.icon_edit_body, R.drawable.ic_edit_black_24dp));
    }

    private void onMucInfoUpdated(String subject, String name) {
        final MucOptions mucOptions = mConversation.getMucOptions();
        if (mucOptions.canChangeSubject() && StringUtils.changed(mucOptions.getSubject(), subject)) {
            xmppConnectionService.pushSubjectToConference(mConversation, subject);
        }
        if (mucOptions.getSelf().getAffiliation().ranks(MucOptions.Affiliation.OWNER) && StringUtils.changed(mucOptions.getName(), name)) {
            Bundle options = new Bundle();
            options.putString("muc#roomconfig_persistentroom", "1");
            options.putString("muc#roomconfig_roomname", StringUtils.nullOnEmpty(name));
            xmppConnectionService.pushConferenceConfiguration(mConversation, options, this);
        }
    }

    @Override
    protected String getShareableUri(boolean http) {
        if (mConversation != null) {
            if (http) {
                return mahiti.org.oelp.Config.inviteMUCURL + XmppUri.lameUrlEncode(mConversation.getJid().asBareJid().toEscapedString());
            } else {
                return "xmpp:" + mConversation.getJid().asBareJid().toEscapedString() + "?join";
            }
        } else {
            return null;
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
//        MenuItem menuItemAdvancedMode = menu.findItem(R.id.action_advanced_mode);
//        menuItemAdvancedMode.setChecked(mAdvancedMode);
//        if (mConversation == null) {
//            return true;
//        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        final boolean groupChat = mConversation != null && mConversation.isPrivateAndNonAnonymous();
//        getMenuInflater().inflate(R.menu.muc_details, menu);
//        final MenuItem share = menu.findItem(R.id.action_share);
//        share.setVisible(!groupChat);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onMediaLoaded(List<Attachment> attachments) {
        runOnUiThread(() -> {
            int limit = GridManager.getCurrentColumnCount(binding.media);
            mMediaAdapter.setAttachments(attachments.subList(0, Math.min(limit, attachments.size())));
            binding.mediaWrapper.setVisibility(attachments.size() > 0 ? View.VISIBLE : View.GONE);
        });
    }

    protected void saveAsBookmark() {
        xmppConnectionService.saveConversationAsBookmark(mConversation, mConversation.getMucOptions().getName());
        updateView();
    }

    protected void deleteBookmark() {
        Account account = mConversation.getAccount();
        Bookmark bookmark = mConversation.getBookmark();
        if (bookmark != null) {
            account.getBookmarks().remove(bookmark);
            bookmark.setConversation(null);
        }
        xmppConnectionService.pushBookmarks(account);
        updateView();
    }

    @Override
    void onBackendConnected() {
        if (mPendingConferenceInvite != null) {
            mPendingConferenceInvite.execute(this);
            mPendingConferenceInvite = null;
        }
        if (getIntent().getAction().equals(ACTION_VIEW_MUC)) {
            this.uuid = getIntent().getExtras().getString("uuid");
        }
        if (uuid != null) {
            this.mConversation = xmppConnectionService.findConversationByUuid(uuid);
            if (this.mConversation != null) {
                if (Compatibility.hasStoragePermission(this)) {
                    final int limit = GridManager.getCurrentColumnCount(this.binding.media);
                    xmppConnectionService.getAttachments(this.mConversation, limit, this);
                    this.binding.showMedia.setOnClickListener((v) -> MediaBrowserActivity.launch(this, mConversation));
                    final boolean groupChat = mConversation != null && mConversation.isPrivateAndNonAnonymous();
//                    this.binding.destroy.setText(groupChat ? R.string.destroy_room : R.string.destroy_channel);
//                    this.binding.leaveMuc.setText(groupChat ? R.string.action_end_conversation_muc : R.string.action_end_conversation_channel);
                    this.binding.autojoinCheckbox.setText(groupChat ? R.string.autojoin_group_chat : R.string.autojoin_channel);
                }
                updateView();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (this.binding.mucEditor.getVisibility() == View.VISIBLE) {
            hideEditor();
        } else {
//            super.onBackPressed();
            this.finish();
        }
    }

    private void updateView() {
        invalidateOptionsMenu();
        if (mConversation == null) {
            return;
        }
        final MucOptions mucOptions = mConversation.getMucOptions();
        final Bookmark bookmark = mConversation.getBookmark();
        final MucOptions.User self = mucOptions.getSelf();
        String account;
        if (Config.DOMAIN_LOCK != null) {
            account = mConversation.getAccount().getJid().getLocal();
        } else {
            account = mConversation.getAccount().getJid().asBareJid().toString();
        }
        setTitle(mucOptions.isPrivateAndNonAnonymous() ? R.string.conference_details : R.string.channel_details);
        this.binding.editMucNameButton.setVisibility((self.getAffiliation().ranks(MucOptions.Affiliation.OWNER) || mucOptions.canChangeSubject()) ? View.VISIBLE : View.GONE);
        this.binding.detailsAccount.setText(getString(R.string.using_account, account));
        this.binding.jid.setText(mConversation.getJid().asBareJid().toEscapedString());
        if (xmppConnectionService.multipleAccounts()) {
            this.binding.detailsAccount.setVisibility(View.VISIBLE);
        } else {
            this.binding.detailsAccount.setVisibility(View.GONE);
        }
        AvatarWorkerTask.loadAvatar(mConversation, binding.detailsMucAvatar, R.dimen.avatar_big);
        AvatarWorkerTask.loadAvatar(mConversation.getAccount(), binding.yourPhoto, R.dimen.avatar_on_details_screen_size);

        String roomName = mucOptions.getName();
        String subject = mucOptions.getSubject();
        final boolean hasTitle;
        if (Bookmark.printableValue(roomName)) {
            this.binding.mucTitle.setText(EmojiWrapper.transform(roomName));
            this.binding.mucTitle.setVisibility(View.VISIBLE);
            hasTitle = true;
        } else if (!Bookmark.printableValue(subject)) {
            this.binding.mucTitle.setText(EmojiWrapper.transform(mConversation.getName()));
            hasTitle = true;
            this.binding.mucTitle.setVisibility(View.VISIBLE);
        } else {
            hasTitle = false;
            this.binding.mucTitle.setVisibility(View.GONE);
        }
        if (Bookmark.printableValue(subject)) {
            SpannableStringBuilder spannable = new SpannableStringBuilder(subject);
            StylingHelper.format(spannable, this.binding.mucSubject.getCurrentTextColor());
            MyLinkify.addLinks(spannable, false);
            this.binding.mucSubject.setText(EmojiWrapper.transform(spannable));
            this.binding.mucSubject.setTextAppearance(this, subject.length() > (hasTitle ? 128 : 196) ? R.style.TextAppearance_Conversations_Body1_Linkified : R.style.TextAppearance_Conversations_Subhead);
            this.binding.mucSubject.setAutoLinkMask(0);
            this.binding.mucSubject.setVisibility(View.VISIBLE);
        } else {
            this.binding.mucSubject.setVisibility(View.GONE);
        }
        this.binding.mucYourNick.setText(EmojiWrapper.transform(mucOptions.getActualNick()));
        if (mucOptions.online()) {
            this.binding.usersWrapper.setVisibility(View.VISIBLE);
            this.binding.mucInfoMore.setVisibility(this.mAdvancedMode ? View.VISIBLE : View.GONE);
            this.binding.jid.setVisibility(this.mAdvancedMode ? View.VISIBLE : View.GONE);
            this.binding.mucRole.setVisibility(View.VISIBLE);
            this.binding.mucRole.setText(getStatus(self));
            if (mucOptions.getSelf().getAffiliation().ranks(MucOptions.Affiliation.OWNER)) {
                this.binding.mucSettings.setVisibility(View.VISIBLE);
                this.binding.mucConferenceType.setText(MucConfiguration.describe(this, mucOptions));
            } else if (!mucOptions.isPrivateAndNonAnonymous() && mucOptions.nonanonymous()) {
                this.binding.mucSettings.setVisibility(View.VISIBLE);
                this.binding.mucConferenceType.setText(R.string.group_chat_will_make_your_jabber_id_public);
            } else {
                this.binding.mucSettings.setVisibility(View.GONE);
            }
            if (mucOptions.mamSupport()) {
                this.binding.mucInfoMam.setText(R.string.server_info_available);
            } else {
                this.binding.mucInfoMam.setText(R.string.server_info_unavailable);
            }
            if (bookmark != null) {
                this.binding.autojoinCheckbox.setVisibility(View.VISIBLE);
                if (bookmark.autojoin()) {
                    this.binding.autojoinCheckbox.setChecked(true);
                } else {
                    this.binding.autojoinCheckbox.setChecked(false);
                }
            } else {
                this.binding.autojoinCheckbox.setVisibility(View.GONE);
            }
            if (self.getAffiliation().ranks(MucOptions.Affiliation.OWNER)) {
                if (mAdvancedMode) {
                    this.binding.destroy.getBackground().setColorFilter(getWarningButtonColor(), PorterDuff.Mode.MULTIPLY);
                    this.binding.destroy.setVisibility(View.VISIBLE);
                } else {
                    this.binding.destroy.setVisibility(View.GONE);
                }
                this.binding.changeConferenceButton.setVisibility(View.VISIBLE);
            } else {
                this.binding.destroy.setVisibility(View.GONE);
                this.binding.changeConferenceButton.setVisibility(View.INVISIBLE);
            }
            this.binding.leaveMuc.setVisibility(View.GONE);
            this.binding.leaveMuc.setOnClickListener(v1 -> {
                final AlertDialog.Builder LeaveMucDialog = new AlertDialog.Builder(ConferenceDetailsActivity.this);
                LeaveMucDialog.setTitle(getString(R.string.action_end_conversation_muc));
                LeaveMucDialog.setMessage(getString(R.string.leave_conference_warning));
                LeaveMucDialog.setNegativeButton(getString(R.string.cancel), null);
                LeaveMucDialog.setPositiveButton(getString(R.string.action_end_conversation_muc),
                        (dialog, which) -> {
                            startActivity(new Intent(xmppConnectionService, ConversationsActivity.class));
                            overridePendingTransition(R.animator.fade_in, R.animator.fade_out);
                            this.xmppConnectionService.archiveConversation(mConversation);
                            finish();
                        });
                LeaveMucDialog.create().show();
            });
            this.binding.leaveMuc.getBackground().setColorFilter(getWarningButtonColor(), PorterDuff.Mode.MULTIPLY);
            this.binding.addContactButton.setVisibility(View.GONE);
            if (mConversation.getBookmark() != null) {
                this.binding.addContactButton.setText(R.string.delete_bookmark);
                this.binding.addContactButton.getBackground().setColorFilter(getWarningButtonColor(), PorterDuff.Mode.MULTIPLY);
                this.binding.addContactButton.setOnClickListener(v2 -> {
                    final AlertDialog.Builder deleteFromRosterDialog = new AlertDialog.Builder(ConferenceDetailsActivity.this);
                    deleteFromRosterDialog.setNegativeButton(getString(R.string.cancel), null);
                    deleteFromRosterDialog.setTitle(getString(R.string.action_delete_contact));
                    deleteFromRosterDialog.setMessage(getString(R.string.remove_bookmark_text, mConversation.getJid().toString()));
                    deleteFromRosterDialog.setPositiveButton(getString(R.string.delete),
                            (dialog, which) -> {
                                deleteBookmark();
                            });
                    deleteFromRosterDialog.create().show();
                });
            } else {
                this.binding.addContactButton.setText(R.string.save_as_bookmark);
                this.binding.addContactButton.getBackground().clearColorFilter();
                this.binding.addContactButton.setOnClickListener(v2 -> {
                    saveAsBookmark();
                });
            }
        } else {
            this.binding.usersWrapper.setVisibility(View.GONE);
            this.binding.mucInfoMore.setVisibility(View.GONE);
            this.binding.mucSettings.setVisibility(View.GONE);
        }

        int ic_notifications = getThemeResource(R.attr.icon_notifications, R.drawable.ic_notifications_black_24dp);
        int ic_notifications_off = getThemeResource(R.attr.icon_notifications_off, R.drawable.ic_notifications_off_black_24dp);
        int ic_notifications_paused = getThemeResource(R.attr.icon_notifications_paused, R.drawable.ic_notifications_paused_black_24dp);
        int ic_notifications_none = getThemeResource(R.attr.icon_notifications_none, R.drawable.ic_notifications_none_black_24dp);
        long mutedTill = mConversation.getLongAttribute(Conversation.ATTRIBUTE_MUTED_TILL, 0);
        if (mutedTill == Long.MAX_VALUE) {
            this.binding.notificationStatusText.setText(R.string.notify_never);
            this.binding.notificationStatusButton.setImageResource(ic_notifications_off);
        } else if (System.currentTimeMillis() < mutedTill) {
            this.binding.notificationStatusText.setText(R.string.notify_paused);
            this.binding.notificationStatusButton.setImageResource(ic_notifications_paused);
        } else if (mConversation.alwaysNotify()) {
            this.binding.notificationStatusText.setText(R.string.notify_on_all_messages);
            this.binding.notificationStatusButton.setImageResource(ic_notifications);
        } else {
            this.binding.notificationStatusText.setText(R.string.notify_only_when_highlighted);
            this.binding.notificationStatusButton.setImageResource(ic_notifications_none);
        }

        final List<MucOptions.User> users = mucOptions.getUsers();
        Collections.sort(users, (a, b) -> {
            if (b.getAffiliation().outranks(a.getAffiliation())) {
                return 1;
            } else if (a.getAffiliation().outranks(b.getAffiliation())) {
                return -1;
            } else {
                if (a.getAvatar() != null && b.getAvatar() == null) {
                    return -1;
                } else if (a.getAvatar() == null && b.getAvatar() != null) {
                    return 1;
                } else {
                    return a.getComparableName().compareToIgnoreCase(b.getComparableName());
                }
            }
        });
        this.mUserPreviewAdapter.submitList(MucOptions.sub(users, GridManager.getCurrentColumnCount(binding.users)));
        this.binding.invite.setVisibility(View.GONE);
        this.binding.showUsers.setVisibility(users.size() > 0 ? View.VISIBLE : View.GONE);
        this.binding.usersWrapper.setVisibility(users.size() > 0 || mucOptions.canInvite() ? View.VISIBLE : View.GONE);
        if (users.size() == 0) {
            this.binding.noUsersHints.setText(mucOptions.isPrivateAndNonAnonymous() ? R.string.no_users_hint_group_chat : R.string.no_users_hint_channel);
            this.binding.noUsersHints.setVisibility(View.VISIBLE);
        } else {
            this.binding.noUsersHints.setVisibility(View.GONE);
        }
    }

    public static String getStatus(Context context, MucOptions.User user, final boolean advanced) {
        if (advanced) {
            return String.format("%s (%s)", context.getString(user.getAffiliation().getResId()), context.getString(user.getRole().getResId()));
        } else {
            return context.getString(user.getAffiliation().getResId());
        }
    }

    private String getStatus(MucOptions.User user) {
        return getStatus(this, user, mAdvancedMode);
    }

    @Override
    public void onAffiliationChangedSuccessful(Jid jid) {
        refreshUi();
    }

    @Override
    public void onAffiliationChangeFailed(Jid jid, int resId) {
        displayToast(getString(resId, jid.asBareJid().toString()));
    }

    @Override
    public void onPushSucceeded() {
        displayToast(getString(R.string.modified_conference_options));
    }

    @Override
    public void onPushFailed() {
        displayToast(getString(R.string.could_not_modify_conference_options));
    }

    private void displayToast(final String msg) {
        runOnUiThread(() -> {
            if (isFinishing()) {
                return;
            }
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (mConversation == null) {
            return;
        }
        final MucOptions mucOptions = mConversation.getMucOptions();
        if (this.binding.mucEditor.getVisibility() == View.VISIBLE) {
            boolean subjectChanged = StringUtils.changed(binding.mucEditSubject.getEditableText().toString(), mucOptions.getSubject());
            boolean nameChanged = StringUtils.changed(binding.mucEditTitle.getEditableText().toString(), mucOptions.getName());
            if (subjectChanged || nameChanged) {
                this.binding.editMucNameButton.setImageResource(getThemeResource(R.attr.icon_save, R.drawable.ic_save_black_24dp));
            } else {
                this.binding.editMucNameButton.setImageResource(getThemeResource(R.attr.icon_cancel, R.drawable.ic_cancel_black_24dp));
            }
        }
    }
}
