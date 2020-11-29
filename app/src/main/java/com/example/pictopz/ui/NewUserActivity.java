package com.example.pictopz.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

import com.example.pictopz.R;

public class NewUserActivity extends AppCompatActivity {

    EditText phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        String number=getIntent().getStringExtra("NUMBER");

        phoneNumber =findViewById(R.id.phone_number_edittext);
        phoneNumber.setText(number);
        phoneNumber.setEnabled(false);


    }
}