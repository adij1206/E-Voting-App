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
import com.aditya.onlinevoting.model.Candidate;

import java.util.List;

public class CandidateAdapter extends RecyclerView.Adapter<CandidateAdapter.CandidateViewHolder> {

    private List<Candidate> candidateList;
    private Context context;

    public CandidateAdapter(List<Candidate> candidateList,Context context){
        this.candidateList = candidateList;
        this.context = context;
    }

    @NonNull
    @Override
    public CandidateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_layout,null);

        return new CandidateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CandidateViewHolder holder, int position) {
        holder.candidateName.setText(candidateList.get(position).getName());
        holder.candidateDetail.setText(candidateList.get(position).getDetail());

    }

    @Override
    public int getItemCount() {
        return candidateList.size();
    }

    public class CandidateViewHolder extends RecyclerView.ViewHolder {

        private TextView candidateName,candidateDetail;
        private Button deleteButton;

        public CandidateViewHolder(@NonNull View itemView) {
            super(itemView);

            candidateName = itemView.findViewById(R.id.cdname);
            candidateDetail = itemView.findViewById(R.id.cddetail);
            deleteButton = itemView.findViewById(R.id.cddelete);
        }
    }
}
