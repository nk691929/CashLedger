package com.example.cashledger.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.cashledger.MainActivity;
import com.example.cashledger.R;
import com.example.cashledger.databinding.ActivitySplashBinding;
import com.example.cashledger.modelClasses.Business;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {
    private ActivitySplashBinding binding;

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String BUSINESS_ID = "businessId";
    private String businessID;
    private Business business;
    private ArrayList<Business> businessList;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_splash);
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if(user==null){
            Intent intent=new Intent(SplashActivity.this,Login_Activity.class);
            startActivity(intent);
            finish();
            return;
        }
        String uId=FirebaseAuth.getInstance().getUid();
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("users").child(uId);
        StorageReference storageReference= FirebaseStorage.getInstance().getReference("users").child(uId);
        databaseReference.keepSynced(true);
        try {
            binding.progressBar.setVisibility(View.VISIBLE);
            checkUserLoggedInOrNot();
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    //fetch Business
    private void fetchBusinessList() {
        String userId = FirebaseAuth.getInstance().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(userId)
                .child("business");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Business bs = snap.getValue(Business.class);
                    businessList.add(bs);
                }
                if(businessList.size()==0){
                    createNewBusiness();
                }else {
                    getSharedPref();
                    updateActivityWithSharedPref();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getSharedPref() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        businessID = sharedPreferences.getString(BUSINESS_ID, "");
    }

    public void updateActivityWithSharedPref() {
        boolean exist = false;
        for (Business b : businessList) {
            if (b.getId().equals(businessID)) {
                business = b;
                exist = true;
                break;
            }
        }
        if (!exist) {
            business = new Business();
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            intent.putExtra("Business", business);
            intent.putExtra("BusinessList", businessList);
            startActivity(intent);
            finish();
        }else{
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            intent.putExtra("Business", business);
            startActivity(intent);
            finish();
        }
    }

    void checkUserLoggedInOrNot() {
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            Thread thread=new Thread(){
                @Override
                public void run() {
                    try{
                        sleep(1*1000);
                        startActivity(new Intent(getApplicationContext(), Login_Activity.class));
                        finish();
                    }catch (Exception e){

                    }
                }
            };
            thread.start();
        } else {
            businessList=new ArrayList<>();
            fetchBusinessList();
        }

    }

    //Moving new user to Business Activity
    private void createNewBusiness(){
        Intent intent=new Intent(SplashActivity.this,BusinessActivity1.class);
        startActivity(intent);
    }
}