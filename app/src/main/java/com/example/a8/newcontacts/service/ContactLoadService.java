package com.example.a8.newcontacts.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.example.a8.newcontacts.bean.MyContacts;
import com.example.a8.newcontacts.utils.DBHelper;

import java.util.ArrayList;
import java.util.List;

public class ContactLoadService extends Service {

    private List<MyContacts> list;

    public ContactLoadService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    public class MyBinder extends Binder {

        public void setContactLoadCallBack(ContactLoadCallBack contactLoadCallBack) {
            mLoadCallBack = contactLoadCallBack;
        }

        public ContactLoadService getService() {
            return ContactLoadService.this;
        }

    }

    public interface ContactLoadCallBack {
        void getList(List<MyContacts> list);
    }

    private ContactLoadCallBack mLoadCallBack;
    private Handler h = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            if (mLoadCallBack != null) {
                mLoadCallBack.getList(list);
            }

        }
    };

    public void loadContacts() {
        list = new ArrayList<>();

        new Thread(){

            @Override
            public void run() {

                list = DBHelper.loadContact(getContentResolver());
                h.sendEmptyMessage(1);

            }
        }.start();
    }

}
