package com.example.pictopz.ui.fragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
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
import com.example.pictopz.adapters.ContestWInnersAdapter;
import com.example.pictopz.firebase.MyCountDownTimer;
import com.example.pictopz.models.ContestObject;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.GregorianCalendar;

public class ShowOneContest extends Fragment {

    FirebaseAuth mAuth;
    ContestObject contestObject;
    int limitValue = 0;
    RecyclerView recyclerView;
    ContestWInnersAdapter contestWInnersAdapter;

    public ShowOneContest(ContestObject contestObject) {
        this.contestObject = contestObject;
    }

    ImageView imageView;
    TextView timerTextView, indicator,desc;
    Button upload;
    TextView limit;

    public ShowOneContest() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.activity_show_one_contest, container, false);

        imageView = root.findViewById(R.id.image_contest_layout);
        timerTextView = root.findViewById(R.id.contest_timer_one_contest);
        upload = root.findViewById(R.id.upload_image_for_contest);
        limit = root.findViewById(R.id.uplaod_limit_per_comp);
        indicator =root.findViewById(R.id.one_contest_indicator);
        desc = root.findViewById(R.id.one_contest_desc);
        recyclerView = root.findViewById(R.id.contest_winners_recycleView);
        contestWInnersAdapter = new ContestWInnersAdapter(getContext(),contestObject.contestID);
        recyclerView.setAdapter(contestWInnersAdapter);

        mAuth = FirebaseAuth.getInstance();
        fetchLimit();


        Glide.with(getContext())
                .load(contestObject.imageUrl)
//                .centerCrop()
                .into(imageView);


        setCounter();
        desc.setText(contestObject.contestDesc);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Participate participate = new Participate(contestObject.contestID, limitValue);
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = manager.beginTransaction();
                fragmentTransaction
                        .replace(R.id.fragment_container, participate, "PARTICIPATE")
                        .addToBackStack("ONE CONTEST")
                        .commit();
            }
        });


        return root;
    }


    private void fetchLimit() {
        upload.setEnabled(false);
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("limits/" + contestObject.contestID + "/" + mAuth.getUid());
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    limitValue = snapshot.getValue(Integer.class);
                    limit.setText(limitValue + "/5");

                    if (limitValue < 5)
                        timeCheck();
                }else {
                    timeCheck();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Check Internet Connectivity", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void timeCheck() {
        GregorianCalendar currentTime = new GregorianCalendar();
        upload.setEnabled(currentTime.getTimeInMillis() >= contestObject.timeStart && currentTime.getTimeInMillis() <= contestObject.timeEnd);
    }

    private void setCounter() {
        MyCountDownTimer counter;
        GregorianCalendar calendar = new GregorianCalendar();
        if (calendar.getTimeInMillis() < contestObject.timeStart) {
            counter = new MyCountDownTimer(contestObject.timeStart, timerTextView);
            indicator.setText("CONTEST STARTS IN");
        } else if(calendar.getTimeInMillis()>contestObject.timeEnd) {
            counter = new MyCountDownTimer(contestObject.timeStart, timerTextView);
            indicator.setText("CONTEST ENDED");
        } else {
            counter = new MyCountDownTimer(contestObject.timeEnd, timerTextView);
            indicator.setText("CONTEST ENDS IN");
        }
        counter.start();

    }


}