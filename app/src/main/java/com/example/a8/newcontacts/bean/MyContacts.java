package com.example.a8.newcontacts.bean;

import android.graphics.Bitmap;

import java.io.Serializable;

public class MyContacts implements Serializable {

    private String name;
    private String email;
    private String phoneNum;
    private Bitmap bitmap;
    private String sort_key;
    private String address;
    private String business;
    private int starred;
    private int ID;

    public int getStarred() {
        return starred;
    }

    public void setStarred(int starred) {
        this.starred = starred;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String adress) {
        this.address = address;
    }

    public String getBusiness() {
        return business;
    }

    public void setBusiness(String business) {
        this.business = business;
    }

    public String getSort_key() {
        return sort_key;
    }

    public void setSort_key(String sort_key) {
        this.sort_key = sort_key;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public MyContacts() {
    }

    public MyContacts(int starred,String name, String email, String phoneNum, Bitmap bitmap, String sort_key, String address, String buiness, int ID) {
        this.name = name;
        this.email = email;
        this.phoneNum = phoneNum;
        this.bitmap = bitmap;
        this.sort_key = sort_key;
        this.address = address;
        this.business = buiness;
        this.starred = starred;
        this.ID = ID;
    }

    @Override
    public String toString() {
        return "MyContacts{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phoneNum='" + phoneNum + '\'' +
                ", bitmap=" + bitmap +
                ", sort_key='" + sort_key + '\'' +
                ", address='" + address + '\'' +
                ", business='" + business + '\'' +
                ", starred=" + starred +
                ", ID=" + ID +
                '}';
    }
}
