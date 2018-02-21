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

public class CreateNewRoomActivity extends AppCompatActivity {
    private static final String TAG = CreateNewRoomActivity.class.getSimpleName();
    private Button btnCreateNewRoom;
    private EditText inputRoomName;
    private EditText inputRoomPassword;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_room);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        inputRoomName = (EditText) findViewById(R.id.inputRoomName);
        inputRoomPassword = (EditText) findViewById(R.id.inputRoomPassword);
        btnCreateNewRoom = (Button) findViewById(R.id.btnCreateNewRoom);

        // Login button Click Event
        btnCreateNewRoom.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String name = inputRoomName.getText().toString().trim();
                String password = inputRoomPassword.getText().toString().trim();

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
        String tag_string_req = "req_new_room";

        pDialog.setMessage("Create new Room ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                EndPoints.CREATE_ROOM, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Create Room Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    Log.d(TAG, "isError");
                    hideDialog();
                    // Check for error node in json
                    if (!error) {
                        // room successfully
                        Log.d(TAG, "noError");
//
                        JSONObject roomObj = jObj.getJSONObject("chat_room");

                        Log.e(TAG, "Create Room: " + roomObj.toString());
                        Log.d(TAG, "wanna run main");
                        // start main activity
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();

                    } else {
                        // Error in create room. Get the error message
                        String errorMsg = jObj.getString("message");
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
                Log.e(TAG, "Create Room Error: " + error.getMessage());
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
