package com.release.political_facebook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.release.political_facebook.model.post;


public class Post extends AppCompatActivity {

    /*private RecyclerView recyclerView;
    private postAdapter adapter;
    private ArrayList<post> postlist;*/
    private String postId;
    private ImageView image_post;
    private TextView text_post;
    private TextView heading;
    private TextView likes;
    private TextView dislikes;
    private int nLike;
    private int nDisLike;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        /*recyclerView = findViewById(R.id.recycler_post);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        postlist = new ArrayList<>();
        adapter = new postAdapter(this,postlist);
        recyclerView.setAdapter(adapter);*/

        SharedPreferences prefs = getSharedPreferences("PREFS",Context.MODE_PRIVATE);
        postId = prefs.getString("postid","none");

        image_post = findViewById(R.id.image_post);
        text_post = findViewById(R.id.text_post);
        heading = findViewById(R.id.heading);
        likes = findViewById(R.id.likes_no);
        dislikes = findViewById(R.id.dislike_no);

        getPosts();
        nrLikes(likes,postId);
        nrDislikes(dislikes,postId);

    }

    private void getPosts(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Posts").child(postId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                post p = snapshot.getValue(post.class);
                boolean isPhoto = p.getImage_post() != null;
                boolean isHeading = p.getHeading() !=null;
                if(isPhoto){
                    Glide.with(Post.this).load(p.getImage_post()).into(image_post);
                }
                if(isHeading){
                    heading.setVisibility(View.VISIBLE);
                    heading.setText(p.getHeading());
                }
                text_post.setText(p.getText_post());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void nrLikes(final TextView likes, String post_id){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(post_id);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nLike = (int) snapshot.getChildrenCount();
                likes.setText(String.valueOf(nLike));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void nrDislikes(final TextView dislikes, String post_id){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Dislikes").child(post_id);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nDisLike = (int) snapshot.getChildrenCount();
                dislikes.setText(String.valueOf(nDisLike));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
