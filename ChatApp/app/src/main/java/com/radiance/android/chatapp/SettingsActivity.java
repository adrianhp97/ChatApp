package com.radiance.android.chatapp;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {
    public static final String
            KEY_PREF_SWITCH = "switch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

        SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(this);
        Boolean switchPref = sharedPref.getBoolean
                (SettingsActivity.KEY_PREF_SWITCH, false);

        String toast = "Notification Off";
        if(switchPref){
            toast = "Notification On";
        }
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
    }
}
