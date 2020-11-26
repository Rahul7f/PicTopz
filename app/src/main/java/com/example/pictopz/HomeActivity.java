package com.example.pictopz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.pictopz.adapters.PostsAdapter;
import com.example.pictopz.adapters.StoryAdapter;

public class HomeActivity extends AppCompatActivity {
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        imageView = findViewById(R.id.profile);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,Profile.class));
            }
        });

        RecyclerView recyclerView=findViewById(R.id.home_story_recycleview);
        recyclerView.setAdapter(new StoryAdapter());

        RecyclerView recyclerView1=findViewById(R.id.home_layout_post_recycleview);
        recyclerView1.setAdapter(new PostsAdapter());
    }
}