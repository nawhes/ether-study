package com.etherstudy.quizdapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.etherstudy.quizdapp.Main2Activity;
import com.etherstudy.quizdapp.QuizAnswerResultModel;
import com.etherstudy.quizdapp.QuizConstants;
import com.etherstudy.quizdapp.QuizModel;
import com.etherstudy.quizdapp.R;
import com.etherstudy.quizdapp.ChatModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChatFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {

    private static final String ARG_ROUND = "round";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private Button select1Btn;
    private Button select2Btn;
    private Button select3Btn;
    private Button select4Btn;

    private TextView quizTv;

    private Button button;
    private EditText editText;
    private ListView listView;

    private String uid;
    private int round;
    private int quizNumber = 0;

    private String str, receiveMsg;

    private OnFragmentInteractionListener mListener;
    String chatid;

    private QuizModel[] quizModels;
    private QuizAnswerResultModel quizAnswerResultModel;
    private int totalQuizNumber;

    private String selectAnswer;

    private int rightCount;
    private int wrongCount;

    private Timer nextQuizTimer;
    private Timer sendAnswerTimer;

    private boolean isWinner;

    public ChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chatid = FirebaseRemoteConfig.getInstance().getString("chatid");
        Log.d("done", "onCreate: "+chatid);

        if (getArguments() != null) {
            round = Integer.parseInt(getArguments().getString(ARG_ROUND));
            Toast.makeText(getActivity(), round+" Round", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat, container, false);

        quizTv = (TextView) v.findViewById(R.id.tv_quiz);
        select1Btn = (Button) v.findViewById(R.id.btn_select1);
        select2Btn = (Button) v.findViewById(R.id.btn_select2);
        select3Btn = (Button) v.findViewById(R.id.btn_select3);
        select4Btn = (Button) v.findViewById(R.id.btn_select4);

        listView = (ListView) v.findViewById(R.id.fragment_chat_listview);
        button = (Button) v.findViewById(R.id.fragment_chat_button);
        editText = (EditText) v.findViewById(R.id.fragment_chat_editText);
        uid = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        select1Btn.setOnClickListener(view -> {
            setBtnsClickable(false);
            selectAnswer = select1Btn.getText().toString();
        });

        select2Btn.setOnClickListener(view -> {
            setBtnsClickable(false);
            selectAnswer = select2Btn.getText().toString();
        });

        select3Btn.setOnClickListener(view -> {
            setBtnsClickable(false);
            selectAnswer = select3Btn.getText().toString();
        });

        select4Btn.setOnClickListener(view -> {
            setBtnsClickable(false);
            selectAnswer = select4Btn.getText().toString();
        });

        button.setOnClickListener(v1 -> {
            ChatModel comment = new ChatModel(uid, editText.getText().toString());
            FirebaseDatabase.getInstance().getReference().child(chatid).push().setValue(comment);
            editText.setText("");
        });

        final ArrayAdapter adapter = new ArrayAdapter(getActivity(), R.layout.item_chat_list,R.id.item_chat_textview);
        listView.setAdapter(adapter);
        // Inflate the item_chat_list for this fragment

        FirebaseDatabase.getInstance().getReference().child(chatid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adapter.clear();
                for(DataSnapshot item : dataSnapshot.getChildren()) {
                    ChatModel chatModel = item.getValue(ChatModel.class);
                    adapter.add(chatModel.getUserName() + ": " + chatModel.getMessage());
                    listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return  v;
    }

    TimerTask sendAnswerTask = new TimerTask() {
        @Override
        public void run() {
            sendAnswer();
        }
    };

    TimerTask showNextQuizTask = new TimerTask() {
        @Override
        public void run() {
            setQuestionByQuizNumber();
        }
    };

    private void setBtnsClickable(boolean flag) {
        select1Btn.setClickable(flag);
        select2Btn.setClickable(flag);
        select3Btn.setClickable(flag);
        select4Btn.setClickable(flag);
    }

    private void sendAnswer() {
        String result;
        Log.i("chpark", selectAnswer + ", " + quizModels[quizNumber].answer);
        if (quizModels[quizNumber].answer.equals(selectAnswer)) {
            isWinner = true;
            result = "right";
        } else {
            isWinner = false;
            result = "false";
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("result", result);
        }
        catch (JSONException e) {

        }
        Log.d("chpark1", jsonObject + "");

        AsyncTask.execute(() -> {
            try {

                URL url = new URL(QuizConstants.SERVER_IP + "/round/result");
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

                Log.i("sendAnswerCode", String.valueOf(conn.getResponseCode()));
                Log.i("sendAnswerMsg", conn.getResponseMessage());

                getQuizAnswerResult();

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
            finally {
                setBtnsClickable(true);
                if (!quizModels[quizNumber].answer.equals(selectAnswer)) {
                    Log.i("chpark", "탈락!!!");
                    if (nextQuizTimer != null) {
                        nextQuizTimer.cancel();
                    }
                    if (sendAnswerTimer != null) {
                        sendAnswerTimer.cancel();
                    }
                    getActivity().runOnUiThread(() -> quizTv.setText("당신은 탈락하였습니다!"));
                }
                quizNumber++;
            }
        });

    }

    private void getQuizAnswerResult() {
        AsyncTask.execute(() -> {
            try {
                URL url = new URL(QuizConstants.SERVER_IP + "/round/result/");

                HttpURLConnection conn =
                        (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("User-Agent", "QuizShow");

                if (conn.getResponseCode() == 200 || conn.getResponseCode() == 201) {
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuffer buffer = new StringBuffer();
                    while ((str = reader.readLine()) != null) {
                        buffer.append(str);
                    }
                    receiveMsg = buffer.toString();
                    Log.i("receiveMsg : ", receiveMsg);

                    reader.close();

                    Gson gson = new GsonBuilder().create();
                    quizAnswerResultModel = gson.fromJson(buffer.toString(), QuizAnswerResultModel.class);
                    rightCount = quizAnswerResultModel.rightCount;
                    wrongCount = quizAnswerResultModel.wrongCount;

                    getActivity().runOnUiThread(() -> {
                        if (quizNumber < totalQuizNumber) {
                            quizTv.setText("정답자: " + quizAnswerResultModel.rightCount + "명, 오답자: " + quizAnswerResultModel.wrongCount + "명 입니다.\n" +
                                    "잠시 후 다음 문제가 나옵니다.");

                            if (isWinner) {
                                if (nextQuizTimer == null) {
                                    nextQuizTimer = new Timer();
                                }
                                nextQuizTimer.schedule(showNextQuizTask, 5000);
                            }
                        } else {
                            quizTv.setText("최종 우승자는 " + rightCount + "명입니다! 축하드립니다!!");
                            sendAnswerTimer.cancel();
                        }
                    });

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
    }

    private void setQuestionByQuizNumber() {
        getActivity().runOnUiThread(() -> {
            if (quizNumber < totalQuizNumber) {
                select1Btn.setText(quizModels[quizNumber].quizAnswerLists.get(0).answerCase);
                select2Btn.setText(quizModels[quizNumber].quizAnswerLists.get(1).answerCase);
                select3Btn.setText(quizModels[quizNumber].quizAnswerLists.get(2).answerCase);
                select4Btn.setText(quizModels[quizNumber].quizAnswerLists.get(3).answerCase);
                quizTv.setText(quizModels[quizNumber].quiz);
            } else {

            }
        });

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        AsyncTask.execute(() -> {
            try {
                URL url = new URL(QuizConstants.SERVER_IP + "/quiz/list/" + round);

                HttpURLConnection conn =
                        (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("User-Agent", "QuizShow");

                if (conn.getResponseCode() == 200 || conn.getResponseCode() == 201) {
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuffer buffer = new StringBuffer();
                    while ((str = reader.readLine()) != null) {
                        buffer.append(str);
                    }
                    receiveMsg = buffer.toString();
                    Log.i("receiveMsg : ", receiveMsg);

                    reader.close();

                    Gson gson = new GsonBuilder().create();
                    quizModels = gson.fromJson(buffer.toString(), QuizModel[].class);
                    totalQuizNumber = quizModels.length;
                    if ( sendAnswerTimer == null) {
                        sendAnswerTimer = new Timer();
                    }
                    sendAnswerTimer.schedule(sendAnswerTask, 5000, 10000);
                    setQuestionByQuizNumber();
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
