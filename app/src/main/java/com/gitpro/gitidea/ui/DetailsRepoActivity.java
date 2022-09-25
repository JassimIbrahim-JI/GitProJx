package com.gitpro.gitidea.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.gitpro.gitidea.R;
import com.gitpro.gitidea.fragments.DetailsViewPager;
import com.gitpro.gitidea.fragments.FollowersFragment;
import com.gitpro.gitidea.fragments.FollowingFragment;
import com.gitpro.gitidea.models.topics.Item;
import com.google.android.material.tabs.TabLayout;

public class DetailsRepoActivity extends AppCompatActivity {

    private static Item item = null;
    private ImageView backBtn,mNoDataIV;
    private TextView mUserName, mRepoLink, mDescription, mNumberOfForks, mNumberOfWatch,
            mNumberOfStars, mNumberOfIssues, mCreated_At, mUpdate_At, mLanguage, mNoDataMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView( R.layout.activity_details);

        //region init UI and bind those UI to perform UI interactions
        init();
        bindUIWithComponents();
        //endregion
    }

    //region init UI
    private void init() {
     backBtn=findViewById(R.id.back_btn);
     mUserName=findViewById(R.id.UserName);
     mDescription=findViewById(R.id.Description);
     mRepoLink=findViewById(R.id.RepoLink);
     mNumberOfForks=findViewById(R.id.NumberOfForks);
     mNumberOfIssues=findViewById(R.id.NumberOfIssues);
     mNumberOfStars=findViewById(R.id.NumberOfStars);
     mNumberOfWatch=findViewById(R.id.NumberOfWatch);
     mLanguage=findViewById(R.id.Language);
     mNoDataMessage=findViewById(R.id.NoDataMessage);
     mCreated_At=findViewById(R.id.Created_At);
     mUpdate_At=findViewById(R.id.Updated_At);
     mNoDataIV=findViewById(R.id.NoDataIV);
    }
    //endregion

    //region bind those UI to perform UI interactions
    private void bindUIWithComponents() {
        //region back click listener
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //endregion

        //region get data from intent and perform certain operations
        getData();
        if (item != null){
            setViewPager();
            setData();
            if (mNoDataIV.getVisibility() == View.VISIBLE) {
                mNoDataIV.setVisibility(View.GONE);
            }
        }
        else{
            Toast.makeText(DetailsRepoActivity.this, "No Item Found",Toast.LENGTH_LONG).show();
            if (mNoDataIV.getVisibility() == View.GONE) {
                mNoDataIV.setVisibility(View.VISIBLE);
            }
        }
        //endregion
    }
    //endregion

    //region get intent parcelable object
    private void getData(){
        if (getIntent().getExtras().getParcelable("item") != null){
            item = (Item) getIntent().getExtras().getParcelable("item");
            Log.v("Shakil : ", "ScholarshipDetailsActivity : getIntentData: "+item.getFull_name());
        }
    }
    //endregion

    //region set data after getting intent data
    private void setData(){
        mUserName.setText(item.getFull_name());
        mRepoLink.setText(item.getHtml_url());

        if (item.getDescription() == null) mDescription.setText("No description for this repository.");
        else mDescription.setText(item.getDescription());

        mNumberOfForks.setText(""+item.getForks_count());
        mNumberOfStars.setText(""+item.getStargazers_count());
        mNumberOfWatch.setText(""+item.getWatchers_count());

        if (item.getOpen_issues() == null || item.getOpen_issues() == 0) mNumberOfIssues.setText("0");
        else mNumberOfIssues.setText(""+item.getOpen_issues());

        if (item.getLanguage() == null) mLanguage.setText("No language");
        else mLanguage.setText(item.getLanguage());

        if (item.getCreated_at() == null) mCreated_At.setText("No data found");
        else mCreated_At.setText(item.getCreated_at());

        if (item.getUpdated_at() == null) mUpdate_At.setText("No data found");
        else mUpdate_At.setText(item.getUpdated_at());
    }
    //endregion

    //region setup viewpager
    private void setViewPager(){
        ViewPager2 viewPager = findViewById(R.id.viewpager);
        DetailsViewPager adapter = new DetailsViewPager(getSupportFragmentManager(), getLifecycle(), item.getFull_name());
       adapter.addFragment(new FollowersFragment());
       adapter.addFragment(new FollowingFragment());
        TabLayout tabLayout =findViewById(R.id.sliding_tabs);
        viewPager.setAdapter(adapter);
       tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
           @Override
           public void onTabSelected(TabLayout.Tab tab) {
               viewPager.setCurrentItem(tab.getPosition());
           }

           @Override
           public void onTabUnselected(TabLayout.Tab tab) {

           }

           @Override
           public void onTabReselected(TabLayout.Tab tab) {

           }
       });
       viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
           @Override
           public void onPageSelected(int position) {
               super.onPageSelected(position);
               tabLayout.selectTab(tabLayout.getTabAt(position));
           }
       });
    }
   // endregion

    //region activity components
    @Override
    public void onBackPressed() {
        if (getIntent().getStringExtra("from").equals("android")){
            startActivity(new Intent(DetailsRepoActivity.this, RepoActivity.class));
        }
        else if (getIntent().getStringExtra("from").equals("Home")) {
            startActivity(new Intent(DetailsRepoActivity.this, ExploreActivity.class));
        }

    }
    //endregion
}