package mahiti.org.oelp.xmpp.stanzas.streammgmt;

import mahiti.org.oelp.xmpp.stanzas.AbstractStanza;

public class EnablePacket extends AbstractStanza {

    public EnablePacket(int smVersion) {
        super("enable");
        this.setAttribute("xmlns", "urn:xmpp:sm:" + smVersion);
        this.setAttribute("resume", "true");
    }

}
