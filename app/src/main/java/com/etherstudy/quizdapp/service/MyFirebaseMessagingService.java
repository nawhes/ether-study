package com.etherstudy.quizdapp.service;

import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import static android.content.ContentValues.TAG;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public MyFirebaseMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String,String> data = remoteMessage.getData();
        Log.d("token", data.get("contents"));
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        // Get updated InstanceID token.
        Log.d(TAG, "Refreshed token: " + s);
        sendRegistrationToServer(s);
    }

    public void sendRegistrationToServer(String token) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        JSONObject jsonObject = new JSONObject();
        try {
            if (currentUser != null) {
                jsonObject.put("email", currentUser.getEmail());
                jsonObject.put("deviceToken", token);

                AsyncTask.execute(new Runnable() { // 사용자 계정 등록
                    @Override
                    public void run() {
                        try {
                            URL url = new URL("http://101.101.161.251:8001/user/");
                            HttpsURLConnection conn =
                                    (HttpsURLConnection) url.openConnection();
                            conn.setRequestMethod("POST");
                            conn.setRequestProperty("User-Agent", "QuizShow");
                            conn.setRequestProperty("Content-Type", "application/json");
                            conn.setRequestProperty("Accept-Charset", "UTF-8");
                            conn.setConnectTimeout(10000);
                            conn.setReadTimeout(10000);

                            OutputStream os = conn.getOutputStream();
                            os.write(jsonObject.toString().getBytes("UTF-8"));
                            os.flush();

                            conn.disconnect();
                        }
                        catch (MalformedURLException e) {
                            System.err.println("URL 프로토콜의 형식이 잘못됨. ex) http://");
                            e.printStackTrace();
                        }
                        catch (IOException e) {
                            System.err.println("URL Connection Failed");
                            e.printStackTrace();
                        }
                    }
                });
            }
            else {
                Log.d("token", "curruntUser is null");
            }
        }
        catch (JSONException e) {

        }

    }
}