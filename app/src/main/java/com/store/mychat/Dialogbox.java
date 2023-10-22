package com.store.mychat;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


public class Dialogbox {
    Context context;
    String profile;
    public Dialogbox(Context context){
    this.context = context;

    }

    public void profileDialog(String profile){
        Dialog dialog = new Dialog(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.profile_dialog,null);
        dialog.setContentView(view);

        ImageView IV = view.findViewById(R.id.ProfileImage);

        Picasso.get().load(profile).into(IV);
        dialog.show();

    }

}
