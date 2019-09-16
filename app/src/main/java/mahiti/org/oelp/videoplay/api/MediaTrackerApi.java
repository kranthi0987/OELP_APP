package mahiti.org.oelp.videoplay.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import mahiti.org.oelp.utils.Logger;
import mahiti.org.oelp.videoplay.CatalogResponseListener;
import mahiti.org.oelp.videoplay.UpdateDbInterface;
import mahiti.org.oelp.videoplay.utils.Validation;

public class MediaTrackerApi {

    private static final String TAG = "MediaTracker ";
    private CatalogResponseListener mContext;
    private Context context;
    public boolean hasToFinish;


    public MediaTrackerApi(Context context) {
        this.context = context;
//        this.mContext = (CatalogResponseListener) context;
    }

    public void mediaTracking(String url, final String userId, final JSONArray media, final UpdateDbInterface updateDbInterface, String deviceId, boolean hasToFinish) {
        this.hasToFinish = hasToFinish;

        //JSONObject object
        StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
            try {
                Logger.logD(TAG, "API : " + url + " RESPONSE :" + response);
                JSONObject object = new JSONObject(response);
                Log.i(TAG, "MediaTracker response : " + object.toString());
                Validation validation = new Validation();
                Log.i(TAG, " Media tracker updated status: " + validation.getStatus());
                validation.setStatus(object.getInt("status"));
                validation.setMessage(object.getString("message"));
//                if (hasToFinish)
//                    mContext.onCatalogResponse(true);
//                else
//                    mContext.onCatalogResponse(false);
                updateDbInterface.trackerUpdate(validation);

            } catch (JSONException e) {
//                mContext.onCatalogResponse(false);
                Log.d("VideoAPI", "VideoAPI" + e);
            }
        }, error -> {
            error.printStackTrace();
//            mContext.onCatalogResponse(false);
        }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put("deviceId", deviceId);
                params.put("user_id", userId);
                params.put("tracker", media.toString());
                Logger.logD(TAG, "API : " + url + " PARAM :" + params.toString());
                return params;

            }
        };

        Volley.newRequestQueue(context).add(request);
    }


}
