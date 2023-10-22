package com.store.mychat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;
import com.store.mychat.Authentication.Authentication;
import com.store.mychat.Authentication.SignUp;
import com.store.mychat.databinding.ActivitySettingsBinding;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Objects;

public class Settings extends AppCompatActivity {

    ActivitySettingsBinding binding;
    String changedName;
    DatabaseReference databaseReference;
    FirebaseAuth auth;

    StorageReference storageReference;
    private Uri FilePath;
    private Bitmap bitmap;
    String url;

    FirebaseUser currentUser;
    String name,Status;
    MyFirebase myFB;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        getSupportActionBar().hide();

        myFB = new MyFirebase(this);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(User.getUid());
        String Email = firebaseUser.getEmail();


//        **************************************************
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            // If the user is not authenticated, redirect to the login activity
            startActivity(new Intent(Settings.this, SignUp.class));
            finish();
            return;
        }
//****************************************************

        databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isComplete()){
                    if(task.getResult().exists()){
                        DataSnapshot snapshot = task.getResult();

                        name = String.valueOf(snapshot.child("userName").getValue());
                        binding.NameSetting.setText(name);

                        url = String.valueOf(snapshot.child("userProfile").getValue());
                        Picasso.get().load(url).into(binding.personsImage);

                        Status = String.valueOf(snapshot.child("userStatus").getValue());
                        binding.Status.setText(Status);

                    }
                }else {
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                }
            }
        });

        binding.Email.setText(Email);

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent (Settings.this,Home_Activity.class));
                finish();
            }
        });

        binding.DELAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Settings.this);
                alertDialogBuilder.setTitle("Delete Account");
                alertDialogBuilder.setMessage("Are you sure you want to delete your account?");
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteUserAccount();
                    }
                });
                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialogBuilder.create().show();
            }
        });

        binding.LOGOUT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(Settings.this);
                alertDialog.setTitle("Log Out" );
                alertDialog.setMessage("Are you sure you want to delete your account?");
                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(Settings.this,SignUp.class));
                        finish();
                    }
                });
                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                   dialog.dismiss();
                    }
                });
                alertDialog.create().show();

            }
        });

        binding.personsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withActivity(Settings.this)
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

        binding.editPencil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String type = "userName";
                final AlertDialog.Builder alert = new AlertDialog.Builder(Settings.this);
                View view1 = getLayoutInflater().inflate(R.layout.custom_dialog , null);

                EditText EditName = (EditText) view1.findViewById(R.id.EditName);
                Button btn_cancel = (Button) view1.findViewById(R.id.cancel_button);
                Button btn_ok = (Button) view1.findViewById(R.id.Ok_btn);

                alert.setView(view1).setTitle("Change Name");

                final AlertDialog alertDialog = alert.create();
                alertDialog.setCanceledOnTouchOutside(false);

                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        name = EditName.getText().toString().trim();
                        Toast.makeText(getApplicationContext(), "Name Changed Sucessfully !!", Toast.LENGTH_LONG).show();
                        binding.NameSetting.setText(name);
                        alertDialog.dismiss();
                        myFB.setUserNameOrStatus(name ,type );
                    }
                });

                alertDialog.show();


            }
        });

        binding.editPencil2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String type = "userStatus";


                final AlertDialog.Builder alert = new AlertDialog.Builder(Settings.this);
                View view1 = getLayoutInflater().inflate(R.layout.custom_dialog , null);
                EditText status = (EditText) view1.findViewById(R.id.EditName);
                Button btn_cancel = (Button) view1.findViewById(R.id.cancel_button);
                Button btn_ok = (Button) view1.findViewById(R.id.Ok_btn);

                alert.setView(view1).setTitle("Change Status");


                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid()).child("userStatus");

                final AlertDialog alertDialog = alert.create();
                alertDialog.setCanceledOnTouchOutside(false);

                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Status =  status.getText().toString();
                        Toast.makeText(getApplicationContext(), "Name Changed Sucessfully !!", Toast.LENGTH_LONG).show();
                        binding.Status.setText(Status);
                        alertDialog.dismiss();
                        myFB.setUserNameOrStatus(Status ,type );

                    }
                });
                alertDialog.show();
            }
        });

    }



    private void deleteUserAccount() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Delete user's profile picture from Firebase Storage
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(user.getUid());
            storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // Profile picture deleted successfully
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Handle the failure to delete the profile picture
                }
            });

            // Remove the user's data from the Realtime Database
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
            databaseReference.removeValue();

            // Delete the user's account
            user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(Settings.this, "Account Deleted", Toast.LENGTH_SHORT).show();
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(Settings.this, SignUp.class));
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Settings.this, "Unable To Delete Account", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == 1 && data != null) {
            FilePath = data.getData();
            try {



                InputStream inputStream = getContentResolver().openInputStream(FilePath);
                bitmap = BitmapFactory.decodeStream(inputStream);
                binding.personsImage.setImageBitmap(bitmap);
                myFB.setProfile(FilePath);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error selecting image", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void setStatus(String status , long lastSeen){
        DatabaseReference statusRef = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid());
        HashMap hashMap = new HashMap<>();
        hashMap.put("Status" , status);
        hashMap.put("lastSeen" , lastSeen);
        statusRef.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        long lastSeen = 0;
        setStatus(  "Online", lastSeen);
    }

    @Override
    protected void onPause() {
        super.onPause();
        long lastSeen = System.currentTimeMillis();
        setStatus(  "Offline", lastSeen);
    }

}