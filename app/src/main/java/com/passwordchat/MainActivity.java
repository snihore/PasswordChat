package com.passwordchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Check
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent = null;

        if(user != null){
            Toast.makeText(this, "Welcome "+user.getEmail(), Toast.LENGTH_SHORT).show();
            //Chat Activity
            intent = new Intent(this, ChatActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        //Signup
        intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
        finish();

    }
}