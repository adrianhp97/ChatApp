package com.radiance.android.chatapp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.radiance.android.chatapp.R;

public class CreateRoomActivity extends AppCompatActivity {
    private static final String TAG = CreateRoomActivity.class.getSimpleName();
    private Button btnCreate;
    private Button btnInvite;
    private Button btnJoin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);

        btnCreate = (Button) findViewById(R.id.create_group);
        btnInvite = (Button) findViewById(R.id.invite_group);
        btnJoin = (Button) findViewById(R.id.join_group);

        btnCreate.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        CreateNewRoomActivity.class);
                startActivity(i);
                finish();
            }
        });

//        btnInvite.setOnClickListener(new View.OnClickListener() {
//
//            public void onClick(View view) {
//                Intent i = new Intent(getApplicationContext(),
//                        RegisterActivity.class);
//                startActivity(i);
//                finish();
//            }
//        });
//
//        btnJoin.setOnClickListener(new View.OnClickListener() {
//
//            public void onClick(View view) {
//                Intent i = new Intent(getApplicationContext(),
//                        RegisterActivity.class);
//                startActivity(i);
//                finish();
//            }
//        });
    }
}
