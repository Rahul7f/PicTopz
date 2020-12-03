package com.example.pictopz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.example.pictopz.adapters.FollowersAdapter;
import com.example.pictopz.models.ApprovedPostObject;
import com.example.pictopz.models.FollowersObject;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FollowersActivity extends AppCompatActivity {
    ArrayList<FollowersObject> arrayList = new ArrayList<>();
    RecyclerView recyclerView;
    FollowersAdapter followersAdapter;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);
        mAuth = FirebaseAuth.getInstance();
        recyclerView = findViewById(R.id.followersList_recycleView);
        followersAdapter = new FollowersAdapter(arrayList,getApplicationContext(),mAuth.getCurrentUser().getUid());
        recyclerView.setAdapter(followersAdapter);


        DatabaseReference dbRef= FirebaseDatabase.getInstance().getReference("/followers/"+mAuth.getCurrentUser().getUid());
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    for (DataSnapshot dataSnapshot:snapshot.getChildren())
                    {
                        FollowersObject followersObject = new FollowersObject();
                        followersObject.followerUserName = dataSnapshot.getValue().toString();
                        followersObject.followerUID = dataSnapshot.getKey();
                        arrayList.add(followersObject);
                       // arrayList.add(dataSnapshot.getKey().toString());
                    }

                    followersAdapter.notifyDataSetChanged();
                    Toast.makeText(FollowersActivity.this, "followers", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(FollowersActivity.this, "No any followers", Toast.LENGTH_SHORT).show();
                }


                // Log.e("likeno", approvedPostObjects.get(2).likeNo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}