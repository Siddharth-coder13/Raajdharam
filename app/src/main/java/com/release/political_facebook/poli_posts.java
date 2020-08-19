package com.release.political_facebook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.release.political_facebook.Adapter.poli_postAdapter;
import com.release.political_facebook.model.post;
import com.release.political_facebook.model.userModel;

import java.util.ArrayList;

public class poli_posts extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<post> public_posts;
    private poli_postAdapter adapter;
    private ArrayList<String> followingList;
    private ArrayList<String> accountList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.politician_posts,container,false);

        recyclerView = view.findViewById(R.id.recyclerView_politicians);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        public_posts = new ArrayList<>();
        adapter = new poli_postAdapter(this,public_posts);
        recyclerView.setAdapter(adapter);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            CheckProfile();
        }
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        public_posts.clear();
    }

    private void CheckFollowing(){
        followingList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(FirebaseAuth.getInstance()
                .getCurrentUser().getUid()).child("Following");


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followingList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    followingList.add(dataSnapshot.getKey());

                }

                attachDatabaseReadListener();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void attachDatabaseReadListener() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String mUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                public_posts.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    post m = dataSnapshot.getValue(post.class);
                    for (String ac : accountList) {
                        if(m.getPublisher().equals(ac)){
                            for (String id : followingList) {
                                if (m.getPublisher().equals(id)) {
                                    public_posts.add(m);
                                }
                            }
                            if (m.getPublisher().equals(mUid)) {
                                public_posts.add(m);
                            }
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void CheckProfile(){
        accountList = new ArrayList<>();

        Query query = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("account").equalTo("Politician");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                accountList.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    userModel m = dataSnapshot.getValue(userModel.class);
                    accountList.add(m.getUser_id());
                }
                CheckFollowing();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
