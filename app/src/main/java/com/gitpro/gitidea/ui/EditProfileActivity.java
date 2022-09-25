package com.gitpro.gitidea.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.gitpro.gitidea.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG ="EditProfileActivity" ;
    TextInputEditText inputEditText1,inputEditText2;
  TextView changePhoto,_Save;
  ImageView closeChange,imageH;
  CircleImageView userPhotoUrl;

   private FirebaseFirestore mAuthDB;
   private FirebaseUser mUser;
    static int PReqCode = 1;
    private ActivityResultLauncher<Intent> startGalleryLauncher;
    private StorageReference mStorageContent;
    Toolbar toolbar;
    LinearLayoutCompat layoutCompat;
    public static int llPadding=30;
    ProgressBar mProgressBar;
    private AlertDialog.Builder builder;
    private  AlertDialog dialog;
    TextView tvText;
    private Uri imageSelected;
    private String displayName=null;
    private String imageHeader;
    ImageView changeHeader;
    public String userId;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);


        mAuthDB=FirebaseFirestore.getInstance();
        mUser=FirebaseAuth.getInstance().getCurrentUser();
        mStorageContent=FirebaseStorage.getInstance().getReference().child("images_url");



        toolbar=findViewById(R.id.edit_tb);
        setSupportActionBar(toolbar);
        inputEditText1=findViewById(R.id.textField);
        inputEditText2=findViewById(R.id.textField2);
        userPhotoUrl=findViewById(R.id.edit_ph);
        closeChange=findViewById(R.id.close_editp);
        _Save=findViewById(R.id.save_change_tv);
        changePhoto=findViewById(R.id.change_photo_tv);
        changeHeader=findViewById(R.id.change_header);
        imageH=findViewById(R.id.header_tab2);

        SharedPreferences preferences=getSharedPreferences("PREFS",Context.MODE_PRIVATE);
        imageHeader=preferences.getString("imageHeader","none");

        profileStartGalleryLauncher();
        userInfo();


        _Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateData();

            }
        });

        closeChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        changeHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(EditProfileActivity.this,GalleryActivity.class);
                startActivity(intent);
            }
        });

        changePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAndRequestForPermission();
         Intent openGallery=new Intent(Intent.ACTION_GET_CONTENT);
         openGallery.setType("image/*");
         startGalleryLauncher.launch(openGallery);
            }
        });
    }
public void userInfo(){
    userId=mUser.getUid();
        mAuthDB.collection("users")
                .document(userId)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
               inputEditText1.setText(documentSnapshot.getString("userName"));
               inputEditText2.setText(documentSnapshot.getString("userBio"));

               Picasso.get().load(documentSnapshot.getString("photoUrl"))
                       .noPlaceholder().into(userPhotoUrl);

               Glide.with(getApplicationContext())
                       .load(documentSnapshot.getString("userHeader"))
                       .into(imageH);
            }
        });


}

      public void updateData(){
          String input1 = inputEditText1.getText().toString();
          String input2 = inputEditText2.getText().toString();

          if (!TextUtils.isEmpty(input1) || !TextUtils.isEmpty(input2)) {

          showProgressBar();
          FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
          if (firebaseUser!=null) {
              UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                      .setDisplayName(input1)
                      .build();
              firebaseUser.updateProfile(request)
                      .addOnSuccessListener(new OnSuccessListener<Void>() {
                          @Override
                          public void onSuccess(Void unused) {
                              hideProgressBar();
                              Map<String,Object>upData=new HashMap<>();
                              upData.put("userName",input1);
                              upData.put("userBio",input2);
                              DocumentReference userRef=mAuthDB.collection("users").document(userId);
                              userRef.update(upData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                  @Override
                                  public void onSuccess(Void unused) {
                                      hideProgressBar();
                                      Toast.makeText(EditProfileActivity.this,"User bio up to date",Toast.LENGTH_SHORT)
                                              .show();
                                      onBackPressed();
                                  }
                              }).addOnFailureListener(new OnFailureListener() {
                                  @Override
                                  public void onFailure(@NonNull Exception e) {
                                      Toast.makeText(EditProfileActivity.this,"Failed to update",Toast.LENGTH_SHORT)
                                              .show();
                                  }
                              });

                          }
                      });
          }
          }
      }

        public void profileStartGalleryLauncher(){
        startGalleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult()
                , new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {

                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            imageSelected = result.getData().getData();
                            String uriSelected = EditProfileActivity.this.getContentResolver().getType(imageSelected);
                            if (uriSelected != null) {
                            uploadImageFireStorage(imageSelected);
                            }
                        }
                    }
           });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
      finish();
    }

    public void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(EditProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(EditProfileActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(EditProfileActivity.this,"Please accept for required permission",Toast.LENGTH_SHORT).show();
            }
            else {
                ActivityCompat.requestPermissions(EditProfileActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
                        , PReqCode);
            }
        }
        else {
            Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startGalleryLauncher.launch(intent);
        }
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


    public void uploadImageFireStorage(Uri imageUri) {

        StorageReference contentName = mStorageContent.child("image" + imageUri.getLastPathSegment());
showProgressBar();
                contentName.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        contentName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                FirebaseUser mUser=FirebaseAuth.getInstance().getCurrentUser();
                                if(mUser!=null) {
                                    UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                            .setPhotoUri(uri)
                                            .build();
                                    mUser.updateProfile(request)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    hideProgressBar();
                                                    Picasso.get().load(uri.toString()).placeholder(R.mipmap.ic_launcher_round)
                                                            .into(userPhotoUrl);
                                                    Log.d(TAG, "Successfully updated photoUrl !!");
                                                }
                                            });
                                }
                                        DocumentReference reference = mAuthDB.collection("users").
                                                document(userId);
                                        Map<String, Object> inputImage = new HashMap<>();
                                        inputImage.put("photoUrl", uri.toString());
                                        Picasso.get().load(uri.toString()).placeholder(R.mipmap.ic_launcher_round)
                                                .into(userPhotoUrl);
                                        reference.update(inputImage).addOnSuccessListener(unused -> Log.d(TAG, "Successfully updated tImage !!"));

                            }
                        });
                    }
                });

    }
}