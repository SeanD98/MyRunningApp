package com.example.myrunningapp.Network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.provider.ContactsContract;
import android.util.Base64;
import android.util.Log;

import com.example.myrunningapp.Utils.Constants;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import android.app.Activity;

public class MyHTTPClient {

    public static String Username = "";
    public static String Password = "";
    private static DefaultHttpClient client = MyHTTPClient.getDefaultHttpClient();
    private static final String TAG = MyHTTPClient.class.getSimpleName();
    private static final int TIME_OUT_CONNECTION = 30000;
    private static final int TIME_OUT_SOCKET = 30000;

    public static void setCredentials(String username, String password) {
        MyHTTPClient.client = getDefaultHttpClient();
        MyHTTPClient.client.getCredentialsProvider().setCredentials(
                new AuthScope(null, -1),
                new UsernamePasswordCredentials(username, password));
    }

    public static DefaultHttpClient getDefaultHttpClient() {
        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params,
                MyHTTPClient.TIME_OUT_CONNECTION);
        HttpConnectionParams.setSoTimeout(params, MyHTTPClient.TIME_OUT_SOCKET);
        HttpConnectionParams.setSocketBufferSize(params, 8192);
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        KeyStore trustStore;
        SSLSocketFactory sf = null;
        try {
            trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            sf = new MySSLSocketFactory(trustStore);
        } catch (KeyStoreException e) {
            Log.e(TAG, e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, e.getMessage());
        } catch (CertificateException e) {
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        } catch (KeyManagementException e) {
            Log.e(TAG, e.getMessage());
        } catch (UnrecoverableKeyException e) {
            Log.e(TAG, e.getMessage());
        }
        sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        schemeRegistry.register(new Scheme("http", PlainSocketFactory
                .getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", sf, 443));
        schemeRegistry.register(new Scheme("rtsp", PlainSocketFactory
                .getSocketFactory(), 554));
        ThreadSafeClientConnManager conn = new ThreadSafeClientConnManager(
                params, schemeRegistry);
        return new DefaultHttpClient(conn, params);
    }

    private static String convertStreamToString(InputStream is)
            throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line + "\n");
        }
        is.close();
        reader.close();
        return sb.toString();
    }

    public static String buildUrl(String uri) {
        StringBuilder builder = new StringBuilder();
        builder.append("http://167.99.91.53");
        builder.append(uri);
        return builder.toString();
    }

    public interface APILoginCallback {
        void onLoginResponse(boolean success, String response);
    }

    public static void Login(String username, String password, final APILoginCallback callback) throws Exception {
        MyHTTPClient.setCredentials(Username, Password);

        HttpPost post = new HttpPost(buildUrl(Constants.API_LOGIN));

        JsonObject object = new JsonObject();
        object.addProperty("username", username);
        object.addProperty("password", password);

        String jsonString = object.toString();

        post.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");

        List<NameValuePair> data = new ArrayList<NameValuePair>();
        data.add(new BasicNameValuePair("username", username));
        data.add(new BasicNameValuePair("password", password));
        post.setEntity(new UrlEncodedFormEntity(data));

        HttpResponse response = client.execute(post);
        System.out.println("Response code: " + response.getStatusLine().getStatusCode());

        int status;
        status = response.getStatusLine().getStatusCode();
        HttpEntity entity = response.getEntity();

        StringBuffer result = null;

        if (status == HttpStatus.SC_UNAUTHORIZED) {
            if (status == HttpStatus.SC_OK) {
                if (entity != null) {
                    BufferedReader rd = new BufferedReader(
                            new InputStreamReader(response.getEntity().getContent()));

                    result = new StringBuffer();
                    String line = "";
                    while ((line = rd.readLine()) != null) {
                        result.append(line);
                    }
                }
            }
        } else {
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
        }
        callback.onLoginResponse(true, result.toString());
    }

    public interface APISignUpCallback {
        void onSignUpResponse(boolean success, String response);
    }

    public static void SignUp(String username, String password, final APISignUpCallback callback) throws Exception {
        MyHTTPClient.setCredentials(Username, Password);

        HttpPost post = new HttpPost(buildUrl(Constants.API_CREATE_USER));

        JsonObject object = new JsonObject();
        object.addProperty("username", username);
        object.addProperty("password", password);

        String jsonString = object.toString();

        post.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");

        List<NameValuePair> data = new ArrayList<NameValuePair>();
        data.add(new BasicNameValuePair("username", username));
        data.add(new BasicNameValuePair("password", password));
        post.setEntity(new UrlEncodedFormEntity(data));


        HttpResponse response = client.execute(post);
        System.out.println("Response code: " + response.getStatusLine().getStatusCode());

        int status;
        status = response.getStatusLine().getStatusCode();
        HttpEntity entity = response.getEntity();

        StringBuffer result = null;

        if (status == HttpStatus.SC_UNAUTHORIZED) {
            if (status == HttpStatus.SC_OK) {
                if (entity != null) {
                    BufferedReader rd = new BufferedReader(
                            new InputStreamReader(response.getEntity().getContent()));

                    result = new StringBuffer();
                    String line = "";
                    while ((line = rd.readLine()) != null) {
                        result.append(line);
                    }
                }
            }
        } else {
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
        }
        callback.onSignUpResponse(true, result.toString());
    }
}
