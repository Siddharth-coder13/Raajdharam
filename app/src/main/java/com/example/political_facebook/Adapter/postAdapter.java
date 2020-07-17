package com.example.political_facebook.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.political_facebook.CommentActivity;
import com.example.political_facebook.R;
import com.example.political_facebook.model.post;
import com.example.political_facebook.ui.home.HomeFragment;

import java.util.ArrayList;
import java.util.HashMap;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class postAdapter extends RecyclerView.Adapter<viewHolder> {

    private ImageView imageView;
    private TextView textView;
    private ImageView like;
    private ImageView dislike;
    private int nLike;


    HomeFragment c ;
    ArrayList<post> posts;

    public postAdapter(HomeFragment c, ArrayList<post> posts) {
        this.c = c;
        this.posts = posts;
    }


    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.template,null);
        imageView = (ImageView) parent.findViewById(R.id.image_post);
        textView = (TextView) parent.findViewById(R.id.text_post);

        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewHolder holder, final int i) {

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        holder.text_post.setText(posts.get(i).getText_post());
        holder.user_name.setText(posts.get(i).getUser_name());
        Glide.with(c).load(posts.get(i).getProfile_pic()).into(holder.profile_pic);
        holder.date_time.setText(posts.get(i).getDate_time());

        boolean isPhoto = posts.get(i).getImage_post() != null;

        isLike(posts.get(i).getPost_id(),holder.dislike);
        nrLikes(holder.likes, posts.get(i).getPost_id());
        create_leaderBoards(posts.get(i).getPost_id(), nLike);

        holder.dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.dislike.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(posts.get(i).getPost_id())
                            .child(user.getUid()).setValue(true);
                }
                else {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(posts.get(i).getPost_id())
                            .child(user.getUid()).removeValue();
                }
            }
        });



        if(isPhoto) {
            imageView.setVisibility(View.VISIBLE);
            textView.setTextSize(15);
            Glide.with(imageView.getContext())
                    .load(posts.get(i).getImage_post())
                    .into(imageView);
        }else{
            //imageView.setVisibility(View.GONE);
        }

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(c.getContext(), CommentActivity.class);
                intent.putExtra("postid",posts.get(i).getPost_id());
                intent.putExtra("publisherid",posts.get(i).getPublisher());
                c.getContext().startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    private void isLike(String post_id, final ImageView imageView){

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes")
                .child(post_id);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(user.getUid()).exists()){
                    imageView.setImageResource(R.drawable.fullstar);
                    imageView.setTag("liked");
                }
                else{
                    imageView.setImageResource(R.drawable.star_border);
                    imageView.setTag("like");
                }
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
                likes.setText(nLike+ "likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void create_leaderBoards(String post_id, int likes){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("leaderBoards").child(post_id);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("post_id",post_id);
        hashMap.put("likes",likes);
        reference.setValue(hashMap);
    }

}
