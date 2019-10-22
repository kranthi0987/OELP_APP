package mahiti.org.oelp.videoplay.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import mahiti.org.oelp.utils.Constants;
import mahiti.org.oelp.utils.Logger;
import mahiti.org.oelp.utils.MySharedPref;
import mahiti.org.oelp.videoplay.CatalogResponseListener;
import mahiti.org.oelp.videoplay.UpdateDbInterface;
import mahiti.org.oelp.videoplay.utils.Validation;
import mahiti.org.oelp.videoplay.utils.VideoDecryptionDb;

public class MediaTrackerApi {

    private static final String TAG = "MediaTracker ";
    private CatalogResponseListener mContext;
    private Context context;
    public boolean hasToFinish;


    public MediaTrackerApi(Context context) {
        this.context = context;
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


    public void mediaTracking(String url, final String userId, final JSONArray media, String deviceId, boolean hasToFinish) {
        this.hasToFinish = hasToFinish;

        StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
            try {
                Logger.logD(TAG, "API : " + url + " RESPONSE :" + response);
                JSONObject object = new JSONObject(response);
                if (object.getInt("status") == 2) {
                    new VideoDecryptionDb(context).delMediaTrackerRecords();
                    new MySharedPref(context).writeBoolean(Constants.MEDIATRACKERCHANGED, false);
                }

            } catch (JSONException e) {
                Log.d("VideoAPI", "VideoAPI" + e);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.logE(TAG, error.getMessage(), error);
            }
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
