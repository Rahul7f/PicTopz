package com.example.pictopz.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.AppCompatTextView;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.pictopz.R;
import com.example.pictopz.firebase.FirebaseUploadData;
import com.example.pictopz.firebase.FirebaseUploadImage;
import com.example.pictopz.models.ContestObject;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class UploadContest extends AppCompatActivity  {

    AppCompatTextView timepickerTV,datepickerTV;
    AppCompatImageView imageView;
    AppCompatButton upload;
    FirebaseUploadImage imgUpload;
    AppCompatSpinner spinner;
    Uri filePath;
    int year,month,day,hour,min,sec;
    long diff;
    long oldLong;
    long NewLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_contest);

        timepickerTV=findViewById(R.id.time_picker_for_upload);
        datepickerTV=findViewById(R.id.date_picker_for_upload);
        imageView=findViewById(R.id.contes_image_to_upload);
        upload=findViewById(R.id.upload_contest_button);
        spinner=findViewById(R.id.upload_category_spinner);


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkpermission();
            }
        });


        GregorianCalendar calendar=new GregorianCalendar();

        final DatePickerDialog datePickerDialog=new DatePickerDialog(UploadContest.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                year=i;
                month=i1;
                day=i2;

                GregorianCalendar calendar= new GregorianCalendar();
                String oldTime = calendar.get(GregorianCalendar.DAY_OF_MONTH)+"."+calendar.get(GregorianCalendar.MONTH)+"."+calendar.get(GregorianCalendar.YEAR)+", "+calendar.get(GregorianCalendar.HOUR)+":"+calendar.get(GregorianCalendar.MINUTE);
                String newTime=day+"."+month+"."+year+", "+hour+":"+min;
                convertDate(oldTime,newTime);

            }
        },calendar.get(GregorianCalendar.YEAR),calendar.get(GregorianCalendar.MONTH),calendar.get(GregorianCalendar.DAY_OF_MONTH));



        final TimePickerDialog timePickerDialog=new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                hour=i;
                min=i1;
                sec=0;
                datePickerDialog.show();
            }
        },calendar.get(GregorianCalendar.HOUR_OF_DAY),calendar.get(GregorianCalendar.MINUTE),true);


        timepickerTV.setClickable(true);
        timepickerTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePickerDialog.show();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(filePath!=null)
                uploadImage(filePath);
                else
                    Toast.makeText(UploadContest.this, "Please select image", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public class MyCount extends CountDownTimer {
        MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            datepickerTV.setText("done!");
        }

        @Override
        public void onTick(long millisUntilFinished) {
            long millis = millisUntilFinished;
            String hms = (TimeUnit.MILLISECONDS.toDays(millis)) + ":"
                    + (TimeUnit.MILLISECONDS.toHours(millis) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(millis)) + ":")
                    + (TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)) + ":"
                    + (TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))));
            datepickerTV.setText(/*context.getString(R.string.ends_in) + " " +*/ hms);
        }
    }

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
            Toast.makeText(this, "This Permission is required", Toast.LENGTH_SHORT).show();
        }
    }

    public void pickimage()
    {
        CropImage.startPickImageActivity(UploadContest.this);
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
                    filePath=result.getUri();
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),result.getUri());
                    Toast.makeText(this, "image uri:- "+bitmap, Toast.LENGTH_SHORT).show();
                    imageView.setImageBitmap(bitmap);
                }catch (IOException e)
                {
                    e.printStackTrace();
                    Toast.makeText(this, "error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void croprequest(Uri imageURL)
    {
        CropImage.activity(imageURL)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
    }

    public void convertDate(String oldTime,String newTime){
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy, HH:mm");

        Date oldDate, newDate;
        try {
            oldDate = formatter.parse(oldTime);
            newDate = formatter.parse(newTime);
            oldLong = oldDate.getTime();
            NewLong = newDate.getTime();
            diff = NewLong - oldLong;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        MyCount counter = new MyCount(diff, 1000);
        counter.start();
    }
    // UploadImage method
    public void uploadImage(Uri filePath){
        imgUpload=new FirebaseUploadImage(UploadContest.this,filePath,"contest_images") {
            @Override
            public void getUrl(String url) {
                Log.e("UPLOAD IMAGE","UPLOAD COMPLETE");
                uploadData(url);
            }
        };
        Log.e("UPLOAD IMAGE","UPLOAD STARTED");
        imgUpload.start();
    }

    public void uploadData(String Imageurl){
        String url="/contests/";

        ContestObject contestObject=new ContestObject(Imageurl,spinner.getSelectedItem().toString(),NewLong,"demo");
        FirebaseUploadData uploadData=new FirebaseUploadData(UploadContest.this,url+"/"+UUID.randomUUID().toString(),contestObject) {
            @Override
            public void onSuccessfulUpload() {
                Toast.makeText(UploadContest.this, "Data Updated Successfully", Toast.LENGTH_SHORT).show();

            };
        };
        uploadData.start();
    }

}