package com.aditya.onlinevoting.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.contentcapture.DataRemovalRequest;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aditya.onlinevoting.R;
import com.aditya.onlinevoting.User.UserVotingActivity;
import com.aditya.onlinevoting.model.Candidate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class UserCandidateAdapter extends RecyclerView.Adapter<UserCandidateAdapter.UserCandidateViewHolder> {

    private List<Candidate> candidateList;
    private Context context;
    private DatabaseReference mRef,voteRef;
    private FirebaseAuth mAuth;

    public UserCandidateAdapter(List<Candidate> candidateList, Context context){
        this.candidateList =candidateList;
        this.context = context;
    }

    @NonNull
    @Override
    public UserCandidateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_candidate_list_layout,parent,false);

        mRef = FirebaseDatabase.getInstance().getReference().child("Candidate");
        mAuth = FirebaseAuth.getInstance();
        voteRef = FirebaseDatabase.getInstance().getReference().child("Vote");

        return new UserCandidateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserCandidateViewHolder holder, int position) {
            Candidate candidate = candidateList.get(position);
            holder.candidateName.setText(candidate.getName());
            holder.candidateDetail.setText(candidate.getDetail());

            holder.voteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String parent = candidate.getParent();
                    String id = candidate.getUserid();
                    String userId = mAuth.getCurrentUser().getUid();
                    DatabaseReference childUser = mRef.child(parent).child(id);
                    childUser.child(userId).setValue("Done").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Log.d("Adi", "onComplete: "+"Vote Casted");
                                voteRef.child(parent).child(userId).setValue("");
                            }
                        }
                    });
                }
            });
    }

    @Override
    public int getItemCount() {
        return candidateList.size();
    }

    public class UserCandidateViewHolder extends RecyclerView.ViewHolder {

        TextView candidateName,candidateDetail;
        Button voteBtn;

        public UserCandidateViewHolder(@NonNull View itemView) {
            super(itemView);

            candidateName = (TextView) itemView.findViewById(R.id.userCandidateName);
            candidateDetail = (TextView) itemView.findViewById(R.id.userCandidateDetail);
            voteBtn = (Button) itemView.findViewById(R.id.voteBtn);
        }
    }
}
