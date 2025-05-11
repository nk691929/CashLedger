package com.example.cashledger.modelClasses;

import android.graphics.drawable.Drawable;

public class Category {
    public int drawable;
    public String category;

    public Category(String category,int drawable) {
        this.category = category;
        this.drawable=drawable;
    }

    public String getCategory() {
        return category;
    }

    public int getDrawable() {
        return drawable;
    }
}
