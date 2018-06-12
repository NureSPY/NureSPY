package ua.nure.nurespy;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import java.io.IOException;
import java.util.Map;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap map;
    ProviderLocationTracker plt;
    Marker marker;
    Marker markerSomeoneNew;
    AlertDialog.Builder adSearch;
    AlertDialog.Builder adFilter;
    AlertDialog.Builder adFilterGroup;
    AlertDialog.Builder adFilterStatus;
    String myFullnameStr = "";
    String myMailStr = "";
    String passwordStr = "";
    String groupStr = "";
    String statusStr = "";
    String phoneStr = "";
    private Map<String, Marker> hashMap;
    // private LocationTracker.LocationUpdateListener lul = new LocationTracker.LocationUpdateListener() ;

    // GPSTracker gps;
    private Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Global app = (Global) getApplication();
        socket = app.getSocket();

        hashMap = new HashMap<String, Marker>();
        Intent intent = getIntent();

        myFullnameStr = intent.getStringExtra("fullname");
        myMailStr = intent.getStringExtra("mail");
        passwordStr = intent.getStringExtra("password");
        groupStr = intent.getStringExtra("group");
        statusStr = intent.getStringExtra("status");
        phoneStr = intent.getStringExtra("phone");


        socket.on("userMove", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject objSomeoneMove = (JSONObject) args[0];
                String objMail = "";
                double lat, lng;
                try {
                    objMail = objSomeoneMove.getString("mail");
                    lat = objSomeoneMove.getDouble("latitude");
                    lng = objSomeoneMove.getDouble("longitude");
                    LatLng pointSomeoneNew = new LatLng(lat, lng);

                    hashMap.put(objMail, map.addMarker(new MarkerOptions()
                            .position(pointSomeoneNew)
                            .snippet(objMail)
                            .title("Another user")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        String title = "Searching";
        String message = "Enter mail:";
        String button1String = "Search";

        adSearch = new AlertDialog.Builder(MapsActivity.this);
        adSearch.setTitle(title);
        adSearch.setMessage(message);
        adSearch.setCancelable(true);
        final EditText input = new EditText(this);
        input.setInputType(1);
        adSearch.setView(input).setNegativeButton(button1String, new android.content.DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("mail", input.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                socket.emit("searchUser", obj);

                dialog.cancel();
            }
        });

        socket.on("searchUser", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject obj = (JSONObject) args[0];
                int code = 0;
                try {
                    code = obj.getInt("err");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (code == 0) {
                    //показать на карте
                } else if (code == 1) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MapsActivity.this, "User is not in NURE", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else if (code == 2) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MapsActivity.this, "There are no such user", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });

        String title1 = "Filtering";
        String message1 = "Choose one:";
        String button1String1 = "Filter";
        //  final String[] filters = {"Status", "Group"};
        adFilter = new AlertDialog.Builder(MapsActivity.this);
        adFilter.setTitle(title1);
        adFilter.setMessage(message1);
        adFilter.setCancelable(true)
                .setPositiveButton("Status",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                adFilterStatus.show();
                                dialog.cancel();
                            }
                        })
                .setNegativeButton("Group",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                adFilterGroup.show();
                                dialog.cancel();
                            }
                        });

        adFilterStatus = new AlertDialog.Builder(MapsActivity.this);

        adFilterStatus.setTitle(title1);
        adFilterStatus.setMessage(message1).setPositiveButton("Student",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int id) {
                        JSONObject obj = new JSONObject();
                        try {
                            obj.put("status", "student");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        socket.emit("filterUserStatus", obj);

                        dialog.cancel();
                    }
                })
                .setNegativeButton("Teacher",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                JSONObject obj = new JSONObject();
                                try {
                                    obj.put("status", "teacher");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                socket.emit("filterUserStatus", obj);

                                dialog.cancel();
                            }
                        });

        adFilterGroup = new AlertDialog.Builder(MapsActivity.this);
        final EditText inputGroup = new EditText(this);
        adFilterGroup.setTitle(title1);
        adFilterGroup.setMessage("Enter group:");
        inputGroup.setInputType(1);
        adFilterGroup.setView(inputGroup).setNegativeButton("Filter", new android.content.DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("group", inputGroup.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                socket.emit("filterUserGroup", obj);

                dialog.cancel();
            }
        });

        socket.on("filterUserStatus", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject obj = (JSONObject) args[0];
                int code = 0;
                try {
                    code = obj.getInt("err");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (code == 0) {

                } else if (code == 1) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MapsActivity.this, "There are no such users", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        socket.on("filterUserFilter", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject obj = (JSONObject) args[0];
                int code = 0;
                try {
                    code = obj.getInt("err");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (code == 0) {
                    //показать на карте
                } else if (code == 1) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MapsActivity.this, "There are no such group", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


        Button searchButton = findViewById(R.id.buttonSearch);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adSearch.show();
            }
        });

        Button filterButton = findViewById(R.id.buttonFilter);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adFilter.show();
            }
        });

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

        plt = new ProviderLocationTracker(getApplicationContext(), ProviderLocationTracker.ProviderType.GPS);
        plt.start(new LocationUpdateListener() {
            @Override
            public void onUpdate(Location oldLoc, long oldTime, Location newLoc, long newTime) {
                double latMy, lngMy;
                latMy = newLoc.getLatitude
                        ();
                lngMy = newLoc.getLongitude();
                LatLng pointNew = new LatLng(latMy, lngMy);
                JSONObject objLoc = new JSONObject();
//                hashMap.put(myMailStr, map.addMarker(new MarkerOptions()
//                        .position(pointNew)
//                        .snippet(myMailStr)
//                        .title(myFullnameStr)));
                try {
                    objLoc.put("mail", myMailStr);
                    objLoc.put("latitude", latMy);
                    objLoc.put("longitude",  lngMy);
                    objLoc.put("height", 2.00);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                marker.setPosition(pointNew);
                socket.emit("userMove", objLoc);
            }
        });
        // gps = new GPSTracker(getApplicationContext());
        //Location location = gps.getLastKnownLocation();
        if (plt.hasLocation()) {
            Location location1 = plt.getLocation();
            double lat, lng;
            if (location1 != null) {
                lat = location1.getLatitude();
                lng = location1.getLongitude();
                LatLng point = new LatLng(lat, lng);
                marker = map.addMarker(new MarkerOptions()
                        .position(point)
                        .title(myFullnameStr)
                        .snippet(myMailStr));
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 17f));

                hashMap.put(marker.getSnippet(),marker);
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MapsActivity.this, "location = null", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        } else if (plt.hasPossiblyStaleLocation()) {
            Location location2 = plt.getPossiblyStaleLocation();
            double lat, lng;
            if (location2 != null) {
                lat = location2.getLatitude();
                lng = location2.getLongitude();
                LatLng point = new LatLng(lat, lng);
                marker = map.addMarker(new MarkerOptions()
                        .position(point)
                        .title(myFullnameStr)
                        .snippet(myMailStr));
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 17f));

                hashMap.put(marker.getSnippet(), marker);
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

}

