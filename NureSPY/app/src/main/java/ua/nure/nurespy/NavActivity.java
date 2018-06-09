package ua.nure.nurespy;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;


import android.provider.Settings;
import android.widget.Toast;

import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class NavActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    AlertDialog.Builder ad;
    Button buttonGetLoc;
    Button buttonDisconnect;
    Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

      //  ActivityCompat.requestPermissions(NavActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);

        setContentView(R.layout.activity_nav);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String title = "Searching";
        String message = "Write surname:";
        String button1String = "Search";

        ad = new AlertDialog.Builder(NavActivity.this);
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


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ad.show();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        setContentView(R.layout.app_bar_nav);
        buttonGetLoc = findViewById(R.id.buttonGetLoc);
        buttonDisconnect = findViewById(R.id.buttonDisconnect);
        try
        {
            socket = IO.socket("http://178.165.46.109:3002");
        }
        catch (URISyntaxException ex){
            ex.printStackTrace();
        }
        double lt, ln;
        ActivityCompat.requestPermissions(NavActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},123);
        buttonGetLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                socket.connect();
                GPSTracker g = new GPSTracker(getApplicationContext());
                Location l = g.getLocation();
                double lat, lng;
                if(l!= null){
                    lat = l.getLatitude();
                    lng = l.getLongitude();

                    Toast.makeText(getApplicationContext(), "LONG:" + lng + "\n LAT" + lat, Toast.LENGTH_LONG).show();
                }
            }
        });
        buttonDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                socket.disconnect();
                // socket.close();
            }
        });


        //socket.open();
        // boolean connect = false;
        JSONObject obj = new JSONObject();

        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "You`ve connected!", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                Toast.makeText(getApplicationContext(), "Error connect", Toast.LENGTH_SHORT).show();
            }
        }).on(Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Toast.makeText(getApplicationContext(), "You`ve connected!", Toast.LENGTH_SHORT).show();
            }
        }).on(Socket.EVENT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {

            }
        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "You`ve disconnected!", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
        socket.connect();
        if(socket.connected()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "STATE = CONNECTED", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "STATE = DISCONNECTED", Toast.LENGTH_SHORT).show();
                }
            });
        }





        //   boolean isConnected =  socket.connected();
           /* if(connect){
                Toast.makeText(getApplicationContext(), "You`ve connected!", Toast.LENGTH_SHORT).show();
            } else{
                Toast.makeText(getApplicationContext(), "Try again! Masha off server!", Toast.LENGTH_SHORT).show();

            }*/

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_meet) {
            Intent intent = new Intent(NavActivity.this, EventActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(NavActivity.this, Settings.class);
            startActivity(intent);
        } else if (id == R.id.nav_maps) {
            Intent intent = new Intent(NavActivity.this, MapsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(NavActivity.this, Settings.class);
            startActivity(intent);
        } /*else if (id == R.id.nav_send) {

        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
