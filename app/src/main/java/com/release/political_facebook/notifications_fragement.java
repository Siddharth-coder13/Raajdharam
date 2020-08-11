package com.release.political_facebook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.release.political_facebook.Adapter.notificationAdapter;
import com.release.political_facebook.model.notification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class notifications_fragement extends Fragment {

    private RecyclerView recyclerView;
    private notificationAdapter adapter;
    private List<notification> notificationList;

    View view;

    /*public notifications_fragement() {
    }*/

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.notifications,container,false);

        recyclerView = view.findViewById(R.id.notification_recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        notificationList = new ArrayList<>();
        adapter = new notificationAdapter(getContext(),notificationList);
        recyclerView.setAdapter(adapter);

        readNotifications();

        return view;
    }

    private void readNotifications(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Query reference = FirebaseDatabase.getInstance().getReference("Notifications").child(user.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notificationList.clear();
                notification n = snapshot.getValue(notification.class);
                notificationList.add(n);

                Collections.reverse(notificationList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
