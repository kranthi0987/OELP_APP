package mahiti.org.oelp.xmpp;

import mahiti.org.oelp.entities.Account;
import mahiti.org.oelp.xmpp.stanzas.MessagePacket;

public interface OnMessagePacketReceived extends PacketReceived {
    public void onMessagePacketReceived(Account account, MessagePacket packet);
}
