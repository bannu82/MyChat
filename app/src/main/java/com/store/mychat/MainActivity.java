package com.store.mychat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.store.mychat.Adapter.UserAdapter;
import com.store.mychat.Models.UserModel;
import com.store.mychat.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    List<UserModel> mUsers;
    UserAdapter userAdapter;
    FirebaseUser currentUser;
    MyFirebase myFB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        Objects.requireNonNull(getSupportActionBar()).hide();
        myFB = new MyFirebase(this);

        databaseReference = FirebaseDatabase.getInstance().getReference("users");


        binding.recycler.setHasFixedSize(true);
        binding.recycler.setLayoutManager(new LinearLayoutManager(this));

        mUsers = new ArrayList<>();

        readUser();
    }


    private void readUser() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            // Handle the case where the user is not authenticated
            // You might want to redirect to the login screen or take appropriate action.
            return;
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserModel user = snapshot.getValue(UserModel.class);
                    if (user != null) {
                        if (!firebaseUser.getUid().equals(user.getUserId())) {
                            mUsers.add(user);
                        }
                    }
                }

                userAdapter = new UserAdapter(MainActivity.this, mUsers, true);
                binding.recycler.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error if needed
            }
        });
    }




    @Override
    public void onResume() {
        super.onResume();
        if (userAdapter != null) {
            userAdapter.setIsChat(true);
        }
        long lastSeen = 0;

        myFB.setOnlineStatus("Online" , lastSeen);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (userAdapter != null) {
            userAdapter.setIsChat(false);
        }
        long lastSeen = System.currentTimeMillis();;

        myFB.setOnlineStatus("Offline" , lastSeen);
    }

}
