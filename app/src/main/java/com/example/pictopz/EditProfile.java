package com.example.pictopz;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;

public class EditProfile extends AppCompatActivity {
    ImageView uploadimage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        uploadimage = findViewById(R.id.uploadimage);

        uploadimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkpermission();
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
            checkpermission();
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
                    Toast.makeText(this, "image uri:- "+bitmap, Toast.LENGTH_SHORT).show();
                    ((ImageView)findViewById(R.id.uploadimage)).setImageBitmap(bitmap);
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
                .start(this);
    }
}