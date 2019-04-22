package com.etherstudy.quizdapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class RegisterUser extends AppCompatActivity {
    private InputStream is = null;
    String result = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        Log.d("chpark", "in RegisterUser");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", currentUser.getEmail());
            jsonObject.put("uid", currentUser.getUid());
        }
        catch (JSONException e) {

        }
        Log.d("chpark1", jsonObject + "");

        AsyncTask.execute(new Runnable() { // 사용자 계정 등록
            @Override
            public void run() {
                try {
                    URL url = new URL("http://101.101.161.251:8001/user");
                    HttpURLConnection conn =
                            (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
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

                    Log.i("registerUserStat", String.valueOf(conn.getResponseCode()));
                    Log.i("registerUserMsg", conn.getResponseMessage());

                    conn.disconnect();
                    startActivity(new Intent(getApplicationContext(), RegisterPubKey.class));
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
    }
}
