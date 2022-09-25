package com.gitpro.gitidea.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.gitpro.gitidea.R;
import com.gitpro.gitidea.ui.ProjectActivity;
import com.gitpro.gitidea.ui.TopicActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

public class TabsFragment extends Fragment {

    TabLayout tabLayout;
    ViewPager2 mViewPager;
    View mView;
  FloatingActionButton fabAddTopic;

    public TabsFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView=inflater.inflate(R.layout.tabs_layout,container,false);
         tabLayout = mView.findViewById(R.id.id_tabs);
        mViewPager = mView.findViewById(R.id.tour_view_pager);
        fabAddTopic =mView.findViewById(R.id.add_topic);

        TopicViewPager pager=new TopicViewPager(mView.getContext(),
        getActivity().getSupportFragmentManager(),getLifecycle());

        pager.addFragment(new TopicFragment());
        pager.addFragment(new ProjectFragment());


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
                       openAc(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mViewPager.setAdapter(pager);
        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
              @Override
              public void onPageSelected(int position) {
                  tabLayout.selectTab(tabLayout.getTabAt(position));
                  openAc(position);
              }
          });

        return mView;
    }
    public void openAc(int pos){
        switch (pos){
            case 0:
                fabAddTopic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(getActivity(), TopicActivity.class);
                        startActivity(intent);
                    }
                });
            break;

            case 1:
                fabAddTopic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent1=new Intent(getActivity(), ProjectActivity.class);
                        startActivity(intent1);
                    }
                });
            break;
        }
    }

}
