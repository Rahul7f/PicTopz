package com.example.pictopz.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
import com.example.pictopz.CustomSharedPrefs;
import com.example.pictopz.Profile;
import com.example.pictopz.R;
import com.example.pictopz.firebase.FirebaseUploadData;
import com.example.pictopz.models.ApprovedPostObject;
import com.example.pictopz.ui.fragment.CommentSectionFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PostsAdapter  extends RecyclerView.Adapter<PostsAdapter.MyViewHolder> {
    ArrayList<ApprovedPostObject> approvedPostObjects=new ArrayList<>();
    CustomSharedPrefs sharedPrefs;
    FirebaseAuth mAuth;
    Context context;
    public PostsAdapter(Context context,ArrayList<ApprovedPostObject> approvedPostObjects) {
        this.context=context;
        this.approvedPostObjects = approvedPostObjects;
        sharedPrefs=new CustomSharedPrefs(context);
        mAuth=FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public PostsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_home_post,parent,false);
        PostsAdapter.MyViewHolder vh=new PostsAdapter.MyViewHolder(v);
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
//                .centerCrop()
                .into(holder.post_image);

        FirebaseDatabase.getInstance().getReference("/likes/"+approvedPostObjects.get(position).dataID+"/"+mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    sharedPrefs.setPrefBool(approvedPostObjects.get(position).dataID,true);
                    holder.like_btn.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(),R.drawable.heart,null));
                }else {
                    sharedPrefs.setPrefBool(approvedPostObjects.get(position).dataID,false);
                    holder.like_btn.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(),R.drawable.heart2,null));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.like_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBitmap(approvedPostObjects.get(position).dataID,holder,position);
            }
        });


        holder.comment_bnt.setClickable(true);
        holder.comment_bnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment=new CommentSectionFragment(approvedPostObjects.get(position).dataID);

                FragmentActivity activity=(FragmentActivity) context;
                FragmentManager fragmentManager=activity.getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container,fragment)
                        .addToBackStack("HOME")
                        .commit();
            }
        });
        DatabaseReference profile_ref = FirebaseDatabase.getInstance().getReference("/users/"+approvedPostObjects.get(position).userUID).child("profileURL");
        profile_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    Glide.with(context).load(snapshot.getValue()).into(holder.gotoProfile);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.gotoProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent =  new Intent(context, Profile.class);
                intent.putExtra("userID",approvedPostObjects.get(position).userUID);
                intent.putExtra("userName",approvedPostObjects.get(position).userName);
                context.startActivity(intent);


            }
        });

    }

    @Override
    public int getItemCount() {
        return approvedPostObjects.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView username,likeNo,commentNo;
        ImageView post_image,like_btn,comment_bnt,gotoProfile;
        public MyViewHolder(View itemview){
            super(itemview);
            post_image=(ImageView)itemview.findViewById(R.id.layout_home_post_image);
            like_btn=(ImageView)itemview.findViewById(R.id.layout_home_like);
            like_btn.setClickable(true);
            comment_bnt=(ImageView)itemview.findViewById(R.id.layout_home_comment);
            username=(TextView)itemview.findViewById(R.id.layout_home_username);
            likeNo=(TextView)itemview.findViewById(R.id.likeNo);
            commentNo=(TextView)itemview.findViewById(R.id.commentNo);
            gotoProfile=(ImageView) itemview.findViewById(R.id.layout_home_more);

        }
    }

    private void setBitmap(String key,MyViewHolder holder,int pos){
        if(!sharedPrefs.getPrefBool(key)){
            //picture not liked
            //so liking it here
            likeOrUnlikeFunction(context,key,true);
            sharedPrefs.setPrefBool(key,true);
            holder.like_btn.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(),R.drawable.heart,null));
            holder.likeNo.setText(String.valueOf(approvedPostObjects.get(pos).likesNo+=1));
        }else{
            //picture is already liked
            //so unliking it here
            likeOrUnlikeFunction(context,key,false);
            sharedPrefs.setPrefBool(key,false);
            holder.like_btn.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(),R.drawable.heart2,null));
            holder.likeNo.setText(String.valueOf(approvedPostObjects.get(pos).likesNo-=1));
        }
    }

    private void likeOrUnlikeFunction(Context context,String postID,boolean like){

        FirebaseUser user=mAuth.getCurrentUser();
        if(like){
            FirebaseUploadData<String> uploadData=new FirebaseUploadData<String>(context,"/likes/"+postID+"/"+user.getUid(),user.getDisplayName()) {
                @Override
                public void onSuccessfulUpload() {
                    Log.e("Like","Response Like");
                }
            };
            uploadData.start();
        }else{
            FirebaseDatabase.getInstance().getReference("/likes/"+postID+"/"+user.getUid()).removeValue();
            Log.e("Like","Response Unlike");
        }
    }



}
