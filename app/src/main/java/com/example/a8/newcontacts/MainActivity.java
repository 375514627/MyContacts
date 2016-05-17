package com.example.a8.newcontacts;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.a8.newcontacts.fragment.FavrContacts_Fragment;
import com.example.a8.newcontacts.fragment.ShowContacts_Fragment;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddContactActivity.class);
                startActivity(intent);
            }
        });

        ViewPager vp_show = (ViewPager) findViewById(R.id.vp_show);
        vp_show.setAdapter(new MyFragmentPagerAdapter());

        TabLayout tl_tabs = (TabLayout) findViewById(R.id.tl_tabs);
        tl_tabs.setupWithViewPager(vp_show);
        vp_show.setCurrentItem(1);
    }

    private Fragment[] fragments = new Fragment[]{new FavrContacts_Fragment(), new ShowContacts_Fragment()};
    private String[] titles = {"收藏", "所有联系人"};

    class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        public MyFragmentPagerAdapter() {
            super(getSupportFragmentManager());
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return titles.length;
        }
    }

}
