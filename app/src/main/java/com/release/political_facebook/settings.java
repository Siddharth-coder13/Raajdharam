package com.release.political_facebook;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class settings extends AppCompatActivity {

    private TextView notifications, privacy, invite, help, feedback, logout;
    private RelativeLayout notification_relative, logout_relative;
    private TextView q1_cancel,q2_cancel;
    private TextView q1_yes,q2_yes;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        notifications = findViewById(R.id.notification_text);
        privacy = findViewById(R.id.privacy_text);
        invite = findViewById(R.id.invite_text);
        help = findViewById(R.id.help_text);
        feedback = findViewById(R.id.feedback_text);
        logout = findViewById(R.id.logout_text);

        mAuth = FirebaseAuth.getInstance();
        //mUser = mAuth.getCurrentUser();

        notification_relative = findViewById(R.id.notification_relative);
        q1_cancel = findViewById(R.id.question1_cancel);

        logout_relative = findViewById(R.id.logout_relative);
        q2_cancel = findViewById(R.id.question2_cancel);
        q2_yes = findViewById(R.id.question2_yes);

        notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notification_relative.setVisibility(View.VISIBLE);
            }
        });
        q1_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notification_relative.setVisibility(View.GONE);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout_relative.setVisibility(View.VISIBLE);
            }
        });
        q2_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout_relative.setVisibility(View.GONE);
            }
        });
        q2_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                finish();
            }
        });
    }
}
