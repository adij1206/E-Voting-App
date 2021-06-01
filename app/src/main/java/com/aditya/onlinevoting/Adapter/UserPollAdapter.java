package com.aditya.onlinevoting.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aditya.onlinevoting.R;
import com.aditya.onlinevoting.User.UserVotingActivity;
import com.aditya.onlinevoting.model.Poll;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class UserPollAdapter extends RecyclerView.Adapter<UserPollAdapter.UserViewHolder> {

    private List<Poll> pollList;
    private Context context;

    private DatabaseReference mRef;
    private FirebaseAuth mAuth;

    public UserPollAdapter(List<Poll> pollList,Context context){
        this.pollList = pollList;
        this.context = context;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_poll_list_layout,parent,false);

        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference().child("Vote");

        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        Poll poll = pollList.get(position);
        holder.userPollName.setText(poll.getName());
        holder.userPollDetail.setText(poll.getDetail());

        if(poll.getClose().equals("close") && poll.getResult().equals("not_declare")){
            holder.userPollAction.setText("Poll Has Been Closed,Result Will be announced Soon");
        }
        else if(!poll.getResult().equals("not_declare")){
            holder.userPollAction.setText(poll.getResult());
        }

        String id = poll.getUserid();
        String userId = mAuth.getCurrentUser().getUid();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(poll.getClose().equals("close")){
                    Toast.makeText(context, "Sorry! You Can't Vote Now....", Toast.LENGTH_SHORT).show();
                }else{
                    Query query = mRef.child(id).orderByKey().equalTo(userId);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                Toast.makeText(context, "You Already Had Voted in a Poll...", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Intent intent = new Intent(context, UserVotingActivity.class);
                                intent.putExtra("id",id);
                                context.startActivity(intent);
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

    @Override
    public int getItemCount() {
        return pollList.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userPollName,userPollDetail,userPollAction;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            userPollName = (TextView) itemView.findViewById(R.id.userPollName);
            userPollDetail = (TextView) itemView.findViewById(R.id.userPollDetail);
            userPollAction = (TextView) itemView.findViewById(R.id.userPollAction);
        }
    }
}
