package com.noye.pairgoal;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import okhttp3.Response;
import okhttp3.ResponseBody;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.ViewHolder> {
    Activity activity;

    GoalAdapter(Activity activity) {
        this.activity = activity;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView title, date, category, partner, id;
        private ImageView img;

        ViewHolder(View view) {
            super(view);

            title = view.findViewById(R.id.recycler_title);
            date = view.findViewById(R.id.recycler_date);
            category = view.findViewById(R.id.recycler_category);
            partner = view.findViewById(R.id.recycler_partner);
            id = view.findViewById(R.id.recycler_id);
            img = view.findViewById(R.id.recycler_goal_image);
        }

        void onBind(GoalData data) {
            title.setText(data.getTitle());
            date.setText(data.getDate());
            category.setText("카테고리: ".concat(data.getCategory()));
            partner.setText("파트너: ".concat(data.getPartner() == null ? "없음" : data.getPartner()));
            id.setText(data.getId());

            new Thread(() -> {
                try {
                    if(data.getUrl() == " ") return;

                    ResponseBody responseBody = HttpConnection.getInstance().responseS3(data.getUrl()).body();

                    InputStream inputStream = responseBody.byteStream();
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                    Bitmap bitmap = BitmapFactory.decodeStream(bufferedInputStream);

                    activity.runOnUiThread(() -> img.setImageBitmap(bitmap));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private ArrayList<GoalData> listData = new ArrayList<>();

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.goal_item, parent, false);
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

    void addItem(GoalData data) {
        listData.add(data);
    }
}

class GoalData {
    private String id, title, date, category, partner, url;

    GoalData(String id, String title, String date, String category, String partner, String url) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.category = category;
        this.partner = partner;
        this.url = url;
    }

    public String getId() { return id; }

    public String getTitle() {
        return title;
    }

    public String getDate() { return date;  }

    public String getCategory() {
        if(Integer.parseInt(category) == 1) return "공부";
        else if (Integer.parseInt(category) == 2) return "운동";
        else if(Integer.parseInt(category) == 3) return "습관";
        else return "기타";
    }

    public String getPartner() {
        return partner;
    }

    public String getUrl() {
        return url;
    }
}
