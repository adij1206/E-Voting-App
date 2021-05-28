package com.aditya.onlinevoting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aditya.onlinevoting.Admin.AdminLoginActivity;
import com.aditya.onlinevoting.model.Poll;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class PollActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;

    private FirebaseAuth mAuth;
    private DatabaseReference mRef,userRef,cRef;
    private List<String> pollList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll);

        pollList = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();
        String userId = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Poll").child(userId);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewID);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<Poll> options = new FirebaseRecyclerOptions.Builder<Poll>()
                .setQuery(userRef,Poll.class)
                .build();
        FirebaseRecyclerAdapter<Poll,ListViewHolder> adapter =
                new FirebaseRecyclerAdapter<Poll, ListViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ListViewHolder holder, final int position, @NonNull Poll model) {
                        holder.pollName.setText(model.getName());
                        holder.pollDetail.setText(model.getDetail());
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String k = getRef(position).getKey();
                                sendToAddCandidate(k);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.poll_list_layout,parent,false);
                        ListViewHolder viewHolder = new ListViewHolder(view);
                        return viewHolder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

        fab = (FloatingActionButton) findViewById(R.id.adminFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPoll();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser==null){
            sendToAdminLogin();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id==R.id.signout){

        }

        return super.onOptionsItemSelected(item);
    }


    private void sendToAdminLogin() {
        Intent adminLoginIntent = new Intent(PollActivity.this, AdminLoginActivity.class);
        startActivity(adminLoginIntent);
        finish();
    }

    private void createPoll() {
        builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.create_poll_layout,null);

        EditText pollName = (EditText) view.findViewById(R.id.editTextPollName);
        EditText pollDescp = (EditText) view.findViewById(R.id.editTextPollDetail);
        CircularProgressButton saveBtn = (CircularProgressButton) view.findViewById(R.id.cirPollCreateBtn);

        builder.setView(view);
        dialog = builder.create();
        dialog.show();

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PollActivity.this,"save Pressed",Toast.LENGTH_SHORT).show();
                String n = pollName.getText().toString();
                String i = pollDescp.getText().toString();
                saveToDb(n,i);
                dialog.dismiss();

            }
        });
    }

    private void sendToAddCandidate(String n) {
        Intent addCandidateIntent = new Intent(PollActivity.this,AddCandidateActivity.class);
        addCandidateIntent.putExtra("pollname",n);
        startActivity(addCandidateIntent);
    }

    private void saveToDb(String n, String i) {
        String  uid = mAuth.getCurrentUser().getUid();
        DatabaseReference childUser = mRef.child("Poll").child(uid).push();

        String a = childUser.getKey();

        HashMap<String, Object> messageInfoMap = new HashMap<>();
        messageInfoMap.put("name",n);
        messageInfoMap.put("detail",i);
        messageInfoMap.put("close","open");
        messageInfoMap.put("userid",a);

        childUser.updateChildren(messageInfoMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    sendToAddCandidate(a);
                    //Toast.makeText(getContext(), "Added SuccessFully", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        TextView pollName,pollDetail;
        Button closeBtn;
        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            pollName = itemView.findViewById(R.id.pollName);
            pollDetail = itemView.findViewById(R.id.pollDetail);
            closeBtn = itemView.findViewById(R.id.closeBtn);

        }
    }
}