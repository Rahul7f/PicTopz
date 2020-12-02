package com.example.pictopz.ui.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.pictopz.R;
import com.example.pictopz.firebase.FirebaseUploadData;
import com.example.pictopz.firebase.FirebaseUploadImage;
import com.example.pictopz.models.ContestObject;
import com.example.pictopz.models.UnApprovedDataObject;
import com.example.pictopz.ui.UploadContest;
import com.google.firebase.auth.FirebaseAuth;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


public class Participate extends Fragment {

    String contestID;
    int limitValue;
    public Participate(String contesId,int limitValue){
        this.contestID=contesId;
        this.limitValue=limitValue;
    }
    FirebaseAuth mAuth;
    FirebaseUploadImage imgUpload;
    ImageView imageView;
    Button uploadButton;
    Uri filePath;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root= inflater.inflate(R.layout.fragment_participate, container, false);

        mAuth=FirebaseAuth.getInstance();

        imageView=root.findViewById(R.id.upload_image_as_participant);
        uploadButton=root.findViewById(R.id.upload_image_button);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkpermission();
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(filePath!=null){
                    uploadImage();
                }else {
                    Toast.makeText(getContext(), "Please select an Image to Upload", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return root;
    }

    public void uploadImage(){
        imgUpload=new FirebaseUploadImage(getContext(),filePath,"images") {
            @Override
            public void getUrl(String url) {
                Log.e("UPLOAD IMAGE","UPLOAD COMPLETE");
                uploadData(url);
            }
        };

        imgUpload.start();
    }

    private void uploadData(String Imageurl){
        String url="/images/unApproved/";
        int likesNo =0;
        int commentsNo=0;
        String key= UUID.randomUUID().toString();
        url+=key;

        UnApprovedDataObject unApprovedDataObject=new UnApprovedDataObject(Imageurl,key,getUsername(mAuth.getCurrentUser().getDisplayName()),mAuth.getUid(),contestID,likesNo,commentsNo);

        FirebaseUploadData uploadData=new FirebaseUploadData(getContext(),url,unApprovedDataObject) {
            @Override
            public void onSuccessfulUpload() {
                Toast.makeText(getContext(), "Data Updated Successfully", Toast.LENGTH_SHORT).show();
                setLimit();
            };
        };
        uploadData.uploadData();
    }

    private void setLimit(){
        String url="limits/"+contestID+"/"+mAuth.getUid();

        FirebaseUploadData uploadData=new FirebaseUploadData(getContext(),url,limitValue+1) {
            @Override
            public void onSuccessfulUpload() {
                Toast.makeText(getContext(), "Limit Updated Successfully", Toast.LENGTH_SHORT).show();

            };
        };
        uploadData.uploadData();
    }

    private String getRealname(String str){
       return str.split("/")[0];
    }

    private String getUsername(String str){
        return str.split("/")[1];
    }











    //image crop and uri
    void  checkpermission()
    {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        {
            try {
                requestPermissions(new  String[] {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA},100);

            }catch (Exception ignored)
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
            Toast.makeText(getContext(), "This Permission is required", Toast.LENGTH_SHORT).show();
        }
    }

    public void pickimage()
    {
        Fragment participate=getParentFragmentManager().findFragmentByTag("PARTICIPATE");
        CropImage.startPickImageActivity(getContext(),participate);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(getContext(), data);
            croprequest(imageUri);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode==RESULT_OK)
            {
                try {
                    filePath=result.getUri();
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),result.getUri());
                    Toast.makeText(getContext(), "image uri:- "+bitmap, Toast.LENGTH_SHORT).show();
                    imageView.setImageBitmap(bitmap);
                }catch (IOException e)
                {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void croprequest(Uri imageURL)
    {
        Fragment participate=getParentFragmentManager().findFragmentByTag("PARTICIPATE");

        CropImage.activity(imageURL)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(getContext(),participate);
    }
}