package com.example.pictopz;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.example.pictopz.R;
import com.example.pictopz.ui.fragment.HomeFragment;
import com.example.pictopz.ui.fragment.UpcomingContests;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DrawerActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView navView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_nav);
        navView = findViewById(R.id.nav_view);

        navView.setOnNavigationItemSelectedListener(this);


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        String TAG="";
        switch (item.getItemId()){

            case R.id.nav_home:
                fragment=new HomeFragment();
                TAG="HOME";
                break;


            case R.id.upcoming_contest_menu:
                fragment = new UpcomingContests();
                TAG="UPCOMING";
                break;

        }

        return loadFragment(fragment,TAG) ;
    }

    private boolean loadFragment(Fragment fragment,String TAG) {
        Log.i("Load called","LOL");
        Fragment TaggedFragment=getSupportFragmentManager().findFragmentByTag(TAG);
        Log.e("TAGGED FRAG",TaggedFragment+"ok");

        if(TaggedFragment !=null){
            if(TaggedFragment instanceof HomeFragment){
                getSupportFragmentManager().popBackStack();
            }

            getSupportFragmentManager().
                   beginTransaction()
                  .replace(R.id.fragment_container,TaggedFragment,TAG)
                 .commit();
         return true;
        }
        else if (fragment != null) {
            Toast.makeText(this, "test", Toast.LENGTH_SHORT).show();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment,TAG)
                    .addToBackStack(TAG)
                    .commit();
            return true;
        }
        return false;
    }

    private void deleteFrag(String TAG){

        Fragment fragment=getSupportFragmentManager().findFragmentByTag(TAG);
        getSupportFragmentManager().beginTransaction().remove(fragment).commit();

    }

    private void manualSelector(String TAG){
        switch (TAG){
            case "HOME":
                navView.setSelectedItemId(R.id.nav_home);
                break;
            case "UPCOMING":
                navView.setSelectedItemId(R.id.upcoming_contest_menu);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Fragment fragment=getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if(fragment instanceof  HomeFragment){
         manualSelector("HOME");
        }else if(fragment instanceof UpcomingContests){
            manualSelector("UPCOMING");
        }
    }

}