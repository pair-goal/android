package com.noye.pairgoal;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class PartnerAdapter extends RecyclerView.Adapter<PartnerAdapter.ViewHolder> {
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nickname, intro, id;

        ViewHolder(View view) {
            super(view);

            id = view.findViewById(R.id.recycler_small_id);
            nickname = view.findViewById(R.id.recycler_nickname);
            intro = view.findViewById(R.id.recycler_intro);
        }

        void onBind(PartnerData data) {
            id.setText(data.getId());
            nickname.setText(data.getNickname());
            intro.setText(data.getIntro());
        }
    }

    private ArrayList<PartnerData> listData = new ArrayList<>();

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.small_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.onBind(listData.get(position));
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    void addItem(PartnerData data) {
        listData.add(data);
    }
}

class PartnerData {
    private String id, nickname, intro;

    PartnerData(String id, String nickname, String intro) {
        this.id = id;
        this.nickname = nickname;
        this.intro = intro;
    }

    public String getNickname() {
        return nickname;
    }

    public String getIntro() {
        return intro;
    }

    public String getId() {
        return id;
    }
}
