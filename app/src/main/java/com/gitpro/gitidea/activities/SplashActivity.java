package com.gitpro.gitidea.activities;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.airbnb.lottie.LottieAnimationView;
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

import de.hdodenhof.circleimageview.CircleImageView;


@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = SplashActivity.class.getName();

    private boolean internet_checked = false;
    private boolean flag=false;

    private FirebaseFirestore mFirebaseFirestore;//store data and queries
    private CollectionReference users;//to get document location

    InternetConnectivity connectivity;

    LottieAnimationView lottieAnimation;
    CircleImageView logoApp;
    RelativeLayout _SplashBg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        checkInternetConnection();
        loginFireBase();


         lottieAnimation=findViewById(R.id.img_view);
         logoApp=findViewById(R.id.app_lo);
         _SplashBg=findViewById(R.id.bg_splash);

    }


    @Override
    protected void onStart() {
        super.onStart();

        lottieAnimation.animate().translationY(-1600).setDuration(1000).setStartDelay(3000);
        logoApp.animate().translationY(-1600).setDuration(1000).setStartDelay(3000);
        _SplashBg.animate().translationY(-1600).setDuration(1000).setStartDelay(3000);

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
        mAuth = FirebaseAuth.getInstance();
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

        mFirebaseFirestore = FirebaseFirestore.getInstance();
        users = mFirebaseFirestore.collection("users");
        users.whereEqualTo("email", mAuth.getCurrentUser().getEmail()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<String> bookmarks = new ArrayList<>();
                List<String> toursCommentedOn = new ArrayList<>();
                User mUser;
                if (queryDocumentSnapshots.isEmpty()) {
                    DocumentReference newUserReference = users.document();
                    mUser = new User(mAuth.getCurrentUser().getDisplayName(),
                            mAuth.getCurrentUser().getEmail(),
                            mAuth.getCurrentUser().getPhotoUrl().toString()+"?height=500",
                            bookmarks,
                            toursCommentedOn,
                            newUserReference.getId(),
                            false);

                    newUserReference.set(mUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                                Log.d(TAG, "onComplete: " + " new user registered");
                            else
                                Log.d(TAG, "Failed!!");
                        }
                    });
                } else {
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
