package com.example.cashledger.modelClasses;

import java.io.Serializable;

public class Customer implements Serializable {
    public String customerId="";
    public String bookId="";
    public String customerName="";
    public String customerPhone="";
    public String customerType="";
    public String customerMsgLang="";
    public String customerOverAllAmount="";

    public Customer() {
    }

    //setter

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public void setCustomerMsgLang(String customerMsgLang) {
        this.customerMsgLang = customerMsgLang;
    }

    public void setCustomerOverAllAmount(String customerOverAllAmount) {
        this.customerOverAllAmount = customerOverAllAmount;
    }

    //getter

    public String getCustomerId() {
        return customerId;
    }

    public String getBookId() {
        return bookId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public String getCustomerType() {
        return customerType;
    }

    public String getCustomerMsgLang() {
        return customerMsgLang;
    }

    public String getCustomerOverAllAmount() {
        return customerOverAllAmount;
    }
}
