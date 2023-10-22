package com.store.mychat;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;
import com.store.mychat.Adapter.MessageAdapter;
import com.store.mychat.Models.MessageModel;
import com.store.mychat.Models.UserModel;
import com.store.mychat.databinding.ActivityChatBinding;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {
    ActivityChatBinding binding;
    String reciverimg,recemail,reciverName,receiverId  ,senderId, reciverStatus , Status;
    DatabaseReference senderReference, receiverReference ,reference,statusReff;
    MessageAdapter messageAdapter;
    FirebaseAuth auth;
    FirebaseUser crrUser;
    DatabaseReference databaseReference , ref;

    FirebaseStorage storage;
    StorageReference storageReference;
    List<MessageModel> ichat;
    List<MessageModel> mchat;

    Bitmap bitmap;
    Uri url , wallpaper;
    MyFirebase myFB;
    String imgUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        getSupportActionBar().hide();
        myFB = new MyFirebase(this);





//*******************************Receiver Detils********************************************
        auth = FirebaseAuth.getInstance();
        crrUser = auth.getCurrentUser();
//        Storage Initialisation
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference().child("chatImage");

        recemail = getIntent().getStringExtra("Email");
        receiverId = getIntent().getStringExtra("id");
        reciverName = getIntent().getStringExtra("nameeee");
        reciverimg = getIntent().getStringExtra("reciverImg");
        reciverStatus = getIntent().getStringExtra("userStatus");

        DatabaseReference statusRef = FirebaseDatabase.getInstance().getReference().child("users");


//*******************************Details Set*************************************************************************

        statusRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String uid = dataSnapshot.getKey();
                    if (uid.equals(receiverId)) {
                        UserModel userModel = dataSnapshot.getValue(UserModel.class);
                        binding.chatUserStatus.setText(userModel.getStatus());
                    }
                }
                readImages(crrUser.getUid(),receiverId);
                readMessage(crrUser.getUid(),receiverId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, "Failed to load Status: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        senderId = FirebaseAuth.getInstance().getUid();
//        senderRoom = senderId + receiverId;
//        receiverRoom = receiverId + senderId;

        Picasso.get().load(reciverimg).into(binding.UserProfileChat);
        binding.chatUserName.setText(reciverName);


// *********************** Adapter Setup *********************************************************

        binding.recycler.setAdapter(messageAdapter);
        binding.recycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        linearLayoutManager.setStackFromEnd(true);

        binding.recycler.setLayoutManager(linearLayoutManager);

        senderReference = FirebaseDatabase.getInstance().getReference("chats");

//  ******************************Buttons ***************************************************
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatActivity.this,Home_Activity.class));
                finish();
            }
        });

        binding.Emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Emojis are not available ",Toast.LENGTH_SHORT).show();
            }
        });

        binding.imagesendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(getApplicationContext(),"Media send option is not available yet ",Toast.LENGTH_SHORT).show();
                Dexter.withActivity(ChatActivity.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                Intent intent =new Intent(Intent.ACTION_PICK);
                                intent.setType("image/*");
                                startActivityForResult(Intent.createChooser(intent,"Selected Image File"),2);

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

        binding.sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = binding.messageEd.getText().toString().trim();
                if (!message.equals("")) {
                    sendMessage(message , senderId , receiverId);
                }else {
                    Toast.makeText(ChatActivity.this, "You can't send empty message",Toast.LENGTH_SHORT).show();
                }
                binding.messageEd.setText("");

            }
        });

        ref = FirebaseDatabase.getInstance().getReference("users").child(receiverId);

        binding.UserProfileChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(ChatActivity.this);
                View view1 = getLayoutInflater().inflate(R.layout.profile_dialog,null);
                ImageView profilePic = (ImageView) view1.findViewById(R.id.ProfileImage);
                TextView name = (TextView) view1.findViewById(R.id.UserNameClick);

                alert.setView(view1);

                databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(receiverId).child("userProfile");
                Picasso.get().load(reciverimg).into(profilePic);
                name.setText(reciverName);

                final AlertDialog alertDialog = alert.create();
                alertDialog.setCanceledOnTouchOutside(false);

                profilePic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ChatActivity.this , MyProfile.class);
                        intent.putExtra("profile" ,reciverimg);
                        startActivity(intent);
                    }
                });

                alertDialog.show();

            }
        });

        binding.chatUserAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatActivity.this, personAbout.class);
                intent.putExtra("id", receiverId);
                intent.putExtra("nameeee", reciverName);
                intent.putExtra("reciverImg", reciverimg);
                intent.putExtra("userStatus",reciverStatus);
                intent.putExtra("Email",recemail);
                startActivity(intent);
                finish();


            }
        });

