package com.example.pictopz.firebase;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public abstract class FirebaseUploadData<E> {

    DatabaseReference database;
    Context context;
    String url;
    E object;
    public FirebaseUploadData(Context context,String url,E object){

        database=FirebaseDatabase.getInstance().getReference("/"+url+"/");
        this.context=context;
        this.url=url;
        this.object=object;

    }

    public void uploadData(){
        database.setValue(object).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //finished uploading
                    onSuccessfulUpload();
                }else {
                    //Not finished
                    Toast.makeText(context, "Error Uploading:"+task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public abstract void onSuccessfulUpload();
}
