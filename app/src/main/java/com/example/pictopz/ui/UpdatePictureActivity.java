package com.example.pictopz.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.example.pictopz.authentication.LoginActivity;
import com.example.pictopz.firebase.FirebaseUploadImage;
import com.example.pictopz.unused.NewUserActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;

public class UpdatePictureActivity extends AppCompatActivity {


    ImageView imageView;
    FloatingActionButton addPhotoButton;
    Button update;
    Uri filePath;
    EditText usernameET;
    FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkalreadyuser();

        setContentView(R.layout.activity_update_picture);

        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            startActivity(new Intent(UpdatePictureActivity.this,LoginActivity.class));
            finish();
        }

        mAuth = FirebaseAuth.getInstance();
        imageView=findViewById(R.id.profilePicture);
        addPhotoButton=findViewById(R.id.addProfilePic);
        update=findViewById(R.id.update_pic);
        update.setEnabled(false);
        usernameET =findViewById(R.id.username_update_picture);


        usernameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length()<4){
                    usernameET.setError("Invalid Name");
                    update.setEnabled(false);
                }else
                    checkUsername(editable.toString());
            }
        });




        addPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkpermission();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(filePath!=null){
                   uploadPhotoToProfile();
               }
            }
        });
    }

    void checkpermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 100);

            } catch (Exception ignored) {

            }
        } else {
            pickimage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            pickimage();
        } else {
            Toast.makeText(this, "This Permission is required", Toast.LENGTH_SHORT).show();
        }
    }

    public void pickimage() {
        CropImage.startPickImageActivity(UpdatePictureActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);
            croprequest(imageUri);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                try {
                    filePath = result.getUri();
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result.getUri());
                    Toast.makeText(this, "image uri:- " + bitmap, Toast.LENGTH_SHORT).show();
                    imageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void croprequest(Uri imageURL) {
        CropImage.activity(imageURL)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .setOutputCompressQuality(60)
                .setAspectRatio(1,1)
                .start(this);
    }

    private void uploadPhotoToProfile(){
        FirebaseUploadImage imageUploader=new FirebaseUploadImage(UpdatePictureActivity.this,filePath,"profile") {
            @Override
            public void getUrl(String url) {
                FirebaseDatabase.getInstance().getReference("/users/").child(user.getUid()).child("profileURL").setValue(url).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(UpdatePictureActivity.this, "Upload Complete", Toast.LENGTH_SHORT).show();
                        String username=usernameET.getText().toString();
                        updateUsername(username);
                    }
                });
            }
        };
    imageUploader.start();
    }

    private void setChangeRequest(String name,Uri filePath){
        UserProfileChangeRequest changeRequest=new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(filePath)
                .build();

        Log.e("CHANGE REQUEST","REQUEST SENT");

        user.updateProfile(changeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    //Successful
                    Log.e("CHANGE REQUEST","REQUEST COMPLETE");
                    startActivity(new Intent(UpdatePictureActivity.this, DrawerActivity.class));
                    finish();
                }else{
                    //dismiss dialog
                }
            }
        });
    }

    private void updateUsername(String username){
        //Updates or adds username in realtime database
        FirebaseDatabase.getInstance().getReference("users/").child(user.getUid()).child("username").setValue(username).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                setChangeRequest(username,filePath);
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
                    usernameET.setError("Username Already Taken");
                    update.setEnabled(false);
                }else {
                    Log.e("USER","NOT TAKEN");
                    update.setEnabled(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdatePictureActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    void checkalreadyuser()
    {
        DatabaseReference dbRef= FirebaseDatabase.getInstance().getReference("/users/"+mAuth.getUid()).child("username");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    startActivity(new Intent(getApplicationContext(), DrawerActivity.class));
                    finish();
                }else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}