package com.gitpro.gitidea.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.gitpro.gitidea.CustomTextView;
import com.gitpro.gitidea.FireStoreQueries;
import com.gitpro.gitidea.R;
import com.gitpro.gitidea.models.Project;
import com.gitpro.gitidea.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.overflowarchives.linkpreview.SkypePreview;
import com.overflowarchives.linkpreview.ViewListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectVH> {

    private static final String TAG="ProjectAdapter";
    Context context;
   public static Project project;
    List<Project> projectList;
    FirebaseFirestore db;
    View view;
    int id = 0;
    public boolean isFavorite=false;

    public ProjectAdapter(Context context,List<Project>projectList){
        this.context=context;
        this.projectList=projectList;
    }

    @NonNull
    @Override
    public ProjectVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      view=LayoutInflater.from(context).inflate(R.layout.custom_project,parent,false);
       ProjectVH projectVH=new ProjectVH(view);

        id = R.menu.admin_option;

        return projectVH;
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectVH holder, int position) {
          project=projectList.get(position);
          holder.bind(project);


        FireStoreQueries.getUser(new FireStoreQueries.FirestoreUsersCallback() {
            @Override
            public void onCallback(User user) {
                if (user.isAdmin){
                    holder.arrowDown.setVisibility(View.VISIBLE);
                    holder.arrowDown.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            createOptionMenu(project,view,context);
                        }
                    });
                }
                else {
                    holder.arrowDown.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }

    public  class ProjectVH extends RecyclerView.ViewHolder{

        public CircleImageView pPicture;
        public CustomTextView pTitle, pDesc,pDate;
        public TextView mLikes, mComments;
        AppCompatImageButton arrowDown;

        public AppCompatImageView shareContent, likeContent, commentContent;
        private SkypePreview imageContent;
        public ProjectVH(@NonNull View itemView) {
            super(itemView);

            pPicture=itemView.findViewById(R.id.profile_image_project);
            pTitle=itemView.findViewById(R.id.project_name2);
            pDesc=itemView.findViewById(R.id.project_desc2);
            pDate=itemView.findViewById(R.id.project_date);
            imageContent=itemView.findViewById(R.id.image_content2);
            mLikes=itemView.findViewById(R.id.like_num_project);
            mComments=itemView.findViewById(R.id.comment_num2);
            arrowDown=itemView.findViewById(R.id.arrow_down);

            shareContent=itemView.findViewById(R.id.share_iic);
            likeContent=itemView.findViewById(R.id.like_iic);
            commentContent=itemView.findViewById(R.id.comment_ic);

        }

        public String getDate(){
            Calendar calendar=Calendar.getInstance();
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat dateFormat=new SimpleDateFormat("dd,MM,yyyy HH:mm", Locale.getDefault());
            return dateFormat.format(calendar.getTime());
        }

        public void bind(Project project){
            if (project!=null){

           imageContent.loadUrl(project.urlPreview, new ViewListener() {
               @Override
               public void onPreviewSuccess(boolean b) {
                if (b){

                }
               }

               @Override
               public void onFailedToLoad(@Nullable Exception e) {
                   Log.d("OnUrlLoad:",e.getMessage());
               }
           });


                mLikes.setText(project.numOfPeopleWhoLiked+"");
                mComments.setText(project.commentsNum+"");
                pDate.setText(getDate());
                pDesc.setText(project.pDescription);
                pTitle.setText(project.mUser);
                Picasso.get().load(project.url).placeholder(android.R.color.transparent)
                .into(pPicture);


            }
        }
    }

    public void createOptionMenu(Project project, View v, final Context context){
        //initialize
        db=FirebaseFirestore.getInstance();
        PopupMenu deleteMenu=new PopupMenu(context,v);
        deleteMenu.getMenuInflater().inflate(id, deleteMenu.getMenu());
        //callback menuItemListener
        deleteMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                //getMenu id;
                switch (menuItem.getItemId()){
                    case R.id.delete_topic:
                        db.collection("projects").document(project.getProjectId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {
                                    Log.d(TAG, "Deleted project.");
                                    projectList.remove(project);
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
