package com.example.cashledger.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cashledger.Activities.BusinessActivity3;
import com.example.cashledger.R;
import com.example.cashledger.modelClasses.Business;
import com.example.cashledger.modelClasses.Category;

import java.io.IOException;
import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    Context context;
    ArrayList<Category> categories;
    private Business business;

    private int selectedItemPosition=-1;
    public CategoryAdapter(Context context, ArrayList<Category> categories, Business business) {
        this.context = context;
        this.categories = categories;
        this.business = business;
    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.category_rec_view_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {
        int pos = position;
        holder.categoryName.setText(categories.get(position).getCategory());
        holder.categoryIcon.setImageResource(categories.get(position).getDrawable());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedItemPosition = pos;
                notifyDataSetChanged();
            }
        });

        if(selectedItemPosition == position) {
            holder.layout.setBackgroundResource(R.drawable.selected_layout);
            getAdapterPosition();
        }
        else
            holder.layout.setBackgroundResource(R.drawable.brown_corner);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView categoryName;
        private ImageView categoryIcon;
        private LinearLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.category_txt);
            categoryIcon = itemView.findViewById(R.id.category_icon);
            layout = itemView.findViewById(R.id.category_layout);
        }
    }

    public int getAdapterPosition(){
        return selectedItemPosition;
    }
}
