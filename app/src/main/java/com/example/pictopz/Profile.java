package com.example.pictopz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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
import android.text.Html;
import android.view.Gravity;
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
import com.example.pictopz.ui.fragment.ProfileUpcomingContest;
import com.example.pictopz.ui.fragment.WinRecord;
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
    ImageView gridChange, profile_image, upcoming_contest, winRecordBtn;
    ImageView logout;
    FirebaseAuth mAuth;
    FirebaseUser user;
    TextView name_tv, email_tv, phone_tv, followersCount_textView, followingCount_textView;
    LinearLayout followersCount, followingCount;
    DatabaseReference ref;
    Button edit_profile_btn;
    boolean isLinearLayout = false;
    String userUID, userName;
    String status;
    String myusername;

    public Profile(String userID, String userName) {
        this.userUID = userID;
        this.userName = userName;
        user = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference().child("users").child(userUID);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_profile, container, false);

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
        upcoming_contest = root.findViewById(R.id.showCONTEST);
        winRecordBtn = root.findViewById(R.id.winRecord_btn);

        getUserData();

        checkFollower();

        ProfileGridAdapter customAdapter = new ProfileGridAdapter(getContext(), userUID);
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
                intent.putExtra("path", "/followers/");
                intent.putExtra("userID", userUID);
                startActivity(intent);
            }
        });

        upcoming_contest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getActivity()
                        .getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new ProfileUpcomingContest());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        winRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getActivity()
                        .getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new WinRecord(userUID));
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        followingCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("path", "/following/");
                intent.putExtra("userID", userUID);
                startActivity(intent);
            }
        });

        edit_profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (status) {
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

                if (isLinearLayout) {

                    simpleGrid.setLayoutManager(new GridLayoutManager(getContext(), 3));
                    isLinearLayout = false;
                } else {

                    simpleGrid.setLayoutManager(new LinearLayoutManager(getContext()));
                    isLinearLayout = true;
                }
            }
        });

        return root;
    }

    void getUserData() {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name, email, phone;
                Uri image_url;
                UserProfileObject userProfileObject = snapshot.getValue(UserProfileObject.class);
                if (mAuth.getUid().equals(userUID)) {
                    if (userProfileObject.email != null)
                        email_tv.setText(userProfileObject.email);

                    if (userProfileObject.phone != null)
                        phone_tv.setText(userProfileObject.phone);
                } else {
                    email_tv.setVisibility(View.GONE);
                    phone_tv.setVisibility(View.GONE);
                }


                if (userProfileObject.username != null){
                    name_tv.setText(userProfileObject.username);
                    changeActionBar(userProfileObject.username);
                }

                followersCount_textView.setText(String.valueOf(userProfileObject.followers));
                followingCount_textView.setText(String.valueOf(userProfileObject.following));

                if (userProfileObject.profileURL != null)
                    Glide.with(getContext()).load(userProfileObject.profileURL).into(profile_image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();

            }
        });

    }

    void editProfile() {
        Intent intent = new Intent(getContext(), EditProfile.class);
        startActivity(intent);
    }

    void checkFollower() {
        if (userUID.equals(mAuth.getUid())) {
            edit_profile_btn.setText("Edit Profile");
            status = "edit";
        } else {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("/following/" + mAuth.getCurrentUser().getUid()).child(userUID);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        edit_profile_btn.setText("UnFollow");
                        status = "Followed";
                    } else {
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

    void follow() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("/following/" + mAuth.getCurrentUser().getUid()).child(userUID);
        reference.setValue(userName).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "data uploaded to following", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //get name
        DatabaseReference nameref = FirebaseDatabase.getInstance().getReference("/users/" + mAuth.getUid()).child("username");
        nameref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    myusername = snapshot.getValue().toString();
                    DatabaseReference referenceto = FirebaseDatabase.getInstance().getReference("/followers/" + userUID).child(mAuth.getCurrentUser().getUid());
                    referenceto.setValue(myusername).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "data uploaded to followers", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "data not found", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    void unFollow() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("/following/" + mAuth.getCurrentUser().getUid()).child(userUID);
        reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "delete in followers", Toast.LENGTH_SHORT).show();
                    DatabaseReference referenceto = FirebaseDatabase.getInstance().getReference("/followers/" + userUID).child(mAuth.getCurrentUser().getUid());
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

    private void changeActionBar(String name){
        TextView view=getActivity().findViewById(R.id.textView4);
        view.setGravity(Gravity.CENTER);
        view.setText(Html.fromHtml("<b>"+name.toUpperCase()+"</b>"));
    }


}