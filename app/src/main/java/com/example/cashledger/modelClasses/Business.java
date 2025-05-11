package com.example.cashledger.modelClasses;

import java.io.Serializable;

public class Business implements Serializable {
    public String id="";
    public String name="";
    public String address="";
    public String staffSize="";
    public String category="";
    public String type="";
    public String registrationType="";
    public String phone="";
    public String email="";
    public int books=0;

    public Business(){}

    //setter

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setStaffSize(String staffSize) {
        this.staffSize = staffSize;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setRegistrationType(String registrationType) {
        this.registrationType = registrationType;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setBooks(int books) {
        this.books = books;
    }
    //getter

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getStaffSize() {
        return staffSize;
    }

    public String getCategory() {
        return category;
    }

    public String getType() {
        return type;
    }

    public String getRegistrationType() {
        return registrationType;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public int getBooks() {
        return books;
    }
}
