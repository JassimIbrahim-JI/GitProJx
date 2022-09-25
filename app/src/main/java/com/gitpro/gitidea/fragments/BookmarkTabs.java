package com.gitpro.gitidea.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.gitpro.gitidea.R;
import com.google.android.material.tabs.TabLayout;

public class BookmarkTabs extends Fragment {

    TabLayout tabLayout;
    ViewPager2 mViewPager;
    View view;

    public BookmarkTabs(){}


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_bookmark,container,false);
        tabLayout=view.findViewById(R.id.book_tabs);
        mViewPager=view.findViewById(R.id.bm_view_pager);

        BookmarkViewPager pager=new BookmarkViewPager(getActivity().getSupportFragmentManager()
        , getActivity().getLifecycle());
        pager.addFragments(new BookmarkTopic());
        pager.addFragments(new BookmarkProject());
        mViewPager.setAdapter(pager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });


        return view;
   }
}
