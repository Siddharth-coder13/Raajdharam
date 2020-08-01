package com.release.political_facebook.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.release.political_facebook.Login_public;
import com.release.political_facebook.R;
import com.release.political_facebook.Adapter.postAdapter;
import com.release.political_facebook.model.post;
import com.release.political_facebook.model.userModel;
import com.release.political_facebook.serach_user;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;


public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private CircleImageView imageView;
    private TextView textView;
    private EditText editText;
    private ImageView imageView1;
    private ImageView nav_image;
    private Button sendButton;
    private RecyclerView mrecyclerView;
    private postAdapter madapter;
    private ArrayList<post> posts;
    private ImageView dislike;
    private ImageView like;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference mdatabaseReference;
    private ChildEventListener mChildEventListener;
    private String mUsername;
    private FirebaseAuth.AuthStateListener authStateListener;
    private NavigationView navigationView;
    private ImageView searchView;
    private ArrayList<String> followingList;
    private FirebaseUser user;
    private String post_id;
    private String image_uri;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_home, container, false);

        //getActivity().getActionBar().hide();

        mrecyclerView = root.findViewById(R.id.recyclerView);
        textView = (TextView) root.findViewById(R.id.user_name);
        editText = (EditText) root.findViewById(R.id.text_message);
        imageView = (CircleImageView) root.findViewById(R.id.profile_pic);
        imageView1 = (ImageView) root.findViewById(R.id.image_post);
        sendButton = (Button) root.findViewById(R.id.sendButton);
        nav_image = (ImageView) root.findViewById(R.id.image);
        searchView = (ImageView) root.findViewById(R.id.search_view);

        //Firebase setup
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mdatabaseReference = mFirebaseDatabase.getReference().child("Posts");
        mUsername = "Anonymous";
        user = FirebaseAuth.getInstance().getCurrentUser();

        post_id = mdatabaseReference.push().getKey();

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), serach_user.class);
                startActivity(i);
            }
        });


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mrecyclerView.setLayoutManager(linearLayoutManager);

        posts = new ArrayList<>();

        madapter = new postAdapter(HomeFragment.this, posts);
        mrecyclerView.setAdapter(madapter);

        //Like The post
        dislike = (ImageView) root.findViewById(R.id.dislike);


        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    sendButton.setEnabled(true);
                } else {
                    sendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String currentDateAndTime = java.text.DateFormat.getDateTimeInstance().format(new Date());
                post m = new post(mUsername, editText.getText().toString(),null, currentDateAndTime,post_id ,FirebaseAuth.getInstance().getCurrentUser().getUid());

                mdatabaseReference.child(post_id).setValue(m);

                //mrecyclerView.smoothScrollToPosition(madapter.getItemCount() -1);
                hideKeyboard();


                editText.setText("");
                Toast.makeText(getActivity(), "Message sent", Toast.LENGTH_SHORT).show();
            }
        });

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    onSignedInIntialise(user.getDisplayName());
                    updateUser();
                } else {
                    mUsername = "Anonymous";
                    Intent i = new Intent(getContext(), Login_public.class);
                    startActivity(i);
                    getActivity().finish();
                }
            }
        };

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        mAuth.removeAuthStateListener(authStateListener);
        detachDatabaseReadListener();
        posts.clear();
    }

    private void CheckFollowing(){
        followingList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(FirebaseAuth.getInstance()
        .getCurrentUser().getUid()).child("Following");


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followingList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    followingList.add(dataSnapshot.getKey());

                }

                attachDatabaseReadListener();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void hideKeyboard(){

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(),0);

    }

    private void onSignedInIntialise(String username){
        mUsername = username;
        CheckFollowing();
    }

    private void attachDatabaseReadListener() {
        if(mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                String mUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    post m = dataSnapshot.getValue(post.class);
                    for(String id : followingList){
                        if(m.getPublisher().equals(id)){
                            posts.add(m);}
                    }

                    if( m.getPublisher().equals(mUid)){
                        posts.add(m);
                    }
                    mrecyclerView.setAdapter(madapter);

                }


                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            };
            mdatabaseReference.addChildEventListener(mChildEventListener);
        }

    }

    private void detachDatabaseReadListener(){
        if(mChildEventListener != null){
            mdatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }

    }

    private void updateUser(){
        DatabaseReference referene = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
        referene.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userModel m = snapshot.getValue(userModel.class);
                image_uri = m.getImageUrl();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }








}