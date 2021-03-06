package com.release.political_facebook.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.release.political_facebook.R;
import com.release.political_facebook.model.userModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.release.political_facebook.user_profile;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class userAdapter extends RecyclerView.Adapter<userAdapter.ViewHolder> {

    private Context mcontext;
    private ArrayList<userModel> userModels;

    private FirebaseUser firebaseUser;

    public userAdapter(Context mcontext, ArrayList<userModel> userModels) {
        this.mcontext = mcontext;
        this.userModels = userModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.user_item,null);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int i) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final userModel UserModel = userModels.get(i);

        holder.follow_button.setVisibility(View.VISIBLE);
        holder.name.setText(UserModel.getName());
        holder.designation.setText(UserModel.getDesignation());
        if(UserModel.getImageUrl() == null){
             holder.image_profile.setImageResource(R.drawable.account);
        }else {
            Glide.with(mcontext).load(UserModel.getImageUrl()).into(holder.image_profile);
        }


        if(UserModel.getUser_id().equals(firebaseUser.getUid())){
            holder.follow_button.setVisibility(View.GONE);
        }

        isFollowing(UserModel.getUser_id(),holder.follow_button);

        holder.follow_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.follow_button.getText().toString().equals("Follow")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("Following").child(UserModel.getUser_id()).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(UserModel.getUser_id())
                            .child("Followers").child(firebaseUser.getUid()).setValue(true);

                    addNotification(UserModel.getUser_id());
                }
                else {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("Following").child(UserModel.getUser_id()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(UserModel.getUser_id())
                            .child("Followers").child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mcontext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("profileid",UserModel.getUser_id());
                editor.apply();

                Intent i = new Intent(mcontext,user_profile.class);
                mcontext.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return userModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView designation;
        public CircleImageView image_profile;
        public Button follow_button;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.user_search);
            designation = itemView.findViewById(R.id.designation_search);
            image_profile = itemView.findViewById(R.id.user_profile_pic);
            follow_button = itemView.findViewById(R.id.follow_button);
        }
    }

    private void isFollowing(final String Userid, final Button button){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow")
                .child(firebaseUser.getUid()).child("Following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(Userid).exists()){
                    button.setText("Following");
                    //button.setBackgroundColor(Color.parseColor("000000"));
                }
                else{
                    button.setText("Follow");
                    //button.setBackgroundColor(Color.parseColor("#2962FF"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addNotification(String userId){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userId);

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("userId",user.getUid());
        hashMap.put("text","started following you");
        hashMap.put("postId", "");
        hashMap.put("isPost",false);

        reference.setValue(hashMap);
    }
}
