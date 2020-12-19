package com.example.pictopz.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pictopz.R;
import com.example.pictopz.models.ApprovedPostObject;
import com.example.pictopz.models.ContestObject;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class WinRecordAdapter extends RecyclerView.Adapter<WinRecordAdapter.MyViewHolder> {

    Context context;
    String UID;
    ArrayList<ApprovedPostObject> winningPosts=new ArrayList<>();
    HashMap<String,String> contestName=new HashMap<>();
    public WinRecordAdapter(Context context, String UID) {
        this.context = context;
        this.UID = UID;
        getWinningPhotos();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.win_record_layout,parent,false);
        WinRecordAdapter.MyViewHolder vh=new WinRecordAdapter.MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.likeNO.setText(winningPosts.get(position).likesNo+"");
        holder.position.setText(winningPosts.get(position).position+"");
        holder.contestName.setText(contestName.get(winningPosts.get(position).contestID));
        Glide.with(context)
                .load(winningPosts.get(position).imgURL)
                .into(holder.winPhoto);
    }

    @Override
    public int getItemCount() {
        return winningPosts.size();
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

    private void getWinningPhotos(){
        FirebaseFirestore
                .getInstance()
                .collection("posts")
                .whereEqualTo("userUID",UID)
                .whereGreaterThan("position",0)
                .orderBy("position")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            winningPosts.clear();
                            for(QueryDocumentSnapshot querysnap:task.getResult()){
                                ApprovedPostObject postObject=querysnap.toObject(ApprovedPostObject.class);
                                winningPosts.add(postObject);
                                getContestName(postObject.contestID);
                                notifyDataSetChanged();
                            }
                        }
                    }
                });
    }

    private void getContestName(String id){
        FirebaseFirestore
                .getInstance()
                .collection("contests")
                .whereEqualTo("contestID",id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot queryDocument:task.getResult()){
                                contestName.put(id,queryDocument.toObject(ContestObject.class).category);
                                notifyDataSetChanged();
                            }
                        }
                    }
                });
    }
}
