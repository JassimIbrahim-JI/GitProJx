package com.gitpro.gitidea.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gitpro.gitidea.CustomTextView;
import com.gitpro.gitidea.R;
import com.gitpro.gitidea.models.Articles;

import java.util.ArrayList;
import java.util.List;

public class PlantFeaturedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    Context context;
    List<Articles>featuredList;
    public final static int LIST_FEATURED_LAYOUT=2;
    public int adapterType;

    public PlantFeaturedAdapter(Context context, List<Articles> featuredList) {
        this.context = context;
        this.featuredList = featuredList;

    }


    @SuppressLint("NotifyDataSetChanged")
    public void setData(ArrayList<Articles> pItems){
        featuredList = pItems;
        notifyDataSetChanged();
    }
    @SuppressLint("NotifyDataSetChanged")
    public void addData(Articles pItems, int position){
        featuredList.add(0, pItems);
        notifyDataSetChanged();
    }




    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        LayoutInflater inflater=LayoutInflater.from(context);
        RecyclerView.ViewHolder viewHolder;
       if (viewType==2){
           v=inflater.inflate(R.layout.featured_plant_item,parent,false);
           adapterType=LIST_FEATURED_LAYOUT;
           viewHolder=new FeaturedVH(v,adapterType);
           return viewHolder;
       }
       else
           v=inflater.inflate(R.layout.featured_plant_item,parent,false);
        adapterType=LIST_FEATURED_LAYOUT;
        viewHolder=new FeaturedVH(v,adapterType);
        return viewHolder;

        }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

      if (adapterType==LIST_FEATURED_LAYOUT){
              FeaturedVH vh1=(FeaturedVH) holder;
          configurationFeaturedVH(vh1,position);
       }

    }

    private void configurationFeaturedVH(FeaturedVH vh, int position){
        Articles model=featuredList.get(position);

            vh.fTitle.setText(model.getTitle());
            vh.fSource.setText(model.getSource().getName());
            vh.fDate.setText(model.getPublishedAt());
            Glide.with(vh.itemView.getContext()).load(model.getUrlToImage())
                    .into(vh.fImg);

            Animation animation= AnimationUtils.loadAnimation(context,R.anim.slide_left_to_right);
           vh.itemView.startAnimation(animation);

    }



    @Override
    public int getItemCount() {

        return featuredList.size();
    }

    public static class FeaturedVH extends RecyclerView.ViewHolder{

        public ImageView fImg;
        public CustomTextView fTitle,fDate,fSource;
        View v;

        public FeaturedVH(@NonNull View itemView,int adapterType) {
            super(itemView);

            fImg = itemView.findViewById(R.id.featured_img);
            fTitle = itemView.findViewById(R.id.featured_title);
            fDate = itemView.findViewById(R.id.dateSource);
            fSource=itemView.findViewById(R.id.source);
            v=itemView;

        }


    }


}
