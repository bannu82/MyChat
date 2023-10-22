package com.store.mychat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.store.mychat.databinding.ActivityMyProfileBinding;

public class MyProfile extends AppCompatActivity {
    ActivityMyProfileBinding binding;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        binding = ActivityMyProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Picasso.get().load(getIntent().getStringExtra("profile")).into(binding.MyProfile);






    }
}