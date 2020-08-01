package com.release.political_facebook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login_public extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView sign_up;
    private EditText email;
    private EditText password;
    private Button login;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_public);

        getSupportActionBar().hide();
        //Bundle name = getIntent().getExtras();
        //final String name = name.getString("name");

        sign_up = (TextView) findViewById(R.id.sign_up);
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login_public.this,register.class);
                startActivity(i);
            }
        });

        mAuth = FirebaseAuth.getInstance();

        email = (EditText)findViewById(R.id.edit_email);
        password = (EditText) findViewById(R.id.edit_password);
        login = (Button)findViewById(R.id.login);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar1);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email_id = email.getText().toString().trim();
                String Password = password.getText().toString().trim();

                if (email_id.isEmpty()){
                    email.setError("Email is required");
                    email.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email_id).matches()){
                    email.setError("Please a valid email id");
                    email.requestFocus();
                    return;
                }
                if (Password.isEmpty()){
                    password.setError("Password is required");
                    email.requestFocus();
                    return;
                }
                else{
                    progressBar.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(email_id,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()){

                                Toast.makeText(Login_public.this, "Signed in successfully", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(Login_public.this,MainActivity.class);
                                //i.putExtra("name",name);
                                startActivity(i);
                                finish();
                            }
                            else{
                                Toast.makeText(Login_public.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });




    }
}
