package com.example.pictopz;

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
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pictopz.firebase.FirebaseUploadImage;
import com.example.pictopz.models.ApprovedPostObject;
import com.example.pictopz.models.StoryObject;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class StoryPhotoUpload extends Fragment {

    FirebaseAuth mAuth;
    FirebaseUploadImage imgUploader;
    ImageView imageView;
    Button uploadPost, uploadStory;
    Uri filePath;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root= inflater.inflate(R.layout.fragment_story_photo_upload, container, false);
        mAuth=FirebaseAuth.getInstance();
        imageView=root.findViewById(R.id.upload_image_as_user);
        uploadPost =root.findViewById(R.id.upload_photo_button);
        uploadStory =root.findViewById(R.id.upload_story_button);
        changeActionBar();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "clicked", Toast.LENGTH_SHORT).show();
                checkpermission();
            }
        });

        uploadPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(filePath!=null){
                    uploadImage();
                }else {
                    Toast.makeText(getContext(), "Please select an Image to Upload", Toast.LENGTH_SHORT).show();
                }
            }
        });

        uploadStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(filePath!=null){
                    uploadStory();
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

    public void pickimage()
    {
        Fragment fragment=getParentFragmentManager().findFragmentByTag("UPLOAD");
        Log.i("FRAMGNET INFO",fragment.getTag()+"ok");
        CropImage.startPickImageActivity(getContext(),fragment);
    }

    public void uploadImage(){
        imgUploader =new FirebaseUploadImage(getContext(),filePath,"images") {
            @Override
            public void getUrl(String url) {
                //After getting URL of Post Image
                String key= UUID.randomUUID().toString();

                ApprovedPostObject object=new ApprovedPostObject(url,key,mAuth.getCurrentUser().getDisplayName(),mAuth.getUid(),new GregorianCalendar().getTimeInMillis());

                FirebaseFirestore.getInstance().collection("posts").document(key).set(object).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //Photo Uploaded Successfully
                        }
                    }
                });
            }
        };

        imgUploader.start();
    }


    private void uploadStory(){
        imgUploader =new FirebaseUploadImage(getContext(),filePath,"story") {
            @Override
            public void getUrl(String url) {
                //After getting URL of Story Image
                String key= UUID.randomUUID().toString();

                StoryObject object=new StoryObject(url,key,new GregorianCalendar().getTimeInMillis(),mAuth.getUid());

                FirebaseFirestore.getInstance().collection("story").document(key).set(object).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            //Story uploaded successfully
                        }
                    }
                });
            }
        };

        imgUploader.start();

    }

    public void croprequest(Uri imageURL)
    {
        Fragment fragment=getParentFragmentManager().findFragmentByTag("UPLOAD");

        CropImage.activity(imageURL)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(getContext(),fragment);
    }

    private void changeActionBar(){
        TextView view=getActivity().findViewById(R.id.textView4);
        ImageView imageView =getActivity().findViewById(R.id.open_drawer);
        imageView.setImageResource(R.drawable.menu_icon);
        view.setGravity(Gravity.CENTER);
        view.setText(Html.fromHtml("UPLOAD<b> STORY</b>"));
    }

}