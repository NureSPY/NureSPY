package ua.nure.nurespy;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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

    private GoogleMap map;
    Marker marker;
    AlertDialog.Builder ad;
    GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 123);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //не работает
        Button searchButton = findViewById(R.id.buttonSearch);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = "Searching";
                String message = "Write surname:";
                String button1String = "Search";

                ad = new AlertDialog.Builder(MapsActivity.this);
                ad.setTitle(title);  // заголовок
                ad.setMessage(message); // сообщение
                ad.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
//                Toast.makeText(NavActivity.this, "Вы сделали правильный выбор",
//                        Toast.LENGTH_LONG).show();
                        dialog.cancel();
                    }
                });

                ad.setCancelable(true);
            }
        });

        //не работает
        Button filterButton = findViewById(R.id.buttonFilter);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = "Filtering";
                String message = "Choose:";
                String button1String = "Filter";

                ad = new AlertDialog.Builder(MapsActivity.this);
                ad.setTitle(title);  // заголовок
                ad.setMessage(message); // сообщение
                ad.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
//                Toast.makeText(NavActivity.this, "Вы сделали правильный выбор",
//                        Toast.LENGTH_LONG).show();
                        dialog.cancel();
                    }
                });

                ad.setCancelable(true);
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
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));


        // Add a marker in Sydney and move the camera
        // map.setMapType(GoogleMap.MAP_TYPE_NONE);
        // map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        LatLng sydney = new LatLng(50.015116, 36.228182);
        //map.addMarker(new MarkerOptions().position(sydney).title("Marker in "));

        //   map.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney,17f));

        GroundOverlayOptions newarkMap = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.tmp_map_background))
                .position(sydney, 239f, 200.5f);
        map.addGroundOverlay(newarkMap);

        marker = map.addMarker(new MarkerOptions()
                .position(new LatLng(50.015190, 36.227385))
                .title("User")
                .snippet("user@nure.ua"));
//        GroundOverlayOptions marker1 = new GroundOverlayOptions()
//                .image(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher))
//                .position(new LatLng(50.015190, 36.227385),239f, 200.5f);
//        map.addGroundOverlay(marker1);

        gps = new GPSTracker(getApplicationContext());
        Location location = gps.getLocation();
        double lat, lng;
        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
            LatLng point = new LatLng(lat, lng);
            marker = map.addMarker(new MarkerOptions()
                    .position(point)
                    .title("Melissa")
                    .snippet("mariia.kryvoruchko@nure.ua"));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 17f));
            //Toast.makeText(NavActivity.this, "LONG:" + lng + "\n LAT" + lat, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MapsActivity.this, "location = null", Toast.LENGTH_SHORT).show();
        }
    }


//    private void init() {
//        GroundOverlayOptions newarkMap = new GroundOverlayOptions()
//                .image(BitmapDescriptorFactory.fromResource(R.drawable.tmp_map_background))
//                .position(new LatLng(0, 0), 500000f, 500000f);
//        map.addGroundOverlay(newarkMap);
//    }
}

