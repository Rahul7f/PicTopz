package com.example.pictopz.adapters;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pictopz.R;
import com.example.pictopz.ui.fragment.ShowOneContest;
import com.example.pictopz.models.ContestObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

public class UpcomingContestsAdapter extends RecyclerView.Adapter<UpcomingContestsAdapter.MyViewHolder> {

    Context context;
    FragmentActivity activity;
    GregorianCalendar calendar= new GregorianCalendar();
    String oldTime = calendar.get(GregorianCalendar.DAY_OF_MONTH)+"."+calendar.get(GregorianCalendar.MONTH)+"."+calendar.get(GregorianCalendar.YEAR)+", "+calendar.get(GregorianCalendar.HOUR)+":"+calendar.get(GregorianCalendar.MINUTE)+":"+calendar.get(Calendar.SECOND);

    ArrayList<ContestObject> arrayList;

    public UpcomingContestsAdapter(Context context, ArrayList<ContestObject> arrayList, FragmentActivity activity) {
        this.context=context;
        this.arrayList=arrayList;
        this.activity=activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate item layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_contest_timer,parent,false);
        MyViewHolder vh=new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.category.setText(arrayList.get(position).category);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowOneContest showOneContest=new ShowOneContest(arrayList.get(position));
                FragmentManager manager=activity.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction= manager.beginTransaction();
                fragmentTransaction
                        .replace(R.id.fragment_container,showOneContest)
                        .addToBackStack("UPCOMING CONTEST")
                        .commit();
            }
        });

        Glide
                .with(context)
                .load(arrayList.get(position).imageUrl)
//                .centerCrop()
                .into(holder.imageView);
        MyCount counter=new MyCount(convertDate(arrayList.get(position).timeStart),1000) {
            @Override
            public void changeTime(String hms) {
                holder.timer.setText(hms);
            }
        };
        counter.start();
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView category,timer;
        ImageView imageView;
        public MyViewHolder(View itemview){
            super(itemview);
            imageView=(ImageView)itemview.findViewById(R.id.image_contest_layout);
            category=(TextView)itemview.findViewById(R.id.contest_layout_category);
            timer=(TextView)itemview.findViewById(R.id.contest_timer_textView);

        }
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
