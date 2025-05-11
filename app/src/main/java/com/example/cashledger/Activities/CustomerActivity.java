package com.example.cashledger.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cashledger.R;
import com.example.cashledger.adapter.CustomerRecAdapter;
import com.example.cashledger.databinding.ActivityCustomerBinding;
import com.example.cashledger.modelClasses.Book;
import com.example.cashledger.modelClasses.Business;
import com.example.cashledger.modelClasses.Customer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Document;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class CustomerActivity extends AppCompatActivity {

    private ActivityCustomerBinding binding;
    private ArrayList<Customer> customerList;
    private DatabaseReference reference;
    private CustomerRecAdapter adapter;
    private RelativeLayout switchBusiness;
    private TextView businessNameTv, businessSwitchTv;
    private ImageView businessNameImg,businessNameImg2;
    private Book book;
    private Business business;
    private String dirpath;
    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBar();
        binding = ActivityCustomerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        try {
            business = (Business) getIntent().getSerializableExtra("Business");
            String code = "other";
            code = getIntent().getStringExtra("Code");
            getIntentExtras();
            init();
            fetchCustomers();
            settingRecView();
            searchCustomers();
            addCustomersLayout();
        } catch (Exception ignored) {
        }
    }

    //getIntent
    private void getIntentExtras() {
        Intent intent = getIntent();
        book = (Book) intent.getSerializableExtra("Book");
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
        businessNameImg.setImageResource(R.drawable.customer_flat_icon);
        businessNameImg2.setVisibility(View.GONE);
        businessSwitchTv.setVisibility(View.GONE);
        businessNameTv.setText("Customers");
        getSupportActionBar().setCustomView(customView);
    }

    //init
    private void init() {
        customerList = new ArrayList<>();
        binding.cusRecView.setLayoutManager(new LinearLayoutManager(this));
    }

    //Fetching data from firebase
    private void fetchCustomers() {
        String userId = FirebaseAuth.getInstance().getUid();
        reference = FirebaseDatabase.getInstance().getReference("users").child(userId)
                .child(business.getId()).
                child(book.getBookID()).child("customers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (customerList.size() == 0) {
                    for (DataSnapshot ss : snapshot.getChildren()) {
                        Customer customer = ss.getValue(Customer.class);
                        customerList.add(customer);
                    }
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CustomerActivity.this, "Error : " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //setting RecyclerView
    private void settingRecView() {
        adapter = new CustomerRecAdapter(CustomerActivity.this, customerList, business, book);
        binding.cusRecView.setAdapter(adapter);
    }

    //adding Customers
    private void addCustomersLayout() {
        binding.addCus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomerActivity.this, ContactsActivity.class);
                intent.putExtra("Business", business);
                intent.putExtra("Business", business);
                intent.putExtra("Book", book);
                intent.putExtra("customerList", customerList);
                startActivity(intent);
                finish();
            }
        });
    }

    //Adding customer in firebase
    private void onClickOnAddButton(Button buttonAdd, Dialog dialogBuild, EditText customerName, EditText customerPhone) {
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = customerName.getText().toString();
                String phone = customerPhone.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    customerName.setError("Required");
                } else if (TextUtils.isEmpty(phone)) {
                    customerPhone.setError("Required");
                } else {
                    Customer customer = new Customer();
                    customer.setCustomerName(name);
                    customer.setCustomerPhone(phone);
                    String userId = FirebaseAuth.getInstance().getUid();
                    reference = FirebaseDatabase.getInstance().getReference("users").child(userId)
                            .child(business.getId()).
                            child(book.getBookID()).child("customers");
                    String customerID = reference.push().getKey();
                    customer.setCustomerId(customerID);
                    customer.setBookId(book.getBookID());
                    customerList.add(customer);
                    reference = reference.child(customerID);
                    reference.setValue(customer);
                    adapter.notifyDataSetChanged();
                    dialogBuild.dismiss();
                }
            }
        });
    }


    //Search Customer
    private void searchCustomers() {
        binding.searchCustomer.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });
    }

    //Perform Filtering
    private void filter(String text) {
        ArrayList<Customer> filteredlist = new ArrayList<Customer>();

        for (Customer item : customerList) {
            if (item.getCustomerName().toLowerCase().contains(text.toLowerCase())) {
                filteredlist.add(item);
            }
        }
        if (filteredlist.isEmpty()) {
        } else {
            adapter.filterList(filteredlist);
        }
    }
}