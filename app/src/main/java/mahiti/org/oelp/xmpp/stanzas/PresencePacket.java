package mahiti.org.oelp.xmpp.stanzas;

public class PresencePacket extends AbstractAcknowledgeableStanza {

    public PresencePacket() {
        super("presence");
    }
}
