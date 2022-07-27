package com.gitpro.gitidea.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.gitpro.gitidea.CustomTextView;
import com.gitpro.gitidea.FireStoreQueries;
import com.gitpro.gitidea.R;
import com.gitpro.gitidea.adapters.CommentAdapter;
import com.gitpro.gitidea.models.Comment;
import com.gitpro.gitidea.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.r0adkll.slidr.Slidr;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailedActivity extends AppCompatActivity{

public static final String TAG=DetailedActivity.class.getName();

    Toolbar toolbar;
    RecyclerView commentRecyclerView;
    CommentAdapter commentAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
     FirebaseAuth mAuth;
     String userId;
    FirebaseFirestore db;
    AppCompatEditText commentInput;
    ImageView shareComment;
    CircleImageView imageProfile;
    CustomTextView username,desc;
    TextView tvDate,tvLike,tvComment;
    AppCompatImageView ivContent,ivLike,ivShare,ivComment;
    String topicId=null;
    List<Comment>comments;
    private boolean likeStatus=false;
    @SuppressLint("SetTextI18n")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);
        Slidr.attach(DetailedActivity.this);
        attachViews();
        setToolbar();
        readData();
        comments = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();
        initComment();
        documents();
        listeners();

    }
    public void documents(){


        //comments filtering
        Query queryFilter=db.collection("topics/"+topicId+"/comments").
                orderBy("date", Query.Direction.DESCENDING);
        queryFilter.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentChange change:value.getDocumentChanges()){
                    if (change.getType() == DocumentChange.Type.ADDED){
                        Comment comment=change.getDocument().toObject(Comment.class);
                        comments.add(comment);
                        ivComment.setImageResource(R.drawable.ic_chat_comment_blue);
                        tvComment.setText(value.size()+"");
                        commentAdapter.notifyDataSetChanged();
                    }
                    else {
                        ivComment.setImageResource(R.drawable.ic_chat_comment_gray);
                        tvComment.setText(0+"");
                        commentAdapter.notifyDataSetChanged();
                    }

                }
            }
        });//end of subCollection comments


    }

    public void readData(){

        topicId=getIntent().getExtras().getString("bTopicId");
        userId=getIntent().getExtras().getString("userId");
        String name=getIntent().getExtras().getString("username");
        username.setText(name);
        String descr=getIntent().getExtras().getString("desc");
        desc.setText(descr);
        String date=getIntent().getExtras().getString("date");
        tvDate.setText(date);
        String url=getIntent().getExtras().getString("url");
        Picasso.get().load(url).placeholder(android.R.color.transparent)
                .into(imageProfile);
        String image=getIntent().getExtras().getString("image");
        Glide.with(this).load(image)
                .into(ivContent);
        int commentNum=getIntent().getExtras().getInt("commentNum");

        tvComment.setText(commentNum+"");

       String tagComment=getIntent().getExtras().getString("ivComment");
       if (tagComment.equals("comment")){
           ivComment.setImageResource(R.drawable.ic_chat_comment_blue);
       }
       else {
           ivComment.setImageResource(R.drawable.ic_chat_comment_gray);
       }
        System.out.println(topicId);

    }
    public void attachViews(){
        swipeRefreshLayout=findViewById(R.id.refresh_comments);
        commentRecyclerView=findViewById(R.id.comments_list);
        toolbar=findViewById(R.id.detailed_toolbar);
        shareComment=findViewById(R.id.comment_post_button);
        commentInput=findViewById(R.id.comment_input);
        imageProfile=findViewById(R.id.profile_image_detail);
        tvDate=findViewById(R.id.detail_date);
        username=findViewById(R.id.detail_name);
        desc=findViewById(R.id.detail_desc);
        tvComment=findViewById(R.id.comment_num_detail);
        tvLike=findViewById(R.id.like_num_detail);
        ivComment=findViewById(R.id.comment_detail);
        ivContent=findViewById(R.id.detail_content);
        ivLike=findViewById(R.id.like_detail);
        ivShare=findViewById(R.id.share_detail);

    }
    public void listeners(){
        commentInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // do something, e.g. set your TextView here via .setText()
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        shareComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FireStoreQueries.getUser(new FireStoreQueries.FirestoreUsersCallback() {
                    @Override
                    public void onCallback(User user) {

                        String commentText = commentInput.getText().toString();
                        if (!commentText.isEmpty()) {
                            Map<String, Object> comment = new HashMap<>();
                            comment.put("user", user.userName);
                            comment.put("comment", commentText);
                            comment.put("date", getCurrentDate());
                            comment.put("photoProfile", user.photoUrl);
                            db.collection("topics/" + topicId + "/comments").add(comment)
                                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "onComplete: Comment Added ");
                                            } else {
                                                Log.d(TAG, "onFailure: Comment Failed");
                                            }
                                        }
                                    });

                            Map<String, Object> userUpdate = new HashMap<>();
                            List<String> toursCommentedOn = user.toursCommentedOn;
                            toursCommentedOn.add(topicId);
                            userUpdate.put("toursCommentedOn", toursCommentedOn);

                            DocumentReference userRefernce = db.collection("users").document(user.userId);
                            userRefernce.update(userUpdate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: user info updated");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG, "onFailure: ", e);
                                }
                            });

                        }
                        commentInput.setText("");
                    }

                });
            }
        });

        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(Intent.ACTION_SEND);
                myIntent.setType("text/plain");
                String shareSub = getString(R.string.sub_title);
                String shareBody = getString(R.string.title_share) + "\n";
                myIntent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
                myIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(myIntent, getString(R.string.using)));
            }
        });

        db.collection("topics/"+topicId+"/likes").document(userId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value.exists()) {
                                ivLike.setImageResource(R.drawable.ic_fill_favorite);
                        }
                        else {
                                ivLike.setImageResource(R.drawable.ic_favourite);
                            }

                    }
                });

        db.collection("topics/"+topicId+"/likes").
                addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (!value.isEmpty()) {
                            tvLike.setText(value.size()+"");
                        }
                        else {
                            tvLike.setText(0 + "");
                        }


                    }
                });

        ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                likeStatus=true;
               db.collection("topics/"+topicId+"/likes").document(userId)
                       .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                   @Override
                   public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                       if (likeStatus) {
                           if (!task.getResult().exists()) {
                               Map<String, Object> maps = new HashMap<>();
                               maps.put("date", FieldValue.serverTimestamp());
                               db.collection("topics/" + topicId + "/likes")
                                       .document(userId).set(maps);
                               likeStatus=false;
                           }
                           else {
                             db.collection("topics/"+topicId+"/likes")
                                     .document(userId).delete();
                              likeStatus=true;
                           }
                       }
                   }

               });

            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initComment();
            }
        });
    }
    
    public void initComment(){

    commentAdapter=new CommentAdapter(DetailedActivity.this,comments,topicId);
    commentRecyclerView.setLayoutManager(new LinearLayoutManager(DetailedActivity.this,LinearLayoutManager.VERTICAL,false));
    commentRecyclerView.setHasFixedSize(true);
    commentRecyclerView.setAdapter(commentAdapter);

    if (swipeRefreshLayout.isRefreshing()){
        swipeRefreshLayout.setRefreshing(false);
    }
 }


     public void setToolbar(){

         setSupportActionBar(toolbar);
         getSupportActionBar().setDisplayShowTitleEnabled(true);
         getSupportActionBar().setDisplayHomeAsUpEnabled(true);
         toolbar.setNavigationOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 onBackPressed();
             }
         });
     }


    private String getCurrentDate() {
        long millis = System.currentTimeMillis();
        Date date = new Date(millis);
        @SuppressLint("SimpleDateFormat") DateFormat format = new SimpleDateFormat("dd MMMM");
        return format.format(date);
    }


}