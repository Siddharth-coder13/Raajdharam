package com.release.political_facebook.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.release.political_facebook.CommentActivity;
import com.release.political_facebook.R;
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

public class my_postsAdapter extends RecyclerView.Adapter<my_postsAdapter.ViewHolder> {

    private int nFollowers;
    private int nFollowing;
    private ImageView imageView;
    private TextView textView;
    private int nLike;
    private int nDisLike;

    private Context c;
    private ArrayList<post> my_posts;


    public my_postsAdapter(Context c, ArrayList<post> my_posts) {
        this.c = c;
        this.my_posts = my_posts;
    }

    private FirebaseUser user;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(c).inflate(R.layout.template,parent,false);
        imageView = (ImageView) parent.findViewById(R.id.image_post);
        textView = (TextView) parent.findViewById(R.id.text_post);
        return new my_postsAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int i) {

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("Users").child(my_posts.get(i).getPublisher());
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userModel m = snapshot.getValue(userModel.class);
                String name = m.getName();
                String uri = m.getImageUrl();
                holder.user_name.setText(name);
                Glide.with(c).load(uri).into(holder.profile_pic);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.text_post.setText(my_posts.get(i).getText_post());

        holder.date_time.setText(my_posts.get(i).getDate_time());

        boolean isPhoto = my_posts.get(i).getImage_post() != null;

        isLike(my_posts.get(i).getPost_id(),holder.dislike);
        nrLikes(holder.likes, my_posts.get(i).getPost_id());
        isDislike(my_posts.get(i).getPost_id(),holder.like);
        nrDislikes(holder.dislikes,my_posts.get(i).getPost_id());

        holder.dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.dislike.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(my_posts.get(i).getPost_id())
                            .child(user.getUid()).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Dislikes").child(my_posts.get(i).getPost_id())
                            .child(user.getUid()).removeValue();
                }
                else {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(my_posts.get(i).getPost_id())
                            .child(user.getUid()).removeValue();
                }
            }
        });

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.like.getTag().equals("dislike")){
                    FirebaseDatabase.getInstance().getReference().child("Dislikes").child(my_posts.get(i).getPost_id())
                            .child(user.getUid()).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(my_posts.get(i).getPost_id())
                            .child(user.getUid()).removeValue();
                }
                else {
                    FirebaseDatabase.getInstance().getReference().child("Dislikes").child(my_posts.get(i).getPost_id())
                            .child(user.getUid()).removeValue();
                }
            }
        });



        if(isPhoto) {
            imageView.setVisibility(View.VISIBLE);
            //textView.setTextSize(15);
            Glide.with(imageView.getContext())
                    .load(my_posts.get(i).getImage_post())
                    .into(imageView);
        }else{
            //imageView.setVisibility(View.GONE);
        }

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(c, CommentActivity.class);
                intent.putExtra("postid",my_posts.get(i).getPost_id());
                intent.putExtra("publisherid",my_posts.get(i).getPublisher());
                c.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return my_posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        ImageView image_post,profile_pic, comment, dislike,like;
        TextView user_name,text_post, date_time, likes,dislikes;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.image_post = itemView.findViewById(R.id.image_post);
            this.user_name = itemView.findViewById(R.id.user_name);
            this.profile_pic = itemView.findViewById(R.id.profile_pic);
            this.text_post = itemView.findViewById(R.id.text_post);
            this.date_time = itemView.findViewById(R.id.date_time);
            this.likes = itemView.findViewById(R.id.likes_no);
            this.comment = itemView.findViewById(R.id.comment);
            this.dislike = itemView.findViewById(R.id.dislike);
            this.like = itemView.findViewById(R.id.like);
            this.dislikes = itemView.findViewById(R.id.dislike_no);
        }
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
