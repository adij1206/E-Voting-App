package com.aditya.onlinevoting.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.aditya.onlinevoting.R;
import com.aditya.onlinevoting.model.Candidate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserVotingActivity extends AppCompatActivity {

    private DatabaseReference mRef;
    private List<Candidate> candidateList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_voting);

        String id = getIntent().getExtras().getString("id");

        mRef = FirebaseDatabase.getInstance().getReference().child("Candidate").child(id);

        candidateList = new ArrayList<>();

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()){
                    for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                        String id = dataSnapshot.getKey();
                        Candidate candidate = dataSnapshot.child(id).getValue(Candidate.class);
                        Log.d("Adi", "onDataChange: "+candidate.getName());
                        candidateList.add(candidate);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}