package com.gitpro.gitidea.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ContentInfoCompat;
import androidx.core.view.ViewCompat;

import com.bumptech.glide.Glide;
import com.gitpro.gitidea.FireStoreQueries;
import com.gitpro.gitidea.R;
import com.gitpro.gitidea.adapters.TopicAdapter;
import com.gitpro.gitidea.models.Comment;
import com.gitpro.gitidea.models.Topic;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class TopicActivity extends AppCompatActivity  {

   public  AppCompatEditText editText;
    CircleImageView imgProfile;
    LinearLayoutCompat linearLayoutCompat;
   public  LinearLayoutCompat viewGroup;
    private ActivityResultLauncher<Intent> startGalleryLauncher;
    private ActivityResultLauncher<Intent> startSpeakLauncher;
    LinearLayoutCompat llGone;
    View view;
    TextView textCounter;
    TextView cancelTopic;
    Toolbar toolbar;
    static int PReqCode = 1;
    private int shortAnimationDuration;
    ProgressBar progress;
    SlidrInterface slidrInterface;
    FloatingActionButton shareTopic;
    private FirebaseFirestore mFirebaseFirestore;
    DocumentReference newTopicRef;
    Topic mTopic;

    RelativeLayout relativeLayout;
    private static final String TAG="Receiver";
    public static final String[]mimeType=new String[]{"image/*"};
    private StorageReference mStorageContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_topic);

        slidrInterface = Slidr.attach(this);
        mFirebaseFirestore=FirebaseFirestore.getInstance();

        initViews();
        showHideOnKeyboard();
        getUserInfo();
        textChecker();
        setRichContent();
        shareTopic.setOnClickListener(view -> addTopic());
        startResultLauncher();
        setStartSpeakLauncher();
        selectedImage();
        setToolBar();


    }

    public void setToolBar(){
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle(null);
            toolbar.setNavigationContentDescription(R.string.cancel);
            cancelTopic.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   onBackPressed();
               }
           });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
            finish();
    }

    public void setRichContent(){
        @SuppressLint("InflateParams")
        View v=getLayoutInflater().inflate(R.layout.thumbials_view,null);
        AppCompatImageView imageView=v.findViewById(R.id.image_row);
        CircleImageView closeView=v.findViewById(R.id.close_content);
        ViewCompat.setOnReceiveContentListener(editText, mimeType, (view, payload) -> {
            Pair<ContentInfoCompat, ContentInfoCompat> split = payload.partition(item -> item.getUri() != null);
            Log.d(TAG, "OnReceiveContent" + payload.getClip().getDescription());

            if (split.first != null) {
                ClipData clip = split.first.getClip();
                for (int i = 0; i < clip.getItemCount(); i++) {

                    ClipData.Item item = clip.getItemAt(i);
                   Uri uri = item.getUri();
                    Uri imageUri = (split.first.getLinkUri() != null) ? uri : null;
                    Log.e(TAG, "OnReceiveContent" + imageUri);
                    mStorageContent = FirebaseStorage.getInstance().getReference().child("ContentFolder");
                    StorageReference contentName = mStorageContent.child("image" + uri.getLastPathSegment());
                    contentName.putFile(uri).addOnSuccessListener(taskSnapshot -> contentName.getDownloadUrl().addOnSuccessListener(uri1 -> {
                    String myUri= uri1.toString();

                  DocumentReference reference= mFirebaseFirestore.collection("topics").
                   document(newTopicRef.getId());

            Map<String,Object>inputImage=new HashMap<>();
            inputImage.put("pImage",myUri);

            reference.update(inputImage).addOnSuccessListener(unused -> Log.d(TAG,"Successfully updated bookImage !!"));
         }));
                    try {
                        if (imageUri != null) {
                            imageView.setVisibility(View.VISIBLE);
                            closeView.setVisibility(View.VISIBLE);
                            Glide.with(TopicActivity.this).load(imageUri).
                                    placeholder(R.drawable.ic_broken_image)
                                    .into(imageView);
                        }
                    } catch (Exception e) {
                       Log.d(TAG,"OnContent://" +e.getMessage());
                    }
                    closeView.setOnClickListener(view1 -> viewGroup.removeView(v));
                    if (viewGroup.getChildCount() <= 0) {
                        viewGroup.addView(v);
                    }
                }
            }
            return split.second;
        });
    }

      public boolean checkContent(){
        if (String.valueOf(editText.getText()).isEmpty()||String.valueOf(editText.getText()).length()==0||
        viewGroup.getChildCount()>1){
            editText.setError("What do you have to share with us");
            return false;
        }
            return true;

    }

      public String getDate(){
        Calendar calendar=Calendar.getInstance();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat=new SimpleDateFormat(" k:mm a  yyyy/MM/dd");
        return dateFormat.format(calendar.getTime());

    }

      public void textChecker(){
              editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String input=String.valueOf(editText.getText());
                int length=input.length();
                progress.setProgress(length);

                textCounter.setText(String.valueOf(charSequence.length()));
                textCounter.setTextColor(Color.WHITE);
                if (length<=0){
                    textCounter.setText("");

                }

                else if (length>=206&&length<260){
                    textCounter.setTextColor(Color.YELLOW);
                }
                else if (length>=260) {

                    textCounter.setTextColor(Color.RED);
                    textCounter.animate().alpha(1f)
                            .setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                            .setListener(null);


                    progress.animate().alpha(1f)
                            .setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                            .setListener(null);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }

      public void getUserInfo(){
                FireStoreQueries.getUser(user -> {
                    if (user!=null){
                        ColorDrawable colorDrawable=new ColorDrawable(Color.LTGRAY);
                        Picasso.get().load(user.photoUrl).fit().placeholder(colorDrawable).into(imgProfile);
                    }

                });
            }

      public void initViews() {
                linearLayoutCompat=findViewById(R.id.container_id);
                editText = findViewById(R.id.text_topic);
                viewGroup=findViewById(R.id.vg_add_content);
                imgProfile = findViewById(R.id.circle_image);
                llGone=findViewById(R.id.ll_cards);
                progress=findViewById(R.id.pb_counter);
                textCounter=findViewById(R.id.txt_counter);
                shareTopic=findViewById(R.id.share_topic);
                cancelTopic=findViewById(R.id.cancel_topic);
                toolbar=findViewById(R.id.toolbar_addTopic);
                relativeLayout=findViewById(R.id.rl_snacks);
            }

            public void showHideOnKeyboard(){
                view=getWindow().getDecorView().getRootView();
                view.getViewTreeObserver().addOnGlobalLayoutListener((ViewTreeObserver.OnGlobalLayoutListener) () -> {
                    Rect r = new Rect();
                    view.getWindowVisibleDisplayFrame(r);

                    int keyboardHeight = view.getRootView().getHeight() - (r.bottom - r.top);

                    if (keyboardHeight > 244) { // if more than 100 pixels, its probably a keyboard...
                        //ok now we know the keyboard is up
                        shortAnimationDuration = getResources().getInteger(
                                android.R.integer.config_shortAnimTime);

                        llGone.setVisibility(View.GONE);

                    } else {
                        //ok now we know the keyboard is down
                        llGone.setAlpha(0f);
                        llGone.setVisibility(View.VISIBLE);
                        llGone.animate().alpha(1f)
                                .setDuration(shortAnimationDuration).setListener(null);

                    }
                });
            }

            public void startResultLauncher() {
                @SuppressLint("InflateParams")
                View v = getLayoutInflater().inflate(R.layout.thumbials_view, null, false);
                CircleImageView cClose = v.findViewById(R.id.close_content);
                CircleImageView cClose3 = v.findViewById(R.id.close_content3);
                AppCompatImageView cImage = v.findViewById(R.id.image_row);
                VideoView cVideo = v.findViewById(R.id.video_row);
                MediaController mediaController = new MediaController(this);
                mediaController.setAnchorView(cVideo);
                startGalleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult()
                        , new ActivityResultCallback<ActivityResult>() {
                            @Override
                            public void onActivityResult(ActivityResult result) {

                                if (result.getResultCode()==RESULT_OK && result.getData()!=null){
                                Uri selectedImage=result.getData().getData();
                                String uriSelected = TopicActivity.this.getContentResolver().getType(selectedImage);
                                if (uriSelected != null) {

                                    String[] columns = {MediaStore.Images.Media.DATA,
                                            MediaStore.Images.Media.MIME_TYPE};

                                    Cursor cursor = TopicActivity.this.getContentResolver().query
                                            (selectedImage, columns, null, null, null);
                                    cursor.moveToFirst();

                                    int mimeTypeColumnIndex = cursor.getColumnIndex(columns[1]);

                                    String myType = cursor.getString(mimeTypeColumnIndex);
                                    cursor.close();

                                    mStorageContent = FirebaseStorage.getInstance().getReference().child("ContentFolder");
                                    StorageReference contentName = mStorageContent.child("image" + selectedImage.getLastPathSegment());
                                    contentName.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            contentName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    Topic topic= TopicAdapter.model;
                                                    DocumentReference reference = mFirebaseFirestore.collection("topics").
                                                            document(topic.getTopicId());
                                                    Map<String, Object> inputImage = new HashMap<>();
                                                    inputImage.put("pImage", uri.toString());
                                                    reference.update(inputImage).addOnSuccessListener(unused -> Log.d(TAG, "Successfully updated tImage !!"));

                                                }
                                            });
                                        }
                                    });

                                    try {
                                        if (myType.contains("image/png") || myType.contains("image/jpg") || myType.contains("image/jpeg")) {
                                            cClose.setVisibility(View.VISIBLE);
                                            cImage.setVisibility(View.VISIBLE);
                                            Glide.with(TopicActivity.this).load(selectedImage).placeholder(R.drawable.ic_broken_image)
                                                    .into(cImage);
                                        } else if (myType.startsWith("video/")) {
                                            cVideo.setVisibility(View.VISIBLE);
                                            cClose3.setVisibility(View.VISIBLE);

                                            cVideo.setMediaController(mediaController);
                                            cVideo.setVideoURI(selectedImage);
                                            cVideo.start();
                                        } else {
                                            Toast.makeText(TopicActivity.this, "Invalid Path", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (Exception e) {
                                        e.fillInStackTrace();
                                    }

                                    cClose3.setOnClickListener(view -> viewGroup.removeView(v));

                                    cClose.setOnClickListener(view -> viewGroup.removeView(v));
                                    viewGroup.addView(v);

                                }
                                }
                            }
                        });
                    }

            public void selectedImage(){

                CardView pickImage=findViewById(R.id.cv_pick);
                CardView recordIV=findViewById(R.id.cv_record);

                recordIV.setOnClickListener(view -> {
                    checkAndRequestForPermission2();
                Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Convert Speak into text");
                try {
                       startSpeakLauncher.launch(intent);
                }
                catch (Exception e){
                  e.fillInStackTrace();
                    Snackbar.make(relativeLayout,""+e.getMessage(),500).show();
                }

                });

             pickImage.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                  checkAndRequestForPermission();
                  Intent intent=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                     startGalleryLauncher.launch(intent);
                 }
             });
            }

            public void setStartSpeakLauncher(){
              startSpeakLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult()
                      , result -> {

                        if (result.getResultCode()==RESULT_OK && result.getData()!=null){
                           ArrayList<String>textResult=result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                           editText.setText(textResult.get(0));

                        }

                      });
            }

            public void checkAndRequestForPermission() {
                if (ContextCompat.checkSelfPermission(TopicActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED){
                            if (ActivityCompat.shouldShowRequestPermissionRationale(TopicActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                                Toast.makeText(TopicActivity.this,"Please accept for required permission",Toast.LENGTH_SHORT).show();
                            }
                            else {
                                  ActivityCompat.requestPermissions(TopicActivity.this,
                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
                                        , PReqCode);
                            }
                }
                else {
                    Intent intent=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startGalleryLauncher.launch(intent, ActivityOptionsCompat.makeBasic());
                }
            }

            public void checkAndRequestForPermission2() {
        if (ContextCompat.checkSelfPermission(TopicActivity.this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(TopicActivity.this,Manifest.permission.RECORD_AUDIO)){
                Toast.makeText(TopicActivity.this,"Please accept for required permission",Toast.LENGTH_SHORT).show();
            }
            else {
                ActivityCompat.requestPermissions(TopicActivity.this,
                        new String[]{Manifest.permission.RECORD_AUDIO}
                        , PReqCode);
            }
        }
        else {
            Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Convert Speak into text");
            startSpeakLauncher.launch(intent, ActivityOptionsCompat.makeBasic());
        }
    }

            @SuppressLint("ObsoleteSdkInt")
            public void addTopic() {

            List<Comment> comments= new ArrayList<>();
            FireStoreQueries.getUser(user -> {

                newTopicRef=mFirebaseFirestore.collection("topics").document();
                mTopic=new Topic(user.userName,String.valueOf(editText.getText()), user.photoUrl, null,0 ,
                        0, newTopicRef.getId(),comments,getDate());
                newTopicRef.set(mTopic).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Log.d("Topic", "onComplete: " + " Created a new Topic");
                    }
                    else {
                        Log.d("Topic", "onFailed: " + " Failed To create");
                    }
                });
                if (checkContent()) {
                    Intent intent = new Intent(TopicActivity.this, ExploreActivity.class);
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    }
                    onBackPressed();
                }
            });
            }

}

