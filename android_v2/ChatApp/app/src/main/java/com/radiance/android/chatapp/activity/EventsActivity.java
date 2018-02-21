package com.radiance.android.chatapp.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import com.radiance.android.chatapp.R;
import com.radiance.android.chatapp.adapter.EventsAdapter;
import com.radiance.android.chatapp.app.Config;
import com.radiance.android.chatapp.app.EndPoints;
import com.radiance.android.chatapp.app.MyApplication;
import com.radiance.android.chatapp.gcm.GcmIntentService;
import com.radiance.android.chatapp.gcm.NotificationUtils;
import com.radiance.android.chatapp.helper.SimpleDividerItemDecoration;
import com.radiance.android.chatapp.model.Event;

public class EventsActivity extends AppCompatActivity {
    private String TAG = MainActivity.class.getSimpleName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ArrayList<Event> eventArrayList;
    private EventsAdapter mAdapter;
    private RecyclerView recyclerView;
    private Intent intent;
    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.events);
        recyclerView = findViewById(R.id.events_list);

        eventArrayList = new ArrayList<>();
        mAdapter = new EventsAdapter(this, eventArrayList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(
                getApplicationContext()
        ));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.buttonEvents);
        myFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCreateEvent();
            }
        });

        fetchEvents();

    }

    public void startCreateEvent(){
        Intent createEventIntent = new Intent(this, CreateEventActivity.class);
        startActivity(createEventIntent);
    }

    private void fetchEvents() {
        StringRequest strReq = new StringRequest(Request.Method.GET,
                EndPoints.EVENTS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "response: " + response);

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error flag
                    if (obj.getBoolean("error") == false) {
                        JSONArray chatRoomsArray = obj.getJSONArray("events");
                        for (int i = 0; i < chatRoomsArray.length(); i++) {
                            JSONObject eventsObj = (JSONObject) chatRoomsArray.get(i);
                            Event ev = new Event();
                            ev.setId(eventsObj.getString("event_id"));
                            ev.setName(eventsObj.getString("name"));
                            ev.setStartAt(eventsObj.getString("start_at"));
                            ev.setTimestamp(eventsObj.getString("created_at"));

                            eventArrayList.add(ev);
                        }

                    } else {
                        // error in fetching chat rooms
                        Toast.makeText(getApplicationContext(), "" + obj.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

                mAdapter.notifyDataSetChanged();

                // subscribing to all chat room topics
                subscribeToAllTopics();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(getApplicationContext(), "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }

    private void subscribeToAllTopics() {
        for (Event ev : eventArrayList) {

            Intent intent = new Intent(this, GcmIntentService.class);
            intent.putExtra(GcmIntentService.KEY, GcmIntentService.SUBSCRIBE);
            intent.putExtra(GcmIntentService.TOPIC, "topic_" + ev.getId());
            startService(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clearing the notification tray
        NotificationUtils.clearNotifications();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }
//
//    public boolean onOptionsItemSelected(MenuItem menuItem) {
//        switch (menuItem.getItemId()) {
//            case R.id.action_logout:
//                MyApplication.getInstance().logout();
//                break;
//            case R.id.action_settings:
//                Intent intentSettings = new Intent(this, SettingsActivity.class);
//                startActivity(intentSettings);
//                return true;
//            case R.id.action_events:
//                Intent intentEvents = new Intent(this, EventsActivity.class);
//                startActivity(intentEvents);
//                return true;
//        }
//
//        return super.onOptionsItemSelected(menuItem);
//    }

}