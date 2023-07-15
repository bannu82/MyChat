package com.store.mychat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.store.mychat.databinding.ActivityChatBinding;

import java.util.UUID;

public class ChatActivity extends AppCompatActivity {
    ActivityChatBinding binding;
    String reciverimg;
    String reciverName;
    String receiverId;
    DatabaseReference senderReference, receiverReference;
    String senderRoom, receiverRoom;
    MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        receiverId = getIntent().getStringExtra("id");
        reciverName = getIntent().getStringExtra("nameeee");
        reciverimg = getIntent().getStringExtra("reciverImg");

        String senderId = FirebaseAuth.getInstance().getUid();
        senderRoom = senderId + receiverId;
        receiverRoom = receiverId + senderId;

        Picasso.get().load(reciverimg).into(binding.UserProfileChat);
        binding.chatUserName.setText(reciverName);

        messageAdapter = new MessageAdapter(this);
        binding.recycler.setAdapter(messageAdapter);
        binding.recycler.setLayoutManager(new LinearLayoutManager(this));

        senderReference = FirebaseDatabase.getInstance().getReference("chats").child(senderRoom);
        receiverReference = FirebaseDatabase.getInstance().getReference("chats").child(receiverRoom);

        senderReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageAdapter.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MessageModel messageModel = dataSnapshot.getValue(MessageModel.class);
                    messageAdapter.add(messageModel);
                }

                // Sort the messages based on timestamps
                messageAdapter.sortMessages();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled event
            }
        });

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatActivity.this,MainActivity.class));
                finish();
            }
        });

        binding.sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = binding.messageEd.getText().toString().trim();
                if (!message.isEmpty()) {
                    sendMessage(message);
                }
            }
        });
    }

    private void sendMessage(String message) {
        String messageId = UUID.randomUUID().toString();
        String senderId = FirebaseAuth.getInstance().getUid();
        long timestamp = System.currentTimeMillis();

        MessageModel messageModel = new MessageModel(messageId, senderId, message, timestamp);

        messageAdapter.add(messageModel);

        senderReference.child(messageId).setValue(messageModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Clear the EditText after sending the message
                binding.messageEd.setText("");
            }
        });

        receiverReference.child(messageId).setValue(messageModel);
    }
}