//**********************************************************************************************************
    }

    private void sendImage(Uri url , String senderId , String receiverId) {
        String imageId = UUID.randomUUID().toString();
        long timestamp = System.currentTimeMillis();
        reference = FirebaseDatabase.getInstance().getReference();

        Dialog dialog = new Dialog(ChatActivity.this);
        dialog.setContentView(R.layout.loading_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setTitle("Uploading Details");
        dialog.show();

        StorageReference uploader = storage.getReference().child("Img").child("sender:"+senderId + "reciver:" + receiverId +"TS:"+timestamp);

        uploader.putFile(url)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                uploader.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        imgUrl = uri.toString();

                                        HashMap<String , Object> hashMap = new HashMap<>();
                                        hashMap.put("message" , imgUrl);
                                        hashMap.put("receiverId",receiverId);
                                        hashMap.put("senderId",senderId );
                                        hashMap.put("timestamp", timestamp);
                                        hashMap.put("messageId" , imageId);
                                        hashMap.put("isSeen" , false);
                                        hashMap.put("messageType" , "image");
                                        reference.child("image").child(imageId).setValue(hashMap);

                                        Toast.makeText(getApplicationContext(),"Done", Toast.LENGTH_LONG).show();
                                    }
                                });
                                dialog.dismiss();

                            }
                        });

    }
    private void sendMessage(String message , String senderId , String receiverId) {

        String messageId = UUID.randomUUID().toString();
        long timestamp = System.currentTimeMillis();
        reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String , Object> hashMap = new HashMap<>();

        hashMap.put("message" , message);
        hashMap.put("receiverId",receiverId );
        hashMap.put("senderId",senderId );
        hashMap.put("timestamp", timestamp);
        hashMap.put("messageId" , messageId);
        hashMap.put("messageType" , "message");
        hashMap.put("isSeen" , false);

        reference.child("chats").child(messageId).setValue(hashMap);

    }

    public void readImages(String myId , String userId){

        ichat = new ArrayList<MessageModel>() ;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("image");

        reference.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            ichat.clear();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                MessageModel media = snapshot.getValue(MessageModel.class);

                if((media.getReceiverId().equals(myId)  &&  media.getSenderId().equals(userId)  && media.getMessageType().equals("image"))  || ( media.getReceiverId().equals(userId) && media.getSenderId().equals(myId) && media.getMessageType().equals("image"))){
                    ichat.add(media);
                    Log.e(TAG,media.getMessage());

                }

                messageAdapter = new MessageAdapter(ChatActivity.this,ichat,media.getMessageType());
                binding.recycler.setAdapter(messageAdapter);
                messageAdapter.sortMessages();

            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    });

    }

    public void readMessage(String myId , String userId){

        mchat = new ArrayList<>();

        DatabaseReference chatReference = FirebaseDatabase.getInstance().getReference("chats");
        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {

                mchat.clear();
                for(DataSnapshot snapshot : datasnapshot.getChildren()){
                    MessageModel chat = snapshot.getValue(MessageModel.class);
                    if((chat.getReceiverId().equals(myId)  &&  chat.getSenderId().equals(userId)  && chat.getMessageType().equals("message"))  || ( chat.getReceiverId().equals(userId) && chat.getSenderId().equals(myId) && chat.getMessageType().equals("message")))
                    {
                            mchat.add(chat);
                    }

                    messageAdapter = new MessageAdapter(ChatActivity.this,mchat,chat.getMessageType());
                    binding.recycler.setAdapter(messageAdapter);
                    messageAdapter.sortMessages();

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


// *********************************Menus and Options *********************************************
    public void showRecMenu(View view){

        PopupMenu popupMenu = new PopupMenu(this , view);
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.receiver_detail_menu,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                return onPopupMenuClick(menuItem);
            }
        });
        popupMenu.show();

    }
    private boolean onPopupMenuClick(MenuItem item){

        if(item.getItemId() == R.id.del_chat){

            DatabaseReference Del_Chat_reff = FirebaseDatabase.getInstance().getReference().child("chats");

            final AlertDialog.Builder alert = new AlertDialog.Builder(ChatActivity.this);
            View view = getLayoutInflater().inflate(R.layout.del_chat_dialog , null);
            TextView cancel = (TextView) view.findViewById(R.id.cancel_button);
            TextView deleteForMe = (TextView) view.findViewById(R.id.delete_for_me);
            TextView deleteForEveryone = (TextView) view.findViewById(R.id.delete_for_everyone);
            alert.setView(view);
            final AlertDialog alertDialog = alert.create();
            alertDialog.setCanceledOnTouchOutside(true);

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });

            deleteForMe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    senderReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            messageAdapter.clear();

                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                MessageModel messageModel = dataSnapshot.getValue(MessageModel.class);
                                if(messageModel.getReceiverId().equals(receiverId) && messageModel.getSenderId().equals(senderId)){
                                    senderReference.child(messageModel.getMessageId()).removeValue();
                                }
                                if(messageModel.getReceiverId().equals(senderId) && messageModel.getSenderId().equals(receiverId)){
                                    senderReference.child(messageModel.getMessageId()).removeValue();
                                }
                            }
                            // Sort the messages based on timestamps
                            messageAdapter.sortMessages();
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle onCancelled event
                        }
                    });
                    alertDialog.dismiss();
                }
            });

            deleteForEveryone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    senderReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            messageAdapter.clear();

                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                MessageModel messageModel = dataSnapshot.getValue(MessageModel.class);
                                if(messageModel.getReceiverId().equals(receiverId) && messageModel.getSenderId().equals(senderId)){

                                    senderReference.child(messageModel.getMessageId()).removeValue();

                                }
                                if(messageModel.getReceiverId().equals(senderId) && messageModel.getSenderId().equals(receiverId)){
                                    senderReference.child(messageModel.getMessageId()).removeValue();
                                }

                            }

                            // Sort the messages based on timestamps
                            messageAdapter.sortMessages();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle onCancelled event
                        }
                    });

                    alertDialog.dismiss();

                }
            });
            alertDialog.show();

