package com.tomohamat.apicem.Model;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static java.lang.Thread.sleep;

/**
 * Created by Robert Tomohamat (rtomoham@cisco.com) on 3/11/2017.
 * The RestClient performs RESTcalls in a separate thread.
 */
public class RestClient extends AsyncTask<String, Void, String> {

    // request codes
    public static final int REQUEST_NONE = 0;
    public static final int REQUEST_TEST_SETTINGS = 1000;
    public static final int REQUEST_APIC_EM_VERSION = 1001;
    public static final int REQUEST_CLI_RUNNER = 1002;
    public static final int REQUEST_GET_CLI_RUNNER_COMMANDS = 1003;
    public static final int REQUEST_GET_FLOW_ANALYSIS = 1004;
    public static final int REQUEST_GET_HOSTS = 1005;
    public static final int REQUEST_GET_NETWORK_DEVICE = 1006;
    public static final int REQUEST_GET_NETWORK_DEVICES = 1007;
    public static final int REQUEST_GET_USERS = 1008;
    public static final int REQUEST_GET_DEVICE_LICENSES = 1009;
    // exceptions & errors
    public static final String ERROR_IN_SETTINGS = "ERROR IN SETTINGS";
    public static final String ERROR_USER_NOT_AUTHORIZED = "USER NOT AUTHORIZED TO MAKE CALL";
    public static final String ERROR_PROCESSING_REQUEST = "SERVER COULD NOT FULFILL REQUEST";
    public static final String EXCEPTION_IO_EXCEPTION = "IO EXCEPTION";
    public static final String EXCEPTION_UNKNOWN_HOST = "UNKNOWN HOST EXCEPTION";
    // request codes
    public static final int REQ_POST = 0;
    public static final int REQ_PUT = 1;
    public static final int REQ_GET = 2;
    // result codes
    public static final int RESULT_OK = 1;
    public static final int RESULT_NONE = 0;
    public static final int RESULT_BAD = -1;
    private static final String TAG = "RestClient";
    // REST methods
    private static final String METHOD_POST = "POST";
    private static final String METHOD_PUT = "PUT";
    private static final String METHOD_GET = "GET";
    // URL suffixes for REST calls (to be appended to baseUrl
    private static final String URL_SUFFIX_TICKET = "/ticket";
    private static final String URL_SUFFIX_TASK = "/task";
    private static final String URL_SUFFIX_USER = "/user";
    private static final String URL_SUFFIX_HOST = "/host";
    private static final String URL_SUFFIX_FILE = "/file";
    private static final String URL_SUFFIC_FLOW_ANALYSIS = "/flow-analysis";
    private static final String URL_SUFFIX_CLI_RUNNER = "/network-device-poller/cli/read-request";
    private static final String URL_SUFFIC_CLI_RUNNER_COMMANDS = "/network-device-poller/cli/legit-reads";
    private static final String URL_SUFFIX_NETWORK_DEVICE = "/network-device";
    private static final String URL_SUFFIX_DEVICE_LICENSE = "/license-info/network-device";
    protected static int responseCode;
    protected static String response;
    protected int result;
    protected String jsonData, urlString, requestMethod;
    protected String username, password;
    protected ApicEm apicEm;
    /**
     * Base url to make REST calls, including http:// or https:// and the port number, e.g. :443
     */
    private String baseUrl;
    /**
     * Identifier for the current request. Must be set prior to calling .execute(String[])
     */
    private int requestCode;
    private String authenticationString;

    public RestClient(ApicEm apicEm, String baseUrl, String username, String password) {
        this.apicEm = apicEm;
        this.baseUrl = baseUrl;
        this.username = username;
        this.password = password;

        authenticationString = "{\"username\" : \"" + username + "\", \"password\" : \"" + password + "\"}";
    }

