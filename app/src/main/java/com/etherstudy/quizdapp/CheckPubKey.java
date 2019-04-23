package com.etherstudy.quizdapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class CheckPubKey extends AppCompatActivity {

    String pubKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_pub_key);

        Log.d("chpark", "in CheckPubKey");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        AsyncTask.execute(() -> { // 사용자 계정에 공개키가 등록되었는지 검사
            try {
                URL url = new URL("http://101.101.161.251:8001/user/email/" + currentUser.getEmail());
                HttpURLConnection conn =
                        (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("User-Agent", "QuizShow");

                if(conn.getResponseCode() == 200 || conn.getResponseCode() == 201) {
                    InputStream responseBody = conn.getInputStream();
                    InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
                    JsonReader jsonReader = new JsonReader(responseBodyReader);
                    jsonReader.beginObject();
                    while(jsonReader.hasNext()) {
                        String key = jsonReader.nextName();
                        if(key.equals("pubKey")) {
                            pubKey = jsonReader.nextString(); // IllegalStateException
                            break;
                        }
                        else jsonReader.skipValue();
                    }
                    jsonReader.close();
                    if(pubKey != null) {
                        startActivity(new Intent(getApplicationContext(), RegisterDeviceToken.class));
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
            catch (IllegalStateException e) {
                e.printStackTrace();
                startActivity(new Intent(getApplicationContext(), RegisterPubKey.class));
                finish();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                finish();
            }
        });
    }
}
