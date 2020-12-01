package com.example.pictopz.ui.fragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.pictopz.R;
import com.example.pictopz.models.ContestObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class ShowOneContest extends Fragment {

    GregorianCalendar calendar= new GregorianCalendar();
    String oldTime = calendar.get(GregorianCalendar.DAY_OF_MONTH)+"."+calendar.get(GregorianCalendar.MONTH)+"."+calendar.get(GregorianCalendar.YEAR)+", "+calendar.get(GregorianCalendar.HOUR)+":"+calendar.get(GregorianCalendar.MINUTE)+":"+calendar.get(Calendar.SECOND);


    ContestObject contestObject;
    public ShowOneContest(ContestObject contestObject){
        this.contestObject =contestObject;
    }

    ImageView imageView;
    TextView textView;
    Button upload;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.activity_show_one_contest, container, false);

        imageView=root.findViewById(R.id.image_contest_layout);
        textView=root.findViewById(R.id.contest_timer_one_contest);
        upload=root.findViewById(R.id.upload_image_for_contest);

        HashMap<String,String> ok;

        Glide.with(getContext())
                .load(contestObject.imageUrl)
                .centerCrop()
                .into(imageView);

        MyCount myCount=new MyCount(convertDate(contestObject.time),1000) {
            @Override
            public void changeTime(String hms) {
                textView.setText(hms);
            }
        };
        myCount.start();

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Participate participate=new Participate(contestObject.contestID);
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

    public abstract class MyCount extends CountDownTimer {
        MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {

        }

        @Override
        public void onTick(long millisUntilFinished) {
            long millis = millisUntilFinished;
            String hms = (TimeUnit.MILLISECONDS.toDays(millis)) + ":"
                    + (TimeUnit.MILLISECONDS.toHours(millis) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(millis)) + ":")
                    + (TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)) + ":"
                    + (TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))));
            changeTime(hms);
        }
        public abstract void changeTime(String hms);
    }

    public long convertDate(Long NewLong){
        Long diff=0l;
        Long oldLong=0l;



        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy, HH:mm:ss");

        Date oldDate, newDate;

        try {
            oldDate = formatter.parse(oldTime);
            oldLong = oldDate.getTime();
            diff = NewLong - oldLong;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return diff;
    }


}