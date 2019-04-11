package com.example.newbike.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.newbike.R;
import com.example.newbike.adapter.TripAdapter;
import com.example.newbike.model.User;
import com.example.newbike.model.trip;
import com.example.newbike.util.ToastUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class TripActivity extends AppCompatActivity {

    @BindView(R.id.ll_back)
    LinearLayout llBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tv_no_trip)
    TextView tvNoTrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        User user = BmobUser.getCurrentUser(User.class);
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvTitle.setText("我的行程");
        BmobQuery<trip> query = new BmobQuery<>();
        query.addWhereEqualTo("username", user.getUsername());
        query.order("-id");
        query.findObjects(new FindListener<trip>() {
            @Override
            public void done(List<trip> list, BmobException e) {
                if (e == null) {
                    if (list.size() == 0) {
                        recyclerView.setVisibility(View.GONE);
                        tvNoTrip.setVisibility(View.VISIBLE);
                        return;
                    }
                    LinearLayoutManager manager = new LinearLayoutManager(TripActivity.this, LinearLayoutManager.VERTICAL, false);
                    recyclerView.setLayoutManager(manager);
                    TripAdapter adapter = new TripAdapter(TripActivity.this, list);
                    recyclerView.setAdapter(adapter);
                } else {
                    ToastUtil.show("数据请求失败" + e);
                }
            }
        });


    }
}
