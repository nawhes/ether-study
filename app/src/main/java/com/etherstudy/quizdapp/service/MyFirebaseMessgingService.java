package com.etherstudy.quizdapp.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.etherstudy.quizdapp.Main2Activity;
import com.etherstudy.quizdapp.MainActivity;
import com.etherstudy.quizdapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class MyFirebaseMessgingService extends FirebaseMessagingService {
    public MyFirebaseMessgingService() {
    }

    @Override
    public void onNewToken(String token) {
        Log.d("chpark", "Refreshed token: " + token);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("chpark warning", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        Log.d("chpark debug", token);
                    }
                });
        sendRegistrationToServer(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("FCMonMessageReceived", "onMessageReceived()");

        String from = remoteMessage.getFrom();
        Map<String, String> data = remoteMessage.getData();
        String contents = data.get("contents");

        sendToActivity(getApplicationContext(), from, contents);
    }

    private void sendToActivity(Context context, String from, String contents) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("from", from);
        intent.putExtra("contents", contents);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

    public void sendRegistrationToServer(String token) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.d("FCMServiceCurUser", currentUser.getEmail()+"");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", currentUser.getEmail());
            jsonObject.put("deviceToken", token);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        AsyncTask.execute(new Runnable() { // 사용자 계정 등록
            @Override
            public void run() {
                try {
                    URL url = new URL("http://101.101.161.251:8001/user/deviceToken");
                    HttpURLConnection conn =
                            (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("User-Agent", "QuizShow");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("Accept-Charset", "UTF-8");
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(10000);

                    OutputStream os = conn.getOutputStream();
                    os.write(jsonObject.toString().getBytes("UTF-8"));
                    os.flush();
                    os.close();

                    Log.i("FCMServiceStat", String.valueOf(conn.getResponseCode()));
                    Log.i("FCMServiceMsg", conn.getResponseMessage());

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
                finally {

                }
            }
        });
    }
}
