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
import android.support.v7.app.AlertDialog;
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

        //初始化contact对象
        Intent intent = getIntent();
        contact = (MyContacts) intent.getSerializableExtra("contact");
        setContentView(R.layout.activity_contact);

        //初始化toolbar
        initToolbar();

        //初始化DBHelper工具
        DBHelper = new DBHelper(this);

        //相应初始化方法
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

        //设置toolbar菜单的消极按钮(回退按钮)的监听事件
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
                    case R.id.menu_create:
                        //change()方法
                        change();
                        break;
                    case R.id.menu_delete:
                        //delete()方法
                        delete();
                        break;
                    case R.id.menu_starred:
                        //starred()方法 收藏 将此按钮的item传进方法, 用于更改图标是空心还是实心
                        starred(item);
                        break;
                }
                return true;
            }
        });
    }

    private void initViews() {

        //找到所有用到的控件id
        iv_directions = findViewById(R.id.iv_directions);
        iv_sendMsg = findViewById(R.id.iv_sendMsg);

        tv_phone = (TextView) findViewById(R.id.tv_phone);
        tv_email = (TextView) findViewById(R.id.tv_email);
        tv_place = (TextView) findViewById(R.id.tv_place);
        tv_business = (TextView) findViewById(R.id.tv_business);

        textViews = new TextView[]{tv_business, tv_email, tv_place, tv_phone};

        views = new View[]{findViewById(R.id.rl_business), findViewById(R.id.rl_email)
                , findViewById(R.id.rl_place), findViewById(R.id.rl_phone), findViewById(R.id.iv_sendMsg)};

        //设置监听器
        setListener();


    }

    private void setListener() {
        //循环遍历所有可点击的布局 设置上点击监听事件
        for (View v : views) {
            v.setOnClickListener(this);
        }

        //设置长点击监听事件
        for (View v : views) {
            v.setOnLongClickListener(this);
        }
    }

    TextView[] textViews;
    MyContacts contact;
    View[] views;

    private void setView() {

        //获取对象的所有属性
        name = contact.getName();
        id = contact.getID();
        email = contact.getEmail();
        address = contact.getAddress();
        bitmap = contact.getBitmap();
        business = contact.getBusiness();
        phoneNum = contact.getPhoneNum();

        //设置标题为联系人的名字
        getSupportActionBar().setTitle(name + "");
        String[] strs = new String[]{business, email, address, phoneNum};

        //判断该联系人内的信息是否为空, 如果是则把对应的控件设置为GONE(隐藏控件并且不占据空间)
        for (int i = 0; i < strs.length; i++) {
            if (!TextUtils.isEmpty(strs[i])) {
                //如果有内容,则设置相应的控件的文本内容
                textViews[i].setText(strs[i]);
            } else {
                views[i].setVisibility(View.GONE);
            }
        }

        //如果地址为空, 则将定位图标隐藏.
        if (TextUtils.isEmpty(address)) {
            iv_directions.setVisibility(View.GONE);
        }

    }

    //创建菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contact, menu);

        //找到收藏按钮的menu item id
        MenuItem item = menu.findItem(R.id.menu_starred);
        Intent intent = getIntent();
        //获取传入的意图里contact对象 判断是否为收藏的
        contact = (MyContacts) intent.getSerializableExtra("contact");
        if (contact.getStarred() > 0) {
            menu.findItem(R.id.menu_starred).setChecked(true);
            //如果是,则图标换成实心的
            item.setIcon(R.mipmap.ic_star_24dp);
        }
        return true;
    }

    //点击编辑后,将contact传到另一个activity 启动意图 结束当前界面
    private void change() {
        Intent intent = new Intent(this, AddContactActivity.class);
        intent.putExtra("contact", contact);
        startActivity(intent);
        finish();
    }

    //返回结果的常量.
    public static final int DELETE_RESULT = 0x123;

    //删除联系人的方法. 跳出一个警告对话框
    private void delete() {
        new AlertDialog.Builder(this)
                .setMessage("删除联系人")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //删除该联系人
                        DBHelper.delete(id);
                        //更新所有联系人的列表
                        ShowContacts_Fragment.getContactAdapter().remove(id);
                        //回到上次的界面
                        finish();
                    }
                })
                .setNegativeButton("取消", null).show();
    }

    //收藏执行的操作
    private void starred(MenuItem item) {
        boolean b = !item.isChecked();
        //把选择状态取反 并设置图标
        item.setChecked(b);
        if (b) {
            item.setIcon(R.mipmap.ic_star_24dp);
        } else {
            item.setIcon(R.mipmap.ic_star_outline_24dp);
        }
        //调用DBHelper的收藏方法
        DBHelper.starred(id, b);

        //刷新两个碎片里的适配器, 实时更新收藏联系人和所有联系人的列表
        FavrContacts_Fragment.refreshAdapter();
        ShowContacts_Fragment.refreshAdapter();
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.rl_phone:
                //打电话
                intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tv_phone.getText().toString()));
                break;
            case R.id.iv_directions:
                //百度地图接口
                break;
            case R.id.iv_sendMsg:
                //跳到发短信
                intent = new Intent(android.content.Intent.ACTION_SENDTO, Uri.parse("smsto://" + tv_phone.getText().toString()));
                break;
        }
        if (intent != null) {
            //如果不为空 最后执行意图
            startActivity(intent);
        }
    }

    @Override
    public boolean onLongClick(View v) {

        //以下为死代码 获取系统的粘贴板管理者 复制长按选中的内容
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
