package com.example.cashledger.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.cashledger.databinding.ActivityGetOtpBinding;

public class GetOtpActivity extends AppCompatActivity {
    private ActivityGetOtpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityGetOtpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}