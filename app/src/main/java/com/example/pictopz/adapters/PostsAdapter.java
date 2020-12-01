package com.example.pictopz.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pictopz.R;
import com.example.pictopz.models.ApprovedPostObject;

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
        TextView username;
        ImageView post_image;
        public MyViewHolder(View itemview){
            super(itemview);
            post_image=(ImageView)itemview.findViewById(R.id.layout_home_post_image);
            username=(TextView)itemview.findViewById(R.id.layout_home_username);

        }
    }
}
