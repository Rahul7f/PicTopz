package com.example.pictopz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {
    ImageView  imageView;
    EditText name_et,email_et,phone_et,password_et,re_password_et;
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
        phone_et = findViewById(R.id.signup_phone_et);
        password_et = findViewById(R.id.signup_password_et);
        re_password_et = findViewById(R.id.signup_REpassword__et);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference("users");

        //hooks_end

        text_watcher(name_et);
        text_watcher(email_et);
        text_watcher(phone_et);
        text_watcher(password_et);
        text_watcher(re_password_et);


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean name_value =  validate_editText(name_et);
                boolean email_value =  validate_editText(email_et);
                boolean phone_value =  validate_editText(phone_et);
                boolean password_value =  validate_editText(password_et);
                boolean repassword_value =  validate_editText(re_password_et);

                int count = 0;

                if (name_value!=false)
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
                if (email_value!=false)
                {
                    set_error(email_et,"enter email");
                    count = 1;
                }
                if(phone_value!=false)
                {
                    phone = phone_et.getText().toString();
                    if (phone.length()<10)
                    {
                        set_error(phone_et,"enter 10 digit phoneNO ");
                    }
                    else {
                        set_error(phone_et,"enter phone no");
                    }
                    count = 1;

                }

                if(password_value!=false)
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

                if(repassword_value!=false)
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
                    phone = phone_et.getText().toString();
                    if (password.endsWith(re_password))
                    {
                       CreateUser(email,password,phone,name);
                    }
                    else
                    {
                        Toast.makeText(SignUp.this, "Password not match", Toast.LENGTH_SHORT).show();
                    }



                }
                //startActivity(new Intent(SignUp.this, HomeActivity.class));
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
                            Toast.makeText(SignUp.this, "SignUp success", Toast.LENGTH_SHORT).show();
                            UserObject userObject = new UserObject(name,email,phone);
                            String uid = mAuth.getUid();
                            ref.child(uid).setValue(userObject).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(SignUp.this, "data u", Toast.LENGTH_SHORT).show();
                                }
                            });
                            updateUI();


                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignUp.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            // ...
                        }

                        // ...
                    }
                });
    }

    private void updateUI() {
        Intent intent = new Intent(SignUp.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}