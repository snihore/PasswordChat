package com.passwordchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private ImageView settings, searchBtn, searchBackBtn;
    private TextView activityTitle;
    private EditText searchEditText;

    private FloatingActionButton addNewChat;
    private RecyclerView recyclerView;

    private static final String INBOX_TITLE = "INBOX_TITLE";
    private static final String INBOX_TYPE_KEY = "INBOX_TYPE";
    private static final String INBOX_TYPE_VALUE = "ADD_NEW_INBOX";
    private static final String INBOX_TYPE_VALUE_TWO = "EXISTING_INBOX";

    private static final String TAG = "ChatActivity";

   private Map<Long, String> inboxMap = new HashMap<>();


    private ChatInboxAdapter chatInboxAdapter;
    private ChatInboxClickListener chatInboxClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //Init Views
        addNewChat = (FloatingActionButton)findViewById(R.id.add_new_chat);
        recyclerView = (RecyclerView)findViewById(R.id.chat_inbox_recycler_view);
        settings = (ImageView)findViewById(R.id.chat_activity_settings);
        searchBtn = (ImageView)findViewById(R.id.chat_activity_search);
        searchBackBtn = (ImageView)findViewById(R.id.chat_inbox_search_back_btn);
        activityTitle = (TextView)findViewById(R.id.chat_activity_title);
        searchEditText = (EditText)findViewById(R.id.chat_inbox_search_edit_text);

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

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBottomsheet();
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                activityTitle.setVisibility(View.INVISIBLE);
                settings.setVisibility(View.INVISIBLE);
                searchBtn.setVisibility(View.INVISIBLE);
                searchBackBtn.setVisibility(View.VISIBLE);
                searchEditText.setVisibility(View.VISIBLE);

                try{
                    searchEditText.requestFocus();
                    searchEditText.setFocusableInTouchMode(true);

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(searchEditText, InputMethodManager.SHOW_FORCED);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

        searchBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityTitle.setVisibility(View.VISIBLE);
                settings.setVisibility(View.VISIBLE);
                searchBtn.setVisibility(View.VISIBLE);
                searchBackBtn.setVisibility(View.INVISIBLE);
                searchEditText.setVisibility(View.INVISIBLE);

                try{

                    //Search
                    if(chatInboxAdapter != null){
                        chatInboxAdapter.getFilter().filter("");
                    }

                    InputMethodManager imm = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                //Search
                if(chatInboxAdapter != null && charSequence != null && !charSequence.toString().matches("")){
                    chatInboxAdapter.getFilter().filter(charSequence);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void openBottomsheet() {

        try{
            //Create and Open Bottom Sheet
            ChatSettingsBottomsheetDialog bottomsheetDialog = new ChatSettingsBottomsheetDialog(getApplicationContext());

            bottomsheetDialog.show(getSupportFragmentManager(), "ModalBottomSheet");
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void getInboxesFromFirestore(){
       CollectionReference collectionReference =  FirestoreInstance.getInboxMessageCollectionRef();

       if(collectionReference == null){
           Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
           return;
       }

       inboxMap.clear();
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
                               long timestamp = (long)documentSnapshot.getData().get("inboxTimestamp");

                               if(title != null && timestamp != 0) inboxMap.put(timestamp, title);
                           }
                       }// FOR LOOP --> End

                       final List<String> inboxes = sortInboxes();

                       //Init RecyclerView Adapter
                       chatInboxClickListener = new ChatInboxClickListener() {
                           @Override
                           public void onClick(View view, int position) {

                               try{
                                   //Hide/Close Keyboard
                                   InputMethodManager imm = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
                                   imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                                   //Biometric Authentication

                                   SharedPreferenceHandle sharedPreferenceHandle = new SharedPreferenceHandle(ChatActivity.this);

                                   if(sharedPreferenceHandle.getFingerprint() != null){
                                       switch (sharedPreferenceHandle.getFingerprint()){

                                           case SharePreferenceConf.KEY_ENABLE:
                                               BiometricAuthentication biometricAuthentication = new BiometricAuthentication(getApplicationContext(), ChatActivity.this, inboxes.get(position));

                                               if(biometricAuthentication.canAuthenticate()){

                                                   biometricAuthentication.authenticate();
                                               }else{
                                                   handleBiometricAuth(inboxes.get(position));
                                               }
                                               break;
                                           case SharePreferenceConf.KEY_DISABLE:
                                               handleBiometricAuth(inboxes.get(position));
                                               break;
                                           case SharePreferenceConf.NOT_AVAILABLE:
                                               handleBiometricAuth(inboxes.get(position));
                                               break;
                                       }
                                   }else{
                                       handleBiometricAuth(inboxes.get(position));
                                   }
                               }catch (Exception e){
                                   Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                               }

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

    private List<String> sortInboxes() {

        List<Long> list = new ArrayList<>(inboxMap.keySet());

        long[] arr = new long[list.size()];

        for(int i=0; i<arr.length; i++){

            arr[i] = list.get(i);
        }

        Arrays.sort(arr);

        List<String> inboxes = new ArrayList<>();

        for(int i=arr.length-1; i>=0; i--){

            inboxes.add(inboxMap.get(arr[i]));
        }

        return inboxes;
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

    public void handleBiometricAuth(String title){
        //Inbox Activity
        Intent intent = new Intent(getApplicationContext(), InboxActivity.class);
        intent.putExtra(INBOX_TYPE_KEY, INBOX_TYPE_VALUE_TWO);
        intent.putExtra(INBOX_TITLE, title);
        startActivity(intent);
    }

}