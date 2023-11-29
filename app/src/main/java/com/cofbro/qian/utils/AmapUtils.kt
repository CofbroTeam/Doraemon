package com.cofbro.qian.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.location.LocationManager
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.maps2d.model.LatLng
import com.cofbro.qian.mapsetting.util.ToastUtil
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.tasks.Task
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.regex.Matcher
import java.util.regex.Pattern


object AmapUtils {
    //要申请的权限
    private val mPermissions =
        Manifest.permission.ACCESS_FINE_LOCATION

    private  final var REQUEST_CODE_LOCATION_SETTINGS = 2

    /**
     * 判断是否缺少权限
     */
    private fun lacksPermission(mContexts: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(mContexts, permission) ==
                PackageManager.PERMISSION_DENIED
    }
    /*
    请求精确定位
     */
    fun checkLocationPermission(activity:AppCompatActivity){
        if (lacksPermission(activity.applicationContext, mPermissions)){
            /*
            缺少精确定位，提醒开启精确定位
             */
            ToastUtil.show(activity.applicationContext,"请选择精确位置")
            getLocationPermission(activity)
        }else{
            //权限开启

        }
    }
    private fun getLocationPermission(activity:AppCompatActivity){
        val locationPermissionRequest = activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    // Precise location access granted.
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                   ToastUtil.show(activity.applicationContext,"请选择精确位置")
                } else -> {
                // No location access granted.
            }
            }
        }
        locationPermissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION))
    }
    /*
    获取自己的定位信息
     */
     fun getCurrentLocationLatLng(applicationContext:Context, onSuccess:(LatLng,String)->Unit = { latLng: LatLng, s: String -> }, onError:(String)->Unit = {}) {
        AMapLocationClient.updatePrivacyAgree(applicationContext, true)
        AMapLocationClient.updatePrivacyShow(applicationContext, true, true)
        //初始化定位
       val mLocationClient = AMapLocationClient(applicationContext)
        var mLocationOption: AMapLocationClientOption? = null
        //设置定位回调监听
        mLocationClient.setLocationListener { amapLocation ->
            if (amapLocation != null) {
                if (amapLocation.errorCode == 0) {
                    val address = urlEncodeChinese(amapLocation.country + " " + amapLocation.address)
                    onSuccess(LatLng(amapLocation.latitude,amapLocation.longitude),address)
                } else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    onError("location Error, ErrCode:" + amapLocation.errorCode + ", errInfo:" + amapLocation.errorInfo)
                }
            }
        }
        //初始化AMapLocationClientOption对象
        mLocationOption = AMapLocationClientOption()

        mLocationOption.locationMode =
            AMapLocationClientOption.AMapLocationMode.Hight_Accuracy

        // 设置为单次定位  : 默认为false
        mLocationOption.isOnceLocation = false
        mLocationOption.httpTimeOut = 20000
        mLocationOption.isLocationCacheEnable = false
        mLocationClient.setLocationOption(mLocationOption)
        //启动定位
        mLocationClient.startLocation()
    }
    private fun urlEncodeChinese(urlString: String): String {
        var url = urlString
        try {
            val matcher: Matcher = Pattern.compile("[\\u4e00-\\u9fa5]").matcher(url)
            var tmp = ""
            while (matcher.find()) {
                tmp = matcher.group()
                url = url.replace(tmp.toRegex(), URLEncoder.encode(tmp, "UTF-8"))
            }
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return url.replace(" ", "%20")
    }

    /**直接跳转至位置信息设置界面 */
    fun openLocation(context: Context) {
        if(getGpsStatus(context)){

        }else{
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            context.startActivity(intent)
        }

    }
    private  fun getGpsStatus(ctx: Context): Boolean {
        //从系统服务中获取定位管理器
        val lm = ctx.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
    fun checkLocationopen(activity: Activity){
        val locationRequest: LocationRequest = LocationRequest.create()
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(activity)

        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener { locationSettingsResponse ->


        }.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                try {
                    // 位置设置未满足应用要求
                    // 弹出对话框提示用户打开位置开关
                    e.startResolutionForResult(activity, REQUEST_CODE_LOCATION_SETTINGS)

                } catch (sendEx: SendIntentException) {
                    // 忽略异常
                }
            }
        }
    }


}