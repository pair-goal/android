package com.noye.pairgoal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {
    private Retrofit retrofit;
    private ApiInterface apiInterface;
    private Call<User> callSignin;
    private Call<Void> callSignup;

    TabHost th;
    TextView signinNickname, signupNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        retrofit = ApiClient.getClient();
        apiInterface = retrofit.create(ApiInterface.class);

        th = findViewById(R.id.th);
        th.setup();

        TabHost.TabSpec ts1 = th.newTabSpec("SIGN IN");
        ts1.setIndicator("SIGN IN");
        ts1.setContent(R.id.sign_in);
        TabHost.TabSpec ts2 = th.newTabSpec("SIGN UP");
        ts2.setIndicator("SIGN UP");
        ts2.setContent(R.id.sign_up);

        th.addTab(ts1);
        th.addTab(ts2);

        th.setCurrentTab(0);

        Button signin = findViewById(R.id.sign_in_button);
        Button signup = findViewById(R.id.sign_up_button);

        signinNickname = findViewById(R.id.nickname_sign_in);
        signupNickname = findViewById(R.id.nickname_sign_up);
        TextView signinPw = findViewById(R.id.pw_sign_in);
        TextView signupPw = findViewById(R.id.pw_sign_up);

        signin.setOnClickListener(v -> {
            User user = new User(null, signinNickname.getText().toString(), signinPw.getText().toString());

            callSignin(user);
        });

        signup.setOnClickListener(v -> {
            User user = new User(null, signupNickname.getText().toString(), signupPw.getText().toString());

            callSignup(user);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void callSignin(User user) {
        callSignin = apiInterface.signinUser(user);
        callSignin.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.code() == 200) {
                    User result = response.body();

                    Intent intent = new Intent(getApplicationContext(), GoalActivity.class);
                    intent.putExtra("token", result.getToken());
                    intent.putExtra("nickname", signinNickname.getText().toString());
                    startActivity(intent);
                } else if(response.code() == 403) {
                    Log.d("403", "403");
                } else {
                    Log.d("else code", Integer.toString(response.code()));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void callSignup(User user) {
        callSignup = apiInterface.createUser(user);
        callSignup.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code() == 201) {
                    th.setCurrentTab(0);
                } else if(response.code() == 403) {
                    Log.d("403", "403");
                } else {
                    Log.d("else code", Integer.toString(response.code()));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
