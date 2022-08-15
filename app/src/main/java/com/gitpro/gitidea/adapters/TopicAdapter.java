package com.gitpro.gitidea.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gitpro.gitidea.CustomTextView;
import com.gitpro.gitidea.FireStoreQueries;
import com.gitpro.gitidea.R;
import com.gitpro.gitidea.fragments.BookmarkTopic;
import com.gitpro.gitidea.models.Topic;
import com.gitpro.gitidea.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.TopicImageVH> {

    public static final String TAG="TopicAdapter";
    Activity fActivity;
    List<Topic> myList;
    List<Topic>positionItemList;
    List<String>bookmark_HashSet;
    public static Topic model;
    FirebaseFirestore db;
    View view;
    int id = 0;
    public static String mTag;
    public static String cTag;
    public static String bTag;
    public static String tTag;
    FirebaseAuth mAuth;
    public boolean likeStatus=false;
    public boolean isExist=false;
    public boolean isSelectAll=false;
    public static String userId;
    public mClickListener mClickListener;
    BookmarkTopic bookFragment = new BookmarkTopic();
    public final ItemClickListener onCallItem;

    public interface ItemClickListener{
        void onCallBackItem(Topic topic);
    }

    public interface mClickListener{
        boolean longClick(boolean click);
    }

    public TopicAdapter(Activity fActivity, List<Topic> myList,ItemClickListener onCallItem,mClickListener mClickListener) {
        this.fActivity = fActivity;
        this.myList = myList;
        this.onCallItem=onCallItem;
        this.mClickListener=mClickListener;
        positionItemList=new ArrayList<>();
        bookmark_HashSet=new ArrayList<>();
    }


    @NonNull
    @Override
    public TopicImageVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        LayoutInflater layoutInflater = LayoutInflater.from(fActivity);
           view = layoutInflater.inflate(R.layout.custom_topic1, parent, false);
           TopicImageVH topicImageVH = new TopicImageVH(view, onCallItem);
           id = R.menu.admin_option;
           return topicImageVH;


    }

    @Override
    public void onBindViewHolder(@NonNull TopicImageVH vh, int position) {
        model = getMyList(position);
        vh.bind(model);

        FireStoreQueries.getUser(new FireStoreQueries.FirestoreUsersCallback() {
            @Override
            public void onCallback(User user) {
                if (user.isAdmin) {
                    vh.arrowDown.setVisibility(View.VISIBLE);
                    vh.arrowDown.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            createOptionMenu(model, view, fActivity);
                        }
                    });
                } else {
                    vh.arrowDown.setVisibility(View.INVISIBLE);
                }
                bookmark_HashSet.addAll(user.bookMark);

            }
        });
        Animation animation = AnimationUtils.loadAnimation(fActivity, R.anim.slide_left_to_right);
        vh.itemView.startAnimation(animation);
    }


    @Override
    public int getItemCount() {
        return myList.size();
    }

    public class TopicImageVH extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        public CircleImageView fPicture;
        public CustomTextView fTitle, fDesc,tDate;
        public TextView mLikes, mComments;
        AppCompatImageButton arrowDown;
        ImageView checkItem;
        RelativeLayout deleteLayout;
        public AppCompatImageView imageContent,shareContent, likeContent, commentContent,bookMark;
        public TopicImageVH(@NonNull View itemView,ItemClickListener onCallBack) {
            super(itemView);

            fPicture = itemView.findViewById(R.id.profile_image_topic);
            fTitle = itemView.findViewById(R.id.project_name);
            imageContent = itemView.findViewById(R.id.image_content);
            fDesc = itemView.findViewById(R.id.project_desc);
            deleteLayout=itemView.findViewById(R.id.delete_ly);
            checkItem=itemView.findViewById(R.id.circle_id);
            mLikes = itemView.findViewById(R.id.like_num);
            mComments = itemView.findViewById(R.id.comment_num);
            shareContent = itemView.findViewById(R.id.share_top);
            likeContent = itemView.findViewById(R.id.like_topic);
            commentContent = itemView.findViewById(R.id.comment_topic);
            arrowDown = itemView.findViewById(R.id.arrow_down);
            tDate=itemView.findViewById(R.id.topic_date);
            bookMark=itemView.findViewById(R.id.favorite_list);

        }

        @Override
        public boolean onLongClick(View view) {
            if (mClickListener.longClick(!bookFragment.isActionModeEnabled)) {
                deleteLayout.setVisibility(View.GONE);

            } else {
                deleteLayout.setVisibility(View.VISIBLE);
                checkItem.setVisibility(View.GONE);
                itemView.setBackgroundColor(Color.WHITE);
            }

            if (isSelectAll) {
                checkItem.setVisibility(View.VISIBLE);
                itemView.setBackgroundColor(Color.LTGRAY);
            } else {
                checkItem.setVisibility(View.GONE);
                itemView.setBackgroundColor(Color.WHITE);
            }

            mClickListener.longClick(true);
            return true;
        }


        public void setTopicLike(int counter){
            mLikes.setText(counter+"");
        }
        public void setTopicComments(int counter){
            mComments.setText(counter+"");
        }

        public void bind(Topic topic) {

            if (topic != null) {
                fTitle.setText(topic.userName);
                fDesc.setText(topic.pDescription);
                tDate.setText(topic.date);
                Picasso.get().load(topic.imageProfile).placeholder(android.R.color.transparent).fit()
                        .into(fPicture);
                    Glide.with(fActivity).load(topic.pImage).into(imageContent);

                String topicId= topic.topicId;
                 userId=mAuth.getCurrentUser().getUid();

                commentContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onCallItem.onCallBackItem(topic);
                    }
                });


    FireStoreQueries.getUser(new FireStoreQueries.FirestoreUsersCallback() {
        @Override
        public void onCallback(User user) {
     bookMark.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        isExist=true;
        DocumentReference reference=db.collection("users").document(user.userId);
        db.collection("topics/"+topicId+"/books").document(userId)
            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (isExist){
                            if (!task.getResult().exists()){
                                Map<String,Object>add=new HashMap<>();
                                add.put("date",FieldValue.serverTimestamp());
                                db.collection("topics/"+topicId+"/books")
                                        .document(userId).set(add);
                                ArrayList<String>addBookList= (ArrayList<String>) user.bookMark;
                                addBookList.add(topicId);
                                Map<String,Object>adds=new HashMap<>();
                                adds.put("bookMark",addBookList);
                                Toast.makeText(fActivity,"Added topic to bookmark list",Toast.LENGTH_SHORT).show();
                                reference.update(adds);
                                isExist=false;
                            }
                            else {
                            db.collection("topics/"+topicId+"/books").document(userId)
                                    .delete();
                              ArrayList<String>removeBookList= (ArrayList<String>) user.bookMark;
                                removeBookList.remove(topicId);
                                Map<String,Object>remove=new HashMap<>();
                                remove.put("bookMark",removeBookList);
                               reference.update(remove);
                            }
                            isExist=true;
                        }//end of exist condition
                            }
                         });//endPoint onComplete listener
                       }
                    });
                  }
              });//end of userQuery


    db.collection("topics/"+topicId+"/books").document(userId)
            .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
             public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error==null){
                    if (value.exists()){
                        bookMark.setImageResource(R.drawable.ic_bookmark);
                        bookMark.setTag("saved");

                    }
                    else {
                        bookMark.setImageResource(R.drawable.ic_bookmark_gery);
                        bookMark.setTag("removed");
                    }
                    bTag=(String) bookMark.getTag();
                        }
                    }
                });
    ;

                likeContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        likeStatus = true;
                            db.collection("topics/" + topicId + "/likes").document(userId)
                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (likeStatus) {
                                        if (!task.getResult().exists()) {
                                            Map<String, Object> like = new HashMap<>();
                                            like.put("date", FieldValue.serverTimestamp());
                                            db.collection("topics/" + topicId + "/likes").document(userId)
                                                    .set(like);
                                            likeStatus = false;
                                        } else {
                                            db.collection("topics/" + topicId + "/likes").document(userId)
                                                    .delete();
                                            likeStatus = true;
                                        }
                                    }
                                }
                            });
                        }
                });

                db.collection("topics/" + topicId + "/likes").document(userId)
                        .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                              if (error==null){
                                  if (value.exists()){
                                      likeContent.setImageResource(R.drawable.ic_fill_favorite);
                                      likeContent.setTag("clicked");
                                  }
                                  else {
                                      likeContent.setImageResource(R.drawable.ic_favourite);
                                      likeContent.setTag("unclicked");
                                  }
                                    mTag=(String) likeContent.getTag();

                              }
                            }
                        });

                DocumentReference topicRef=db.collection("topics").document(topicId);
                db.collection("topics/" + topicId + "/likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error==null){
                            if (!value.isEmpty()){
                                int count= value.size();
                                setTopicLike(count);
                                likeContent.setTag(count+"");
                            }
                            else {
                                setTopicLike(0);
                                likeContent.setTag(0+"");
                            }
                            cTag=(String)likeContent.getTag();

                            Map<String,Object>mapLike=new HashMap<>();
                            mapLike.put("numOfPeopleWhoLiked",value.size());
                            topicRef.update(mapLike).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                }
                            });

                        }
                    }
                });
        db.collection("topics/"+topicId+"/comments").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error==null){
                        if (!value.isEmpty()){
                            int count=value.size();
                            setTopicComments(count);
                            commentContent.setImageResource(R.drawable.ic_chat_comment_blue);
                            commentContent.setTag("comment");
                        }
                        else {
                            setTopicComments(0);
                            commentContent.setImageResource(R.drawable.ic_chat_comment_gray);
                            commentContent.setTag("no comment");
                        }
                        tTag=(String)commentContent.getTag();

                        Map<String,Object>mapLike=new HashMap<>();
                        mapLike.put("commentsNum",value.size());
                        topicRef.update(mapLike).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                            }
                        });
                    }
                    }
                });
                if (topic.pImage == null) {
                    imageContent.setVisibility(View.GONE);
                } else {
                    imageContent.setVisibility(View.VISIBLE);
                    Glide.with(itemView.getContext()).load(topic.pImage)
                            .placeholder(android.R.color.transparent).into(imageContent);
                }
                shareContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent myIntent = new Intent(Intent.ACTION_SEND);
                        myIntent.setType("text/plain");
                        String shareSub = fActivity.getString(R.string.sub_title);
                        String shareBody = fActivity.getString(R.string.title_share) + "\n";
                        myIntent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
                        myIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                        fActivity.startActivity(Intent.createChooser(myIntent, fActivity.getString(R.string.using)));

                    }
                });


                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onCallItem.onCallBackItem(topic);
                    }
                });
            }
        }
    }


    public Topic getMyList(int position) {
        return myList.get(position);
    }



    public void createOptionMenu(Topic topic, View v, final Context context){
        //initialize

        PopupMenu deleteMenu=new PopupMenu(context,v);
        deleteMenu.getMenuInflater().inflate(id, deleteMenu.getMenu());
        //callback menuItemListener
        deleteMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
              //getMenu id;
                switch (menuItem.getItemId()){
                    case R.id.delete_topic:
                        db.collection("topics").document(topic.getTopicId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {
                                    Log.d(TAG, "Deleted Topic.");
                                       myList.remove(topic);
                                       notifyDataSetChanged();
                                }
                                else
                                    Log.d(TAG, "Failed!!");
                            }
                        });
                        break;
                   }

                return true;
            }
        });
              deleteMenu.show();

    }

}
