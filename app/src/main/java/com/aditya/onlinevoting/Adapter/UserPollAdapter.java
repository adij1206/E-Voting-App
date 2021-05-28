package com.aditya.onlinevoting.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aditya.onlinevoting.R;
import com.aditya.onlinevoting.User.UserVotingActivity;
import com.aditya.onlinevoting.model.Poll;

import java.util.List;

public class UserPollAdapter extends RecyclerView.Adapter<UserPollAdapter.UserViewHolder> {

    private List<Poll> pollList;
    private Context context;

    public UserPollAdapter(List<Poll> pollList,Context context){
        this.pollList = pollList;
        this.context = context;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_poll_list_layout,parent,false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        Poll poll = pollList.get(position);
        holder.userPollName.setText(poll.getName());
        holder.userPollDetail.setText(poll.getDetail());

        String id = poll.getUserid();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UserVotingActivity.class);
                intent.putExtra("id",id);
                context.startActivity(intent);
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
