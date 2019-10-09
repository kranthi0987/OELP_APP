package mahiti.org.oelp.xmpp.stanzas.streammgmt;

import mahiti.org.oelp.xmpp.stanzas.AbstractStanza;

public class AckPacket extends AbstractStanza {

    public AckPacket(int sequence, int smVersion) {
        super("a");
        this.setAttribute("xmlns", "urn:xmpp:sm:" + smVersion);
        this.setAttribute("h", Integer.toString(sequence));
    }

}
