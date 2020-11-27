package com.example.pictopz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.example.pictopz.adapters.UpcomingContestsAdapter;
import com.example.pictopz.adapters.RankAdapter;

public class UpcomingContests extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_contest);

        TextView textView=findViewById(R.id.contest_contest_text);
        textView.setText(Html.fromHtml("UPCOMING <b>CONTEST</b>"));

        RecyclerView recyclerView=findViewById(R.id.contest_recycle_view);
        UpcomingContestsAdapter adapter=new UpcomingContestsAdapter(this);
        recyclerView.setAdapter(adapter);


        RecyclerView recyclerView2=findViewById(R.id.contest_rank_recycle);
        RankAdapter adapter2=new RankAdapter();
        recyclerView2.setAdapter(adapter2);
    }
}