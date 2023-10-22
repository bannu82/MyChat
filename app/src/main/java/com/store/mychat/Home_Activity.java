package com.store.mychat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.store.mychat.Adapter.UserAdapter;
import com.store.mychat.Adapter.VPAdapter;
import com.store.mychat.Authentication.Authentication;
import com.store.mychat.Authentication.SignUp;
import com.store.mychat.Models.UserModel;


public class Home_Activity extends AppCompatActivity {

    TabLayout tab;
    ViewPager viewPager;
//    ImageButton imbtn;

    DatabaseReference databaseReference;
    UserAdapter userAdapter;

    TextView UserName;
    FirebaseAuth auth;
    FirebaseUser currentUser;
    ImageView AddUser , userImage,Settings;
    String UserPic;

    MyFirebase myFB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
//        getSupportActionBar().hide();

        myFB = new MyFirebase(this);

        UserName = findViewById(R.id.UserName);
        userImage = findViewById(R.id.userImg);
        Settings = findViewById(R.id.settings);
//********************************************************************************
        tab = findViewById(R.id.TabLayout);
        viewPager = findViewById(R.id.ViewPager);

        AddUser = findViewById(R.id.AddUser);

        VPAdapter adapter = new VPAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        tab.setBackgroundColor(getColor(R.color.toolbar));
        tab.setupWithViewPager(viewPager);


//        **************************************************
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            // If the user is not authenticated, redirect to the login activity
            startActivity(new Intent(Home_Activity.this, SignUp.class));
            finish();
            return;
        }
//****************************************************


        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Dialog dialog = new Dialog(Home_Activity.this );
                View view1 = getLayoutInflater().inflate(R.layout.profile_dialog ,  null);
                ImageView Profile = view1.findViewById(R.id.ProfileImage);
                RelativeLayout RL = view1.findViewById(R.id.relativeLaout2);
                TextView TV = view1.findViewById(R.id.UserNameClick);

                dialog.setContentView(view1);
                dialog.show();
                RL.setVisibility(View.GONE);

                TV.setText("Profile pic");
                TV.setBackground(getDrawable(R.color.toolbar ));
                Picasso.get().load(UserPic).into(Profile);
                Profile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                         Intent intent = new Intent(Home_Activity.this , MyProfile.class);
                         intent.putExtra("profile" , UserPic );
                         startActivity(intent);
                    }
                });


            }
        });


        userImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(getApplicationContext() , "Hamlo" , Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        AddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home_Activity.this, MainActivity.class) );
//                Toast.makeText(Home_Activity.this , "Add contact ", Toast.LENGTH_LONG).show();
            }
        });


//*****************************************************************************************


        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            // If the user is not authenticated, redirect to the login activity
            startActivity(new Intent(Home_Activity.this, SignUp.class));
            finish();
            return;
        }else {
        }

        DatabaseReference ProReference = FirebaseDatabase.getInstance().getReference("users");

        ProReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    UserModel user = snapshot.getValue(UserModel.class);
                    if (user != null && currentUser != null) {
                        if (user.getUserId() != null && user.getUserId().equals(currentUser.getUid())) {
                            UserName.setText(user.getUserName());
                            UserPic = user.getUserProfile();
                            Picasso.get().load(user.getUserProfile()).into(userImage);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error if needed
            }
        });



        Settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home_Activity.this, Settings.class));

            }
        });


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
        myFB.setOnlineStatus("Offline" , lastSeen);
    }


}