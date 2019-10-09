package mahiti.org.oelp.xmpp.jingle;

import mahiti.org.oelp.entities.Account;
import mahiti.org.oelp.xmpp.PacketReceived;
import mahiti.org.oelp.xmpp.jingle.stanzas.JinglePacket;

public interface OnJinglePacketReceived extends PacketReceived {
    void onJinglePacketReceived(Account account, JinglePacket packet);
}
