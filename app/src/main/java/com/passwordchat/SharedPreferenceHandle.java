package com.passwordchat;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SharedPreferenceHandle {

    private SharedPreferences sharedPreferences;
    private Activity activity;
    private SharedPreferences.Editor editor;

    public SharedPreferenceHandle(Activity activity) {
        this.activity = activity;
        this.sharedPreferences = this.activity.getSharedPreferences(SharePreferenceConf.NAME, Context.MODE_PRIVATE);
        this.editor = this.sharedPreferences.edit();
    }

    public boolean setFingerprint(String value){

        try{

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            String email = user.getEmail();

            editor.putString(email, value);

            editor.commit();

            return true;

        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public String getFingerprint(){

        try{

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            String email = user.getEmail();

            return sharedPreferences.getString(email, SharePreferenceConf.NOT_AVAILABLE);

        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

}
