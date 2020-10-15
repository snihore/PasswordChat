package com.passwordchat;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class BiometricAuthentication {

    private  Context context;
    private Activity activity;
    private String tag;

    private static final String MAIN_ACTIVITY_TAG = "MainActivity";

    public BiometricAuthentication(Context context, Activity activity, String tag) {
        this.context = context;
        this.activity = activity;
        this.tag = tag;
    }

    public boolean canAuthenticate(){

        BiometricManager biometricManager = BiometricManager.from(context);

        switch (biometricManager.canAuthenticate()){

            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Toast.makeText(context, "Biometric hardware unavailable", Toast.LENGTH_SHORT).show();
                return false;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(context, "Biometric no hardware error", Toast.LENGTH_SHORT).show();
                return false;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Toast.makeText(context, "Biometric none enrolled error", Toast.LENGTH_SHORT).show();
                return false;
            case BiometricManager.BIOMETRIC_SUCCESS:
                return true;
        }

        return false;
    }

    public void authenticate(){

        Executor executor = null;
        BiometricPrompt biometricPrompt = null;
        BiometricPrompt.PromptInfo promptInfo = null;

        executor = ContextCompat.getMainExecutor(context);
        biometricPrompt = new BiometricPrompt((FragmentActivity) activity,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(context,
                        "Authentication error: " + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);

                try{

                    if(tag.equals(MAIN_ACTIVITY_TAG)){

                        MainActivity mainActivity = (MainActivity)activity;

                        mainActivity.handleBiometricAuth();
                    }else{
                        String title = tag;

                        ChatActivity chatActivity = (ChatActivity)activity;

                        chatActivity.handleBiometricAuth(title);
                    }

                }catch (Exception e){
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(context, "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login for my app")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Use account password")
                .build();

        // Prompt appears when user clicks "Log in".
        // Consider integrating with the keystore to unlock cryptographic operations,
        // if needed by your app.
        if(biometricPrompt != null && promptInfo != null){
            biometricPrompt.authenticate(promptInfo);
        }
    }
}
