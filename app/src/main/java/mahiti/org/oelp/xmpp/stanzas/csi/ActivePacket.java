package mahiti.org.oelp.xmpp.stanzas.csi;

import mahiti.org.oelp.xmpp.stanzas.AbstractStanza;

public class ActivePacket extends AbstractStanza {
    public ActivePacket() {
        super("active");
        setAttribute("xmlns", "urn:xmpp:csi:0");
    }
}
