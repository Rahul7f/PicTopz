package com.example.pictopz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
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

public class Profile extends AppCompatActivity {

    GridView simpleGrid;
    ImageView gridChange,profile_image;
    TextView logout;
    FirebaseAuth mAuth;
    FirebaseUser user;
    TextView name_tv,email_tv,phone_tv,followersCount_textView,followingCount_textView;
    LinearLayout followersCount,followingCount;
    DatabaseReference ref;
    Button edit_profile_btn;
    ArrayList<UserProfileObject> userProfileObjects;
    String userUID,userName;
    String status;
    ImageView add_story_icon;
    String myusername;

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
        add_story_icon=findViewById(R.id.add_story_icon);

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

        add_story_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkpermission();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
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
        //get name
        DatabaseReference nameref = FirebaseDatabase.getInstance().getReference("/users/"+mAuth.getUid()).child("username");
        nameref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    myusername= snapshot.getValue().toString();
                }
                else {
                    Toast.makeText(Profile.this, "data not found", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //String myusername = getUsername(mAuth.getCurrentUser().getDisplayName());

        DatabaseReference referenceto = FirebaseDatabase.getInstance().getReference("/followers/"+userUID).child(mAuth.getCurrentUser().getUid());
        referenceto.setValue(myusername).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    Toast.makeText(Profile.this, "data uploaded to followers", Toast.LENGTH_SHORT).show();
                }
            }
        });

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



    //code related to uploading image on story

    private void addStory(Uri uri){
        String uploadPath="story/"+ UUID.randomUUID().toString();
        FirebaseUploadImage uploadImage=new FirebaseUploadImage(Profile.this,uri,uploadPath) {
            @Override
            public void getUrl(String url) {
                addDataOfStory(url);
            }
        };
        uploadImage.start();
    }

    private void addDataOfStory(String imageURL){
        String storyID=UUID.randomUUID().toString();
        String dataURL="story/"+mAuth.getUid()+"/"+storyID;
        GregorianCalendar calendar= new GregorianCalendar();

        StoryObject storyObject=new StoryObject(imageURL,storyID,calendar.getTimeInMillis(),mAuth.getUid());
        FirebaseUploadData<StoryObject> uploadData=new FirebaseUploadData<StoryObject>(Profile.this,dataURL,storyObject) {
            @Override
            public void onSuccessfulUpload() {
                Toast.makeText(Profile.this, "Story Uploaded Successfully", Toast.LENGTH_SHORT).show();
            }
        };
        uploadData.start();
    }

    void  checkpermission()
    {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        {
            try {
                requestPermissions(new  String[] {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA},100);

            }catch (Exception ignored)
            {

            }
        }
        else {
            pickimage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode==100 && grantResults[0]== PackageManager.PERMISSION_GRANTED)
        {
            pickimage();
        }
        else
        {
            Toast.makeText(this, "This Permission is required", Toast.LENGTH_SHORT).show();
        }
    }

    public void pickimage()
    {
        CropImage.startPickImageActivity(Profile.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);
            croprequest(imageUri);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode==RESULT_OK)
            {
                try {
//                    filePath=result.getUri();
                    addStory(result.getUri());
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),result.getUri());
                    Toast.makeText(this, "image uri:- "+bitmap, Toast.LENGTH_SHORT).show();
//                    imageView.setImageBitmap(bitmap);
                }catch (IOException e)
                {
                    e.printStackTrace();
                    Toast.makeText(this, "error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void croprequest(Uri imageURL)
    {
        CropImage.activity(imageURL)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
    }


}