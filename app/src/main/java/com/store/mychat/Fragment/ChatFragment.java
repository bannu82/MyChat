package com.store.mychat.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.store.mychat.Adapter.UserAdapter;
import com.store.mychat.Models.MessageModel;
import com.store.mychat.Models.UserModel;
import com.store.mychat.R;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    private DatabaseReference chatsReference;
    private UserAdapter userAdapter;
    private FirebaseUser currentUser;

    private List<String> userList;
    private RecyclerView recyclerView;

    public ChatFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        userList = new ArrayList<>();

        // Reference to the "chats" node
        chatsReference = FirebaseDatabase.getInstance().getReference("chats");



        // ...
        chatsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();


                for (DataSnapshot chatSnapshot : dataSnapshot.getChildren()) {
                    MessageModel chat = chatSnapshot.getValue(MessageModel.class);


                    if (chat != null) {
                        String senderId = chat.getSenderId();
                        String receiverId = chat.getReceiverId();

                        if (currentUser != null && senderId != null && receiverId != null) {

                            if (senderId.equals(currentUser.getUid())) {
                                userList.add(receiverId);
                            } else if (receiverId.equals(currentUser.getUid())) {
                                userList.add(senderId);
                            }
                        }
                    }
                }

                // Fetch user data and update the RecyclerView
                readUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur during database retrieval
            }
        });

        return view;
    }

    private void readUsers() {
        DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("users");
        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<UserModel> mUser = new ArrayList<>();

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    UserModel user = userSnapshot.getValue(UserModel.class);

                    // Add the user to the mUser list if their ID is in the userList
                    if (user != null && userList.contains(user.getUserId())) {
                        mUser.add(user);
                    }
                }

                // Update the RecyclerView with the user data
                userAdapter = new UserAdapter(getContext(), mUser, true);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur during database retrieval
            }
        });
    }
}
