package com.gitpro.gitidea.activities;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.firebase.ui.auth.AuthUI;
import com.gitpro.gitidea.CustomTextView;
import com.gitpro.gitidea.FireStoreQueries;
import com.gitpro.gitidea.R;
import com.gitpro.gitidea.fragments.AboutUsFragment;
import com.gitpro.gitidea.fragments.BookmarkTabs;
import com.gitpro.gitidea.fragments.HomeFragment;
import com.gitpro.gitidea.fragments.ProfileFragment;
import com.gitpro.gitidea.fragments.TabsFragment;
import com.gitpro.gitidea.models.User;
import com.gitpro.gitidea.network.InternetConnectivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class ExploreActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    BottomNavigationView bNavigationView;
    NavigationView dNavigationView;
    BottomAppBar appBar;

    public Toolbar toolbar;
     public AppBarLayout appBarLayout;

    CircleImageView imgHeader;
    CustomTextView nameHeader, emailHeader;

    LinearLayoutCompat layoutCompat;
    public static int llPadding=30;
    ProgressBar mProgressBar;
    TextView tvText;
    private AlertDialog.Builder builder;
    private  AlertDialog dialog;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //fragments_tags
    private final String HOME_FRAGMENT_TAG = "home";

    InternetConnectivity connectivity;
    private boolean internet_checked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        getUserInfo();
        setToolbar();
        initViews(savedInstanceState);
        initNavigationComponents();
        userStatus();



    }

    public void userStatus(){
        mAuth=FirebaseAuth.getInstance();
        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=mAuth.getCurrentUser();
                if (user==null){
                    Intent intent=new Intent(ExploreActivity.this,SplashActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
    }

    public void showProgressBar(){
        if (builder==null){
            layoutCompat =new LinearLayoutCompat(this);
            layoutCompat.setOrientation(LinearLayoutCompat.HORIZONTAL);
            layoutCompat.setPadding(llPadding,llPadding,llPadding,llPadding);
            layoutCompat.setGravity(Gravity.CENTER);
            LinearLayoutCompat.LayoutParams llParam=new LinearLayoutCompat.LayoutParams(
                    LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                    LinearLayoutCompat.LayoutParams.WRAP_CONTENT
            );
            llParam.gravity=Gravity.CENTER;
            layoutCompat.setLayoutParams(llParam);

            mProgressBar=new ProgressBar(this);
            mProgressBar.setIndeterminate(true);
            mProgressBar.setPadding(0, 0, llPadding, 0);
            mProgressBar.setLayoutParams(llParam);

            llParam = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            llParam.gravity = Gravity.CENTER;
            tvText = new TextView(this);
            tvText.setText("Loading ...");
            tvText.setTextColor(Color.parseColor("#000000"));
            tvText.setTextSize(20);
            tvText.setLayoutParams(llParam);

            layoutCompat.addView(mProgressBar);
            layoutCompat.addView(tvText);

            builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setView(layoutCompat);

             dialog = builder.create();
            dialog.show();
            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(dialog.getWindow().getAttributes());
                layoutParams.width = LinearLayoutCompat.LayoutParams.WRAP_CONTENT;
                layoutParams.height = LinearLayoutCompat.LayoutParams.WRAP_CONTENT;
                dialog.getWindow().setAttributes(layoutParams);
            }

        }

    }

    public void hideProgressBar(){
        if (dialog!=null && dialog.isShowing()){
            dialog.dismiss();

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        hideProgressBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
       if (internet_checked) {
           mAuth.addAuthStateListener(mAuthListener);
       }
       else {
           retryDialog();
       }
    }

    public void getUserInfo() {
                FireStoreQueries.getUser(new FireStoreQueries.FirestoreUsersCallback() {
                    @Override
                    public void onCallback(User user) {

                        nameHeader = dNavigationView.getHeaderView(0).findViewById(R.id.name_header);
                        imgHeader = dNavigationView.getHeaderView(0).findViewById(R.id.img_header);
                        emailHeader = dNavigationView.getHeaderView(0).findViewById(R.id.email_header);

                        if (user != null) {
                            if (user.photoUrl != null) {
                                new DownloadImageTask().execute(user.photoUrl);
                            }
                            nameHeader.setText(user.userName);
                            emailHeader.setText(user.email);

                            Log.d("TAG", "email: " + user.email);
                        }
                        else {
                            imgHeader.getLayoutParams().width=(getResources().getDisplayMetrics().widthPixels/100)*64;
                            imgHeader.setImageResource(R.mipmap.ic_launcher);
                        }


                    }
                });
    }



    @SuppressLint({"NonConstantResourceId"})
    private void initNavigationComponents() {

        String PROFILE_FRAGMENT_TAG = "profile";
        String MARK_FRAGMENT_TAG = "topic_mark";

        dNavigationView = findViewById(R.id.navigation_view);
        dNavigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {

                case R.id.profile_id:
                  if (getSupportFragmentManager().findFragmentByTag(PROFILE_FRAGMENT_TAG)!=null){
                      getSupportFragmentManager().beginTransaction().
                              show(getSupportFragmentManager().findFragmentByTag(PROFILE_FRAGMENT_TAG)).commit();
                  }
                  else {
                      getSupportFragmentManager().beginTransaction().
                              replace(R.id.fragment_container, new ProfileFragment(), PROFILE_FRAGMENT_TAG).commit();
                      appBar.setVisibility(View.GONE);
                  }

                  if (getSupportFragmentManager().findFragmentByTag(HOME_FRAGMENT_TAG)!=null){
                      getSupportFragmentManager().beginTransaction().
                              hide(getSupportFragmentManager().findFragmentByTag(HOME_FRAGMENT_TAG)).commitNow();
                  }
                  if (getSupportFragmentManager().findFragmentByTag(MARK_FRAGMENT_TAG)!= null){
                      getSupportFragmentManager().beginTransaction().
                              hide(getSupportFragmentManager().findFragmentByTag(MARK_FRAGMENT_TAG)).commitNow();
                  }
                    break;

                case R.id.mark_id:
                if (getSupportFragmentManager().findFragmentByTag(MARK_FRAGMENT_TAG)!=null){
                    getSupportFragmentManager().beginTransaction().
                            show(getSupportFragmentManager().findFragmentByTag(MARK_FRAGMENT_TAG)).commitNow();
                }
                else {
                    getSupportFragmentManager().beginTransaction().
                            replace(R.id.fragment_container,new BookmarkTabs(),MARK_FRAGMENT_TAG).commitNow();
                    appBar.setVisibility(View.GONE);
                }

                    if (getSupportFragmentManager().findFragmentByTag(HOME_FRAGMENT_TAG)!=null){
                        getSupportFragmentManager().beginTransaction().
                                hide(getSupportFragmentManager().findFragmentByTag(HOME_FRAGMENT_TAG)).commitNow();
                    }
                    if (getSupportFragmentManager().findFragmentByTag(PROFILE_FRAGMENT_TAG)!= null){
                        getSupportFragmentManager().beginTransaction().
                                hide(getSupportFragmentManager().findFragmentByTag(PROFILE_FRAGMENT_TAG)).commitNow();
                    }

                    break;

                case R.id.log_out:
                   signOut();
                    break;

                case R.id.explore_id:
                    if (getSupportFragmentManager().findFragmentByTag(HOME_FRAGMENT_TAG)!=null){
                        getSupportFragmentManager().beginTransaction().
                                show(getSupportFragmentManager().findFragmentByTag(HOME_FRAGMENT_TAG)).commitNow();
                    }
                    else {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new HomeFragment(),HOME_FRAGMENT_TAG).commitNow();
                        bNavigationView.setSelectedItemId(R.id.discover_id);
                        appBar.setVisibility(View.VISIBLE);
                    }
                    if (getSupportFragmentManager().findFragmentByTag(PROFILE_FRAGMENT_TAG)!=null){
                        getSupportFragmentManager().beginTransaction().
                                hide(getSupportFragmentManager().findFragmentByTag(PROFILE_FRAGMENT_TAG)).commitNow();
                    }
                    if (getSupportFragmentManager().findFragmentByTag(MARK_FRAGMENT_TAG)!= null){
                        getSupportFragmentManager().beginTransaction().
                                hide(getSupportFragmentManager().findFragmentByTag(MARK_FRAGMENT_TAG)).commitNow();
                    }
                    break;
            }
            DrawerLayout drawerLayout3 = findViewById(R.id.drawerLayout);
            drawerLayout3.closeDrawer(GravityCompat.START);
            return true;


        });
        appBar=findViewById(R.id.bottom_ba);
        bNavigationView = findViewById(R.id.bottom_bar);
        bNavigationView.setBackgroundColor(getColor(android.R.color.transparent));
        bNavigationView.setOnItemSelectedListener(navigationBarListener);

    }


    public void checkInternetConnection(){
        connectivity=new InternetConnectivity(this);
        if (connectivity.isConnectToNetwork()){
            retryDialog();
        }
        else {
            internet_checked=true;

        }
    }//end checkInternetConnection Method

    @SuppressLint("ResourceType")
    public void retryDialog() {
        Dialog dialog=new Dialog(this);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.failed_connection_layout);
        AppCompatButton retryBtn=dialog.findViewById(R.id.btn_retry);
        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialog.isShowing()){
                    dialog.dismiss();
                }
                checkInternetConnection();
                onResume();
            }
        });
       dialog.show();
    }




    @SuppressLint("NonConstantResourceId")
    private final NavigationBarView.OnItemSelectedListener navigationBarListener = item -> {

        Fragment selectedFragment ;

        switch (item.getItemId()) {
            case R.id.topic_id:
                    selectedFragment = new TabsFragment();

                break;
            case R.id.favi_id:
                selectedFragment = new AboutUsFragment();
                break;
            default:selectedFragment=new HomeFragment();
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commitNow();
        return true;
    };


    @SuppressLint("RestrictedApi")
    public void signOut() {
        Log.d("", "firebaseAuthWithGoogle:" + AuthUI.getInstance().getAuth().getCurrentUser().getUid());
       showProgressBar();

        AuthUI.getInstance()
                .signOut(this);
        mAuth.addAuthStateListener(mAuthListener);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("", "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }


    @SuppressLint("NonConstantResourceId")
    private void initViews(Bundle savedInstanceState) {

        DrawerLayout drawerLayout1 = findViewById(R.id.drawerLayout);

        if (savedInstanceState == null) {
            int fragmentId = getIntent().getIntExtra("FRAGMENT_ID", 0);
            Bundle bundle = new Bundle();
            bundle.putInt("TARGET_FRAGMENT_ID", fragmentId);
            HomeFragment homeFragment=new HomeFragment();
            FragmentManager fragmentManager=getSupportFragmentManager();
            FragmentTransaction transaction= fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container,new HomeFragment(), HOME_FRAGMENT_TAG).commitNow();
            homeFragment.setArguments(bundle);

        }
        checkInternetConnection();

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout1, toolbar,
                R.string.appbar_scrolling_view_behavior, R.string.appbar_scrolling_view_behavior);

        drawerLayout1.addDrawerListener(drawerToggle);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        drawerToggle.setDrawerIndicatorEnabled(false);
        drawerToggle.setToolbarNavigationClickListener(view -> drawerLayout1.openDrawer(GravityCompat.START));
        drawerToggle.setHomeAsUpIndicator(R.drawable.ic_drawer);
        drawerToggle.syncState();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void setToolbar() {
        appBarLayout = findViewById(R.id.main_appbar);
        toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setTitle(0);
            appBarLayout.setBackgroundColor(getColor(android.R.color.transparent));
        }

    }



    @SuppressLint("StaticFieldLeak")
    public  class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {


       @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap mIcon = null;

            try {
                InputStream inputStream = new URL(strings[0]).openStream();
                mIcon = BitmapFactory.decodeStream(inputStream);

            } catch (Exception e) {
                e.fillInStackTrace();
                Log.e("DownloadImageTask", e.getMessage());
            }

            return mIcon;
        }


        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
             imgHeader=dNavigationView.getHeaderView(0).findViewById(R.id.img_header);
             imgHeader.getLayoutParams().width=(getResources().getDisplayMetrics().widthPixels/100)*24;
             imgHeader.setImageBitmap(bitmap);
            }
        }
    }
}