package com.example.pictopz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.example.pictopz.adapters.ContestAdapter;

public class ContestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contest);

        TextView textView=findViewById(R.id.contest_contest_text);
        textView.setText(Html.fromHtml("UPCOMING <b>CONTEST</b>"));

        RecyclerView recyclerView=findViewById(R.id.contest_recycle_view);
        ContestAdapter adapter=new ContestAdapter();
        recyclerView.setAdapter(adapter);
    }
}