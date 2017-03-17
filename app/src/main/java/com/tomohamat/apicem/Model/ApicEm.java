package com.tomohamat.apicem.Model;

import android.util.Log;

import com.tomohamat.apicem.MainTabbedActivity;
import com.tomohamat.apicem.TaskDelegate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Robert Tomohamat (rtomoham) on 3/11/2017.
 */
public class ApicEm implements TaskDelegate {

    public static final int REQUEST_TICKET = 1;
    public static final int REQUEST_NETWORK_DEVICES = 3;

    //	public static final int REST_CODE_TICKET = 1;
//	public static final int REST_CODE_USER = 2;
    public static final int REQUEST_HOSTS = 4;
    public static final int REQUEST_FLOW_ANALYSIS = 5;
    public static final int REQUEST_LEGIT_READS = 6;
    public static final int REQUEST_READ = 7;
    public static final int REQUEST_TASK_COMPLETION = 8;
    public static final int REQUEST_FILE = 9;
    public static final int REQUEST_NETWORK_DEVICE = 10;
    public static final String PROTOCOL_HTTP = "http";
    public static final String PROTOCOL_HTTPS = "https";
    private static final String TAG = "ApicEm";
    private static final String URL_SUFFIX = "/api/v1";
    private MainTabbedActivity activity;
    private String ticketUrl, taskUrl, fileUrl;
    private String successString, failureString, blacklistedString;

    private String protocol;
    private String address;
    private String port;
    private String username;
    private String password;

    private boolean settingsTested = false;

    //	private String authenticationTicket;
    private int request;

    private ArrayList users, networkDevices, hosts, legitReads;

    private String sourceIp, destIp;

    private RestClient restClient;

    public ApicEm(MainTabbedActivity activity) {
        this.activity = activity;
    }

    public ApicEm(MainTabbedActivity activity, String address, String protocol, String port, String username, String password) {
        this(activity);

        this.address = address;
        this.protocol = protocol;
        this.port = port;
        this.username = username;
        this.password = password;

    }

    /**
     * Returns the base url string, e.g. https://sandboxapi.cisco.com:443/api/v1
     */
    private String getBaseUrlString() {
        return protocol + "://" + this.address + ":" + this.port + URL_SUFFIX;
    }

    public boolean getSettingsTested() {
        return settingsTested;
    }

    public void setSettingsTested(boolean tested) {
        settingsTested = tested;
    }

    /**
     * Returns true if ApicEm has all the necessary data to perform REST calls, false otherwise.
     */
    public boolean isInitialized() {
        if (null == address) {
            return false;
        }

        if (null == protocol) {
            return false;
        }

        if (null == port) {
            return false;
        }

        if (null == username) {
            return false;
        }

        return null != password;

    }

    public ArrayList getNetworkDevices() {
        return networkDevices;
    }

    public ArrayList getUsers() {
        return users;
    }

