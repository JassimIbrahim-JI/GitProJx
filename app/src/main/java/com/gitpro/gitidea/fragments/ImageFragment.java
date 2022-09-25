package com.gitpro.gitidea.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.gitpro.gitidea.R;
import com.gitpro.gitidea.ui.EditProfileActivity;

public class ImageFragment extends Fragment {
    View v;
    RecyclerView recyclerImages;
    Toolbar toolbar;

    public ImageFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       v=inflater.inflate(R.layout.image_fragment,container,false);
       recyclerImages=v.findViewById(R.id.rvImages);
       toolbar=v.findViewById(R.id.thumbail_toolbar);

       if (getActivity()!=null) {
           ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
           if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
           toolbar.setNavigationOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   Intent intent=new Intent(getActivity(), EditProfileActivity.class);
                   getActivity().startActivity(intent);
               }
           });
           }
       }
        return v;
    }
}
