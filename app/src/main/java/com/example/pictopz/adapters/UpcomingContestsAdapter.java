package com.example.pictopz.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.util.Log;
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
import com.example.pictopz.firebase.MyCountDownTimer;
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

    GregorianCalendar calendar = new GregorianCalendar();
    Context context;
    FragmentActivity activity;
    ArrayList<ContestObject> arrayList;
    float width;

    public UpcomingContestsAdapter(Context context, ArrayList<ContestObject> arrayList, FragmentActivity activity) {
        this.context = context;
        this.arrayList = arrayList;
        this.activity = activity;
        DisplayMetrics displayMetrics= context.getResources().getDisplayMetrics();
        width=displayMetrics.widthPixels;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate item layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_contest_timer, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.category.setText(arrayList.get(position).category);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowOneContest showOneContest = new ShowOneContest(arrayList.get(position));
                FragmentManager manager = activity.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = manager.beginTransaction();
                fragmentTransaction
                        .replace(R.id.fragment_container, showOneContest)
                        .addToBackStack("UPCOMING CONTEST")
                        .commit();
            }
        });
        holder.imageView.getLayoutParams().height=(int) (width/2.5);

        Glide
                .with(context)
                .load(arrayList.get(position).imageUrl)
//                .centerCrop()
                .into(holder.imageView);
        MyCountDownTimer counter;
        if (calendar.getTimeInMillis() < arrayList.get(position).timeStart) {
           counter = new MyCountDownTimer(arrayList.get(position).timeStart, holder.timer);
           holder.indicator.setText("CONTEST STARTS IN");
           holder.indicator.setTextColor(Color.parseColor("#06F10F"));
           holder.indicator.setBackgroundColor(Color.parseColor("#8D000000"));
        }else {
            counter = new MyCountDownTimer((arrayList.get(position).timeEnd),holder.timer);
            holder.indicator.setText("CONTEST ENDS IN");
            holder.indicator.setTextColor(Color.parseColor("#F30B0B"));
        }
        counter.start();
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView category, timer, indicator;
        ImageView imageView;

        public MyViewHolder(View itemview) {
            super(itemview);
            imageView = (ImageView) itemview.findViewById(R.id.image_contest_layout);
            category = (TextView) itemview.findViewById(R.id.contest_layout_category);
            timer = (TextView) itemview.findViewById(R.id.contest_timer_textView);
            indicator = (TextView) itemview.findViewById(R.id.contest_start_end_indicator);
        }
    }


}
