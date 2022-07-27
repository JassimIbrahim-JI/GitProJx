package com.gitpro.gitidea;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;


import com.gitpro.gitidea.fragments.ProjectFragment;
import com.gitpro.gitidea.fragments.TopicFragment;

import java.util.ArrayList;
import java.util.List;

public class SwipeFragment extends FragmentStateAdapter {

    List<Fragment> fragmentList=new ArrayList<>();
    Fragment myFragment;




    public SwipeFragment(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);

    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position){
            case 0: myFragment=new TopicFragment();
            break;

            case 1: myFragment=new ProjectFragment();
            break;

        }


        return myFragment;
    }


    public void createFragmentPosition(Fragment fragment){
        fragmentList.add(fragment);
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }
}
