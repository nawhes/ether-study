package com.etherstudy.quizdapp.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.etherstudy.quizdapp.QuizConstants;
import com.etherstudy.quizdapp.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView showInformationTv;
    private Button showStartBtn;

    private int round;
    private String startDate;
    private String rewardToken;
    private int rewardAmount;

    private OnFragmentInteractionListener mListener;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        AsyncTask.execute(() -> { // 사용자 계정의 공개키 조회
            try {
                URL url = new URL(QuizConstants.SERVER_IP + "/show");
                HttpURLConnection conn =
                        (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("User-Agent", "QuizShow");

                if (conn.getResponseCode() == 200 || conn.getResponseCode() == 201) {
                    InputStream responseBody = conn.getInputStream();
                    InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
                    JsonReader jsonReader = new JsonReader(responseBodyReader);
                    jsonReader.beginObject();
                    Log.i("chpark", jsonReader.toString());
                    while (jsonReader.hasNext()) {
                        String key = jsonReader.nextName();
                        if (key.equals("round")) {
                            round = jsonReader.nextInt();
                        } else if (key.equals("startDate")) {
                            startDate = jsonReader.nextString();
                        } else if (key.equals("rewardToken")) {
                            rewardToken = jsonReader.nextString();
                        } else if (key.equals("rewardAmount")) {
                            rewardAmount = jsonReader.nextInt();
                        }
                        else jsonReader.skipValue();
                    }
                    Log.i("chpark", round + ", " + startDate + ", " + rewardToken + ", " + rewardAmount);
                    showInformationTv.setText(round + "라운드 퀴즈쇼가 " + startDate + "에 시작됩니다.\n " + "상품은 " + rewardToken + "토큰 " + rewardAmount + "개 입니다!");
                    jsonReader.close();
                    responseBodyReader.close();
                    responseBody.close();

                } else {
                    Log.d("chpark", conn.getResponseCode() + "");
                }
                conn.disconnect();
            } catch (MalformedURLException e) {
                System.err.println("URL 프로토콜의 형식이 잘못됨. ex) http://");
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        // Inflate the layout for this fragment

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        showInformationTv = (TextView) v.findViewById(R.id.tv_quizinfo);
        showStartBtn = (Button) v.findViewById(R.id.btn_start_show);

        showStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                ChatFragment fragment = new ChatFragment();
                Bundle bundle = new Bundle(1);
                bundle.putString("round", String.valueOf(round));
                fragment.setArguments(bundle);
                transaction.replace(R.id.content_main_framelayout, fragment);
                transaction.commit();
            }
        });
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
}
