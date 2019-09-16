package mahiti.org.oelp.crypto.axolotl;

public interface OnMessageCreatedCallback {
    void run(XmppAxolotlMessage message);
}
