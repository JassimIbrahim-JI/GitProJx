package com.gitpro.gitidea.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.gitpro.gitidea.R;
import com.gitpro.gitidea.models.User;
import com.gitpro.gitidea.ui.EditProfileActivity;
import com.gitpro.gitidea.ui.ExploreActivity;
import com.gitpro.gitidea.ui.UserFollowersActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment  extends Fragment {


    private static final String TAG = "ProfileFragment";
    ExploreActivity activity2;
    ViewPager2 profileVP;
    TabLayout profileTabs;
    CircleImageView profileImage;
    TextView userName, eMail, userBio;
    ImageView headerTabImage;
    AppCompatButton followUser;
    private FirebaseFirestore db;
    TextView followingNum, followersNum, following, followers;
    private FirebaseUser mUser;
    private String imageHeader;
    View view;
    public static String profileId;


    public ProfileFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity2 = (ExploreActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.profile_fragment, container, false);
        activity2 = (ExploreActivity) getActivity();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        String data = getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE).getString("profileId", "none");
        if (data.equals("none")) {
            profileId = mUser.getUid();
        } else {
            profileId = data;
            getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().clear().apply();
        }
        SharedPreferences prefs2 = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        imageHeader = prefs2.getString("imageHeader", "none");

        firebase_connection(view);
        return view;
    }


    public void firebase_connection(View v) {
        profileTabs = v.findViewById(R.id.profile_tabs);
        profileVP = v.findViewById(R.id.profile_vp);
        userName = v.findViewById(R.id.profile_username);
        eMail = v.findViewById(R.id.profile_email);
        profileImage = v.findViewById(R.id.profile_uri);
        userBio = v.findViewById(R.id.bio_tv);
        headerTabImage = v.findViewById(R.id.header_tab);
        followUser = v.findViewById(R.id.follow_user);
        following = v.findViewById(R.id.following);
        followers = v.findViewById(R.id.follower);
        followingNum = v.findViewById(R.id.following_num);
        followersNum = v.findViewById(R.id.followers_num);

        userInfo(profileId);
        checkFollowers(profileId);
        if (profileId.equals(mUser.getUid())) {
            followUser.setText("Edit Profile");
            Glide.with(getContext())
                    .load(imageHeader)
                    .placeholder(android.R.color.transparent)
                    .into(headerTabImage);
        } else {
            checkFollowing(profileId);
        }


        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity2, UserFollowersActivity.class);
                intent.putExtra("userId", profileId);
                intent.putExtra("tag", "followerPage");
                activity2.startActivity(intent);
            }
        });

        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity2, UserFollowersActivity.class);
                intent.putExtra("userId", profileId);
                intent.putExtra("tag", "followingPage");
                activity2.startActivity(intent);
            }
        });

        followUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String btnStatus = String.valueOf(followUser.getText());
                if (btnStatus.equals("Edit Profile")) {
                    Intent intent = new Intent(getContext(), EditProfileActivity.class);
                    if (getActivity() != null) {
                        getActivity().startActivity(intent);
                    }
                }
                else {
                    if (btnStatus.equals("follow+")) {
                        db.collection("users/" + mUser.getUid() + "/followings").document(profileId)
                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (!task.getResult().exists()) {
                                    Map<String, Object> setValues = new HashMap<>();
                                    setValues.put("date", FieldValue.serverTimestamp());
                                    db.collection("users/" + mUser.getUid() + "/followings").document(profileId)
                                            .set(setValues);
                                }
                            }
                        });

                        db.collection("users/" + profileId + "/followers")
                                .document(mUser.getUid())
                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (!task.getResult().exists()) {
                                    Map<String, Object> setValues = new HashMap<>();
                                    setValues.put("date", FieldValue.serverTimestamp());
                                    db.collection("users/" + profileId + "/followers").
                                            document(mUser.getUid())
                                            .set(setValues);
                                }
                            }
                        });
                    } else {
                        db.collection("users/" + mUser.getUid() + "/followings").document(profileId)
                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.getResult().exists()) {
                                    db.collection("users/" + mUser.getUid() + "/followings")
                                            .document(profileId)
                                            .delete();
                                    db.collection("users/" + profileId + "/followers")
                                            .document(mUser.getUid())
                                            .delete();
                                }
                            }
                        });
                    }
                }
            }
        });//END OF LISTENER


        ProfileViewPager viewPager = new ProfileViewPager(activity2.getSupportFragmentManager(), activity2.getLifecycle());
        viewPager.addFragment(new ActivitiesFragment());
        viewPager.addFragment(new RepliesFragment());
        viewPager.addFragment(new LikeFragment());

        profileVP.setAdapter(viewPager);

        profileTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                profileVP.setCurrentItem(tab.getPosition());
                if (tab.getPosition()==0){
                    SharedPreferences.Editor editor = activity2.getSharedPreferences("PROFILE", Context.MODE_PRIVATE)
                            .edit();
                    editor.putString("profileId", profileId);
                    editor.apply();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        profileVP.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                profileTabs.selectTab(profileTabs.getTabAt(position));

            }
        });
    }//End of Firebase_connection

    public void checkFollowing(String userId) {
        db.collection("users/" + mUser.getUid() + "/followings").document(userId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error == null) {
                            if (value != null && value.exists()) {
                                followUser.setText("following");
                                followUser.setTag("following");
                            } else {
                                followUser.setText("follow+");
                                followUser.setTag("follow+");
                            }

                        }
                    }
                });//END OF Following change

        db.collection("users/" + userId + "/followings")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error == null) {
                            if (value != null && !value.isEmpty()) {
                                int followingNumbers = value.size();
                                followingNum.setText(followingNumbers + "");
                            } else {
                                followingNum.setText(0 + "");
                            }
                        }
                    }
                });
    }//END OF FOLLOWING Method


    public void checkFollowers(String userId) {

        DocumentReference ref=db.collection("users").document(userId);
        ref.collection("followers");

        db.collection("users/" + userId + "/followers")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error == null) {
                            if (value != null && !value.isEmpty()) {
                                followersNum.setText(value.size() + "");
                            } else {
                                followersNum.setText(0 + "");
                            }
                        }
                    }
                });
    }

    public void userInfo(String profileId) {
        db.collection("users").document(profileId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                User user=task.getResult().toObject(User.class);
                   Picasso.get().load(user.photoUrl)
                           .noPlaceholder().into(profileImage);

                   eMail.setText(user.email);
                   userName.setText(user.userName);
                   userBio.setText(user.userBio);
            }
        });
    }

}