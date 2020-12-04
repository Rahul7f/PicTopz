package com.example.pictopz.adapters;

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
import java.util.List;

import xute.storyview.StoryModel;
import xute.storyview.StoryView;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.MyViewHolder> {

    ArrayList<StoryModel> uris = new ArrayList<>();
    ArrayList<StoryObject> storyObjects=new ArrayList<>();

    public StoryAdapter(ArrayList<StoryModel> uris) {

        this.uris = uris;
        String username="";
//        ArrayList<ArrayList> models=new ArrayList<>();
//        for(StoryObject story:storyObjects){
//            if(username.equals(story.uploaderUID))
//                uris.add(new StoryModel(story.imageURL,story.uploaderUID,String.valueOf(story.uploadTime)));
//            else {
//                models.add(uris);
//                uris=new ArrayList<>();
//            }
//            username=story.uploaderUID;
//        }
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
        ArrayList<StoryModel> fakeuris=new ArrayList<>();
        fakeuris.add(uris.get(position));
        holder.storyView.setImageUris(fakeuris);
    }

    @Override
    public int getItemCount() {
        return uris.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        StoryView storyView;

        public MyViewHolder(View itemview){
            super(itemview);
            storyView=(StoryView) itemview.findViewById(R.id.storyView);

        }
    }
}
