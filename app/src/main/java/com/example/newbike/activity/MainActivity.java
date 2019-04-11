package com.example.newbike.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.Toast;

import com.example.newbike.R;
import com.example.newbike.fragment.MainFragmentOne;
import com.example.newbike.fragment.MainFragmentTwo;
import com.example.newbike.view.ImageTabEntity;
import com.example.newbike.view.NoSrcollViewPage;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.com_tablayout)
    CommonTabLayout comTablayout;
    @BindView(R.id.viewpager)
    NoSrcollViewPage viewpager;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private String[] title = {"用车", "我的"};
    private int[] unSelectIcon = {R.drawable.map, R.drawable.my};
    private int[] selectIcon = {R.drawable.map_pre, R.drawable.my_pre};
    private int REQUEST_CODE = 0001;
    private long exitTime = 0;

    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= 23) {
            String[] strings = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION
                    , Manifest.permission.CAMERA, Manifest.permission.CALL_PHONE
                    , Manifest.permission.READ_LOGS, Manifest.permission.READ_PHONE_STATE
                    , Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.SET_DEBUG_APP
                    , Manifest.permission.SYSTEM_ALERT_WINDOW, Manifest.permission
                    .GET_ACCOUNTS, Manifest.permission.WRITE_APN_SETTINGS};
            ActivityCompat.requestPermissions(this, strings, REQUEST_CODE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {//未开启定位权限
            //开启定位权限,200是标识码
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        } else {
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        ArrayList<CustomTabEntity> tabs = new ArrayList<>();
        ImageTabEntity imageTabEntity0 = new ImageTabEntity(title[0], selectIcon[0], unSelectIcon[0]);
        ImageTabEntity imageTabEntity1 = new ImageTabEntity(title[1], selectIcon[1], unSelectIcon[1]);
        tabs.add(imageTabEntity0);
        tabs.add(imageTabEntity1);
        comTablayout.setTabData(tabs);
        fragments.add(new MainFragmentOne());
        fragments.add(new MainFragmentTwo());
        viewpager.setAdapter(new myViewPagerAdapter(getSupportFragmentManager()));

        comTablayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                viewpager.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {

            }
        });

        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                comTablayout.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    class myViewPagerAdapter extends FragmentPagerAdapter {

        public myViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                //弹出提示，可以有多种方式
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);

    }

}
