package com.example.cashledger.Activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.cashledger.R;
import com.example.cashledger.adapter.ContactsAdapter;
import com.example.cashledger.databinding.ActivityContactsBinding;
import com.example.cashledger.modelClasses.Book;
import com.example.cashledger.modelClasses.Business;
import com.example.cashledger.modelClasses.ContactModel;
import com.example.cashledger.modelClasses.Customer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ContactsActivity extends AppCompatActivity {
    private List<ContactModel> contacts;
    private ActivityContactsBinding binding;
    private ContactsAdapter adapter;
    private RelativeLayout switchBusiness;
    private TextView businessNameTv,businessSwitchTv;
    private ImageView businessNameImg;
    private Business business;
    private Book book;
    private ArrayList<Customer> customerList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBar();
        binding=ActivityContactsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //getting Intent
        business=(Business) getIntent().getSerializableExtra("Business");
        customerList=(ArrayList<Customer>) getIntent().getSerializableExtra("customerList");
        book = (Book) getIntent().getSerializableExtra("Book");

        //permission check
        String[] permission = {
                "android.permission.READ_CONTACTS"
        };
        checkPermission(permission[0], 16);
        binding.contactsListView.setLayoutManager(new LinearLayoutManager(this));
        contacts=getContacts(this);
        adapter=new ContactsAdapter(this,contacts,business,book,customerList);
        binding.contactsListView.setAdapter(adapter);
        searchContacts();
        addCustomersLayout();
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
        businessNameImg = customView.findViewById(R.id.switch_Bussiness);
        businessNameImg.setVisibility(View.GONE);
        businessSwitchTv.setVisibility(View.GONE);
        businessNameTv.setText("Contacts");
        getSupportActionBar().setCustomView(customView);
    }


    //getting Contact List
    public List<ContactModel> getContacts(Context ctx) {
        List<ContactModel> list = new ArrayList<>();
        ContentResolver contentResolver = ctx.getContentResolver();
        Uri uri=ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String nam = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String num = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                ContactModel contactModel=new ContactModel();
                contactModel.name=nam;
                contactModel.mobileNumber=num;
                list.add(contactModel);
            }
            cursor.close();
        }
        return list;
    }

    // Function to check and request permission
    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(ContactsActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(ContactsActivity.this, new String[]{permission}, requestCode);
        }else {
            contacts=getContacts(this);
            adapter=new ContactsAdapter(this,contacts,business,book,customerList);
            binding.contactsListView.setLayoutManager(new LinearLayoutManager(this));
            binding.contactsListView.setAdapter(adapter);
        }
    }


    //search
    void searchContacts(){
        binding.searchContacts.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
        ArrayList<ContactModel> filteredlist = new ArrayList<ContactModel>();

        for (ContactModel item : contacts) {
            if (item.name.toLowerCase().contains(text.toLowerCase())||item.mobileNumber.contains(text)) {
                filteredlist.add(item);
            }
        }
        if (filteredlist.isEmpty()) {
        } else {
            adapter.filterList(filteredlist);
        }
    }


    //adding Customers
    private void addCustomersLayout() {
        binding.addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog customDialog = new Dialog(ContactsActivity.this);
                customDialog.setContentView(R.layout.add_customer_custm_dialog);
                EditText customerName, customerPhone;
                Button addCustomerBtn = customDialog.findViewById(R.id.add_cus_dialog);
                customerName = customDialog.findViewById(R.id.cust_name_dialog);
                customerPhone = customDialog.findViewById(R.id.cust_number_dialog);
                onClickOnAddButton(addCustomerBtn, customDialog, customerName, customerPhone);
                customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                customDialog.show();
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
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(userId)
                            .child(business.getId()).
                            child(book.getBookID()).child("customers");
                    String customerID = reference.push().getKey();
                    customer.setCustomerId(customerID);
                    customer.setBookId(book.getBookID());
                    customerList.add(customer);
                    reference = reference.child(customerID);
                    reference.setValue(customer);
                    dialogBuild.dismiss();
                    Intent intent=new Intent(ContactsActivity.this,CustomerActivity.class);
                    intent.putExtra("Business",business);
                    intent.putExtra("Business",business);
                    intent.putExtra("Book",book);
                    intent.putExtra("customerList",customerList);
                    intent.putExtra("Code","contact");
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(ContactsActivity.this,CustomerActivity.class);
        intent.putExtra("Business",business);
        intent.putExtra("Business",business);
        intent.putExtra("Book",book);
        intent.putExtra("customerList",customerList);
        intent.putExtra("Code","contact");
        startActivity(intent);
        finish();
    }
}