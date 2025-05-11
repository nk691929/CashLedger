package com.example.cashledger.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cashledger.Activities.CustomerEditActivity;
import com.example.cashledger.Activities.KhataActivity;
import com.example.cashledger.R;
import com.example.cashledger.modelClasses.Amount;
import com.example.cashledger.modelClasses.Book;
import com.example.cashledger.modelClasses.Business;
import com.example.cashledger.modelClasses.Customer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CustomerRecAdapter extends RecyclerView.Adapter<CustomerRecAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Customer> customerViewList;
    private ArrayList<Amount> customerKhataList;
    private Customer customer;
    private static final String USER="users";
    private static FirebaseAuth auth;
    private Business business;
    private Book book;

    public CustomerRecAdapter(Context context, ArrayList<Customer> customerView, Business business, Book book) {
        this.context = context;
        this.customerViewList=customerView;
        this.book=book;
        this.business=business;
        auth=FirebaseAuth.getInstance();
        customerKhataList=new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.customer_rec_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int pos=position;

        holder.name.setText(customerViewList.get(position).getCustomerName());
        holder.phone.setText(customerViewList.get(position).getCustomerPhone());
        holder.logo.setText(customerViewList.get(position).getCustomerName().charAt(0)+"");
        holder.customerHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customer=customerViewList.get(pos);
                Intent intent =new Intent(context, KhataActivity.class);
                intent.putExtra("Customer",customer);
                intent.putExtra("Book",book);
                intent.putExtra("Business",business);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });



        //more clickListener
        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu menu = new PopupMenu(context, view);
                menu.inflate(R.menu.customer_more_menu);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.edit_customer_menu:
                                editCustomer(pos);
                                return true;
                            case R.id.delete_customer_menu:
                                deleteCustomer(pos);
                                return true;
                        }
                        return false;
                    }
                });
                menu.show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return customerViewList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView name;
        private final TextView phone;
        private final TextView logo;
        private final RelativeLayout customerHolder;
        private ImageView more;
        public  ViewHolder(View itemView){
            super(itemView);
            name=itemView.findViewById(R.id.nameCustomer);
            phone=itemView.findViewById(R.id.phoneCustomer);
            logo=itemView.findViewById(R.id.customerIcon);
            more=itemView.findViewById(R.id.customer_more);
            customerHolder=itemView.findViewById(R.id.customer_holder);
        }
    }


    // method for filtering our recyclerview items.
    public void filterList(ArrayList<Customer> filterlist) {
        customerViewList = filterlist;
        notifyDataSetChanged();
    }


    //Fetching khata of customer from firebase
    private void fetchCustomerKhata(String id)
    {
        String userKey=auth.getUid();
        DatabaseReference reference;
        reference= FirebaseDatabase.getInstance().getReference(USER).child(userKey)
                .child(business.getId()).
                 child("Khata").child(id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ss : snapshot.getChildren()) {
                        Amount amount1 = ss.getValue(Amount.class);
                        customerKhataList.add(amount1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Error : "+ error, Toast.LENGTH_SHORT).show();
            }
        });
    }


    //Find sum of Taken Amount
    private Long getTakenAmount(ArrayList<Amount> tempList)
    {
        Long sum=0L;
        for(Amount amount:tempList){
            sum=sum+amount.getTakenAmount();
        }
        return sum;
    }

    //Find sum of Given Amount
    private Long getGivenAmount(ArrayList<Amount> tempList)
    {
        Long sum=0L;
        for(Amount amount:tempList){
            sum=sum+amount.getTakenAmount();
        }
        return sum;
    }


    private Pair<String,Long> getOverAllAmount(Long takenAmount,Long givenAmount){
        String quarry=null;
        Long amount=0L;

        if(takenAmount>givenAmount){
            quarry="Lene";
            amount=takenAmount;
        }else if(givenAmount>takenAmount){
            quarry="Dene";
            amount=givenAmount;
        }else {
            quarry="";
            amount=0L;
        }
        Pair<String,Long> overAllAmount=new Pair<String,Long>(quarry,amount);
        return overAllAmount;
    }


    //customer Edit method
    private void editCustomer(int pos){
        Intent intent=new Intent(context, CustomerEditActivity.class);
        intent.putExtra("Business",business);
        intent.putExtra("Book",book);
        intent.putExtra("Customer",customerViewList.get(pos));
        context.startActivity(intent);
        ((Activity)context).finish();

    }


    //customer delete method
    private void deleteCustomer(int pos){
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle("Delete");
        builder.setMessage("Are You Sure?");
        builder.setIcon(R.drawable.book);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String userId= FirebaseAuth.getInstance().getUid();
                DatabaseReference reference= FirebaseDatabase.getInstance().getReference("users").child(userId)
                        .child(business.getId()).
                        child(book.getBookID()).child("customers").child(customerViewList.get(pos).getCustomerId());
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        snapshot.getRef().removeValue();
                        customerViewList.remove(pos);
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                DatabaseReference khataRef=FirebaseDatabase.getInstance().getReference("users").child(userId)
                        .child(business.getId()).
                        child(book.getBookID()).child(customerViewList.get(pos).getCustomerId());
                khataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot snap:snapshot.getChildren()) {
                            snapshot.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        builder.setNegativeButton("No",null);
        builder.show();
    }

}
