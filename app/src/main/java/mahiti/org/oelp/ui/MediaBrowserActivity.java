package mahiti.org.oelp.ui;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import java.util.List;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import mahiti.org.oelp.R;
import mahiti.org.oelp.databinding.ActivityMediaBrowserBinding;
import mahiti.org.oelp.entities.Account;
import mahiti.org.oelp.entities.Contact;
import mahiti.org.oelp.entities.Conversation;
import mahiti.org.oelp.ui.adapter.MediaAdapter;
import mahiti.org.oelp.ui.interfaces.OnMediaLoaded;
import mahiti.org.oelp.ui.util.Attachment;
import mahiti.org.oelp.ui.util.GridManager;
import rocks.xmpp.addr.Jid;


public class MediaBrowserActivity extends XmppActivity implements OnMediaLoaded {

    private ActivityMediaBrowserBinding binding;

    private MediaAdapter mMediaAdapter;

    public static void launch(Context context, Contact contact) {
        launch(context, contact.getAccount(), contact.getJid().asBareJid().toEscapedString());
    }

    public static void launch(Context context, Conversation conversation) {
        launch(context, conversation.getAccount(), conversation.getJid().asBareJid().toEscapedString());
    }

    private static void launch(Context context, Account account, String jid) {
        final Intent intent = new Intent(context, MediaBrowserActivity.class);
        intent.putExtra("account", account.getUuid());
        intent.putExtra("jid", jid);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_media_browser);
        setSupportActionBar((Toolbar) binding.toolbar);
        configureActionBar(getSupportActionBar());
        mMediaAdapter = new MediaAdapter(this, R.dimen.media_size);
        this.binding.media.setAdapter(mMediaAdapter);
        GridManager.setupLayoutManager(this, this.binding.media, R.dimen.browser_media_size);
        this.binding.noMedia.setVisibility(View.GONE);
        this.binding.progressbar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void refreshUiReal() {

    }

    @Override
    void onBackendConnected() {
        Intent intent = getIntent();
        String account = intent == null ? null : intent.getStringExtra("account");
        String jid = intent == null ? null : intent.getStringExtra("jid");
        if (account != null && jid != null) {
            xmppConnectionService.getAttachments(account, Jid.of(jid), 0, this);
        }
    }

    @Override
    public void onMediaLoaded(List<Attachment> attachments) {
        runOnUiThread(() -> {
            if (attachments.size() > 0) {
                mMediaAdapter.setAttachments(attachments);
                this.binding.noMedia.setVisibility(View.GONE);
                this.binding.progressbar.setVisibility(View.GONE);
            } else {
                this.binding.noMedia.setVisibility(View.VISIBLE);
                this.binding.progressbar.setVisibility(View.GONE);
            }
        });
    }
}