    private void getApicEmVersion() {
        String urlString = "https://sandboxapic.cisco.com/system_info";
        makeRestCall(REQ_GET, urlString, getAuthenticationTicket(), null);
        if (HttpURLConnection.HTTP_OK == responseCode) {
            Log.d(TAG, "getApicEmVErsion::response == " + response);
        }
    }

    private String getAuthenticationTicket() {
        String ticket = null;
        String ticketUrl = baseUrl + URL_SUFFIX_TICKET;

        makeRestCall(REQ_POST, ticketUrl, null, authenticationString);

        if (HttpURLConnection.HTTP_OK == responseCode) {
            ticket = getAuthenticationTicketFromResponse(response);
        } else {
            Log.d(TAG, "getAuthenticationTicket::responseCode == " + responseCode);
        }

        return ticket;
    }

    private String getAuthenticationTicketFromResponse(String response) {
        String authenticationTicket = null;
        try {
            JSONObject responseJson = new JSONObject(response);
            responseJson = responseJson.getJSONObject("response");
            authenticationTicket = responseJson.getString("serviceTicket");
//            Log.d(TAG, "Ticket received: " + authenticationTicket);
        } catch (JSONException e) {
            Log.d(TAG, "getAuthenticationTicketFromResponse caught exception " + e.toString());
        }
        return authenticationTicket;
    }

    private void getCliRunnerCommands() {
        String urlString = baseUrl + URL_SUFFIC_CLI_RUNNER_COMMANDS;
        makeRestCall(REQ_GET, urlString, getAuthenticationTicket(), null);
        if (HttpURLConnection.HTTP_OK == responseCode) {
            result = RESULT_OK;
        } else {
            result = RESULT_BAD;
        }
    }

    private void getDeviceLicense(String deviceId) {
        String urlString = baseUrl + URL_SUFFIX_DEVICE_LICENSE + "/" + deviceId;
        makeRestCall(REQ_GET, urlString, getAuthenticationTicket(), null);
        if (HttpURLConnection.HTTP_OK == responseCode) {
            result = RESULT_OK;
        } else {
            result = RESULT_BAD;
        }
    }

    private void getDeviceLicenses() {

    }

    private void getFile(String fileId) {
        String fileUrl = baseUrl + URL_SUFFIX_FILE + "/" + fileId;
        Log.d(TAG, "Trying url: " + fileUrl);

        makeRestCall(REQ_GET, fileUrl, getAuthenticationTicket(), null);

        System.out.println("getFile::AUTHENTICATED_GET Response Code :: " + responseCode);

        if (HttpURLConnection.HTTP_OK == responseCode) { // success
            result = RESULT_OK;
        } else {
            Log.d(TAG, "getFile::REST request failed. ResponseCode == " + responseCode);
            result = RESULT_BAD;
        }
    }

    private String getFileId() {
        try {
            JSONObject responseJson = new JSONObject(response);
            responseJson = responseJson.getJSONObject("response");
            String progress = responseJson.getString("progress");
            responseJson = new JSONObject(progress);
            return responseJson.getString("fileId");
        } catch (JSONException e) {
            Log.d(TAG, "getFileId caught exception " + e.toString());
            return null;
        }
    }

    private void getFlowAnalysis(String sourceIp, String destinationIp) {
        String urlString = baseUrl + URL_SUFFIC_FLOW_ANALYSIS;
        String payload = "{\"sourceIP\":" + sourceIp + "\"destIP\":\"" + destinationIp + "\"}";
        makeRestCall(REQ_POST, urlString, getAuthenticationTicket(), payload);
    }

    public String getResponse() {
        return response;
    }

    public int getResponseCode() {
        return responseCode;
    }

    private String getTaskId() {
        try {
            JSONObject responseJson = new JSONObject(response);
            responseJson = responseJson.getJSONObject("response");
            return responseJson.getString("taskId");
        } catch (JSONException e) {
            Log.d(TAG, "getTaskId caught exception " + e.toString());
            return null;
        }
    }

