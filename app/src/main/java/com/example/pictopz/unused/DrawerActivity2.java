package com.example.pictopz.unused;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.pictopz.R;
import com.example.pictopz.ui.fragment.Profile;
import com.example.pictopz.ui.fragment.UpcomingContests;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.functions.FirebaseFunctions;

public class DrawerActivity2 extends AppCompatActivity    {

    ImageView imageView5,imageView;
    FirebaseFunctions mFunctions;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        mFunctions=FirebaseFunctions.getInstance();
        mAuth = FirebaseAuth.getInstance();

        final DrawerLayout mDrawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        //imageView5=findViewById(R.id.imageView5);

        imageView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawer.openDrawer(Gravity.LEFT);
            }
        });

        imageView = findViewById(R.id.open_drawer);


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DrawerActivity2.this, Profile.class);
                intent.putExtra("userID",mAuth.getCurrentUser().getUid());
                startActivity(intent);
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;

                switch (item.getItemId()){

                    case R.id.drawer_contest_fragment:
                        Toast.makeText(DrawerActivity2.this, "Home", Toast.LENGTH_SHORT).show();
                        break;

//                    case R.id.create_contest:
//                        startActivity(new Intent(DrawerActivity.this,UploadContest.class));
//                        Toast.makeText(DrawerActivity.this, "Create Contest", Toast.LENGTH_SHORT).show();
//                        break;

                    case R.id.drawer_home_fragment:
                        fragment = new UpcomingContests();
                        break;

//                    case R.id.http_test_function:
//                        Log.i("HTTP CALL","ok "+addMessage("Hello beach"));
//                        break;
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