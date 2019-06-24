package com.noye.pairgoal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Ack;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.socketio.client.IO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ChattingActivity extends AppCompatActivity {
    ChattingAdapter chattingAdapter;

    private Retrofit retrofit;
    private ApiInterface apiInterface;

    private Call<ArrayList<Message>> callMessageList;
    private Call<Message> callMessage;
    private Call<Void> callPostMessage;

    private Intent intent;
    private String token, id, nickname;

    private RecyclerView recyclerView;

    Button btn;
    EditText et;

    private Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        intent = getIntent();
        token = intent.getStringExtra("token");
        id = intent.getStringExtra("id");
        nickname = intent.getStringExtra("nickname");

        btn = findViewById(R.id.send_button);
        et = findViewById(R.id.message);

        try {
            socket = IO.socket("http://10.0.2.2:8888?nickname="+nickname);
        } catch (Exception e) {
            e.printStackTrace();
        }

        socket.on("sendMessage", args -> runOnUiThread(() -> {
            Log.d("on SendMessage", "sendmessage");
            String messageId = args[0].toString();

            callMessage(messageId);
        }));
        socket.connect();

        retrofit = ApiClient.getClient();
        apiInterface = retrofit.create(ApiInterface.class);

        btn.setOnClickListener(v -> {
            String message = et.getText().toString().trim();

            if (TextUtils.isEmpty(message)) return;

            String[] data = new String[1];
            data[0] = "abcd";
            socket.emit("sendMessage", data, args -> callPostMessage());
        });

        callMessageList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        socket.disconnect();
        socket.off();
    }

    private void recyclerInit(int id) {
        recyclerView = findViewById(id);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        chattingAdapter = new ChattingAdapter();
        recyclerView.setAdapter(chattingAdapter);
    }

    private void getRecyclerData(ArrayList<Message> messageData) {
        recyclerInit(R.id.chatting_recycler);

        for(int i=0; i<messageData.size(); i++) {
            int itemType;
            if(messageData.get(i).getSender().equals(nickname)) itemType = 1;
            else itemType = 0;

            ChattingData data = new ChattingData(messageData.get(i).getSender(), messageData.get(i).getContent(), itemType);

            chattingAdapter.addItem(data);
        }

        chattingAdapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount()-1);
    }

    private void callMessageList() {
        callMessageList = apiInterface.doGetMessages(token, id);
        callMessageList.enqueue(new Callback<ArrayList<Message>>() {
            @Override
            public void onResponse(Call<ArrayList<Message>> call, Response<ArrayList<Message>> response) {
                if(response.code() == 200) {
                    ArrayList<Message> result = response.body();

                    getRecyclerData(result);
                } else if(response.code() == 403) {
                    Log.d("403", "403");
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Message>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void callMessage(String messageId) {
        callMessage = apiInterface.doGetMessage(token, messageId);
        callMessage.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if(response.code() == 200) {
                    Message result = response.body();

                    int itemType;
                    if(result.getSender() == nickname) itemType = 1;
                    else itemType = 0;

                    ChattingData data = new ChattingData(result.getSender(), result.getContent(), itemType);
                    chattingAdapter.addItem(data);
                    chattingAdapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount()-1);
                } else if(response.code() == 403) {
                    Log.d("403", "403");
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void callPostMessage() {
        Message message = new Message(null, null, et.getText().toString(), null);

        callPostMessage = apiInterface.doPostMessage(token, id, message);
        callPostMessage.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code() == 204) {
                    Log.d("204", "204");

                    ChattingData data = new ChattingData(nickname, et.getText().toString(), 1);
                    chattingAdapter.addItem(data);
                    chattingAdapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount()-1);
                    et.setText("");
                } else if(response.code() == 403) {
                    Log.d("403", "403");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
