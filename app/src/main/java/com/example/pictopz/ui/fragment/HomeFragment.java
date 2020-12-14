package com.example.pictopz.ui.fragment;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.pictopz.R;
import com.example.pictopz.adapters.PostsAdapter;
import com.example.pictopz.adapters.StoryAdapter;
import com.example.pictopz.models.ApprovedPostObject;
import com.example.pictopz.models.StoryObject;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;

import xute.storyview.StoryModel;


public class HomeFragment extends Fragment {

    ImageView imageView;
    SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<ApprovedPostObject> approvedPostObjects = new ArrayList<>();
    PostsAdapter postAdapter;
    RecyclerView recyclerView1, recyclerView;
    StoryAdapter storyAdapter;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    HashMap<String, ArrayList> storyHashMap = new HashMap<>();
    ArrayList<String> keyset = new ArrayList<>();
    ArrayList<String> following = new ArrayList<>();

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

//                loadRecycleViewData();

            }
        });


        fetchFollowList();
        recyclerView = root.findViewById(R.id.home_story_recycleview);
        storyAdapter = new StoryAdapter(storyHashMap, keyset);
        recyclerView.setAdapter(storyAdapter);
        // post
        recyclerView1 = root.findViewById(R.id.home_layout_post_recycleview);
        postAdapter = new PostsAdapter(getContext(), approvedPostObjects);
        recyclerView1.setAdapter(postAdapter);

        //TODO have to add a scroll listener so that more than 10 items can be displayed

        loadRecycleViewData();


        return root;
    }

    private void loadRecycleViewData() {
        swipeRefreshLayout.setRefreshing(true);
        fetchFollowList();
        CountDownTimer timer=new CountDownTimer(2000,2000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                swipeRefreshLayout.setRefreshing(false);
            }
        };

        timer.start();


    }

    private void fetchFollowList() {

        storyHashMap.clear();
        keyset.clear();

        FirebaseDatabase.getInstance().getReference("following/" + user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    following.clear();
                    for (DataSnapshot users : snapshot.getChildren()) {
                        following.add(users.getKey());
                    }
                    following.add(user.getUid());
                    fetchStories();
                    fetchPosts();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fetchStories() {

        CollectionReference reference = FirebaseFirestore.getInstance().collection("story");
        reference
                .whereIn("uploaderUID", following)
                .orderBy("uploadTime", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                        StoryObject object = snapshot.toObject(StoryObject.class);
                        deleteStoryIfTimeout(object, object.uploaderUID);
                    }
                }
            }
        });

    }

    private void deleteStoryIfTimeout(StoryObject object, String otherUserUID) {
        //Delete Story if timout
        //Add to arraylist if valid

        GregorianCalendar calendar = new GregorianCalendar();
        long currentTime = calendar.getTimeInMillis();
        if (object.uploadTime + 28800000 < currentTime) {
            FirebaseFirestore.getInstance().collection("story").document(object.storyID).delete();
        } else {
            StoryModel model = new StoryModel(object.imageURL, object.uploaderUID, String.valueOf(object.uploadTime));

            if (storyHashMap.containsKey(object.uploaderUID)) {
                storyHashMap.get(object.uploaderUID).add(model);
            } else {
                ArrayList<StoryModel> models = new ArrayList<>();
                models.add(model);
                storyHashMap.put(object.uploaderUID, (ArrayList) models);
                keyset.add(object.uploaderUID);
            }
        }
        storyAdapter.notifyDataSetChanged();
    }

    private void fetchPosts() {
        following.add("CONTEST");
        CollectionReference dbRef = FirebaseFirestore.getInstance().collection("posts");
        Query query = dbRef
                .whereEqualTo("approved", true)
                .whereIn("filterID", following)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    approvedPostObjects.clear();
                    for (QueryDocumentSnapshot snapshot1 : task.getResult()) {
                        approvedPostObjects.add(snapshot1.toObject(ApprovedPostObject.class));
                        Log.e("POST", "LIKES " + approvedPostObjects.get(0).commentsNo);
                    }
                    postAdapter.notifyDataSetChanged();
                }
            }
        });
    }


}