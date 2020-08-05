package com.release.political_facebook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.release.political_facebook.Adapter.savepostsAdapter;
import com.release.political_facebook.model.post;

import java.util.ArrayList;
import java.util.Collections;

public class saved_posts_fragement extends Fragment {


    private RecyclerView recyclerView;
    private savepostsAdapter adapter;
    private ArrayList<post> saved;
    private DatabaseReference reference;
    private String userId;
    ArrayList<String> postId;

    View view;
    public saved_posts_fragement() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.saved_posts,container,false);

        recyclerView = view.findViewById(R.id.recycler_saved);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        saved = new ArrayList<>();
        adapter = new savepostsAdapter(this,saved);
        recyclerView.setAdapter(adapter);

        reference = FirebaseDatabase.getInstance().getReference().child("Posts");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        checkSaved();

        return view;
    }
    public void checkSaved(){
        postId = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Saved posts").child(userId);
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                postId.add(snapshot.getKey());
                getSaved();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void getSaved(){


        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("Posts");

        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                saved.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    post p = dataSnapshot.getValue(post.class);
                    for(String id : postId){
                        if(p.getPost_id().equals(id)){
                            saved.add(p);
                        }
                    }

                }
                Collections.reverse(saved);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}
