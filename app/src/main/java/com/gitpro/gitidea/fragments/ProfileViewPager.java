package com.gitpro.gitidea.fragments;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class ProfileViewPager extends FragmentStateAdapter {


    ArrayList<Fragment> fragmentList=new ArrayList<>();

    public ProfileViewPager(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
       Fragment fragment;
       switch (position){
           case 0:fragment=new ActivitiesFragment();
           break;
           case 1:fragment=new RepliesFragment();
           break;
           case 2:fragment=new LikeFragment();
               break;
           default:fragment=new ActivitiesFragment();
       }
        return fragment;
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }
    public void addFragment(Fragment fragment){
        fragmentList.add(fragment);
    }
}
