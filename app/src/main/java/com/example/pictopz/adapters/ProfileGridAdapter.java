package com.example.pictopz.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pictopz.R;
import com.example.pictopz.models.UnApprovedDataObject;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.function.UnaryOperator;


public class ProfileGridAdapter extends RecyclerView.Adapter<ProfileGridAdapter.ProfileViewHolder> {

    Context context;
    ArrayList<UnApprovedDataObject> list=new ArrayList<>();

    public ProfileGridAdapter(Context context,String UID) {
        this.context = context;
        fetchData(UID);
    }


    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(context).inflate(R.layout.gride_layout,null,false);
        ProfileViewHolder holder=new ProfileViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        Glide.with(context)
                .load(list.get(position).imgURL)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }



    class ProfileViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        public ProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.icon);

        }
    }

    private void fetchData(String UID){
        CollectionReference reference=FirebaseFirestore.getInstance().collection("posts");
        reference.whereEqualTo("approved",true)
            .whereEqualTo("userUID",UID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    list.clear();
                    for (QueryDocumentSnapshot objects:task.getResult()){
                        UnApprovedDataObject object=objects.toObject(UnApprovedDataObject.class);
                        list.add(object);
                    }
                    notifyDataSetChanged();
                }
            }
        });


    }
}
