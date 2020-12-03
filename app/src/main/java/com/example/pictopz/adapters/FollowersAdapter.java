package com.example.pictopz.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pictopz.R;
import com.example.pictopz.models.FollowersObject;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FollowersAdapter extends RecyclerView.Adapter<FollowersAdapter.MyView> {
    ArrayList<FollowersObject> arrayList;
    Context context;
    String currentUser;
    String path;


    public FollowersAdapter(ArrayList<FollowersObject> arrayList, Context context,String currentUser,String path) {
        this.arrayList = arrayList;
        this.context = context;
        this.currentUser = currentUser;
        this.path = path;
    }

    @NonNull
    @Override
    public MyView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.followers_layout,parent,false);
        FollowersAdapter.MyView vh=new FollowersAdapter.MyView(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyView holder, int position) {
        holder.followerName.setText(arrayList.get(position).followerUserName);
        holder.removeFollower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(path+currentUser+"/"+arrayList.get(position).followerUID);
                databaseReference.removeValue();
                notifyDataSetChanged();
                notifyItemRemoved(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyView extends RecyclerView.ViewHolder {
        ImageView followerProfile;
        TextView followerName;
        Button removeFollower;
        public MyView(@NonNull View itemView) {
            super(itemView);
            followerProfile=(ImageView)itemView.findViewById(R.id.follower_profile);
            removeFollower=(Button) itemView.findViewById(R.id.removeFollower_btn);
            followerName=(TextView) itemView.findViewById(R.id.follower_username);
        }
    }
}
