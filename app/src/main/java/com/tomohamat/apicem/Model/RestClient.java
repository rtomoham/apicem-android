package com.tomohamat.apicem.Model;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Robert Tomohamat (rtomoham@cisco.com) on 3/11/2017.
 * The RestClient performs RESTcalls in a separate thread.
 */
public class RestClient extends AsyncTask<String, Void, String> {

    public static final String METHOD_POST = "POST";
    public static final String METHOD_PUT = "PUT";
    public static final String METHOD_GET = "GET";
    public static final String METHOD_AUTHENTICATED_GET = "AUTHENTICATED GET";
    public static final String METHOD_AUTHENTICATED_POST = "AUTHENTICATED POST";
    public static final String WAIT_FOR_TASK_COMPLETION = "WAIT FOR TASK COMPLETION";
    public static final int RESULT_OK = 1;
    public static final int RESULT_BAD = -1;
    private static final String TAG = "RestClient";
    protected static int responseCode;
    protected static String response;
    protected int result;
    protected String jsonData, urlString, requestMethod;
    protected String ticketUrl, taskUrl, fileUrl;
    protected String username, password;
    protected ApicEm apicEm;

    public RestClient(ApicEm apicEm, String ticketUrl, String taskUrl, String fileUrl, String username, String password) {
        this.apicEm = apicEm;
        this.ticketUrl = ticketUrl;
        this.taskUrl = taskUrl;
        this.fileUrl = fileUrl;
        this.username = username;
        this.password = password;
    }

    public static String getResponse() {
        return response;
    }

    public static void setResponse(String response) {
        RestClient.response = response;
    }

    public static int getResponseCode() {
        return responseCode;
    }

    public static void setResponseCode(int responseCode) {
        RestClient.responseCode = responseCode;
    }