    private void getHosts() {
        String urlString = baseUrl + URL_SUFFIX_HOST;
        makeRestCall(REQ_GET, urlString, getAuthenticationTicket(), null);
        if (HttpURLConnection.HTTP_OK == responseCode) {
            result = RESULT_OK;
        } else {
            result = RESULT_BAD;
        }
    }

    private void getNetworkDevice(String deviceId) {
        String urlString = baseUrl + URL_SUFFIX_NETWORK_DEVICE + "/" + deviceId;
        makeRestCall(REQ_GET, urlString, getAuthenticationTicket(), null);
        if (HttpURLConnection.HTTP_OK == responseCode) {
            result = RESULT_OK;
        } else {
            result = RESULT_BAD;
        }
    }

    private void getNetworkDevices() {
        String urlString = baseUrl + URL_SUFFIX_NETWORK_DEVICE;
        makeRestCall(REQ_GET, urlString, getAuthenticationTicket(), null);
        if (HttpURLConnection.HTTP_OK == responseCode) {
            result = RESULT_OK;
        } else {
            result = RESULT_BAD;
        }
    }

    private void getUsers() {
        String urlString = baseUrl + URL_SUFFIX_USER;
        makeRestCall(REQ_GET, urlString, getAuthenticationTicket(), null);
        if (HttpURLConnection.HTTP_OK == responseCode) {
            result = RESULT_OK;
        } else {
            result = RESULT_BAD;
        }
    }

