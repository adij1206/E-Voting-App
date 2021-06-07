package com.aditya.onlinevoting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.aditya.onlinevoting.Admin.AdminLoginActivity;
import com.aditya.onlinevoting.User.LoginActivity;
import com.aditya.onlinevoting.User.UserPollActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private CircularProgressButton adminBtn,userBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();

        adminBtn = (CircularProgressButton) findViewById(R.id.admin_btn);
        userBtn = (CircularProgressButton) findViewById(R.id.user_btn);

        adminBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent adminLoginIntent = new Intent(MainActivity.this,PollActivity.class);
//                startActivity(adminLoginIntent);
//                finish();
                FirebaseUser cUser = mAuth.getCurrentUser();
                if(cUser==null){
                    Intent adminLoginIntent = new Intent(MainActivity.this,AdminLoginActivity.class);
                    startActivity(adminLoginIntent);
                    finish();
                }
                else{
                    String uid = cUser.getUid();
                    mRef.child("Admin").child(uid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                sendToPoll();
                                Toast.makeText(MainActivity.this, "SuccessFully Logged In...", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                mAuth.signOut();
                                Toast.makeText(MainActivity.this, "You Are Not Admin!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
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
                    String uId = currentUser.getUid();
                    mRef.child("Voters").child(uId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                sendToUserPoll();
                                Toast.makeText(MainActivity.this, "SuccessFully Logged In...", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                mAuth.signOut();
                                Toast.makeText(MainActivity.this, "You Are Not User!", Toast.LENGTH_SHORT).show();
                                sendToLogin();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }
        });
    }

    private void sendToPoll() {
        Intent pollIntent = new Intent(MainActivity.this,PollActivity.class);
        startActivity(pollIntent);
        finish();
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