package com.example.pictopz.adapters;

import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pictopz.R;

import java.util.ArrayList;

public class RankAdapter extends RecyclerView.Adapter<RankAdapter.MyViewHolder> {

    ArrayList<String> ranking;
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ranking=new ArrayList<>();
        ranking.add("st");  ranking.add("nd");
        ranking.add("rd");  ranking.add("th");
        ranking.add("th");  ranking.add("th");
        ranking.add("th");
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rank_layout,parent,false);
        RankAdapter.MyViewHolder vh=new RankAdapter.MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.ranking.setText(String.format(""+(int)(position+1)));
        holder.subs.setText(ranking.get(position));


    }

    @Override
    public int getItemCount() {
        return 7;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView ranking,subs;
        ImageView imageView;
        public MyViewHolder(View itemview){
            super(itemview);
            subs=(TextView)itemview.findViewById(R.id.rank_layout_rank_subscript);
            imageView=(ImageView)itemview.findViewById(R.id.rank_layout_image);
            ranking=(TextView)itemview.findViewById(R.id.rank_layout_text);

        }
    }
}
