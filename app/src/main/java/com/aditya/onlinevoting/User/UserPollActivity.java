package com.aditya.onlinevoting.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.aditya.onlinevoting.Adapter.UserPollAdapter;
import com.aditya.onlinevoting.R;
import com.aditya.onlinevoting.model.Poll;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserPollActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private DatabaseReference mRef;

    private RecyclerView recyclerView;
    private UserPollAdapter userPollAdapter;

    private final List<Poll> pollList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_poll);


        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference().child("Poll");

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewUserPollId);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(UserPollActivity.this));

        userPollAdapter = new UserPollAdapter(pollList,UserPollActivity.this);
        recyclerView.setAdapter(userPollAdapter);

    }
    

    @Override
    protected void onStart() {
        super.onStart();

        pollList.clear();
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()){
                    for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                        String id = dataSnapshot.getKey();
                        Log.d("Adi1", "onDataChange: "+id);
                        DatabaseReference reference = mRef.child(id);
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                if(snapshot.hasChildren()){
                                    for(DataSnapshot dataSnapshot1:snapshot1.getChildren()){
                                        Poll poll = dataSnapshot1.getValue(Poll.class);
                                        Log.d("Adi", "onDataChange: "+ poll.getName());
                                        pollList.add(poll);
                                        userPollAdapter.notifyDataSetChanged();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}