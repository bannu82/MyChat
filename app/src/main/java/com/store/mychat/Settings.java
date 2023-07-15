package com.store.mychat;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.store.mychat.databinding.ActivitySettingsBinding;

import java.nio.channels.Pipe;

public class Settings extends AppCompatActivity {

    ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

//        Showing Profile on ImageView and Name
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String url = firebaseUser.getPhotoUrl().toString();
        String name = firebaseUser.getDisplayName().toString();
        Picasso.get().load(url).into(binding.personsImage);
        binding.NameSetting.setText(name);

        binding.ChangName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent (Settings.this,MainActivity.class));
                finish();
            }
        });


    }
}