package com.passwordchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InboxActivity extends AppCompatActivity{

    private static final String TAG = "InboxActivity";
    private static final String INBOX_TYPE_KEY = "INBOX_TYPE";
    private static final String INBOX_TITLE = "INBOX_TITLE";
    private String INBOX_TYPE_VALUE = "";

    private EditText messageArea;
    private TextView titleTextView;
    private ImageView saveBtn, sendBtn;
    private RecyclerView recyclerView;

    private Inbox inbox;
    private InboxMessageRecyclerViewAdapter adapter;
    private InboxMessageClickListener inboxMessageClickListener;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        INBOX_TYPE_VALUE = getIntent().getStringExtra(INBOX_TYPE_KEY);

        //Init Views
        messageArea = (EditText)findViewById(R.id.inbox_message_area);
        titleTextView = (TextView)findViewById(R.id.inbox_title);
        saveBtn = (ImageView)findViewById(R.id.inbox_save_btn);
        sendBtn = (ImageView)findViewById(R.id.inbox_send_btn);
        recyclerView = (RecyclerView)findViewById(R.id.inbox_recycler_view);

        //Progress
        progressDialog = new ProgressDialog(InboxActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Wait...");

        //Init Inbox

        if(INBOX_TYPE_VALUE.equals("ADD_NEW_INBOX")){
            inbox = new Inbox("No Title");
            titleTextView.setText("No Title");

            //Init RecyclerView Adapter
            initRecyclerView();
        }else if(INBOX_TYPE_VALUE.equals("EXISTING_INBOX")){
            String inboxTitle = getIntent().getStringExtra(INBOX_TITLE);
            getInboxFromFirestore(inboxTitle);

        }

        messageArea.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    addMessageToInbox();
                }
                return false;
            }
        });
        //Click Events
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try{
                    // Hide/Close keyboard
                    InputMethodManager imm = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                }catch (Exception e){
                    e.printStackTrace();
                }

                if(INBOX_TYPE_VALUE.equals("ADD_NEW_INBOX")){
                    showCustomDialog(null, null);
                }else if(INBOX_TYPE_VALUE.equals("EXISTING_INBOX")){
                    savePermanent(null, inbox.getTitle(), inbox.getUrl());

                }
            }
        });
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMessageToInbox();
            }
        });

    }

    private void initRecyclerView() {

        inboxMessageClickListener = new InboxMessageClickListener() {
            @Override
            public void onClick(View view, int position) {

                // InboxMessage Recycler View ClickListener
                try {

                    Message message = inbox.getMessages().get(position);

                    boolean isEncrypted = message.isEncrypted();

                    String key = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    if(key.length() >= 16){
                        key = key.substring(0, 16);
                    }else{
                        throw new Exception("No Key(UID) found");
                    }

                    if(isEncrypted)
                        message.decrypt(key);
                    else
                        message.encrypt(key);

                    adapter.notifyDataSetChanged();

                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onLongClick(View view, int position) {

                Message message = inbox.getMessages().get(position);

                openMessageDialog(message);
            }
        };
        adapter =
                new InboxMessageRecyclerViewAdapter(getApplicationContext(), inbox.getMessages(), inboxMessageClickListener);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void openMessageDialog(final Message message) {

        if(message == null){
            return;
        }

        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        final View dialogView = LayoutInflater.from(InboxActivity.this).inflate(R.layout.inbox_message_dialog, viewGroup, false);

        //Title and URL
        TextView copy = (TextView)dialogView.findViewById(R.id.inbox_message_dialog_copy);
        TextView delete = (TextView)dialogView.findViewById(R.id.inbox_message_dialog_delete);

        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);

        //finally creating the alert dialog and displaying it
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try{
                    //Copy Message
                    // Gets a handle to the clipboard service.
                    ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);

                    // Creates a new text clip to put on the clipboard
                    ClipData clip = ClipData.newPlainText("message_msg", message.getMsg());

                    // Set the clipboard's primary clip.
                    clipboard.setPrimaryClip(clip);

                    Toast.makeText(InboxActivity.this, message.getMsg(), Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(InboxActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                alertDialog.dismiss();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteMessage(message.getMsg());
                adapter.notifyDataSetChanged();
                alertDialog.dismiss();
            }
        });
    }

    private void getInboxFromFirestore(final String inboxTitle) {

        CollectionReference collectionReference = FirestoreInstance.getInboxMessageCollectionRef();

        if(collectionReference == null){
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            return;
        }

        collectionReference.document(inboxTitle).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                try{

                    if(task.isSuccessful() && task.getResult().exists()){

                        DocumentSnapshot documentSnapshot = task.getResult();

                        inbox = new Inbox((String) documentSnapshot.getData().get("title"));
                        inbox.setUrl((String) documentSnapshot.getData().get("url"));
                        inbox.setImage((Bitmap) documentSnapshot.getData().get("image"));
                        inbox.setInboxTimestamp((long) documentSnapshot.getData().get("inboxTimestamp"));

                        List<Map> list = (List<Map>) documentSnapshot.getData().get("messages");
                        List<Message> messages = new ArrayList<>();

                        for(Map map: list){

                            Message message = new Message((String) map.get("msg"), (long)map.get("timestamp"));
                            message.setEncrypted((boolean)map.get("encrypted"));

                            messages.add(message);
                        }

                        inbox.setMessages(messages);

                        titleTextView.setText(inbox.getTitle());

                        //Init RecyclerView Adapter
                        initRecyclerView();

                    }

                    if(!task.isSuccessful()){
                        Toast.makeText(InboxActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(InboxActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            }
        });
    }

    private void addMessageToInbox() {

        String msg = messageArea.getText().toString().trim();

        if(msg.matches("")){
            Toast.makeText(this, "Please type a username or password", Toast.LENGTH_SHORT).show();
            return;
        }

        Date date = new Date();
        long timestamp = date.getTime();

        Message message = new Message(msg, timestamp);

        inbox.addMessage(message);

        //Inbox Timestamp
        inbox.setInboxTimestamp(timestamp);

        sortMessages();

        //Notify Recycler View
        adapter.notifyDataSetChanged();

        //Reset Message Area
        messageArea.setText("");

    }

    private void sortMessages() {

        long[] arr01 = new long[inbox.getMessages().size()];
        Map<Long, String> map01 = new HashMap<>();
        Map<Long, Boolean> map02 = new HashMap<>();

        for(int i=0; i<inbox.getMessages().size(); i++){
            arr01[i] = inbox.getMessages().get(i).getTimestamp();
            map01.put(arr01[i], inbox.getMessages().get(i).getMsg());
            map02.put(arr01[i], inbox.getMessages().get(i).isEncrypted());
        }

        //SORT
        Arrays.sort(arr01);

        //CLEAR
        inbox.getMessages().clear();

        for(int i=0; i<arr01.length; i++){
            Message message = new Message(map01.get(arr01[i]), arr01[i]);
            message.setEncrypted(map02.get(arr01[i]));

            inbox.getMessages().add(message);
        }
    }

    private void showCustomDialog(String t, String u) {

        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(InboxActivity.this).inflate(R.layout.inbox_save_dialog, viewGroup, false);

        //Title and URL
        final EditText title = (EditText)dialogView.findViewById(R.id.save_title);
        final EditText url = (EditText)dialogView.findViewById(R.id.save_url);

        if(t != null && u != null){
            title.setText(t);
            url.setText(u);
        }

        AppCompatButton saveFinal = (AppCompatButton)dialogView.findViewById(R.id.save_final);

        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);

        //finally creating the alert dialog and displaying it
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        saveFinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(title.getText().toString().trim().matches("")){
                    Toast.makeText(InboxActivity.this, "Please enter title", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(url.getText().toString().trim().matches("")){
                    Toast.makeText(InboxActivity.this, "Please enter url", Toast.LENGTH_SHORT).show();
                    return;
                }

                savePermanent(alertDialog, title.getText().toString().trim(), url.getText().toString().trim());
            }
        });

    }

    private void savePermanent(AlertDialog alertDialog, String title, String url) {

        String t01 = title.toUpperCase().substring(0, 1);
        String t02 = title.toLowerCase().substring(1, title.length());

        title = t01 + t02;

        inbox.setTitle(title);
        inbox.setUrl(url);

        CollectionReference collectionReference = FirestoreInstance.getInboxMessageCollectionRef();

        if(collectionReference == null){
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference inboxMessageDocRef = collectionReference.document(title);

        if(alertDialog != null ) alertDialog.dismiss();

        progress(true);

        inboxMessageDocRef.set(inbox).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progress(false);
                Toast.makeText(InboxActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                onBackPressed();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(InboxActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                progress(false);
            }
        });

    }

    private void deleteMessage(String msg){

        if(msg == null){
            return;
        }

        List<Message> messages = inbox.getMessages();

        int index = -1;

        for(int i=0; i<messages.size(); i++){

            if(messages.get(i).getMsg().equals(msg)){
                index = i;
                break;
            }
        }

        if(index != -1 && index < messages.size()){
            inbox.getMessages().remove(index);
        }

    }


    private void progress(boolean flag){

        if (progressDialog == null) return;

        if(flag)
            progressDialog.show();
        else
            progressDialog.dismiss();
    }

}