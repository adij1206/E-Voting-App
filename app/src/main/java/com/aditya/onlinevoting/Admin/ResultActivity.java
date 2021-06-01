package com.aditya.onlinevoting.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.aditya.onlinevoting.R;
import com.aditya.onlinevoting.model.Candidate;
import com.aditya.onlinevoting.model.Poll;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.chart.common.listener.Event;
import com.anychart.chart.common.listener.ListenersInterface;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private AnyChartView pieChart;
    String pollName,pollId;
    Map<String,Long> map;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        getSupportActionBar().setTitle("Result Details");

        pollName = getIntent().getExtras().getString("pollname");
        pollId = getIntent().getExtras().getString("pollid");

        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference().child("Candidate").child(pollId);
        pieChart = (AnyChartView) findViewById(R.id.piechart);
        map = new HashMap<>();

        String[] result_name = {""};
        long[] result_val = {0};

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()){
                    for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                        String id = dataSnapshot.getKey();
                        Candidate candidate = dataSnapshot.child(id).getValue(Candidate.class);
                        long a =  dataSnapshot.getChildrenCount();
                        Log.d("Adi", "onDataChange: "+ a);
                        map.put(candidate.getName(),a-1);

                        if(a>result_val[0]){
                            result_val[0] = a;
                            result_name[0] = candidate.getName();
                        }
                    }
                    abc();
                    updatePollResult(result_val[0],result_name[0]);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void abc(){
        Log.d("Adi", "onCreate: "+map.size());


        Pie pie = AnyChart.pie();
        pie.setOnClickListener(new ListenersInterface.OnClickListener() {
            @Override
            public void onClick(Event event) {
                Toast.makeText(ResultActivity.this, event.getData().get("x")+":"+event.getData().get("value"), Toast.LENGTH_SHORT).show();
            }
        });

        List<DataEntry> dataEntries = new ArrayList<>();

        for(Map.Entry<String, Long> pair : map.entrySet()){
            dataEntries.add(new ValueDataEntry(pair.getKey(),pair.getValue()));
        }
        pie.data(dataEntries);

        pie.title("Poll Result");
        pie.labels().position("outside");

        pie.legend().title().enabled(true);
        pie.legend().title()
                .text(pollName)
                .padding(0d,0d,10d,0d);

        pie.legend().position("center-bottom")
                    .itemsLayout(LegendLayout.HORIZONTAL)
                    .align(Align.CENTER);

        pieChart.setChart(pie);

    }

    private void updatePollResult(long value,String name){
        String userId = mAuth.getCurrentUser().getUid();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Poll").child(userId);


        reference.child(pollId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                  Poll poll = snapshot.getValue(Poll.class);

                  if(poll.getResult().equals("not_declare")){
                      HashMap<String, Object> messageInfoMap = new HashMap<>();
                      messageInfoMap.put("name",poll.getName());
                      messageInfoMap.put("detail",poll.getDetail());
                      messageInfoMap.put("close",poll.getClose());
                      messageInfoMap.put("userid",poll.getUserid());
                      messageInfoMap.put("result","Winner is "+name + " With "+value+" votes");

                      reference.child(pollId).updateChildren(messageInfoMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                          @Override
                          public void onComplete(@NonNull Task<Void> task) {
                              Toast.makeText(ResultActivity.this, "Result updated", Toast.LENGTH_SHORT).show();
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