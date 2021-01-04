package com.example.pictopz.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.pictopz.R;
import com.example.pictopz.helper.FirebaseUploadImage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;

public class EditProfile extends AppCompatActivity {
    ImageView update_btn;
    ShapeableImageView uploadimage;
//    EditText name_et,email_et,phone_et;
//    String name,email,phone;
    Uri profileURL;
    //Firebase
    FirebaseAuth mAuth = FirebaseAuth.getInstance();;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        //hooks
        uploadimage = findViewById(R.id.uploadimage);
//        name_et = findViewById(R.id.edit_name_et);
//        email_et = findViewById(R.id.edit_email_et);
//        phone_et = findViewById(R.id.edit_phone_et);
        update_btn = findViewById(R.id.edit_btn);


        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users").child(mAuth.getCurrentUser().getUid());
        getimage();
        //hooks end

        uploadimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkpermission();
            }
        });
        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(profileURL!=null)
                uploadImage(profileURL);
//                name = name_et.getText().toString();
//                email = email_et.getText().toString();
//                phone = phone_et.getText().toString();
//                uplaoddata(name,email,phone);

            }
        });

    }

    void  checkpermission()
    {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        {
            try {
                requestPermissions(new  String[] {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA},100);
            }catch (Exception e)
            {

            }
        }
        else {
            pickimage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode==100 && grantResults[0]== PackageManager.PERMISSION_GRANTED)
        {
            pickimage();
        }
        else
        {
            Toast.makeText(this, "This Permission is Required !", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);
            croprequest(imageUri);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode==RESULT_OK)
            {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),result.getUri());
                    profileURL = result.getUri();
//                    Toast.makeText(this, "image uri:- "+bitmap, Toast.LENGTH_SHORT).show();
                    uploadimage.setImageBitmap(bitmap);
                }catch (IOException e)
                {
                    e.printStackTrace();
                    Toast.makeText(this, "error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void pickimage()
    {
        CropImage.startPickImageActivity(EditProfile.this);
    }

    public void croprequest(Uri imageURL)
    {
        CropImage.activity(imageURL)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .setAspectRatio(1,1)
                .setOutputCompressQuality(40)
                .start(this);
    }

    private void uploadImage(Uri imageurl) {

        FirebaseUploadImage imageUploader=new FirebaseUploadImage(this,imageurl,"profile") {
            @Override
            public void getUrl(String url) {
                FirebaseDatabase.getInstance().getReference("/users/").child(mAuth.getUid()).child("profileURL").setValue(url).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(EditProfile.this, "Upload Complete", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        imageUploader.start();
    }

    public void getimage()
    {
        FirebaseDatabase.getInstance().getReference("users").child(mAuth.getUid()).child("profileURL").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                Glide.with(EditProfile.this)
                        .load(snapshot.getValue(String.class))
                        .into(uploadimage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void uplaoddata(String name,String email,String phone)
    {


       if (name!=null && !name.equals(""))
       {
         databaseReference.child("username").setValue(name);

       }

        if (email!=null && !email.equals(""))
        {
            databaseReference.child("email").setValue(email);

        }

        if (phone!=null && !phone.equals(""))
        {
            databaseReference.child("phone").setValue(phone);

        }
    }
}