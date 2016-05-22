package com.example.a8.newcontacts;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.a8.newcontacts.bean.MyContacts;
import com.example.a8.newcontacts.fragment.ShowContacts_Fragment;
import com.example.a8.newcontacts.utils.DBHelper;
import com.example.a8.newcontacts.utils.ImageLoad;

public class AddContactActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText et_name;
    private EditText et_phone;
    private EditText et_email;
    private EditText et_place;
    private EditText et_business;
    private ImageView iv_setHead;
    private Bitmap b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        //初始化toolbar菜单栏
        initToolbar();
        initViews();
        setView();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //设置当前标题为新增联系人
        toolbar.setTitle("新增联系人");
        setSupportActionBar(toolbar);
        //设置返回上级按钮
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {
        //初始化控件
        iv_setHead = (ImageView) findViewById(R.id.iv_person_120dp);
        et_name = (EditText) findViewById(R.id.et_name);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_email = (EditText) findViewById(R.id.et_email);
        et_place = (EditText) findViewById(R.id.et_place);
        et_business = (EditText) findViewById(R.id.et_business);
        //设置监听器
        setListener();
    }

    private boolean isNew = true;

    private void setView() {
        //设置为是编辑联系人的状态
        isNew = false;
        //如果是编辑联系人而不是新增
        Intent intent = getIntent();
        //更新标题
        getSupportActionBar().setTitle("编辑联系人");
        MyContacts contact = (MyContacts) intent.getSerializableExtra("contact");
        //更新编辑框的内容 为该联系人的相应信息
        if (contact != null) {
            et_name.setText(contact.getName());
            et_phone.setText(contact.getPhoneNum());
            et_email.setText(contact.getEmail());
            et_place.setText(contact.getAddress());
            et_business.setText(contact.getBusiness());
            //设置图片
            iv_setHead.setImageBitmap(contact.getBitmap());
        }

    }

    private void setListener() {
        //为更改相片按钮设置监听器
        findViewById(R.id.btn_change).setOnClickListener(this);
    }

    //初始化菜单栏toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //设置菜单栏 按钮的监听
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                //点击返回上级时 关闭当前界面
                finish();
                break;
            case R.id.add_done:
                //点击勾时 构造一个联系人对象contact

                //获取填入的信息 保存到局部变量
                String name = et_name.getText().toString();
                String business = et_business.getText().toString();
                String email = et_email.getText().toString();
                String phone = et_phone.getText().toString();
                String place = et_place.getText().toString();

                //构造contact
                MyContacts contact = new MyContacts(0, name, email, phone, b, null, place, business, 0);
                //执行方法新创建一个空的联系人 返回一个ID 当ID大于零时 将会创建一个带有信息的联系人
                if (isNew) {
                    addContact(contact);
                } else {
                    changeContact(contact);
                }

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void changeContact(MyContacts contact) {
        DBHelper helper = new DBHelper(this);
        int update = helper.update(contact, contact.getID());

        //如果更新的条目数update大于0则表示新增成功
        Toast.makeText(AddContactActivity.this, update > 0 ? "更改成功" : "更改失败", Toast.LENGTH_SHORT).show();
        if (update > 0) {
            //如果更改成功 更新所有联系人的列表.
            ShowContacts_Fragment.getContactAdapter().add(contact);
            //返回上级界面
            finish();
        }
    }

    //新增联系人的方法
    private void addContact(MyContacts contact) {
        //初始化DBHelper
        DBHelper helper = new DBHelper(this);
        int id = helper.insertID(contact);
        contact.setID(id);
        //如果id大于0则表示新增成功
        Toast.makeText(AddContactActivity.this, id > 0 ? "增加成功" : "增加失败", Toast.LENGTH_SHORT).show();
        if (id > 0) {
            //如果新增成功 更新所有联系人的列表.
            ShowContacts_Fragment.getContactAdapter().add(contact);
            //返回上级界面
            finish();
        }
    }

    //未实现
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_change) {

            new android.app.AlertDialog.Builder(this)
                    .setTitle("更改头像")
                    .setItems(new String[]{"拍照", "从相册中选择"}
                            , new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0: {
                                            String status = Environment.getExternalStorageState();
                                            if (status.equals(Environment.MEDIA_MOUNTED)) {//判断是否有SD卡
                                                takePhoto();
                                            } else {
                                                Toast.makeText(AddContactActivity.this, "未检测到SD卡", Toast.LENGTH_SHORT).show();
                                            }
                                            break;

                                        }
                                        case 1:
                                            pickPhotoFromGallery();// 从相册中去获取
                                            break;
                                    }
                                }
                            }).show();

        }
    }

    //以下 未实现
    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 1);
    }

    private void pickPhotoFromGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 80);
        intent.putExtra("outputY", 80);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && RESULT_OK == resultCode) {
            if (data.hasExtra("data")) {
                b = data.getParcelableExtra("data");
                Toast.makeText(this, "Image saved to:\n" + data.getData(),
                        Toast.LENGTH_LONG).show();
                Bitmap bitmap = ImageLoad.compressBitmap(data.getData().toString(), iv_setHead.getWidth(), iv_setHead.getHeight());
                iv_setHead.setImageBitmap(bitmap);
            }
        } else if (requestCode == 2 && RESULT_OK == resultCode) {
            Bitmap b2 = data.getParcelableExtra("data");
            iv_setHead.setImageBitmap(b2);
        }
    }
}
