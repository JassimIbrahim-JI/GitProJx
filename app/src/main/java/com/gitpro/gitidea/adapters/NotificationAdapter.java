package com.gitpro.gitidea.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gitpro.gitidea.R;
import com.gitpro.gitidea.fragments.ProfileFragment;
import com.gitpro.gitidea.fragments.TopicFragment;
import com.gitpro.gitidea.models.Notification;
import com.gitpro.gitidea.models.Project;
import com.gitpro.gitidea.models.Topic;
import com.gitpro.gitidea.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Notification> mNotifications;
    private FirebaseFirestore db;
    private static final int USER_NOTIFY= 1;
    private static final int APP_NOTIFY = 2;


    public NotificationAdapter(Context mContext, List<Notification> mNotifications) {
        this.mContext = mContext;
        this.mNotifications = mNotifications;
    }
    @Override
    public int getItemViewType(int position) {
        // here your custom logic to choose the view type
        return position == 0 ? USER_NOTIFY : APP_NOTIFY;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolder;
        db=FirebaseFirestore.getInstance();
      if (viewType==USER_NOTIFY) {
          view = LayoutInflater.from(mContext).inflate(R.layout.notification_item, parent, false);
          viewHolder = new UserNotifyVH(view);
      }
      else {
          view = LayoutInflater.from(mContext).inflate(R.layout.notification_item2,parent,false);
          viewHolder=new AppNotifyVH(view);
      }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final Notification notification = mNotifications.get(position);
        if (holder.getItemViewType() == USER_NOTIFY) {
            UserNotifyVH mHolder = (UserNotifyVH) holder;

            getUser(mHolder.imageView, mHolder.userName, notification.userId);
            mHolder.comment.setText(notification.nText);
            if (notification.isItTopic) {
                mHolder.topicImage.setVisibility(View.VISIBLE);
                getTopicImage(mHolder.topicImage, notification.topicId);
            } else {
                mHolder.topicImage.setVisibility(View.GONE);
            }

            mHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (notification.isItTopic) {
                        mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
                                .edit().putString("topicId", notification.topicId).apply();

                        ((FragmentActivity) mContext).getSupportFragmentManager()
                                .beginTransaction().replace(R.id.fragment_container, new TopicFragment()).commit();
                    } else {
                        mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE)
                                .edit().putString("profileId", notification.userId).apply();

                        ((FragmentActivity) mContext).getSupportFragmentManager()
                                .beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                    }
                }
            });
        }
        else{
            AppNotifyVH mHolder=(AppNotifyVH) holder;
            boolean key1=((Activity)mContext).getIntent().hasExtra("key1");
            if (key1){
                for (String key:((Activity)mContext).getIntent().getExtras().keySet()){

                }
            }

        }

    }

    @Override
    public int getItemCount() {
        return mNotifications.size();
    }


    public static class UserNotifyVH extends RecyclerView.ViewHolder{
        TextView userName,comment;
        ImageView topicImage;
        CircleImageView imageView;
        public UserNotifyVH(@NonNull View itemView) {
            super(itemView);
            userName=itemView.findViewById(R.id.username);
            comment=itemView.findViewById(R.id.comment);
            topicImage =itemView.findViewById(R.id.topic_image);
            imageView=itemView.findViewById(R.id.image_profile);
        }
    }
    public static class AppNotifyVH extends RecyclerView.ViewHolder{
        TextView userName,comment;
        ImageView notificationBG;
        CircleImageView imageView;

        public AppNotifyVH(@NonNull View itemView) {
            super(itemView);
            userName=itemView.findViewById(R.id.username1);
            comment=itemView.findViewById(R.id.comment2);
            notificationBG=itemView.findViewById(R.id.notification_bg);
            imageView=itemView.findViewById(R.id.image_app);

        }
    }

    private void getTopicImage(final ImageView imageView, String topicId) {

        db.collection("topics").document(topicId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    Topic topic=task.getResult().toObject(Topic.class);
                    if (topic!=null) {
                        Glide.with(mContext)
                                .load(topic.pImage)
                                .placeholder(R.mipmap.ic_launcher)
                                .into(imageView);
                    }
                }
            }
        });
    }

    private void getProjectImage(final ImageView imageView, String projectId){
        db.collection("projects").document(projectId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
              if (task.isSuccessful()){
                  Project project=task.getResult().toObject(Project.class);
                  if (project!=null){

                  }
              }
            }
        });
    }

    private void getUser(final ImageView imageView, final TextView textView, String userId) {
        db.collection("users").document(userId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
              if (task.isSuccessful()){
                  User user=task.getResult().toObject(User.class);
                  if (user!=null) {
                     Picasso.get().load(user.photoUrl).placeholder(android.R.color.transparent).
                             into(imageView);

                      textView.setText(user.userName);
                  }
              }
            }
        });

    }


}
