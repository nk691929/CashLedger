package com.example.cashledger.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.example.cashledger.MainActivity;
import com.example.cashledger.R;
import com.example.cashledger.adapter.BusinessTypeAdapter;
import com.example.cashledger.databinding.ActivityBusiness3Binding;
import com.example.cashledger.modelClasses.Business;
import com.example.cashledger.modelClasses.BusinessType;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;

public class BusinessActivity3 extends AppCompatActivity {

    private ActivityBusiness3Binding binding;
    private BusinessTypeAdapter adapter;
    private ArrayList<BusinessType> types;
    private Business business;
    private static final String SHARED_PREFS="sharedPrefs";
    private static final String BUSINESS_ID="businessId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityBusiness3Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        types=new ArrayList<>();
        business=(Business) getIntent().getSerializableExtra("Business");
        setTypeRecView();
        finishBusinessProfile();
    }

    //setting rec view
    void setTypeRecView()
    {
        types.add(new BusinessType("Retailer",R.drawable.retailer_icon));
        types.add(new BusinessType("Distributor",R.drawable.distributor_icon));
        types.add(new BusinessType("Manufacturer",R.drawable.manfecturer_icon));
        types.add(new BusinessType("Service Provider",R.drawable.service_icon));
        types.add(new BusinessType("Trader",R.drawable.trader_icon));
        types.add(new BusinessType("Other",R.drawable.other_icon));

        binding.typesRecView.setLayoutManager(new LinearLayoutManager(this));
        adapter=new BusinessTypeAdapter(BusinessActivity3.this,types,business);
        binding.typesRecView.setAdapter(adapter);
    }

    //finish
    private void finishBusinessProfile(){
        binding.finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                business.setType(types.get(adapter.getAdapterPosition()).getType());
                String userId= FirebaseAuth.getInstance().getUid();
                DatabaseReference reference= FirebaseDatabase.getInstance().getReference("users").child(userId)
                        .child("business");
                reference.keepSynced(true);
                String businessId=reference.push().getKey();
                business.setId(businessId);
                saveSharedPref();
                reference=reference.child(businessId);
                reference.setValue(business);
                Intent intent=new Intent(BusinessActivity3.this, MainActivity.class);
                intent.putExtra("Business",business);
                startActivity(intent);
            }
        });
    }

    public void saveSharedPref(){
        SharedPreferences sharedPreferences=getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();

        editor.putString(BUSINESS_ID,business.getId());
        editor.apply();
    }
}