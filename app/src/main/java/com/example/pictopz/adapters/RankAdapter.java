package com.example.pictopz.adapters;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pictopz.R;
import com.example.pictopz.models.GiftObject;

import java.util.ArrayList;

public class RankAdapter extends RecyclerView.Adapter<RankAdapter.MyViewHolder> {

    ArrayList<GiftObject> ranking;
    Context context;

    public RankAdapter(ArrayList<GiftObject> ranking,Context context) {
        this.ranking = ranking;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rank_layout,parent,false);
        RankAdapter.MyViewHolder vh=new RankAdapter.MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (ranking.isEmpty())
        {
            Toast.makeText(context, "list is empty", Toast.LENGTH_SHORT).show();
        }
        holder.pos.setText(String.valueOf(position+1));
        holder.ranking.setText(ranking.get(position).giftName);
        Glide.with(context).load(ranking.get(position).giftURL).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return ranking.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView ranking,pos;
        ImageView imageView;
        public MyViewHolder(View itemview){
            super(itemview);
            pos=(TextView)itemview.findViewById(R.id.pos);
            imageView=(ImageView)itemview.findViewById(R.id.rank_layout_image);
            ranking=(TextView)itemview.findViewById(R.id.rank_layout_text);

        }
    }
}
