package com.gitpro.gitidea.ui;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;

import com.gitpro.gitidea.utils.FireStoreQueries;
import com.gitpro.gitidea.R;
import com.gitpro.gitidea.models.Comment;
import com.gitpro.gitidea.models.Project;
import com.gitpro.gitidea.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.r0adkll.slidr.Slidr;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectActivity extends AppCompatActivity {

    AppCompatSpinner spinner;
    private ArrayAdapter<CharSequence>spinnerAdapter;
    TextInputEditText projectUrl,projectDesc;
    AppCompatButton shareProject;
    String items;
    ProgressBar progress,loading;
    Toolbar toolbar;
    TextView cancelProject,counter;
    DocumentReference newProjectRef;
    FirebaseFirestore mFirebaseFirestore;
    Project mProject;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_project);
        Slidr.attach(this);
        setToolBar();
        mFirebaseFirestore=FirebaseFirestore.getInstance();
        spinner=findViewById(R.id.spinner_languages);
        shareProject=findViewById(R.id.share_project);
        projectUrl=findViewById(R.id.project_url);
        projectDesc=findViewById(R.id.project_description);
        loading=findViewById(R.id.project_pg);
        progress=findViewById(R.id.pb_counter_pr);
        counter=findViewById(R.id.desc_counter);
        textChecker();

        spinnerAdapter=ArrayAdapter.createFromResource(this,R.array.languages, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                items=adapterView.getSelectedItem().toString();
                int parentId=adapterView.getId();
                if (parentId==R.id.spinner_languages){
                    if (!items.equalsIgnoreCase("Select project language")){

                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        shareProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProject();
            }
        });
    }

     public void setToolBar(){
       toolbar=findViewById(R.id.toolbar_project);
       cancelProject=findViewById(R.id.cancel_project);
       setSupportActionBar(toolbar);
       if (getSupportActionBar()!=null){
           getSupportActionBar().setTitle(null);
           toolbar.setNavigationContentDescription(R.string.cancel);
           cancelProject.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   onBackPressed();
               }
           });
       }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void textChecker(){
        projectDesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String input=String.valueOf(projectDesc.getText());
                int length=input.length();
                progress.setProgress(length);
                counter.setText(String.valueOf(charSequence.length()));
                counter.setTextColor(Color.BLACK);
                if (length<=0){
                    counter.setText("");
                }
                else if (length>=206&&length<260){
                    counter.setTextColor(Color.YELLOW);
                }
                else if (length>=260) {

                   counter.setTextColor(Color.RED);
                    counter.animate().alpha(1f)
                            .setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                            .setListener(null);


                    progress.animate().alpha(1f)
                            .setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                            .setListener(null);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void addProject(){

        loading.setVisibility(View.VISIBLE);
        List<Comment>list=new ArrayList<>();
        String urlPreview=String.valueOf(projectUrl.getText());

        FireStoreQueries.getUser(new FireStoreQueries.FirestoreUsersCallback() {
            @Override
            public void onCallback(User user) {
                 DocumentReference reference=mFirebaseFirestore.collection("users").document(user.userId);
                ArrayList<String>contentKeyword=(ArrayList<String>) user.contentUser;
                newProjectRef =mFirebaseFirestore.collection("projects").document();
                mProject=new Project(
                        newProjectRef.getId(),
                        user.userName,
                        items,
                        getDate(),
                        user.photoUrl,
                        0,
                       urlPreview,
                        projectDesc.getText().toString(),
                        list,
                        0);
                newProjectRef.set(mProject).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                      if (task.isSuccessful()){
                          loading.setVisibility(View.INVISIBLE);
                          Toast.makeText(ProjectActivity.this,"New Project just added",Toast.LENGTH_SHORT).show();

                          contentKeyword.add(newProjectRef.getId());
                          Map<String, Object> addContent = new HashMap<>();
                          addContent.put("contentUser", contentKeyword);
                          reference.update(addContent);
                          Log.d("Project", "onComplete: " + " Created a new Project");
                      }
                      else {
                          Log.d("Project", "onFailure: " + " Failed to create Project");
                      }
                    }
                });
                if (isValid(urlPreview)){
                    Handler handler=new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onBackPressed();
                        }
                    },3000);

                }
            }
        });
    }

    public String getDate(){
        Calendar calendar=Calendar.getInstance();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat=new SimpleDateFormat(" k:mm a  yyyy/MM/dd");
        return dateFormat.format(calendar.getTime());

    }

    public static boolean isValid(String url) {

        try {
            if (!url.contains("http")) {
                url = "http://"+url;
            }
            new URL(url).toURI();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
