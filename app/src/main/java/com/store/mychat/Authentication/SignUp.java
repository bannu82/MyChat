package com.store.mychat.Authentication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
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
import com.karumi.dexter.listener.single.BasePermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.store.mychat.Dialogbox;
import com.store.mychat.Home_Activity;
import com.store.mychat.Models.UserModel;
import com.store.mychat.R;
import com.store.mychat.databinding.ActivitySettingsBinding;
import com.store.mychat.databinding.ActivitySignUpBinding;

import java.io.InputStream;

public class SignUp extends AppCompatActivity {

    DatabaseReference databaseReference;
    StorageReference storageReference;
    FirebaseUser firebaseUser;
    FirebaseAuth auth;
    String Onlinestatus;

    String email , name , password , status ;
    Bitmap bitmap;
    Uri uri;
    String url;
    ActivitySignUpBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        storageReference = FirebaseStorage.getInstance().getReference();

        binding.UserImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dexter.withActivity(SignUp.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType("image/*");
                                startActivityForResult(Intent.createChooser(intent ,"Select Image File" ) , 1);

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

        binding.alreadyHaveAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.dontHaveAcc.setVisibility(View.VISIBLE);
                binding.alreadyHaveAcc.setVisibility(View.GONE);
                binding.Name.setVisibility(View.GONE);
                binding.Status.setVisibility(View.GONE);
                binding.SignUp.setText("Log In");
                binding.title.setText("L O G  I N");
                binding.UserImg.setVisibility(View.GONE);
                binding.linearLayout4.setVisibility(View.VISIBLE);
                binding.linearLayout3.setVisibility(View.GONE);
            }
        });
        binding.dontHaveAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.dontHaveAcc.setVisibility(View.GONE);
                binding.alreadyHaveAcc.setVisibility(View.VISIBLE);
                binding.Name.setVisibility(View.VISIBLE);
                binding.Status.setVisibility(View.VISIBLE);
                binding.SignUp.setText("Sign Up");
                binding.title.setText("S I G N  U P");
                binding.UserImg.setVisibility(View.VISIBLE);
                binding.linearLayout4.setVisibility(View.GONE);
                binding.linearLayout3.setVisibility(View.VISIBLE);

            }
        });




        binding.SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signUpDetails();


                if (email.isEmpty() && password.isEmpty() && name.isEmpty() && status.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Fill All Fields", Toast.LENGTH_SHORT).show();
                } else if (uri == null) {
                    Toast.makeText(getApplicationContext(), "Please Select Profile Image", Toast.LENGTH_SHORT).show();
                } else if (email.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please Enter Email", Toast.LENGTH_SHORT).show();
                } else if (password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please Enter Password", Toast.LENGTH_SHORT).show();
                } else if (name.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please Enter Name", Toast.LENGTH_SHORT).show();
                } else if (status.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please Enter Status", Toast.LENGTH_SHORT).show();
                } else {
                    signUp();
                }            }
        });

        binding.LogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logInDetails();

                if (email.isEmpty() && password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Fill All Fields", Toast.LENGTH_SHORT).show();
                } else if (email.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Enter Email", Toast.LENGTH_SHORT).show();
                } else if (password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Enter Password", Toast.LENGTH_SHORT).show();
                } else {

                    logIn();
                }
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            startActivity(new Intent(SignUp.this , Home_Activity.class));
            finish();
        }
    }

    private void logIn() {


        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.loading_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setTitle("Loading ...");
        dialog.show();

        auth.signInWithEmailAndPassword(email , password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        Toast.makeText(SignUp.this, "LogIn Sucessfully!!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignUp.this , Home_Activity.class));
                        dialog.dismiss();
                        finish();

                        binding.UserImg.setImageResource(R.drawable.person);
                        binding.Email.setText("");
                        binding.Name.setText("");
                        binding.Password.setText("");
                        binding.Status.setText("");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignUp.this, "Unable to Login", Toast.LENGTH_SHORT).show();
                    }
                });

    }
    private void logInDetails() {
        this.email = binding.Email.getText().toString().trim();
        this.password = binding.Password.getText().toString().trim();

    }

    private void signUp() {


        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.loading_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setTitle("Loading ...");
        dialog.show();

        auth.fetchSignInMethodsForEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                            @Override
                            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                boolean isNewUser = task.getResult().getSignInMethods().isEmpty();

                                if (isNewUser){

                                    auth.createUserWithEmailAndPassword(email,password)
                                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                                @Override
                                                public void onSuccess(AuthResult authResult) {
                                                    UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                                            .setDisplayName(name)
                                                            .build();

                                                    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                                    assert firebaseUser != null;

                                                    firebaseUser.updateProfile(request);
                                                    StorageReference uploader = FirebaseStorage.getInstance().getReference(FirebaseAuth.getInstance().getUid());

                                                    uploader.putFile(uri)
                                                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                @Override
                                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                    uploader.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                        @Override
                                                                        public void onSuccess(Uri uri) {
                                                                            url = uri.toString();
                                                                            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                                                                    .setPhotoUri(Uri.parse(url)).build();


                                                                            UserModel model = new UserModel(
                                                                                    auth.getUid(),
                                                                                    name,
                                                                                    email,
                                                                                    password,
                                                                                    url,
                                                                                    status
                                                                            );

                                                                            databaseReference.child(FirebaseAuth.getInstance().getUid()).setValue(model);
                                                                            startActivity(new Intent(SignUp.this , Home_Activity.class));
                                                                            finish();
                                                                            dialog.dismiss();


                                                                            binding.UserImg.setImageResource(R.drawable.person);
                                                                            binding.Email.setText("");
                                                                            binding.Name.setText("");
                                                                            binding.Password.setText("");
                                                                            binding.Status.setText("");

                                                                        }
                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Toast.makeText(SignUp.this, "Unable to Load", Toast.LENGTH_SHORT).show();
                                                                            dialog.dismiss();
                                                                        }
                                                                    });

                                                                }
                                                            });



                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(SignUp.this, "Unable to sign In", Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss();
                                                }
                                            });

                                }else {
                                    Toast.makeText(SignUp.this, "User already Exist", Toast.LENGTH_SHORT).show();
                                }


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignUp.this, "Unable to sign in", Toast.LENGTH_SHORT).show();
                    }
                });


    }


    private void signUpDetails() {
        this.email = binding.Email.getText().toString().trim();
        this.name = binding.Name.getText().toString().trim();
        this.password = binding.Password.getText().toString().trim();
        this.status = binding.Status.getText().toString().trim();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK && requestCode == 1 && data != null ){
            uri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                bitmap = BitmapFactory.decodeStream(inputStream);
                binding.UserImg.setImageBitmap(bitmap);
            }catch (Exception e){
                Toast.makeText(this, "Unable to load", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}