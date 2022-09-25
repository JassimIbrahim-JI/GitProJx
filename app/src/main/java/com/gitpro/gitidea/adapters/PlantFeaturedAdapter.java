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
import com.gitpro.gitidea.customs.CustomTextView;
import com.gitpro.gitidea.R;
import com.gitpro.gitidea.models.Articles;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
            vh.fDate.setText("\u2022"+vh.dateTime(model.getPublishedAt()));
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
  public String dateTime(String s){
      PrettyTime time=new PrettyTime(new Locale(country()));
      String t=null;
      try {
          SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:",Locale.ENGLISH);
          Date date=dateFormat.parse(s);
          t=time.format(date);
      } catch (ParseException e) {
          e.printStackTrace();
      }
      return t;
  }
  public String country(){
            Locale locale=Locale.getDefault();
            String c= locale.getCountry();
            return c.toLowerCase(Locale.ROOT);
  }

    }


}
