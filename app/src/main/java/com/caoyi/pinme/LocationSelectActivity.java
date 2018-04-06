package com.caoyi.pinme;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

public class LocationSelectActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Toolbar mToolbar;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private String mCurrentUid, mWithUid;

    private DatabaseReference mMessageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_select);

        mCurrentUid = getIntent().getStringExtra("current_uid");
        mWithUid = getIntent().getStringExtra("with_uid");

        mToolbar = findViewById(R.id.maps_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Maps");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);

        mMessageReference = FirebaseDatabase.getInstance().getReference().child("Messages");

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);

                Location lastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                centerMapOnLocation(lastKnownLocation, "Your location");

            }


        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {


            @Override
            public void onCameraMoveStarted(int reason) {
                LatLng center = mMap.getCameraPosition().target;
                Location centerLocation = new Location(LocationManager.GPS_PROVIDER);
                centerLocation.setLatitude(center.latitude);
                centerLocation.setLongitude(center.longitude);
                centerMapOnLocation(centerLocation, "Your Location");
            }

        });


        // Add a marker in Sydney and move the camera
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                centerMapOnLocation(location, "Your location");
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

//        if (Build.VERSION.SDK_INT < 23) {
//
//            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
//
//        } else {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);

            Location lastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            centerMapOnLocation(lastKnownLocation, "Your location");

            mMap.moveCamera(CameraUpdateFactory.zoomTo(20));

        } else {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        }


//        }

    }

    private void centerMapOnLocation(Location location, String title) {
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(userLocation).title(title));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        Log.i("CREATE_MENU", "onCreateOptionsMenu() is called");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.maps_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.maps_locate:
                return true;
            case R.id.maps_send:
                LatLng latLng = mMap.getCameraPosition().target;
                String mapUrl = "https://www.google.com/maps/@" + latLng.latitude + "," + latLng.longitude + ",18z";

                Log.i("CURRENT_LOCATION", mapUrl);

                String currentUserRef = mCurrentUid + "/" + mWithUid;
                String withUserRef = mWithUid + "/" + mCurrentUid;

                Map msgMap = new HashMap();
                msgMap.put("message", mapUrl);
                msgMap.put("type", "location");
                msgMap.put("timestamp", ServerValue.TIMESTAMP);
                msgMap.put("from", mCurrentUid);


                String pushId = FirebaseDatabase.getInstance().getReference().child("Messages")
                        .child(mCurrentUid).child(mWithUid).push().getKey();



                Map msgUserMap = new HashMap();
                msgUserMap.put(currentUserRef + "/" + pushId, msgMap);
                msgUserMap.put(withUserRef + "/" + pushId, msgMap);



                mMessageReference.updateChildren(msgUserMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            Log.e("MESSAGE_LOG", databaseError.getMessage());
                        }
                    }
                });
                finish();
                return true;
            default:
                return true;
        }
    }



}
