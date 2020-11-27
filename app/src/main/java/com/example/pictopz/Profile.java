package com.example.pictopz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.pictopz.adapters.ProfileGrideAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile extends AppCompatActivity {

    GridView simpleGrid;
    ImageView gridChange,logout,profile_image;
    FirebaseAuth mAuth;
    FirebaseUser user;
    TextView name_tv,email_tv,phone_tv;
    DatabaseReference ref;

    boolean i = true;
    int logos[] = {R.drawable.sample_image, R.drawable.sample_image, R.drawable.sample_image, R.drawable.sample_image,
            R.drawable.sample_image, R.drawable.sample_image, R.drawable.sample_image, R.drawable.sample_image, R.drawable.sample_image,
            R.drawable.sample_image, R.drawable.sample_image, R.drawable.sample_image, R.drawable.sample_image};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //hooks

        user = FirebaseAuth.getInstance().getCurrentUser();
        gridChange = findViewById(R.id.grid_to_l);
        mAuth = FirebaseAuth.getInstance();
        simpleGrid = (GridView) findViewById(R.id.simpleGridView);
        logout = findViewById(R.id.logout);

        name_tv = findViewById(R.id.name_tv);
        email_tv = findViewById(R.id.email_tv);
        phone_tv = findViewById(R.id.mobile_tv);
        profile_image = findViewById(R.id.profile_image_view);
        ref = FirebaseDatabase.getInstance().getReference().child("user").child(mAuth.getUid());
        //hooks end

        getUserData();

        ProfileGrideAdapter customAdapter = new ProfileGrideAdapter(getApplicationContext(), logos);
        simpleGrid.setAdapter(customAdapter);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        gridChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               if (i==true)
               {
                   simpleGrid.setNumColumns(1);
                   i=false;
               }
               else {
                   simpleGrid.setNumColumns(3);
                   i = true;
               }

            }
        });

        simpleGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }


    void getUserData()
    {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Glide.with(this).load(user.getPhotoUrl()).into(profile_image);
        name_tv.setText(user.getDisplayName());
        email_tv.setText(user.getEmail());
        phone_tv.setText(user.getPhoneNumber());
    }




}