package com.example.cashledger.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cashledger.MainActivity;
import com.example.cashledger.R;
import com.example.cashledger.modelClasses.Business;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

public class BusinessAdapter extends RecyclerView.Adapter<BusinessAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Business> businesses;
    private Business business;
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String BUSINESS_ID = "businessId";
    private String businessId;
    private int selectedPos = -1;
    private String ref;


    public BusinessAdapter(Context context, ArrayList<Business> businesses, Business business, String ref) {
        this.context = context;
        this.businesses = businesses;
        this.business = business;
        this.ref = ref;
    }

    @NonNull
    @Override
    public BusinessAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.business_rec_view_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BusinessAdapter.ViewHolder holder, int position) {
        int pos = position;
        holder.businessName.setText(businesses.get(position).getName());
        holder.businessBooks.setText("Books : " + businesses.get(position).getBooks());


        if (businesses.get(pos).getId().equals(business.getId()) && !ref.equals("move")) {
            holder.itemView.setBackgroundResource(R.drawable.selected_layout);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ref.equals("switch")) {
                    selectedPos = pos;
                    holder.itemView.setBackgroundResource(R.drawable.selected_layout);
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("Business", businesses.get(pos));
                    saveSharedPref();
                    context.startActivity(intent);
                    ((Activity) context).finish();
                } else if (ref.equals("move")) {
                    selectedPos = pos;
                    notifyDataSetChanged();
                }
            }
        });


        if (ref.equals("move")) {
            if (selectedPos == position) {
                holder.itemView.setBackgroundResource(R.drawable.selected_layout);
            } else {
                holder.itemView.setBackgroundResource(R.drawable.brown_corner);
            }
        }
    }

    @Override
    public int getItemCount() {
        return businesses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView businessName, businessBooks;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            businessBooks = itemView.findViewById(R.id.business_book_num);
            businessName = itemView.findViewById(R.id.business_name);
        }
    }

    public void saveSharedPref() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(BUSINESS_ID, businesses.get(selectedPos).getId());
        editor.apply();
    }

    public int getAdapterPos() {
        return selectedPos;
    }
}
