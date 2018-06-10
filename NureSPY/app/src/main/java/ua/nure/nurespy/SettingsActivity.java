package ua.nure.nurespy;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SettingsActivity extends AppCompatActivity {

    private AutoCompleteTextView email;
    private EditText password;
    private EditText fullName;
    private EditText groupText;
    private EditText phone;
    String fullnameStr = "";
    String mailStr = "";
    String passwordStr = "";
    String groupStr = "";
    String statusStr = "";
    String phoneStr = "";
    private Switch studSwitch;
    Button updateBtn;
    Button buttonDelete;
    Button buttonSignOut;
    private Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        // setSupportActionBar(toolbar);
        Intent intent = getIntent();

        fullnameStr = intent.getStringExtra("fullname");
        mailStr = intent.getStringExtra("mail");
        passwordStr = intent.getStringExtra("password");
        groupStr = intent.getStringExtra("group");
        statusStr = intent.getStringExtra("status");
        phoneStr = intent.getStringExtra("phone");

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        fullName = findViewById(R.id.fullName);
        phone = findViewById(R.id.phone);
        groupText = findViewById(R.id.groupText);
        studSwitch = findViewById(R.id.switch3);
        updateBtn = findViewById(R.id.updateBtn);
        buttonDelete = findViewById(R.id.buttonDelete);
        buttonSignOut = findViewById(R.id.buttonSignOut);


        fullName.setText(fullnameStr);
        email.setText(mailStr);
        password.setText(passwordStr);
        groupText.setText(groupStr);
        phone.setText(phoneStr);


        boolean checked;
        TextInputLayout group = findViewById(R.id.group);
        if (statusStr.equals("student")) {
            checked = true;
            studSwitch.setChecked(true);
            group.setVisibility(View.VISIBLE);
        } else if (statusStr.equals("teacher")) {
            checked = false;
            studSwitch.setChecked(false);
            group.setVisibility(View.INVISIBLE);
        }

        studSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                TextInputLayout group = findViewById(R.id.group);
                boolean checked = studSwitch.isChecked();
                if (!checked) {
                    checked = true;
                } else {
                    checked = false;
                }
                if (checked) {
                    group.setVisibility(View.VISIBLE);
                } else {
                    group.setVisibility(View.INVISIBLE);
                }
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Global app = (Global) getApplication();
                socket = app.getSocket();
                String status;
                if (studSwitch.isChecked()) {
                    status = "student";
                    groupText = findViewById(R.id.groupText);
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("mail", email.getText().toString());
                        obj.put("password", password.getText().toString());
                        obj.put("fullname", fullName.getText().toString());
                        obj.put("phone", phone.getText().toString());
                        obj.put("group", groupText.getText().toString());
                        obj.put("status", status);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    socket.emit("userEditProfile", obj);

                    Intent intent = new Intent(SettingsActivity.this, NavActivity.class);
                    intent.putExtra("fullname", fullName.getText().toString());
                    intent.putExtra("mail", email.getText().toString());
                    intent.putExtra("password", password.getText().toString());
                    intent.putExtra("group", groupText.getText().toString());
                    intent.putExtra("status", status);
                    intent.putExtra("phone", phone.getText().toString());
                    startActivity(intent);
                } else {
                    status = "teacher";
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("mail", email.getText().toString());
                        obj.put("password", password.getText().toString());
                        obj.put("fullName", fullName.getText().toString());
                        obj.put("phone", phone.getText().toString());
                        obj.put("group", "0");
                        obj.put("status", status);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    socket.emit("userEditProfile", obj);

                    Intent intent = new Intent(SettingsActivity.this, NavActivity.class);
                    intent.putExtra("fullname", fullName.getText().toString());
                    intent.putExtra("mail", email.getText().toString());
                    intent.putExtra("password", password.getText().toString());
                    intent.putExtra("group", "0");
                    intent.putExtra("status", status);
                    intent.putExtra("phone", phone.getText().toString());
                    startActivity(intent);
                }

            }
        });

        buttonSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Global app = (Global) getApplication();
                socket = app.getSocket();
                socket.emit("userDelete");
                socket.disconnect();
                Intent intent = new Intent(SettingsActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Global app = (Global) getApplication();
                socket = app.getSocket();
                socket.emit("signOut");
                socket.on("signOut", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        JSONObject obj = (JSONObject) args[0];
                        int code = 0;
                        try {
                            code = obj.getInt("isSignedOut");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (code == 0){
                            socket.disconnect();
                            Intent intent = new Intent(SettingsActivity.this, SignInActivity.class);
                            startActivity(intent);
                        }

                    }
                });
            }
        });
    }
}