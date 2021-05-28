package com.aditya.onlinevoting.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aditya.onlinevoting.R;
import com.aditya.onlinevoting.model.Poll;

import java.util.List;

public class PollAdapter extends RecyclerView.Adapter<PollAdapter.PollViewHolder> {

    List<Poll> pollList;
    Context context;

    public PollAdapter(List<Poll> pollList, Context context){
        this.pollList = pollList;
        this.context = context;
    }

    @NonNull
    @Override
    public PollViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.poll_list_layout,parent,false);


        return new PollViewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull PollViewHolder holder, int position) {


    }

    @Override
    public int getItemCount() {
        return pollList.size();
    }

    public class PollViewHolder extends RecyclerView.ViewHolder {
        TextView pollName,pollDetail;
        Button closeBtn;

        public PollViewHolder(@NonNull View itemView,Context ctx) {
            super(itemView);
            ctx = context;

            pollName = itemView.findViewById(R.id.pollName);
            pollDetail = itemView.findViewById(R.id.pollDetail);
            closeBtn = itemView.findViewById(R.id.closeBtn);

        }
    }
}
