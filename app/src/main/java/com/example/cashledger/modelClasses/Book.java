package com.example.cashledger.modelClasses;

import java.io.Serializable;

public class Book implements Serializable {
    public String bookID="";
    public String businessId="";
    public String bookName="";
    public String bookOverAllAMount="";
    public String bookUpdatedTime="";

    public Book() {
    }

    //setter
    public void setBookID(String bookID) {
        this.bookID = bookID;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public void setBookOverAllAMount(String bookOverAllAMount) {
        this.bookOverAllAMount = bookOverAllAMount;
    }

    public void setBookUpdatedTime(String bookUpdatedTime) {
        this.bookUpdatedTime = bookUpdatedTime;
    }

    //getter

    public String getBookID() {
        return bookID;
    }

    public String getBusinessId() {
        return businessId;
    }

    public String getBookName() {
        return bookName;
    }

    public String getBookOverAllAMount() {
        return bookOverAllAMount;
    }

    public String getBookUpdatedTime() {
        return bookUpdatedTime;
    }
}
