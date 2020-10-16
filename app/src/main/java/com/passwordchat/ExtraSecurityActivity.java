package com.passwordchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ExtraSecurityActivity extends AppCompatActivity {

    private static final String TAG = "ExtraSecurityActivity";

    private ImageView backbtn;
    private Switch aSwitch;
    private LinearLayout linearLayout;
    private TextView error;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extra_security);

        backbtn = (ImageView)findViewById(R.id.extra_security_back_btn);
        aSwitch = (Switch)findViewById(R.id.extra_security_fingerprint_switch);
        linearLayout = (LinearLayout)findViewById(R.id.extra_security_fingerprint_layout);
        error = (TextView)findViewById(R.id.extra_security_fingerprint_error);

        final SharedPreferenceHandle sharedPreferenceHandle = new SharedPreferenceHandle(ExtraSecurityActivity.this);

        final BiometricAuthentication biometricAuthentication = new BiometricAuthentication(this, this, TAG);

        if(biometricAuthentication.canAuthenticate()){
            error.setVisibility(View.INVISIBLE);
            linearLayout.setVerticalGravity(View.VISIBLE);
            if(sharedPreferenceHandle.getFingerprint() != null){
                switch (sharedPreferenceHandle.getFingerprint()){

                    case SharePreferenceConf.KEY_ENABLE:
                        aSwitch.setChecked(true);
                        break;
                    case SharePreferenceConf.KEY_DISABLE:
                        aSwitch.setChecked(false);
                        break;
                    case SharePreferenceConf.NOT_AVAILABLE:
                        aSwitch.setChecked(false);
                        break;
                }
            }else{
                aSwitch.setChecked(false);
            }
        }else{
            error.setVisibility(View.INVISIBLE);
            linearLayout.setVerticalGravity(View.VISIBLE);
        }

        //Click events
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if(isChecked){
                    //Enable Fingerprint
                    biometricAuthentication.authenticate();
                }else{
                    //Disable Fingerprint

                    //Store State

                    if(sharedPreferenceHandle.setFingerprint(SharePreferenceConf.KEY_DISABLE)){
                        Toast.makeText(ExtraSecurityActivity.this, "Fingerprint Disabled", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(ExtraSecurityActivity.this, "Not Disabled", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }

    public void switchOFF(){

        aSwitch.setChecked(false);
    }
}