package com.aditya.onlinevoting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.aditya.onlinevoting.Adapter.CandidateAdapter;
import com.aditya.onlinevoting.model.Candidate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class AddCandidateActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private RecyclerView recyclerView;
    private CandidateAdapter candidateAdapter;

    private FirebaseAuth mAuth;
    private DatabaseReference mRef,userRef;

    private String pollName;

    private List<Candidate> candidateList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_candidate);

        getSupportActionBar().setTitle("Add Candidate");

        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();

        pollName = getIntent().getExtras().getString("pollname");
        Log.d("Adi", "onCreate: "+pollName);

        //Log.d("Adi", "onCreate: "+candidateList.get(0).getName());

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewCandidateId);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        candidateAdapter = new CandidateAdapter(candidateList,AddCandidateActivity.this);
        recyclerView.setAdapter(candidateAdapter);

        fab = (FloatingActionButton) findViewById(R.id.candidateFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createCandidate();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        mRef.child("Candidate").child(pollName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()){
                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                        String id = dataSnapshot.getKey();
                        Candidate c = dataSnapshot.child(id).getValue(Candidate.class);
                        Log.d("Adi", "onDataChange: "+c.getName());
                        candidateList.add(c);
                        candidateAdapter.notifyDataSetChanged();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        }

    private List<Candidate> getAllCandidate(){
       List<Candidate> list = new ArrayList<>();

       mRef.child("Candidate").child(pollName).addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               if(snapshot.hasChildren()){
                   for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                       String id = dataSnapshot.getKey();
                       Candidate c = dataSnapshot.child(id).getValue(Candidate.class);
                       Log.d("Adi", "onDataChange: "+c.getName());
                       list.add(c);
                   }
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });

       return list;
    }

    private void createCandidate() {
        builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.create_candidate_layout,null);

        EditText candidateName = (EditText) view.findViewById(R.id.editTextCandidateName);
        EditText candidateDescp = (EditText) view.findViewById(R.id.editTextCandidateDetail);
        CircularProgressButton saveBtn = (CircularProgressButton) view.findViewById(R.id.cirCandidateCreateBtn);

        builder.setView(view);
        dialog = builder.create();
        dialog.show();

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Adi", "saveToDb: "+"Ok");
                Toast.makeText(AddCandidateActivity.this,"save Pressed",Toast.LENGTH_SHORT).show();
                String n = candidateName.getText().toString();
                String i = candidateDescp.getText().toString();
                saveToDb(n,i);
                Log.d("Adi", "saveToDb: "+"Ok");
                dialog.dismiss();
            }
        });
    }

    private void saveToDb(String n, String i) {

        DatabaseReference childUser = mRef.child("Candidate").child(pollName).push();
        String id = childUser.getKey();
        HashMap<String, Object> messageInfoMap = new HashMap<>();
        messageInfoMap.put("name",n);
        messageInfoMap.put("detail",i);
        messageInfoMap.put("userid",id);
        messageInfoMap.put("parent",pollName);
        Log.d("Adi", "saveToDb: "+"Ok");
        childUser.child(id).updateChildren(messageInfoMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //start a new activity
                            Intent intent = new Intent(AddCandidateActivity.this, AddCandidateActivity.class);
                            intent.putExtra("pollname",pollName);
                            startActivity(intent);
                            finish();

                        }
                    }, 1200); //  1 second.*/

                }
                else{
                    String e = task.getException().toString();
                    Log.d("Adi", "onComplete: "+e);

                    Toast.makeText(AddCandidateActivity.this, e, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}