package com.example.a8.newcontacts.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.a8.newcontacts.ContactActivity;
import com.example.a8.newcontacts.R;
import com.example.a8.newcontacts.adapter.FavrAdapter;
import com.example.a8.newcontacts.bean.MyContacts;

import java.util.List;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavrContacts_Fragment extends Fragment {


    private FragmentActivity activity;
    private GridView gv_favr;
    private PtrClassicFrameLayout ptr_favr;
    private static FavrAdapter favrAdapter;

    public FavrContacts_Fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = getActivity();
        return inflater.inflate(R.layout.fragment_favr_contacts_, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView() {
        gv_favr = (GridView) activity.findViewById(R.id.gv_favr);
        ptr_favr = (PtrClassicFrameLayout) activity.findViewById(R.id.ptr_favr);
        favrAdapter = new FavrAdapter(getContext());
        gv_favr.setAdapter(favrAdapter);
        favrAdapter.initFavr();
        setListener();
    }

    private void setListener() {
        ptr_favr.setPtrHandler(new PtrDefaultHandler() {

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                favrAdapter.initFavr();
            }
        });

        gv_favr.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                int which = favrAdapter.getItem(position).getId();
                List<MyContacts> list = ShowContacts_Fragment.getContactAdapter().getList();
                Intent intent = new Intent(getActivity(), ContactActivity.class);
                for (MyContacts c : list) {
                    if (c.getID() == which) {
                        intent.putExtra("contact", c);
                        break;
                    }
                }
                startActivity(intent);

            }
        });

    }

    public static FavrAdapter getFavrAdapter() {
        return favrAdapter;
    }

    public static void refreshAdapter() {
        favrAdapter.change();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ContactActivity.DELETE_RESULT) {
            int id = data.getIntExtra("delete", 0);
            favrAdapter.delete(id);
        }
    }
}
