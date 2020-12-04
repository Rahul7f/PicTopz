package com.example.pictopz.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pictopz.R;

import java.util.ArrayList;

import xute.storyview.StoryModel;
import xute.storyview.StoryView;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.MyViewHolder> {


    @NonNull
    @Override
    public StoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.story_layout,parent,false);
        StoryAdapter.MyViewHolder vh=new StoryAdapter.MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull StoryAdapter.MyViewHolder holder, int position) {
        //holder.ranking.setText();

        holder.storyView.resetStoryVisits();
        ArrayList<StoryModel> uris = new ArrayList<>();
        uris.add(new StoryModel("https://firebasestorage.googleapis.com/v0/b/pictopz-fd4d4.appspot.com/o/images%2F7dd503f0-6627-43de-aaaa-db18c7b23f9b?alt=media&token=dd75e6db-6bac-452a-8c0e-dd526342134e"
                ,"rahul","Yesterday"));
        uris.add(new StoryModel("https://firebasestorage.googleapis.com/v0/b/pictopz-fd4d4.appspot.com/o/images%2Ff5000629-263a-4b78-b36a-00e0d3aac799?alt=media&token=e4e863fa-951c-4970-bfb0-7d6e4a66f519"
                ,"aadrsh","10:15 PM"));
        holder.storyView.setImageUris(uris);
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        StoryView storyView;

        public MyViewHolder(View itemview){
            super(itemview);
            storyView=(StoryView) itemview.findViewById(R.id.storyView);

        }
    }
}
