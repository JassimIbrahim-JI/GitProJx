package com.gitpro.gitidea.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.gitpro.gitidea.R;
import com.gitpro.gitidea.models.Slider;

import java.util.List;

public class SwiperActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private List<Slider> list;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swiper);
//
//        viewPager=findViewById(R.id.view_pager);
//
//        final int pos = getIntent().getIntExtra("pos", 0);
//
//        Singleton singleton = Singleton.getInstance();
//
//        list = new ArrayList<>();
//        list = singleton.getUris();
//
//        mAttachmentAdapter= new UrisAttachmentAdapter( this,list);
//
//        viewPager.setAdapter(mAttachmentAdapter);
//        viewPager.setCurrentItem(pos);



    }
}
