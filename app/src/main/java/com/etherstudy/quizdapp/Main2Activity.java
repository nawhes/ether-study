package com.etherstudy.quizdapp;

import android.icu.util.Output;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.etherstudy.quizdapp.fragment.ChatFragment;
import com.etherstudy.quizdapp.fragment.MainFragment;
import com.etherstudy.quizdapp.fragment.CreateWalletFragment;
import com.etherstudy.quizdapp.fragment.WalletFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ChatFragment.OnFragmentInteractionListener ,
        MainFragment.OnFragmentInteractionListener,
        CreateWalletFragment.OnFragmentInteractionListener {

    private FirebaseAuth mAuth;
    private String pubKey, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        AsyncTask.execute(new Runnable() { // 사용자 계정 등록 여부 검사
            @Override
            public void run() {
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
                            if(key.equals("email")) {
                                email = jsonReader.nextString();
                                break;
                            }
                            else {
                                jsonReader.skipValue();
                            }
                        }
                        jsonReader.close();
                        if(email == null) {
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("email", currentUser.getEmail());
                                jsonObject.put("uid", currentUser.getUid());
                            }
                            catch (JSONException e) {

                            }
                            AsyncTask.execute(new Runnable() { // 사용자 계정 등록
                                @Override
                                public void run() {
                                    try {
                                        URL url = new URL("http://101.101.161.251:8001/user");
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
                catch (IOException e) {
                    System.err.println("URL Connection Failed");
                    e.printStackTrace();
                }
            }
        });

        AsyncTask.execute(new Runnable() { // 사용자 계정에 공개키가 등록되었는지 검사
            @Override
            public void run() {
                try {
                    URL url = new URL("http://101.101.161.251:8001/user/pubKey/" + email);
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
                                pubKey = jsonReader.nextString();
                                break;
                            }
                            else {
                                jsonReader.skipValue();
                            }
                        }
                        jsonReader.close();
                        if(pubKey != null) {
                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                            MainFragment fragment = new MainFragment();
                            transaction.replace(R.id.content_main_framelayout, fragment);
                            transaction.commit();
                        }
                        else {
                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                            CreateWalletFragment fragment = new CreateWalletFragment();
                            transaction.replace(R.id.content_main_framelayout, fragment);
                            transaction.commit();
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
                catch (IOException e) {
                    System.err.println("URL Connection Failed");
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_chat) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            ChatFragment fragment = new ChatFragment();
            transaction.replace(R.id.content_main_framelayout, fragment);
            transaction.commit();

        } else if (id == R.id.nav_wallet) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            WalletFragment fragment = new WalletFragment();
            Bundle bundle = new Bundle(1);
            bundle.putString("pubKey", pubKey);
            fragment.setArguments(bundle);
            transaction.replace(R.id.content_main_framelayout, fragment);
            transaction.commit();

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


}
