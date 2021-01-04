package com.example.pictopz.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pictopz.R;
import com.example.pictopz.helper.FirebaseUploadData;
import com.example.pictopz.models.UserProfileObject;
import com.example.pictopz.ui.activity.DrawerActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SignUp extends AppCompatActivity {
    ImageView  imageView;
    EditText name_et,email_et,password_et,re_password_et;
    String name,email,phone,password,re_password;
    private FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        //hooks
        imageView = findViewById(R.id.signup_Btn);
        name_et = findViewById(R.id.signup_username_et);
        email_et = findViewById(R.id.signup_email_et);
        password_et = findViewById(R.id.signup_password_et);
        re_password_et = findViewById(R.id.signup_REpassword__et);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference("users");

        //hooks_end
        textWatcherForUsername();
        text_watcher(email_et);
        text_watcher(password_et);
        text_watcher(re_password_et);


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean name_value =  validate_editText(name_et);
                boolean email_value =  validate_editText(email_et);
                boolean password_value =  validate_editText(password_et);
                boolean repassword_value =  validate_editText(re_password_et);

                int count = 0;

                if (name_value)
                {
                    name = name_et.getText().toString();
                    if (name.length()<4)
                    {
                        set_error(name_et,"too short name");
                    }
                    else
                    {
                        set_error(name_et,"enter name");
                    }
                    count = 1;


                }
                if (email_value)
                {
                    set_error(email_et,"enter email");
                    count = 1;
                }

                if(password_value)
                {
                    password = password_et.getText().toString();
                    if (password.length()<6)
                    {
                        set_error(password_et,"Enter 6 digits password");
                    }
                    else {
                        set_error(password_et,"enter password");
                    }

                    count = 1;
                }

                if(repassword_value)
                {

                    set_error(re_password_et,"re enter password");
                    count = 1;
                }

                if (count == 0)
                {

                    email = email_et.getText().toString();
                    password = password_et.getText().toString();
                    re_password = re_password_et.getText().toString();
                    name = name_et.getText().toString();
                    if (password.equals(re_password))
                    {
                       CreateUser(email,password,"",name);
                    }
                    else
                    {
                        Toast.makeText(SignUp.this, "Password not match", Toast.LENGTH_SHORT).show();
                    }



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


    void textWatcherForUsername(){
        name_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length()<4){
                    name_et.setError("Invalid Username");
                    imageView.setEnabled(false);
                }else {
                    checkUsername(editable.toString());
                }
            }
        });
    }

    private void checkUsername(String usernameText){
        Query query= FirebaseDatabase.getInstance().getReference("/users/").orderByChild("username").equalTo(usernameText);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Log.e("USER","TAKEN");
                    name_et.setError("Username Already Taken");
                    imageView.setEnabled(false);
                }else {
                    Log.e("USER","NOT TAKEN");
                    imageView.setEnabled(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    // check edit text is empty or not
    boolean  validate_editText(EditText editText)
    {
        String text = editText.getText().toString();
        if (text.isEmpty())
        {
            return true;
        }
        else
        {
            return  false;
        }

    }

    // set error message when edit text is empty
    void set_error(EditText textInputLayout,String error_message)
    {
//        textInputLayout.setErrorEnabled(true);
        textInputLayout.setError(error_message);
    }


    void CreateUser(final String email, String password, final String phone, final String name)
    {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {


                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(SignUp.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                            uploadData(name,email);
                            setChangeRequest(name,email,"");

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignUp.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            // ...
                        }

                        // ...
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(mAuth.getCurrentUser()==null){
                    //do nothing
                }else {
                    if(mAuth.getCurrentUser().isEmailVerified()){
                        startActivity(new Intent(SignUp.this, DrawerActivity.class));
                        finish();
                    }else{
                        Toast.makeText(SignUp.this, "Email Not Verified", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUp.this,EmailVerification.class));
                            finish();
                        }
                }
            }
        });
    }

    private void setChangeRequest(String name,String email,String phone){
        UserProfileChangeRequest changeRequest=new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        mAuth.getCurrentUser().updateProfile(changeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Log.e("CHANGE REQUEST","REQUEST COMPLETE");
                }else{
                    Toast.makeText(SignUp.this, "Network Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadData(String name,String email){
        UserProfileObject object=new UserProfileObject(name,email);

        FirebaseUploadData<UserProfileObject> uploadData=new FirebaseUploadData<UserProfileObject>(SignUp.this,"users/"+mAuth.getUid(),object) {
            @Override
            public void onSuccessfulUpload() {
                Toast.makeText(SignUp.this, "Data Successfully Updated", Toast.LENGTH_SHORT).show();
            }
        };

        uploadData.start();
    }
}