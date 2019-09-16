package mahiti.org.oelp.xmpp;

import mahiti.org.oelp.entities.Account;
import mahiti.org.oelp.xmpp.stanzas.IqPacket;

public interface OnIqPacketReceived extends PacketReceived {
    void onIqPacketReceived(Account account, IqPacket packet);
}
