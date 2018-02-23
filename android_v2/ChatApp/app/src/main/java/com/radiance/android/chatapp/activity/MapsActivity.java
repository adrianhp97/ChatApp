package com.radiance.android.chatapp.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Date;

import com.radiance.android.chatapp.R;

import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;

/**
 * Created by ASUS PC on 22/02/2018.
 */

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private String TAG = MapsActivity.class.getSimpleName();

    private GoogleMap mMap;

    private GoogleApiClient googleApiClient;

    Intent prevIntent;

    private static final int LOCATION_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        prevIntent = getIntent();

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

    }

    public void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    public void onStop() {
        super.onStop();
        googleApiClient.disconnect();

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        checkLocationandAddToMap();
    }

    @Override
    public void onConnected(Bundle bundle) {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
                    checkLocationandAddToMap();
                } else
                    Toast.makeText(this, "Location Permission Denied", Toast.LENGTH_SHORT).show();
                break;

        }

    }

    private void checkLocationandAddToMap() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            return;
        }

        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        Log.e(TAG, "LOCATION: " + location);

        // Setup Current Location
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 11.0f));
        mMap.getUiSettings().setZoomControlsEnabled(true);

        final MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(location.getLatitude(), location.getLongitude()));
        markerOptions.title("You are Here");
        Date today = new Date();
        markerOptions.snippet(String.valueOf(today));
        mMap.addMarker(markerOptions);

        String actCode = prevIntent.getStringExtra("actCode");

        if(actCode.equals("eventCreate")){
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng position) {
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(new LatLng(position.latitude,position.longitude))
                            .draggable(true).visible(true));
                    Intent postIntent = new Intent(getApplicationContext(), CreateEventActivity.class);
                    postIntent.putExtra("latitude", String.valueOf(position.latitude));
                    postIntent.putExtra("longitude", String.valueOf(position.longitude));
                    startActivity(postIntent);
                }
            });
        }else{
            double latitude = Float.parseFloat(prevIntent.getStringExtra("latitude"));
            double longitude = Float.parseFloat(prevIntent.getStringExtra("longitude"));
            markerOptions.position(new LatLng(latitude, longitude));
            markerOptions.title(prevIntent.getStringExtra("name"));
            markerOptions.snippet(getIntent().getStringExtra("start_at"));
            mMap.addMarker(markerOptions);
        }
    }
}
