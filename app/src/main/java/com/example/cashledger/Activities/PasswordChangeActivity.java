package com.example.cashledger.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.cashledger.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import java.util.Objects;

public class PasswordChangeActivity extends AppCompatActivity {
    private EditText email;
    private TextView backToLogin;
    private Button forgotBtn;
    private ProgressBar progressBar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change);
        getSupportActionBar().hide();
        try {
            initViews();
            setForgotBtnListener();
            setBackToLogin();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Initialize Views
    private void initViews()
    {
        email=findViewById(R.id.email_forgot);
        backToLogin=findViewById(R.id.back_reset_pass);
        forgotBtn=findViewById(R.id.forgot_button);
        progressBar=findViewById(R.id.forgot_progress_bar);
        progressBar.setVisibility(View.GONE);
        auth=FirebaseAuth.getInstance();
    }

    //Forgot Button Click Listener
    private void setForgotBtnListener()
    {
        forgotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailTxt=String.valueOf(email.getText()).trim();
                if(TextUtils.isEmpty(emailTxt)){
                    email.setError("Required");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);

                auth.sendPasswordResetEmail(emailTxt).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(PasswordChangeActivity.this, "Check Your Mail", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            startActivity(new Intent(PasswordChangeActivity.this, Login_Activity.class));
                            finish();
                        }else {
                            Toast.makeText(PasswordChangeActivity.this,
                                    Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    //back to Login
    private void setBackToLogin()
    {
        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PasswordChangeActivity.this,Login_Activity.class));
                finish();
            }
        });
    }
}