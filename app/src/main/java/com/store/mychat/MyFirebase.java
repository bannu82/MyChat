package com.store.mychat;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
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

import java.io.InputStream;
import java.util.HashMap;

public class MyFirebase {
    DatabaseReference UserDatabaseReference;
    DatabaseReference ChatDatabaseReference;
    DatabaseReference ImageDatabaseReference;
    FirebaseAuth firebaseAuth;
    Context context;
    StorageReference storageReference ,ImageStorageReference ;


    //  Constructor
    public MyFirebase(Context context) {
        this.context = context;
        UserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        ChatDatabaseReference = FirebaseDatabase.getInstance().getReference().child("chats");
        ImageDatabaseReference = FirebaseDatabase.getInstance().getReference().child("image");
        storageReference = FirebaseStorage.getInstance().getReference();
        ImageStorageReference = FirebaseStorage.getInstance().getReference().child("Img");
        firebaseAuth = FirebaseAuth.getInstance();
    }

    void  setProfile(Uri FilePath){
        DatabaseReference UserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseAuth.getCurrentUser().getUid()).child("userProfile");
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(FirebaseAuth.getInstance().getUid());
        storageReference.putFile(FilePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                UserDatabaseReference.setValue(uri.toString());
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context.getApplicationContext(),"Failed to upllaod", Toast.LENGTH_SHORT).show();
                    }
                });

    }


    void setOnlineStatus(String status , long lastSeen){
        DatabaseReference statusRef = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseAuth.getCurrentUser().getUid());
        HashMap hashMap = new HashMap<>();
        hashMap.put("Status" , status);
        hashMap.put("lastSeen" , lastSeen);
        statusRef.updateChildren(hashMap);

    }

    void setUserNameOrStatus(String userName  , String type){
        DatabaseReference nameRef = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseAuth.getCurrentUser().getUid()).child(type);
        nameRef.setValue(userName);

    }

    void setPassword(String password){
        DatabaseReference passRef = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseAuth.getCurrentUser().getUid()).child("userPassword");
        passRef.setValue(password);
    }

}