    public void reset() {
        response = null;
        responseCode = -1;
        result = RESULT_NONE;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public void setUrlString(String urlString) {
        this.urlString = urlString;
    }

    private void makeRestCall(int requestMethod, String urlString, String authenticationTicket, String payload) {
        HttpsTrustManager.allowAllSSL();
        try {
            URL url = new URL(urlString);
            Log.d(TAG, "makeRestCall::Trying url: " + urlString);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000 /*milliseconds*/);
            connection.setConnectTimeout(15000 /* milliseconds */);

            if (null != authenticationTicket) {
                connection.setRequestProperty("X-Auth-Token", authenticationTicket);
            }

            connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            connection.setRequestProperty("X-Requested-With", "XMLHttpRequest");

            switch (requestMethod) {
                case REQ_GET:
                    connection.setRequestMethod(METHOD_GET);
                    responseCode = connection.getResponseCode();
                    Log.d(TAG, "makeRestCall::GET Response Code :: " + responseCode);
                    break;
                case REQ_POST:
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setRequestMethod(METHOD_POST);
                    connection.connect();
                    if (null != payload) {
                        OutputStream os = new BufferedOutputStream(connection.getOutputStream());
                        os.write(payload.getBytes());
                        os.flush();
                        os.close();
                    }
                    responseCode = connection.getResponseCode();
                    Log.d(TAG, "makeRestCall::POST Response Code :: " + responseCode);
                    break;
            }
            if ((HttpURLConnection.HTTP_OK == responseCode) || (202 == responseCode)) { // success
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
                String inputLine;
                StringBuffer tempResponse = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    tempResponse.append(inputLine);
                }
                in.close();
                response = tempResponse.toString();
                Log.d(TAG, "makeRestCall::REST call succeeded. Response code: " + response.toString());
            } else {
                Log.d(TAG, "makeRestCall::REST call failed. Response code: " + responseCode);
                result = RESULT_BAD;
                switch (responseCode) {
                    case 307:
                        response = ERROR_IN_SETTINGS;
                        break;
                    case 403:
                        response = ERROR_USER_NOT_AUTHORIZED;
                        break;
                    case 500:
                        response = ERROR_PROCESSING_REQUEST;
                        break;
                }
            }
        } catch (java.net.UnknownHostException unknownHostException) {
            Log.d(TAG, "makeRestCall::UnKnownHostException" + unknownHostException.toString());
            response = EXCEPTION_UNKNOWN_HOST;
            result = RESULT_BAD;
        } catch (java.io.IOException ioException) {
            Log.d(TAG, "makeRestCall::IOException" + ioException.toString());
            response = EXCEPTION_IO_EXCEPTION;
            result = RESULT_BAD;
        }
    }

    /**
     * @param strings Three or four element String array containing the following items:
     *                strings[0] == the complete URL, including protocol, port number and REST call
     *                strings[1] == requestMethod in [METHOD_POST, METHOD_PUT, METHOD_GET]
     *                strings[2] == REST_CODE identifying the type of call, e.g., get ticket, get users
     *                strings[3] == REST body (optional)
     */
    @Override
    protected String doInBackground(String... strings) {
        switch (requestCode) {
            case REQUEST_TEST_SETTINGS:
                testSettings();
                break;
            case REQUEST_APIC_EM_VERSION:
                getApicEmVersion();
                break;
            case REQUEST_CLI_RUNNER:
                runCliRunner(strings[0], strings[1]);
                break;
            case REQUEST_GET_CLI_RUNNER_COMMANDS:
                getCliRunnerCommands();
                break;
            case REQUEST_GET_FLOW_ANALYSIS:
                getFlowAnalysis(strings[0], strings[1]);
                break;
            case REQUEST_GET_HOSTS:
                getHosts();
                break;
            case REQUEST_GET_NETWORK_DEVICE:
                getNetworkDevice(strings[0]);
                break;
            case REQUEST_GET_NETWORK_DEVICES:
                getNetworkDevices();
                break;
            case REQUEST_GET_USERS:
                getUsers();
                break;
            case REQUEST_GET_DEVICE_LICENSES:
                getDeviceLicense(strings[0]);
                break;
        }
        return "";
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.d(TAG, "onPostExecute:: result == " + result);
        apicEm.taskCompletionResult(result);
    }

    private void runCliRunner(String deviceId, String cli) {
        String urlString = baseUrl + URL_SUFFIX_CLI_RUNNER;
        String payload;
        if ("".equals(cli)) {
            payload = "{\"commands\":[\"" + "show version" + "\"],\"deviceUuids\":[\"" + deviceId + "\"]}";
        } else {
            payload = "{\"commands\":[\"" + cli + "\"],\"deviceUuids\":[\"" + deviceId + "\"]}";
        }

        makeRestCall(REQ_POST, urlString, getAuthenticationTicket(), payload);
        if (202 == responseCode) {
            String taskId = getTaskId();
            waitForTaskToFinish(taskId);
            String fileId = getFileId();
            getFile(fileId);
        } else {
            result = RESULT_BAD;
        }
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    private boolean taskFinished(String taskId) {
        String taskUrl = baseUrl + URL_SUFFIX_TASK + "/" + taskId;
        Log.d(TAG, "getTaskStatus::Trying taskUrl " + taskUrl);

        makeRestCall(REQ_GET, taskUrl, getAuthenticationTicket(), null);

        if (HttpURLConnection.HTTP_OK == responseCode) { // success
            return taskFinishedFromResponse(response);
        } else {
            Log.d(TAG, "taskFinished::GET request failed. Response code: " + responseCode);
            return true;
        }
    }

    private boolean taskFinishedFromResponse(String response) {
        boolean finished = false;

        try {
            JSONObject responseJson = new JSONObject(response);
            responseJson = responseJson.getJSONObject("response");
            finished = responseJson.has("endTime");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return finished;
    }

    public void testSettings() {
        if (null != getAuthenticationTicket()) {
            result = RESULT_OK;
        } else {
            result = RESULT_BAD;
        }
    }

    private void waitForTaskToFinish(String taskId) {
        boolean finished = false;
        while (!finished) {
            try {
                sleep(5000);
                finished = taskFinished(taskId);
            } catch (Exception e) {
                Log.d(TAG, "waitForTaskToFinish::caught exception " + e.toString());
            }
        }
    }

}