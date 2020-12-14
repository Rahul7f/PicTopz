package com.example.pictopz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.pictopz.adapters.ProfileGridAdapter;
import com.example.pictopz.authentication.LoginActivity;
import com.example.pictopz.firebase.FirebaseUploadData;
import com.example.pictopz.firebase.FirebaseUploadImage;
import com.example.pictopz.models.StoryObject;
import com.example.pictopz.models.UserProfileObject;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.UUID;

public class Profile extends Fragment {

    RecyclerView simpleGrid;
    ImageView gridChange,profile_image;
    ImageView logout;
    FirebaseAuth mAuth;
    FirebaseUser user;
    TextView name_tv,email_tv,phone_tv,followersCount_textView,followingCount_textView;
    LinearLayout followersCount,followingCount;
    DatabaseReference ref;
    Button edit_profile_btn;
    boolean isLinearLayout=false;
    String userUID,userName;
    String status;

    String myusername;

    public Profile(String userID,String userName) {
        this.userUID = userID;
        this.userName = userName;
        user = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference().child("users").child(userUID);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.activity_profile,container,false);

        gridChange = root.findViewById(R.id.grid_to_l);
        simpleGrid = (RecyclerView) root.findViewById(R.id.simpleGridView);
//        logout = root.findViewById(R.id.logout);
        name_tv = root.findViewById(R.id.name_tv);
        email_tv = root.findViewById(R.id.email_tv);
        phone_tv = root.findViewById(R.id.mobile_tv);
        followersCount = root.findViewById(R.id.followersCount);
        followingCount = root.findViewById(R.id.followingCount);
        followersCount_textView = root.findViewById(R.id.followersCount_textView);
        followingCount_textView = root.findViewById(R.id.followingCount_textView);
        profile_image = root.findViewById(R.id.profile_image_view);
        edit_profile_btn = root.findViewById(R.id.edit_profile_btn);

        getUserData();

        checkFollower();

        ProfileGridAdapter customAdapter = new ProfileGridAdapter(getContext(),userUID);
        simpleGrid.setAdapter(customAdapter);

//        logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mAuth.signOut();
//                Intent intent = new Intent(getContext(), LoginActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//            }
//        });

        followersCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("path","/followers/");
                intent.putExtra("userID",userUID);
                startActivity(intent);
            }
        });

        followingCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FollowersActivity.class);
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
            public void onClick(View view) {

                if(isLinearLayout){

                    simpleGrid.setLayoutManager(new GridLayoutManager(getContext(),3));
                    isLinearLayout=false;
                } else{

                    simpleGrid.setLayoutManager(new LinearLayoutManager(getContext()));
                    isLinearLayout=true;
                }
            }
        });

        return root;
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_profile);

        //hook
//        userUID = getIntent().getStringExtra("userID");
//        userName = getIntent().getStringExtra("userName");
//        user = FirebaseAuth.getInstance().getCurrentUser();
//        gridChange = findViewById(R.id.grid_to_l);
//        mAuth = FirebaseAuth.getInstance();
//        simpleGrid = (RecyclerView) findViewById(R.id.simpleGridView);
//        logout = findViewById(R.id.logout);
//        add_story_icon=findViewById(R.id.add_story_icon);
//
//        name_tv = findViewById(R.id.name_tv);
//        email_tv = findViewById(R.id.email_tv);
//        phone_tv = findViewById(R.id.mobile_tv);
//        followersCount = findViewById(R.id.followersCount);
//        followingCount = findViewById(R.id.followingCount);
//        followersCount_textView = findViewById(R.id.followersCount_textView);
//        followingCount_textView = findViewById(R.id.followingCount_textView);
//        profile_image = findViewById(R.id.profile_image_view);
//        edit_profile_btn = findViewById(R.id.edit_profile_btn);
//        ref = FirebaseDatabase.getInstance().getReference().child("users").child(userUID);
        //hooks end

