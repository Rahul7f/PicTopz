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
import com.example.pictopz.models.ContestObject;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UpcomingContests extends Fragment {

    ArrayList<ContestObject> arrayList=new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                         ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.activity_upcoming_contest, container, false);

        final RecyclerView recyclerView=root.findViewById(R.id.contest_recycle_view);
        final UpcomingContestsAdapter adapter=new UpcomingContestsAdapter(getContext(),arrayList);
        recyclerView.setAdapter(adapter);


        DatabaseReference dbRef= FirebaseDatabase.getInstance().getReference("/contests/");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    arrayList.add(snapshot1.getValue(ContestObject.class));
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        RecyclerView recyclerView2=root.findViewById(R.id.contest_rank_recycle);
        RankAdapter adapter2=new RankAdapter();
        recyclerView2.setAdapter(adapter2);



        return root;
    }
}