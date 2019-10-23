package mahiti.org.oelp.chat.utilies;


import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.io.IOException;

import mahiti.org.oelp.chat.service.XMPP;

public class ConnectionUtils {

    XMPPTCPConnection connection = null;

    public XMPPTCPConnection getXmptcConnection() {

        if (XMPP.getInstance().isConnected()) {
            try {
                connection = XMPP.getInstance().getConnection();
            } catch (XMPPException | SmackException | IOException | InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            try {
                connection = XMPP.getInstance().getConnection();
            } catch (XMPPException | SmackException | IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

}
