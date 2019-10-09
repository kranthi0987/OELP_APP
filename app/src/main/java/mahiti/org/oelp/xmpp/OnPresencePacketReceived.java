package mahiti.org.oelp.xmpp;

import mahiti.org.oelp.entities.Account;
import mahiti.org.oelp.xmpp.stanzas.PresencePacket;

public interface OnPresencePacketReceived extends PacketReceived {
    public void onPresencePacketReceived(Account account, PresencePacket packet);
}
