package com.example.a8.newcontacts.fragment;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a8.newcontacts.AddContactActivity;
import com.example.a8.newcontacts.ContactActivity;
import com.example.a8.newcontacts.R;
import com.example.a8.newcontacts.adapter.ContactAdapter;
import com.example.a8.newcontacts.adapter.FavrAdapter;
import com.example.a8.newcontacts.bean.FavrContacts;
import com.example.a8.newcontacts.bean.MyContacts;
import com.example.a8.newcontacts.service.ContactLoadService;
import com.example.a8.newcontacts.utils.DBHelper;
import com.example.a8.newcontacts.view.QuickBar;

import java.util.List;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowContacts_Fragment extends Fragment {

    private QuickBar qucikBar;
    private TextView float_letter;
    private static ContentResolver resolver;
    private PtrClassicFrameLayout myPtrCFL;
    private static ContactLoadService loadService;

    public ShowContacts_Fragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_show_contacts_, container, false);
    }

    private ListView lv_contact;
    private static ContactAdapter mAdapter;
    private DBHelper dbHelper;

    public static ContactAdapter getContactAdapter() {
        return mAdapter;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        resolver = getActivity().getContentResolver();
        bindService();
        initView();
    }

    private Handler myHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            myPtrCFL.refreshComplete();
            mAdapter.notifyDataSetChanged();
        }

    };

    private void bindService() {
        getActivity().bindService(new Intent(getActivity(), ContactLoadService.class),
                new ServiceConnection() {

                    @Override
                    public void onServiceConnected(ComponentName name, IBinder service) {
                        ContactLoadService.MyBinder binder = (ContactLoadService.MyBinder) service;
                        loadService = binder.getService();
                        binder.setContactLoadCallBack(new ContactLoadService.ContactLoadCallBack() {

                            @Override
                            public void getList(List<MyContacts> list) {
                                mAdapter.setList(list);
                                myHandler.sendEmptyMessage(1);
                            }
                        });
                        loadService.loadContacts();
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName name) {

                    }
                }, Context.BIND_AUTO_CREATE);
    }

    private void initView() {
        lv_contact = (ListView) getView().findViewById(R.id.lv_contact);
        float_letter = (TextView) getView().findViewById(R.id.float_letter);
        qucikBar = (QuickBar) getView().findViewById(R.id.quickBar);
        myPtrCFL = (PtrClassicFrameLayout) getView().findViewById(R.id.myPtrCFL);
        addListener();
        mAdapter = new ContactAdapter(getContext());
        lv_contact.setAdapter(mAdapter);
    }

    private void addListener() {

        myPtrCFL.setPtrHandler(new PtrDefaultHandler() {

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                frame.post(new Runnable() {

                    @Override
                    public void run() {
                        //启动服务在后台更新,结束后回调传回引用
                        loadService.loadContacts();
                    }

                });
            }
        });

        lv_contact.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyContacts contact = mAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), ContactActivity.class);
                intent.putExtra("contact", contact);
                startActivity(intent);
                System.out.println("------" + contact);
            }
        });

        lv_contact.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, final long id) {
                final String[] items = new String[]{"呼叫", "发送信息", "编辑", "删除", "添加收藏"};
                final List<MyContacts> list = mAdapter.getList();
                for (FavrContacts fc : FavrAdapter.getList()) {
                    if (fc.getId() == list.get(position).getID()) {
                        System.out.println("-----" + fc.getName() + "  " + list.get(position).getName());
                        items[4] = "取消收藏";
                        break;
                    }
                }
                FavrAdapter.getList();
                new AlertDialog.Builder(getActivity())
                        .setTitle(list.get(position).getName())
                        .setItems(items, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        call(position);
                                        break;
                                    case 1:
                                        sendMsg(position);
                                        break;
                                    case 2:
                                        Intent intent = new Intent(getActivity(), AddContactActivity.class);
//                                        intent.putExtra("contact", list.get(position));
                                        startActivity(intent);
                                        break;
                                    case 3:
                                        delete(position);
                                        break;
                                    case 4:
                                        System.out.println("-----" + items[which]);
                                        boolean how = false;
                                        if (items[4].equals("添加收藏")) {
                                            how = true;
                                        }
                                        starred(list.get(position).getID(), how);

                                        break;
                                }
                            }
                        }).show();
                return true;
            }
        });

        lv_contact.setOnScrollChangeListener(new View.OnScrollChangeListener() {

            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

            }
        });

        qucikBar.setOnTouchLetterChangeListener(new QuickBar.OnTouchLetterChangeListener() {

            @Override
            public void onPressLetter(String letter) {
                float_letter.setText(letter);
                ObjectAnimator animator = ObjectAnimator.ofFloat(float_letter, "alpha", 0, 1);
                animator.setDuration(500);
                animator.start();
                movePos(letter);
            }

            @Override
            public void onMoveLetterChange(String letter) {
                float_letter.setText(letter);
                movePos(letter);
            }

            @Override
            public void onDetachedLetter() {
                ObjectAnimator animator = ObjectAnimator.ofFloat(float_letter, "alpha", float_letter.getAlpha(), 0);
                animator.setDuration(500);
                animator.start();
            }
        });
    }

    private void movePos(String letter) {
        int position = 0;
        List<MyContacts> list = mAdapter.getList();
        for (MyContacts c : list) {
            if (c.getSort_key().equals(letter)) {
                position = list.indexOf(c);
                break;
            } else if (list.get(list.size() - 1).equals(c)) {
                return;
            }
        }
        lv_contact.setSelection(position);
    }

    private void starred(int id, boolean how) {
        ContentValues values = new ContentValues();
        values.put("starred", how ? "1" : "0");
        int update = resolver.update(ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id), values, null, null);
        FavrContacts_Fragment.getFavrAdapter().change();
        Toast.makeText(getActivity(), update > 0 ? how ? "收藏成功" : "取消成功" : how ? "收藏失败" : "取消失败", Toast.LENGTH_SHORT).show();
    }

    private void call(int position) {
        Uri uri = Uri.parse("tel:" + mAdapter.getList().get(position).getPhoneNum());
        Intent mIntent = new Intent(Intent.ACTION_CALL, uri);
        startActivity(mIntent);
    }

    private void sendMsg(int position) {
        Uri smsToUri = Uri.parse("smsto://" + mAdapter.getList().get(position).getPhoneNum());
        Intent mIntent = new Intent(Intent.ACTION_SENDTO, smsToUri);
        startActivity(mIntent);
        return;
    }

    private void delete(final int position) {
        dbHelper = new DBHelper(getActivity());
        new AlertDialog.Builder(getActivity())
                .setMessage("删除联系人")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        List<MyContacts> list = mAdapter.getList();
                        dbHelper.delete(mAdapter.getList().get(position).getID());
                        list.remove(position);
                        mAdapter.setList(list);
                        mAdapter.notifyDataSetInvalidated();
                    }
                })
                .setNegativeButton("取消", null).show();
    }

    public static void refreshAdapter() {
        loadService.loadContacts();
    }

}
