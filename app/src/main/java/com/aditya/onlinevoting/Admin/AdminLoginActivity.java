package com.aditya.onlinevoting.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.aditya.onlinevoting.AdminRegisterActivity;
import com.aditya.onlinevoting.PollActivity;
import com.aditya.onlinevoting.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class AdminLoginActivity extends AppCompatActivity {

    private EditText adminEmail,adminPassword;
    private CircularProgressButton adminLoginBtn;

    private FirebaseAuth mAuth;
    private DatabaseReference mRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();

        adminEmail = (EditText) findViewById(R.id.adminLoginEmail);
        adminPassword = (EditText) findViewById(R.id.adminLoginPassword);
        adminLoginBtn = (CircularProgressButton) findViewById(R.id.adminLoginBtn);

        adminLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginAdmin();
            }
        });
    }

    private void loginAdmin() {
        String email = adminEmail.getText().toString();
        String password = adminPassword.getText().toString();

        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        String uid = mAuth.getCurrentUser().getUid();
                        mRef.child("Admin").child(uid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        sendToPoll();
                                        Toast.makeText(AdminLoginActivity.this, "SuccessFully Logged In...", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        mAuth.signOut();
                                        Toast.makeText(AdminLoginActivity.this, "You Are Not Admin!", Toast.LENGTH_SHORT).show();
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
        else{
            Toast.makeText(AdminLoginActivity.this, "Please Enter the Blank Field...", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendToPoll() {
        Intent pollIntent = new Intent(AdminLoginActivity.this, PollActivity.class);
        startActivity(pollIntent);
        finish();
    }

    public void onRegisterClick(View view) {
        Intent registerIntent = new Intent(AdminLoginActivity.this, AdminRegisterActivity.class);
        startActivity(registerIntent);
        finish();
    }
}