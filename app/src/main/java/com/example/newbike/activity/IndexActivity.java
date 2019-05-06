package com.example.newbike.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.INaviInfoCallback;
import com.amap.api.navi.model.AMapNaviLocation;
import com.example.newbike.R;
import com.example.newbike.model.Money;
import com.example.newbike.model.User;
import com.example.newbike.model.trip;
import com.example.newbike.util.ToastUtil;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.trello.rxlifecycle2.components.RxActivity;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class IndexActivity extends RxActivity {

    @BindView(R.id.tv_car_num)
    TextView tvCarNum;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.btn_return_car)
    Button btnReturnCar;
    private static final String TAG = "IndexActivity";
    @BindView(R.id.btn_navigation)
    Button btnNavigation;
    @BindView(R.id.tv_minute)
    TextView tvMinute;
    @BindView(R.id.tv_sconed)
    TextView tvSconed;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private String objectId;
    private String longitude;
    private String latitude;
    private String address;
    private int second = 1;
    private int minute = 0;
    private int time = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        User user = BmobUser.getCurrentUser(User.class);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String car_num = bundle.getString("car_num");
        objectId = bundle.getString("objectId");
        tvCarNum.setText(car_num);
        tvDate.setText(initDate());
        Observable.interval(1, 1, TimeUnit.SECONDS)
                .compose(this.bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onNext(Long aLong) {
                        if (aLong % 60 != 0) {
                            if (second < 10) {
                                tvSconed.setText("0" + second);
                            } else {
                                tvSconed.setText(second+"");
                            }
                            second++;
                        } else {
                            if (aLong == 0)return;
                            tvSconed.setText("59");
                            second = 0;
                            minute++;
                            if (minute < 10) {
                                tvMinute.setText("0" + minute);
                            } else {
                                tvMinute.setText(minute+"");
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: ");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: ");
                    }
                });

        btnReturnCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (objectId != null) {
                    startLocationAddress();
                    new XPopup.Builder(IndexActivity.this)
                            .asConfirm("提示", "确定要还车结算吗？", new OnConfirmListener() {
                                @Override
                                public void onConfirm() {
                                    mLocationClient = new AMapLocationClient(getApplicationContext());
                                    mLocationClient.setLocationListener(new AMapLocationListener() {
                                        @Override
                                        public void onLocationChanged(AMapLocation aMapLocation) {
                                            if (aMapLocation.getErrorCode() == 0) {
                                                //定位成功回调信息，设置相关消息
                                                Log.i(TAG, "纬度 ----------------" + aMapLocation.getLatitude());//获取纬度
                                                Log.i(TAG, "经度-----------------" + aMapLocation.getLongitude());//获取经度
                                                Log.i(TAG, "地址-----------------" + aMapLocation.getAddress());//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息
                                                /*int payment = Integer.parseInt(Integer.parseInt(Integer.parseInt(tvTime.getText().toString().trim()) * 5
                                                        + tvMinute.getText().toString().trim()) / 60 * 5
                                                        + tvSconed.getText().toString().trim()) / 3600 * 5;
                                                    if (payment < 1) {
                                                    payment = 1;
                                                }*/
                                                int payment = 0;
                                                int time = Integer.parseInt(Integer.parseInt(Integer.parseInt(tvTime.getText().
                                                toString().trim()) * 60
                                                + tvMinute.getText().toString().trim())
                                                + tvSconed.getText().toString().trim()) / 60;
                                                if (time <= 30) {
                                                    payment = 1;
                                                }else if (30<time & time<=60){
                                                    payment = 2;
                                                }else if (60<time & time<=90){
                                                    payment = 3;
                                                }else if (90<time & time<=120) {
                                                    payment = 4;
                                                }
                                                DecimalFormat df = new DecimalFormat("#.00");
                                                Money money = new Money();
                                                money.setUsername(user.getUsername());
                                                money.setMoney(-payment);
                                                money.save(new SaveListener<String>() {
                                                    @Override
                                                    public void done(String s, BmobException e) {
                                                    }
                                                });
                                                trip mTrip = new trip();
                                                mTrip.setEnd_address(address);
                                                mTrip.setEnd_longitude(longitude);
                                                mTrip.setEnd_latitude(latitude);
                                                mTrip.setUsername(user.getUsername());
                                                mTrip.setPayment(payment);
                                                mTrip.setTime(tvTime.getText().toString()+" : "
                                                +tvMinute.getText().toString()+" : "
                                                +tvSconed.getText().toString());
                                                mTrip.update(objectId, new UpdateListener() {
                                                    @Override
                                                    public void done(BmobException e) {
                                                        if (e == null) {
                                                            ToastUtil.show("还车成功");
                                                            startActivity(new Intent(IndexActivity.this, MainActivity.class));
                                                            IndexActivity.this.finish();
                                                        } else {
                                                            ToastUtil.show("操作失败" + e);
                                                        }
                                                    }
                                                });
                                            } else {
                                                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                                                Log.e("AmapError", "startLocationAddress Error, ErrCode:"
                                                        + aMapLocation.getErrorCode() + ", errInfo:"
                                                        + aMapLocation.getErrorInfo());
                                            }
                                        }
                                    });
                                    mLocationOption = new AMapLocationClientOption();
                                    //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
                                    mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
                                    //设置是否返回地址信息（默认返回地址信息）
                                    mLocationOption.setNeedAddress(true);
                                    //获取一次定位结果：
                                    //该方法默认为false。
                                    mLocationOption.setOnceLocation(true);
                                    //设置是否允许模拟位置,默认为false，不允许模拟位置
                                    mLocationOption.setMockEnable(false);
                                    //给定位客户端对象设置定位参数
                                    mLocationClient.setLocationOption(mLocationOption);
                                    //启动定位
                                    mLocationClient.startLocation();
                                }
                            })
                            .show();
                }
            }
        });
        btnNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AmapNaviPage.getInstance().showRouteActivity(IndexActivity.this, new AmapNaviParams(null), new INaviInfoCallback() {
                    //导航初始化失败时的回调函数
                    @Override
                    public void onInitNaviFailure() {

                    }

                    //导航播报信息回调函数
                    @Override
                    public void onGetNavigationText(String s) {

                    }

                    //当GPS位置有更新时的回调函数。
                    @Override
                    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

                    }

                    //到达目的地后回调函数。
                    @Override
                    public void onArriveDestination(boolean b) {

                    }

                    //启动导航后的回调函数
                    @Override
                    public void onStartNavi(int i) {

                    }

                    //算路成功回调 路线id数组
                    @Override
                    public void onCalculateRouteSuccess(int[] ints) {

                    }

                    //步行或者驾车路径规划失败后的回调函数
                    @Override
                    public void onCalculateRouteFailure(int i) {

                    }

                    //停止语音回调，收到此回调后用户可以停止播放语音
                    @Override
                    public void onStopSpeaking() {

                    }

                    @Override
                    public void onReCalculateRoute(int i) {

                    }

                    @Override
                    public void onExitPage(int i) {

                    }

                    @Override
                    public void onStrategyChanged(int i) {

                    }

                    @Override
                    public View getCustomNaviBottomView() {
                        return null;
                    }

                    @Override
                    public View getCustomNaviView() {
                        return null;
                    }

                    @Override
                    public void onArrivedWayPoint(int i) {

                    }
                });
            }
        });
    }

    public void startLocationAddress() {
        mLocationClient = new AMapLocationClient(getApplicationContext());
        mLocationClient.setLocationListener(mLocationListener);
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //获取一次定位结果：
        //该方法默认为false。
        mLocationOption.setOnceLocation(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0) {
                    //定位成功回调信息，设置相关消息
                    Log.i(TAG, "纬度 ----------------" + amapLocation.getLatitude());//获取纬度
                    Log.i(TAG, "经度-----------------" + amapLocation.getLongitude());//获取经度
                    Log.i(TAG, "地址-----------------" + amapLocation.getAddress());//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息
                    longitude = String.valueOf(amapLocation.getLongitude());
                    latitude = String.valueOf(amapLocation.getLatitude());
                    address = amapLocation.getAddress();
                } else {
                    //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError", "startLocationAddress Error, ErrCode:"
                            + amapLocation.getErrorCode() + ", errInfo:"
                            + amapLocation.getErrorInfo());
                }
            }
        }
    };

    private String initDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return year + "-" + month + "-" + day;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return false;
    }
}
