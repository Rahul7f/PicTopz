package com.example.pictopz;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.example.pictopz.authentication.LoginActivity;
import com.example.pictopz.ui.fragment.HomeFragment;
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
        NavigationView.OnNavigationItemSelectedListener,
        PopupMenu.OnMenuItemClickListener{

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

        //fetching profile details for showing in header
        setDrawerHeader();

        //Top-right corner popup menu
        PopupMenu popupMenu = new PopupMenu(this, drawerToggle);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(this);

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

    private void initViews(){
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
        Fragment fragment = null;
        String TAG = "";
        switch (item.getItemId()) {

            case R.id.home_fragment:
            case R.id.drawer_home_fragment:
                fragment = new HomeFragment();
                TAG = "HOME";
                break;

            case R.id.contest_fragment:
            case R.id.drawer_contest_fragment:
                fragment = new UpcomingContests();
                TAG = "UPCOMING";
                break;

            case R.id.profile_fragment:
            case R.id.drawer_profile_fragment:
                fragment = new Profile(mAuth.getUid(), mAuth.getCurrentUser().getDisplayName());
                TAG = "PROFILE";
                break;

            case R.id.addphoto_fragment:
            case R.id.drawer_addphoto_fragment:
                fragment = new StoryPhotoUpload();
                TAG = "UPLOAD";
                break;
            case R.id.drawer_logout:
                mAuth.signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;

        }
        drawerLayout.closeDrawer(Gravity.LEFT);
        manualSelector(TAG);

        return loadFragment(fragment, TAG);
    }

    private boolean loadFragment(Fragment fragment, String TAG) {
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
                bottomNavigationView.getMenu().findItem(R.id.home_fragment).setChecked(true);
                break;
            case "UPCOMING":
                bottomNavigationView.getMenu().findItem(R.id.contest_fragment).setChecked(true);
                break;
            case "PROFILE":
                bottomNavigationView.getMenu().findItem(R.id.profile_fragment).setChecked(true);
                break;
            case "UPLOAD":
                bottomNavigationView.getMenu().findItem(R.id.addphoto_fragment).setChecked(true);
                break;
        }
    }

    @Override
    public void onBackPressed() {

        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.fragment_container);
        if (!(fragment instanceof UpcomingContests)) {
            manager.beginTransaction()
                    .replace(R.id.fragment_container, manager.findFragmentByTag("UPCOMING"))
                    .commit();
            manualSelector("UPCOMING");
        } else
            finish();
    }

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

//    private void popAllFragment() {
//        FragmentManager manager = getSupportFragmentManager();
//        for (int i = manager.getBackStackEntryCount(); i > 0; i--)
//            manager.popBackStack();
//
//    }

private void populateIdForIntro() {
    ids.add(R.id.contest_fragment);
    ids.add(R.id.home_fragment);
    ids.add(R.id.addphoto_fragment);
    ids.add(R.id.profile_fragment);
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

    private void setDrawerHeader(){
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
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.logout:
                mAuth.signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;

            case R.id.aboutApp:
                Toast.makeText(DrawerActivity.this, "about us coming soon", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }
}