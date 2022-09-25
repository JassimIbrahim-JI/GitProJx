package com.gitpro.gitidea.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gitpro.gitidea.utils.FireStoreQueries;
import com.gitpro.gitidea.R;
import com.gitpro.gitidea.adapters.GalleryAdapter;
import com.gitpro.gitidea.models.Gallery;
import com.r0adkll.slidr.Slidr;

import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {

    ProgressBar progressBar;
    List<Gallery> imageList=new ArrayList<>();
    RecyclerView recyclerView;
    SwipeRefreshLayout refreshLayout;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        Slidr.attach(this);

        recyclerView=findViewById(R.id.gallery_rv);
        refreshLayout=findViewById(R.id.refresh_gallery);
        toolbar=findViewById(R.id.gallery_tb);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(GalleryActivity.this,EditProfileActivity.class);
                startActivity(intent);
            }
        });

        retrieveImage();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                retrieveImage();
            }
        });

    }

    public void retrieveImage(){
        FireStoreQueries.getImages(new FireStoreQueries.FirestoreImagesCallback() {
            @Override
            public void onCallback(List<Gallery> images) {
                imageList=images;
                GalleryAdapter adapter=new GalleryAdapter(imageList);
                adapter.setHasStableIds(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(GalleryActivity.this,RecyclerView.VERTICAL,false));
                recyclerView.setAdapter(adapter);

                if (refreshLayout.isRefreshing()){
                    refreshLayout.setRefreshing(false);
                }
            }
        });

    }




}