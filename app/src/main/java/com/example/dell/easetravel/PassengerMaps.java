package com.example.dell.easetravel;

import android.*;
import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PassengerMaps extends ActionBarActivity implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Marker markerOptions;

    FirebaseAuth authentication;
    FirebaseDatabase db;
    DatabaseReference ref;

    String username, getBusNumber = "", getBusNumberNearest = "";
    int map_counter = 0;

    Marker[] marker_options = new Marker[100];
    int m_opt = 0;

    boolean searchByBusStatus = false;
    boolean nearestBus = false;
    boolean marker_click = false;

    Locationn loc = new Locationn();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (checkGooglePlayService()) {
            setContentView(R.layout.activity_passenger_maps);

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
            Toast.makeText(this, "Cannot Connect to Google Play Service", Toast.LENGTH_SHORT).show();

        return false;
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API)
                .addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();

        mMap.setOnMarkerClickListener(this);

    }
    public boolean onMarkerClick(final Marker marker)
    {
        marker_click = true;
        final String driver_name = marker.getTag().toString();

        ref.child("Users").child("Drivers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String number_n = "";
                String lat_n = "";
                String longi_n = "";

                for(DataSnapshot childs: dataSnapshot.getChildren())
                {
                    if(childs.getKey().equals(driver_name))
                    {
                        number_n = childs.child("Bus Number").getValue().toString();
                        lat_n = childs.child("Latitude").getValue().toString();
                        longi_n = childs.child("Longitude").getValue().toString();
                    }
                }
                showTimeDistance_DriverInfo(number_n, lat_n, longi_n, driver_name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return true;

    }
    public void showTimeDistance_DriverInfo(String num, String lati, String longii, String driver)
    {
        if(marker_click) {
            Location startPoint = new Location("Passenger Location");
            startPoint.setLatitude(loc.getLat());
            startPoint.setLongitude(loc.getLongi());

            Location endPoint = new Location("Driver Location");
            endPoint.setLatitude(Double.parseDouble(lati));
            endPoint.setLongitude(Double.parseDouble(longii));

            DecimalFormat df = new DecimalFormat("#.00");

            double distance = Math.round(startPoint.distanceTo(endPoint));
            String time = df.format(((distance / 1000) / 25 * 60));
            double time_t = Double.parseDouble(time);
            int minutes = (int) time_t;
            double seconds = (time_t * 100) % 100;
            if (seconds > 59) {
                minutes = minutes + 1;
                seconds = seconds - 60;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("Info").setMessage("Bus " + num + " is " + distance + " meters away" + "\n" + "Aproximate Time is " + minutes + " minutes " + seconds + " seconds" + "\n" + "Driver is Mr." + driver).setPositiveButton("ok", null);
            builder.show();

            marker_click = false;
        }
    }
    public void goToLocation(final double lat, final double longi) {

        checkForMarker();

        LatLng object = new LatLng(lat, longi);
        markerOptions = mMap.addMarker(new MarkerOptions().position(object).title("Your Location"));

        if(map_counter == 0)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(object, 15));

        map_counter ++;

        ref.child("Users").child("Passengers").child(username).child("Latitude").setValue(lat);
        ref.child("Users").child("Passengers").child(username).child("Longitude").setValue(longi);

        loc.setLocation(lat,longi);

        ref.child("Users").child("Drivers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                m_opt = 0;
                for( DataSnapshot childs : dataSnapshot.getChildren() )
                {
                    if(searchByBusStatus)
                    {
                        if((childs.child("Location").getValue().toString()).equals("On"))
                        {
                            if((childs.child("Bus Number").getValue().toString()).equals(getBusNumber))
                            {
                                String lat_s = childs.child("Latitude").getValue().toString();
                                String long_s = childs.child("Longitude").getValue().toString();

                                double lat_n = Double.parseDouble(lat_s);
                                double longi_n = Double.parseDouble(long_s);

                                LatLng object_n = new LatLng(lat_n, longi_n);
                                if(marker_options[m_opt] != null)
                                    marker_options[m_opt].remove();

                                marker_options[m_opt] = mMap.addMarker(new MarkerOptions().position(object_n).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                                marker_options[m_opt].setTag(childs.getKey());
                            }
                            else
                            {
                                if(marker_options[m_opt] != null)
                                    marker_options[m_opt].remove();
                            }
                            m_opt ++;
                        }
                        else if((childs.child("Location").getValue().toString()).equals("Off"))
                        {
                            if(marker_options[m_opt] != null)
                                marker_options[m_opt].remove();

                            m_opt ++;
                        }
                    }
                    else
                    {
                        if((childs.child("Location").getValue().toString()).equals("On"))
                        {
                            String lat_s = childs.child("Latitude").getValue().toString();
                            String long_s = childs.child("Longitude").getValue().toString();

                            double lat_n = Double.parseDouble(lat_s);
                            double longi_n = Double.parseDouble(long_s);

                            LatLng object_n = new LatLng(lat_n, longi_n);
                            if(marker_options[m_opt] != null)
                                marker_options[m_opt].remove();

                            marker_options[m_opt] = mMap.addMarker(new MarkerOptions().position(object_n).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                            marker_options[m_opt].setTag(childs.getKey());
                            m_opt ++;
                        }
                        else if((childs.child("Location").getValue().toString()).equals("Off"))
                        {
                            if(marker_options[m_opt] != null)
                                marker_options[m_opt].remove();

                            m_opt ++;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    LocationRequest locationRequest;

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(100);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

    @Override
    public void onLocationChanged(Location location) {

        if( location == null )
            Toast.makeText(this, "Cannot Find Location", Toast.LENGTH_SHORT).show();
        else
        {
            goToLocation(location.getLatitude(), location.getLongitude());
        }

    }

    private void checkForMarker() {

        if( markerOptions != null )
            markerOptions.remove();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.pass_men, menu);

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.bus_cat) {

            searchBusByCategory();
        }
        else if(id == R.id.bus_all)
        {
            showAllBuses();
        }

        return super.onOptionsItemSelected(item);
    }
    public void searchBusByCategory(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Bus Number");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Search", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                getBusNumber = input.getText().toString();
                searchByBusStatus = true;
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    public void showAllBuses(){

        getBusNumber = "";
        searchByBusStatus = false;

        getBusNumberNearest = "";
        nearestBus = false;
    }
    @Override
    public void onBackPressed()
    {

    }
}
