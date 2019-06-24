package com.noye.pairgoal;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class GoalActivity extends AppCompatActivity {
    private GoalAdapter goalAdapter, resultAdapter;
    private PartnerAdapter partnerAdapter;

    private Retrofit retrofit;
    private ApiInterface apiInterface;
    private Call<GoalRes> callGoalList;
    private Call<GoalRes> callResultList;
    private Call<ArrayList<Conversation>> callConversationList;

    private Intent intent;
    private String token, nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal);

        intent = getIntent();
        token = intent.getStringExtra("token");
        nickname = intent.getStringExtra("nickname");

        retrofit = ApiClient.getClient();
        apiInterface = retrofit.create(ApiInterface.class);
        callGoalList();
        callConversationList();

        TabHost th = findViewById(R.id.th_goal);
        th.setup();

        TabHost.TabSpec ts1 = th.newTabSpec("GOAL");
        ts1.setIndicator("목표");
        ts1.setContent(R.id.goal);
        TabHost.TabSpec ts2 = th.newTabSpec("RESULT");
        ts2.setIndicator("결과");
        ts2.setContent(R.id.result);
        TabHost.TabSpec ts3 = th.newTabSpec("PARTNER");
        ts3.setIndicator("파트너");
        ts3.setContent(R.id.partner);

        th.addTab(ts1);
        th.addTab(ts2);
        th.addTab(ts3);

        th.setCurrentTab(0);
    }

    public void goViewGoal(View v) {
        TextView recyclerId = v.findViewById(R.id.recycler_id);
        TextView partner = v.findViewById(R.id.recycler_partner);
        ImageView img = v.findViewById(R.id.recycler_goal_image);
        Intent intent = new Intent(v.getContext(), ViewGoalActivity.class);
        intent.putExtra("token", token);
        intent.putExtra("id", recyclerId.getText());
        intent.putExtra("partner", partner.getText());
        startActivity(intent);
    }

    public void goCreateGoal(View v) {
        Intent intent = new Intent(v.getContext(), CreateGoalActivity.class);
        intent.putExtra("token", token);
        startActivity(intent);
    }

    public void goChatting(View v) {
        TextView recyclerId = v.findViewById(R.id.recycler_small_id);
        Intent intent = new Intent(v.getContext(), ChattingActivity.class);
        intent.putExtra("token", token);
        intent.putExtra("id", recyclerId.getText());
        intent.putExtra("nickname", nickname);
        startActivity(intent);
    }

    private void recyclerInit(int id, int adapterType) {
        RecyclerView recyclerView = findViewById(id);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        if(adapterType == 1) {
            goalAdapter = new GoalAdapter(this);
            recyclerView.setAdapter(goalAdapter);
        } else if(adapterType == 2) {
            resultAdapter = new GoalAdapter(this);
            recyclerView.setAdapter(resultAdapter);
        } else {
            partnerAdapter = new PartnerAdapter();
            recyclerView.setAdapter(partnerAdapter);
        }
    }

    private void getRecyclerGoalData(ArrayList<Goal> goalData, ArrayList<String> url, boolean isGoal) {
        if(isGoal) recyclerInit(R.id.goal_recycler, 1);
        else recyclerInit(R.id.result_recycler, 2);

        for(int i=0; i<goalData.size(); i++) {
            Goal goal = goalData.get(i);
            String date = goal.getStart_date().substring(0, 10).concat("~").concat(goal.getEnd_date().substring(0, 10));
            GoalData data = new GoalData(goal.getId(), goal.getTitle(), date, goal.getCategory_num(), goal.getPartner_name(), url.get(i));

            if(isGoal) goalAdapter.addItem(data);
            else resultAdapter.addItem(data);
        }

        if(isGoal) goalAdapter.notifyDataSetChanged();
        else resultAdapter.notifyDataSetChanged();
    }

    private void getRecyclerConversationData(ArrayList<Conversation> conversationData) {
        recyclerInit(R.id.partner_recycler, 3);

        for(int i=0; i<conversationData.size(); i++) {
            Conversation conversationD = conversationData.get(i);
            Conversation.Participant participant;

            if(conversationD.getParticipants().get(0).getNickname().equals(nickname)) participant = conversationD.getParticipants().get(1);
            else participant = conversationD.getParticipants().get(0);

            PartnerData data = new PartnerData(conversationD.getId().get$oid(), participant.getNickname(), participant.getTitle());

            partnerAdapter.addItem(data);
        }

        partnerAdapter.notifyDataSetChanged();
    }

    private void callGoalList() {
        callGoalList = apiInterface.doGetGoals(token, true);
        callGoalList.enqueue(new Callback<GoalRes>() {
            @Override
            public void onResponse(Call<GoalRes> call, Response<GoalRes> response) {
                if(response.code() == 200) {
                    GoalRes result = response.body();

                    getRecyclerGoalData(result.getGoals(), result.getUrls(), true);
                } else if(response.code() == 403) {
                    Log.d("403", "403");
                }
            }

            @Override
            public void onFailure(Call<GoalRes> call, Throwable t) {
                t.printStackTrace();
            }
        });

        callResultList = apiInterface.doGetGoals(token, false);
        callResultList.enqueue(new Callback<GoalRes>() {
            @Override
            public void onResponse(Call<GoalRes> call, Response<GoalRes> response) {
                if(response.code() == 200) {
                    GoalRes result = response.body();

                    getRecyclerGoalData(result.getGoals(), result.getUrls(), false);
                } else if(response.code() == 403) {
                    Log.d("403", "403");
                }
            }

            @Override
            public void onFailure(Call<GoalRes> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void callConversationList() {
        callConversationList = apiInterface.doGetConversations(token);
        callConversationList.enqueue(new Callback<ArrayList<Conversation>>() {
            @Override
            public void onResponse(Call<ArrayList<Conversation>> call, Response<ArrayList<Conversation>> response) {
                if(response.code() == 200) {
                    ArrayList<Conversation> result = response.body();

                    getRecyclerConversationData(result);
                } else if(response.code() == 403) {
                    Log.d("403", "403");
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Conversation>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
