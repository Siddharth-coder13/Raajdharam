package com.release.political_facebook.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.release.political_facebook.CommentActivity;
import com.release.political_facebook.R;
import com.release.political_facebook.model.post;
import com.release.political_facebook.model.userModel;
import com.release.political_facebook.saved_posts_fragement;

import java.util.ArrayList;

public class savepostsAdapter extends RecyclerView.Adapter<savepostsAdapter.viewHolder> {

    private saved_posts_fragement s;
    private ArrayList<post> saved;

    private int nLike;
    private int nDisLike;
    private ImageView imageView;
    private TextView textView;

    public savepostsAdapter(saved_posts_fragement s, ArrayList<post> saved) {
        this.s = s;
        this.saved = saved;
    }

    public savepostsAdapter() {
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

        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("Users").child(saved.get(i).getPublisher());
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userModel m = snapshot.getValue(userModel.class);
                String name = m.getName();
                String uri = m.getImageUrl();
                holder.user_name.setText(name);
                if(uri != null){
                    Glide.with(s).load(uri).into(holder.profile_pic);
                }else {
                    holder.profile_pic.setImageResource(R.drawable.account);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.text_post.setText(saved.get(i).getText_post());

        holder.date_time.setText(saved.get(i).getDate_time());

        boolean isPhoto = saved.get(i).getImage_post() != null;

        isLike(saved.get(i).getPost_id(),holder.dislike);
        nrLikes(holder.likes, saved.get(i).getPost_id());
        isDislike(saved.get(i).getPost_id(),holder.like);
        nrDislikes(holder.dislikes,saved.get(i).getPost_id());

        holder.dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.dislike.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(saved.get(i).getPost_id())
                            .child(user.getUid()).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Dislikes").child(saved.get(i).getPost_id())
                            .child(user.getUid()).removeValue();
                }
                else {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(saved.get(i).getPost_id())
                            .child(user.getUid()).removeValue();
                }
            }
        });

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.like.getTag().equals("dislike")){
                    FirebaseDatabase.getInstance().getReference().child("Dislikes").child(saved.get(i).getPost_id())
                            .child(user.getUid()).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(saved.get(i).getPost_id())
                            .child(user.getUid()).removeValue();
                }
                else {
                    FirebaseDatabase.getInstance().getReference().child("Dislikes").child(saved.get(i).getPost_id())
                            .child(user.getUid()).removeValue();
                }
            }
        });

        holder.saved.setText("Delete");



        if(isPhoto) {
            imageView.setVisibility(View.VISIBLE);
            //textView.setTextSize(15);
            Glide.with(imageView.getContext())
                    .load(saved.get(i).getImage_post())
                    .into(imageView);
        }else{
            //imageView.setVisibility(View.GONE);
        }

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(s.getContext(), CommentActivity.class);
                intent.putExtra("postid",saved.get(i).getPost_id());
                intent.putExtra("publisherid",saved.get(i).getPublisher());
                s.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return saved.size();
    }


    public class viewHolder extends RecyclerView.ViewHolder{
        ImageView image_post,profile_pic, comment, dislike,like,save;
        TextView user_name,text_post, date_time, likes,dislikes,saved;

        public viewHolder(@NonNull View itemView) {
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
            this.save = itemView.findViewById(R.id.save);
            this.saved = itemView.findViewById(R.id.save_text);
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