    private void receiveFile() {
        String response = restClient.getResponse();

        Log.d(TAG, "receiveFile: " + response);

        try {
//            JSONObject responseJson = new JSONObject(response);
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                String deviceUuid, ip, mac;
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                deviceUuid = jsonObject.getString("deviceUuid");
                JSONObject commandResponses = jsonObject.getJSONObject("commandResponses");

                Iterator<String> iterator;
                ArrayList<String> keys;

                successString = commandResponses.getString("SUCCESS");
                jsonObject = new JSONObject(successString);
                if (0 < jsonObject.length()) {
                    iterator = jsonObject.keys();
                    keys = new ArrayList<>();
                    while (iterator.hasNext()) {
                        keys.add(iterator.next());
                    }
                    for (String key : keys) {
                        successString = jsonObject.getString(key);
                    }
                    activity.showCliRunnerResult(successString);
                }

                failureString = commandResponses.getString("FAILURE");
                jsonObject = new JSONObject(failureString);
                if (0 < jsonObject.length()) {
                    iterator = jsonObject.keys();
                    keys = new ArrayList<>();
                    while (iterator.hasNext()) {
                        keys.add(iterator.next());
                    }
                    for (String key : keys) {
                        failureString = jsonObject.getString(key);
                    }
                    activity.showCliRunnerResult(failureString);
                }

                blacklistedString = commandResponses.getString("BLACKLISTED");
                jsonObject = new JSONObject(blacklistedString);
                if (0 < jsonObject.length()) {
                    iterator = jsonObject.keys();
                    keys = new ArrayList<>();
                    while (iterator.hasNext()) {
                        keys.add(iterator.next());
                    }
                    for (String key : keys) {
                        blacklistedString = jsonObject.getString(key);
                    }
                    activity.showCliRunnerResult(blacklistedString);
                }
                Log.d(TAG, "successString: " + successString);
                Log.d(TAG, "failureString: " + failureString);
                Log.d(TAG, "blacklistedString: " + blacklistedString);
            }
        } catch (JSONException e) {
            Log.d(TAG, "receiveFile caught exception " + e.toString());
            // TODO Auto-generated catch block
//            e.printStackTrace();
        }
/*
        for (int h = 0; h < hosts.size(); h++) {
            Log.d(TAG, "Host " + h + ": " + ((Host)hosts.get(h)).getId());
        }
*/
//        activity.showHosts(hosts);

