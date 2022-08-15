package com.gitpro.gitidea.adapters;

import static com.gitpro.gitidea.models.repos.AndroidGitRepository.ALL_TOPICS_BASE_URL;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gitpro.gitidea.CustomTextView;
import com.gitpro.gitidea.R;
import com.gitpro.gitidea.activities.DetailsRepoActivity;
import com.gitpro.gitidea.activities.RepoActivity;
import com.gitpro.gitidea.models.Articles;
import com.gitpro.gitidea.models.Group;
import com.gitpro.gitidea.models.News;
import com.gitpro.gitidea.models.viewmodels.AndroidRepoViewModel;
import com.gitpro.gitidea.models.topics.Item;
import com.gitpro.gitidea.network.ApiClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GroupAdapter  extends RecyclerView.Adapter<GroupAdapter.GroupVH> implements Filterable {

    AppCompatActivity context;
    List<Group>groupList;
    List<Articles> featuredList;
    List<Articles>filterList;
    itemClickListener itemClickListener;
    PlantFeaturedAdapter plantFeaturedAdapter;
    AllTopicAdapter allTopicAdapter;
    ArrayList<Item>recommendedList;
    private AndroidRepoViewModel androidRepoViewModel;
    private final String api="109a9b6a1ad3488eaabb246a97f70d1e";


    public interface itemClickListener {
        void itemSelected(Articles groupModel);
    }



    public GroupAdapter(AppCompatActivity context, List<Group> groupList, List<Articles> featuredList, ArrayList<Item> recommendedList, itemClickListener itemClickListener) {
        this.context = context;
        this.groupList = groupList;
        this.featuredList = featuredList;
        this.recommendedList = recommendedList;
        this.itemClickListener=itemClickListener;
        filterList=featuredList;
    }

    @NonNull
    @Override
    public GroupVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.group_item,parent,false);
        return new GroupVH(v);

    }

    @Override
    public void onBindViewHolder(@NonNull GroupVH holder, int position) {
       Group modelGroup=groupList.get(position);
       if (modelGroup!=null){
           holder.groupTitle.setText(modelGroup.getGroupTitle());
           holder.groupBtn.setText(modelGroup.getGroupButtonTitle());

           if (holder.groupTitle.getText().equals("Discover")){
   holder.groupBtn.setOnClickListener(view -> context.startActivity(new Intent(context, RepoActivity.class),
           ActivityOptions.makeSceneTransitionAnimation(context).toBundle()));

           }

       }

        holder.setLists(position,holder.groupItems,getCountry(),api);
        holder.itemView.setOnClickListener(view -> itemClickListener.itemSelected(featuredList.get(holder.getAdapterPosition())));

    }
    public String getCountry(){
        Locale locale=Locale.getDefault();
        String country= locale.getCountry();
        return country.toLowerCase();
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

              FilterResults results=new FilterResults();
           if (charSequence.length()==0&&charSequence.toString().isEmpty()){
                   results.values=filterList;
                   results.count=filterList.size();
           }
           else {
             String searchText=charSequence.toString().toLowerCase();
             List<Articles>searchList=new ArrayList<>();
             for (Articles pLists:filterList){
                 if (pLists.getTitle().toLowerCase().contains(searchText)||
                         pLists.getDescription().toLowerCase().contains(searchText)){
                     searchList.add(pLists);
                 }

             }
             results.values=searchList;
             results.count=searchList.size();
           }
                return results;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
              featuredList= (List<Articles>) filterResults.values;
              notifyDataSetChanged();
            }
        };
    }

    public class GroupVH extends RecyclerView.ViewHolder {
        CustomTextView groupTitle;
        AppCompatButton groupBtn;
        RecyclerView groupItems;

        public GroupVH(@NonNull View itemView) {
            super(itemView);

            groupTitle=itemView.findViewById(R.id.group_title);
            groupBtn=itemView.findViewById(R.id.group_btn);
            groupItems=itemView.findViewById(R.id.group_rv);

        }


        public void setLists(int pos,RecyclerView recyclerView,String country,String api){
            switch (pos){
                case 0:setRecommendedFeaturedList(recyclerView);
                    break;
                case 1:setFeaturedList(recyclerView,country,api);
                break;
            }

        }

        private void setFeaturedList(RecyclerView recyclerView,String country,String api) {
            Call<News> newsCall= ApiClient.getInstance().getApi().getHeadlines(country,api);
            newsCall.enqueue(new Callback<News>() {
                @Override
                public void onResponse(Call<News> call, Response<News> response) {
                    if (response.isSuccessful() && response.body().getArticles() != null) {
                        featuredList.clear();
                        featuredList = response.body().getArticles();

                        plantFeaturedAdapter=new PlantFeaturedAdapter(context,featuredList);
                        recyclerView.setLayoutManager(new LinearLayoutManager(context,RecyclerView.HORIZONTAL,false));
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setNestedScrollingEnabled(true);
                        recyclerView.setAdapter(plantFeaturedAdapter);

                    }
                }
                @Override
                public void onFailure(Call<News> call, Throwable t) {
                    Log.e("onCallFailure",""+new Throwable(t.getLocalizedMessage()));
                }
            });



        }

        private void setRecommendedFeaturedList(RecyclerView recyclerView) {
            androidRepoViewModel=new ViewModelProvider(context).get(AndroidRepoViewModel.class);
            androidRepoViewModel.getData(context,ALL_TOPICS_BASE_URL , "android");
            androidRepoViewModel.getAndroidRepos().observe(context, new Observer<List<Item>>() {
                @Override
                public void onChanged(List<Item> items) {

                    if (items != null) {
                        recommendedList = new ArrayList<>(items);
                        recyclerView.setVisibility(View.VISIBLE);
                        allTopicAdapter = new AllTopicAdapter(recommendedList,context,R.layout.shimmer_placeholder_adapter_topics);
                        recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setNestedScrollingEnabled(true);
                        recyclerView.setAdapter(allTopicAdapter);
                        allTopicAdapter.notifyDataSetChanged();
                        allTopicAdapter.setOnItemClickListener(new AllTopicAdapter.onItemClickListener() {
                            @Override
                            public void respond(Item androidItem) {
                                Intent intent = new Intent(context , DetailsRepoActivity.class);
                                intent.putExtra("from","Home");
                                intent.putExtra("item", androidItem);
                                context.startActivity(intent);
                            }
                        });

                    }
                    else {
                        Toast.makeText(context.getApplicationContext(), context.getString(R.string.no_data_message), Toast.LENGTH_LONG).show();

                    }
                }
            });

            }
        }

}
