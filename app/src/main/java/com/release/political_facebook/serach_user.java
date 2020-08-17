package com.release.political_facebook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.release.political_facebook.Adapter.userAdapter;
import com.release.political_facebook.model.userModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class serach_user extends AppCompatActivity {

    private RecyclerView recyclerView;
    private userAdapter muserAdapter;
    private ArrayList<userModel> userModels;
    private ImageView imageView;
    private Toolbar toolbar;
    private ImageView arrow_back;

    String profileId;

    EditText search_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serach_user);

        toolbar = findViewById(R.id.toolbar_search);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences prefs = getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileId = prefs.getString("profileid","none");

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        search_bar = findViewById(R.id.search);
        arrow_back = findViewById(R.id.home);
        arrow_back.setVisibility(View.GONE);

        userModels = new ArrayList<>();
        muserAdapter  = new userAdapter(this,userModels);
        recyclerView.setAdapter(muserAdapter);

        //readUser();

        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search_user(s.toString());
                //readUser();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void search_user(String s){
        Query query = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("name")
                .startAt(s)
                .endAt(s+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userModels.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    userModel Usermodels = dataSnapshot.getValue(userModel.class);
                    userModels.add(Usermodels);
                }
                if(search_bar.getText().toString().equals("")){
                    userModels.clear();
                }
                muserAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readUser(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(search_bar.getText().toString().equals("")){
                    userModels.clear();
                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                        userModel Usermodels = dataSnapshot.getValue(userModel.class);
                        userModels.add(Usermodels);
                    }
                    muserAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addNotification() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(profileId);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userId", user.getUid());
        hashMap.put("text", "started following you");
        hashMap.put("postId", "");
        hashMap.put("isPost", false);

        reference.setValue(hashMap);
    }

}
