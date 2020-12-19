package com.example.pictopz.adapters;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pictopz.R;
import com.example.pictopz.firebase.MyCountDownTimer;
import com.example.pictopz.models.ContestObject;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.GregorianCalendar;


public class ProfileContestAdapter extends RecyclerView.Adapter<ProfileContestAdapter.MyViewHolder> {

    ArrayList<ContestObject> contestObjects=new ArrayList<>();
    Context context;

    public ProfileContestAdapter(Context context) {
        this.context = context;
        getContest();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_contest_layout,parent,false);
        ProfileContestAdapter.MyViewHolder vh=new ProfileContestAdapter.MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MyCountDownTimer timer=new MyCountDownTimer(contestObjects.get(position).timeStart,holder.profile_contest_startTime);
        timer.start();
        holder.profile_contest_name.setText(contestObjects.get(position).category);
        holder.profile_contest_desc.setText(contestObjects.get(position).contestDesc+"...");
        Glide.with(context)
                .load(contestObjects.get(position).imageUrl)
                .into(holder.profile_contest_image);
    }

    @Override
    public int getItemCount() {
        return contestObjects.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
       TextView profile_contest_name,profile_contest_desc,profile_contest_startTime;
       ImageView profile_contest_image;

        public MyViewHolder(View itemview){
            super(itemview);
            profile_contest_image=itemview.findViewById(R.id.profile_contest_image);
            profile_contest_desc=itemview.findViewById(R.id.profile_contest_description);
            profile_contest_name=itemview.findViewById(R.id.profile_contest_name);
            profile_contest_startTime=itemview.findViewById(R.id.profile_contest_startTime);
        }
    }

    private void getContest(){
        GregorianCalendar calendar=new GregorianCalendar();
        FirebaseFirestore
                .getInstance()
                .collection("contests")
                .whereGreaterThan("timeStart",calendar.getTimeInMillis())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            contestObjects.clear();
                            for(QueryDocumentSnapshot object:task.getResult()){
                                contestObjects.add(object.toObject(ContestObject.class));
                                notifyDataSetChanged();
                            }
                        }
                    }
                });
    }
}