//        getUserData();
//
//        checkFollower();
//
//        ProfileGridAdapter customAdapter = new ProfileGridAdapter(getApplicationContext(),userUID);
//        simpleGrid.setAdapter(customAdapter);
//
//        add_story_icon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                checkpermission();
//            }
//        });
//
//        logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mAuth.signOut();
//                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//            }
//        });
//        followersCount.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), FollowersActivity.class);
//                intent.putExtra("path","/followers/");
//                intent.putExtra("userID",userUID);
//                startActivity(intent);
//            }
//        });
//
//        followingCount.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), FollowersActivity.class);
//                intent.putExtra("path","/following/");
//                intent.putExtra("userID",userUID);
//                startActivity(intent);
//            }
//        });
//
//        edit_profile_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                switch (status)
//                {
//                    case "edit":
//                        editProfile();
//                        break;
//                    case "Followed":
//                        unFollow();
//                        break;
//                    case "Unfollow":
//                        follow();
//                        break;
//                }
//
//            }
//        });
//
//        gridChange.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                    if(isLinearLayout){
//
//                        simpleGrid.setLayoutManager(new GridLayoutManager(Profile.this,3));
//                        isLinearLayout=false;
//                    } else{
//
//                        simpleGrid.setLayoutManager(new LinearLayoutManager(Profile.this));
//                        isLinearLayout=true;
//                    }
//                }
//        });
//    }

    void getUserData()
    {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name,email,phone;
                Uri image_url;
                UserProfileObject userProfileObject = snapshot.getValue(UserProfileObject.class);
                if (mAuth.getUid().equals(userUID))
                {
                    if (userProfileObject.email!=null)
                        email_tv.setText(userProfileObject.email);

                    if (userProfileObject.phone!=null)
                        phone_tv.setText(userProfileObject.phone);
                }
                else
                {
                    email_tv.setVisibility(View.GONE);
                    phone_tv.setVisibility(View.GONE);
                }


                if (userProfileObject.username!=null)
                    name_tv.setText(userProfileObject.username);

                followersCount_textView.setText(String.valueOf(userProfileObject.followers));
                followingCount_textView.setText(String.valueOf(userProfileObject.following));

                if (userProfileObject.profileURL!=null)
                    Glide.with(getContext()).load(userProfileObject.profileURL).into(profile_image);
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();

            }
        });

//        Glide.with(this).load(user.getPhotoUrl()).into(profile_image);
//        name_tv.setText(user.getDisplayName());
//        email_tv.setText(user.getEmail());
//        phone_tv.setText(user.getPhoneNumber());
    }

  void editProfile()
  {
      Intent intent = new Intent(getContext(), EditProfile.class);
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
        reference.setValue(userName).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    Toast.makeText(getContext(), "data uploaded to following", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //get name
        DatabaseReference nameref = FirebaseDatabase.getInstance().getReference("/users/"+mAuth.getUid()).child("username");
        nameref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    myusername= snapshot.getValue().toString();
                    DatabaseReference referenceto = FirebaseDatabase.getInstance().getReference("/followers/"+userUID).child(mAuth.getCurrentUser().getUid());
                    referenceto.setValue(myusername).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                Toast.makeText(getContext(), "data uploaded to followers", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(getContext(), "data not found", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //String myusername = getUsername(mAuth.getCurrentUser().getDisplayName());



    }

    void unFollow()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("/following/"+mAuth.getCurrentUser().getUid()).child(userUID);
        reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    Toast.makeText(getContext(), "delete in followers", Toast.LENGTH_SHORT).show();
                    DatabaseReference referenceto = FirebaseDatabase.getInstance().getReference("/followers/"+userUID).child(mAuth.getCurrentUser().getUid());
                    referenceto.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getContext(), "delete in  followers", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });



    }

    private String getUsername(String str){
        return str.split("/")[1];
    }

    //code related to uploading image on story

//    private void addStory(Uri uri){
//        String uploadPath="story/"+ UUID.randomUUID().toString();
//        FirebaseUploadImage uploadImage=new FirebaseUploadImage(getContext(),uri,uploadPath) {
//            @Override
//            public void getUrl(String url) {
//                addDataOfStory(url);
//            }
//        };
//        uploadImage.start();
//    }
//
//    private void addDataOfStory(String imageURL){
//        String storyID=UUID.randomUUID().toString();
//        String dataURL="story/"+mAuth.getUid()+"/"+storyID;
//        GregorianCalendar calendar= new GregorianCalendar();
//
//        StoryObject storyObject=new StoryObject(imageURL,storyID,calendar.getTimeInMillis(),mAuth.getUid());
//        FirebaseUploadData<StoryObject> uploadData=new FirebaseUploadData<StoryObject>(getContext(),dataURL,storyObject) {
//            @Override
//            public void onSuccessfulUpload() {
//                Toast.makeText(getContext(), "Story Uploaded Successfully", Toast.LENGTH_SHORT).show();
//            }
//        };
//        uploadData.start();
//    }


}