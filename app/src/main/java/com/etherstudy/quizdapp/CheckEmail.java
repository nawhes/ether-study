package com.etherstudy.quizdapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class CheckEmail extends AppCompatActivity {

    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_email);

        Log.d("chpark", "in CheckEmail");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        AsyncTask.execute(new Runnable() { // 사용자 계정 등록 여부 검사
            @Override
            public void run() {
                try {
                    URL url = new URL("http://101.101.161.251:8001/user/email/" + currentUser.getEmail());
                    Log.d("chpark1", currentUser.getEmail()+"");
                    HttpURLConnection conn =
                            (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("User-Agent", "QuizShow");

                    if(conn.getResponseCode() == 200 || conn.getResponseCode() == 201) {
                        InputStream responseBody = conn.getInputStream();
                        InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
                        JsonReader jsonReader = new JsonReader(responseBodyReader);
                        jsonReader.beginObject(); // 없으면 IOException 발생
                        while(jsonReader.hasNext()) {
                            String key = jsonReader.nextName();
                            if(key.equals("email")) {
                                email = jsonReader.nextString();
                                break;
                            }
                            else jsonReader.skipValue();
                        }
                        jsonReader.close();

                        if(email != null) { // email이 있으면
                            startActivity(new Intent(getApplicationContext(), CheckPubKey.class)); // 지갑 여부 검사
                        }
                    }
                    else {
                        Log.d("chpark", conn.getResponseCode() + "");
                    }
                    conn.disconnect();
                }
                catch (MalformedURLException e) {
                    System.err.println("URL 프로토콜의 형식이 잘못됨. ex) http://");
                    e.printStackTrace();
                }
                catch (IOException e) { // 결과 존재하지 않음
                    e.printStackTrace();
                    startActivity(new Intent(getApplicationContext(),RegisterUser.class)); // 사용자 등록
                    finish();
                }
                finally {
                    finish();
                }
            }
        });
    }
}