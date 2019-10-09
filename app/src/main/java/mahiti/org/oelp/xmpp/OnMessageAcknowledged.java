package mahiti.org.oelp.xmpp;

import mahiti.org.oelp.entities.Account;

public interface OnMessageAcknowledged {
    boolean onMessageAcknowledged(Account account, String id);
}
