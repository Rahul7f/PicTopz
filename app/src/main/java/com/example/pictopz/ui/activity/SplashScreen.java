package com.example.pictopz.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pictopz.R;
import com.example.pictopz.authentication.EmailVerification;
import com.example.pictopz.authentication.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashScreen extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth=FirebaseAuth.getInstance();

//        int secondsDelayed = 1;
//        new Handler().postDelayed(new Runnable() {
//            public void run() {
//                startActivity(new Intent(SplashScreen.this, LoginActivity.class));
//                finish();
//            }
//        }, secondsDelayed * 3000);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(listener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (mAuth.getCurrentUser() != null) {
                    mAuth.getCurrentUser().reload();
                    if (mAuth.getCurrentUser().isEmailVerified()) {
                        checkUserName();
                    } else {
                        startActivity(new Intent(SplashScreen.this, EmailVerification.class));
                        finish();
                    }
                }else{
                    new Handler().postDelayed(()-> {
                            startActivity(new Intent(SplashScreen.this,LoginActivity.class));
                            finish();
                    },1000);

                 }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(listener);
    }

    private void checkUserName() {
        FirebaseDatabase.getInstance().getReference("users").child(mAuth.getUid()).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    startActivity(new Intent(SplashScreen.this, DrawerActivity.class));
                    finish();
                } else {
                    Log.e("LOL", "Eexe");
                    startActivity(new Intent(SplashScreen.this, NewUserPic.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}