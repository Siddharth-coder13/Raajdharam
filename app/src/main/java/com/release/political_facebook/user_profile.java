package com.release.political_facebook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.release.political_facebook.model.userModel;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class user_profile extends AppCompatActivity {

    private Toolbar toolbar;
    private CircleImageView profile_pic;
    private TextView username;
    private TextView designation;
    private TextView bio;
    private Button follow;
    private Button view_posts;
    private String profileId;
    FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        toolbar = findViewById(R.id.toolbar_userProfile);
        profile_pic = findViewById(R.id.profile_pic_user);
        username = findViewById(R.id.user_name_user);
        designation = findViewById(R.id.designation_user);
        bio = findViewById(R.id.bio);
        follow = findViewById(R.id.follow);
        view_posts = findViewById(R.id.view_post);

        SharedPreferences prefs = getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileId = prefs.getString("profileid","none");

        //Toast.makeText(this, userId, Toast.LENGTH_SHORT).show();
        userinfo();
        isFollowing(profileId,follow);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(profileId.equals(user.getUid())){
            follow.setVisibility(View.GONE);
        }

        view_posts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(user_profile.this,My_posts.class);
                intent.putExtra("UserId",profileId);
                startActivity(intent);
            }
        });

        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(follow.getText().toString().equals("Follow")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getUid())
                            .child("Following").child(profileId).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId)
                            .child("Followers").child(user.getUid()).setValue(true);

                    addNotification();
                }
                else {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getUid())
                            .child("Following").child(profileId).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId)
                            .child("Followers").child(user.getUid()).removeValue();
                }
            }
        });


    }

    private void isFollowing(final String Userid, final Button button){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow")
                .child(firebaseUser.getUid()).child("Following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(Userid).exists()){
                    button.setText("Following");
                    //button.setBackgroundColor(Color.parseColor("000000"));
                    //button.setBackgroundColor(getResources().getColor(R.color.following_color));
                }
                else{
                    button.setText("Follow");
                    //button.setBackgroundColor(Color.parseColor("#2962FF"));
                    //button.setBackgroundColor(getResources().getColor(R.color.follow_color));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void userinfo(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(profileId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(user_profile.this == null){
                    return;
                }

                userModel m = snapshot.getValue(userModel.class);
                username.setText(m.getName());
                designation.setText(m.getDesignation());
                bio.setText(m.getBio());
                Glide.with(user_profile.this).load(m.getImageUrl()).into(profile_pic);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    /*public void viewPost(){
        Query query = FirebaseDatabase.getInstance().getReference().child("Posts").orderByChild("publisher").equalTo(userId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    post p = dataSnapshot.getValue(post.class);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }*/

    private void addNotification(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(profileId);

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("userId",user.getUid());
        hashMap.put("text","started following you");
        hashMap.put("postId", "");
        hashMap.put("isPost",false);

        reference.setValue(hashMap);
    }


}
