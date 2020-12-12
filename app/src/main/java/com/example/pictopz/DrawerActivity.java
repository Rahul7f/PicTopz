package com.example.pictopz;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.pictopz.ui.fragment.HomeFragment;
import com.example.pictopz.ui.fragment.UpcomingContests;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class DrawerActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView navView;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_nav);
        navView = findViewById(R.id.nav_view);
        mAuth = FirebaseAuth.getInstance();
        navView.setOnNavigationItemSelectedListener(this);

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
                fragment = new Profile(mAuth.getUid());
                TAG = "PROFILE";
                break;

        }

        return loadFragment(fragment, TAG);
    }

    private boolean loadFragment(Fragment fragment, String TAG) {
        FragmentManager manager = getSupportFragmentManager();
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
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();

        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.fragment_container);
        if(!(fragment instanceof HomeFragment)){
        manager.beginTransaction()
                .replace(R.id.fragment_container,manager.findFragmentByTag("HOME"))
                .addToBackStack(null)
                .commit();
        manualSelector("HOME");
        }else
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