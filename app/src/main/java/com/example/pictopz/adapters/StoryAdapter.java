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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import xute.storyview.StoryModel;
import xute.storyview.StoryView;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.MyViewHolder> {

    HashMap<String,ArrayList> storyObjects;
    ArrayList<String> keyset;
    HashMap<String,String> usernames;
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
        FirebaseDatabase.getInstance().getReference("users").child(keyset.get(position)).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<StoryModel> models=storyObjects.get(keyset.get(position));
                for(StoryModel model:models) {
                    model.name = snapshot.getValue(String.class);
                    GregorianCalendar cal=new GregorianCalendar();
                    cal.setTimeInMillis(Long.parseLong(model.time));
                    model.time= String.format(Locale.getDefault(),"%02d:%02d:%02d",cal.get(GregorianCalendar.HOUR),cal.get(GregorianCalendar.MINUTE),cal.get(GregorianCalendar.SECOND));
                }
                holder.storyView.setImageUris(models);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
