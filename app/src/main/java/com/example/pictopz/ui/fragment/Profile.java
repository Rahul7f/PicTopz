package com.example.pictopz.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pictopz.R;
import com.example.pictopz.adapters.ProfileContestAdapter;
import com.example.pictopz.adapters.ProfileGridAdapter;
import com.example.pictopz.adapters.WinRecordAdapter;
import com.example.pictopz.models.UserProfileObject;
import com.example.pictopz.ui.activity.EditProfile;
import com.example.pictopz.ui.activity.FollowersActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile extends Fragment {

    public String userUID, userName;
    ImageView gridChange, profile_image, upcoming_contest, winRecordBtn;
    FirebaseAuth mAuth;
    FirebaseUser user;
    TextView name_tv, email_tv, phone_tv, followersCount_textView, followingCount_textView;
    DatabaseReference ref;
    Button edit_profile_btn;
    RecyclerView recyclerView;
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
        recyclerView = root.findViewById(R.id.simpleGridView);

        name_tv = root.findViewById(R.id.name_tv);
        email_tv = root.findViewById(R.id.email_tv);
        phone_tv = root.findViewById(R.id.mobile_tv);
        followersCount_textView = root.findViewById(R.id.followersCount_textView);
        followingCount_textView = root.findViewById(R.id.followingCount_textView);
        profile_image = root.findViewById(R.id.profile_image_view);
        edit_profile_btn = root.findViewById(R.id.edit_profile_btn);
        upcoming_contest = root.findViewById(R.id.showCONTEST);
        winRecordBtn = root.findViewById(R.id.winRecord_btn);

        getUserData();

        checkFollower();

        ProfileGridAdapter customAdapter = new ProfileGridAdapter(getContext(), userUID);
        WinRecordAdapter winRecordAdapter = new WinRecordAdapter(getContext(), userUID);
        ProfileContestAdapter profileContestAdapter = new ProfileContestAdapter(getContext());

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(customAdapter);


        followersCount_textView.setOnClickListener(new View.OnClickListener() {
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
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(profileContestAdapter);
            }
        });

        winRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(winRecordAdapter);
            }
        });

        followingCount_textView.setOnClickListener(new View.OnClickListener() {
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

        gridChange.setOnClickListener(view -> {
            recyclerView.setAdapter(customAdapter);
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        });

        return root;
    }

    void getUserData() {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

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


                if (userProfileObject.username != null) {
                    name_tv.setText(userProfileObject.username);
                    changeActionBar(userProfileObject.username);
                }

                followersCount_textView.setText(userProfileObject.followers + " Followers");
                followingCount_textView.setText(userProfileObject.following + " Following");

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
        TextView view = getActivity().findViewById(R.id.textView4);
        TextView subView = getActivity().findViewById(R.id.subTitle);
        subView.setVisibility(View.GONE);
        ImageView imageView = getActivity().findViewById(R.id.open_drawer);
        imageView.setImageResource(R.drawable.ic_round_plus);
        view.setGravity(Gravity.CENTER);
        view.setText(Html.fromHtml("PROFILE <b>" + name.toUpperCase() + "</b>"));
    }


}