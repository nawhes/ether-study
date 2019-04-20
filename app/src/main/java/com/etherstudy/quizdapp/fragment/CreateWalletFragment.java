package com.etherstudy.quizdapp.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.etherstudy.quizdapp.R;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateWalletFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreateWalletFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateWalletFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;

    private EditText etPassword;
    private TextView tvCreatedWalletAddress, tvCreatedWalletPath;
    private Button btnCreateWallet;

    private OnFragmentInteractionListener mListener;

    public CreateWalletFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateWalletFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateWalletFragment newInstance(String param1, String param2) {
        CreateWalletFragment fragment = new CreateWalletFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the item_chat_list for this fragment
        View v = inflater.inflate(R.layout.fragment_create_wallet, container, false);

        etPassword = v.findViewById(R.id.etPassword);
        tvCreatedWalletAddress = v.findViewById(R.id.tvCreatedWalletAddress);
        tvCreatedWalletPath = v.findViewById(R.id.tvCreatedWalletPath);
        btnCreateWallet = v.findViewById(R.id.btnCreateWallet);
        btnCreateWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String password = etPassword.getText().toString();
                String[] result = createWallet(password);
                if (result != null) {
                    String walletAddress = result[1];
                    String walletPath = result[0];
                    tvCreatedWalletAddress.setText(walletAddress);
                    tvCreatedWalletPath.setText(walletPath);
                }
            }
        });
        return v;
    }

//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//
//    }

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
