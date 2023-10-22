package com.store.mychat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.store.mychat.Models.UserModel;
import com.store.mychat.databinding.ActivityPersonAboutBinding;

import java.util.HashMap;

public class personAbout extends AppCompatActivity {
    ActivityPersonAboutBinding binding;
    String reciverimg,recemail;
    String reciverName;
    String receiverId , reciverStatus ;

    FirebaseAuth auth;
    FirebaseUser currentUser;
    MyFirebase myFB;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPersonAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        getSupportActionBar().hide();

        myFB = new MyFirebase(this);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();


        receiverId = getIntent().getStringExtra("id");
        reciverName = getIntent().getStringExtra("nameeee");
        reciverimg = getIntent().getStringExtra("reciverImg");
        reciverStatus = getIntent().getStringExtra("userStatus");
        recemail = getIntent().getStringExtra("Email");


        binding.recvName.setText(reciverName);
        binding.RecStatus.setText(reciverStatus);
        binding.recvName.setText(reciverName);
        binding.recvEmail.setText(recemail);

        Picasso.get().load(reciverimg).into(binding.recvImg);
        binding.OnlineStatus.setText("hello");

        DatabaseReference statusRef = FirebaseDatabase.getInstance().getReference().child("users");

        statusRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String uid = dataSnapshot.getKey();
                    if (uid.equals(receiverId)) {
                        UserModel userModel = dataSnapshot.getValue(UserModel.class);
                        binding.OnlineStatus.setText(userModel.getStatus());
//                        Toast.makeText(ChatActivity.this, "Failed to load Status: " + Status , Toast.LENGTH_SHORT).show();

//                        Status = "hello";
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(personAbout.this, "Failed to load Status: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });



        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(personAbout.this,ChatActivity.class));
                finish();
            }
        });
    }

    public void status(String status  ,long lastSeen) {

        DatabaseReference statusReff = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Status", status);
        hashMap.put("lastSeen" ,lastSeen);

        statusReff.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        long lastSeen = 0;
        myFB.setOnlineStatus("Online" , lastSeen);

    }

    @Override
    protected void onPause() {
        super.onPause();
        long lastSeen = System.currentTimeMillis();
        myFB.setOnlineStatus("Offline", lastSeen);
    }
}