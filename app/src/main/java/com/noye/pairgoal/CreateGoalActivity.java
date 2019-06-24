package com.noye.pairgoal;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CreateGoalActivity extends AppCompatActivity {
    private Retrofit retrofit;
    private ApiInterface apiInterface;
    private Call<ImageUrl> callPostGoal;

    private Intent intent;
    private String token;

    private final int GET_GALLERY_IMAGE = 200;
    ImageView goalImage;
    String startYear, startMonth, startDay, endYear, endMonth, endDay;

    EditText goalTitle;
    Spinner goalCategory;

    Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_goal);

        intent = getIntent();
        token = intent.getStringExtra("token");

        retrofit = ApiClient.getClient();
        apiInterface = retrofit.create(ApiInterface.class);

        Button changeImageButton = findViewById(R.id.change_goal_image);
        Button startButton = findViewById(R.id.crate_goal_start);
        Button endButton = findViewById(R.id.create_goal_end);
        Button writeComplete = findViewById(R.id.write_complete);
        goalTitle = findViewById(R.id.create_goal_title);
        goalCategory = findViewById(R.id.create_goal_category);
        goalImage = findViewById(R.id.crate_goal_image);

        changeImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent. setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(intent, GET_GALLERY_IMAGE);
        });

        startButton.setOnClickListener(v ->
            new DatePickerDialog(this, (view, year, month, day) -> {
                startYear = Integer.toString(year);
                startMonth = Integer.toString(month+1);
                startDay = Integer.toString(day);

                if(month<9) startMonth = "0".concat(startMonth);
                if(day<10) startDay = "0".concat(startDay);
            }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH)).show()
        );

        endButton.setOnClickListener(v ->
                new DatePickerDialog(this, (view, year, month, day) -> {
                    endYear = Integer.toString(year);
                    endMonth = Integer.toString(month+1);
                    endDay = Integer.toString(day);

                    if(month<9) endMonth = "0".concat(endMonth);
                    if(day<10) endDay = "0".concat(endDay);
                }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH)).show()
        );

        writeComplete.setOnClickListener(v ->  callPostGoal(this));
    }

    @Override
    protected  void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == GET_GALLERY_IMAGE && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            goalImage.setImageURI(selectedImageUri);
        }
    }

    private void callPostGoal(AppCompatActivity activity) {
        String startDate = startYear.concat(startMonth).concat(startDay);
        String endDate = startYear.concat(endMonth).concat(endDay);
        String title = goalTitle.getText().toString();
        String category = goalCategory.getSelectedItem().toString();
        String categoryNum;

        if(category == "공부") categoryNum = "1";
        else if(category == "운동") categoryNum = "2";
        else if(category == "습관") categoryNum = "3";
        else categoryNum = "4";

        Goal goal = new Goal(null, title, categoryNum, startDate, endDate,
                selectedImageUri == null ?  null : getRealPathFromURI(selectedImageUri), null);

        callPostGoal = apiInterface.doPostGoal(token, goal);
        callPostGoal.enqueue(new Callback<ImageUrl>() {
            @Override
            public void onResponse(Call<ImageUrl> call, Response<ImageUrl> response) {
                if(response.code() == 201) {
                    ImageUrl result = response.body();

                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                HttpConnection.getInstance().requestS3(result.getUrl(), getRealPathFromURI(selectedImageUri));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();

                    activity.finish();
                } else if(response.code() == 403) {
                    Log.d("403", "403");
                }
            }

            @Override
            public void onFailure(Call<ImageUrl> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }
}
