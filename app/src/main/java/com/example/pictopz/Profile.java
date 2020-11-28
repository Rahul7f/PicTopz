package com.example.pictopz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.pictopz.adapters.ProfileGridAdapter;
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
    Button edit_profile_btn;

    boolean i = true;
    int logos[] = {R.drawable.sample_image, R.drawable.sample_image, R.drawable.sample_image, R.drawable.sample_image,
            R.drawable.sample_image, R.drawable.sample_image, R.drawable.sample_image, R.drawable.sample_image, R.drawable.sample_image,
            R.drawable.sample_image, R.drawable.sample_image, R.drawable.sample_image, R.drawable.sample_image};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //hook
        user = FirebaseAuth.getInstance().getCurrentUser();
        gridChange = findViewById(R.id.grid_to_l);
        mAuth = FirebaseAuth.getInstance();
        simpleGrid = (GridView) findViewById(R.id.simpleGridView);
        logout = findViewById(R.id.logout);

        name_tv = findViewById(R.id.name_tv);
        email_tv = findViewById(R.id.email_tv);
        phone_tv = findViewById(R.id.mobile_tv);
        profile_image = findViewById(R.id.profile_image_view);
        edit_profile_btn = findViewById(R.id.edit_profile_btn);
        ref = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getUid());
        //hooks end

        getUserData();

        ProfileGridAdapter customAdapter = new ProfileGridAdapter(getApplicationContext(), logos);
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
        edit_profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EditProfile.class);
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

                String name,email,phone;
                Uri image_url;
                if (snapshot.exists())
                {

                    // for name
                  if (snapshot.child("username").exists())
                  {
                     name_tv.setText(snapshot.child("username").getValue().toString());
                  }
                  else
                  {
                      Toast.makeText(Profile.this, "please update your name", Toast.LENGTH_SHORT).show();
                  }

                    // for email
                  if (snapshot.child("email").exists())
                  {
                      email_tv.setText(snapshot.child("email").getValue().toString());
                  }
                  else
                  {
                      Toast.makeText(Profile.this, "please update your name", Toast.LENGTH_SHORT).show();
                  }


                    // for phone
                  if (snapshot.child("phone").exists())
                  {
                      phone_tv.setText(snapshot.child("phone").getValue().toString());
                  }
                  else
                  {
                      Toast.makeText(Profile.this, "please update your phone number", Toast.LENGTH_SHORT).show();
                  }


                    // for profile
                  if (snapshot.child("userProfile").exists())
                  {
                      Glide.with(Profile.this).load(snapshot.child("userProfile").getValue()).into(profile_image);
                  }
                  else
                  {
                      Toast.makeText(Profile.this, "please update your profile image", Toast.LENGTH_SHORT).show();
                  }


                }
                else {
                    Toast.makeText(Profile.this, "please update your profile", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Profile.this, "something went wrong", Toast.LENGTH_SHORT).show();

            }
        });

//        Glide.with(this).load(user.getPhotoUrl()).into(profile_image);
//        name_tv.setText(user.getDisplayName());
//        email_tv.setText(user.getEmail());
//        phone_tv.setText(user.getPhoneNumber());
    }




}