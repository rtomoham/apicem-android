package com.tomohamat.apicem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tomohamat.apicem.Model.ApicEm;
import com.tomohamat.apicem.Model.Host;
import com.tomohamat.apicem.Model.NetworkDevice;
import com.tomohamat.apicem.Model.User;

import java.util.ArrayList;

public class MainTabbedActivity extends AppCompatActivity implements
        View.OnClickListener {

    private static final String TAG = "MainTabbedActivity";
    private static final int RC_EXIT_SETTINGS_ACTIVITY = 1001;
    private int settingsRevision = -1;
    private boolean settingsValid = false;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private GeneralFragment mGeneralFragment;
    private SwitchFragment mSwitchFragment;
    private ApFragment mApFragment;
    private Button mGetUsersButton, mGetNetworkDevicesButton, mRequestHostsButton, mRequestLegitReadsButton, mRequestTestButton;
    private Spinner mSpinner;
    private TextView mTestView;

    private ArrayList<NetworkDevice> networkDevices;

    private ApicEm apicEm;

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick received for " + view.toString());
        switch (view.getId()) {
            case R.id.requestUsersButton:
                if (apicEm.isInitialized()) {
                    mGeneralFragment.showProgressDialog(true);
                    mGeneralFragment.showPleaseWait();
                    apicEm.requestUsers();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.apicem_not_initialized), Toast.LENGTH_SHORT).show();
                    startActivitySettings();
                }
                break;
/*
            case R.id.requestNetworkDevicesButton:
                if (apicEm.isInitialized()) {
                    apicEm.requestNetworkDevices();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.apicem_not_initialized), Toast.LENGTH_SHORT).show();
                    startActivitySettings();
                }
                break;
*/
            case R.id.requestHostsButton:
                if (apicEm.isInitialized()) {
                    mGeneralFragment.showProgressDialog(true);
                    mGeneralFragment.showPleaseWait();
                    apicEm.requestHosts();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.apicem_not_initialized), Toast.LENGTH_SHORT).show();
                    startActivitySettings();
                }
                break;
            case R.id.requestLegitReadsButton:
                if (apicEm.isInitialized()) {
                    mGeneralFragment.showProgressDialog(true);
                    mGeneralFragment.showPleaseWait();
                    apicEm.requestCliRunnerCommands();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.apicem_not_initialized), Toast.LENGTH_SHORT).show();
                    startActivitySettings();
                }
                break;
/*
            case R.id.requestTestButton:
                if (apicEm.isInitialized()) {
                    apicEm.requestCliRunner();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.apicem_not_initialized), Toast.LENGTH_SHORT).show();
                    startActivitySettings();
                }
                break;
*/
            case R.id.deviceDetailsButton:
                if (apicEm.isInitialized()) {
                    String id = mSwitchFragment.getSelectedDeviceId();
                    mSwitchFragment.showPleaseWait();
                    mSwitchFragment.showProgressDialog(true);
                    apicEm.requestNetworkDevice(id);
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.apicem_not_initialized), Toast.LENGTH_SHORT).show();
                    startActivitySettings();
                }
                break;
            case R.id.cliRunnerButton:
                if (apicEm.isInitialized()) {
                    mSwitchFragment.showPleaseWait();
                    mSwitchFragment.showProgressDialog(true);
                    apicEm.requestCliRunner(mSwitchFragment.getSelectedDeviceId(), mSwitchFragment.getCli());
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.apicem_not_initialized), Toast.LENGTH_SHORT).show();
                    startActivitySettings();
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tabbed);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mGeneralFragment = GeneralFragment.newInstance(this, "", "");
        mGeneralFragment.setListener(this);
        mSwitchFragment = SwitchFragment.newInstance("", "");
        mSwitchFragment.setListener(this);
        mApFragment = ApFragment.newInstance("", "");
        mApFragment.setListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        try {
            // Obtain version name, e.g. 1.3.1
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            // Write version to options menu
            (menu.getItem(0)).setTitle(getString(R.string.version) + ": " + version);
        } catch (Exception e) {
            // do nothing
        }

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
            startActivitySettings();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        readSettings();

        if (apicEm.isInitialized()) {
//            apicEm.testSettings();
//            apicEm.requestNetworkDevices();
        } else {
            startActivitySettings();
        }
    }

    private void readSettings() {
        SharedPreferences prefs = getSharedPreferences(getString(R.string.pref_identifier), Context.MODE_PRIVATE);

        int revision = prefs.getInt(getString(R.string.pref_revision), 0);

        Log.d(TAG, "readSettings::revision == " + revision + " settingsRevision == " + settingsRevision);

        if (revision > settingsRevision) {
            settingsRevision = revision;
            String address = prefs.getString(getString(R.string.pref_address), null);
            String protocol = prefs.getString(getString(R.string.pref_protocol), null);
            String port = prefs.getString(getString(R.string.pref_port), null);
            String username = prefs.getString(getString(R.string.pref_username), null);
            String password = prefs.getString(getString(R.string.pref_password), null);

            apicEm = new ApicEm(this, address, protocol, port, username, password);
            apicEm.testSettings();
        }
    }

    public void setNetworkDevice(NetworkDevice device) {
        mSwitchFragment.showProgressDialog(false);
        mSwitchFragment.setNetworkDevice(device);
    }

    public void setNetworkDevices(ArrayList<NetworkDevice> devices) {
        mSwitchFragment.setNetworkDevices(devices);
    }

    public void setSettingsValidity(boolean valid) {
        Log.d(TAG, "setSettingsValidity " + valid);
        settingsValid = valid;
        if (valid) {
            mGeneralFragment.showProgressDialog(false);
            mGeneralFragment.enableButtons();
//            apicEm.requestApicEmVersion();
            apicEm.requestNetworkDevices();
        } else {
            // disable all buttons, except settings
            mGeneralFragment.disableButtons();
            Toast.makeText(getApplicationContext(), "Error in settings: " + apicEm.getError(), Toast.LENGTH_SHORT).show();
            startActivitySettings();
        }
    }

    public void showCliRunnerResult(String result) {
        mSwitchFragment.showProgressDialog(false);
        mSwitchFragment.showResult(result);
    }

    public void showHosts(ArrayList<Host> hosts) {
        mGeneralFragment.showProgressDialog(false);
        mGeneralFragment.showHosts(hosts);
    }

    public void showLegitReads(ArrayList<String> readCommands) {
        mGeneralFragment.showProgressDialog(false);
        mGeneralFragment.showLegitReads(readCommands);
    }

    public void showNetworkDevices(ArrayList<NetworkDevice> devices) {
        mGeneralFragment.showProgressDialog(false);
        mGeneralFragment.showNetworkDevices(devices);
    }

    public void showUsers(ArrayList<User> users) {
        mGeneralFragment.showProgressDialog(false);
        mGeneralFragment.showUsers(users);
    }

    public void startActivitySettings() {
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), SettingsActivity.class);
        startActivity(intent);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main_tabbed, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));

            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            switch (position) {
                case 0:
                    return mGeneralFragment;
                case 1:
                    return mSwitchFragment;
                default:
                    return mApFragment;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "GENERAL";
                case 1:
                    return "SWITCH";
                case 2:
                    return "AP";
            }
            return null;
        }
    }


}
