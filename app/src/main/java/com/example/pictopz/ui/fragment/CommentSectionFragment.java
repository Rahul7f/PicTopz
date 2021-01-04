package com.example.pictopz.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pictopz.R;
import com.example.pictopz.adapters.CommentSectionAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class CommentSectionFragment extends Fragment {


    String postID;
    EditText comment;
    Button post_comment;
    FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
    ArrayList<String> comment_data=new ArrayList<>();
    public CommentSectionFragment(String postID) {
        // Required empty public constructor
        this.postID=postID;
    }
    String username;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root=inflater.inflate(R.layout.fragment_comment_section,container,false);

        comment=root.findViewById(R.id.your_comment_here);
        post_comment=root.findViewById(R.id.click_to_post_comment);
        RecyclerView recyclerView=root.findViewById(R.id.comments_recycleView);


        CommentSectionAdapter adapter=new CommentSectionAdapter(comment_data);
        recyclerView.setAdapter(adapter);


        post_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String postcomment = comment.getText().toString();
                if(postcomment.trim().isEmpty()){
                    Toast.makeText(getContext(), "enter comment", Toast.LENGTH_SHORT).show();
                }
                else 
                {
                    String data=comment.getText().toString();
                    String username=user.getDisplayName().split("/")[0];
                    DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference("/comments/"+postID+"/"+username);
                    commentsRef.setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            comment_data.add(username+"/"+data);
                            comment.setText("");
                        }
                    });

                }
            }
        });

        //fetch comments
        Query query=FirebaseDatabase.getInstance().getReference("/comments/").child(postID).limitToFirst(10);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    comment_data.add(snapshot1.getKey()+"/"+snapshot1.getValue(String.class));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return root;
    }


}