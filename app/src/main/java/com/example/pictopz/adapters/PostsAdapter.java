package com.example.pictopz.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pictopz.R;
import com.example.pictopz.models.ApprovedPostObject;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class PostsAdapter  extends RecyclerView.Adapter<PostsAdapter.MyViewHolder> {
    ArrayList<ApprovedPostObject> approvedPostObjects=new ArrayList<>();
    Context context;
    public PostsAdapter(Context context,ArrayList<ApprovedPostObject> approvedPostObjects) {
        this.context=context;
        this.approvedPostObjects = approvedPostObjects;
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
                .centerCrop()
                .into(holder.post_image);

    }

    @Override
    public int getItemCount() {
        return approvedPostObjects.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView username,likeNo,commentNo;
        ImageView post_image,like_btn,comment_bnt;
        public MyViewHolder(View itemview){
            super(itemview);
            post_image=(ImageView)itemview.findViewById(R.id.layout_home_post_image);
            like_btn=(ImageView)itemview.findViewById(R.id.layout_home_like);
            comment_bnt=(ImageView)itemview.findViewById(R.id.layout_home_comment);
            username=(TextView)itemview.findViewById(R.id.layout_home_username);
            likeNo=(TextView)itemview.findViewById(R.id.likeNo);
            commentNo=(TextView)itemview.findViewById(R.id.commentNo);

        }
    }
}
