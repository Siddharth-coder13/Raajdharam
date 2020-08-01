package com.release.political_facebook;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.release.political_facebook.model.userModel;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DrawerLayout drawerLayout;
    private CircleImageView imageView;
    private NavigationView navigationView;
    private TextView username;
    private TextView bio;
    private TextView designation;
    private ImageView edit;
    private String image_url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth = FirebaseAuth.getInstance();
        getSupportActionBar().hide();

        navigationView = findViewById(R.id.navigation_view);
        View header = navigationView.getHeaderView(0);
        edit = header.findViewById(R.id.edit);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        imageView = (CircleImageView) header.findViewById(R.id.profile_pic_nav);
        username = header.findViewById(R.id.user_nav);
        bio = header.findViewById(R.id.experience);
        designation = header.findViewById(R.id.designation);

        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                if(user != null){
                    String mUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(mUid);
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            userModel m = snapshot.getValue(userModel.class);
                            username.setText(m.getName());
                            bio.setText(m.getBio());
                            designation.setText(m.getDesignation());
                            image_url = m.getImageUrl();
                            Glide.with(MainActivity.this).load(m.getImageUrl()).into(imageView);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else{
                    startActivity(new Intent(MainActivity.this,Login_public.class));
                    finish();
                }
            }
        });


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,profile_update.class);
                i.putExtra("name",username.getText().toString());
                i.putExtra("bio",bio.getText().toString());
                i.putExtra("designation",designation.getText().toString());
                i.putExtra("image", image_url);
                startActivity(i);
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();

                if(id == R.id.my_posts){
                    //open my posts
                    startActivity(new Intent(MainActivity.this,My_posts.class));

                }else if(id == R.id.notification){
                    // open notification

                }else if(id == R.id.settings){
                    // open settings
                    startActivity(new Intent(MainActivity.this,settings.class));
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

    }



}