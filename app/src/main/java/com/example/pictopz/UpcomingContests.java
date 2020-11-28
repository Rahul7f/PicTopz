package com.example.pictopz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pictopz.adapters.UpcomingContestsAdapter;
import com.example.pictopz.adapters.RankAdapter;

public class UpcomingContests extends Fragment {


    public View onCreateView(@NonNull LayoutInflater inflater,
                         ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.activity_upcoming_contest, container, false);


        RecyclerView recyclerView=root.findViewById(R.id.contest_recycle_view);
        UpcomingContestsAdapter adapter=new UpcomingContestsAdapter(getContext());
        recyclerView.setAdapter(adapter);


        RecyclerView recyclerView2=root.findViewById(R.id.contest_rank_recycle);
        RankAdapter adapter2=new RankAdapter();
        recyclerView2.setAdapter(adapter2);
        return root;
    }
}