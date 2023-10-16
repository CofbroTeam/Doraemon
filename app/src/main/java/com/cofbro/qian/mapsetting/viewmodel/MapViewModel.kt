package com.cofbro.qian.mapsetting.viewmodel

import android.app.Dialog
import androidx.lifecycle.viewModelScope
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.maps2d.model.LatLng
import com.amap.api.maps2d.model.Marker
import com.amap.api.services.poisearch.PoiResultV2
import com.amap.api.services.poisearch.PoiSearchV2
import com.cofbro.hymvvmutils.base.BaseViewModel
import com.cofbro.hymvvmutils.base.ResponseMutableLiveData
import com.cofbro.qian.mapsetting.repository.MapRepository
import com.cofbro.qian.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Response

class MapViewModel : BaseViewModel<MapRepository>() {
    val preSignLiveData = ResponseMutableLiveData<Response>()
    val signLiveData = ResponseMutableLiveData<Response>()

    var progressDialog: Dialog? = null // 搜索时进度条
    var poiResult: PoiResultV2? = null // poi返回的结果
    var currentPage = 1
    var query: PoiSearchV2.Query? = null // Poi查询条件类
    var poiSearch: PoiSearchV2? = null // POI搜索
    var mPoiMarker: Marker? = null
    var currentTipPoint: LatLng = LatLng(0.0, 0.0)//获取当前的经纬度
    var Tip_address: String? = null
    var Tip_name: String? = null
    var Tip_City: String? = null
    // 声明AMapLocationClient类对象
    var  mLocationClient: AMapLocationClient? = null;
    var mLocationOption: AMapLocationClientOption? = null
    var default_Lating:LatLng? = null
    fun sign(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.request(signLiveData) {
                val request = NetworkUtils.buildClientRequest(url)
                NetworkUtils.request(request)
            }
        }
    }

    fun preSign(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.request(preSignLiveData, false) {
                val request = NetworkUtils.buildClientRequest(url)
                NetworkUtils.request(request)
            }
        }
    }
}