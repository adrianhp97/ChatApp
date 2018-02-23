package com.radiance.android.chatapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.radiance.android.chatapp.R;

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
    }
}
