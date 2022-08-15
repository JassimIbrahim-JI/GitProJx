package com.gitpro.gitidea.fragments;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class BookmarkFragmentPager extends FragmentStateAdapter {

   List<Fragment>fragments;

    public BookmarkFragmentPager(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
        fragments=new ArrayList<>();
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
       Fragment fragment;
       switch (position){
           case 0:fragment=new BookmarkTopic();
           return fragment;
           case 1:fragment=new BookmarkProject();
           return fragment;
           default: fragment=new BookmarkTopic();
       }

        return fragment;
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }
    public void addFragments(Fragment fragment){
        fragments.add(fragment);
    }
}
