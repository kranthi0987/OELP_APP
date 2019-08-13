package oelp.mahiti.org.newoepl.videoplay.api;

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

import oelp.mahiti.org.newoepl.videoplay.UpdateDbInterface;
import oelp.mahiti.org.newoepl.videoplay.utils.Validation;
import oelp.mahiti.org.newoepl.videoplay.utils.VideoDecryptionDb;

public class VideoAPI {

    private static final String TAG = "VideoAPI ";
    private Context context;

    public VideoAPI(Context context) {
        this.context=context;
    }

    public  void videoIntegration(String url, final String imei, final String deviceId , final UpdateDbInterface updateDbInterface)
    {
        final VideoDecryptionDb videoDecryptionDb=new VideoDecryptionDb(context);
        StringRequest request=new StringRequest(Request.Method.POST,url, response -> {
            try {
                JSONObject object=new JSONObject(response);
                Validation validation= new Validation();
                Log.i(TAG, "OnResponse : "+response);
                validation.setStatus(object.getInt("status"));
                validation.setMessage(object.getString("message"));
                JSONArray jsonArray= object.getJSONArray("validationData");
                videoDecryptionDb.insertData(jsonArray);


                updateDbInterface.updateDB();
            } catch (JSONException e) {
                Log.d("VideoAPI","VideoAPI"+e);
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<>();
                /* Posting Parameters*/
                params.put("imei", imei);
                params.put("deviceId", deviceId);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(request);

    }
}
