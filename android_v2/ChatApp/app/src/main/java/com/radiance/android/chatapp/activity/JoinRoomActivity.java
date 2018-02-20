package com.radiance.android.chatapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.radiance.android.chatapp.R;
import com.radiance.android.chatapp.app.EndPoints;
import com.radiance.android.chatapp.app.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class JoinRoomActivity extends AppCompatActivity {
    private static final String TAG = CreateNewRoomActivity.class.getSimpleName();
    private Button btnJoinRoom;
    private EditText inputJoinRoomName;
    private EditText inputJoinRoomPassword;
    private ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_room);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        inputJoinRoomName = (EditText) findViewById(R.id.inputRoomName);
        inputJoinRoomPassword = (EditText) findViewById(R.id.inputRoomPassword);
        btnJoinRoom = (Button) findViewById(R.id.btnCreateNewRoom);

        // Login button Click Event
        btnJoinRoom.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String name = inputJoinRoomName.getText().toString().trim();
                String password = inputJoinRoomPassword.getText().toString().trim();

                // Check for empty data in the form
                if (!name.isEmpty()) {
                    // login user
                    checkRoom(name, password);
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "Please enter the credentials!", Toast.LENGTH_LONG)
                            .show();
                }
            }

        });
    }

    private void checkRoom(final String name, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_join_room";

        pDialog.setMessage("Create Join Room ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                EndPoints.JOIN_ROOM, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Join Room Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // room successfully logged in

                        JSONObject roomObj = jObj.getJSONObject("chat_room");

                        Log.e(TAG, "Join Room: " + roomObj.toString());

                        // start main activity
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();

                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Join Room Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("password", password);

                return params;
            }

        };

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
