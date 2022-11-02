package com.gitpro.gitidea.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.gitpro.gitidea.customs.CustomTextView;
import com.gitpro.gitidea.R;
import com.gitpro.gitidea.fragments.ProfileFragment;
import com.gitpro.gitidea.models.Notification;
import com.gitpro.gitidea.models.User;
import com.gitpro.gitidea.ui.ExploreActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> implements Filterable {

    private static FirebaseFirestore dbFire;
    List<User> uList;
    List<User> userFilters;
    Context context;
    FirebaseUser mUser;
    private final boolean isFragment;


    public UserAdapter(List<User> uList, Context context, boolean isFragment) {
        this.uList = uList;
        this.context = context;
        userFilters = uList;
        this.isFragment = isFragment;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        dbFire = FirebaseFirestore.getInstance();
        return new UserViewHolder(LayoutInflater.from(context).inflate(R.layout.user_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        final User user = uList.get(position);
        holder.addUser.setVisibility(View.VISIBLE);
        holder.name.setText(user.userName);
        holder.email.setText(user.email);
        Picasso.get().load(user.photoUrl)
                .noPlaceholder().into(holder.imageUrl);
        isFollowing(user.userId, holder.addUser);
        if (user.userId.equals(mUser.getUid())) {
            holder.addUser.setVisibility(View.GONE);
        }

        holder.addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String btnStatus = holder.addUser.getText().toString();
                if (btnStatus.equals("follow+")) {
                    dbFire.collection("users/" + mUser.getUid() + "/followings")
                            .document(user.userId)
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (!task.getResult().exists()) {
                                Map<String, Object> setValues = new HashMap<>();
                                setValues.put("date", FieldValue.serverTimestamp());
                                dbFire.collection("users/" + mUser.getUid() + "/followings")
                                        .document(user.userId)
                                        .set(setValues);
                            }
                        }
                    });

                    addNotification(user.userId, user.photoUrl);

                    dbFire.collection("users/" + user.userId + "/followers")
                            .document(mUser.getUid())
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (!task.getResult().exists()) {
                                Map<String, Object> setValues = new HashMap<>();
                                setValues.put("date", FieldValue.serverTimestamp());
                                dbFire.collection("users/" + user.userId + "/followers")
                                        .document(mUser.getUid())
                                        .set(setValues);
                            }
                        }
                    });

                } else {
                    dbFire.collection("users/" + mUser.getUid() + "/followings")
                            .document(user.userId)
                            .delete();
                    dbFire.collection("users/" + user.userId + "/followers")
                            .document(mUser.getUid())
                            .delete();
                }
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFragment) {
                    SharedPreferences.Editor editor = context.getSharedPreferences("PROFILE", Context.MODE_PRIVATE)
                            .edit();
                    editor.putString("profileId", user.userId);
                    editor.apply();
                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment())
                            .commitNow();
                } else {
                    Intent intent = new Intent(context, ExploreActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("publisherId", user.userId);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return uList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results = new FilterResults();
                if (charSequence.length() == 0 && charSequence.toString().isEmpty()) {
                    results.values = userFilters;
                    results.count = userFilters.size();
                } else {
                    String usrSearch = String.valueOf(charSequence).toLowerCase();
                    List<User> users = new ArrayList<>();
                    for (User user : userFilters) {
                        if (user.userName.contains(usrSearch.toLowerCase()) || user.email.contains(usrSearch.toLowerCase())) {
                            users.add(user);
                        }
                    }
                    results.values = users;
                    results.count = users.size();
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                uList = (List<User>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    private void isFollowing(final String userId, AppCompatButton compatButton) {
        DocumentReference userRef = dbFire.collection("users/" + mUser.getUid() + "/followings")
                .document(userId);
        userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error == null) {
                    if (value != null && value.exists()) {
                        compatButton.setText("following");
                    } else {
                        compatButton.setText("follow+");
                    }
                }
            }
        });
    }

    private void addNotification(String userId, String photoUrl) {
        DocumentReference notificationRef = dbFire.collection("notifications")
                .document(userId);
        Notification notification = new Notification(mUser.getUid(), photoUrl, "started following you..",
                "", "", false, false);

        notificationRef.set(notification);
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        AppCompatButton addUser;
        private final CircleImageView imageUrl;
        private final CustomTextView name;
        private final CustomTextView email;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            imageUrl = itemView.findViewById(R.id.photo_search);
            name = itemView.findViewById(R.id.user_search);
            email = itemView.findViewById(R.id.email_search);
            addUser = itemView.findViewById(R.id.follow_search);

        }
    }
}
