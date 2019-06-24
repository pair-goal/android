package com.noye.pairgoal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ViewGoalActivity extends AppCompatActivity {
    private Retrofit retrofit;
    private ApiInterface apiInterface;
    private Call<Diary> callGetDiary;
    private Call<Void> callPostDiary;

    private Intent intent;
    private String token, id, partner;

    RatingBar rb;
    EditText et;
    TextView partnerText;
    ImageView img;

    float rating = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_goal);

        rb = findViewById(R.id.rating_bar);
        et = findViewById(R.id.view_goal_content);
        partnerText = findViewById(R.id.view_goal_partner);
        Button bt = findViewById(R.id.write_complete_diary);
        img = findViewById(R.id.view_goal_image);

        retrofit = ApiClient.getClient();
        apiInterface = retrofit.create(ApiInterface.class);

        intent = getIntent();
        token = intent.getStringExtra("token");
        id = intent.getStringExtra("id");
        partner = intent.getStringExtra("partner");

        callGetDiary(this);

        rb.setOnRatingBarChangeListener(((ratingBar, rating, fromUser) -> this.rating = rating));
        bt.setOnClickListener(v -> callPostDiary(this));
    }

    private void callGetDiary(AppCompatActivity activity) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        TimeZone timeZone = TimeZone.getTimeZone("Asia/Seoul");
        format.setTimeZone(timeZone);

        String date = format.format(new Date());

        callGetDiary = apiInterface.doGetDiary(token, id, date);
        callGetDiary.enqueue(new Callback<Diary>() {
            @Override
            public void onResponse(Call<Diary> call, Response<Diary> response) {
                if(response.code() == 200) {
                    Diary result = response.body();

                    et.setText(result.getComment());
                    partnerText.setText(partner);

                    if(result.getScore() != null) rb.setRating(Float.parseFloat(result.getScore()));

                    new Thread(() -> {
                        try {
                            if(result.getImage_path() == " ") return;

                            ResponseBody responseBody = HttpConnection.getInstance().responseS3(result.getImage_path()).body();

                            InputStream inputStream = responseBody.byteStream();
                            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                            Bitmap bitmap = BitmapFactory.decodeStream(bufferedInputStream);

                            activity.runOnUiThread(() -> img.setImageBitmap(bitmap));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }).start();
                } else if(response.code() == 403) {
                    Log.d("403--------------", "403");
                } else {
                    Log.d("else error", Integer.toString(response.code()));
                }
            }

            @Override
            public void onFailure(Call<Diary> call, Throwable t) {
                Log.d("error---------------", "error");
                t.printStackTrace();
            }
        });
    }

    private void callPostDiary(AppCompatActivity activity) {
        Diary diary = new Diary("", et.getText().toString(),  Float.toString(rating), Float.toString(rating), et.getText().toString(), "");
        String date = (new SimpleDateFormat("yyyyMMdd", Locale.KOREA)).format(new Date());

        callPostDiary = apiInterface.doPutDiary(token, id, date, diary);
        callPostDiary.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code() == 201) {
                    Log.d("201---------------", "201");
                    activity.finish();
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
