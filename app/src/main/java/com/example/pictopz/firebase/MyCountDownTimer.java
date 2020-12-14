package com.example.pictopz.firebase;

import android.os.CountDownTimer;
import android.widget.TextView;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MyCountDownTimer extends CountDownTimer{
    TextView textView;
    public MyCountDownTimer(long millisInFuture, TextView view) {
        super(convertDate(millisInFuture), 1000);
        this.textView=view;
    }

    @Override
    public void onFinish() {

    }

    @Override
    public void onTick(long millisUntilFinished) {
        long millis = millisUntilFinished;
        String hms =String.format(Locale.getDefault(),"%02d:%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toDays(millis),
                (TimeUnit.MILLISECONDS.toHours(millis) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(millis))),
                (TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis))),
                (TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)))
        );
        changeTime(hms);
    }
    private void changeTime(String hms){
        textView.setText(hms);
    }

    private static long convertDate(long NewLong){
        long diff=0l;
        GregorianCalendar calendar=new GregorianCalendar();
        diff = NewLong - calendar.getTimeInMillis();
        return diff;
    }
}
