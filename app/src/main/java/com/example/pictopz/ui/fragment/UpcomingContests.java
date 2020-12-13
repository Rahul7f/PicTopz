package com.example.pictopz.ui.fragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pictopz.R;
import com.example.pictopz.adapters.UpcomingContestsAdapter;
import com.example.pictopz.adapters.RankAdapter;
import com.example.pictopz.models.ContestObject;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.Document;

import java.util.ArrayList;

public class UpcomingContests extends Fragment {

    public static ArrayList<ContestObject> arrayList=new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                         ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.activity_upcoming_contest, container, false);

        RecyclerView recyclerView=root.findViewById(R.id.contest_recycle_view);
        UpcomingContestsAdapter adapter=new UpcomingContestsAdapter(getContext(),arrayList,getActivity());
        recyclerView.setAdapter(adapter);

        FirebaseFirestore dbRef= FirebaseFirestore.getInstance();
        dbRef.collection("contests").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    arrayList.clear();
                    for (DocumentSnapshot snapshots:task.getResult()){
                        Log.i("FIRESTONE LOG","OBJ : "+snapshots.toObject(ContestObject.class));
                        arrayList.add(snapshots.toObject(ContestObject.class));
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });


        RecyclerView recyclerView2=root.findViewById(R.id.contest_rank_recycle);
        RankAdapter adapter2=new RankAdapter();
        recyclerView2.setAdapter(adapter2);



        return root;
    }
}