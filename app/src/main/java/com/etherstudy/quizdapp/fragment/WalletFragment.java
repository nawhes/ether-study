package com.etherstudy.quizdapp.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.etherstudy.quizdapp.QuizConstants;
import com.etherstudy.quizdapp.QuizModel;
import com.etherstudy.quizdapp.QuizShowTokenContract;
import com.etherstudy.quizdapp.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Timer;
import java.util.concurrent.ExecutionException;

import static com.etherstudy.quizdapp.QuizConstants.GAS_LIMIT;
import static com.etherstudy.quizdapp.QuizConstants.GAS_PRICE;
import static com.etherstudy.quizdapp.QuizConstants.QUIZ_CONTRACT_ADDRESS;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WalletFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WalletFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WalletFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PUB_KEY = "pubKey";

    // TODO: Rename and change types of parameters
    private String pubKey, balance;
    private TextView tvWalletAddress, tvEthBalance, tvTokenBalance;

    private OnFragmentInteractionListener mListener;

    private Web3j web3;
    private String tokenBalance;

    public WalletFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WalletFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WalletFragment newInstance(String param1, String param2) {
        WalletFragment fragment = new WalletFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PUB_KEY, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pubKey = getArguments().getString(ARG_PUB_KEY);

            web3 = Web3j.build(new HttpService(QuizConstants.ETH_NODE_URL));
            balance = getBalance();
            tokenBalance = getTokenBalance();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_wallet, container, false);
        tvWalletAddress = v.findViewById(R.id.tvWalletAddress);
        tvEthBalance = v.findViewById(R.id.tv_eth_balance);
        tvTokenBalance = v.findViewById(R.id.tv_token_balance);

        tvWalletAddress.setText(pubKey);

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public String getBalance()
    {


        //통신할 노드의 주소를 지정해준다.

        String result = null;
        EthGetBalance ethGetBalance;
        try {

            //이더리움 노드에게 지정한 Address 의 잔액을 조회한다.
            ethGetBalance = web3.ethGetBalance(pubKey, DefaultBlockParameterName.LATEST).sendAsync().get();
            BigInteger wei = ethGetBalance.getBalance();

            //wei 단위를 ETH 단위로 변환 한다.
            result = Convert.fromWei(wei.toString(), Convert.Unit.ETHER).toString();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String getTokenBalance() {
        AsyncTask.execute(() -> {
            try {
                Credentials credentials = Credentials.create(QuizConstants.TOKEN_HOLDER_PK);
                QuizShowTokenContract contract = QuizShowTokenContract.load(QUIZ_CONTRACT_ADDRESS, web3, credentials, GAS_PRICE, GAS_LIMIT);
                BigInteger bigInteger = contract.balanceOf(pubKey).send();
                tokenBalance = String.valueOf(bigInteger.divide(QuizConstants.CONTRACT_DECIMAL));
                Log.i("chpark", "token Balance: " + tokenBalance);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(balance != null) tvEthBalance.setText("이더리움 잔액: " + balance);
                        if(tokenBalance != null) tvTokenBalance.setText("QT 토큰 잔액: " + tokenBalance);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return tokenBalance;
    }
}
