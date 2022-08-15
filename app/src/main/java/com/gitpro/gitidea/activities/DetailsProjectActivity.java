package com.gitpro.gitidea.activities;

import static com.gitpro.gitidea.activities.DetailsTopicActivity.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.overflowarchives.linkpreview.SkypePreview;
import com.overflowarchives.linkpreview.ViewListener;
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

public class DetailsProjectActivity extends AppCompatActivity {

    String userIdP;
    String projectId=null;
    FirebaseFirestore db;
    AppCompatEditText commentInput;
    CommentAdapter commentAdapter;
    ImageView shareComment;
    CircleImageView imageProfile;
    CustomTextView username,desc;
    TextView tvDate,tvLike,tvComment;
    AppCompatImageView ivLike,ivShare,ivComment;
    SkypePreview ivContent;
    Toolbar toolbar;
    RecyclerView commentRecyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    List<Comment> comments;
    FirebaseAuth mAuth;
    private boolean likeStatus=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_project_activitiy);
        Slidr.attach(this);
        initViews();
        setToolbar();
        readData();
        comments = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        mAuth= FirebaseAuth.getInstance();
        initComment();
        documents();
        listeners();

    }

    public void documents(){
        //comments filtering in project ref
        Query queryFilter2=db.collection("projects/"+projectId+"/comments").
                orderBy("date", Query.Direction.DESCENDING);
        queryFilter2.addSnapshotListener(new EventListener<QuerySnapshot>() {
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
        });//end of subCollection comments in projects ref
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

    private void initViews() {
        swipeRefreshLayout=findViewById(R.id.refresh_commentsP);
        commentRecyclerView=findViewById(R.id.comments_listP);
        toolbar=findViewById(R.id.detailedP_toolbar);
        shareComment=findViewById(R.id.comment_post_buttonP);
        commentInput=findViewById(R.id.comment_inputP);
        imageProfile=findViewById(R.id.profile_image_detailP);
        tvDate=findViewById(R.id.detail_dateP);
        username=findViewById(R.id.detail_nameP);
        desc=findViewById(R.id.detail_descP);
        tvComment=findViewById(R.id.comment_num_detailP);
        tvLike=findViewById(R.id.like_num_detailP);
        ivComment=findViewById(R.id.comment_detailP);
        ivContent=findViewById(R.id.detail_contentP);
        ivLike=findViewById(R.id.like_detailP);
        ivShare=findViewById(R.id.share_detailP);

    }

    public void readData(){
        projectId=getIntent().getExtras().getString("projectId");
        String nameP=getIntent().getExtras().getString("usernameP");
        String descP=getIntent().getExtras().getString("descP");
        String urlPreview=getIntent().getExtras().getString("urlPreview");
        String urlP=getIntent().getExtras().getString("urlP");
        String date=getIntent().getExtras().getString("dateP");
        userIdP=getIntent().getExtras().getString("userIdP");
        int commentNumP=getIntent().getExtras().getInt("commentNumP");
        String ivCommentP=getIntent().getExtras().getString("ivCommentP");
        username.setText(nameP);
        desc.setText(descP);
        tvDate.setText(date);
       ivContent.loadUrl(urlPreview, new ViewListener() {
           @Override
           public void onPreviewSuccess(boolean b) {

           }

           @Override
           public void onFailedToLoad(@Nullable Exception e) {

           }
       });
        tvComment.setText(commentNumP+"");
        Picasso.get().load(urlP).into(imageProfile);

        if (ivCommentP.equals("comment")){
            ivComment.setImageResource(R.drawable.ic_chat_comment_blue);
        }
        else {
            ivComment.setImageResource(R.drawable.ic_chat_comment_gray);
        }
    }


    public void listeners(){


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
                        comment.put("date",getCurrentDate());
                        comment.put("photoProfile", user.photoUrl);
                        db.collection("projects/" + projectId + "/comments").add(comment)
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
                        toursCommentedOn.add(projectId);
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

    db.collection("projects/"+projectId+"/likes").document(userIdP)
         .addSnapshotListener(new EventListener<DocumentSnapshot>() {
             @Override
             public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                 if (value.exists()) {
                     ivLike.setImageResource(R.drawable.ic_fill_favorite);
                    }

                 else{
                     ivLike.setImageResource(R.drawable.ic_favourite);
                     }
                }
            });

    db.collection("projects/"+projectId+"/likes")
         .addSnapshotListener(new EventListener<QuerySnapshot>() {
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
            db.collection("projects/"+projectId+"/likes").document(userIdP)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (likeStatus) {
                        if (!task.getResult().exists()) {
                            Map<String, Object> maps = new HashMap<>();
                            maps.put("date", FieldValue.serverTimestamp());
                            db.collection("projects/" + projectId + "/likes")
                                    .document(userIdP).set(maps);
                            likeStatus=false;
                        }
                        else {
                            db.collection("projects/"+projectId+"/likes")
                                    .document(userIdP).delete();
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

        commentAdapter=new CommentAdapter(DetailsProjectActivity.this,comments,projectId);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(DetailsProjectActivity.this,LinearLayoutManager.VERTICAL,false));
        commentRecyclerView.setHasFixedSize(true);
        commentRecyclerView.setAdapter(commentAdapter);

        if (swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private String getCurrentDate() {
        long millis = System.currentTimeMillis();
        Date date = new Date(millis);
        @SuppressLint("SimpleDateFormat") DateFormat format = new SimpleDateFormat("dd MMMM");
        return format.format(date);
    }


}