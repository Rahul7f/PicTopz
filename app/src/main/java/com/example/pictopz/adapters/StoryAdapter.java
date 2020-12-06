package com.example.pictopz.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pictopz.R;
import com.example.pictopz.models.StoryObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import xute.storyview.StoryModel;
import xute.storyview.StoryView;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.MyViewHolder> {

    HashMap<String,ArrayList> storyObjects;
    ArrayList<String> keyset;
    public StoryAdapter(HashMap<String,ArrayList> storyObjects,ArrayList<String> keyset) {

        this.storyObjects=storyObjects;
        this.keyset=keyset;



    }

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
        holder.storyView.setImageUris(storyObjects.get(keyset.get(position)));
    }

    @Override
    public int getItemCount() {
        return keyset.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        StoryView storyView;

        public MyViewHolder(View itemview){
            super(itemview);
            storyView=(StoryView) itemview.findViewById(R.id.storyView);

        }
    }

}
