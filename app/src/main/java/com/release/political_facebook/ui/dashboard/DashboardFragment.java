package com.release.political_facebook.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.release.political_facebook.R;
import com.release.political_facebook.model.post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class DashboardFragment extends Fragment {
    private RecyclerView recyclerView;
    private DatabaseReference reference;
    private FirebaseDatabase database;

    private DashboardViewModel dashboardViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        recyclerView = root.findViewById(R.id.recyclerView_leader);


        database = FirebaseDatabase.getInstance();


        return root;
    }

    private void get_leaderboards(String post_id){
        Query query = FirebaseDatabase.getInstance().getReference().child("leaderBoards").child(post_id).orderByChild("likes");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                post p = snapshot.getValue(post.class);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}