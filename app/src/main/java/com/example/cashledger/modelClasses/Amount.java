package com.example.cashledger.modelClasses;

import java.io.Serializable;
import java.sql.Time;
import java.util.Date;

public class Amount implements Serializable {
    public String amountId="";
    public String customerId="";
    public Long takenAmount=0L;
    public Long givenAmount=0L;
    public String date="";
    public String time="";
    public String details="";
    public String billPhoto="";

    public Amount() {
    }

    //setter
    public void setAmountId(String amountId) {
        this.amountId = amountId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setTakenAmount(Long takenAmount) {
        this.takenAmount = takenAmount;
    }

    public void setGivenAmount(Long givenAmount) {
        this.givenAmount = givenAmount;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setBillPhoto(String billPhoto) {
        this.billPhoto = billPhoto;
    }

    //getter

    public String getAmountId() {
        return amountId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public Long getTakenAmount() {
        return takenAmount;
    }

    public Long getGivenAmount() {
        return givenAmount;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getDetails() {
        return details;
    }

    public String getBillPhoto() {
        return billPhoto;
    }
}
