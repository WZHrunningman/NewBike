package com.example.newbike.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.INaviInfoCallback;
import com.amap.api.navi.model.AMapNaviLocation;
import com.example.newbike.R;
import com.example.newbike.activity.IndexActivity;
import com.example.newbike.model.Bike;
import com.example.newbike.model.Money;
import com.example.newbike.model.User;
import com.example.newbike.model.trip;
import com.example.newbike.util.ToastUtil;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnInputConfirmListener;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

import static android.app.Activity.RESULT_OK;
import static cn.bmob.v3.Bmob.getApplicationContext;

public class MainFragmentOne extends Fragment {

    @BindView(R.id.map)
    MapView map;
    Unbinder unbinder;
    @BindView(R.id.tv_sweep_code)
    TextView tvSweepCode;
    @BindView(R.id.iv_navigation)
    ImageView ivNavigation;
    private Context context;
    private AMap aMap;
    private MyLocationStyle myLocationStyle;
    private UiSettings mUiSettings;//定义一个UiSettings对象
    private int REQUEST_CODE = 0001;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private static final String TAG = "11111111";
    private String WAIT_WRITE = "待填写";
    private List<Integer> doubleList;
    private int money;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 200:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    ToastUtil.show("未开启定位权限，请手动到设置打开");
                }
                break;
            default:
                break;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_one, container, false);
        unbinder = ButterKnife.bind(this, view);
        map.onCreate(savedInstanceState);
        initMap();
        initView();
        return view;
    }

    private void initView() {
        tvSweepCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = BmobUser.getCurrentUser(User.class);
                if (!BmobUser.isLogin()) {
                    ToastUtil.show("您还未登录，请前去登录");
                    return;
                } else {
                    if (!user.getIspayment()) {
                        ToastUtil.show("您还未缴纳押金，无法用车");
                        return;
                    }
                    money = 0;
                    doubleList = new ArrayList<>();
                    BmobQuery<Money> query = new BmobQuery<>();
                    query.addWhereEqualTo("username", user.getUsername());
                    query.findObjects(new FindListener<Money>() {
                        @Override
                        public void done(List<Money> list, BmobException e) {
                            if (e == null) {
                                for (int i = 0; i < list.size(); i++) {
                                    doubleList.add(list.get(i).getMoney());
                                    Log.d(TAG, "done: do" + doubleList.get(i));
                                }
                                for (Integer d : doubleList) {
                                    money += d;
                                }
                                if (money <= 0) {
                                    ToastUtil.show("余额不足");
                                    return;
                                } else {
                                    centreDialog();
                                }
                            } else {
                                ToastUtil.show("" + e);
                            }
                        }
                    });
                }

            }
        });
        ivNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AmapNaviPage.getInstance().showRouteActivity(context, new AmapNaviParams(null), new INaviInfoCallback() {
                    @Override
                    public void onInitNaviFailure() {

                    }

                    @Override
                    public void onGetNavigationText(String s) {

                    }

                    @Override
                    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

                    }

                    @Override
                    public void onArriveDestination(boolean b) {

                    }

                    @Override
                    public void onStartNavi(int i) {

                    }

                    @Override
                    public void onCalculateRouteSuccess(int[] ints) {

                    }

                    @Override
                    public void onCalculateRouteFailure(int i) {

                    }

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

    private void centreDialog() {
        new XPopup.Builder(context)
                .asCenterList("请选择：", new String[]{"手动输入号码", "扫码解锁"}, new OnSelectListener() {
                    @Override
                    public void onSelect(int position, String text) {
                        switch (position) {
                            case 0:
                                new XPopup.Builder(context)
                                        .asInputConfirm("提示", "请输入车牌号", new OnInputConfirmListener() {
                                            @Override
                                            public void onConfirm(String text) {
                                                BmobQuery<Bike> query = new BmobQuery<>();
                                                query.addWhereEqualTo("number", text);
                                                query.findObjects(new FindListener<Bike>() {
                                                    @Override
                                                    public void done(List<Bike> list, BmobException e) {
                                                        if (e == null) {
                                                            for (Bike b : list) {
                                                                if (b.getNumber().equals(text)) {
                                                                    startLocation(text);
                                                                    return;
                                                                }
                                                            }
                                                            ToastUtil.show("请输入正确的车牌号");
                                                        } else {
                                                            Log.d(TAG, "done: ========");
                                                        }
                                                    }
                                                });
                                            }
                                        })
                                        .show();
                                break;
                            case 1:
                                Intent intent = new Intent(context, CaptureActivity.class);
                                startActivityForResult(intent, REQUEST_CODE);
                                break;
                        }
                    }
                })
                .show();
    }

    private void initMap() {
        if (aMap == null) {
            aMap = map.getMap();
        }
        if (myLocationStyle == null) {
            myLocationStyle = new MyLocationStyle();
        }
        myLocationStyle.interval(3000);//3秒定位一次
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setMyLocationEnabled(true);
        aMap.moveCamera(CameraUpdateFactory.zoomTo(18));
        mUiSettings = aMap.getUiSettings();
        mUiSettings.setCompassEnabled(true);//指南针
        mUiSettings.setMyLocationButtonEnabled(true);
        LatLng latLng = new LatLng(23.1373810000, 113.2380560000);
        final Marker marker = aMap.addMarker(new MarkerOptions().position(latLng).title("北京").snippet("DefaultMarker"));
        LatLng latLng1 = new LatLng(23.1559200000,113.0308500000);
        final Marker marker1 = aMap.addMarker(new MarkerOptions().position(latLng1).title("车牌号").snippet("111"));
        LatLng latLng2 = new LatLng(23.1365320624,113.0253481865);
        final Marker marker2 = aMap.addMarker(new MarkerOptions().position(latLng2).title("车牌号").snippet("自定义"));
        LatLng latLng3 = new LatLng(23.1384263072,113.0253696442);
        final Marker marker3 = aMap.addMarker(new MarkerOptions().position(latLng3).title("车牌号").snippet("自定义"));
        LatLng latLng4 = new LatLng(23.1390774477,113.0265712738);
        final Marker marker4 = aMap.addMarker(new MarkerOptions().position(latLng4).title("车牌号").snippet("自定义"));
        LatLng latLng5 = new LatLng(23.1391563736,113.0249190331);
        final Marker marker5 = aMap.addMarker(new MarkerOptions().position(latLng5).title("车牌号").snippet("自定义"));
        LatLng latLng6 = new LatLng(23.1381303332,113.0239534378);
        final Marker marker6 = aMap.addMarker(new MarkerOptions().position(latLng6).title("车牌号").snippet("自定义"));
        LatLng latLng7 = new LatLng(23.1367096490,113.0231165886);
        final Marker marker7 = aMap.addMarker(new MarkerOptions().position(latLng7 ).title("车牌号").snippet("自定义"));
        List<MarkerOptions> list = new ArrayList<>();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                String content = data.getStringExtra(Constant.CODED_CONTENT);
                BmobQuery<Bike> query = new BmobQuery<>();
                query.addWhereEqualTo("number", content);
                query.findObjects(new FindListener<Bike>() {
                    @Override
                    public void done(List<Bike> list, BmobException e) {
                        if (e == null) {
                            for (Bike b : list) {
                                if (b.getNumber().equals(content)) {
                                    startLocation(content);
                                    return;
                                }
                            }
                            ToastUtil.show("请输入正确的车牌号");
                        } else {
                            Log.d(TAG, "done: ========");
                        }
                    }
                });
                Log.d("11111", "onActivityResult: " + content);
            }
        }
    }

    private void startLocation(String bikeNum) {
        mLocationClient = new AMapLocationClient(getApplicationContext());
        mLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation.getErrorCode() == 0) {
                    //定位成功回调信息，设置相关消息
                    Log.i(TAG, "纬度 ----------------" + aMapLocation.getLatitude());//获取纬度
                    Log.i(TAG, "经度-----------------" + aMapLocation.getLongitude());//获取经度
                    Log.i(TAG, "地址-----------------" + aMapLocation.getAddress());//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息
                    trip mTrip = new trip();
                    mTrip.setBike_num(bikeNum);
                    mTrip.setStart_address(aMapLocation.getAddress());
                    mTrip.setStart_longitude(String.valueOf(aMapLocation.getLongitude()));
                    mTrip.setStart_latitude(String.valueOf(aMapLocation.getLatitude()));
                    mTrip.setEnd_address(WAIT_WRITE);
                    mTrip.setEnd_latitude(WAIT_WRITE);
                    mTrip.setEnd_longitude(WAIT_WRITE);
                    mTrip.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            Intent intent = new Intent(context, IndexActivity.class);
                            Bundle bundle1 = new Bundle();
                            bundle1.putString("car_num", bikeNum);
                            bundle1.putString("objectId", s);
                            intent.putExtras(bundle1);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
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

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        map.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        map.onDestroy();
        unbinder.unbind();
    }
}
