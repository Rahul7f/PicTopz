package com.example.pictopz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.pictopz.adapters.ProfileGrideAdapter;

public class Profile extends AppCompatActivity {

    GridView simpleGrid;
    int logos[] = {R.drawable.sample_image, R.drawable.sample_image, R.drawable.sample_image, R.drawable.sample_image,
            R.drawable.sample_image, R.drawable.sample_image, R.drawable.sample_image, R.drawable.sample_image, R.drawable.sample_image,
            R.drawable.sample_image, R.drawable.sample_image, R.drawable.sample_image, R.drawable.sample_image};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        simpleGrid = (GridView) findViewById(R.id.simpleGridView);
        ProfileGrideAdapter customAdapter = new ProfileGrideAdapter(getApplicationContext(), logos);
        simpleGrid.setAdapter(customAdapter);

        simpleGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // set an Intent to Another Activity
//                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
//                intent.putExtra("image", logos[position]); // put image data in Intent
//                startActivity(intent); // start Intent
            }
        });
    }
}