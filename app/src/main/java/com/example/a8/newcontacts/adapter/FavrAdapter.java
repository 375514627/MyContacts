package com.example.a8.newcontacts.adapter;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.a8.newcontacts.R;
import com.example.a8.newcontacts.bean.FavrContacts;
import com.example.a8.newcontacts.view.CircleImageView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;

/**
 * Created by A8 on 2016/5/12.
 */

public class FavrAdapter extends BaseAdapter {

    private static List<FavrContacts> list = new ArrayList<>();
    private Context context;
    private View view;

    public FavrAdapter(Context context) {
        this.context = context;
    }

    public static List<FavrContacts> getList() {
        return list;
    }

    public void setList(List<FavrContacts> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public FavrContacts getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        FavrViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.girdview_list_item, parent, false);
            ViewGroup.LayoutParams layoutParams = convertView.getLayoutParams();
            layoutParams.width = context.getResources().getDisplayMetrics().widthPixels / 3;
            convertView.setLayoutParams(layoutParams);
            holder = new FavrViewHolder();
            holder.iv_favrhead = (CircleImageView) convertView.findViewById(R.id.iv_favrhead);
            holder.tv_favrname = (TextView) convertView.findViewById(R.id.tv_favrname);
            convertView.setTag(holder);

        } else {
            holder = (FavrViewHolder) convertView.getTag();
        }
        FavrContacts favrContacts = list.get(position);
        Bitmap b = favrContacts.getBitmap();
        if (b == null) {
            holder.iv_favrhead.setImageResource(R.mipmap.ic_person_white_120dp);
        } else {
            holder.iv_favrhead.setImageBitmap(b);
        }
        holder.tv_favrname.setText(favrContacts.getName());
        return convertView;
    }

    class FavrViewHolder {

        private CircleImageView iv_favrhead;
        private TextView tv_favrname;
    }

    public void initFavr() {
        list.clear();
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPostExecute(Void aVoid) {
                ((PtrClassicFrameLayout) ((Activity) context).findViewById(R.id.ptr_favr)).refreshComplete();
                notifyDataSetChanged();
            }

            @Override
            protected Void doInBackground(Void... params) {

                ContentResolver resolver = context.getContentResolver();
                Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, new String[]{"_id"}, "starred =?", new String[]{"1"}, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        int id = cursor.getInt(0);
                        Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);
                        InputStream is = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(), uri);
                        Bitmap b = BitmapFactory.decodeStream(is);
                        Cursor data = context.getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI,
                                new String[]{"display_name"}, "contact_id=?", new String[]{id + ""}, null);
                        if (data != null) {
                            data.moveToNext();
                        }
                        String name = data.getString(0);
                        data.close();
                        list.add(new FavrContacts(id, name, b));
                    }
                }
                cursor.close();
                return null;
            }

        }.execute();

    }

    public void change() {
        initFavr();
        notifyDataSetChanged();
    }

    public void delete(int id) {
        for (FavrContacts f : list) {
            if (f.getId() == id) {
                list.remove(f);
                break;
            }
        }
        notifyDataSetChanged();
    }
}
