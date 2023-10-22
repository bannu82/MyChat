package com.store.mychat.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.store.mychat.Fragment.CallFragment;
import com.store.mychat.Fragment.ChatFragment;
import com.store.mychat.Fragment.StatusFragment;

public class VPAdapter extends FragmentPagerAdapter {

    public VPAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }



    @Override
    public Fragment getItem(int position) {
        if(position == 0){

           return new ChatFragment();
        }else if(position == 1 ){



            return new StatusFragment();
        } else {
            return new CallFragment();
        }

    }

    @Override
    public int getCount() {
        return 3;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        if(position == 0){
            return "Chat";
        } else if (position == 1) {
            return "Status";
        }else {
            return "Call";
        }


    }
}
