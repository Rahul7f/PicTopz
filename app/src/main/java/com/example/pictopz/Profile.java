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
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.pictopz.adapters.ProfileGridAdapter;
import com.example.pictopz.models.UserProfileObject;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Profile extends AppCompatActivity {

    GridView simpleGrid;
    ImageView gridChange,logout,profile_image;
    FirebaseAuth mAuth;
    FirebaseUser user;
    TextView name_tv,email_tv,phone_tv,followersCount_textView,followingCount_textView;
    LinearLayout followersCount,followingCount;
    DatabaseReference ref;
    Button edit_profile_btn;
    ArrayList<UserProfileObject> userProfileObjects;
    String userUID,userName;
    String status;

    boolean i = true;
    int logos[] = {R.drawable.sample_image, R.drawable.sample_image, R.drawable.sample_image, R.drawable.sample_image,
            R.drawable.sample_image, R.drawable.sample_image, R.drawable.sample_image, R.drawable.sample_image, R.drawable.sample_image,
            R.drawable.sample_image, R.drawable.sample_image, R.drawable.sample_image, R.drawable.sample_image};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //hook
        userUID = getIntent().getStringExtra("userID");
        userName = getIntent().getStringExtra("userName");
        user = FirebaseAuth.getInstance().getCurrentUser();
        gridChange = findViewById(R.id.grid_to_l);
        mAuth = FirebaseAuth.getInstance();
        simpleGrid = (GridView) findViewById(R.id.simpleGridView);
        logout = findViewById(R.id.logout);

        name_tv = findViewById(R.id.name_tv);
        email_tv = findViewById(R.id.email_tv);
        phone_tv = findViewById(R.id.mobile_tv);
        followersCount = findViewById(R.id.followersCount);
        followingCount = findViewById(R.id.followingCount);
        followersCount_textView = findViewById(R.id.followersCount_textView);
        followingCount_textView = findViewById(R.id.followingCount_textView);
        profile_image = findViewById(R.id.profile_image_view);
        edit_profile_btn = findViewById(R.id.edit_profile_btn);
        ref = FirebaseDatabase.getInstance().getReference().child("users").child(userUID);
        //hooks end

        getUserData();

        checkFollower();

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
        followersCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FollowersActivity.class);
                intent.putExtra("path","/followers/");
                intent.putExtra("userID",userUID);
                startActivity(intent);
            }
        });

        followingCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FollowersActivity.class);
                intent.putExtra("path","/following/");
                intent.putExtra("userID",userUID);
                startActivity(intent);
            }
        });

        edit_profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (status)
                {
                    case "edit":
                        editProfile();
                        break;
                    case "Followed":
                        unFollow();
                        break;
                    case "Unfollow":
                        follow();
                        break;
                }

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
                UserProfileObject userProfileObject = snapshot.getValue(UserProfileObject.class);
                if (userProfileObject.username!=null)
                    name_tv.setText(userProfileObject.username);

                if (userProfileObject.email!=null)
                    email_tv.setText(userProfileObject.email);

                if (userProfileObject.phone!=null)
                    phone_tv.setText(userProfileObject.phone);

                followersCount_textView.setText(String.valueOf(userProfileObject.followers));
                followingCount_textView.setText(String.valueOf(userProfileObject.following));

                if (userProfileObject.profileURL!=null)
                    Glide.with(getApplicationContext()).load(userProfileObject.profileURL).into(profile_image);
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

  void editProfile()
  {
      Intent intent = new Intent(getApplicationContext(), EditProfile.class);
      startActivity(intent);
  }

    void checkFollower()
    {
        if (userUID.equals(mAuth.getUid()))
        {
            edit_profile_btn.setText("Edit Profile");
            status = "edit";
        }
        else
        {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("/following/"+mAuth.getCurrentUser().getUid()).child(userUID);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists())
                    {
                        edit_profile_btn.setText("UnFollow");
                        status = "Followed";
                    }
                    else {
                        edit_profile_btn.setText("Follow");
                        status = "Unfollow";
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }


    }

    void  follow()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("/following/"+mAuth.getCurrentUser().getUid()).child(userUID);
        reference.setValue(userName);
        String myusername = getUsername(mAuth.getCurrentUser().getDisplayName());

        DatabaseReference referenceto = FirebaseDatabase.getInstance().getReference("/followers/"+userUID).child(mAuth.getCurrentUser().getUid());
        referenceto.setValue(myusername);

    }

    void unFollow()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("/following/"+mAuth.getCurrentUser().getUid()).child(userUID);
        reference.removeValue();

        DatabaseReference referenceto = FirebaseDatabase.getInstance().getReference("/followers/"+userUID).child(mAuth.getCurrentUser().getUid());
        referenceto.removeValue();

    }

    private String getUsername(String str){
        return str.split("/")[1];
    }


}