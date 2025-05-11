package com.example.cashledger.modelClasses;


public class Profile {
   public String userId="";
   public String userName="";
   public String userPhone="";
   public String userPhoto="";
   public String userAddress="";

    public Profile() {
    }

    //setters
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    //getters

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public String getUserAddress() {
        return userAddress;
    }
}
