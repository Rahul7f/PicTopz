package com.example.pictopz.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pictopz.R;
import com.example.pictopz.ui.activity.DrawerActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailVerification extends AppCompatActivity {

    FirebaseAuth mAuth=FirebaseAuth.getInstance();
    FirebaseUser user;

    Button verify_email_button,signOut;
    TextView verification_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        user = mAuth.getCurrentUser();

        timer.start();

        verify_email_button=findViewById(R.id.verify_email_button);
        signOut=findViewById(R.id.signOut_non_verified);
        verification_status=findViewById(R.id.verification_status);


        verify_email_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.reload();
                verify();
            }
        });
    }

    private void verify(){
        verify_email_button.setEnabled(false);
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        // Re-enable button
                        verify_email_button.setEnabled(true);

                        if (task.isSuccessful()) {
                            Toast.makeText(EmailVerification.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                            verification_status.setText("Verification Email Sent to "+user.getEmail());
                            verify_email_button.setText("Resend");

                        } else {
                            Log.e("EMAILVERIFICATION", "sendEmailVerification", task.getException());
                            Toast.makeText(EmailVerification.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    CountDownTimer timer=new CountDownTimer(300000,2000) {
        @Override
        public void onTick(long l) {
            user.reload();
            if(user==null){
                timer.cancel();
                startActivity(new Intent(EmailVerification.this, SignUp.class));
                finish();
            } else if(user.isEmailVerified()){
                timer.cancel();
                startActivity(new Intent(EmailVerification.this, DrawerActivity.class));
                finish();
            }
        }

        @Override
        public void onFinish() {

        }
    };
}