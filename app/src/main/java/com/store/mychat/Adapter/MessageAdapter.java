package com.store.mychat.Adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.store.mychat.Models.MessageModel;
import com.store.mychat.R;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    private Context context;
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private String messageType;
    FirebaseUser fUser;
    private List<MessageModel> messageModelList ;

    public MessageAdapter(Context context , List<MessageModel> messageModelList , String messageType) {
        this.context = context;
        this.messageModelList = messageModelList;
        this.messageType = messageType;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT ) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sender_row, parent, false);
            return new MyViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.receiver_row, parent, false);
            return new MyViewHolder(view);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {



        MessageModel messageModel = messageModelList.get(position);


        if(messageModel.getMessageType().equals("image")){
            holder.msg.setVisibility(View.GONE);
            holder.imgMsg.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(messageModel.getMessage())
                    .into(holder.imgMsg);
        }

        if (messageModel.getMessageType().equals("message")) {
            holder.imgMsg.setVisibility(View.GONE);
            holder.msg.setVisibility(View.VISIBLE);
            holder.msg.setText(messageModel.getMessage());
        }


        // Check the type of message and set the gravity for the layout
        int viewType = getItemViewType(position);
        if (viewType == MSG_TYPE_RIGHT) {

            holder.main.setGravity(Gravity.END);
        } else {
            holder.main.setGravity(Gravity.START);
        }



//        MessageModel messageModel = messageModelList.get(position);
//        holder.msg.setText(messageModel.getMessage());
//        long timestamp = messageModel.getTimestamp();
//        holder.timestamp.setText(getFormattedTimestamp(timestamp));
//
//        // Check the type of message and set the gravity for the layout
//        int viewType = getItemViewType(position);
//        if (viewType == MSG_TYPE_RIGHT) {
//            holder.main.setGravity(Gravity.END);
//        } else {
//            holder.main.setGravity(Gravity.START);
//        }

    }





    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView msg;
        private TextView timestamp;

        private RelativeLayout main;
        private ImageView imgMsg;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            msg = itemView.findViewById(R.id.message);
            timestamp = itemView.findViewById(R.id.timestamp);
            main = itemView.findViewById(R.id.mainMessageLayout);
            imgMsg = itemView.findViewById(R.id.messageImg);
        }
    }

    public int getItemViewType(int position) {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (messageModelList.get(position).getSenderId().equals(fUser.getUid())) {
            return MSG_TYPE_RIGHT; // Return the view type for the sender's messages
        } else {
            return MSG_TYPE_LEFT; // Return the view type for the receiver's messages
        }
    }



    @Override
    public int getItemCount() {return messageModelList.size();}
    // Helper method to format timestamp to a readable format
    private String getFormattedTimestamp(long timestamp) {
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
            return sdf.format(new Date(timestamp));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    // Method to sort the messages based on their timestamps
    public void sortMessages() {
        messageModelList.sort(new Comparator<MessageModel>() {
            @Override
            public int compare(MessageModel message1, MessageModel message2) {
                return Long.compare(message1.getTimestamp(), message2.getTimestamp());
            }
        });
        notifyDataSetChanged();
    }

    void add(MessageModel messageModel) {
        messageModelList.add(messageModel);
        notifyDataSetChanged();
    }
    public void clear() {
        messageModelList.clear();
        notifyDataSetChanged();
    }

}
