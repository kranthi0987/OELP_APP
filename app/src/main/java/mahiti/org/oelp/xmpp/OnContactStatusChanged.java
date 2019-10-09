package mahiti.org.oelp.xmpp;

import mahiti.org.oelp.entities.Contact;

public interface OnContactStatusChanged {
    public void onContactStatusChanged(final Contact contact, final boolean online);
}
