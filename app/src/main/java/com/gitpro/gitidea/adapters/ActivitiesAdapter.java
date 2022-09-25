package com.gitpro.gitidea.adapters;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gitpro.gitidea.customs.CustomTextView;
import com.gitpro.gitidea.R;
import com.gitpro.gitidea.models.Group;
import com.gitpro.gitidea.models.Project;
import com.gitpro.gitidea.models.Topic;
import com.gitpro.gitidea.ui.DetailsProjectActivity;

import java.util.List;

public class ActivitiesAdapter extends RecyclerView.Adapter<ActivitiesAdapter.ActivityVH>  {

   List<Group>acList;
   Activity context;
   List<Topic>topics;
   List<Project>projects;
   TopicAdapter topicAdapter;
   ProjectAdapter projectAdapter;

   public ActivitiesAdapter(List<Group>acList, List<Topic>topics, List<Project>projects, Activity context){
       this.acList=acList;
       this.context=context;
       this.projects=projects;
       this.topics=topics;
   }
    @NonNull
    @Override
    public ActivityVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ActivityVH(LayoutInflater.from(context).
                inflate(R.layout.activities_rows,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityVH holder, int position) {
       holder.activityTitle.setText(acList.get(position).getGroupTitle());
       holder.setLists(position, holder.activityItem);
    }

    @Override
    public int getItemCount() {
        return acList.size();
    }

    public class ActivityVH extends RecyclerView.ViewHolder implements TopicAdapter.mClickListener, TopicAdapter.ItemClickListener,ProjectAdapter.ItemClickProjectListener{
        RecyclerView activityItem;
        CustomTextView activityTitle;
       public ActivityVH(@NonNull View itemView) {
            super(itemView);
            activityItem=itemView.findViewById(R.id.activity_rv);
            activityTitle=itemView.findViewById(R.id.activity_title);

        }

        public void setLists(int pos,RecyclerView recyclerView){
            switch (pos){
                case 0:
                    setTopicList(recyclerView);
                    break;
                case 1:
                    setProjectList(recyclerView);
                    break;
            }
        }

        private void setTopicList(RecyclerView recyclerView) {

   topicAdapter=new TopicAdapter(context,topics,ActivityVH.this::onCallBackItem,ActivityVH.this::longClick);
   recyclerView.setLayoutManager(new LinearLayoutManager(context,RecyclerView.VERTICAL,false));
   recyclerView.setHasFixedSize(true);
   recyclerView.setAdapter(topicAdapter);

        }

        private void setProjectList(RecyclerView recyclerView) {

       recyclerView.setVisibility(View.VISIBLE);
       projectAdapter = new ProjectAdapter(context,projects, ActivityVH.this::onCallBackItem);
       recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
       recyclerView.setHasFixedSize(true);
       recyclerView.setAdapter(projectAdapter);

        }

        @Override
        public void onCallBackItem(Topic topic) {

        }

        @Override
        public boolean longClick(boolean click) {
            return false;
        }

        @Override
        public void onCallBackItem(Project project) {
            Intent intent=new Intent(context, DetailsProjectActivity.class);
            Bundle bundle=new Bundle();
//            bundle.putString("usernameP",project.mUser);
//            bundle.putString("descP",project.pDescription);
//            bundle.putString("urlPreview",project.urlPreview);
//            bundle.putString("urlP",project.url);
//            bundle.putString("projectId",project.projectId);
//            bundle.putString("userIdP", ProjectAdapter.userId);
//            bundle.putString("dateP",project.pDate);
//            bundle.putInt("commentNumP", project.commentsNum);
//            bundle.putString("ivCommentP",ProjectAdapter.tTag);
//            bundle.putString("ivlikeP", ProjectAdapter.mTag);
//            bundle.putString("likeNumP", ProjectAdapter.cTag);
//            bundle.putSerializable("commentsP", (Serializable) project.comments);
//            intent.putExtras(bundle);
            context.startActivity(intent);
        }
    }
}
