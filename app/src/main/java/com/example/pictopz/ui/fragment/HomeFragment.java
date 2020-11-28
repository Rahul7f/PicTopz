package com.example.pictopz.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pictopz.Profile;
import com.example.pictopz.R;
import com.example.pictopz.adapters.PostsAdapter;
import com.example.pictopz.adapters.StoryAdapter;

public class HomeFragment extends Fragment {

    ImageView imageView;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);



        RecyclerView recyclerView=root.findViewById(R.id.home_story_recycleview);
        recyclerView.setAdapter(new StoryAdapter());

        RecyclerView recyclerView1=root.findViewById(R.id.home_layout_post_recycleview);
        recyclerView1.setAdapter(new PostsAdapter());

        return root;
    }
}