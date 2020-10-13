package com.passwordchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private EditText email, password;
    private AppCompatButton signup, login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Progress
        progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Wait...");

        //Init Views
        email = (EditText)findViewById(R.id.input_email);
        password = (EditText)findViewById(R.id.input_password);
        signup = (AppCompatButton)findViewById(R.id.btn_signup);
        login =  (AppCompatButton)findViewById(R.id.btn_login);

        //Click events
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String e = email.getText().toString().trim();
                if(e.matches("")){
                    Toast.makeText(SignupActivity.this, "Please enter email", Toast.LENGTH_SHORT).show();
                    return;
                }

                final String p = password.getText().toString().trim();
                if (p.matches("")){
                    Toast.makeText(SignupActivity.this, "Please enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                //SIGNUP
                progress(true);
                firebaseAuth.createUserWithEmailAndPassword(e, p).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        progress(false);
                        if(e.equals(authResult.getUser().getEmail())){
                            Toast.makeText(SignupActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(SignupActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            authResult.getUser().delete();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progress(false);
                        Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String e = email.getText().toString().trim();
                if(e.matches("")){
                    Toast.makeText(SignupActivity.this, "Please enter email", Toast.LENGTH_SHORT).show();
                    return;
                }

                final String p = password.getText().toString().trim();
                if (p.matches("")){
                    Toast.makeText(SignupActivity.this, "Please enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                //LOGIN
                progress(true);
                firebaseAuth.signInWithEmailAndPassword(e, p).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        progress(false);
                        if(e.equals(authResult.getUser().getEmail())){
                            Toast.makeText(SignupActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(SignupActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progress(false);
                        Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });



    }

    private void progress(boolean flag){

        if (progressDialog == null) return;

        if(flag)
            progressDialog.show();
        else
            progressDialog.dismiss();
    }
}