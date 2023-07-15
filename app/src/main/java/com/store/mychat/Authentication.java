package com.store.mychat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.store.mychat.databinding.ActivityAuthenticationBinding;

import java.io.InputStream;
import java.util.Objects;

public class Authentication extends AppCompatActivity
{
    ActivityAuthenticationBinding binding;
    String name,email,password;
    DatabaseReference databaseReference;
    Uri FilePath;
    String url;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAuthenticationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        binding.Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = binding.Email.getText().toString();
                password = binding.Password.getText().toString();
                if (email.isEmpty() && password.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Fill All Feilds",Toast.LENGTH_SHORT).show();
                } else if(email.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Enter Email",Toast.LENGTH_SHORT).show();
                } else if (password.isEmpty()) {
                    Toast.makeText(getApplicationContext(),"Enter Password",Toast.LENGTH_SHORT).show();
                }else {
                    login();
                }
            }
        });

        binding.SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = binding.Email.getText().toString();
                name = binding.Name.getText().toString();
                password = binding.Password.getText().toString();
                if (email.isEmpty() && password.isEmpty() && name.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Fill All Feilds",Toast.LENGTH_SHORT).show();
                } else if(email.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Enter Email",Toast.LENGTH_SHORT).show();
                } else if (password.isEmpty()) {
                    Toast.makeText(getApplicationContext(),"Enter Password",Toast.LENGTH_SHORT).show();
                } else if (name.isEmpty()) {
                    Toast.makeText(getApplicationContext(),"Enter Name",Toast.LENGTH_SHORT).show();
                }else {
                    signUp();
                }
            }
        });

        binding.UserImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity(Authentication.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType("image/*");
                                startActivityForResult(Intent.createChooser(intent,"Select Image File"),1);
                            }
                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {

                            }
                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser()!= null){
            startActivity(new Intent(Authentication.this , MainActivity.class));
            finish();
        }
    }

    private void login() {
        FirebaseAuth
                .getInstance()
                .signInWithEmailAndPassword(email.trim(),password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        startActivity(new Intent(Authentication.this , MainActivity.class));
                        finish();
                    }
                });
    }
    private void signUp() {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.trim(), password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .build();
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        assert firebaseUser != null;
                        firebaseUser.updateProfile(userProfileChangeRequest);

                        // Storage
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference uploader = storage.getReference().child("users").child(name).child("image");

                        uploader.putFile(FilePath)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        uploader.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                url = uri.toString();

                                                // Update user profile with the image URL
                                                UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                                        .setPhotoUri(Uri.parse(url))
                                                        .build();
                                                firebaseUser.updateProfile(profileUpdate);

                                                // Save user data to the database
                                                UserModel userModel = new UserModel(FirebaseAuth.getInstance().getUid(), name, email, password, url);

                                                databaseReference.child(FirebaseAuth.getInstance().getUid()).setValue(userModel);
                                                startActivity(new Intent(Authentication.this, MainActivity.class));
                                                finish();
                                            }
                                        });

                                        Toast.makeText(getApplicationContext(), "File Uploaded", Toast.LENGTH_LONG).show();
                                    }
                                });

                        binding.Name.setText("");
                        binding.Email.setText("");
                        binding.Password.setText("");
                        binding.UserImg.setImageResource(R.drawable.person);
                    }
                });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == 1 && data != null) {
            FilePath = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(FilePath);
                bitmap = BitmapFactory.decodeStream(inputStream);
                binding.UserImg.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error selecting image", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}