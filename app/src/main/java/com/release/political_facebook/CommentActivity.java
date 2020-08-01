package com.release.political_facebook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.release.political_facebook.Adapter.CommentAdapter;
import com.release.political_facebook.model.Comment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class CommentActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private ArrayList<Comment> comments;


    EditText addComment;
    Button sendComment;
    ImageView image_profie;
    Toolbar toolbar;
    String post_id;
    String publisher_id;

    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        //getSupportActionBar().hide();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Comments");

        user = FirebaseAuth.getInstance().getCurrentUser();


        addComment = findViewById(R.id.comment_type);
        sendComment = findViewById(R.id.post_comment);
        image_profie = findViewById(R.id.image_profile);

        Intent i = getIntent();
        post_id = i.getStringExtra("postid");
        publisher_id = i.getStringExtra("publisherid");

        recyclerView = findViewById(R.id.recycler_View);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        comments = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, comments);
        recyclerView.setAdapter(commentAdapter);

        addComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    sendComment.setEnabled(true);
                } else {
                    sendComment.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addComment();
            }
        });


        readComment();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                
            }
        });



    }


    private void addComment(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(post_id);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("comment", addComment.getText().toString());
        hashMap.put("publisher", user.getUid());

        reference.push().setValue(hashMap);
        addComment.setText("");
    }

    private void readComment(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(post_id);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                comments.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Comment c = dataSnapshot.getValue(Comment.class);
                    comments.add(c);
                }
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
