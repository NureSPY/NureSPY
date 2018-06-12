package ua.nure.nurespy;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class SignUpActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    private Socket socket;
    //MyTask mt;
    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView email;
    private EditText password;
    private EditText fullName;
    private EditText groupText;
    private EditText phone;
    private View mProgressView;
    private View mLoginFormView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        fullName = findViewById(R.id.fullName);
        phone = findViewById(R.id.phone);


        final Switch studSwitch = findViewById(R.id.switch3);
        studSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            boolean checked = true;
            TextInputLayout group = findViewById(R.id.group);

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
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

        Button signUpButton = (Button) findViewById(R.id.sign_up_btn);
        signUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                Global app = (Global) getApplication();
                socket = app.getSocket();
                socket.connect();
                attemptLogin();
                String status;
                if (studSwitch.isChecked()) {
                    if (email.getText().toString().trim().length() != 0 && password.getText().toString().trim().length() != 0 &&
                            fullName.getText().toString().trim().length() != 0 &&
                            phone.getText().toString().trim().length() != 0 &&
                            groupText.getText().toString().trim().length() != 0) {
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

                        socket.emit("signUp", obj);
                    }
                } else {
                    if (email.getText().toString().trim().length() != 0 && password.getText().toString().trim().length() != 0 &&
                            fullName.getText().toString().trim().length() != 0 &&
                            phone.getText().toString().trim().length() != 0) {
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

                        socket.emit("signUp", obj);

                    }
                }

                socket.on("signUp", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        JSONObject obj = (JSONObject) args[0];
                        String code = "";
                        try {
                            code = obj.getString("err");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (code.equals("0")) {
                            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                            startActivity(intent);
                        } else if (code.equals("1")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Mail is already used.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(email, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        email.setError(null);
        password.setError(null);

        // Store values at the time of the login attempt.
        String email = this.email.getText().toString();
        String password = this.password.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            this.password.setError(getString(R.string.error_invalid_password));
            focusView = this.password;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            this.email.setError(getString(R.string.error_field_required));
            focusView = this.email;
            cancel = true;
        } else if (!isEmailValid(email)) {
            this.email.setError(getString(R.string.error_invalid_email));
            focusView = this.email;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(SignUpActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        email.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


//    public class MyTask extends AsyncTask<Void, Integer, String> {
//        // 3 параметр это тип возвращаемых данных методом doInBackground, его я буду обрабатывать в onPostExecute
//        // фоновая работа
//        @Override
//        protected String doInBackground(Void... params) {
////            try {
////                return SendServer.sendRegData("" + email.getText().toString() + "", "" + password.getText().toString() + "");
////            } catch (IOException e) {
////                e.printStackTrace();
////                return e.toString();
////            }
//        }
//
//        // выполняется после doInBackground, имеет доступ к UI
//        protected void onPostExecute(String result) {
////            JSONObject object = null;
////            try {
////                object = new JSONObject(result);
////            } catch (JSONException e1) {
////                e1.printStackTrace();
////            }
////            try {
////                String status = object.getString("status");
////            } catch (JSONException e1) {
////                e1.printStackTrace();
////            }
//        }
//
//
//    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                password.setError(getString(R.string.error_incorrect_password));
                password.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }


    }
}

