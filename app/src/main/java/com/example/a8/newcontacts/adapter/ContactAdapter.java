package com.example.a8.newcontacts.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.a8.newcontacts.R;
import com.example.a8.newcontacts.bean.MyContacts;
import com.example.a8.newcontacts.view.CircleImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by A8 on 2016/5/11.
 */
public class ContactAdapter extends BaseAdapter {

    private Context context;

    private static List<MyContacts> list = new ArrayList<>();

    public static List<MyContacts> getList() {
        return list;
    }

    public void setList(List<MyContacts> list) {
        this.list = list;
    }

    public ContactAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public MyContacts getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_list_item, parent, false);
            holder = new ViewHolder();
            holder.iv_headicon = (CircleImageView) convertView.findViewById(R.id.iv_headIcon);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        MyContacts contacts = getItem(position);
        Bitmap b = contacts.getBitmap();
        if (b == null) {
            holder.iv_headicon.setImageResource(R.mipmap.ic_person_white_120dp);
        } else {
            holder.iv_headicon.setImageBitmap(b);
        }
        holder.tv_name.setText(contacts.getName());
        return convertView;

    }

    class ViewHolder {

        private TextView tv_name;
        private CircleImageView iv_headicon;

    }

    public void add(MyContacts contacts) {
        list.add(contacts);
        sort();
        notifyDataSetChanged();
    }

    public void sort() {
        Collections.sort(list, new Comparator<MyContacts>() {

            @Override
            public int compare(MyContacts lhs, MyContacts rhs) {
                if (lhs.getSort_key().equals(rhs.getSort_key())) {
                    return lhs.getName().compareTo(rhs.getName());
                }
                return lhs.getSort_key().compareTo(rhs.getSort_key());
            }

        });
    }

    public void remove(MyContacts contacts) {
        list.remove(contacts);
        notifyDataSetChanged();
    }

    public void remove(int id) {
        for (MyContacts c : list) {
            if (c.getID() == id) {
                list.remove(c);
                break;
            }
        }
        notifyDataSetChanged();
    }

}
