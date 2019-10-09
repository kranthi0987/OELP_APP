package mahiti.org.oelp.xmpp.stanzas.csi;

import mahiti.org.oelp.xmpp.stanzas.AbstractStanza;

public class InactivePacket extends AbstractStanza {
    public InactivePacket() {
        super("inactive");
        setAttribute("xmlns", "urn:xmpp:csi:0");
    }
}
