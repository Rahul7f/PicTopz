package com.example.pictopz.ui.fragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pictopz.R;
import com.example.pictopz.adapters.UpcomingContestsAdapter;
import com.example.pictopz.adapters.RankAdapter;
import com.example.pictopz.models.ContestObject;
import com.example.pictopz.models.GiftObject;
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
    public static ArrayList<GiftObject> giftsList=new ArrayList<>();
    RankAdapter adapter2;

    public View onCreateView(@NonNull LayoutInflater inflater,
                         ViewGroup container, Bundle savedInstanceState) {

        changeActionBar();

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
                    adapter2.notifyDataSetChanged();
                }
            }
        });


        RecyclerView recyclerView2=root.findViewById(R.id.contest_rank_recycle);
        fetchGiftItem();
        adapter2=new RankAdapter(giftsList,getContext());
        recyclerView2.setAdapter(adapter2);



        return root;
    }

    // fetch gift item
    void fetchGiftItem()
    {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("/gift/");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    giftsList.clear();
                    for (DataSnapshot snapshot1:snapshot.getChildren())
                    {
                        giftsList.add(snapshot1.getValue(GiftObject.class));
                    }

                }
                else {
                    Toast.makeText(getContext(), "data not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void changeActionBar(){
        TextView view=getActivity().findViewById(R.id.textView4);
        ImageView imageView =getActivity().findViewById(R.id.open_drawer);
        imageView.setImageResource(R.drawable.menu_icon);
        view.setGravity(Gravity.CENTER);
        view.setText(Html.fromHtml("UPCOMING <b>CONTEST</b>"));
    }
}