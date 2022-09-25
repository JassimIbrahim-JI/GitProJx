package com.gitpro.gitidea.ui;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
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

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.res.ResourcesCompat;

import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.gitpro.gitidea.R;
import com.gitpro.gitidea.models.User;
import com.gitpro.gitidea.network.InternetConnectivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = SplashActivity.class.getName();

    private boolean internet_checked = false;
    private boolean flag=false;

    private CollectionReference users;//to get document location

    private FirebaseFirestore mFirebaseFirestore;//store data and queries

    InternetConnectivity connectivity;

    LinearLayoutCompat layoutCompat;
    public static int llPadding=30;
    ProgressBar mProgressBar;
    TextView tvText;
    private AlertDialog.Builder builder;
    private  AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth=FirebaseAuth.getInstance();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        checkInternetConnection();
        loginFireBase();


    }

    public void showProgressbar(){
        if(builder==null){

         layoutCompat=new LinearLayoutCompat(this);
         layoutCompat.setOrientation(LinearLayoutCompat.HORIZONTAL);
         layoutCompat.setPadding(llPadding,llPadding,llPadding,llPadding);
         layoutCompat.setGravity(Gravity.CENTER);
         LinearLayoutCompat.LayoutParams params=new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
         , ViewGroup.LayoutParams.WRAP_CONTENT);
         params.gravity=Gravity.CENTER;
         layoutCompat.setBackgroundColor(ResourcesCompat.getColor(getResources(),R.color.fui_transparent,null));
         layoutCompat.setLayoutParams(params);


         mProgressBar=new ProgressBar(this);
         mProgressBar.setIndeterminate(true);
         mProgressBar.setPadding(0,0,llPadding,0);
         mProgressBar.setProgressTintList(ColorStateList.valueOf(Color.BLACK));
         mProgressBar.setIndeterminate(true);
         mProgressBar.setIndeterminateTintList(ColorStateList.valueOf(ResourcesCompat.getColor(getResources(),R.color.dark_late_gray,null)));
         mProgressBar.setLayoutParams(params);

            tvText = new TextView(this);
            tvText.setText("Loading ...");
            tvText.setTextColor(ResourcesCompat.getColor(getResources(),R.color.dark_late_gray,null));
            tvText.setTextSize(20);
            tvText.setLayoutParams(params);

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
            //    window.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.spring_dot_stroke_background,null));
                dialog.getWindow().setAttributes(layoutParams);
            }

        }
    }
    public void hideProgressbar(){
    if (dialog!=null&& dialog.isShowing()){
        dialog.dismiss();
    }
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
        dialog.setContentView(R.layout.failed_connection_layout);
        AppCompatButton retryBtn=dialog.findViewById(R.id.btn_retry);
           dialog.setCancelable(false);

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

    @Override
    protected void onResume() {
        super.onResume();

        if (internet_checked){
            mAuth.addAuthStateListener(mAuthListener);
        }
        else {
            retryDialog();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAuth.removeAuthStateListener(mAuthListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideProgressbar();
    }

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    onSignInResult(result);
                }
            }
    );

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            Toast.makeText(this, "sign in Successful", Toast.LENGTH_SHORT).show();
            setUsers();
        }
        else {
            if (response==null){
                Toast.makeText(this, "sign in canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
            else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK){
                retryDialog();
                Log.e(TAG, String.valueOf(Objects.requireNonNull(response.getError()).getErrorCode()));
            }

        }
    }


    public void loginFireBase(){
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                if(user == null) {

                    Log.d(TAG, "onAuthStateChanged:signed_out:");
                    flag=true;

                    List<String>scopes=new ArrayList<String>(){
                        {
                            add("user:email");

                        }
                    };

                    Map<String, String> customParams = new HashMap<>();
                    customParams.put("login","");

                    List<AuthUI.IdpConfig> providers = Arrays.asList(
                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                            new AuthUI.IdpConfig.GitHubBuilder().setScopes(scopes).
                                    setCustomParameters(customParams).build()
                    );

                    AuthMethodPickerLayout builder=new AuthMethodPickerLayout.Builder(R.layout.activity_login).
                            setGithubButtonId(R.id.github_sign).setGoogleButtonId(R.id.google_sign).build();

                    Intent signInIntent=AuthUI.getInstance().createSignInIntentBuilder().
                            setAvailableProviders(providers).setIsSmartLockEnabled(false,true).
                            setTheme(R.style.AppThemeFirebaseAuth).setAuthMethodPickerLayout(builder).build();

                    signInLauncher.launch(signInIntent);

                }
                else {

                    if (!flag){
                        setUsers();
                    }

                    Log.d(TAG, "onAuthStateChanged:signed_in"+user.getUid());

                }

            }
        };

    }

    public void setUsers(){

        showProgressbar();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        users = mFirebaseFirestore.collection("users");
        users.whereEqualTo("email", mAuth.getCurrentUser().getEmail()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<String> bookmarks = new ArrayList<>();
                List<String> bookmarks2 = new ArrayList<>();
                List<String> contentUser = new ArrayList<>();
                List<String> toursCommentedOn = new ArrayList<>();
                List<String>userImages=new ArrayList<>();
                User mUser;
                if (queryDocumentSnapshots.isEmpty()) {
                    DocumentReference newUserReference = users.document(mAuth.getCurrentUser().getUid());
                    mUser = new User(mAuth.getCurrentUser().getDisplayName(),
                            mAuth.getCurrentUser().getEmail(),
                            mAuth.getCurrentUser().getPhotoUrl().toString()+"?height=500",
                            null,
                            bookmarks,
                            bookmarks2,
                            toursCommentedOn,
                            mAuth.getCurrentUser().getUid(),
                            false,
                            contentUser,
                            userImages,
                            null);
                    newUserReference.set(mUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SplashActivity.this, "Welcome back", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "onComplete: " + " new user registered");
                            }
                            else {
                                hideProgressbar();
                                Log.d(TAG, "Failed!!");
                            }
                        }
                    });
                }
                else{
                    User user = queryDocumentSnapshots.getDocuments().get(0).toObject(User.class);

                if (!user.photoUrl.equals(mAuth.getCurrentUser().getPhotoUrl().toString()) || !user.userName.equals(mAuth.getCurrentUser().getDisplayName())) {
                    final DocumentReference userRef = mFirebaseFirestore.collection("users").document(user.userId);

                    Map<String, Object> updatedData = new HashMap<>();
                    updatedData.put("photoUrl", mAuth.getCurrentUser().getPhotoUrl().toString() + "?height=500");
                    updatedData.put("userName", mAuth.getCurrentUser().getDisplayName());

                    userRef.update(updatedData).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "onSuccess: user updated successfully");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: Failed to update user image");
                        }
                    });
                }
            }

                Intent in=new Intent(SplashActivity.this,ExploreActivity.class);
                startActivity(in);
                finish();
            }
        });
    }


}
