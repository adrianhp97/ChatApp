package com.radiance.android.chatapp.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.radiance.android.chatapp.R;
import com.radiance.android.chatapp.app.EndPoints;
import com.radiance.android.chatapp.app.MyApplication;

/**
 * Created by ASUS PC on 19/02/2018.
 */

public class CreateEventActivity extends AppCompatActivity {
    private String TAG = CreateEventActivity.class.getSimpleName();
    private Calendar myCalendar;
    private EditText name, desc, location, sTime, sDate, eTime, eDate;
    private TextInputLayout nameLayout, descLayout, locationLayout, sDateLayout, sTimeLayout, eDateLayout, eTimeLayout;
    private Button btn;
    private EditText locationText;
    private Intent intentMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.create_event);

        myCalendar = Calendar.getInstance();

        EditText startDateText= (EditText) findViewById(R.id.input_sdateEvent);
        showDatePicker(startDateText, "Start Date");

        EditText startTimeText= (EditText) findViewById(R.id.input_stimeEvent);
        showTimePicker(startTimeText, "Start Time");

        EditText endDateText= (EditText) findViewById(R.id.input_edateEvent);
        showDatePicker(endDateText, "End Date");

        EditText endTimeText= (EditText) findViewById(R.id.input_etimeEvent);
        showTimePicker(endTimeText, "End Time");

        locationText= (EditText) findViewById(R.id.input_locationEvent);
        locationText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapIntent = new Intent(CreateEventActivity.this, MapsActivity.class);
                mapIntent.putExtra("actCode", "eventCreate");
                startActivity(mapIntent);
            }
        });

        intentMap = getIntent();
        String latitude = intentMap.getStringExtra("latitude");
        String longitude = intentMap.getStringExtra("longitude");
        Log.e(TAG,"LATLONG : " + latitude + " : "+longitude);
        if(latitude != null && longitude != null){
            setLocationOnField(Double.parseDouble(latitude), Double.parseDouble(longitude));
        }

        nameLayout = (TextInputLayout) findViewById(R.id.input_event_name);
        descLayout = (TextInputLayout) findViewById(R.id.input_event_desc);
        sDateLayout = (TextInputLayout) findViewById(R.id.input_event_sdate);
        sTimeLayout = (TextInputLayout) findViewById(R.id.input_event_stime);
        eDateLayout = (TextInputLayout) findViewById(R.id.input_event_edate);
        eTimeLayout = (TextInputLayout) findViewById(R.id.input_event_etime);
        locationLayout = (TextInputLayout) findViewById(R.id.input_event_location);

        name = (EditText) findViewById(R.id.input_nameEvent);
        desc = (EditText) findViewById(R.id.input_descEvent);
        sDate = (EditText) findViewById(R.id.input_sdateEvent);
        sTime = (EditText) findViewById(R.id.input_stimeEvent);
        eDate = (EditText) findViewById(R.id.input_edateEvent);
        eTime = (EditText) findViewById(R.id.input_etimeEvent);
        location = (EditText) findViewById(R.id.input_locationEvent);

        btn = (Button) findViewById(R.id.btn_createEvent);

        name.addTextChangedListener(new MyTextWatcher(name));
        desc.addTextChangedListener(new MyTextWatcher(desc));
        sDate.addTextChangedListener(new MyTextWatcher(sDate));
        sTime.addTextChangedListener(new MyTextWatcher(sTime));
        eDate.addTextChangedListener(new MyTextWatcher(eDate));
        eTime.addTextChangedListener(new MyTextWatcher(eTime));
        location.addTextChangedListener(new MyTextWatcher(location));

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createEvent();
            }
        });

    }

    public  void setLocationOnField(double latitude, double longitude){
        locationText.setText(latitude + "," + longitude);
    }

    private void showDatePicker(final EditText text, final String str){
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(text);
            }

        };

        text.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                DatePickerDialog dpd = new DatePickerDialog(CreateEventActivity.this, date,
                        myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                dpd.show();
                dpd.setTitle(str);
            }
        });
    }

    private void updateLabel(EditText edittext) {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        edittext.setText(sdf.format(myCalendar.getTime()));
    }

    private void showTimePicker(final EditText text, final String str){
        text.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
                int minute = myCalendar.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(CreateEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String h,m;
                        h = String.valueOf(selectedHour);
                        m = String.valueOf(selectedMinute);
                        if(String.valueOf(selectedHour).length() == 1)
                            h = "0" + h;
                        if(String.valueOf(selectedMinute).length() == 1)
                            m = "0" + m;
                        text.setText( h + ":" + m + ":00");
                    }
                }, hour, minute, true);

                mTimePicker.setTitle(str);
                mTimePicker.show();

            }
        });
    }

    private void createEvent() {
        if (!validateName() || !validateDesc() || !validateSDate() || !validateSTime() ||
                !validateEDate() || !validateETime() || !validateLocation()) {
            return;
        }

        final String nameF = name.getText().toString();
        final String descF = desc.getText().toString();
        final String startF = sDate.getText().toString() + " " + sTime.getText().toString();
        final String endF = eDate.getText().toString() + " " + eTime.getText().toString();
        final String locationF = location.getText().toString();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                EndPoints.NEW_EVENT, new Response.Listener<String>() {

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
                params.put("name", nameF);
                params.put("desc", descF);
                params.put("start_at", startF);
                params.put("end_at", endF);
                params.put("location", locationF);

                Log.e(TAG, "params: " + params.toString());
                return params;
            }
        };

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private boolean validateName() {
        if (name.getText().toString().trim().isEmpty()) {
            nameLayout.setError(getString(R.string.err_msg_general));
            requestFocus(name);
            return false;
        } else {
            nameLayout.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateDesc() {
        if (desc.getText().toString().trim().isEmpty()) {
            descLayout.setError(getString(R.string.err_msg_general));
            requestFocus(desc);
            return false;
        } else {
            descLayout.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateSDate() {
        if (sDate.getText().toString().trim().isEmpty()) {
            sDateLayout.setError(getString(R.string.err_msg_general));
            requestFocus(sDate);
            return false;
        } else {
            sDateLayout.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateSTime() {
        if (sTime.getText().toString().trim().isEmpty()) {
            sTimeLayout.setError(getString(R.string.err_msg_general));
            requestFocus(sTime);
            return false;
        } else {
            sTimeLayout.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateEDate() {
        if (eDate.getText().toString().trim().isEmpty()) {
            eDateLayout.setError(getString(R.string.err_msg_general));
            requestFocus(eDate);
            return false;
        } else {
            eDateLayout.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateETime() {
        if (eTime.getText().toString().trim().isEmpty()) {
            eTimeLayout.setError(getString(R.string.err_msg_general));
            requestFocus(eTime);
            return false;
        } else {
            eTimeLayout.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateLocation() {
        if (location.getText().toString().trim().isEmpty()) {
            locationLayout.setError(getString(R.string.err_msg_general));
            requestFocus(location);
            return false;
        } else {
            locationLayout.setErrorEnabled(false);
        }

        return true;
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;
        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_nameEvent:
                    validateName();
                    break;
                case R.id.input_descEvent:
                    validateDesc();
                    break;
                case R.id.input_sdateEvent:
                    validateSDate();
                    break;
                case R.id.input_stimeEvent:
                    validateSTime();
                    break;
                case R.id.input_edateEvent:
                    validateEDate();
                    break;
                case R.id.input_etimeEvent:
                    validateETime();
                    break;
                case R.id.input_locationEvent:
                    validateLocation();
                    break;
            }
        }
    }
}
