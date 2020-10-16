package com.passwordchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Biometric Authentication
        try{

            SharedPreferenceHandle sharedPreferenceHandle = new SharedPreferenceHandle(this);

            if(sharedPreferenceHandle.getFingerprint() != null){
                switch (sharedPreferenceHandle.getFingerprint()){

                    case SharePreferenceConf.KEY_ENABLE:
                        BiometricAuthentication biometricAuthentication = new BiometricAuthentication(this, this, TAG);
                        if(biometricAuthentication.canAuthenticate()){
                            biometricAuthentication.authenticate();
                        }else{
                            handleBiometricAuth();
                        }
                        break;
                    case SharePreferenceConf.KEY_DISABLE:
                        handleBiometricAuth();
                        break;
                    case SharePreferenceConf.NOT_AVAILABLE:
                        handleBiometricAuth();
                        break;
                }
            }else{
                handleBiometricAuth();
            }
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    public void handleBiometricAuth(){

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