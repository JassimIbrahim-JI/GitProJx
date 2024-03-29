package com.gitpro.gitidea.fragments;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class DetailsViewPager extends FragmentStateAdapter {
    private Context context;
    private String userName;
    private List<Fragment> selectedFragment=new ArrayList<>();
    public DetailsViewPager(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, String userName) {
        super(fragmentManager, lifecycle);
        this.userName=userName;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        switch (position){

            case 0: fragment=new FollowersFragment();
                break;

            case 1:  fragment=new FollowingFragment();
                break;

            default: fragment=new FollowersFragment();

        }
        return fragment;
    }

    @Override
    public int getItemCount() {
        return selectedFragment.size();
    }
    public void addFragment(Fragment fragment){
        selectedFragment.add(fragment);
    }

      }
