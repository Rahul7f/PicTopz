package com.example.pictopz.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pictopz.R;

public class WinRecordAdapter extends RecyclerView.Adapter<WinRecordAdapter.MyViewHolder> {

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.win_record_layout,parent,false);
        WinRecordAdapter.MyViewHolder vh=new WinRecordAdapter.MyViewHolder(v);
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
        TextView position,contestName,likeNO;
        ImageView winPhoto;

        public MyViewHolder(View itemview){
            super(itemview);
            position=(TextView)itemview.findViewById(R.id.win_position);
            winPhoto=(ImageView)itemview.findViewById(R.id.winPhoto);
            contestName=(TextView)itemview.findViewById(R.id.win_contest);
            likeNO=(TextView)itemview.findViewById(R.id.win_likes_no);



        }
    }
}
