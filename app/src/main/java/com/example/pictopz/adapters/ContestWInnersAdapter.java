package com.example.pictopz.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pictopz.R;

public class ContestWInnersAdapter extends RecyclerView.Adapter<ContestWInnersAdapter.MyViewHolder> {

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contest_winner_layout,parent,false);
        ContestWInnersAdapter.MyViewHolder vh=new ContestWInnersAdapter.MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView username,position;
        ImageView profileImage;
        public MyViewHolder(View itemview){
            super(itemview);
            username=(TextView)itemview.findViewById(R.id.contestWinner_name);
            profileImage=(ImageView)itemview.findViewById(R.id.contestWinner_image);
            position=(TextView)itemview.findViewById(R.id.contestWinner_position);

        }
    }
}
