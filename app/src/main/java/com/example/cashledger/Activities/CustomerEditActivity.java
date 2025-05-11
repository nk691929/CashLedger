package com.example.cashledger.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.cashledger.R;
import com.example.cashledger.databinding.ActivityCustomerEditBinding;
import com.example.cashledger.modelClasses.Book;
import com.example.cashledger.modelClasses.Business;
import com.example.cashledger.modelClasses.Customer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CustomerEditActivity extends AppCompatActivity {
    private Customer customer;
    private Book book;
    private Business business;
    private RelativeLayout switchBusiness;
    private TextView businessNameTv, businessSwitchTv;
    private ImageView businessNameImg,businessNameImg2;
    private String type="";

    private ActivityCustomerEditBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityCustomerEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setBar();
        customer=(Customer) getIntent().getSerializableExtra("Customer");
        book=(Book) getIntent().getSerializableExtra("Book");
        business=(Business) getIntent().getSerializableExtra("Business");

        if(customer.getCustomerType().equals("Customer")){
            binding.customerRadioBtn.setChecked(true);
        }else if(customer.getCustomerType().equals("Supplier")){
            binding.supplierRadioBtn.setChecked(true);
        }
        binding.name.setText(customer.getCustomerName());
        binding.contact.setText(customer.getCustomerPhone());


        updateCustomer();

    }


    //setting bar
    private void setBar() {
        View customView = getLayoutInflater().inflate(R.layout.toolbar, null);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ActionBar bar = getSupportActionBar();
        assert bar != null;
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3F51B5")));
        switchBusiness = customView.findViewById(R.id.switch_Business_layout_toll);
        businessNameTv = customView.findViewById(R.id.business_name_bar);
        businessSwitchTv = customView.findViewById(R.id.toolbar_switch_tv);
        businessNameImg = customView.findViewById(R.id.toolbar_icon);
        businessNameImg2 = customView.findViewById(R.id.switch_Bussiness);
        businessNameImg.setImageResource(R.drawable.edit_flat_icon);
        businessSwitchTv.setVisibility(View.GONE);
        businessNameImg2.setVisibility(View.GONE);
        businessNameTv.setText("Edit");
        getSupportActionBar().setCustomView(customView);
    }


    //save btn Click
    private void updateCustomer(){
        binding.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name= String.valueOf(binding.name.getText());
                String phone= String.valueOf(binding.contact.getText());

                if(TextUtils.isEmpty(name)&&TextUtils.isEmpty(phone)){
                    binding.name.setError("Required");
                    binding.contact.setError("Required");
                }else{
                    if(binding.customerRadioBtn.isChecked()){
                        type="Customer";
                    }else{
                        type="Supplier";
                    }
                    customer.setCustomerName(name);
                    customer.setCustomerPhone(phone);
                    customer.setCustomerType(type);

                    String uId= FirebaseAuth.getInstance().getUid();
                    DatabaseReference reference= FirebaseDatabase.getInstance().getReference("users").child(uId)
                            .child(business.getId()).child(book.getBookID()).child("customers").child(customer.getCustomerId());
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            snapshot.getRef().setValue(customer);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    Intent intent=new Intent(CustomerEditActivity.this,CustomerActivity.class);
                    intent.putExtra("Business",business);
                    intent.putExtra("Book",book);
                    intent.putExtra("Customer",customer);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(this,CustomerActivity.class);
        intent.putExtra("Business",business);
        intent.putExtra("Book",book);
        intent.putExtra("Customer",customer);
        startActivity(intent);
        finish();
    }
}