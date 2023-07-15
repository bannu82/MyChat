package com.store.mychat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.store.mychat.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    DatabaseReference databaseReference;
    UserAdapter userAdapter;
    FirebaseAuth auth;
    StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userAdapter =new UserAdapter(this);
        binding.recycler.setAdapter(userAdapter);
        binding.recycler.setLayoutManager(new LinearLayoutManager(this));

        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userAdapter.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    String uid = dataSnapshot.getKey();
                    assert uid != null;
                    if (!uid.equals(FirebaseAuth.getInstance().getUid())){
                        UserModel userModel = dataSnapshot.getValue(UserModel.class);
                        userAdapter.add(userModel);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.logout){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this,Authentication.class));
            finish();
            return true;
        }

        else if (item.getItemId() == R.id.settings) {
            startActivity(new Intent(MainActivity.this , Settings.class));
        }

        else if (item.getItemId() == R.id.DelAcc) {

            UserModel model = new UserModel();

            auth = FirebaseAuth.getInstance();
            FirebaseUser user = auth.getCurrentUser();

            String uid = auth.getUid().toString();

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference root = database.getReference().child("users").child(uid);

            DatabaseReference uName = database.getReference().child("users").child(uid).child("userName");
            String Name = uName.get().toString();

            storageReference = FirebaseStorage.getInstance().getReference().child("users").child(Name);

            assert user != null;
            user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    root.setValue(null);
                    storageReference.delete();
                    Toast.makeText(MainActivity.this, "Account Deleted",Toast.LENGTH_SHORT).show();

                    auth.signOut();
                    startActivity(new Intent(MainActivity.this , Authentication.class));
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, "Unable To Delete Account",Toast.LENGTH_SHORT).show();
                }
            });
        }

        return false;

    }
}