package com.release.political_facebook.Adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.release.political_facebook.R;

public class viewHolder extends RecyclerView.ViewHolder {

    ImageView image_post,profile_pic, comment, dislike,like;
    TextView user_name,text_post, date_time, likes,dislikes;
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

    }
}
