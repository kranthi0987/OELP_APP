package mahiti.org.oelp.xmpp.stanzas.streammgmt;

import mahiti.org.oelp.xmpp.stanzas.AbstractStanza;

public class RequestPacket extends AbstractStanza {

    public RequestPacket(int smVersion) {
        super("r");
        this.setAttribute("xmlns", "urn:xmpp:sm:" + smVersion);
    }

}
