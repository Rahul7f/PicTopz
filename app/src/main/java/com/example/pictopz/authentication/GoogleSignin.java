package com.example.pictopz.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pictopz.DrawerActivity;
import com.example.pictopz.R;
import com.example.pictopz.models.UserProfileObject;
import com.example.pictopz.ui.UpdatePictureActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import android.os.Bundle;

public class GoogleSignin extends AppCompatActivity {
    private static final int RC_SIGN_IN = 101 ;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_signin);

        mAuth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference("users");

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .requestId()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(getApplicationContext(), gso);

        signIn();
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    FirebaseUser user = mAuth.getCurrentUser();
                    Toast.makeText(getApplicationContext(), user.getEmail()+"\n"+user.getDisplayName(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), "successfully", Toast.LENGTH_SHORT).show();
                    String uid = mAuth.getUid();

                    String email = user.getEmail();
                    String phone = user.getPhoneNumber();
                    Uri imageURL = user.getPhotoUrl();

                    validate("name",email,phone);
                    UserProfileObject userObject  = new UserProfileObject(null,email,phone);
                    userObject.profileURL=imageURL.toString();

                    ref.child(uid).child("email").setValue(email);

                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
//                            Snackbar.make(mBinding.mainLayout, "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

    }

    FirebaseAuth.AuthStateListener listener;
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(listener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(mAuth.getCurrentUser()!=null){
                    Log.e("Log","exegc");
                    startActivity(new Intent(GoogleSignin.this, UpdatePictureActivity.class));
                    finish();
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(listener);
    }

    void  validate(String name, String email, String phone)
    {
        if (name==null)
        {
            name = "user name";
        }

        if (email==null)
        {
            name = "Email";
        }

        if (phone==null)
        {
            name = "Phone no";
        }
    }

}
