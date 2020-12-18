package com.example.pictopz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.SupportMenuInflater;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.example.pictopz.authentication.LoginActivity;
import com.example.pictopz.ui.fragment.HomeFragment;
import com.example.pictopz.ui.fragment.UpcomingContests;
import com.example.pictopz.unused.DrawerActivity2;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class DrawerActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView navView;
    FirebaseAuth mAuth;
    ImageView optionMenu;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    TextView username,email;
    ImageView imageView;
    View header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_nav);
        navView = findViewById(R.id.nav_view);
        mAuth = FirebaseAuth.getInstance();
        navView.setOnNavigationItemSelectedListener(this);
        FirebaseMessaging.getInstance().subscribeToTopic("login");
        optionMenu = findViewById(R.id.more_options);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView= findViewById(R.id.drawer_view);


        header = navigationView.getHeaderView(0);
        username = header.findViewById(R.id.username_text);
        email = header.findViewById(R.id.useremail_text);
        imageView = header.findViewById(R.id.userprofile_image);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("/users/"+mAuth.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    username.setText(snapshot.child("username").getValue().toString());
                    email.setText(snapshot.child("email").getValue().toString());
                    Glide.with(getApplicationContext()).load(snapshot.child("profileURL").getValue()).into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        PopupMenu popupMenu = new PopupMenu(DrawerActivity.this, optionMenu);
        popupMenu.getMenuInflater().inflate(R.menu.menu2, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case R.id.logout:

                        mAuth.signOut();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                        break;
                    case R.id.aboutApp:
                        Toast.makeText(DrawerActivity.this, "about us coming soon", Toast.LENGTH_SHORT).show();
                        break;
                }

                return true;
            }
        });

        optionMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //popupMenu.show();
                if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.openDrawer(GravityCompat.START);
                } else {
                    drawerLayout.closeDrawer(GravityCompat.END);
                }
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                String TAG = "";
                switch (item.getItemId()){

                    case R.id.drawer_upcoming_contest_fragment:
                        fragment = new UpcomingContests();
                        TAG = "UPCOMING";
                        break;

                    case R.id.drawer_home_fragment:
                        fragment = new HomeFragment();
                        TAG = "HOME";
                        break;

                    case R.id.drawer_addphoto_fragment:
                        fragment = new StoryPhotoUpload();
                        TAG = "UPLOAD";
                        break;

                    case R.id.drawer_profile_fragment:
                        fragment = new Profile(mAuth.getUid(),mAuth.getCurrentUser().getDisplayName());
                        TAG = "PROFILE";
                        break;
                    case R.id.drawer_logout:
                        mAuth.signOut();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        break;
                }
                drawerLayout.closeDrawer(Gravity.LEFT);
                manualSelector(TAG);

                return loadFragment(fragment, TAG);
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        String TAG = "";
        switch (item.getItemId()) {

            case R.id.home_fragment:
                fragment = new HomeFragment();
                TAG = "HOME";
                break;


            case R.id.upcoming_contest_fragment:
                fragment = new UpcomingContests();
                TAG = "UPCOMING";
                break;

            case R.id.profile_fragment:
                fragment = new Profile(mAuth.getUid(),mAuth.getCurrentUser().getDisplayName());
                TAG = "PROFILE";
                break;

            case R.id.addphoto_fragment:
                fragment = new StoryPhotoUpload();
                TAG = "UPLOAD";
                break;

        }

        return loadFragment(fragment, TAG);
    }

    private boolean loadFragment(Fragment fragment, String TAG) {
//        FragmentManager manager = getSupportFragmentManager();
//        Fragment taggedFragment = manager.findFragmentByTag(TAG);
//        if (taggedFragment != null) {
//            Log.e("FRAGMENT", "TAGGED FRAGMENT FOUND");
//            manager
//                    .beginTransaction()
//                    .replace(R.id.fragment_container, taggedFragment, TAG)
//                    .commit();
//
//
//
//            return true;
//        } else
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment, TAG)
                    .addToBackStack(TAG)
                    .commit();
            return true;
        }
        return false;
    }


    private void manualSelector(String TAG) {
        switch (TAG) {
            case "HOME":
                navView.getMenu().findItem(R.id.home_fragment).setChecked(true);
                break;
            case "UPCOMING":
                navView.getMenu().findItem(R.id.upcoming_contest_fragment).setChecked(true);
                break;
            case "PROFILE":
                navView.getMenu().findItem(R.id.profile_fragment).setChecked(true);
                break;
            case "UPLOAD":
                navView.getMenu().findItem(R.id.addphoto_fragment).setChecked(true);
                break;
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();

        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.fragment_container);
        if (!(fragment instanceof UpcomingContests)) {
            manager.beginTransaction()
                    .replace(R.id.fragment_container, manager.findFragmentByTag("UPCOMING"))
                    .commit();
            manualSelector("UPCOMING");
        } else
            finish();


//        manager.beginTransaction().remove(fragment).commit();
//        int index=manager.getBackStackEntryCount()-1;
//        FragmentManager.BackStackEntry backStackEntry=manager.getBackStackEntryAt(index);
//        Fragment stackFrag=manager.findFragmentByTag(backStackEntry.getName());
//        manager.beginTransaction().replace(R.id.fragment_container,stackFrag,backStackEntry.getName()).commit();
//
//
//        if (fragment instanceof HomeFragment) {
//            finish();
//        } else if (fragment instanceof UpcomingContests) {
//            manualSelector("UPCOMING");
//        } else if (fragment instanceof Profile) {
//            manualSelector("PROFILE");
//        }

    }

//    private void popAllFragment() {
//        FragmentManager manager = getSupportFragmentManager();
//        for (int i = manager.getBackStackEntryCount(); i > 0; i--)
//            manager.popBackStack();
//
//    }

}