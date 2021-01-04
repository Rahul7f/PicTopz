package com.example.pictopz.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pictopz.R;
import com.example.pictopz.helper.CustomSharedPrefs;
import com.example.pictopz.helper.FirebaseUploadData;
import com.example.pictopz.models.ApprovedPostObject;
import com.example.pictopz.ui.fragment.CommentSectionFragment;
import com.example.pictopz.ui.fragment.Profile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.MyViewHolder> {
    ArrayList<ApprovedPostObject> approvedPostObjects = new ArrayList<>();
    CustomSharedPrefs sharedPrefs;
    FirebaseAuth mAuth;
    Context context;

    public PostsAdapter(Context context, ArrayList<ApprovedPostObject> approvedPostObjects) {
        this.context = context;
        this.approvedPostObjects = approvedPostObjects;
        sharedPrefs = new CustomSharedPrefs(context);
        mAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public PostsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_home_post, parent, false);
        PostsAdapter.MyViewHolder vh = new PostsAdapter.MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull PostsAdapter.MyViewHolder holder, int position) {


        holder.username.setText(approvedPostObjects.get(position).userName);
        holder.likeNo.setText(String.valueOf(approvedPostObjects.get(position).likesNo));
        holder.commentNo.setText(String.valueOf(approvedPostObjects.get(position).commentsNo));

        Glide
                .with(context)
                .load(approvedPostObjects.get(position).imgURL)
                .into(holder.post_image);

        FirebaseDatabase.getInstance().getReference("/likes/" + approvedPostObjects.get(position).dataID + "/" + mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    sharedPrefs.setPrefBool(approvedPostObjects.get(position).dataID, true);
                    holder.like_btn.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.heart, null));
                } else {
                    sharedPrefs.setPrefBool(approvedPostObjects.get(position).dataID, false);
                    holder.like_btn.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.heart2, null));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.like_btn.setOnClickListener(view -> {
            setBitmap(approvedPostObjects.get(position).dataID, holder, position);

        });


        holder.comment_bnt.setClickable(true);
        holder.comment_bnt.setOnClickListener(view -> {
                    Fragment fragment = new CommentSectionFragment(approvedPostObjects.get(position).dataID);
                    FragmentActivity activity = (FragmentActivity) context;
                    FragmentManager fragmentManager = activity.getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, fragment, "COMMENT")
                            .commit();
                }
        );

        DatabaseReference profile_ref = FirebaseDatabase.getInstance().getReference("/users/").child(approvedPostObjects.get(position).userUID).child("profileURL");
        profile_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Glide.with(context)
                            .load(snapshot.getValue())
                            .into(holder.profilePic);
                } else {
                    Glide.with(context)
                            .load(R.drawable.avatar)
                            .into(holder.profilePic);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.profilePic.setOnClickListener(view -> {
            gotoProfile(position);
        });

        holder.username.setOnClickListener(view -> {
            gotoProfile(position);
        });

    }

    @Override
    public int getItemCount() {
        return approvedPostObjects.size();
    }

    private void setBitmap(String key, MyViewHolder holder, int pos) {
        if (!sharedPrefs.getPrefBool(key)) {
            //picture not liked
            //so liking it here
            likeOrUnlikeFunction(context, key, true);
            sharedPrefs.setPrefBool(key, true);
            holder.like_btn.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.heart, null));
            holder.likeNo.setText(String.valueOf(approvedPostObjects.get(pos).likesNo += 1));
        } else {
            //picture is already liked
            //so unliking it here
            likeOrUnlikeFunction(context, key, false);
            sharedPrefs.setPrefBool(key, false);
            holder.like_btn.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.heart2, null));
            holder.likeNo.setText(String.valueOf(approvedPostObjects.get(pos).likesNo -= 1));
        }
    }

    private void likeOrUnlikeFunction(Context context, String postID, boolean like) {

        FirebaseUser user = mAuth.getCurrentUser();
        if (like) {
            FirebaseUploadData<String> uploadData = new FirebaseUploadData<String>(context, "/likes/" + postID + "/" + user.getUid(), user.getDisplayName()) {
                @Override
                public void onSuccessfulUpload() {
//                    Log.e("Like", "Response Like");
                }
            };
            uploadData.start();
        } else {
            FirebaseDatabase.getInstance().getReference("/likes/" + postID + "/" + user.getUid()).removeValue();
//            Log.e("Like", "Response Unlike");
        }
    }

    private void gotoProfile(int position) {
        Fragment fragment = new Profile(approvedPostObjects.get(position).userUID, approvedPostObjects.get(position).userName);
        FragmentActivity activity = (FragmentActivity) context;
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment, context.getString(R.string.other_profile))
                .addToBackStack(null)
                .commit();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView username, likeNo, commentNo;
        ImageView post_image, like_btn, comment_bnt, profilePic;

        public MyViewHolder(View itemview) {
            super(itemview);
            post_image = itemview.findViewById(R.id.layout_home_post_image);
            like_btn = itemview.findViewById(R.id.layout_home_like);
            like_btn.setClickable(true);
            comment_bnt = itemview.findViewById(R.id.layout_home_comment);
            username = itemview.findViewById(R.id.layout_home_username);
            likeNo = itemview.findViewById(R.id.likeNo);
            commentNo = itemview.findViewById(R.id.commentNo);
            profilePic = itemview.findViewById(R.id.layout_home_more);

        }
    }

}
