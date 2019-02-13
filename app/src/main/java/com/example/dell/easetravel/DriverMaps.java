package com.example.dell.easetravel;

import android.*;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DriverMaps extends ActionBarActivity implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Marker markerOptions;

    FirebaseAuth authentication;
    FirebaseDatabase db;
    DatabaseReference ref;

    String username;
    int map_counter = 0;

    Locationn loc = new Locationn();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (checkGooglePlayService()) {
            setContentView(R.layout.activity_driver_maps);

            authentication = FirebaseAuth.getInstance();
            db = FirebaseDatabase.getInstance();
            ref = db.getReference();

            Intent intent = getIntent();
            username = intent.getStringExtra("username");

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }

    public boolean checkGooglePlayService() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);

        if (isAvailable == ConnectionResult.SUCCESS)
            return true;
        else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        } else
            Toast.makeText(this, "Cannot Connect To Google Play Services", Toast.LENGTH_LONG).show();

        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API)
                .addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();

        mGoogleApiClient.connect();
    }

    public void goToLocation(double lat, double longi) {

        loc.setLocation(lat,longi);
        mMap.clear();

        LatLng obj = new LatLng(lat, longi);
        markerOptions = mMap.addMarker(new MarkerOptions().position(obj));

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(obj, 15));

        map_counter ++;
        ref.child("Users").child("Drivers").child(username).child("Latitude").setValue(lat);
        ref.child("Users").child("Drivers").child(username).child("Longitude").setValue(longi);
    }

    LocationRequest locationRequest;

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest = locationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(100);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                return;
            }
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void checkForMarker()
    {
        if(markerOptions != null)
            markerOptions.remove();
    }

    @Override
    public void onLocationChanged(Location location) {

        if( location == null )
            Toast.makeText(this, "Cannot Find Location", Toast.LENGTH_SHORT).show();
        else
        {
            goToLocation(location.getLatitude(), location.getLongitude());
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.location, menu);

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.location_on) {

            ref.child("Users").child("Drivers").child(username).child("Location").setValue("On");
        }
        else if (id == R.id.location_off) {

            ref.child("Users").child("Drivers").child(username).child("Location").setValue("Off");
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed()
    {
    }
}
