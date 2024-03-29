package com.example.pictopz.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pictopz.R;
import com.example.pictopz.ui.activity.DrawerActivity;
import com.example.pictopz.ui.activity.NewUserPic;
import com.example.pictopz.unused.PhoneLoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    Button login_btn;
    EditText email_et, password_et;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String email, password;
    FirebaseUser firebaseUser;
    ImageView google_signup, facebook_signup, phoneLogin;
    TextView noaccount;
    TextView forget_password;
    FirebaseAuth.AuthStateListener listener;
    ProgressBar progressBar;

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
                        startActivity(new Intent(LoginActivity.this, EmailVerification.class));
                        finish();
                    }
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
                    startActivity(new Intent(LoginActivity.this, DrawerActivity.class));
                    finish();
                } else {
                    Log.e("LOL", "Eexe");
                    startActivity(new Intent(LoginActivity.this, NewUserPic.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // hooks
        login_btn = findViewById(R.id.login_btn);
        email_et = findViewById(R.id.login_email_et);
        password_et = findViewById(R.id.login_password_et);
        google_signup = findViewById(R.id.google_signin);
        facebook_signup = findViewById(R.id.facebook_signin);
        phoneLogin = findViewById(R.id.phonelogin);
        facebook_signup.setClickable(false);
        noaccount = findViewById(R.id.no_account);
        forget_password = findViewById(R.id.forget_password);
        progressBar = findViewById(R.id.progress_dialog);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        // end hooks

        text_watcher(password_et);
        text_watcher(email_et);

        forget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forgetPassword();
            }
        });

        noaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUp.class));
            }
        });

        google_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, GoogleSignin.class));
            }
        });

        phoneLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, PhoneLoginActivity.class));
            }
        });


        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                boolean email_value = validate_editText(email_et, "enter email");
                boolean phone_value = validate_editText(password_et, "enter 6 digit password");

                int count = 0;

                if (email_value) {
                    set_error(email_et, "enter email");
                    count = 1;
                    progressBar.setVisibility(View.GONE);
                }
                if (phone_value) {
                    set_error(password_et, "enter 6 digit password");
                    count = 1;
                    progressBar.setVisibility(View.GONE);
                }

                if (count == 0) {
                    email = email_et.getText().toString();
                    password = password_et.getText().toString();

                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });

                }


            }
        });


    }


    void text_watcher(final EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                editText.setError(null);
                // editText.setErrorEnabled(false);

            }
        });
    }


    // check edit text is empty or not
    boolean validate_editText(EditText editText, String error_message) {
        String text = editText.getText().toString();
        if (text.isEmpty()) {
            editText.setError(error_message);
            return true;
        } else {
            return false;
        }

    }


    // set error message when edit text is empty
    void set_error(EditText editText, String error_message) {
        // editText.setErrorEnabled(true);
        editText.setError(error_message);
    }


    private void forgetPassword() {
        if (!validate_editText(email_et, "Enter Email To Reset Password")) {
            FirebaseAuth.getInstance().sendPasswordResetEmail(email_et.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Reset Email Sent", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Error Requesting Reset Email", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

}