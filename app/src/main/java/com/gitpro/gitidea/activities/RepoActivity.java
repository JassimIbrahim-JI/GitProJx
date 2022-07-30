package com.gitpro.gitidea.activities;

import static com.gitpro.gitidea.models.repos.AndroidGitRepository.ALL_TOPICS_BASE_URL;
import static com.gitpro.gitidea.utils.UtilsManager.hasConnection;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.gitpro.gitidea.R;
import com.gitpro.gitidea.adapters.AllTopicAdapter;
import com.gitpro.gitidea.models.viewmodels.AndroidRepoViewModel;
import com.gitpro.gitidea.models.topics.Item;
import com.gitpro.gitidea.network.UX;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RepoActivity extends AppCompatActivity {

    private static final String TAG = "Shakil::RepoActivity";
    public UX ux;
    private ArrayList<Item> androidTopicList;
    private String[] androidFilterList = new String[]{"Select Query","Layouts","Drawing",
            "Navigation","Scanning","RecyclerView","ListView","Image Processing","Binding","Debugging"};
    private AndroidRepoViewModel androidRepoViewModel;
    private AllTopicAdapter allTopicAdapter;
    private Spinner FilterSpinner;
    private RecyclerView androidTopicRecyclerView;
    private TextView NoData;
    private ImageView NoDataIv,backBtn;
    AdView adView;
    ShimmerFrameLayout shimmerFrameLayout;
    private CircleImageView refreshList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        bindUIWithComponents();
        MobileAds.initialize(this);

    }
    //region init UI components
    public void initView(){
        androidTopicList = new ArrayList<>();
        ux = new UX(this);
        FilterSpinner=findViewById(R.id.FilterSpinner);
        androidTopicRecyclerView=findViewById(R.id.mRecyclerView);
        NoData=findViewById(R.id.NoDataMessage);
       NoDataIv=findViewById(R.id.NoDataIV);
       refreshList=findViewById(R.id.RefreshList);
       adView=findViewById(R.id.adView);
       backBtn=findViewById(R.id.BackButton);
       shimmerFrameLayout=findViewById(R.id.shimmerFrameLayout);
       androidRepoViewModel=new ViewModelProvider(this).get(AndroidRepoViewModel.class);
    }
    //endregion

    //region bind UI components
    private void bindUIWithComponents() {
        //region load first time data
        if (!hasConnection(RepoActivity.this)) {
            noDataVisibility(false);
            performServerOperation("android");
        }
        else{
            noDataVisibility(true);
            Toast.makeText(getApplicationContext(), getString(R.string.please_Enable_internet_connection), Toast.LENGTH_LONG).show();
        }
        //endregion

        loadListView();

        //region refresh and spinner filter
      refreshList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasConnection(RepoActivity.this)) {
                    noDataVisibility(false);
                    androidTopicRecyclerView.setVisibility(View.GONE);
                    performServerOperation("android");
                }
                else{
                    noDataVisibility(true);
                    Toast.makeText(getApplicationContext(), getString(R.string.please_Enable_internet_connection), Toast.LENGTH_LONG).show();
                }
            }
        });

        FilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String queryString = adapterView.getItemAtPosition(position).toString();
                if (!queryString.equals("Select Query")) {
                    if (!hasConnection(RepoActivity.this)) {
                        noDataVisibility(false);
                        androidTopicRecyclerView.setVisibility(View.GONE);
                        performServerOperation("android"+queryString);
                    }
                    else{
                        noDataVisibility(true);
                        Toast.makeText(getApplicationContext(), getString(R.string.please_Enable_internet_connection), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //endregion

        //region toolbar on back click listener
       backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RepoActivity.this,ExploreActivity.class));
            }
        });
        //endregion

        ux.setSpinnerAdapter(FilterSpinner,androidFilterList);

        //region adMob
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                Log.v("onInitComplete","InitializationComplete");
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        adView.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                Log.v("onAdListener","AdlLoaded");
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                Log.v("onAdListener","AdFailedToLoad Error "+adError.getMessage());
            }

            @Override
            public void onAdOpened() {
                Log.v("onAdListener","AdOpened");
            }

            @Override
            public void onAdClicked() {
                Log.v("onAdListener","AdClicked");
            }

            @Override
            public void onAdClosed() {
                Log.v("onAdListener","AdClosed");
            }
        });
        //endregion
    }
    //endregion

    //region load list data
    private void loadListView(){
        allTopicAdapter = new AllTopicAdapter(androidTopicList, this, R.layout.shimmer_placeholder_adapter_topics);
        androidTopicRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        androidTopicRecyclerView.setItemAnimator(new DefaultItemAnimator());
        androidTopicRecyclerView.setAdapter(allTopicAdapter);
        allTopicAdapter.notifyDataSetChanged();
        allTopicAdapter.setOnItemClickListener(new AllTopicAdapter.onItemClickListener() {
            @Override
            public void respond(Item androidItem) {
                Intent intent = new Intent(RepoActivity.this , DetailsRepoActivity.class);
                intent.putExtra("from","android");
                intent.putExtra("item", androidItem);
                startActivity(intent);
            }
        });
    }
    //endregion

    //region perform mvvm server fetch
    private void performServerOperation(String queryString){
        //region start the shimmer layout
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmerAnimation();
        ///endregion
        androidRepoViewModel.getData(this,ALL_TOPICS_BASE_URL , queryString);
        androidRepoViewModel.getAndroidRepos().observe(this, new Observer<List<Item>>() {
            @Override
            public void onChanged(List<Item> items) {
                shimmerFrameLayout.stopShimmerAnimation();
             shimmerFrameLayout.setVisibility(View.GONE);
                if (items != null) {
                    androidTopicList = new ArrayList<>(items);
                    androidTopicRecyclerView.setVisibility(View.VISIBLE);
                    loadListView();
                    allTopicAdapter.notifyDataSetChanged();
                    noDataVisibility(false);
                }
                else {
                    Toast.makeText(getApplicationContext(), getString(R.string.no_data_message), Toast.LENGTH_LONG).show();
                    noDataVisibility(true);
                }
            }
        });
    }
    //endregion

    //region set no data related components visible
    private void noDataVisibility(boolean shouldVisible){
        if (shouldVisible) {
            androidTopicRecyclerView.setVisibility(View.GONE);
            shimmerFrameLayout.setVisibility(View.GONE);
            shimmerFrameLayout.stopShimmerAnimation();
            NoData.setVisibility(View.VISIBLE);
            NoDataIv.setVisibility(View.VISIBLE);
        } else {
            androidTopicRecyclerView.setVisibility(View.VISIBLE);
            NoData.setVisibility(View.GONE);
            NoDataIv.setVisibility(View.GONE);
        }
    }
    //endregion

    //region activity components
    @Override
    public void onBackPressed() {
        startActivity(new Intent(RepoActivity.this, ExploreActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}