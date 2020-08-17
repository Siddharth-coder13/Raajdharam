package com.release.political_facebook.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.release.political_facebook.My_posts;
import com.release.political_facebook.R;
import com.release.political_facebook.model.notification;
import com.release.political_facebook.model.post;
import com.release.political_facebook.model.userModel;
import com.release.political_facebook.user_profile;

import java.util.List;

public class notificationAdapter extends RecyclerView.Adapter<notificationAdapter.viewHolder> {

    private Context c;
    private List<notification> notifications;

    public notificationAdapter(Context c, List<notification> notifications) {
        this.c = c;
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(c).inflate(R.layout.notifications_item,parent,false);
        return new notificationAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int i) {
        final notification notify = notifications.get(i);

        holder.text.setText(notify.getText());

        userInfo(holder.profile_image,holder.username,notify.getUserId());

        if(notify.isPost()){
            holder.post_image.setVisibility(View.VISIBLE);
            getPostImage(holder.post_image,notify.getPostId());
        }
        else {
            holder.post_image.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(notify.isPost()){
                    SharedPreferences.Editor editor = c.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                    editor.putString("postid",notify.getPostId());
                    editor.apply();

                    /*Intent i = new Intent(c,My_posts.class);
                    c.startActivity(i);*/
                }
                else{
                    SharedPreferences.Editor editor = c.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                    editor.putString("profileid",notify.getPostId());
                    editor.apply();

                    Intent i = new Intent(c,user_profile.class);
                    c.startActivity(i);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{

        public ImageView profile_image, post_image;
        public TextView username , text;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            post_image = itemView.findViewById(R.id.post_image);
            profile_image = itemView.findViewById(R.id.profile_pic_notification);
            username = itemView.findViewById(R.id.username);
            text = itemView.findViewById(R.id.comment);

        }
    }

    public void userInfo(final ImageView imageView, final TextView username , String publisherId){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(publisherId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userModel user = snapshot.getValue(userModel.class);
                Glide.with(c).load(user.getImageUrl()).into(imageView);
                username.setText(user.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    public void getPostImage(final ImageView imageView, String postId){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Posts").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                post posts = snapshot.getValue(post.class);
                Glide.with(c).load(posts.getImage_post()).into(imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
