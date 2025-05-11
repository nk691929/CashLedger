package com.example.cashledger.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.example.cashledger.R;
import com.example.cashledger.databinding.ActivityBusiness1Binding;
import com.example.cashledger.modelClasses.Business;

public class BusinessActivity1 extends AppCompatActivity {
    private ActivityBusiness1Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityBusiness1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        binding.firstNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String businessName=binding.businessName.getText().toString();
                if(TextUtils.isEmpty(businessName)){
                    binding.businessName.setError("required");
                }else{
                    Business business=new Business();
                    business.setName(businessName);
                    Intent intent=new Intent(BusinessActivity1.this,BusinessActivity2.class);
                    intent.putExtra("Business",business);
                    startActivity(intent);
                }
            }
        });
    }
}