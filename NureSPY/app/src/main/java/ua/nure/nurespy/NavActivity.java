package ua.nure.nurespy;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Date;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class NavActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    AlertDialog.Builder ad;

    Socket socket;
    TextView textViewUserName;
    TextView textViewUserMail;
    String fullname = "";
    String phone = "";
    String mail = "";
    String password = "";
    String group = "";
    String status = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_nav);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        textViewUserName = headerView.findViewById(R.id.textViewUserName);
        textViewUserMail = headerView.findViewById(R.id.textViewUserMail);
        Intent intent = getIntent();

        fullname = intent.getStringExtra("fullname");
        mail = intent.getStringExtra("mail");
        password = intent.getStringExtra("password");
        group = intent.getStringExtra("group");
        phone = intent.getStringExtra("phone");
        status = intent.getStringExtra("status");

        textViewUserName.setText(fullname);
        textViewUserMail.setText(mail);

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
            Intent intent = new Intent(NavActivity.this, Settings.class);
            startActivity(intent);
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
            Intent intent = new Intent(NavActivity.this, SettingsActivity.class);
            intent.putExtra("fullname", fullname);
            intent.putExtra("mail", mail);
            intent.putExtra("password", password);
            intent.putExtra("group", group);
            intent.putExtra("status", status);
            intent.putExtra("phone", phone);
            startActivity(intent);
        } else if (id == R.id.nav_maps) {
            Intent intent = new Intent(NavActivity.this, MapsActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
