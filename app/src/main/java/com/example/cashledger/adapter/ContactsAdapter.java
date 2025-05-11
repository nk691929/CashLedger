package com.example.cashledger.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cashledger.Activities.CustomerActivity;
import com.example.cashledger.R;
import com.example.cashledger.modelClasses.Book;
import com.example.cashledger.modelClasses.Business;
import com.example.cashledger.modelClasses.ContactModel;
import com.example.cashledger.modelClasses.Customer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    private Context context;
    private Activity contextAct;
    private String id;
    private Business business;
    private Book book;
    private ArrayList<Customer> customerList;
    private List<ContactModel> contacts;
    private int selectedPos=-1;

    public ContactsAdapter(@NonNull Context context, List<ContactModel> contacts,Business business,Book book,ArrayList<Customer> customerList) {
        this.context=context;
        this.contacts=contacts;
        this.business=business;
        this.book=book;
        this.customerList=customerList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.contacts_adapter_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int pos=position;
        holder.name.setText(contacts.get(position).name);
        holder.number.setText(contacts.get(position).mobileNumber);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedPos=pos;

                Customer customer=new Customer();
                String userId = FirebaseAuth.getInstance().getUid();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(userId)
                        .child(business.getId()).
                        child(book.getBookID()).child("customers");
                String customerID = reference.push().getKey();
                customer.setCustomerId(customerID);
                customer.setCustomerName(contacts.get(pos).name);
                customer.setCustomerPhone(contacts.get(pos).mobileNumber);
                customer.setBookId(book.getBookID());
                customerList.add(customer);
                reference = reference.child(customerID);
                reference.setValue(customer);

                Intent intent=new Intent(context,CustomerActivity.class);
                intent.putExtra("Business",business);
                intent.putExtra("Business",business);
                intent.putExtra("Book",book);
                intent.putExtra("customerList",customerList);
                intent.putExtra("Code","contact");
                context.startActivity(intent);
                ((Activity)context).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView name,number;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.contact_name);
            number=itemView.findViewById(R.id.contact_phone);
        }
    }


    // method for filtering our recyclerview items.
    public void filterList(ArrayList<ContactModel> filterList) {
        contacts = filterList;
        notifyDataSetChanged();
    }

    //getPos
    private int getAdapterPos(){
       return selectedPos;
    }
}
