package ua.nure.nurespy;

import android.content.Intent;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Marker marker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

//        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        Button btnTest =  findViewById(R.id.btnTest);
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Add a marker in Sydney and move the camera
                // mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
               // mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                LatLng sydney = new LatLng(50.015116, 36.228182);
               //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in "));
                // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,12.0f));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney,17f));

                GroundOverlayOptions newarkMap = new GroundOverlayOptions()
                        .image(BitmapDescriptorFactory.fromResource(R.drawable.tmp_map_background))
                        .position(sydney, 239f, 200.5f);
                mMap.addGroundOverlay(newarkMap);

                marker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(50.015190, 36.227385))
                        .title("Melissa")
                        .snippet("mariia.kryvoruchko@nure.ua"));
//        GroundOverlayOptions marker1 = new GroundOverlayOptions()
//                .image(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher))
//                .position(new LatLng(50.015190, 36.227385),239f, 200.5f);
//        mMap.addGroundOverlay(marker1);

                GPSTracker gpsTracker = new GPSTracker(getApplicationContext());
                Location location = gpsTracker.getLocation();
                double lat, lng;
                if (location != null) {
                    lat = location.getLatitude();
                    lng = location.getLongitude();
                    marker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(lat, lng))
                            .title("Melissa")
                            .snippet("mariia.kryvoruchko@nure.ua"));
                    //Toast.makeText(NavActivity.this, "LONG:" + lng + "\n LAT" + lat, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MapsActivity.this, "location = null", Toast.LENGTH_SHORT).show();
                }

            }
        });
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
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }



//    private void init() {
//        GroundOverlayOptions newarkMap = new GroundOverlayOptions()
//                .image(BitmapDescriptorFactory.fromResource(R.drawable.tmp_map_background))
//                .position(new LatLng(0, 0), 500000f, 500000f);
//        mMap.addGroundOverlay(newarkMap);
//    }
}