    private void getFile(String fileId) {
        try {
            URL url = new URL(fileUrl + "/" + fileId);
            Log.d(TAG, "Trying url: " + fileUrl);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /*milliseconds*/);
            conn.setConnectTimeout(15000 /* milliseconds */);

            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
            conn.setRequestMethod(METHOD_GET);
            conn.setRequestProperty("X-Auth-Token", getAuthenticationTicket());
            responseCode = conn.getResponseCode();
            System.out.println("AUTHENTICATED_GET Response Code :: " + responseCode);

            if ((responseCode == HttpURLConnection.HTTP_OK) || (202 == responseCode)) { // success
//                result = new Integer(strings[2]);
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()));
                String inputLine;
                StringBuffer tempResponse = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    tempResponse.append(inputLine);
                }
                in.close();
                response = tempResponse.toString();
                System.out.println(response.toString());
            } else {
                result = RESULT_BAD;
                System.out.println("REST request failed. Response code: " + responseCode);
//                return RESULT_BAD + "";
            }
        } catch (IOException e) {
            e.printStackTrace();
//            return RESULT_BAD + "";
        }

    }

    private boolean getTaskStatus(String taskId) {
        String[] strings = new String[5];
        try {
            URL url = new URL(taskUrl + "/" + taskId);

            Log.d(TAG, "Trying taskUrl " + taskUrl + "/" + taskId);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /*milliseconds*/);
            conn.setConnectTimeout(15000 /* milliseconds */);

            strings[3] = getAuthenticationTicket();

            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
            conn.setRequestMethod(METHOD_GET);
            conn.setRequestProperty("X-Auth-Token", strings[3]);
            responseCode = conn.getResponseCode();
            System.out.println("AUTHENTICATED_GET Response Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) { // success
//                result = new Integer(strings[2]);
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()));
                String inputLine;
                StringBuffer tempResponse = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    tempResponse.append(inputLine);
                }
                in.close();
                response = tempResponse.toString();
                System.out.println(response.toString());

                return getTaskStatusFromResponse(response);
            } else {
                result = RESULT_BAD;
                System.out.println("GET request failed. Response code: " + responseCode);
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean getTaskStatusFromResponse(String response) {
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

    protected String getAuthenticationTicket() {
        String ticket = null;

        HttpsTrustManager.allowAllSSL();
        try {
//            Log.d(TAG, "ticketUrl: " + ticketUrl);
            URL url = new URL(ticketUrl);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /*milliseconds*/);
            conn.setConnectTimeout(15000 /* milliseconds */);

            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
            conn.setRequestMethod(METHOD_POST);
            conn.connect();
//            if (4 < strings.length) {
            OutputStream os = new BufferedOutputStream(conn.getOutputStream());
            String body = "{\"username\" : \"" + username + "\", \"password\" : \"" + password + "\"}";
            os.write(body.getBytes());
            os.flush();
            os.close();
//            }
            responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                result = responseCode;
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()));
                String inputLine;
                StringBuffer tempResponse = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    tempResponse.append(inputLine);
                }
                in.close();
                response = tempResponse.toString();
                ticket = getAuthenticationTicketFromResponse(response);
                System.out.println(response.toString());
            } else {
                result = RESULT_BAD;
                System.out.println("GET request failed. Response code: " + responseCode);
                return RESULT_BAD + "";
            }
        } catch (IOException e) {
            Log.d(TAG, "Caught IOException " + e.toString());
            return RESULT_BAD + "";
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return authenticationTicket;
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

    /**
     * Makes the actual REST call.
     *
     * @param strings Three or four element String array containing the following items:
     *                strings[0] == the complete URL, including protocol, port number and REST call
     *                strings[1] == requestMethod in [METHOD_POST, METHOD_PUT, METHOD_GET]
     *                strings[2] == REST_CODE identifying the type of call, e.g., get ticket, get users
     *                strings[3] == REST body (optional)
     */
    @Override
    protected String doInBackground(String... strings) {
        HttpsTrustManager.allowAllSSL();
        if (WAIT_FOR_TASK_COMPLETION.equals(strings[1])) {
            boolean finished = false;
            int counter = 0;
            while (!finished) {
                try {
                    Thread.sleep(5000);
                    finished = getTaskStatus(strings[4]);
                    Log.d(TAG, "finished\t" + finished + "\t" + counter++);
                } catch (Exception e) {
                    Log.d(TAG, "Caught exception checking task status" + e.toString());
                    finished = true;
                }
            }
        } else if (ApicEm.REQUEST_FILE == new Integer(strings[2])) {
            getFile(strings[4]);
        } else {
            try {
                URL url = new URL(strings[0]);
                Log.d(TAG, "Trying url: " + strings[0]);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /*milliseconds*/);
                conn.setConnectTimeout(15000 /* milliseconds */);

                strings[3] = getAuthenticationTicket();

                switch (strings[1]) {
                    case METHOD_AUTHENTICATED_GET:
                        conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                        conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
                        conn.setRequestMethod(METHOD_GET);
                        conn.setRequestProperty("X-Auth-Token", strings[3]);
                        responseCode = conn.getResponseCode();
                        System.out.println("AUTHENTICATED_GET Response Code :: " + responseCode);
                        break;
                    case METHOD_AUTHENTICATED_POST:
                        conn.setDoInput(true);
                        conn.setDoOutput(true);
                        conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                        conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
                        conn.setRequestMethod(METHOD_POST);
                        conn.setRequestProperty("X-Auth-Token", strings[3]);
                        conn.connect();
                        if (4 < strings.length) {
                            OutputStream os = new BufferedOutputStream(conn.getOutputStream());
                            os.write(strings[4].getBytes());
                            os.flush();
                            os.close();
                        }
                        responseCode = conn.getResponseCode();
                        break;
                    case METHOD_POST:
                    case METHOD_PUT:
                        conn.setDoInput(true);
                        conn.setDoOutput(true);
                        conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                        conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
                        conn.setRequestMethod(strings[1]);
                        conn.connect();
                        if (4 < strings.length) {
                            OutputStream os = new BufferedOutputStream(conn.getOutputStream());
                            os.write(strings[4].getBytes());
                            os.flush();
                            os.close();
                        }
                        responseCode = conn.getResponseCode();
                        Log.d(TAG, "responseCode: " + responseCode);
                        break;
                    case METHOD_GET:
                        conn.setRequestMethod(strings[1]);
                        responseCode = conn.getResponseCode();
                        System.out.println("GET Response Code :: " + responseCode);
                        break;
                }
                if ((responseCode == HttpURLConnection.HTTP_OK) || (202 == responseCode)) { // success
                    result = new Integer(strings[2]);
                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            conn.getInputStream()));
                    String inputLine;
                    StringBuffer tempResponse = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        tempResponse.append(inputLine);
                    }
                    in.close();
                    response = tempResponse.toString();
                    System.out.println(response.toString());
                } else {
                    result = RESULT_BAD;
                    System.out.println("REST request failed. Response code: " + responseCode);
                    return RESULT_BAD + "";
                }
            } catch (IOException e) {
                e.printStackTrace();
                return RESULT_BAD + "";
            }
        }

        return strings[2];
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.d(TAG, "onPostExecute done: " + result);
        apicEm.taskCompletionResult(result);
    }


}