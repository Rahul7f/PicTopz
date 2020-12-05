package com.example.pictopz.unused;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pictopz.R;

public class ActivityPhoneAuthBinding {
    static Button buttonStartVerification,buttonVerifyPhone,buttonResend,signOutButton;
    static EditText fieldPhoneNumber,fieldVerificationCode;
    static TextView detail,status;
    static LinearLayout phoneAuthFields,signedInButtons;
    static LayoutInflater inflater;

    public static ActivityPhoneAuthBinding inflate(LayoutInflater inflater2){
        inflater=inflater2;
        return new ActivityPhoneAuthBinding();
    }

    public static View getRoot(){
        View root=inflater.inflate(R.layout.activity_phone_auth,null);

        buttonStartVerification =root.findViewById(R.id.buttonStartVerification);
        buttonVerifyPhone=root.findViewById(R.id.buttonVerifyPhone);
        buttonResend=root.findViewById(R.id.buttonResend);
        signOutButton=root.findViewById(R.id.signOutButton);

        fieldPhoneNumber=root.findViewById(R.id.fieldPhoneNumber);
        fieldVerificationCode=root.findViewById(R.id.fieldVerificationCode);

        detail=root.findViewById(R.id.detail);
        status=root.findViewById(R.id.status);

        phoneAuthFields=root.findViewById(R.id.phoneAuthFields);
        signedInButtons=root.findViewById(R.id.signedInButtons);

        return root;
    }
}
