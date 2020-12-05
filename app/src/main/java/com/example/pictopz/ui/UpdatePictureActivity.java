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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.pictopz.R;
import com.example.pictopz.firebase.FirebaseUploadImage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;

public class UpdatePictureActivity extends AppCompatActivity {


    ImageView imageView;
    FloatingActionButton addPhotoButton;
    Button update;
    Uri filePath;
    FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_picture);

        imageView=findViewById(R.id.profilePicture);
        addPhotoButton=findViewById(R.id.addProfilePic);
        update=findViewById(R.id.update_pic);

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
                    }
                });
            }
        };
    imageUploader.start();
    }


}