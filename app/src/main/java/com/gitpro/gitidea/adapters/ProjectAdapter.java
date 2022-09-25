package com.gitpro.gitidea.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.gitpro.gitidea.customs.CustomTextView;
import com.gitpro.gitidea.utils.FireStoreQueries;
import com.gitpro.gitidea.R;
import com.gitpro.gitidea.models.Notification;
import com.gitpro.gitidea.models.Project;
import com.gitpro.gitidea.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.overflowarchives.linkpreview.SkypePreview;
import com.overflowarchives.linkpreview.ViewListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectVH> {

    private static final String TAG = "ProjectAdapter";
    Context context;
    public static Project project;
    List<Project> projectList;
    List<String>bookmark_HashSet;
    View view;
    int id = 0;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirebase;
    public boolean likeStatus=false;
    public boolean isExist=false;
    public static String userId;
    public static String mTag;
    public static String cTag;
    public static String bTag;
    public static String tTag;

    public final ItemClickProjectListener onCallItem;

    public interface ItemClickProjectListener {
        void onCallBackItem(Project project);
    }


    public ProjectAdapter(Context context, List<Project> projectList, ItemClickProjectListener onCallItem) {
        this.context = context;
        this.projectList = projectList;
        this.onCallItem = onCallItem;
        bookmark_HashSet=new ArrayList<>();
    }

    @NonNull
    @Override
    public ProjectVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.custom_project, parent, false);
        ProjectVH projectVH = new ProjectVH(view);
        mAuth = FirebaseAuth.getInstance();
        mFirebase = FirebaseFirestore.getInstance();
        id = R.menu.admin_option;

        return projectVH;
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectVH holder, int position) {
        project = projectList.get(position);
        holder.bind(project);


        FireStoreQueries.getUser(new FireStoreQueries.FirestoreUsersCallback() {
            @Override
            public void onCallback(User user) {
                if (user.isAdmin) {
                    holder.arrowDown.setVisibility(View.VISIBLE);
                    holder.arrowDown.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            holder.createOptionMenu(project, view, context);
                        }
                    });
                } else {
                    holder.arrowDown.setVisibility(View.INVISIBLE);
                }
                bookmark_HashSet.addAll(user.bookMark2);

            }
        });

    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }

    public class ProjectVH extends RecyclerView.ViewHolder {

        public CircleImageView pPicture;
        public CustomTextView pTitle, pDesc, pDate;
        public TextView mLikes, mComments,projectLanguage;
        AppCompatImageButton arrowDown;
        public AppCompatImageView shareContent, likeContent, commentContent,bookMark;
        private SkypePreview imageContent;

        public ProjectVH(@NonNull View itemView) {
            super(itemView);

            pPicture = itemView.findViewById(R.id.profile_image_project);
            pTitle = itemView.findViewById(R.id.project_name2);
            pDesc = itemView.findViewById(R.id.project_desc2);
            pDate = itemView.findViewById(R.id.project_date);
            imageContent = itemView.findViewById(R.id.image_content2);
            mLikes = itemView.findViewById(R.id.like_num_project);
            mComments = itemView.findViewById(R.id.comment_num2);
            projectLanguage=itemView.findViewById(R.id.project_lang);
            arrowDown = itemView.findViewById(R.id.arrow_down);
           bookMark=itemView.findViewById(R.id.favorite_list2);
            shareContent = itemView.findViewById(R.id.share_iic);
            likeContent = itemView.findViewById(R.id.like_iic);
            commentContent = itemView.findViewById(R.id.comment_ic);

        }

        public void setProjectLike(int counter){
            mLikes.setText(counter+"");
        }
        public void setProjectComments(int counter){
            mComments.setText(counter+"");
        }

        public void bind(Project project) {
            if (project != null) {


                imageContent.loadUrl(project.urlPreview, new ViewListener() {
                    @Override
                    public void onPreviewSuccess(boolean b) {

                    }

                    @Override
                    public void onFailedToLoad(@Nullable Exception e) {
                        Log.d("OnUrlLoad:", e.getMessage());
                    }
                });
                FirebaseUser mUser=mAuth.getCurrentUser();
                if (mUser!=null){
                    pTitle.setText(mUser.getDisplayName());
                    Picasso.get().load(mUser.getPhotoUrl()).placeholder(android.R.color.transparent)
                            .into(pPicture);
                }
                String projectId = project.projectId;
                userId = mAuth.getCurrentUser().getUid();
                mLikes.setText(project.numOfPeopleWhoLiked + "");
                mComments.setText(project.commentsNum + "");
                pDate.setText(project.pDate);
                pDesc.setText(project.pDescription);
                projectLanguage.setText(project.projectLanguage);
        commentContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCallItem.onCallBackItem(project);
            }
        });
        FireStoreQueries.getUser(new FireStoreQueries.FirestoreUsersCallback() {
            @Override
            public void onCallback(User user) {
                DocumentReference reference = mFirebase.collection("users").document(user.userId);
                bookMark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
            isExist = true;
          mFirebase.collection("projects/" + projectId + "/books").document(userId)
            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
        @Override
        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
            if (isExist) {
                if (!task.getResult().exists()) {
                    Map<String, Object> add = new HashMap<>();
                    add.put("date", FieldValue.serverTimestamp());
                    mFirebase.collection("projects/" + projectId + "/books")
                            .document(userId).set(add);
                    Map<String, Object> books = new HashMap<>();
                    ArrayList<String> bookList = (ArrayList<String>) user.bookMark2;
                    bookList.add(projectId);
                    books.put("bookMark2", bookList);
                    reference.update(books);
                    Toast.makeText(context,"Added project to bookmark list", Toast.LENGTH_SHORT).show();
                    isExist = false;
                } else {
                    mFirebase.collection("projects/" + projectId + "/books").document(userId)
                            .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Deleted bookmark item form list.");
                                Map<String, Object> remove = new HashMap<>();
                                ArrayList<String> removeBookItem = (ArrayList<String>) user.bookMark2;
                                removeBookItem.remove(projectId);
                                remove.put("bookMark2",removeBookItem);
                                reference.update(remove);
                            }
                            isExist = true;
                        }
                    });
                }
            }//end of exist condition
            else {

            }
                    }
                });
            }
        });
    }
});//end of userQuery

                mFirebase.collection("projects/" + projectId + "/books").document(userId)
                        .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                if (error == null) {
                                    if (value.exists()) {
                                        bookMark.setImageResource(R.drawable.ic_bookmark);
                                        bookMark.setTag("saved");
                                    } else {
                                        bookMark.setImageResource(R.drawable.ic_bookmark_gery);
                                        bookMark.setTag("removed");
                                    }
                                    bTag = (String) bookMark.getTag();
                                }
                            }
                        });

                likeContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        likeStatus = true;
                        mFirebase.collection("projects/" + projectId + "/likes").document(userId)
                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (likeStatus) {
                                    if (!task.getResult().exists()) {
                                        Map<String, Object> like = new HashMap<>();
                                        like.put("date", FieldValue.serverTimestamp());
                                        mFirebase.collection("projects/" + projectId+ "/likes").document(userId)
                                                .set(like);
                                        addNotification(userId,project.url,projectId);
                                        likeStatus = false;
                                    } else {
                                        mFirebase.collection("projects/" + projectId + "/likes").document(userId)
                                                .delete();
                                        likeStatus = true;
                                    }
                                }
                            }
                        });
                    }
                });

                mFirebase.collection("projects/" + projectId+ "/likes").document(userId)
                        .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                                if (error == null) {
                                    if (value.exists()) {
                                        likeContent.setImageResource(R.drawable.ic_fill_favorite);
                                        likeContent.setTag("clicked");
                                    } else {
                                        likeContent.setImageResource(R.drawable.ic_favourite);
                                        likeContent.setTag("unclicked");
                                    }
                                    mTag = (String) likeContent.getTag();

                                }
                            }
                        });

                DocumentReference topicRef = mFirebase.collection("projects").document(projectId);
                mFirebase.collection("projects/" + projectId + "/likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error == null) {
                            if (!value.isEmpty()) {
                                int count = value.size();
                                setProjectLike(count);
                                likeContent.setTag(count + "");
                            } else {
                                setProjectLike(0);
                                likeContent.setTag(0 + "");
                            }
                            cTag = (String) likeContent.getTag();

                            Map<String, Object> mapLike = new HashMap<>();
                            mapLike.put("numOfPeopleWhoLiked", value.size());
                            topicRef.update(mapLike).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                }
                            });

                        }
                    }
                });
                mFirebase.collection("projects/" + projectId + "/comments").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error == null) {
                            if (!value.isEmpty()) {
                                int count = value.size();
                                setProjectComments(count);
                                commentContent.setImageResource(R.drawable.ic_chat_comment_blue);
                                commentContent.setTag("comment");
                            } else {
                                setProjectComments(0);
                                commentContent.setImageResource(R.drawable.ic_chat_comment_gray);
                                commentContent.setTag("no comment");
                            }
                            tTag = (String) commentContent.getTag();

                            Map<String, Object> mapLike = new HashMap<>();
                            mapLike.put("commentsNum", value.size());
                            topicRef.update(mapLike).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                }
                            });
                        }
                    }
                });

                shareContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent myIntent = new Intent(Intent.ACTION_SEND);
                        myIntent.setType("text/plain");
                        String shareSub = context.getString(R.string.sub_title);
                        String shareBody = context.getString(R.string.title_share) + "\n";
                        myIntent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
                        myIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                        context.startActivity(Intent.createChooser(myIntent, context.getString(R.string.using)));

                    }
                });


                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onCallItem.onCallBackItem(project);
                    }
                });
            }
        }

        public void createOptionMenu(Project project, View v, final Context context) {
            //initialize
            PopupMenu deleteMenu = new PopupMenu(context, v);
            deleteMenu.getMenuInflater().inflate(id, deleteMenu.getMenu());
            //callback menuItemListener
            deleteMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    //getMenu id;
                    switch (menuItem.getItemId()) {
                        case R.id.delete_topic:
                            mFirebase.collection("projects").document(project.getProjectId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "Deleted project.");
                                        projectList.remove(project);
                                        notifyDataSetChanged();
                                    } else
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

    private void addNotification(String userId,String photoUrl,String projectId) {
        DocumentReference notificationRef=mFirebase.collection("notifications")
                .document(userId);
        Notification notification=new Notification(mAuth.getCurrentUser().getUid(),photoUrl,"liked your project..",
                "",projectId,false,true);

        notificationRef.set(notification);
    }

}
