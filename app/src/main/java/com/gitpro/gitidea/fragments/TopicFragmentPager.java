package com.gitpro.gitidea.fragments;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class TopicFragmentPager extends FragmentStateAdapter {

    Context context;
    ArrayList<Fragment> fragmentList=new ArrayList<>();

    public TopicFragmentPager(@NonNull Context context,@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
        this.context=context;
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
          Fragment fragment;
        switch (position){

            case 0: fragment=new TopicFragment();
            break;

            case 1:  fragment=new ProjectFragment();
            break;

            default: fragment=new TopicFragment();

        }
        return fragment;
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }

    public Fragment addFragment(Fragment fragment){
        fragmentList.add(fragment);
        return fragment;
    }

}
