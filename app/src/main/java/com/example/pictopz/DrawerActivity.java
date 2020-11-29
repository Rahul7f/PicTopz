package com.example.pictopz;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.pictopz.ui.UploadContest;
import com.example.pictopz.ui.fragment.UpcomingContests;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

public class DrawerActivity extends AppCompatActivity    {

    ImageView imageView5,imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);


        final DrawerLayout mDrawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        imageView5=findViewById(R.id.imageView5);

        imageView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawer.openDrawer(Gravity.LEFT);
            }
        });

        imageView = findViewById(R.id.profile);


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DrawerActivity.this, Profile.class));
            }
        });


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;

                switch (item.getItemId()){

                    case R.id.nav_home:
                        Toast.makeText(DrawerActivity.this, "Home", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.create_contest:
                        startActivity(new Intent(DrawerActivity.this,UploadContest.class));
                        Toast.makeText(DrawerActivity.this, "Create Contest", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.upcoming_contest_menu:
                        fragment = new UpcomingContests();
                        break;
                }
                mDrawer.closeDrawer(Gravity.LEFT);

                return loadFragment(fragment) ;
            }
        });
    }


    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            Toast.makeText(this, "test", Toast.LENGTH_SHORT).show();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack("HOME")
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
       // HomeFragment fragment=(HomeFragment)getSupportFragmentManager().findFragmentByTag("HOME");
        super.onBackPressed();
    }
}