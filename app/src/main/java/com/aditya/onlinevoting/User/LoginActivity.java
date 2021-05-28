package com.aditya.onlinevoting.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.aditya.onlinevoting.R;
import com.aditya.onlinevoting.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class LoginActivity extends AppCompatActivity {

    private EditText emailText,passwordText;
    private CircularProgressButton loginBtn;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        emailText = (EditText) findViewById(R.id.editTextEmail);
        passwordText = (EditText) findViewById(R.id.editTextPassword);
        loginBtn = (CircularProgressButton) findViewById(R.id.cirLoginButton);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailText.getText().toString();
                String password = passwordText.getText().toString();

                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
                    mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(LoginActivity.this, "SuccessFully Logged In...", Toast.LENGTH_SHORT).show();
                                sendToUserPoll();
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(LoginActivity.this, "Please Enter the Blank Field...", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void sendToUserPoll() {
        Intent userPollIntent = new Intent(LoginActivity.this, UserPollActivity.class);
        startActivity(userPollIntent);
        finish();
    }

    private void sendToRegister(){
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);
        finish();
    }


    public void onRegisterClick(View view) {
        sendToRegister();
    }
}