package com.release.political_facebook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class profile_update extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText name;
    private EditText bio;
    private EditText designation;
    private Button update;
    private CircleImageView profile;
    private final int RC_PHOTO_PICKER = 1;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private Uri downloadUri;
    private FirebaseUser user;
    private ProgressBar progressBar;
    private String image1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_update);

        user = FirebaseAuth.getInstance().getCurrentUser();
        storage = FirebaseStorage.getInstance();

        storageReference = storage.getReference().child("Users");


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Update profile");

        name = findViewById(R.id.change_username);
        bio = findViewById(R.id.change_bio);
        designation = findViewById(R.id.change_designation);
        profile = findViewById(R.id.change_profile_pic);
        update = findViewById(R.id.update);
        progressBar = findViewById(R.id.upload_pic);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
                progressBar.setVisibility(View.VISIBLE);
            }
        });

        Intent i = getIntent();
        String name1 = i.getStringExtra("name");
        String bio1 = i.getStringExtra("bio");
        String designation1 = i.getStringExtra("designation");
        image1 = i.getStringExtra("image");

        name.setText(name1);
        bio.setText(bio1);
        designation.setText(designation1);
        Glide.with(this).load(image1).into(profile);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update_user_info(name.getText().toString(),bio.getText().toString(),designation.getText().toString(),image1,user.getUid());
                Toast.makeText(profile_update.this,"Profile updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        progressBar.setVisibility(View.VISIBLE);

        if(requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK){
            Uri selectedUri = data.getData();
            final StorageReference picref = storageReference.child(selectedUri.getLastPathSegment());

            picref.putFile(selectedUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    picref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            downloadUri = task.getResult();
                            if(downloadUri != null){
                                image1 = downloadUri.toString();
                                Glide.with(profile_update.this).load(downloadUri.toString()).into(profile);
                                progressBar.setVisibility(View.GONE);
                            }
                            //Toast.makeText(profile_update.this,"Profile updated successfully", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            });
        }

    }

    private void update_user_info(String name, String bio, String designation, String image, String user_id) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(profile_update.this, "User not updated", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        reference.child("imageUrl").setValue(image);
        reference.child("name").setValue(name);
        reference.child("bio").setValue(bio);
        reference.child("designation").setValue(designation);
    }
}
