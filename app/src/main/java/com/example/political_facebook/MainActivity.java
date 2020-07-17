package com.example.political_facebook;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.political_facebook.model.userModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;
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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference databaes_leader_account;
    private DatabaseReference databaes_public_account;
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
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);



        mAuth = FirebaseAuth.getInstance();
        //mFirebaseDatabase = FirebaseDatabase.getInstance();
        //databaes_leader_account = mFirebaseDatabase.getReference().child("Leader account");
        //databaes_public_account = mFirebaseDatabase.getReference().child("Public account");

        getSupportActionBar().hide();

        //Leader_account();
        navigationView = findViewById(R.id.navigation_view);
        View header = navigationView.getHeaderView(0);
        final Button sign_out = header.findViewById(R.id.sign_out);
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
            }
        });







        sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                finish();
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






    }



}
