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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ContestWInnersAdapter extends RecyclerView.Adapter<ContestWInnersAdapter.MyViewHolder> {

    ArrayList<ApprovedPostObject> winningPosts = new ArrayList<>();
    String contestID;
    Context context;
    public ContestWInnersAdapter(Context context,String contestID) {
        this.contestID = contestID;
        this.context=context;
        getWinningPhotos();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contest_winner_layout, parent, false);
        ContestWInnersAdapter.MyViewHolder vh = new ContestWInnersAdapter.MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.position.setText(winningPosts.get(position).position+"");
        holder.username.setText(winningPosts.get(position).userName);
        Glide.with(context)
                .load(winningPosts.get(position).imgURL)
                .into(holder.profileImage);
    }

    @Override
    public int getItemCount() {
        return winningPosts.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView username, position;
        ImageView profileImage;

        public MyViewHolder(View itemview) {
            super(itemview);
            username = (TextView) itemview.findViewById(R.id.contestWinner_name);
            profileImage = (ImageView) itemview.findViewById(R.id.contestWinner_image);
            position = (TextView) itemview.findViewById(R.id.contestWinner_position);

        }
    }

    private void getWinningPhotos() {
        FirebaseFirestore
                .getInstance()
                .collection("posts")
                .whereEqualTo("contestID",contestID)
                .whereGreaterThan("position", 0)
                .orderBy("position")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            winningPosts.clear();
                            for (QueryDocumentSnapshot querysnap : task.getResult()) {
                                ApprovedPostObject postObject = querysnap.toObject(ApprovedPostObject.class);
                                winningPosts.add(postObject);
                                notifyDataSetChanged();
                            }
                        }
                    }
                });
    }
}
