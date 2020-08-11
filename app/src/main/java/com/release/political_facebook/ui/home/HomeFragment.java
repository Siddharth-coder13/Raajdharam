package com.release.political_facebook.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.release.political_facebook.R;
import com.release.political_facebook.ViewPager.viewPagerAdapter_dashboard;
import com.release.political_facebook.new_post;
import com.release.political_facebook.poli_posts;
import com.release.political_facebook.public_posts;
import com.release.political_facebook.serach_user;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

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
    //private String image_uri;
    private ImageView add_post;
    private ImageView hide;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public HomeFragment() {
    }

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
        add_post = (ImageView) root.findViewById(R.id.new_post);
        hide = (ImageView) root.findViewById(R.id.hide);
        tabLayout = (TabLayout) root.findViewById(R.id.tab_layout1);
        viewPager = (ViewPager) root.findViewById(R.id.view_pager1);

        mAuth = FirebaseAuth.getInstance();
        mUsername = "Anonymous";
        user = FirebaseAuth.getInstance().getCurrentUser();

        viewPagerAdapter_dashboard adapter = new viewPagerAdapter_dashboard(getActivity().getSupportFragmentManager(),tabLayout.getTabCount());
        adapter.addFragments(new public_posts(),"Public");
        adapter.addFragments(new poli_posts(),"Politics");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), serach_user.class);
                startActivity(i);
            }
        });


        add_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), new_post.class));
            }
        });

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    onSignedInIntialise(user.getDisplayName());
                }
            }
        };

        return root;
    }

    private void onSignedInIntialise(String username){
        mUsername = username;
    }
}