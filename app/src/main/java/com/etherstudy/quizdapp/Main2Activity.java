package com.etherstudy.quizdapp;

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
import com.etherstudy.quizdapp.fragment.WalletFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ChatFragment.OnFragmentInteractionListener ,
        MainFragment.OnFragmentInteractionListener ,
        WalletFragment.OnFragmentInteractionListener {

    private String pubKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String email = currentUser.getEmail();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Log.d("chpark", "in Main2Activity");

        AsyncTask.execute(() -> { // 사용자 계정의 공개키 조회
            try {
                URL url = new URL(QuizConstants.SERVER_IP + "/user/email/" + currentUser.getEmail());
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
                        else jsonReader.skipValue();
                    }
                    jsonReader.close();
                    responseBodyReader.close();
                    responseBody.close();
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
                e.printStackTrace();
            }
        });
        // test
        // FirebaseAuth.getInstance().signOut();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        MainFragment fragment = new MainFragment();
        transaction.replace(R.id.content_main_framelayout, fragment);
        transaction.commit();
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
