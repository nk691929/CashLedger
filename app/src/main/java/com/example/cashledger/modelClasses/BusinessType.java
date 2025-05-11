package com.example.cashledger.modelClasses;

public class BusinessType {
    public int drawable;
    public String type;

    public BusinessType(String type,int drawable) {
        this.type = type;
        this.drawable=drawable;
    }

    public String getType() {
        return type;
    }

    public int getDrawable() {
        return drawable;
    }
}
