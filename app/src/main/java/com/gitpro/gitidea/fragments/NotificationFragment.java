package com.gitpro.gitidea.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gitpro.gitidea.R;
import com.gitpro.gitidea.adapters.NotificationAdapter;
import com.gitpro.gitidea.models.Notification;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class NotificationFragment extends Fragment {

    RecyclerView notification_rv;
    private FirebaseFirestore mFirebaseDB;
    private NotificationAdapter notificationAdapter;
    private FirebaseUser mUser;
    List<Notification>notificationList;

    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about_us, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mFirebaseDB=FirebaseFirestore.getInstance();
        mUser= FirebaseAuth.getInstance().getCurrentUser();
        notification_rv=view.findViewById(R.id.recycler_view);

        notification_rv = view.findViewById(R.id.recycler_view);
        notification_rv.setHasFixedSize(true);
        notification_rv.setLayoutManager(new LinearLayoutManager(getContext()));
        notificationList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(getContext(), notificationList);
        notification_rv.setAdapter(notificationAdapter);
        readNotifications();

        super.onViewCreated(view, savedInstanceState);
    }


    private void readNotifications() {

        mFirebaseDB.collection("notifications")
                .document(mUser.getUid())
            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

               if (task.isSuccessful()){
                   notificationList.clear();
                   Notification notification=task.getResult().toObject(Notification.class);
                   if (notification!=null){
                       notificationList.add(notification);
                   }
                   Collections.reverse(notificationList);
                   notificationAdapter.notifyDataSetChanged();
               }
            }
        });

    }



}