        // reset request
        request = 0;
    }

    private void receiveFlowAnalysis() {
        //TODO: implement this
    }

    private void receiveHosts() {
        String response = restClient.getResponse();

        hosts = new ArrayList();
        try {
            JSONObject responseJson = new JSONObject(response);
            JSONArray jsonArray = responseJson.getJSONArray("response");
            for (int h = 0; h < jsonArray.length(); h++) {
                String id, ip, mac;
                JSONObject userObject = (JSONObject) jsonArray.get(h);
                id = userObject.getString("id");
                ip = userObject.getString("hostIp");
                mac = userObject.getString("hostMac");
                Host host = new Host(id, ip, mac);
                hosts.add(host);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        for (int h = 0; h < hosts.size(); h++) {
            Log.d(TAG, "Host " + h + ": " + ((Host) hosts.get(h)).getId());
        }

        activity.showHosts(hosts);

        // reset request
        request = 0;
    }

    private void receiveLegitReads() {
        String response = restClient.getResponse();

        Log.d(TAG, "receiveLegitReads: " + response);

        legitReads = new ArrayList();
        try {
            JSONObject responseJson = new JSONObject(response);
            JSONArray jsonArray = responseJson.getJSONArray("response");
            for (int i = 0; i < jsonArray.length(); i++) {
                legitReads.add(jsonArray.getString(i));
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        for (int i = 0; i < legitReads.size(); i++) {
            Log.d(TAG, "Command" + i + ": " + legitReads.get(i) + "\n");
        }

        activity.showLegitReads(legitReads);

        // reset request
        request = 0;
    }

    private void receiveNetworkDevice() {
        String response = restClient.getResponse();
        Log.d(TAG, "receiveNetworkDevice: " + response);

        try {
            JSONObject responseJson = new JSONObject(response);
            JSONArray responseArray = responseJson.getJSONArray("response");
            NetworkDevice networkDevice = null;
            for (int i = 0; i < responseArray.length(); i++) {
                JSONObject deviceObject = (JSONObject) responseArray.get(i);
                String id = deviceObject.getString("id");
                String serialNumber = deviceObject.getString("serialNumber");
                String hostname = deviceObject.getString("hostname");
                String family = deviceObject.getString("family");
                String type = deviceObject.getString("type");

                networkDevice = new NetworkDevice(id, serialNumber, hostname, family, type);
            }
            activity.setNetworkDevice(networkDevice);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // reset request
        request = 0;
    }

    private void receiveNetworkDevices() {
        String response = restClient.getResponse();
        networkDevices = new ArrayList();

        try {
            JSONObject responseJson = new JSONObject(response);
            JSONArray jsonArray = responseJson.getJSONArray("response");
            for (int d = 0; d < jsonArray.length(); d++) {
                JSONObject deviceObject = (JSONObject) jsonArray.get(d);
                String id = deviceObject.getString("id");
                String serialNumber = deviceObject.getString("serialNumber");
                String hostname = deviceObject.getString("hostname");
                String family = deviceObject.getString("family");
                String type = deviceObject.getString("type");

                NetworkDevice networkDevice = new NetworkDevice(id, serialNumber, hostname, family, type);
                networkDevices.add(networkDevice);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        for (int d = 0; d < networkDevices.size(); d++) {
            Log.d(TAG, "Device " + d + ": " + ((NetworkDevice) networkDevices.get(d)).getHostname());
        }

//        activity.showNetworkDevices(networkDevices);
        activity.setNetworkDevices(networkDevices);

        // reset request
        request = 0;
    }

    private void receiveReadRequest() {
/*
        request = REQUEST_TASK_COMPLETION;
        String response = RestClient.getResponse();

        try {
            JSONObject responseJson = new JSONObject(response);
            responseJson = responseJson.getJSONObject("response");
            String taskId = responseJson.getString("taskId");

            String[] strings = new String[5];

            strings[0] = getBaseUrlString() + URL_SUFFIX_TASK;
            strings[1] = RestClient.WAIT_FOR_TASK_COMPLETION;
            strings[2] = REQUEST_TASK_COMPLETION + "";
            strings[4] = taskId;

            restClient = new RestClient(this, ticketUrl, taskUrl, fileUrl, username, password);
            restClient.execute(strings);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
*/
    }

    private void receiveTask() {
/*
        String response = RestClient.getResponse();
        Log.d(TAG, "Task completed, response: " + response);

        try {
            JSONObject responseJson = new JSONObject(response);
            responseJson = responseJson.getJSONObject("response");
            String progress = responseJson.getString("progress");

            responseJson = new JSONObject(progress);
            String fileId = responseJson.getString("fileId");

            Log.d(TAG, "fileId: " + fileId);

            request = REQUEST_FILE;

            String[] strings = new String[5];

            strings[0] = getBaseUrlString() + URL_SUFFIX_FILE;
            strings[1] = RestClient.REQ_AUTHENTICATED_GET;
            strings[2] = REQUEST_FILE + "";
//        strings[3] = authTicket;
            strings[4] = fileId;

            restClient = new RestClient(this, ticketUrl, taskUrl, fileUrl, username, password);
            restClient.execute(strings);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
*/
    }

    private void receiveTicket() {
        String response = restClient.getResponse();

        try {
            if (null != response) {
                JSONObject responseJson = new JSONObject(response);
                responseJson = responseJson.getJSONObject("response");

                String authenticationTicket = responseJson.getString("serviceTicket");

                Log.d(TAG, "Ticket received: " + authenticationTicket);
                Log.d(TAG, "request: " + request);
                activity.setSettingsValidity(true);
            } else {
                Log.d(TAG, "No authentication ticket received!");
                activity.setSettingsValidity(false);
            }


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void receiveUsers() {
        String response = restClient.getResponse();

        users = new ArrayList();
        try {
            JSONObject responseJson = new JSONObject(response);
            JSONArray jsonArray = responseJson.getJSONArray("response");
            for (int u = 0; u < jsonArray.length(); u++) {
                String username;
                JSONObject userObject = (JSONObject) jsonArray.get(u);
                username = userObject.getString("username");
                User user = new User(username);
                users.add(user);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        for (int u = 0; u < users.size(); u++) {
            Log.d(TAG, "User " + u + ": " + ((User) users.get(u)).getUsername());
        }

        activity.showUsers(users);

        // reset request
        request = 0;
    }

    /**
     * Private method for ApicEm to make a REST call for the hosts.
     * @param authTicket valid authentication ticket, obtained through
    private void requestFlowAnalysis(String authTicket) {
    String[] strings = new String[5];

    strings[0] = getBaseUrlString() + URL_SUFFIX_HOST;
    strings[1] = RestClient.REQ_AUTHENTICATED_POST;
    strings[2] = REQUEST_FLOW_ANALYSIS + "";
    strings[3] = authTicket;
    strings[4] = "{\"sourceIP\":" + sourceIp + "\"destIP\":\"" + destIp + "\"}";

    restClient = new RestClient(this, ticketUrl, taskUrl, username, password);
    restClient.execute(strings);
    }
     */

    /**
     * Public method for View to request the flow analysis configured in ApicEm.
     */
    public void requestFlowAnalysis(String sourceIp, String destIp) {
        request = RestClient.REQUEST_GET_FLOW_ANALYSIS;

        String[] strings = new String[2];
        strings[0] = sourceIp;
        strings[1] = destIp;

        restClient = new RestClient(this, getBaseUrlString(), username, password);
        restClient.setRequestCode(request);
        restClient.execute(strings);
    }

    /**
     * Private method for ApicEm to make a REST call for the hosts.
     * @param authTicket valid authentication ticket, obtained through
    private void requestHosts(String authTicket) {
    String[] strings = new String[5];

    strings[0] = getBaseUrlString() + URL_SUFFIX_HOST;
    strings[1] = RestClient.REQ_AUTHENTICATED_GET;
    strings[2] = REQUEST_HOSTS + "";
    strings[3] = authTicket;

    restClient = new RestClient(this, ticketUrl, taskUrl, username, password);
    restClient.execute(strings);
    }
     */

    /**
     * Public method for View to request the hosts configured in ApicEm.
     */
    public void requestHosts() {
        String[] strings = new String[0];
        request = RestClient.REQUEST_GET_HOSTS;
        restClient = new RestClient(this, getBaseUrlString(), username, password);
        restClient.setRequestCode(request);
        restClient.execute(strings);
    }

    /**
     * Public method for View to request the legit reads in ApicEm.
     */
    public void requestLegitReads() {
/*
        request = REQUEST_LEGIT_READS;

        String[] strings = new String[5];

        strings[0] = getBaseUrlString() + URL_SUFFIC_LEGIT_READS;
        strings[1] = RestClient.REQ_AUTHENTICATED_GET;
        strings[2] = REQUEST_LEGIT_READS + "";
//        strings[3] = authTicket;

        restClient = new RestClient(this, ticketUrl, taskUrl, fileUrl, username, password);
        restClient.execute(strings);
*/
    }

    /**
     * Private method for ApicEm to make a REST call for the legit reads.
     */

    public void requestNetworkDevice(String id) {
        request = RestClient.REQUEST_GET_NETWORK_DEVICE;

        String[] strings = new String[1];
        strings[0] = id;

        restClient = new RestClient(this, getBaseUrlString(), username, password);
        restClient.setRequestCode(request);
        restClient.execute(strings);
    }

    /**
     * Public method for View to request the network devices configured in ApicEm.
     */
    public void requestNetworkDevices() {
        String[] strings = new String[0];
        request = RestClient.REQUEST_GET_NETWORK_DEVICES;
        restClient = new RestClient(this, getBaseUrlString(), username, password);
        restClient.setRequestCode(request);
        restClient.execute(strings);
    }

    /**
     * Private method for ApicEm to make a REST call for the network devices.
     */

    public void requestCliRunner(String deviceId, String cli) {
        String[] strings = new String[2];
        strings[0] = deviceId;
        strings[1] = cli;

        request = RestClient.REQUEST_CLI_RUNNER;
        restClient = new RestClient(this, getBaseUrlString(), username, password);
        restClient.setRequestCode(request);
        restClient.execute(strings);
    }

    /**
     * Public method for View to request the users configured in ApicEm.
     */
    public void requestUsers() {
        request = RestClient.REQUEST_GET_USERS;
        restClient = new RestClient(this, getBaseUrlString(), username, password);
        restClient.setRequestCode(request);
        restClient.execute();
    }

    /**
     * Called when a RestClient has completed a REST call.
     *
     * @param result Result code. <0 if REST CALL failed, one of the REST_CODEs otherwise.
     */
    public void taskCompletionResult(int result) {
        Log.d(TAG, "request: " + request + " taskCompletionResult: " + result);
        if (RestClient.RESULT_BAD == result) {
            Log.d(TAG, "taskCompletionResult::RESULT_BAD: " + restClient.getResponse());
            activity.setSettingsValidity(false);
        } else {
            switch (request) {
                case REQUEST_TICKET:
                    Log.d(TAG, "Ticket received for request " + request);
                    receiveTicket();
                    break;
                case REQUEST_NETWORK_DEVICES:
                    Log.d(TAG, "Network devices received");
                    receiveNetworkDevices();
                    break;
                case REQUEST_HOSTS:
                    Log.d(TAG, "Hosts received");
                    receiveHosts();
                    break;
                case REQUEST_FLOW_ANALYSIS:
                    Log.d(TAG, "Flow analysis received");
                    receiveFlowAnalysis();
                    break;
                case REQUEST_LEGIT_READS:
                    Log.d(TAG, "Legit reads received");
                    receiveLegitReads();
                    break;
                case REQUEST_READ:
                    Log.d(TAG, "Read request received");
                    receiveReadRequest();
                    break;
                case REQUEST_TASK_COMPLETION:
                    Log.d(TAG, "Task completed");
                    receiveTask();
                    break;
                case REQUEST_FILE:
                    Log.d(TAG, "File request completed");
                    receiveFile();
                    break;
                case REQUEST_NETWORK_DEVICE:
                    Log.d(TAG, "Network device request completed");
                    receiveNetworkDevice();
                    break;
                case RestClient.REQUEST_TEST_SETTINGS:
                    Log.d(TAG, "taskCompletionResult::REQUEST_TEST_SETTINGS: " + result);
                    activity.setSettingsValidity(RestClient.RESULT_OK == result);
                    break;
                case RestClient.REQUEST_CLI_RUNNER:
                    Log.d(TAG, "taskCompletionResult::REQUEST_CLI_RUNNER: " + result);
                    receiveFile();
                    break;
                case RestClient.REQUEST_GET_HOSTS:
                    Log.d(TAG, "taskCompletionResult::REQUEST_GET_HOSTS: " + result);
                    receiveHosts();
                    break;
                case RestClient.REQUEST_GET_NETWORK_DEVICE:
                    Log.d(TAG, "taskCompletionResult::REQUEST_GET_NETWORK_DEVICE: " + result);
                    receiveNetworkDevice();
                    break;
                case RestClient.REQUEST_GET_NETWORK_DEVICES:
                    Log.d(TAG, "taskCompletionResult::REQUEST_GET_NETWORK_DEVICES: " + result);
                    receiveNetworkDevices();
                    break;
                case RestClient.REQUEST_GET_USERS:
                    Log.d(TAG, "taskCompletionResult::REQUEST_GET_USERS: " + result);
                    receiveUsers();
                    break;
            }
        }
    }

    public void taskCompletedResult(int result) {
        Log.d(TAG, "taskCompletedResult: " + result);
    }

    public void testSettings() {
        Log.d(TAG, "testSettings");
        request = RestClient.REQUEST_TEST_SETTINGS;
        restClient = new RestClient(this, getBaseUrlString(), username, password);
        restClient.setRequestCode(request);
        restClient.execute();
    }

}
