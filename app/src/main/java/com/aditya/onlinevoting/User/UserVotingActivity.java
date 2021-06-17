package com.aditya.onlinevoting.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.aditya.onlinevoting.Adapter.UserCandidateAdapter;
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
    private final List<Candidate> candidateList = new ArrayList<>();

    private RecyclerView recyclerView;
    private UserCandidateAdapter userCandidateAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_voting);

        candidateList.clear();
        String id = getIntent().getExtras().getString("id");
        mRef = FirebaseDatabase.getInstance().getReference().child("Candidate").child(id);

        recyclerView = (RecyclerView) findViewById(R.id.userVotingRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(UserVotingActivity.this));

        userCandidateAdapter = new UserCandidateAdapter(candidateList,UserVotingActivity.this);
        recyclerView.setAdapter(userCandidateAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        candidateList.clear();

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()){
                    candidateList.clear();
                    for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                        String id = dataSnapshot.getKey();
                        Candidate candidate = dataSnapshot.child(id).getValue(Candidate.class);
                        Log.d("Adi", "onDataChange: "+candidate.getName());
                        candidateList.add(candidate);
                        userCandidateAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}