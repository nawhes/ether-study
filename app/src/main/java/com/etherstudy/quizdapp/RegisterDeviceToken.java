package com.etherstudy.quizdapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RegisterDeviceToken extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_device_token);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        Log.d("chpark", "in RegisterToken");
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
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("email", currentUser.getEmail());
                            jsonObject.put("deviceToken", token);
                        }
                        catch (JSONException e) {

                        }
                        Log.d("chpark1", jsonObject + "");

                        AsyncTask.execute(new Runnable() { // 사용자 계정 등록
                            @Override
                            public void run() {
                                try {
                                    URL url = new URL(QuizConstants.SERVER_IP + "/user/deviceToken");
                                    HttpURLConnection conn =
                                            (HttpURLConnection) url.openConnection();
                                    conn.setRequestMethod("PATCH");
                                    conn.setRequestProperty("Content-Type", "application/json");
                                    conn.setRequestProperty("Accept","application/json");
                                    conn.setRequestProperty("Accept-Charset", "UTF-8");
                                    conn.setConnectTimeout(10000);
                                    conn.setReadTimeout(10000);
                                    conn.setDoOutput(true);

                                    OutputStream os = conn.getOutputStream();
                                    os.write(jsonObject.toString().getBytes("UTF-8"));
                                    os.flush();
                                    os.close();

                                    Log.i("registerDTStat", String.valueOf(conn.getResponseCode()));
                                    Log.i("registerDTMsg", conn.getResponseMessage());

                                    conn.disconnect();
                                    startActivity(new Intent(getApplicationContext(), Main2Activity.class));
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
                                    finish();
                                }
                            }
                        });

                        // Log and toast
                        Log.d("chpark debug", token);
                    }
                });
    }
}
