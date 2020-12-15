package com.example.pictopz.ui.fragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.pictopz.R;
import com.example.pictopz.firebase.MyCountDownTimer;
import com.example.pictopz.models.ContestObject;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class ShowOneContest extends Fragment {

    FirebaseAuth mAuth;
    ContestObject contestObject;
    int limitValue=0;
    public ShowOneContest(ContestObject contestObject){
        this.contestObject =contestObject;
    }

    ImageView imageView;
    TextView textView;
    Button upload;
    TextView limit;

    public ShowOneContest() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.activity_show_one_contest, container, false);

        imageView=root.findViewById(R.id.image_contest_layout);
        textView=root.findViewById(R.id.contest_timer_one_contest);
        upload=root.findViewById(R.id.upload_image_for_contest);
        limit=root.findViewById(R.id.uplaod_limit_per_comp);


        mAuth=FirebaseAuth.getInstance();
        fetchLimit();

        Glide.with(getContext())
                .load(contestObject.imageUrl)
//                .centerCrop()
                .into(imageView);



        MyCountDownTimer myCount=new MyCountDownTimer(contestObject.timeStart,textView) ;
        myCount.start();

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Participate participate=new Participate(contestObject.contestID,limitValue);
                FragmentManager manager=getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction= manager.beginTransaction();
                fragmentTransaction
                        .replace(R.id.fragment_container,participate,"PARTICIPATE")
                        .addToBackStack("ONE CONTEST")
                        .commit();
            }
        });


        return root;
        }


    private void fetchLimit(){
        upload.setEnabled(false);
        DatabaseReference dbRef=FirebaseDatabase.getInstance().getReference("limits/"+contestObject.contestID+"/"+mAuth.getUid());
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    limitValue=snapshot.getValue(Integer.class);
                    limit.setText(limitValue+"/5");

                    if(limitValue<5)
                        upload.setEnabled(true);
                }else
                    upload.setEnabled(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Check Internet Connectivity", Toast.LENGTH_SHORT).show();
            }
        });
    }


}