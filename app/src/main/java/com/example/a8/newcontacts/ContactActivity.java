package com.example.a8.newcontacts;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a8.newcontacts.bean.MyContacts;
import com.example.a8.newcontacts.fragment.FavrContacts_Fragment;
import com.example.a8.newcontacts.fragment.ShowContacts_Fragment;
import com.example.a8.newcontacts.utils.DBHelper;

public class ContactActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private CollapsingToolbarLayout collapsing_toolbar_layout;
    private DBHelper DBHelper;
    private TextView tv_phone;
    private TextView tv_email;
    private TextView tv_place;
    private TextView tv_business;
    private String name;
    private int id;
    private String email;
    private String address;
    private Bitmap bitmap;
    private String phoneNum;
    private String business;
    private View iv_directions;
    private View iv_sendMsg;
    private ClipboardManager mClipboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        contact = (MyContacts) intent.getSerializableExtra("contact");
        setContentView(R.layout.activity_contact);

        initToolbar();


        DBHelper = new DBHelper(this);

        initViews();
        setView();

    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        collapsing_toolbar_layout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        collapsing_toolbar_layout.setCollapsedTitleTextColor(Color.WHITE);
        collapsing_toolbar_layout.setExpandedTitleColor(Color.WHITE);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case android.R.id.home:
                        finish();
                        break;
                    case R.id.menu_create:
                        change();
                        break;
                    case R.id.menu_delete:
                        delete();
                        break;
                    case R.id.menu_starred:
                        starred(item);
                        break;
                }
                return true;
            }
        });
    }

    private void initViews() {

        iv_directions = findViewById(R.id.iv_directions);
        iv_sendMsg = findViewById(R.id.iv_sendMsg);

        tv_phone = (TextView) findViewById(R.id.tv_phone);
        tv_email = (TextView) findViewById(R.id.tv_email);
        tv_place = (TextView) findViewById(R.id.tv_place);
        tv_business = (TextView) findViewById(R.id.tv_business);

        textViews = new TextView[]{tv_business, tv_email, tv_place, tv_phone};

        views = new View[]{findViewById(R.id.rl_business), findViewById(R.id.rl_email)
                , findViewById(R.id.rl_place), findViewById(R.id.rl_phone), findViewById(R.id.iv_sendMsg)};

        setListener();


    }

    private void setListener() {
        for (View v : views) {
            v.setOnClickListener(this);
        }

        for (View v : views) {
            v.setOnLongClickListener(this);
        }
    }

    TextView[] textViews;
    MyContacts contact;
    View[] views;

    private void setView() {

        name = contact.getName();
        id = contact.getID();
        email = contact.getEmail();
        address = contact.getAddress();
        bitmap = contact.getBitmap();
        business = contact.getBusiness();
        phoneNum = contact.getPhoneNum();

        getSupportActionBar().setTitle(name + "");
        String[] strs = new String[]{business, email, address, phoneNum};

        for (int i = 0; i < strs.length; i++) {
            if (!TextUtils.isEmpty(strs[i])) {
                textViews[i].setText(strs[i]);
            } else {
                views[i].setVisibility(View.GONE);
            }
        }

        if (TextUtils.isEmpty(address)) {
            iv_directions.setVisibility(View.GONE);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contact, menu);
        MenuItem item = menu.findItem(R.id.menu_starred);
        Intent intent = getIntent();
        contact = (MyContacts) intent.getSerializableExtra("contact");
        if (contact.getStarred() > 0) {
            menu.findItem(R.id.menu_starred).setChecked(true);
            item.setIcon(R.mipmap.ic_star_24dp);
        }
        return true;
    }


    private void change() {
        Intent intent = new Intent(this, AddContactActivity.class);
        intent.putExtra("contact", contact);
        startActivity(intent);
        finish();
    }

    public static final int DELETE_RESULT = 0x123;

    private void delete() {
        new android.app.AlertDialog.Builder(this)
                .setMessage("删除联系人")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DBHelper.delete(id);
                        ShowContacts_Fragment.getContactAdapter().remove(id);
                        finish();
                    }
                })
                .setNegativeButton("取消", null).show();
    }

    private void starred(MenuItem item) {
        boolean b = !item.isChecked();
        System.out.println("-----" + b);
        item.setChecked(b);
        if (b) {
            item.setIcon(R.mipmap.ic_star_24dp);
        } else {
            item.setIcon(R.mipmap.ic_star_outline_24dp);
        }
        DBHelper.starred(id, b);
        FavrContacts_Fragment.refreshAdapter();
        ShowContacts_Fragment.refreshAdapter();
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.rl_phone:
                intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tv_phone.getText().toString()));
                break;
            case R.id.iv_directions:
                //百度地图接口
                break;
            case R.id.iv_sendMsg:
                intent = new Intent(android.content.Intent.ACTION_SENDTO, Uri.parse("smsto://" + tv_phone.getText().toString()));
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (mClipboard == null) {
            mClipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        }
        ClipData clip = null;
        switch (v.getId()) {

            case R.id.rl_email:
                clip = ClipData.newPlainText("simple text", tv_email.getText());
                break;
            case R.id.rl_phone:
                clip = ClipData.newPlainText("simple text", tv_phone.getText());
                break;

            case R.id.rl_place:
                clip = ClipData.newPlainText("simple text", tv_place.getText());
                break;
        }
        if (clip != null) {
            mClipboard.setPrimaryClip(clip);
            Toast.makeText(this, "已复制到粘贴板", Toast.LENGTH_SHORT).show();
        }
        return true;
    }
}
