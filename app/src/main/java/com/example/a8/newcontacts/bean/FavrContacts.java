package com.example.a8.newcontacts.bean;

import android.graphics.Bitmap;

/**
 * Created by A8 on 2016/5/12.
 */
public class FavrContacts {

    private int id;
    private String name;
    private Bitmap bitmap ;

    public FavrContacts(int id, String name,Bitmap bitmap) {
        this.id = id;
        this.name = name;
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
