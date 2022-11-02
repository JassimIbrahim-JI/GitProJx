package com.gitpro.gitidea.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gitpro.gitidea.utils.FireStoreQueries;
import com.gitpro.gitidea.R;
import com.gitpro.gitidea.ui.EditProfileActivity;
import com.gitpro.gitidea.models.Gallery;
import com.gitpro.gitidea.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryVH> {

    List<Gallery>urls;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    public GalleryAdapter(List<Gallery>urls){
        this.urls=urls;
    }

    public void setUrls(List<Gallery>urls){
        this.urls=urls;
        notifyDataSetChanged();
    }


    public static class GalleryVH extends RecyclerView.ViewHolder{
      ShapeableImageView galleryImage;
      ProgressBar progressBar;
        public GalleryVH(@NonNull View itemView) {
            super(itemView);
            galleryImage=itemView.findViewById(R.id.gallery_image);
            progressBar=itemView.findViewById(R.id.gallery_pg);

        }
    }

    @NonNull
    @Override
    public GalleryAdapter.GalleryVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       db=FirebaseFirestore.getInstance();
       mAuth=FirebaseAuth.getInstance();

        return new GalleryVH(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.gallery_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryAdapter.GalleryVH holder, int position) {
       Gallery pos= urls.get(position);

        Glide.with(holder.itemView.getContext()).load(pos.getLargeImageURL()).placeholder(android.R.color.transparent)
        .into(holder.galleryImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FireStoreQueries.getUser(new FireStoreQueries.FirestoreUsersCallback() {
                    @Override
                    public void onCallback(User user) {
                        Map<String, Object> updatedData = new HashMap<>();
                        holder.progressBar.setVisibility(View.VISIBLE);
                        SharedPreferences.Editor editor=holder.itemView.getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE)
                                .edit();
                        editor.putString("imageHeader",pos.getLargeImageURL());
                        editor.apply();
                        updatedData.put("userHeader",pos.getLargeImageURL());
                        DocumentReference userRef=db.collection("users").document(user.userId);
                        userRef.update(updatedData).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(holder.itemView.getContext(),"successfully updated",Toast.LENGTH_SHORT)
                                        .show();
                                holder.progressBar.setVisibility(View.GONE);
                            }
                        });
                    }
                });
                Intent intent=new Intent(holder.itemView.getContext(), EditProfileActivity.class);
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return urls.size();
    }
}
