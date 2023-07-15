package com.store.mychat;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    private Context context;
    private List<MessageModel> messageModelList;

    public MessageAdapter(Context context) {
        this.context = context;
        messageModelList = new ArrayList<>();
    }

    void add(MessageModel messageModel) {
        messageModelList.add(messageModel);
        notifyDataSetChanged();
    }

    public void clear() {
        messageModelList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MessageModel messageModel = messageModelList.get(position);
        holder.msg.setText(messageModel.getMessage());

        if (messageModel.getSenderId().equals(FirebaseAuth.getInstance().getUid())) {
            holder.main.setGravity(Gravity.END);
            holder.main.setBackgroundColor(context.getResources().getColor(R.color.screen));
            holder.msg.setTextColor(context.getResources().getColor(R.color.white));
        } else {
            holder.main.setGravity(Gravity.START);
            holder.main.setBackgroundColor(context.getResources().getColor(R.color.screen));
            holder.msg.setTextColor(context.getResources().getColor(R.color.white));
        }

        // Set timestamp
        long timestamp = messageModel.getTimestamp();
        holder.timestamp.setText(getFormattedTimestamp(timestamp));
    }

    @Override
    public int getItemCount() {
        return messageModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView msg;
        private TextView timestamp;
        private LinearLayout main;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            msg = itemView.findViewById(R.id.message);
            timestamp = itemView.findViewById(R.id.timestamp);
            main = itemView.findViewById(R.id.mainMessageLayout);
        }
    }

    // Helper method to format timestamp to a readable format
    private String getFormattedTimestamp(long timestamp) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
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
}
