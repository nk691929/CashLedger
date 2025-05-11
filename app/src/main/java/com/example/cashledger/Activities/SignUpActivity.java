package com.example.cashledger.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cashledger.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {
    private TextView password,email,rePassword,loginBtn;
    private Button signUp;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Objects.requireNonNull(getSupportActionBar()).hide();
        try {
            initViews();
            alreadyHaveAccount();
            signUpBtnClick();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //Initialize views
    private void initViews()
    {
        password=findViewById(R.id.password_signUp);
        rePassword=findViewById(R.id.re_password_signUp);
        email=findViewById(R.id.email_signUp);
        signUp=findViewById(R.id.signUp_button);
        loginBtn=findViewById(R.id.signUp_lg);
        mAuth = FirebaseAuth.getInstance();
        progressBar=findViewById(R.id.signUp_progress_bar);
        progressBar.setVisibility(View.GONE);
    }

    //SignUp ClickListener
    private void signUpBtnClick()
    {
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fullFormPass = String.valueOf(password.getText()).trim();
                String fullFormEmail = String.valueOf(email.getText()).trim();
                String fullFormRePass = String.valueOf(rePassword.getText()).trim();

                if (TextUtils.isEmpty(fullFormEmail)) {
                    email.setError("Required");
                    return;
                }

                if (TextUtils.isEmpty(fullFormPass)) {
                    password.setError("Required");
                    rePassword.setError("Required");
                    return;
                }

                if (!fullFormPass.equals(fullFormRePass)) {
                    Toast.makeText(SignUpActivity.this, "Password is not same", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);

                mAuth.createUserWithEmailAndPassword(fullFormEmail, fullFormPass)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    password.setText("");
                                    email.setText("");
                                    rePassword.setText("");
                                    Toast.makeText(SignUpActivity.this, "Authentication Successful.",
                                            Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                    Intent intent = new Intent(SignUpActivity.this, Login_Activity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }


    //Don't have account ClickListener
    private void alreadyHaveAccount()
    {
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, Login_Activity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}