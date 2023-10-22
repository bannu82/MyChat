package com.store.mychat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.store.mychat.ChatActivity;
import com.store.mychat.Dialogbox;
import com.store.mychat.Models.UserModel;
import com.store.mychat.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {
    private Context context;
    private List<UserModel> userModelList;
    private boolean ischat;
    FirebaseAuth auth;


    public UserAdapter(Context context ,List<UserModel> userModelList ){
        this.context = context;
        this.userModelList = userModelList;
    }
    public UserAdapter(Context context, List<UserModel> userModelList,boolean ischat) {
        this.context = context;
        this.ischat = ischat;
        this.userModelList = userModelList;

        }

    public void setIsChat(boolean ischat) {
        this.ischat = ischat;
        notifyDataSetChanged();
    }

    void add(UserModel userModel) {
        userModelList.add(userModel);
        notifyDataSetChanged();
    }

    public void clear() {
        userModelList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_row, parent, false);
        return new UserAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        UserModel userModel = userModelList.get(position);
        holder.name.setText(userModel.getUserName());
        holder.email.setText(userModel.getUserEmail());


        if(userModel.getStatus().equals("Online")){
            holder.online_offline.setText(userModel.getStatus());
        }else if(userModel.getStatus().equals("Offline")){
            holder.online_offline.setText("Last Seen at " + getFormattedTimestamp(userModel.lastSeen));
        }

        // Use Glide to load the profile image
        Glide.with(context)
                .load(userModel.getUserProfile())
                .into(holder.profile);


        holder.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialogbox dialogbox = new Dialogbox(context);
                dialogbox.profileDialog(userModel.getUserProfile());

            }
        });


        if (ischat) {
            if (userModel.getStatus().equals("Online")) {
                holder.img_on.setVisibility(View.VISIBLE);
                holder.img_off.setVisibility(View.GONE);
            } else {
                holder.img_on.setVisibility(View.GONE);
                holder.img_off.setVisibility(View.VISIBLE);
            }
        } else {
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("id", userModel.getUserId());
                intent.putExtra("nameeee", userModel.getUserName());
                intent.putExtra("reciverImg", userModel.getUserProfile());
                intent.putExtra("userStatus",userModel.getUserStatus());
                intent.putExtra("Email",userModel.getUserEmail());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userModelList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView name, email;
        private CircleImageView profile;
        private ImageView img_on, img_off;
        private TextView online_offline;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.userName);
            email = itemView.findViewById(R.id.userEmail);
            profile = itemView.findViewById(R.id.UserProfile);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);

            online_offline = itemView.findViewById(R.id.Status_line);
        }
    }


    private String getFormattedTimestamp(long lastSeen) {
        try {

            long currTime = System.currentTimeMillis();
            if((currTime-lastSeen)<=86400000){
                SimpleDateFormat sdf = new SimpleDateFormat(" h:mm a");
                return sdf.format(new Date(lastSeen));
            }else {
                SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM h:mm a");
                return sdf.format(new Date(lastSeen));
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }




}
