package com.example.pictopz.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.pictopz.DrawerActivity;
import com.example.pictopz.R;
import com.example.pictopz.firebase.FirebaseUploadData;
import com.example.pictopz.models.UserObject;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class NewUserActivity extends AppCompatActivity {

    EditText phoneNumber,name,username;
    ImageView picture;
    Button confirm;
    FirebaseAuth mAuth;
    FirebaseUser user;
    UserProfileChangeRequest changeRequest;
    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        String number=getIntent().getStringExtra("NUMBER");

        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();

        dialog=new ProgressDialog(this);
        dialog.showDialog();

        DatabaseReference dbRef= FirebaseDatabase.getInstance().getReference("/users/"+mAuth.getUid());
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dialog.dismissDialog();
                if(snapshot.exists()){
                    startActivity(new Intent(NewUserActivity.this, DrawerActivity.class));
                    finish();
                }else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismissDialog();
            }
        });



        phoneNumber =findViewById(R.id.phone_number_edittext);
        phoneNumber.setText(number);
        phoneNumber.setEnabled(false);

        name=findViewById(R.id.new_user_name);
        username=findViewById(R.id.new_user_username);
        picture=findViewById(R.id.new_user_profilepic);
        confirm=findViewById(R.id.confirm_details);

        confirm.setEnabled(false);

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length()<4){
                    name.setError("Invalid Name");
                    confirm.setEnabled(false);
                }else
                    confirm.setEnabled(true);
            }
        });

        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length()<4){
                    username.setError("Invalid Username");
                    confirm.setEnabled(false);
                }else {
                    checkUsername(editable.toString());
                }
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.showDialog();
                setChangeRequest();
            }
        });



    }

    private void setChangeRequest(){
        changeRequest=new UserProfileChangeRequest.Builder()
                .setDisplayName(name.getText().toString()+"/"+username.getText().toString())
                .build();

        Log.e("CHANGE REQUEST","REQUEST SENT");

        user.updateProfile(changeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    //Successful
                    Log.e("CHANGE REQUEST","REQUEST COMPLETE");
                    uploadData();
                }else{
                    dialog.dismissDialog();
                }
            }
        });
    }

    private void checkUsername(String usernameet){
        Query query= FirebaseDatabase.getInstance().getReference("/users/").orderByChild("username").equalTo(usernameet);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                   Log.e("USER","TAKEN");
                    username.setError("Username Already Taken");
                    confirm.setEnabled(false);
                }else {
                    Log.e("USER","NOT TAKEN");
                    confirm.setEnabled(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismissDialog();
            }
        });
    }

    private void uploadData(){
        UserObject object=new UserObject(username.getText().toString());

        FirebaseUploadData<UserObject> uploadData=new FirebaseUploadData<UserObject>(NewUserActivity.this,"users/"+mAuth.getUid(),object) {

            @Override
            public void onSuccessfulUpload() {
                dialog.dismissDialog();
                Toast.makeText(NewUserActivity.this, "Data Successfully Updated", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(NewUserActivity.this, DrawerActivity.class));
                finish();
            }
        };

        uploadData.start();
    }
}