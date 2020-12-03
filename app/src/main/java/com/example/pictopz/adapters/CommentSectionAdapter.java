package com.example.pictopz.adapters;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pictopz.R;

import java.util.ArrayList;
import java.util.HashMap;

public class CommentSectionAdapter extends RecyclerView.Adapter<CommentSectionAdapter.CommentViewHolder> {

    ArrayList<String> comment;

    public CommentSectionAdapter (ArrayList<String> comment){
        this.comment=comment;
    }

    @NonNull
    @Override
    public CommentSectionAdapter.CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_layout,parent,false);
        CommentSectionAdapter.CommentViewHolder vh=new CommentSectionAdapter.CommentViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentSectionAdapter.CommentViewHolder holder, int position) {
        holder.comment_tv.setText(Html.fromHtml("<b>"+getUsername(comment.get(position),true)+"</b> : "+getUsername(comment.get(position),false)));
    }

    @Override
    public int getItemCount() {
        return comment.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder{
        TextView comment_tv;
        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            comment_tv=itemView.findViewById(R.id.comment_in_http);
        }
    }

    private String getUsername(String username,boolean needUsername){
        String output="";
        if(needUsername) output=username.split("/")[0];
        else output=username.split("/")[1];
        return output;
    }
}
