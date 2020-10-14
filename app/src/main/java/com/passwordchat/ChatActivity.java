package com.passwordchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    private FloatingActionButton addNewChat;
    private RecyclerView recyclerView;

    private static final String INBOX_TITLE = "INBOX_TITLE";
    private static final String INBOX_TYPE_KEY = "INBOX_TYPE";
    private static final String INBOX_TYPE_VALUE = "ADD_NEW_INBOX";
    private static final String INBOX_TYPE_VALUE_TWO = "EXISTING_INBOX";

    private static final String TAG = "ChatActivity";

    private List<String> inboxes = new ArrayList<>();

    private ChatInboxAdapter chatInboxAdapter;
    private ChatInboxClickListener chatInboxClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //Init Views
        addNewChat = (FloatingActionButton)findViewById(R.id.add_new_chat);
        recyclerView = (RecyclerView)findViewById(R.id.chat_inbox_recycler_view);

        //Progress
        progressDialog = new ProgressDialog(ChatActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Wait...");

        //Click Events
        addNewChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Inbox Activity
                Intent intent = new Intent(getApplicationContext(), InboxActivity.class);
                intent.putExtra(INBOX_TYPE_KEY, INBOX_TYPE_VALUE);
                startActivity(intent);
            }
        });
    }

    private void getInboxesFromFirestore(){
       CollectionReference collectionReference =  FirestoreInstance.getInboxMessageCollectionRef();

       if(collectionReference == null){
           Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
           return;
       }

       inboxes.clear();
       progress(true);

       collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
           @Override
           public void onComplete(@NonNull Task<QuerySnapshot> task) {

               progress(false);

               try{

                   if(task.isSuccessful()){

                       for(DocumentSnapshot documentSnapshot: task.getResult().getDocuments()){

                           if(documentSnapshot.exists()){
                               String title = (String) documentSnapshot.getData().get("title");

                               if(title != null) inboxes.add(title);
                           }
                       }// FOR LOOP --> End

                       //Init RecyclerView Adapter
                       chatInboxClickListener = new ChatInboxClickListener() {
                           @Override
                           public void onClick(View view, int position) {

                               //Inbox Activity
                               Intent intent = new Intent(getApplicationContext(), InboxActivity.class);
                               intent.putExtra(INBOX_TYPE_KEY, INBOX_TYPE_VALUE_TWO);
                               intent.putExtra(INBOX_TITLE, inboxes.get(position));
                               startActivity(intent);
                           }
                       };
                       chatInboxAdapter = new ChatInboxAdapter(getApplicationContext(), inboxes, chatInboxClickListener);
                       recyclerView.setAdapter(chatInboxAdapter);
                       recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                   }

                   if(!task.isSuccessful()){
                       Toast.makeText(ChatActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                   }

               }catch (Exception e){
                   e.printStackTrace();
                   Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
               }
           }
       });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getInboxesFromFirestore();
    }

    private void progress(boolean flag){

        if (progressDialog == null) return;

        if(flag)
            progressDialog.show();
        else
            progressDialog.dismiss();
    }
}