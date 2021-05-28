package com.aditya.onlinevoting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.aditya.onlinevoting.Admin.AdminLoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class AdminRegisterActivity extends AppCompatActivity {

    private EditText adminEmail,adminName,adminPassword;
    private CircularProgressButton adminRegisterBtn;
    private ProgressDialog loadingBar;

    private FirebaseAuth mAuth;
    private DatabaseReference mRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_register);

        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();

        adminEmail = (EditText) findViewById(R.id.adminRegisterEmail);
        adminName = (EditText)findViewById(R.id.adminRegisterName);
        adminPassword = (EditText) findViewById(R.id.adminRegisterPassword);
        adminRegisterBtn = (CircularProgressButton) findViewById(R.id.adminRegisterBtn);
        loadingBar = new ProgressDialog(this);

        adminRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerAdmin();
            }
        });
    }

    private void registerAdmin() {
        int c=0;
        String name = adminName.getText().toString();
        String email = adminEmail.getText().toString();
        String password = adminPassword.getText().toString();

        if(TextUtils.isEmpty(email)){
            c++;
            Toast.makeText(AdminRegisterActivity.this, "Please Enter The Email Id...", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(password)){
            c++;
            Toast.makeText(AdminRegisterActivity.this, "Please Enter The Password...", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(name)){
            c++;
            Toast.makeText(AdminRegisterActivity.this, "Please Enter The Name...", Toast.LENGTH_SHORT).show();
        }

        if(c==0){
            loadingBar.setTitle("Creating New Account");
            loadingBar.setMessage("Please wait while we are creating your account...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        String uid = mAuth.getCurrentUser().getUid();
                        mRef.child("Admin").child(uid).setValue(name);
                        loadingBar.dismiss();
                        sendToPoll();
                        Toast.makeText(AdminRegisterActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        String error = task.getException().toString();
                        Toast.makeText(AdminRegisterActivity.this, "Error :"+error, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });
        }
    }

    private void sendToPoll() {
        Intent pollIntent = new Intent(AdminRegisterActivity.this,PollActivity.class);
        startActivity(pollIntent);
        finish();
    }

    public void onLoginClick(View view) {
        Intent loginIntent = new Intent(AdminRegisterActivity.this, AdminLoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
}