//            Toast.makeText(getApplicationContext(),"Chat deteted from you", Toast.LENGTH_LONG).show();
        } else if (item.getItemId() == R.id.block_user) {

            Toast.makeText(getApplicationContext(),"Block User option is not Available", Toast.LENGTH_LONG).show();
        } else if (item.getItemId() == R.id.view_user) {
            Toast.makeText(getApplicationContext(),"UserView option is not Available", Toast.LENGTH_LONG).show();

        } else if (item.getItemId() == R.id.Wallpaper) {
//            setWallpaper();
            Toast.makeText(this, "Wallpaper", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

//    ******************************Send Media ****************************************************


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == 2 && data != null) {
            url = data.getData();
            try {
// ***************************DialogBox For Media Send**********************************************
                Dialog dialog = new Dialog(ChatActivity.this);
                View view = getLayoutInflater().inflate(R.layout.send_img_dialog , null);
                dialog.setContentView(view);
                dialog.setTitle("Selected Image");
                dialog.show();

                ImageView dialogImg = view.findViewById(R.id.dialogImg);
                ImageView sendImg = view.findViewById(R.id.sendImageButton);

                InputStream inputStream = getContentResolver().openInputStream(url);
                bitmap = BitmapFactory.decodeStream(inputStream);
                dialogImg.setImageBitmap(bitmap);

                sendImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendImage(url ,senderId , receiverId);
                        dialog.dismiss();

                    }
                });


            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error selecting image", Toast.LENGTH_SHORT).show();
            }
        }

//        if (resultCode == RESULT_OK && requestCode == 3 && data != null) {
//
//            wallpaper = data.getData();
//            try{
//                InputStream inputStream = getContentResolver().openInputStream(wallpaper);
//                bitmap = BitmapFactory.decodeStream(inputStream);
//
//
//            }catch (Exception e){
//                e.printStackTrace();
//                Toast.makeText(this, "Error selecting image", Toast.LENGTH_SHORT).show();
//            }
//
//        }
        super.onActivityResult(requestCode, resultCode, data);
    }

//    void setWallpaper(){
//
//        Dexter
//                .withActivity(this)
//                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
//                .withListener(new PermissionListener() {
//                    @Override
//                    public void onPermissionGranted(PermissionGrantedResponse response) {
//                        Intent intent = new Intent(Intent.ACTION_PICK);
//                        intent.setType("image/*");
//                        startActivityForResult(Intent.createChooser(intent, "Select Wallpaper File"), 3);
//
//                    }
//
//                    @Override
//                    public void onPermissionDenied(PermissionDeniedResponse response) {
//
//                    }
//
//                    @Override
//                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
//                        token.continuePermissionRequest();
//                    }
//                });
//
//    }

    @Override
    protected void onResume() {
        super.onResume();
        long lastSeen = 0;
        myFB.setOnlineStatus("Online" , lastSeen);
    }

    @Override
    protected void onPause() {
        super.onPause();
        long lastSeen = System.currentTimeMillis();;
//        statusReff.removeEventListener(seenListener);
        myFB.setOnlineStatus("Offline" , lastSeen);
    }
//****************************************************************

}
