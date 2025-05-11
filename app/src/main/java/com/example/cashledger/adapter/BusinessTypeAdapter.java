package com.example.cashledger.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cashledger.Activities.BusinessActivity3;
import com.example.cashledger.R;
import com.example.cashledger.modelClasses.Business;
import com.example.cashledger.modelClasses.BusinessType;
import com.example.cashledger.modelClasses.Category;

import java.io.IOException;
import java.util.ArrayList;

public class BusinessTypeAdapter extends RecyclerView.Adapter<BusinessTypeAdapter.ViewHolder>{

    private Context context;
    private ArrayList<BusinessType> types;
    private Business business;
    private int selectedItemPosition=-1;

    public BusinessTypeAdapter(Context context, ArrayList<BusinessType> types, Business business) {
        this.context = context;
        this.types = types;
        this.business=business;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(context).inflate(R.layout.bussiness_type_rec_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int pos=position;
        holder.typeName.setText(types.get(position).getType());
        holder.typeIcon.setImageResource(types.get(position).getDrawable());

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
        return types.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView typeName;
        private ImageView typeIcon;
        private LinearLayout layout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            typeName=itemView.findViewById(R.id.type_txt);
            typeIcon=itemView.findViewById(R.id.type_icon);
            layout=itemView.findViewById(R.id.type_layout);
        }
    }

    public int getAdapterPosition(){
        return selectedItemPosition;
    }
}
