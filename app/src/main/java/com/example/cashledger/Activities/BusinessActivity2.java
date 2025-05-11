package com.example.cashledger.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.cashledger.MainActivity;
import com.example.cashledger.R;
import com.example.cashledger.adapter.BusinessTypeAdapter;
import com.example.cashledger.adapter.CategoryAdapter;
import com.example.cashledger.databinding.ActivityBusiness2Binding;
import com.example.cashledger.modelClasses.Business;
import com.example.cashledger.modelClasses.Category;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;

public class BusinessActivity2 extends AppCompatActivity {
    private ActivityBusiness2Binding binding;
    private Business business;
    private ArrayList<Category> categories;
    private CategoryAdapter adapter;
    private static final String SHARED_PREFS="sharedPrefs";
    private static final String BUSINESS_ID="businessId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBusiness2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        business = (Business) getIntent().getSerializableExtra("Business");
        categories = new ArrayList<>();
        setRecView();
        moveToNext();
        skipNext();
    }

    //setting rec view
    private void setRecView() {

        //setting Categories
        categories.add(new Category("Education", R.drawable.education_icon));
        categories.add(new Category("Construction", R.drawable.construction_icon));
        categories.add(new Category("Agriculture", R.drawable.agriculture_icon));
        categories.add(new Category("Electronics", R.drawable.electronics_icon));
        categories.add(new Category("Financial Service", R.drawable.financial_service_icon));
        categories.add(new Category("Food/ Restaurant", R.drawable.food_icon));
        categories.add(new Category("Cloth/ Fashion", R.drawable.fashion_icon));
        categories.add(new Category("Hardware", R.drawable.hardware_icon));
        categories.add(new Category("Jewellery", R.drawable.jewellery_icon));
        categories.add(new Category("Transport", R.drawable.transport_icon));
        categories.add(new Category("Healthcare/ Fitness", R.drawable.healthcare_icon));
        categories.add(new Category("Kirana/ Grocery", R.drawable.kirana_icon));
        categories.add(new Category("Other", R.drawable.other_icon));

        adapter = new CategoryAdapter(BusinessActivity2.this, categories, business);
        binding.categoryRecView.setLayoutManager(new GridLayoutManager(this, 2));
        binding.categoryRecView.setAdapter(adapter);
    }

    //moving to next activity
    private void moveToNext() {
        binding.next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                business.setCategory(categories.get(adapter.getAdapterPosition()).getCategory());
                Intent intent = new Intent(BusinessActivity2.this, BusinessActivity3.class);
                intent.putExtra("Business", business);
                startActivity(intent);
            }
        });
    }


    private void skipNext() {
        binding.skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userId = FirebaseAuth.getInstance().getUid();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(userId)
                        .child("business");
                String businessId = reference.push().getKey();
                business.setId(businessId);
                reference = reference.child(businessId);
                reference.setValue(business);
                saveSharedPref();
                Intent intent = new Intent(BusinessActivity2.this, MainActivity.class);
                intent.putExtra("Business", business);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        adapter.notifyDataSetChanged();
    }

    public void saveSharedPref(){
        SharedPreferences sharedPreferences=getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();

        editor.putString(BUSINESS_ID,business.getId());
        editor.apply();
    }
}