package com.example.pictopz;

import android.Manifest;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.pictopz.firebase.FirebaseUploadImage;
import com.example.pictopz.models.ApprovedPostObject;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.GregorianCalendar;
import java.util.UUID;

public class StoryPhotoUpload extends Fragment {

    FirebaseAuth mAuth;
    FirebaseUploadImage imgUpload;
    ImageView imageView;
    Button uplaodPost,uplaodStory;
    Uri filePath;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root= inflater.inflate(R.layout.fragment_story_photo_upload, container, false);
        mAuth=FirebaseAuth.getInstance();
        imageView=root.findViewById(R.id.upload_image_as_user);
        uplaodPost=root.findViewById(R.id.upload_photo_button);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "clicked", Toast.LENGTH_SHORT).show();
                checkpermission();
            }
        });

        uplaodPost.setOnClickListener(new View.OnClickListener() {
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

    public void pickimage()
    {
        Fragment fragment=getParentFragmentManager().findFragmentByTag("UPLOAD");
        CropImage.startPickImageActivity(getContext(),fragment);
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
        String key= UUID.randomUUID().toString();
        url+=key;

        ApprovedPostObject unApprovedDataObject=new ApprovedPostObject(Imageurl,key,mAuth.getCurrentUser().getDisplayName(),mAuth.getUid(),new GregorianCalendar().getTimeInMillis());

        FirebaseFirestore.getInstance().collection("posts").document(key).set(unApprovedDataObject).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {

                }

            }
        });

    }

}