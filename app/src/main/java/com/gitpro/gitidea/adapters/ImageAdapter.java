package com.gitpro.gitidea.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gitpro.gitidea.R;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageVh> {

    private List<String> imageList;
    private Context context;


    public ImageAdapter(List<String> imageList, Context context) {
        this.imageList = imageList;
        this.context = context;
    }

    @NonNull
    @Override
    public ImageVh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ImageVh(LayoutInflater.from(context).inflate(R.layout.image_items,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ImageVh holder, int position) {
       String url=imageList.get(position);
        Glide.with(holder.itemView).load(url).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }


    public class ImageVh extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageVh(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.ivImage);
        }
    }
}
