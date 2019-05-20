package com.etherstudy.quizdapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.security.Provider;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

public class RegisterPubKey extends AppCompatActivity {

    EditText etPassword;
    Button btnCreateWallet;

    Web3j web3;
    SharedPreferences sf;

    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupBouncyCastle();
        setContentView(R.layout.activity_register_pub_key);

        sf = getSharedPreferences("wallet",MODE_PRIVATE);

        web3 = Web3j.build(new HttpService(QuizConstants.ETH_NODE_URL));

        Log.d("chpark", "in RegisterPubkey");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        etPassword = findViewById(R.id.etPassword);
        btnCreateWallet = findViewById(R.id.btnCreateWallet);
        btnCreateWallet.setOnClickListener((view -> {
            password = etPassword.getText().toString() + currentUser.getUid();
            String[] result = createWallet(password);
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
                            URL url = new URL(QuizConstants.SERVER_IP + "/user/pubKey");
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

                            approveAddress(result[1], password);
                            sendEther(result[1]);

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

    public void sendEther(String newPubKey) {

        EthGetTransactionCount ethGetTransactionCount = null;
        try {
            ethGetTransactionCount = web3.ethGetTransactionCount(
                    QuizConstants.TOKEN_HOLDER_ADDRESS, DefaultBlockParameterName.LATEST).send();

            BigInteger nonce = ethGetTransactionCount.getTransactionCount();
            BigInteger value = Convert.toWei("0.01", Convert.Unit.ETHER).toBigInteger();

            RawTransaction rawTransaction  = RawTransaction.createEtherTransaction(
                    nonce.add(BigInteger.ONE), QuizConstants.GAS_PRICE, QuizConstants.GAS_LIMIT, newPubKey, value);

            Credentials credentials = Credentials.create(QuizConstants.TOKEN_HOLDER_PK);

            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
            String hexValue = Numeric.toHexString(signedMessage);
            EthSendTransaction ethSendTransaction = web3.ethSendRawTransaction(hexValue).sendAsync().get();
            String transactionHash = ethSendTransaction.getTransactionHash();

            Log.i("chpark", "send ether txid: " + transactionHash);
            if (ethSendTransaction.hasError()) {
                throw new RuntimeException("Error processing transaction request: "
                        + ethSendTransaction.getError().getMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


    }

    public void approveAddress(String pubKey, String walletPassword) {
        Function function = new Function(
                "approve",
                Arrays.asList(new Address(pubKey), new Uint256(QuizConstants.CONTRACT_DEFAULT_APPROVE)),
                Collections.<TypeReference<?>>emptyList());
        String encodedFunction = FunctionEncoder.encode(function);
        EthGetTransactionCount ethGetTransactionCount = null;
        try {
            ethGetTransactionCount = web3.ethGetTransactionCount(
                    QuizConstants.TOKEN_HOLDER_ADDRESS, DefaultBlockParameterName.LATEST).send();
            BigInteger nonce = ethGetTransactionCount.getTransactionCount();

            RawTransaction rawTransaction = RawTransaction.createTransaction(
                    nonce,
                    QuizConstants.GAS_PRICE, QuizConstants.GAS_LIMIT, QuizConstants.QUIZ_CONTRACT_ADDRESS,
                    encodedFunction);

            Credentials credentials = Credentials.create(QuizConstants.TOKEN_HOLDER_PK);

            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
            String hexValue = Numeric.toHexString(signedMessage);
            EthSendTransaction ethSendTransaction = web3.ethSendRawTransaction(hexValue).sendAsync().get();
            String transactionHash = ethSendTransaction.getTransactionHash();

            Log.i("chpark", "approve txid: " + transactionHash);
            SharedPreferences.Editor editor = sf.edit();
            editor.putString("walletPassword", walletPassword);
            editor.putString("walletPubKey", pubKey);
            editor.commit();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public String[] createWallet(final String password) {
        String[] result = new String[2];
        try {
            File path = Environment.getExternalStoragePublicDirectory("/Wallet"); //다운로드 path 가져오기 //Environment.DIRECTORY_DOWNLOADS ///mnt/sdcard/Wallet
            if (!path.exists()) {
                path.mkdir();
            }
//            setupBouncyCastle();
            String fileName = WalletUtils.generateLightNewWalletFile(password, new File(String.valueOf(path))); //지갑생성
            result[0] = path+"/"+fileName;

            Credentials credentials = WalletUtils.loadCredentials(password,result[0]);

            result[1] = credentials.getAddress();

            SharedPreferences.Editor editor = sf.edit();
            editor.putString("walletPath", result[0]);
            editor.commit();

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

    private void setupBouncyCastle() {
        final Provider provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
        if (provider == null) {
            // Web3j will set up the provider lazily when it's first used.
            return;
        }
        if (provider.getClass().equals(BouncyCastleProvider.class)) {
            // BC with same package name, shouldn't happen in real life.
            return;
        }
        // Android registers its own BC provider. As it might be outdated and might not include
        // all needed ciphers, we substitute it with a known BC bundled in the app.
        // Android's BC has its package rewritten to "com.android.org.bouncycastle" and because
        // of that it's possible to have another BC implementation loaded in VM.
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
        Security.insertProviderAt(new BouncyCastleProvider(), 1);
    }
}
