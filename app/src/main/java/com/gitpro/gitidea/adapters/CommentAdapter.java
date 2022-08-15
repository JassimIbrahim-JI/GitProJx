package com.gitpro.gitidea.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gitpro.gitidea.R;
import com.gitpro.gitidea.models.Comment;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentVH> {

    List<Comment> mComment;
    Context context;
        String topicPosition;


    public CommentAdapter(Context context,List<Comment>mComment,String topicPosition){

        this.context=context;
        this.mComment=mComment;
        this.topicPosition=topicPosition;
    }

    @NonNull
    @Override
    public CommentVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommentVH(LayoutInflater.from(context).
                inflate(R.layout.all_comments_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull CommentVH holder, int position) {
        Comment comment= mComment.get(position);

        holder.setCommentTime(comment.date);
        holder.setTextComment(comment.comment);
        holder.setUserComment(comment.user);
        holder.setProfileCommentPic(comment.photoProfile);


    }

    @Override
    public int getItemCount() {
        return mComment.size();
    }

    public static class CommentVH extends RecyclerView.ViewHolder{
        View mView;
        public CommentVH(View itemView){
            super(itemView);
            mView=itemView;
        }


        private String getCurrentDate() {
            long millis = System.currentTimeMillis();
            Date date = new Date(millis);
            @SuppressLint("SimpleDateFormat") DateFormat format = new SimpleDateFormat("dd MMMM");
            return format.format(date);
        }
        public void setUserComment(String userName){
            TextView userComment=mView.findViewById(R.id.comment_username);
            userComment.setText("@"+ userName+"");
        }
        public void setProfileCommentPic(String imageURL){
            CircleImageView profileCommentPic=mView.findViewById(R.id.comment_profile_pic);
            Picasso.get().load(imageURL).fit().into(profileCommentPic);
        }
        public void setTextComment(String textComment){
         TextView mComment=mView.findViewById(R.id.comment_text);
            mComment.setText(textComment);
        }
        public void setCommentTime(String time){
          TextView cTime=mView.findViewById(R.id.comment_time);
            cTime.setText("\u2022"+time);
        }


    }


}
