package com.store.mychat;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {

    private Context context;
    private List<UserModel> userModelList;

    public UserAdapter(Context context) {
        this.context = context;
        userModelList = new ArrayList<>();
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
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        UserModel userModel = userModelList.get(position);
        holder.name.setText(userModel.getUserName());
        holder.email.setText(userModel.getUserEmail());

        Picasso.get().load(userModel.getUserProfile()).into(holder.profile);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,ChatActivity.class);
                intent.putExtra("id",userModel.getUserId());
                intent.putExtra("nameeee",userModel.getUserName());
                intent.putExtra("reciverImg",userModel.getUserProfile());
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

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.userName);
            email = itemView.findViewById(R.id.userEmail);
            profile = itemView.findViewById(R.id.UserProfile);

        }
    }
}
