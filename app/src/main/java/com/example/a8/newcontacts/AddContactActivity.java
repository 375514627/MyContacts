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
import com.example.a8.newcontacts.utils.DBhelper;
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
        initToolbar();
        initView();
        setView();
    }

    private void initView() {
        iv_setHead = (ImageView) findViewById(R.id.iv_person_120dp);
        et_name = (EditText) findViewById(R.id.et_name);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_email = (EditText) findViewById(R.id.et_email);
        et_place = (EditText) findViewById(R.id.et_place);
        et_business = (EditText) findViewById(R.id.et_business);
        setListener();
    }

    private void setView() {
        Intent intent = getIntent();
        MyContacts contact = (MyContacts) intent.getSerializableExtra("contact");
        if (contact != null) {
            et_name.setText(contact.getName());
            et_phone.setText(contact.getPhoneNum());
            et_email.setText(contact.getEmail());
            et_place.setText(contact.getAddress());
            et_business.setText(contact.getBusiness());
            iv_setHead.setImageBitmap(contact.getBitmap());
        }

    }

    private void setListener() {
        findViewById(R.id.btn_change).setOnClickListener(this);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("新增联系人");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.add_done:
                DBhelper dBhelper = new DBhelper(this);
                String name = et_name.getText().toString();
                String business = et_business.getText().toString();
                String email = et_email.getText().toString();
                String phone = et_phone.getText().toString();
                String place = et_place.getText().toString();
                MyContacts contacts = new MyContacts(0, name, email, phone, b, null, place, business, 0);
                long insert = dBhelper.insert(contacts);
                Toast.makeText(AddContactActivity.this, insert > 0 ? "增加成功" : "增加失败", Toast.LENGTH_SHORT).show();
                if (insert > 0) {
                    ShowContacts_Fragment.getContactAdapter().add(contacts);
                    finish();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

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
