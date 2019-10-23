/*
 * Copyright (c) 2019.
 * Project created and maintained by sanjay kranthi kumar
 * if need to contribute contact us on
 * kranthi0987@gmail.com
 */

package mahiti.org.oelp.chat.utilies;

import android.util.Log;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearch;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jivesoftware.smackx.xdata.Form;
import org.jxmpp.jid.DomainBareJid;

import java.util.List;

public class Userutils {
    ConnectionUtils connectionUtils = new ConnectionUtils();
    XMPPConnection connection;

    public Userutils() {
    }

    public String getUserNickname() {
        if (connection == null)
            connection = connectionUtils.getXmptcConnection();
        VCard mVCard = new VCard();
        try {
            mVCard.load(connection, connection.getUser().asEntityBareJidIfPossible());
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mVCard.getNickName();
    }

    public void setUserNickName(String Nickname) {
        if (connection == null)
            connection = connectionUtils.getXmptcConnection();
        VCard mVCard = new VCard();
        try {
            mVCard.load(connection, connection.getUser().asEntityBareJidIfPossible());
            mVCard.setNickName(Nickname);
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void getAllXmppUsers() {
        if (connection == null)
            connection = connectionUtils.getXmptcConnection();
        try {
            UserSearchManager manager = new UserSearchManager(connection);
            DomainBareJid searchFormString = connection.getServiceName().asDomainBareJid();
            Log.d("***", "SearchForm: " + searchFormString);
            Form searchForm = null;

            searchForm = manager.getSearchForm(searchFormString);

            Form answerForm = searchForm.createAnswerForm();

            UserSearch userSearch = new UserSearch();
            answerForm.setAnswer("Username", true);
            answerForm.setAnswer("search", "*");

            ReportedData results = userSearch.sendSearchForm(connection, answerForm, searchFormString);
            if (results != null) {
                List<ReportedData.Row> rows = results.getRows();
                for (ReportedData.Row row : rows) {
                    Log.d("***", "row: " + row.getValues("Username").toString());
                }
            } else {
                Log.d("***", "No result found");
            }

        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
