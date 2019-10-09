package mahiti.org.oelp.utils;

import android.app.Activity;
import android.content.Intent;

import mahiti.org.oelp.Config;
import mahiti.org.oelp.entities.Account;
import mahiti.org.oelp.services.XmppConnectionService;
import mahiti.org.oelp.ui.ConversationsActivity;
import mahiti.org.oelp.ui.EditAccountActivity;
import mahiti.org.oelp.ui.ManageAccountActivity;
import mahiti.org.oelp.ui.StartConversationActivity;
import mahiti.org.oelp.ui.WelcomeActivity;

public class SignupUtils {

    public static Intent getSignUpIntent(final Activity activity) {
        final Intent intent = new Intent(activity, WelcomeActivity.class);
        return intent;
    }

    public static Intent getRedirectionIntent(final ConversationsActivity activity) {
        final XmppConnectionService service = activity.xmppConnectionService;
        Account pendingAccount = AccountUtils.getPendingAccount(service);
        Intent intent;
        if (pendingAccount != null) {
            intent = new Intent(activity, EditAccountActivity.class);
            intent.putExtra("jid", pendingAccount.getJid().asBareJid().toString());
        } else {
            if (service.getAccounts().size() == 0) {
                if (Config.X509_VERIFICATION) {
                    intent = new Intent(activity, ManageAccountActivity.class);
                } else if (Config.MAGIC_CREATE_DOMAIN != null) {
                    intent = getSignUpIntent(activity);
                } else {
                    intent = new Intent(activity, EditAccountActivity.class);
                }
            } else {
                intent = new Intent(activity, StartConversationActivity.class);
            }
        }
        intent.putExtra("init", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }
}