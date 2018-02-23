package com.radiance.android.chatapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.radiance.android.chatapp.R;
import com.radiance.android.chatapp.app.EndPoints;
import com.radiance.android.chatapp.app.MyApplication;

public class EventDetailActivity extends AppCompatActivity {

    private String TAG = ChatRoomActivity.class.getSimpleName();

    private String eventId, eventName, eventDesc, eventStart, eventEnd, eventLoc, pageCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        Intent intent = getIntent();
        eventId = intent.getStringExtra("event_id");
        eventName = intent.getStringExtra("name");
        eventDesc = intent.getStringExtra("desc");
        eventStart = intent.getStringExtra("start_at");
        eventEnd = intent.getStringExtra("end_at");
        eventLoc = intent.getStringExtra("location");

        int icomma = eventLoc.indexOf(",");
        Log.e(TAG, "INDEX OF COMMA : " + icomma);
        final String latitude = eventLoc.substring(0,icomma);
        final String longitude = eventLoc.substring(icomma+1);

        ((TextView) findViewById(R.id.eventTitle)).setText(eventName);
        ((TextView) findViewById(R.id.eventDesc)).setText(eventDesc);
        ((TextView) findViewById(R.id.eventStartDate)).setText(eventStart);
        ((TextView) findViewById(R.id.eventEndDate)).setText(eventEnd);

        Button locationT = (Button) findViewById(R.id.eventLocation);
        locationT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent postIntent = new Intent(getApplicationContext(), MapsActivity.class);
                postIntent.putExtra("name", eventName);
                postIntent.putExtra("start_at", eventStart);
                postIntent.putExtra("latitude", latitude);
                postIntent.putExtra("longitude", longitude);
                postIntent.putExtra("actCode", "eventDetail");
                startActivity(postIntent);
            }
        });

        Button delete = (Button) findViewById(R.id.eventDelete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteEvent();
            }
        });
    }

    private void deleteEvent() {
        StringRequest strReq = new StringRequest(Request.Method.POST,
                EndPoints.DELETE_EVENT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "response: " + response);

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error flag
                    if (obj.getBoolean("error") == false) {
                        // start main activity
                        startActivity(new Intent(getApplicationContext(), EventsActivity.class));
                        finish();

                    } else {
                        // login error - simply toast the message
                        Toast.makeText(getApplicationContext(), "" + obj.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "Json parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(getApplicationContext(), "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(eventId));

                Log.e(TAG, "EVENT ID: " + params.toString());
                return params;
            }
        };

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }
}