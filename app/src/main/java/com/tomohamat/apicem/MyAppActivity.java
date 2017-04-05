package com.tomohamat.apicem;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.tomohamat.apicem.Model.ApicEm;
import com.tomohamat.apicem.Model.Host;
import com.tomohamat.apicem.Model.NetworkDevice;
import com.tomohamat.apicem.Model.User;

import java.util.ArrayList;

/**
 * Created by Robert on 4/5/2017.
 */

public class MyAppActivity extends AppCompatActivity {

    protected static final int RC_EXIT_SETTINGS_ACTIVITY = 1001;
    private static final String TAG = "MyAppActivity";
    protected int settingsRevision = -1;

    protected ApicEm apicEm;

    /**
     * Empty stub, to be implemented by child.
     *
     * @param networkDevice
     */
    public void setNetworkDevice(NetworkDevice networkDevice) {
        // Empty stub, to be implemented by child.
        Log.d(TAG, "MyAppActivity.setNetworkDevice::empty stub called");
    }

    /**
     * Empty stub, to be implemented by child.
     *
     * @param networkDevices
     */
    public void setNetworkDevices(ArrayList<NetworkDevice> networkDevices) {
        // Empty stub, to be implemented by child.
        Log.d(TAG, "MyAppActivity.setNetworkDevices::empty stub called");
    }

    /**
     * Empty stub, to be implemented by child.
     *
     * @param valid
     */
    public void setSettingsValidity(boolean valid) {
        // Empty stub, to be implemented by child.
        Log.d(TAG, "MyAppActivity.setSettingsValidity::empty stub called");
    }

    /**
     * Empty stub, to be implemented by child.
     *
     * @param successString
     */
    public void showCliRunnerResult(String successString) {
        // Empty stub, to be implemented by child.
        Log.d(TAG, "MyAppActivity.showCliRunnerResult::empty stub called");
    }

    /**
     * Empty stub, to be implemented by child.
     *
     * @param hosts
     */
    public void showHosts(ArrayList<Host> hosts) {
        // Empty stub, to be implemented by child.
        Log.d(TAG, "MyAppActivity.showHosts::empty stub called");
    }

    /**
     * Empty stub, to be implemented by child.
     *
     * @param readCommands
     */
    public void showLegitReads(ArrayList<String> readCommands) {
        // Empty stub, to be implemented by child.
        Log.d(TAG, "MyAppActivity.showLegitReads::empty stub called");
    }

    /**
     * Empty stub, to be implemented by child.
     *
     * @param users
     */
    public void showUsers(ArrayList<User> users) {
        // Empty stub, to be implemented by child.
        Log.d(TAG, "MyAppActivity.showUsers::empty stub called");
    }


}
