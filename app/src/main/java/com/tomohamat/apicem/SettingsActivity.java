package com.tomohamat.apicem;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.tomohamat.apicem.Model.ApicEm;

/**
 * A login screen that offers login via email/password.
 */
public class SettingsActivity extends MyAppActivity implements
//        LoaderCallbacks<Cursor>,
        RadioGroup.OnCheckedChangeListener {

    private static final String TAG = "SettingsActivity";

    private ApicEm apicEm;
    private int settingsRevision = -1;
    private String address;
    private String protocol;
    private String port;
    private String username;
    private String password;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
//    private UserLoginTask mAuthTask = null;

    // UI references.
//    private AutoCompleteTextView mEmailView;
    private AutoCompleteTextView mAddressView, mUsernameView, mPortView;
    private RadioGroup protocolRadioGroup;
    private RadioButton httpsRadioButton, httpRadioButton;
    private EditText mPasswordView;
    private Button mSaveButton;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedButton) {
        switch (radioGroup.getId()) {
            case R.id.protocolRadioGroup:
                updatePort(checkedButton);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setupActionBar();
        // Set up the login form.
//        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mAddressView = (AutoCompleteTextView) findViewById(R.id.address);
        mUsernameView = (AutoCompleteTextView) findViewById(R.id.username);
        mPortView = (AutoCompleteTextView) findViewById(R.id.port);
//        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);

        protocolRadioGroup = (RadioGroup) findViewById(R.id.protocolRadioGroup);
        httpsRadioButton = (RadioButton) findViewById(R.id.httpsRadioButton);
        httpRadioButton = (RadioButton) findViewById(R.id.httpRadioButton);
        protocolRadioGroup.setOnCheckedChangeListener(this);

        readSettings();

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    writeSettings();
                    return true;
                }
                return false;
            }
        });

        Button mSaveButton = (Button) findViewById(R.id.saveSettingsButton);
        mSaveButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                writeSettings();
                finish();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

/*
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
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
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
*/

    /**
     * Callback received when a permissions request has been completed.
     */
/*
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }
*/

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
/*
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
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
*/

    /**
     * Shows the progress UI and hides the login form.
     */
/*
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

*/

/*
    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(SettingsActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }
*/

    private void readSettings() {
        SharedPreferences prefs = getSharedPreferences(getString(R.string.pref_identifier), Context.MODE_PRIVATE);

        int revision = prefs.getInt(getString(R.string.pref_revision), 0);

        Log.d(TAG, "readSettings::revision == " + revision + " settingsRevision == " + settingsRevision);

        if (revision > settingsRevision) {
            settingsRevision = revision;
            address = prefs.getString(getString(R.string.pref_address), null);
            protocol = prefs.getString(getString(R.string.pref_protocol), null);
            port = prefs.getString(getString(R.string.pref_port), null);
            username = prefs.getString(getString(R.string.pref_username), null);
            password = prefs.getString(getString(R.string.pref_password), null);
        }

        if (null != address) {
            mAddressView.setText(address);
        }

        if (null != protocol) {
            if (protocol.equals(getString(R.string.button_https))) {
                protocolRadioGroup.check(R.id.httpsRadioButton);
            } else if (protocol.equals(getString(R.string.button_http))) {
                protocolRadioGroup.check(R.id.httpRadioButton);
            }
        }

        if (null != port) {
            mPortView.setText(port);
        }

        if (null != username) {
            mUsernameView.setText(username);
        }

        if (null != password) {
            mPasswordView.setText(password);
        }

    }

    private boolean settingsChanged() {
        if (!mAddressView.getText().toString().equals(address)) {
            return true;
        }

        switch (protocolRadioGroup.getCheckedRadioButtonId()) {
            case R.id.httpsRadioButton:
                if ("http".equals(protocol)) {
                    return true;
                }
            case R.id.httpRadioButton:
                if ("https".equals(protocol)) {
                    return true;
                }
        }

        if (!mPortView.getText().toString().equals(port)) {
            return true;
        }

        Log.d(TAG, "settingsChanged::mUsernameView == " + mUsernameView.getText());
        Log.d(TAG, "settingsChanged::username == " + username);
        if (!mUsernameView.getText().toString().equals(username)) {
            return true;
        }

        return !mPasswordView.getText().toString().equals(password);

    }

    private void updatePort(int checkedButton) {
        switch (checkedButton) {
            case R.id.httpsRadioButton:
                mPortView.setText(getString(R.string.port_https));
                break;
            case R.id.httpRadioButton:
                mPortView.setText(getString(R.string.port_http));
                break;
        }
    }

    private void writeSettings() {
        Log.d(TAG, "writeSettings::settingsChanged == " + settingsChanged());
        if (settingsChanged()) {
            SharedPreferences prefs = getSharedPreferences(getString(R.string.pref_identifier), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            Log.d(TAG, "writeSettings::settingsRevision");

            editor.putInt(getString(R.string.pref_revision), ++settingsRevision);

            Log.d(TAG, "writeSettings::settingsRevision");

            editor.putString(getString(R.string.pref_address), mAddressView.getText().toString());
            switch (protocolRadioGroup.getCheckedRadioButtonId()) {
                case R.id.httpsRadioButton:
                    editor.putString(getString(R.string.pref_protocol), getString(R.string.button_https));
                    break;
                case R.id.httpRadioButton:
                    editor.putString(getString(R.string.pref_protocol), getString(R.string.button_http));
                    break;
            }
            editor.putString(getString(R.string.pref_port), mPortView.getText().toString());
            editor.putString(getString(R.string.pref_username), mUsernameView.getText().toString());
            editor.putString(getString(R.string.pref_password), mPasswordView.getText().toString());

            editor.apply();
        }
    }

/*
    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }
*/
    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
/*
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
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
*/
}

