package com.example.pictopz.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pictopz.R;

public class PostsAdapter  extends RecyclerView.Adapter<PostsAdapter.MyViewHolder> {


    @NonNull
    @Override
    public PostsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_home_post,parent,false);
        PostsAdapter.MyViewHolder vh=new PostsAdapter.MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull PostsAdapter.MyViewHolder holder, int position) {
        //holder.ranking.setText();
    }

    @Override
    public int getItemCount() {
        return 4;
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
