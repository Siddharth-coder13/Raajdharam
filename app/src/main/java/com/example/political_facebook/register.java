package com.example.political_facebook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.political_facebook.model.userModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;


public class register extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button register;
    private EditText email;
    private EditText password;
    private EditText musername;
    private ProgressBar progressBar;
    private RadioButton radioButton;
    private RadioGroup radioGroup;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference databaseReference;
    private ArrayList<userModel> userModels;
    private FirebaseUser firebaseUser;
    private CircleImageView circleImageView;
    private final int RC_PHOTO_PICKER = 1;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    Uri downloadUri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().hide();

        email = (EditText)findViewById(R.id.edit_email);
        password = (EditText)findViewById(R.id.edit_password);
        musername = (EditText)findViewById(R.id.edit_name);
        register = (Button)findViewById(R.id.register);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        radioButton = (RadioButton) findViewById(R.id.leader_account);
        radioGroup = (RadioGroup)findViewById(R.id.category);


        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();



        //radioGroup.clearCheck();
        storageReference = FirebaseStorage.getInstance().getReference().child("Users");

        if(radioButton.isChecked()){
            radioButton.setError("This account type is paid");
            radioButton.requestFocus();
        }

        /*circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });*/



        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email_id = email.getText().toString().trim();
                final String image = "https://firebasestorage.googleapis.com/v0/b/political-facebook.appspot.com/o/account_image.png?alt=media&token=20085d36-4404-4fdf-9b74-927dcd652c67";
                String Password = password.getText().toString().trim();
                final String name = musername.getText().toString().trim();
                Integer account_id = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton1 = findViewById(account_id);
                final String account_type = radioButton1.getText().toString().trim();

                if (name.isEmpty()){
                    musername.setError("Name is required");
                    musername.requestFocus();
                    return;
                }
                if(account_type.isEmpty()){
                    Toast.makeText(register.this, "Please select an account type", Toast.LENGTH_SHORT).show();
                }
                if(email_id.isEmpty()){
                    email.setError("Email is required");
                    email.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email_id).matches()){
                    email.setError("Please enter a valid email");
                    email.requestFocus();
                    return;
                }
                if(Password.isEmpty()){
                    password.setError("Password is required");
                    password.requestFocus();
                    return;
                }
                if(Password.length()<8){
                    password.setError("Minimum length of password should be 8");
                    password.requestFocus();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                mAuth.createUserWithEmailAndPassword(email_id,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if(task.isSuccessful()){

                            //user information
                            FirebaseUser user = mAuth.getCurrentUser();
                            String id = user.getUid();
                            databaseReference = mFirebaseDatabase.getReference().child("Users").child(id);
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (!task.isSuccessful()) {
                                                Toast.makeText(register.this, "User not updated", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                            Toast.makeText(register.this, "Uesr registered successful", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(getApplicationContext(),Login_public.class);
                            //i.putExtra("name",name);
                            startActivity(i);
                            // Register user to the database
                            //userModels.clear();
                            userModel userModels = new userModel();
                            userModels.setName(name);
                            userModels.setUser_id(id);
                            userModels.setImageUrl(image);
                            databaseReference.setValue(userModels);

                            finish();
                        }
                        else{
                            if (task.getException() instanceof FirebaseAuthUserCollisionException){
                                Toast.makeText(register.this, "You are already registerd", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(register.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

                //Userdata.user_name = name;

                //Userdata.account_Type = account_type;




            }
        });
    }
}
