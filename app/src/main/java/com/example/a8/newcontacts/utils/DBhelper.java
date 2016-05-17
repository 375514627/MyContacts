package com.example.a8.newcontacts.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.widget.Toast;

import com.example.a8.newcontacts.bean.MyContacts;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by A8 on 2016/4/24.
 */
public class DBhelper {

    private Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    public DBhelper(Context context) {
        this.context = context;
    }

    private Uri dataUri = ContactsContract.Data.CONTENT_URI;
    private Uri rawContactUri = ContactsContract.RawContacts.CONTENT_URI;

    //删除功能 传入联系人的ID 并进行条件查找后该列所有数据
    public void delete(int id) {
        int delete = context.getContentResolver().delete(rawContactUri, "_id = ?", new String[]{id + ""});
        if (delete > 0) {
            Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
        }
    }

    //增加功能 传入一个封装好的
    public long insert(MyContacts contacts) {

        ContentResolver contentResolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        String firstCase = contacts.getName().charAt(0) + "";
        String firstPinyin = Chinese2Pinyin.getPingYing(firstCase);
        values.put("phonebook_label", firstPinyin);
        Uri uri = contentResolver.insert(rawContactUri, values);

        long id = ContentUris.parseId(uri);

        //得到对象的所有相关数据 一个一个加入到数据的表中
        String name = contacts.getName();
        String adress = contacts.getAddress();
        String business = contacts.getBusiness();
        String email = contacts.getEmail();
        String phoneNum = contacts.getPhoneNum();
        byte[] b = null;
        Bitmap bitmap = contacts.getBitmap();
        if (bitmap != null) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                b = baos.toByteArray();
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        values.clear();
        values.put("mimetype", "vnd.android.cursor.item/name");
        values.put("data1", name);
        values.put("raw_contact_id", id);
        contentResolver.insert(dataUri, values);

        values.clear();
        values.put("mimetype", "vnd.android.cursor.item/photo");
        values.put("data1", b);
        values.put("raw_contact_id", id);
        contentResolver.insert(dataUri, values);

        values.clear();
        values.put("mimetype", "vnd.android.cursor.item/postal-address_v2");
        values.put("data1", adress);
        values.put("raw_contact_id", id);
        contentResolver.insert(dataUri, values);

        values.clear();
        values.put("mimetype", "vnd.android.cursor.item/organization");
        values.put("data1", business);
        values.put("raw_contact_id", id);
        contentResolver.insert(dataUri, values);

        values.clear();
        values.put("mimetype", "vnd.android.cursor.item/email_v2");
        values.put("data1", email);
        values.put("raw_contact_id", id);
        contentResolver.insert(dataUri, values);

        values.clear();
        values.put("mimetype", "vnd.android.cursor.item/phone_v2");
        values.put("data1", phoneNum);
        values.put("raw_contact_id", id);
        contentResolver.insert(dataUri, values);

        return id;
    }

    //变更是否为收藏联系人
    public int starred(int id, boolean how) {
        ContentValues values = new ContentValues();
        values.put("starred", how ? "1" : "0");
        int update = context.getContentResolver().update(ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id), values, null, null);
        return update;
    }

    public static List<MyContacts> loadContact(ContentResolver resolver) {
        List<MyContacts> list = new ArrayList<>();
        Cursor data = resolver.query(ContactsContract.RawContacts.CONTENT_URI,
                new String[]{"contact_id", "display_name", "phonebook_label","starred"}, "deleted=?", new String[]{"0"}, "phonebook_label");
        while (data.moveToNext()) {
            int id = data.getInt(0);
            String name = data.getString(1);
            String sort_key = data.getString(2);
            int starred = data.getInt(3);
            Bitmap contactPhoto = null;
            Uri dataUri = Uri.parse("content://com.android.contacts/contacts/" + id + "/data");
            String phone = null;
            String email = null;
            String address = null;
            String business =null;
            Cursor cursor = resolver.query(dataUri, new String[]{"mimetype", "data1"}, null, null, null);
            while (cursor.moveToNext()) {
                String mimetype = cursor.getString(0);
                String temp = cursor.getString(1);
                switch (mimetype) {
                    case "vnd.android.cursor.item/phone_v2":
                        phone = temp;
                        break;
                    case "vnd.android.cursor.item/photo":
                        Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);
                        InputStream is = ContactsContract.Contacts.openContactPhotoInputStream(resolver, uri);
                        contactPhoto = BitmapFactory.decodeStream(is);
                        break;
                    case "vnd.android.cursor.item/email_v2":
                        email = temp;
                        break;
                    case "vnd.android.cursor.item/organization":
                        business=temp;
                        break;
                    case "vnd.android.cursor.item/postal-address_v2":
                        address = temp;
                        break;
                }
            }
            cursor.close();
            list.add(new MyContacts(starred,name,email,phone,contactPhoto,sort_key,address,business,id));
        }
        data.close();

        Collections.sort(list, new Comparator<MyContacts>() {

            @Override
            public int compare(MyContacts lhs, MyContacts rhs) {
                if (lhs.getSort_key().equals(rhs.getSort_key())) {
                    return lhs.getName().compareTo(rhs.getName());
                }
                return lhs.getSort_key().compareTo(rhs.getSort_key());
            }

        });

        return list;
    }

}
