package com.passwordchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ChatActivity extends AppCompatActivity {

    private FloatingActionButton addNewChat;

    private static final String INBOX_TYPE_KEY = "INBOX_TYPE";
    private static final String INBOX_TYPE_VALUE = "ADD_NEW_INBOX";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //Init Views
        addNewChat = (FloatingActionButton)findViewById(R.id.add_new_chat);

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
}