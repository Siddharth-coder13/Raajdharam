package com.example.political_facebook.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.political_facebook.R;
import com.example.political_facebook.model.Comment;
import com.example.political_facebook.model.userModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Comment> comments;

    public CommentAdapter(Context mContext, ArrayList<Comment> comments) {
        this.mContext = mContext;
        this.comments = comments;
    }

    private FirebaseUser firebaseUser;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comments_items ,parent,false);
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final Comment comment = comments.get(i);

        holder.comment.setText(comment.getComment());
        getUserinfo(holder.username,comment.getPublisher());

    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView image_profile;
        public TextView username, comment;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.image_profile = itemView.findViewById(R.id.image_profile);
            this.username = itemView.findViewById(R.id.user_name);
            this.comment = itemView.findViewById(R.id.comment_text);
        }
    }

    private void getUserinfo(final TextView username, String publisherId){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(publisherId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userModel UserModel = snapshot.getValue(userModel.class);
                username.setText(UserModel.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
