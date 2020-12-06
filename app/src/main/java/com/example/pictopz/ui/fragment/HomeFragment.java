package com.example.pictopz.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.pictopz.Profile;
import com.example.pictopz.R;
import com.example.pictopz.adapters.PostsAdapter;
import com.example.pictopz.adapters.StoryAdapter;
import com.example.pictopz.models.ApprovedPostObject;
import com.example.pictopz.models.StoryObject;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;

import xute.storyview.StoryModel;


public class HomeFragment extends Fragment {

    ImageView imageView;
    SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<ApprovedPostObject> approvedPostObjects=new ArrayList<>();
    PostsAdapter postAdapter;
    RecyclerView recyclerView1,recyclerView;
    StoryAdapter storyAdapter;
    FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();

    HashMap<String,ArrayList> storyHashMap=new HashMap<>();
    ArrayList<String> keyset=new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        swipeRefreshLayout = root.findViewById(R.id.refreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadRecycleViewData();
                fetchFollowList();
            }
        });
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
//
//                loadRecycleViewData();

            }
        });


        // story
//        MyStory currentStory = new MyStory(
//                "dummy-link",
//                //simpleDateFormat.parse("20-10-2019 10:00:00"),
//                );

        fetchFollowList();
        recyclerView=root.findViewById(R.id.home_story_recycleview);
        storyAdapter =new StoryAdapter(storyHashMap,keyset);
        recyclerView.setAdapter(storyAdapter);
        // post
        recyclerView1=root.findViewById(R.id.home_layout_post_recycleview);
        postAdapter=new PostsAdapter(getContext(),approvedPostObjects);
        recyclerView1.setAdapter(postAdapter);

        //TODO have to add a scroll listener so that more than 10 items can be displayed

        //fetching posts data form apProved node lol....seriously apProved ?
        DatabaseReference dbRef=FirebaseDatabase.getInstance().getReference("images/apProved");
        dbRef.limitToFirst(10).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                approvedPostObjects.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren())
                approvedPostObjects.add(snapshot1.getValue(ApprovedPostObject.class));
                postAdapter.notifyDataSetChanged();

              // Log.e("likeno", approvedPostObjects.get(2).likeNo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return root;
    }

    private void loadRecycleViewData() {
        swipeRefreshLayout.setRefreshing(true);
        DatabaseReference dbRef=FirebaseDatabase.getInstance().getReference("images/apProved");
        dbRef.limitToFirst(10).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                approvedPostObjects.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren())
                    approvedPostObjects.add(snapshot1.getValue(ApprovedPostObject.class));
                postAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);

                // Log.e("likeno", approvedPostObjects.get(2).likeNo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void fetchFollowList(){

        storyHashMap.clear();
        keyset.clear();

        fetchStories(user.getUid());

        FirebaseDatabase.getInstance().getReference("following/"+user.getUid()).limitToFirst(50).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())

                    for(DataSnapshot users:snapshot.getChildren()){

                    fetchStories(users.getKey());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fetchStories(String otherUserUID){
        FirebaseDatabase.getInstance().getReference("story/"+otherUserUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                    for (DataSnapshot stories:snapshot.getChildren()){
                        StoryObject object=stories.getValue(StoryObject.class);
                        Log.e("Story List",object.storyID);
                        deleteStoryIfTimeout(object,otherUserUID);
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void deleteStoryIfTimeout(StoryObject object,String otherUserUID){
        //Delete Story if timout
        //Add to arraylist if valid

        GregorianCalendar calendar=new GregorianCalendar();
        long currentTime=calendar.getTimeInMillis();
        if(object.uploadTime+28800000<currentTime){
            FirebaseDatabase.getInstance().getReference("/story/"+otherUserUID+"/"+object.storyID).removeValue();
        }else {
            StoryModel model=new StoryModel(object.imageURL,object.uploaderUID,String.valueOf(object.uploadTime));

            if(storyHashMap.containsKey(object.uploaderUID)){
                storyHashMap.get(object.uploaderUID).add(model);
            }else{
                ArrayList<StoryModel> models=new ArrayList<>();
                models.add(model);
                storyHashMap.put(object.uploaderUID,(ArrayList) models);
                keyset.add(object.uploaderUID);
            }
        }
        storyAdapter.notifyDataSetChanged();
    }

}