package com.example.cashledger.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cashledger.MainActivity;
import com.example.cashledger.R;
import com.example.cashledger.databinding.ActivityLoginBinding;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Login_Activity extends AppCompatActivity {


    private TextView email,password,signUp,forgotPass;
    private Button loginBtn;
    private FirebaseAuth mAuth;
    private ActivityLoginBinding binding;

    public String userId;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).hide();
        try{
        initViews();
        logInBtnClick();
        doNotHaveAccount();
        forgotBtnClick();
        //sendOtpOnNum();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    //Initialize views
    void initViews()
    {
        password=findViewById(R.id.password_login);
        email=findViewById(R.id.email_login);
        signUp=findViewById(R.id.signUp_login);
        forgotPass=findViewById(R.id.forgot_pass);
        loginBtn=findViewById(R.id.login_button);
        mAuth = FirebaseAuth.getInstance();
        progressBar=findViewById(R.id.login_progress_bar);
        progressBar.setVisibility(View.GONE);
    }


    //LoginBtn ClickListener
    private void logInBtnClick()
    {
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fullEmail=email.getText().toString().trim();
                String fullUserPass=password.getText().toString().trim();

                if (TextUtils.isEmpty(fullEmail)) {
                    email.setError("Required");
                    return;
                }

                if (TextUtils.isEmpty(fullUserPass)) {
                    password.setError("Required");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);

                mAuth.signInWithEmailAndPassword(fullEmail, fullUserPass)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(Login_Activity.this, "Authentication Successful.",
                                            Toast.LENGTH_SHORT).show();
                                    userId=mAuth.getCurrentUser().getUid();
                                    email.setText("");
                                    password.setText("");
                                    Intent intent=new Intent(Login_Activity.this, SplashActivity.class);
                                    intent.putExtra("userId",userId);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(Login_Activity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });
    }

    //Don't have account ClickListener
    private void doNotHaveAccount()
    {
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Login_Activity.this,SignUpActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    //Forgot Btn ClickListener
    private void forgotBtnClick()
    {
        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Login_Activity.this,PasswordChangeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent=new Intent(Login_Activity.this,SplashActivity.class);
            startActivity(intent);
            finish();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==100){
            Task<GoogleSignInAccount> task=GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                task.getResult(ApiException.class);
                HomeActivity();
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private void HomeActivity() {
        Toast.makeText(this, "Login Successfully", Toast.LENGTH_SHORT).show();
        finish();
        Intent intent=new Intent(getApplicationContext(),SplashActivity.class);
        startActivity(intent);
    }
}