package com.example.pictopz.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pictopz.R;
import com.example.pictopz.ShowContest;

public class ContestAdapter extends RecyclerView.Adapter<ContestAdapter.MyViewHolder> {

Context context;
    public ContestAdapter(Context context) {
        this.context=context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate item layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_contest_timer,parent,false);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, ShowContest.class));
            }
        });
        MyViewHolder vh=new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.category.setText("NATURE");

    }

    @Override
    public int getItemCount() {
        return 4;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView category;
        ImageView imageView;
        public MyViewHolder(View itemview){
            super(itemview);
            imageView=(ImageView)itemview.findViewById(R.id.image_contest_layout);
            category=(TextView)itemview.findViewById(R.id.contest_layout_category);

        }
    }
}
