package com.leben.merchant.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.jakewharton.rxbinding2.view.RxView;
import com.leben.base.ui.activity.BaseActivity;
import com.leben.base.util.LogUtils;
import com.leben.base.util.ToastUtils;
import com.leben.base.widget.titleBar.TitleBar;
import com.leben.common.model.bean.AddressEntity;
import com.leben.merchant.R;
import com.leben.merchant.constant.MerchantConstant;
import java.util.concurrent.TimeUnit;
import io.reactivex.android.schedulers.AndroidSchedulers;

@Route(path = MerchantConstant.Router.ADD_ADDRESS)
public class AddAddressActivity extends BaseActivity implements AMap.OnCameraChangeListener,
        GeocodeSearch.OnGeocodeSearchListener{

    // UI
    private MapView mMapView; // 高德的 MapView
    private TextView mTvPoiAddress;
    private EditText etDetail;
    private Button btnSave;

    // 高德核心对象
    private AMap aMap;
    private AMapLocationClient mLocationClient;
    private GeocodeSearch geocoderSearch;

    // 数据
    private boolean isFirstLocate = true;
    private double currentLat = 0.0;
    private double currentLng = 0.0;
    private String currentPoiAddress = "";
    private RegeocodeAddress address;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_merchant_add_address;
    }

    @Override
    public void onInit() {
        super.onInit();
        MapsInitializer.updatePrivacyShow(this,true,true);
        MapsInitializer.updatePrivacyAgree(this,true);
    }

    @Override
    public void initView() {
        TitleBar titleBar=findViewById(R.id.title_bar);
        titleBar.setTitle("店铺地址");
        mMapView = findViewById(R.id.map_view);
        // 高德地图必须在 onCreate 调用此方法，否则地图不显示
        mMapView.onCreate(null);

        mTvPoiAddress = findViewById(R.id.tv_poi_address);
        etDetail = findViewById(R.id.et_detail);
        btnSave = findViewById(R.id.btn_save);

        if (aMap == null) {
            aMap = mMapView.getMap();
        }

        // 初始化搜索模块 (用于坐标转地址)
        try {
            geocoderSearch = new GeocodeSearch(this);
            geocoderSearch.setOnGeocodeSearchListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("CheckResult")
    @Override
    public void initListener() {
        // 1. 监听地图移动 (用户拖拽地图停止时触发)
        aMap.setOnCameraChangeListener(this);

        RxView.clicks(btnSave)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .filter(unit -> {

                    String detail = etDetail.getText().toString().trim();
                    if (TextUtils.isEmpty(detail)) {
                        ToastUtils.show(this, "请输入门牌号");
                        return false;
                    }

                    if (currentLat == 0.0 || currentLng == 0.0) {
                        ToastUtils.show(this, "定位信息获取失败，请移动地图重新定位");
                        return false;
                    }
                    return true;
                })
                .subscribe(result -> {

                    String finalAddress = currentPoiAddress;

                    if (!TextUtils.isEmpty(address.getProvince())) {
                        finalAddress = finalAddress.replace(address.getProvince(), "");
                    }
                    if (!TextUtils.isEmpty(address.getCity())) {
                        finalAddress = finalAddress.replace(address.getCity(), "");
                    }
                    if (!TextUtils.isEmpty(address.getDistrict())) {
                        finalAddress = finalAddress.replace(address.getDistrict(), "");
                    }

                    // 3. 创建 Intent 回传数据
                    Intent intent = new Intent();
                    intent.putExtra("latitude", currentLat);
                    intent.putExtra("longitude", currentLng);
                    intent.putExtra("address", finalAddress); // 使用清理后的地址
                    intent.putExtra("detailAddress", etDetail.getText().toString().trim());

                    // 2. 设置结果并关闭页面
                    setResult(RESULT_OK, intent);
                    finish();

                }, throwable -> {
                    LogUtils.error("点击事件错误: " + throwable.getMessage());
                });
    }

    @Override
    public void initData() {
        try {
            mLocationClient = new AMapLocationClient(getApplicationContext());
            AMapLocationClientOption option = new AMapLocationClientOption();
            // 设置高精度定位模式
            option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            option.setOnceLocation(true); // 只定位一次
            mLocationClient.setLocationOption(option);

            mLocationClient.setLocationListener(location -> {
                if (location != null && location.getErrorCode() == 0) {
                    currentLat = location.getLatitude();
                    currentLng = location.getLongitude();

                    // 首次定位，把地图移过去，并放大
                    if (isFirstLocate) {
                        isFirstLocate = false;
                        LatLng latLng = new LatLng(currentLat, currentLng);
                        // 缩放级别 17
                        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f));

                        // 主动查一次地址
                        searchAddressByLatlng(currentLat, currentLng);
                    }
                } else {
                    LogUtils.error("定位失败: " + (location != null ? location.getErrorInfo() : "null"));
                    mTvPoiAddress.setText("定位失败，请检查权限");
                }
            });

            mLocationClient.startLocation();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        // 拖动结束，获取屏幕中心点坐标
        LatLng target = cameraPosition.target;
        currentLat = target.latitude;
        currentLng = target.longitude;

        mTvPoiAddress.setText("正在获取地址...");

        // 发起反查
        searchAddressByLatlng(currentLat, currentLng);
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        if (i == 1000) { // 1000 代表成功
            if (regeocodeResult != null && regeocodeResult.getRegeocodeAddress() != null) {
                address = regeocodeResult.getRegeocodeAddress();
                // 优先显示 POI 名称 (例如 "浦翠景庭")，如果没有则显示格式化地址
                String formatAddress = address.getFormatAddress();
                // 如果周围有POI，取第一个POI的名字会更像 "地点"
                if (!address.getPois().isEmpty()) {
                    currentPoiAddress = address.getPois().get(0).getTitle();
                } else {
                    currentPoiAddress = formatAddress;
                    // 去掉前面的省市区描述，让显示更简洁 (可选)
                    currentPoiAddress = currentPoiAddress.replace(address.getProvince(), "");
                }

                mTvPoiAddress.setText(currentPoiAddress);
            }
        } else {
            mTvPoiAddress.setText("未知位置");
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
        // 不需要实现，这是地址转坐标
    }

    /**
     * 根据经纬度查询地址文字 (逆地理编码)
     */
    private void searchAddressByLatlng(double lat, double lng) {
        LatLonPoint point = new LatLonPoint(lat, lng);
        // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        RegeocodeQuery query = new RegeocodeQuery(point, 200, GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(query);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mMapView != null) mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mMapView != null) mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mMapView != null) mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mLocationClient != null) mLocationClient.onDestroy();
        if(mMapView != null) mMapView.onDestroy();
    }

    @Override
    protected View getTitleBarView() {
        return findViewById(R.id.title_bar);
    }

    @Override
    protected int getStatusBarColor() {
        return com.leben.base.R.color.white;
    }
}
