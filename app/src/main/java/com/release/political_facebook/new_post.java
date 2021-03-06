package com.release.political_facebook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.release.political_facebook.model.post;
import com.release.political_facebook.model.userModel;

import java.util.Date;

public class new_post extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView profile_pic;
    private TextView username;
    private ImageView image_post;
    private TextView heading;
    private TextView text_post;
    private Button post;
    private Button post_anonymously;
    private DatabaseReference mdatabaseReference;
    private String post_id;
    private String userId;
    private String image_uri;
    private int RC_PHOTO_PICKER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        toolbar = findViewById(R.id.toolbar_new_Post);
        profile_pic = findViewById(R.id.profile_pic_add);
        username = findViewById(R.id.name_text_add);
        image_post = findViewById(R.id.image_post_add);
        heading = findViewById(R.id.heading_add);
        text_post = findViewById(R.id.text_post_add);
        post = findViewById(R.id.post_add);
        post_anonymously = findViewById(R.id.post_add_anonymously);

        mdatabaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");
        post_id = mdatabaseReference.push().getKey();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        image_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });

        user_info();
        post.setEnabled(false);
        post_anonymously.setEnabled(false);

        heading.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                heading.setText(null);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().length() == 0){
                    heading.setText(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        text_post.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    post.setEnabled(true);
                    post_anonymously.setEnabled(true);
                } else {
                    post.setEnabled(false);
                    post_anonymously.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String currentDateAndTime = java.text.DateFormat.getDateTimeInstance().format(new Date());
                com.release.political_facebook.model.post m = new post(user.getDisplayName(), text_post.getText().toString(),null, currentDateAndTime,post_id , userId, heading.getText().toString());

                mdatabaseReference.child(post_id).setValue(m);




                text_post.setText("");
                finish();
            }
        });

        post_anonymously.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String currentDateAndTime = java.text.DateFormat.getDateTimeInstance().format(new Date());
                com.release.political_facebook.model.post m = new post("Anonymous", text_post.getText().toString(),image_uri, currentDateAndTime,post_id , FirebaseAuth.getInstance().getCurrentUser().getUid(), heading.getText().toString());

                mdatabaseReference.child(post_id).setValue(m);

                text_post.setText("");
                finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK){
            image_uri = data.getData().toString();
            Glide.with(new_post.this).load(image_uri).into(image_post);
        }
    }

    public void user_info(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userModel u = snapshot.getValue(userModel.class);

                if(u != null){
                    username.setText(u.getName());
                    Glide.with(new_post.this).load(u.getImageUrl()).into(profile_pic);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
