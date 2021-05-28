package com.aditya.onlinevoting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.aditya.onlinevoting.User.LoginActivity;
import com.aditya.onlinevoting.User.UserPollActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private CircularProgressButton adminBtn,userBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        adminBtn = (CircularProgressButton) findViewById(R.id.admin_btn);
        userBtn = (CircularProgressButton) findViewById(R.id.user_btn);

        adminBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent adminLoginIntent = new Intent(MainActivity.this,PollActivity.class);
                startActivity(adminLoginIntent);
                finish();
            }
        });

        userBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if(currentUser==null){
                    sendToLogin();
                }
                else{
                    sendToUserPoll();
                }
            }
        });
    }

    private void sendToUserPoll() {
        Intent userPollIntent = new Intent(MainActivity.this, UserPollActivity.class);
        startActivity(userPollIntent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
//
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        //String id = currentUserId;
//        if(currentUser==null){
//            sendToLogin();
//        }

    }

    private void sendToLogin() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
}