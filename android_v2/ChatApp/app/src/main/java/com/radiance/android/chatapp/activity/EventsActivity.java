package com.radiance.android.chatapp.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
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
import com.radiance.android.chatapp.R;
import com.radiance.android.chatapp.adapter.EventsAdapter;
import com.radiance.android.chatapp.app.Config;
import com.radiance.android.chatapp.app.EndPoints;
import com.radiance.android.chatapp.app.MyApplication;
import com.radiance.android.chatapp.gcm.GcmIntentService;
import com.radiance.android.chatapp.gcm.NotificationUtils;
import com.radiance.android.chatapp.helper.MyBroadcastReceiver;
import com.radiance.android.chatapp.helper.SimpleDividerItemDecoration;
import com.radiance.android.chatapp.helper.VolleyCallback;
import com.radiance.android.chatapp.model.Event;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.text.ParseException;

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

        recyclerView.addOnItemTouchListener(new EventsAdapter.RecyclerTouchListener(getApplicationContext(), recyclerView, new EventsAdapter.ClickListener() {

            @Override
            public void onClick(View view, int position) {
                Event eventDetail = eventArrayList.get(position);
                Intent intent = new Intent(EventsActivity.this, EventDetailActivity.class);
                intent.putExtra("event_id", eventDetail.getId());
                intent.putExtra("name", eventDetail.getName());
                intent.putExtra("desc", eventDetail.getDesc());
                intent.putExtra("start_at", eventDetail.getStartAt());
                intent.putExtra("end_at", eventDetail.getEndAt());
                intent.putExtra("location", eventDetail.getLocation());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.buttonEvents);
        myFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCreateEvent();
            }
        });

        fetchEvents(new VolleyCallback(){
            @Override
            public void onSuccess(String result){
                Log.e(TAG, "STRING RES: " + result);
                startAlertAtParticularTime(eventArrayList);
            }
        });

    }

    public void startCreateEvent(){
        Intent createEventIntent = new Intent(this, CreateEventActivity.class);
        startActivity(createEventIntent);
    }

    private void fetchEvents(final VolleyCallback callback) {
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
                            ev.setDesc(eventsObj.getString("desc"));
                            ev.setStartAt(eventsObj.getString("start_at"));
                            ev.setEndAt(eventsObj.getString("end_at"));
                            ev.setLocation(eventsObj.getString("location"));
                            ev.setTimestamp(eventsObj.getString("created_at"));

                            Log.e(TAG, "DESC OBJ " + eventsObj.getString("desc"));
                            Log.e(TAG, "DESC EV " + ev.getDesc());
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

                callback.onSuccess(response);
            }
        }
        , new Response.ErrorListener() {

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

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_logout:
                MyApplication.getInstance().logout();
                break;
            case R.id.action_events:
                Intent intentEvents = new Intent(this, EventsActivity.class);
                startActivity(intentEvents);
                return true;
        }

        return super.onOptionsItemSelected(menuItem);
    }

    public void startAlertAtParticularTime(ArrayList<Event> events) {
        Log.e(TAG, "Array Size " + String.valueOf(events.size()));

        int announcedEvents = 0;

        for (int i = 0; i < events.size(); i++) {

            String alarmTime = eventArrayList.get(i).getStartAt();
            SimpleDateFormat alarmFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                // Setup Schedule
                Date alarmDate = alarmFormat.parse(alarmTime);
                SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
                SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
                SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
                SimpleDateFormat hourFormat = new SimpleDateFormat("HH");
                SimpleDateFormat minuteFormat = new SimpleDateFormat("mm");

                String year = yearFormat.format(alarmDate);
                String month = monthFormat.format(alarmDate);
                String day = dayFormat.format(alarmDate);
                String hour = hourFormat.format(alarmDate);
                String minute = minuteFormat.format(alarmDate);

                // Setup Time Now
                DateFormat yearFormatter = new SimpleDateFormat("yyyy");
                DateFormat monthFormatter = new SimpleDateFormat("MM");
                DateFormat dayFormatter = new SimpleDateFormat("dd");
                DateFormat hourFormatter = new SimpleDateFormat("HH");
                DateFormat minFormatter = new SimpleDateFormat("mm");
                DateFormat secFormatter = new SimpleDateFormat("ss");

                yearFormatter.setLenient(false);
                monthFormatter.setLenient(false);
                dayFormatter.setLenient(false);
                hourFormatter.setLenient(false);
                minFormatter.setLenient(false);
                secFormatter.setLenient(false);

                Date today = new Date();
                String yer = yearFormatter.format(today);
                String mot = monthFormatter.format(today);
                String dey = dayFormatter.format(today);
                String hor = hourFormatter.format(today);
                String min = minFormatter.format(today);
                String sec = secFormatter.format(today);

                // Calculate Date to second
                long sumNow = (Integer.parseInt(yer) * 365) + (Integer.parseInt(mot) * 30) + Integer.parseInt(dey);
                sumNow = (Integer.parseInt(hor) * 60 * 60) + (Integer.parseInt(min) * 60) + Integer.parseInt(sec) + (sumNow * 24 * 60 * 60);
                long schedule = (Integer.parseInt(year) * 365) + (Integer.parseInt(month) * 30) + Integer.parseInt(day);
                schedule = (Integer.parseInt(hour) * 60 * 60) + (Integer.parseInt(minute) * 60) + (schedule * 24 * 60 * 60);

                Log.e(TAG, "Date now: " + yer + "-" + mot + "-" + dey + " " + hor + ":" + min);
                Log.e(TAG, "Alarm set at: " + year + "-" + month + "-" + day + " " + hour + ":" + minute);

                Log.e(TAG, "Sum");
                Log.e(TAG, "Now: " + String.valueOf(sumNow));
                Log.e(TAG, "Schedule: " + String.valueOf(schedule));

                if(sumNow <= schedule) {
                    announcedEvents++;

                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(System.currentTimeMillis());
                    cal.set(Integer.parseInt(year),Integer.parseInt(month)-1,Integer.parseInt(day));
                    cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
                    cal.set(Calendar.MINUTE, Integer.parseInt(minute));
                    cal.set(Calendar.SECOND, 0);

                    intent = new Intent(this, MyBroadcastReceiver.class);
                    pendingIntent = PendingIntent.getBroadcast(
                            this.getApplicationContext(), 801 + i, intent, PendingIntent.FLAG_CANCEL_CURRENT);

                    AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
                    }else {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
                    }
                }
            } catch (ParseException e) {
                Log.e(TAG," Error when setting the alarm!");
                e.printStackTrace();
            }

        }

        if(announcedEvents > 0) {
            Toast.makeText(this, "Alarm will vibrate at specified time",
                    Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "No Events To Alarm", Toast.LENGTH_SHORT).show();
        }
    }

}
