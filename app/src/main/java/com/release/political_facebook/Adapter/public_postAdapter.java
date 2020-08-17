package com.release.political_facebook.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.release.political_facebook.CommentActivity;
import com.release.political_facebook.R;
import com.release.political_facebook.model.post;
import com.release.political_facebook.model.userModel;
import com.release.political_facebook.public_posts;
import com.release.political_facebook.ui.home.HomeFragment;
import com.release.political_facebook.user_profile;

import java.util.ArrayList;
import java.util.HashMap;

public class public_postAdapter extends RecyclerView.Adapter<viewHolder> {

    private ImageView imageView;
    private TextView textView;
    private int nLike;
    private int nDisLike;
    private String userId;


    public_posts c ;
    ArrayList<post> posts;

    public public_postAdapter(public_posts c, ArrayList<post> posts) {
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

        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("Users").child(posts.get(i).getPublisher());
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userModel m = snapshot.getValue(userModel.class);
                String name = m.getName();
                String uri = m.getImageUrl();
                holder.user_name.setText(name);
                if(uri != null){
                    Glide.with(c).load(uri).into(holder.profile_pic);
                }else {
                    holder.profile_pic.setImageResource(R.drawable.account);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.text_post.setText(posts.get(i).getText_post());

        holder.date_time.setText(posts.get(i).getDate_time());

        boolean isPhoto = posts.get(i).getImage_post() != null;
        boolean isHeading = posts.get(i).getHeading() !=null;

        isLike(posts.get(i).getPost_id(),holder.dislike);
        nrLikes(holder.likes, posts.get(i).getPost_id());
        //create_leaderBoards(posts.get(i).getPost_id(), nLike);
        isDislike(posts.get(i).getPost_id(),holder.like);
        nrDislikes(holder.dislikes,posts.get(i).getPost_id());
        isSaved(posts.get(i).getPost_id(),holder.save, holder.saved);

        holder.dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.dislike.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(posts.get(i).getPost_id())
                            .child(user.getUid()).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Dislikes").child(posts.get(i).getPost_id())
                            .child(user.getUid()).removeValue();
                }
                else {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(posts.get(i).getPost_id())
                            .child(user.getUid()).removeValue();
                }
            }
        });

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.like.getTag().equals("dislike")){
                    FirebaseDatabase.getInstance().getReference().child("Dislikes").child(posts.get(i).getPost_id())
                            .child(user.getUid()).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(posts.get(i).getPost_id())
                            .child(user.getUid()).removeValue();
                    addNotification(posts.get(i).getPublisher(),posts.get(i).getPost_id());
                }
                else {
                    FirebaseDatabase.getInstance().getReference().child("Dislikes").child(posts.get(i).getPost_id())
                            .child(user.getUid()).removeValue();
                }
            }
        });

        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.save.getTag().equals("save")){
                    FirebaseDatabase.getInstance().getReference().child("Saved posts").child(user.getUid())
                            .child(posts.get(i).getPost_id()).setValue(true);

                }
                else{
                    Toast.makeText(c.getContext(), "Already saved", Toast.LENGTH_SHORT).show();
                }
            }
        });


        if(isHeading){
            holder.heading.setVisibility(View.VISIBLE);
        }

        if(isPhoto) {
            imageView.setVisibility(View.VISIBLE);
            //textView.setTextSize(15);
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

    private void isSaved(final String post_id, final ImageView imageView, final TextView textView){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Saved posts")
                .child(user.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(post_id).exists()){
                    imageView.setTag("saved");
                    textView.setText("Saved");
                }
                else{
                    imageView.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void isLike(String post_id, final ImageView imageView){

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes")
                .child(post_id);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(user.getUid()).exists()){
                    imageView.setImageResource(R.drawable.ic_thumb_up_purple);
                    imageView.setTag("liked");
                }
                else{
                    imageView.setImageResource(R.drawable.ic_thumb_up_white);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void isDislike(String post_id, final ImageView imageView){

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Dislikes")
                .child(post_id);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(user.getUid()).exists()){
                    imageView.setImageResource(R.drawable.ic_thumb_down_purple);
                    imageView.setTag("disliked");
                }
                else{
                    imageView.setImageResource(R.drawable.ic_thumb_down_white);
                    imageView.setTag("dislike");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void addNotification(String userId, String postId){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userId);

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("userId",user.getUid());
        hashMap.put("text","liked your post");
        hashMap.put("postId", postId);
        hashMap.put("isPost",true);

        reference.setValue(hashMap);
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

    private void getUserId(TextView username, String post_id){
        Query query = FirebaseDatabase.getInstance().getReference().child("Posts").child(post_id);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                post p  = snapshot.getValue(post.class);
                userId = p.getPublisher();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}

