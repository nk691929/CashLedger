package com.example.cashledger.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cashledger.Activities.AmountActivity;
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


public class KhataRecordAdapter extends RecyclerView.Adapter<KhataRecordAdapter.ViewHolder> {
    private final Customer customer;
    private final Context context;
    private final Book book;
    private final Business business;
    private ArrayList<Amount> amounts;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.table_row_layout, parent, false);
        return new ViewHolder(view);
    }

    public KhataRecordAdapter(Context context, ArrayList<Amount> amounts, Business business, Book book, Customer customer) {
        this.context = context;
        this.amounts = amounts;
        this.customer = customer;
        this.business=business;
        this.book = book;
    }

    public void setAmounts(ArrayList<Amount> amounts) {
        this.amounts = amounts;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int pos = position;
        String currDate = amounts.get(position).getDate();
        String currTime = amounts.get(position).getTime();
        String mergeDate = currTime + " \n" + currDate;
        holder.date.setText(mergeDate);
        String amountLiye = String.valueOf(amounts.get(position).getTakenAmount());
        String amountDiye = String.valueOf(amounts.get(position).getGivenAmount());
        if (amountLiye.equals("0")) {
            holder.takenAmount.setText("");
            holder.givenAmount.setText(amountDiye);
        } else {
            holder.takenAmount.setText(amountLiye);
            holder.givenAmount.setText("");
        }

        //ClickListener on holder
        holder.rowContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, AmountActivity.class);
                intent.putExtra("Amount", amounts.get(pos));
                intent.putExtra("Business", business);
                intent.putExtra("Book", book);
                intent.putExtra("Customer", customer);
                context.startActivity(intent);
            }
        });


        holder.rowContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete");
                builder.setMessage("Are You Sure?");
                builder.setIcon(R.drawable.book);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String uid = FirebaseAuth.getInstance().getUid();
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(uid).
                                child(business.getId()).
                                child(book.getBookID()).child(customer.getCustomerId()).child("khata").child(amounts.get(pos).getAmountId());
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                snapshot.getRef().removeValue();
                                amounts.remove(pos);
                                notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });
                builder.setNegativeButton("No", null);
                builder.show();
                return false;
            }
        });


    }

    @Override
    public int getItemCount() {
        return amounts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView date;
        private final TextView givenAmount;
        private final TextView takenAmount;
        private final LinearLayout rowContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date_of_amount);
            givenAmount = itemView.findViewById(R.id.given_amount);
            takenAmount = itemView.findViewById(R.id.taken_amount);
            rowContainer = itemView.findViewById(R.id.row_container);
        }
    }
}
