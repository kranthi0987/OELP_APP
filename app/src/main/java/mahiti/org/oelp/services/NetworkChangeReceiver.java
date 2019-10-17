package mahiti.org.oelp.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import mahiti.org.oelp.utils.CheckNetwork;

/**
 * Created by RAJ ARYAN on 2019-10-16.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (CheckNetwork.checkNet(context)) {
                new SyncingUserData(context);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
