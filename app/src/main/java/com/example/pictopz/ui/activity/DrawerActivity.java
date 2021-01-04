package com.example.pictopz.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.pictopz.R;
import com.example.pictopz.authentication.LoginActivity;
import com.example.pictopz.helper.frag;
import com.example.pictopz.ui.fragment.HomeFragment;
import com.example.pictopz.ui.fragment.Profile;
import com.example.pictopz.ui.fragment.StoryPhotoUpload;
import com.example.pictopz.ui.fragment.UpcomingContests;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.shape.ShapeType;
import co.mobiwise.materialintro.view.MaterialIntroView;

public class DrawerActivity extends AppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener,
        NavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    ImageView drawerToggle;
    DrawerLayout drawerLayout;
    NavigationView drawerNavigationView;
    TextView headerUsername, headerEmail;
    ImageView headerDP;
    View drawerHeaderView;
    MaterialIntroView introView;
    ArrayList<Integer> ids = new ArrayList<>();

    boolean backPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_nav);

        //For Firebase Push Notifications
        FirebaseMessaging.getInstance().subscribeToTopic("login");

        initViews();             //initialization on view items
        populateIdForIntro();    //populate id into arraylist
        showcaseView(0);   //showcase view callback

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        drawerNavigationView.setNavigationItemSelectedListener(this);

        initFragments();

        //fetching profile details for showing in header
        setDrawerHeader();


        drawerToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.openDrawer(GravityCompat.START);
                } else {
                    drawerLayout.closeDrawer(GravityCompat.END);
                }

            }
        });

    }

    private void initFragments() {
        frag.hashMap.put("Contest", new UpcomingContests());
        frag.hashMap.put("Home", new HomeFragment());
        frag.hashMap.put("Upload", new StoryPhotoUpload());
        frag.hashMap.put("Profile", new Profile(mAuth.getUid(), mAuth.getCurrentUser().getDisplayName()));

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, frag.hashMap.get("Contest"))
                .commit();
    }

    private void initViews() {
        bottomNavigationView = findViewById(R.id.nav_view);
        drawerToggle = findViewById(R.id.open_drawer);
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerNavigationView = findViewById(R.id.drawer_view);

        drawerHeaderView = drawerNavigationView.getHeaderView(0);
        headerUsername = drawerHeaderView.findViewById(R.id.username_text);
        headerEmail = drawerHeaderView.findViewById(R.id.useremail_text);
        headerDP = drawerHeaderView.findViewById(R.id.userprofile_image);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if (item.getTitle().toString().equals("Logout")) {
            mAuth.signOut();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, frag.hashMap.get(item.getTitle().toString()), "HOME_ITEMS")
                    .commit();
        }

        drawerLayout.closeDrawer(Gravity.LEFT);
        return true;
    }

    void showcaseView(int index) {
        if (index > 3)
            return;
        int id = ids.get(index);
        String texts = "";
        switch (id) {
            case R.id.contest_fragment:
                texts = "You can find Contests Here";
                break;
            case R.id.home_fragment:
                texts = "Uploaded Posts Stories are here";
                break;
            case R.id.addphoto_fragment:
                texts = "Your can upload photos and stories here";
                break;
            case R.id.profile_fragment:
                texts = "Use this button to visit your Profile.";
                break;
        }

        introView = new MaterialIntroView.Builder(this)
                .enableIcon(false)
                .setFocusGravity(FocusGravity.CENTER)
                .setFocusType(Focus.MINIMUM)
                .setDelayMillis(0)
                .enableFadeAnimation(true)
                .performClick(false)
                .setInfoText(texts)
                .setShape(ShapeType.CIRCLE)
                .setTarget(bottomNavigationView.getRootView().findViewById(id))
                .setMaskColor(Color.parseColor("#aa000000"))
                .setUsageId(id + "yukfuy")
                .setListener(materialIntroViewId -> showcaseView(index + 1))
                .show();
    }

    private void populateIdForIntro() {
        ids.add(R.id.contest_fragment);
        ids.add(R.id.home_fragment);
        ids.add(R.id.addphoto_fragment);
        ids.add(R.id.profile_fragment);
    }

    private void setDrawerHeader() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("/users/" + mAuth.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    headerUsername.setText(snapshot.child("username").getValue().toString());
                    headerEmail.setText(snapshot.child("email").getValue().toString());
                    Glide.with(getApplicationContext()).load(snapshot.child("profileURL").getValue()).into(headerDP);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment instanceof UpcomingContests) {
            //if current fragment is upcoming contest then ask for exit
            if (!backPressed) {
                backPressed = true;
                Toast.makeText(this, "Press back again to exit!", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(() -> {
                    backPressed = false;
                }, 1500);
            } else {
                finish();
            }
        } else if (currentFragment.getTag().equals(getString(R.string.participate))) {
            //if current frag is participate then go to one contest
            loadFragment(getString(R.string.one_contest));
        } else if (currentFragment.getTag().equals(getString(R.string.other_profile)) || currentFragment.getTag().equals("COMMENT")) {
            loadFragment("Home");
        } else {
            loadFragment("Contest");
            bottomNavigationView.setSelectedItemId(R.id.contest_fragment);
        }
    }

    private void loadFragment(String key) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, frag.hashMap.get(key))
                .commit();
    }
}