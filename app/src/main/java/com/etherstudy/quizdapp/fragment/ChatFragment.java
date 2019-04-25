package com.etherstudy.quizdapp.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.etherstudy.quizdapp.R;
import com.etherstudy.quizdapp.ChatModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
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
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private Button chat_send;
    private EditText chat_text;
    private ListView listView;

    private TextView quiz_question;
    private RadioGroup quiz_view;
    private RadioButton quiz_view1;
    private RadioButton quiz_view2;
    private RadioButton quiz_view3;
    private long current_time;
    private long quiz_start_time;

    private Timer quiz_timer;
    private Timer left_timer;
    private TimerTask quiz_timertask;



    private String chat_email;

    private OnFragmentInteractionListener mListener;
    String chatid;

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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat, container, false);

        //챗
        listView = (ListView) v.findViewById(R.id.fragment_chat_listview);
        chat_send = (Button) v.findViewById(R.id.fragment_chat_button);
        chat_text = (EditText) v.findViewById(R.id.fragment_chat_editText);

        //퀴즈
        quiz_question = (TextView) v.findViewById(R.id.quiz_question);
        quiz_view = (RadioGroup) v.findViewById(R.id.quiz_view);
        quiz_view1 = (RadioButton) v.findViewById(R.id.quiz_view1);
        quiz_view2 = (RadioButton) v.findViewById(R.id.quiz_view2);
        quiz_view3 = (RadioButton) v.findViewById(R.id.quiz_view3);
//        current_time = System.currentTimeMillis();



        chat_email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        chat_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatModel comment = new ChatModel(chat_email, chat_text.getText().toString());
                FirebaseDatabase.getInstance().getReference().child(chatid).push().setValue(comment);
                chat_text.setText("");
            }
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


//        AsyncTask.execute(new Runnable() { // 퀴즈 받아오기 구현될 부분
//            @Override
//            public void run() {
//                try {
//                    URL url = new URL("http://101.101.161.251:8001/user/email/" + currentUser.getEmail());
//                    HttpURLConnection conn =
//                            (HttpURLConnection) url.openConnection();
//                    conn.setRequestProperty("User-Agent", "QuizShow");
//
//                    if(conn.getResponseCode() == 200 || conn.getResponseCode() == 201) {
//                        InputStream responseBody = conn.getInputStream();
//                        InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
//                        JsonReader jsonReader = new JsonReader(responseBodyReader);
//                        jsonReader.beginObject();
//                        while(jsonReader.hasNext()) {
//                            String key = jsonReader.nextName();
//                            if(key.equals("email")) {
//                                email = jsonReader.nextString();
//                                break;
//                            }
//                            else {
//                                jsonReader.skipValue();
//                            }
//                        }
//                        jsonReader.close();
//                        if(email == null) {
//                            JSONObject jsonObject = new JSONObject();
//                            try {
//                                jsonObject.put("email", currentUser.getEmail());
//                                jsonObject.put("uid", currentUser.getUid());
//                            }
//                            catch (JSONException e) {
//
//                            }
//                            AsyncTask.execute(new Runnable() { // 사용자 계정 등록
//                                @Override
//                                public void run() {
//                                    try {
//                                        URL url = new URL("http://101.101.161.251:8001/user");
//                                        HttpsURLConnection conn =
//                                                (HttpsURLConnection) url.openConnection();
//                                        conn.setRequestMethod("POST");
//                                        conn.setRequestProperty("User-Agent", "QuizShow");
//                                        conn.setRequestProperty("Content-Type", "application/json");
//                                        conn.setRequestProperty("Accept-Charset", "UTF-8");
//                                        conn.setConnectTimeout(10000);
//                                        conn.setReadTimeout(10000);
//
//                                        OutputStream os = conn.getOutputStream();
//                                        os.write(jsonObject.toString().getBytes("UTF-8"));
//                                        os.flush();
//
//                                        conn.disconnect();
//                                    }
//                                    catch (MalformedURLException e) {
//                                        System.err.println("URL 프로토콜의 형식이 잘못됨. ex) http://");
//                                        e.printStackTrace();
//                                    }
//                                    catch (IOException e) {
//                                        System.err.println("URL Connection Failed");
//                                        e.printStackTrace();
//                                    }
//                                }
//                            });
//                        }
//                    }
//                    else {
//                        Log.d("chpark", conn.getResponseCode() + "");
//                    }
//                conn.disconnect();
//                }
//                catch (MalformedURLException e) {
//                    System.err.println("URL 프로토콜의 형식이 잘못됨. ex) http://");
//                    e.printStackTrace();
//                }
//                catch (IOException e) {
//                    System.err.println("URL Connection Failed");
//                    e.printStackTrace();
//                }
//            }
//        });


        JSONObject quiz = new JSONObject(result);
        quiz_start_time = quiz.getLong("startTime");


        quiz_timertask = new TimerTask() {
            @Override
            public void run() {

                int UserSelectedAnswer = quiz_view.getCheckedRadioButtonId();

                quiz_display(quiz.question, quiz.view1, quiz.view2, quiz.view3);
            }
        };

        quiz_timer = new Timer();
        left_timer = new Timer();

        quiz_timer.schedule(quiz_timertask,Date quiz_start_time, delay);



        return  v;
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

    public void quiz_display(String question, String view1, String view2, String view3){
        quiz_question.setText(question);
        quiz_view1.setText(view1);
        quiz_view2.setText(view2);
        quiz_view3.setText(view3);
    }



}
