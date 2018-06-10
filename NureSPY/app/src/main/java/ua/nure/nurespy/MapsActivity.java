package ua.nure.nurespy;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

import java.io.IOException;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap map;
    Marker marker;
    AlertDialog.Builder ad;
    GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        //не работает
//        Button searchButton = findViewById(R.id.buttonSearch);
//        searchButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String title = "Searching";
//                String message = "Write surname:";
//                String button1String = "Search";
//
//                ad = new AlertDialog.Builder(MapsActivity.this);
//                ad.setTitle(title);  // заголовок
//                ad.setMessage(message); // сообщение
//                ad.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int arg1) {
////                Toast.makeText(NavActivity.this, "OK",
////                        Toast.LENGTH_LONG).show();
//                        dialog.cancel();
//                    }
//                });
//
//                ad.setCancelable(true);
//            }
//        });
//

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        LatLng centerOfNURE = new LatLng(50.015116, 36.228182);
       // map.animateCamera(CameraUpdateFactory.newLatLngZoom(centerOfNURE, 17f));
        GroundOverlayOptions newarkMap = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.tmp_map_background))
                .position(centerOfNURE, 239f, 200.5f);
        map.addGroundOverlay(newarkMap);

        gps = new GPSTracker(getApplicationContext());
        Location location = gps.getLastKnownLocation();
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
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MapsActivity.this, "location = null", Toast.LENGTH_SHORT).show();
                }
            });

        }

    }
}

