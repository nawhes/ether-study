package com.etherstudy.quizdapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class RegisterPubKey extends AppCompatActivity {

    EditText etPassword;
    Button btnCreateWallet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_pub_key);

        Log.d("chpark", "in RegisterPubkey");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        etPassword = findViewById(R.id.etPassword);
        btnCreateWallet = findViewById(R.id.btnCreateWallet);
        btnCreateWallet.setOnClickListener((view -> {
            final String password = etPassword.getText().toString();
            String[] result = createWallet(password + currentUser.getUid());
            if (result != null) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("email", currentUser.getEmail());
                    jsonObject.put("pubKey", result[1]);
                    Log.d("registerPubKey", result[1]);
                } catch (JSONException e) {

                }
                Log.d("registerPubKeyJSON", jsonObject + "");

                AsyncTask.execute(new Runnable() { // 사용자 계정 등록
                    @Override
                    public void run() {
                        try {
                            URL url = new URL("http://101.101.161.251:8001/user/pubKey");
                            HttpURLConnection conn =
                                    (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("PATCH");
                            conn.setRequestProperty("Content-Type", "application/json");
                            conn.setRequestProperty("Accept", "application/json");
                            conn.setRequestProperty("Accept-Charset", "UTF-8");
                            conn.setConnectTimeout(10000);
                            conn.setReadTimeout(10000);
                            conn.setDoOutput(true);

                            OutputStream os = conn.getOutputStream();
                            os.write(jsonObject.toString().getBytes("UTF-8"));
                            os.flush();
                            os.close();

                            Log.i("registerPKStat", String.valueOf(conn.getResponseCode()));
                            Log.i("registerPKMsg", conn.getResponseMessage());

                            conn.disconnect();
                            startActivity(new Intent(getApplicationContext(), RegisterDeviceToken.class));
                        } catch (MalformedURLException e) {
                            System.err.println("URL 프로토콜의 형식이 잘못됨. ex) http://");
                            e.printStackTrace();
                        } catch (IOException e) {
                            System.err.println("URL Connection Failed");
                            e.printStackTrace();
                        } finally {
                            finish();
                        }
                    }
                });
            }
        }));
    }

	// 왜 커밋이 안될까
	
    public String[] createWallet(final String password) {
        String[] result = new String[2];
        try {
            File path = Environment.getExternalStoragePublicDirectory("/Wallet"); //다운로드 path 가져오기 //Environment.DIRECTORY_DOWNLOADS ///mnt/sdcard/Wallet
            if (!path.exists()) {
                path.mkdir();
            }
            String fileName = WalletUtils.generateLightNewWalletFile(password, new File(String.valueOf(path))); //지갑생성
            result[0] = path+"/"+fileName;

            Credentials credentials = WalletUtils.loadCredentials(password,result[0]);

            result[1] = credentials.getAddress();

            return result;
        } catch (NoSuchAlgorithmException
                | NoSuchProviderException
                | InvalidAlgorithmParameterException
                | IOException
                | CipherException e) {
            e.printStackTrace();
            return null;
        }
    }
}
