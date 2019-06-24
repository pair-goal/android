package com.noye.pairgoal;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class ChattingAdapter extends RecyclerView.Adapter<ChattingAdapter.ViewHolder> {
    public static final int YOUR_CHATTING = 0;
    public static final int MY_CHATTING = 1;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nickname, message;

        ViewHolder(View view, int type) {
            super(view);

            if(type == MY_CHATTING) {
                nickname = view.findViewById(R.id.my_chatting_nickname);
                message = view.findViewById(R.id.my_chatting_message);
            } else {
                nickname = view.findViewById(R.id.your_chatting_nickname);
                message = view.findViewById(R.id.your_chatting_message);
            }
        }

        void onBind(ChattingData data) {
            nickname.setText(data.getNickname());
            message.setText(data.getMessage());
        }
    }

    private ArrayList<ChattingData> listData = new ArrayList<>();

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if(viewType == MY_CHATTING)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_chatting_item, parent, false);
        else
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.your_chatting_item, parent, false);

        return new ViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.onBind(listData.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        if(listData.get(position).getItemViewType() == 0)
            return YOUR_CHATTING;
        else
            return MY_CHATTING;
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    void addItem(ChattingData data) {
        listData.add(data);
    }
}

class ChattingData {
    private String nickname, message;
    private int itemViewType;

    ChattingData(String nickname, String message, int itemViewType) {
        this.nickname = nickname;
        this.message = message;
        this.itemViewType = itemViewType;
    }

    public String getNickname() {
        return nickname;
    }

    public String getMessage() {
        return message;
    }

    public int getItemViewType() { return itemViewType; }
}
