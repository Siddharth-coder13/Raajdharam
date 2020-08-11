package com.release.political_facebook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.release.political_facebook.Adapter.my_postsAdapter;
import com.release.political_facebook.model.post;
import com.release.political_facebook.model.userModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class My_posts extends Fragment {

    private RecyclerView recyclerView;
    private my_postsAdapter adapter;
    private ArrayList<post> my_posts;
    private Toolbar toolbar;
    private DatabaseReference reference;
    private String userId;

    private TextView username;
    private ImageView profile_pic;
    private TextView followers;
    private TextView following;
    private int nFollowers;
    private int nFollowing;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_my_posts,container,false);

        toolbar = view.findViewById(R.id.toolbar_myPost);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        username = view.findViewById(R.id.name_text);
        profile_pic = view.findViewById(R.id.user_profile_pic);
        followers = view.findViewById(R.id.followers_text);
        following = view.findViewById(R.id.following_text);

        recyclerView = view.findViewById(R.id.recyclerView_myPosts);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        my_posts = new ArrayList<>();
        adapter = new my_postsAdapter(getContext(),my_posts);
        recyclerView.setAdapter(adapter);

        Intent i = getActivity().getIntent();
        userId = i.getStringExtra("UserId");

        User_info();
        getFollowers();
        getPosts();

        return view;
    }

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);

        toolbar = findViewById(R.id.toolbar_myPost);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        username = findViewById(R.id.name_text);
        profile_pic = findViewById(R.id.user_profile_pic);
        followers = findViewById(R.id.followers_text);
        following = findViewById(R.id.following_text);

        recyclerView = findViewById(R.id.recyclerView_myPosts);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        my_posts = new ArrayList<>();
        adapter = new my_postsAdapter(this,my_posts);
        recyclerView.setAdapter(adapter);

        reference = FirebaseDatabase.getInstance().getReference().child("Posts");
        /*FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        Intent i = getIntent();
        userId = i.getStringExtra("UserId");

        User_info();
        getFollowers();
        getPosts();

    }*/

    public void User_info(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userModel user_info = snapshot.getValue(userModel.class);

                if(user_info != null){
                    username.setText(user_info.getName());
                    Glide.with(My_posts.this).load(user_info.getImageUrl()).into(profile_pic);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowers(){
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("Follow").child(userId).child("Followers");

        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nFollowers = (int) snapshot.getChildrenCount();
                followers.setText(String.valueOf(nFollowers));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference().child("Follow").child(userId).child("Following");
        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nFollowing = (int) snapshot.getChildrenCount();
                following.setText(String.valueOf(nFollowing));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getPosts(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                my_posts.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    post p = dataSnapshot.getValue(post.class);
                    if(p.getPublisher().equals(userId)){
                        my_posts.add(p);
                    }
                }
                Collections.reverse(my_posts);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
