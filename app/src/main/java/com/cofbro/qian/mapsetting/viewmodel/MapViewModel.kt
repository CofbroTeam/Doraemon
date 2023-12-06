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
import com.cofbro.qian.mapsetting.util.PreWeb
import com.cofbro.qian.utils.HtmlParser
import com.cofbro.qian.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Response
import com.cofbro.qian.utils.AmapUtils.BDLating
class MapViewModel : BaseViewModel<MapRepository>() {
    val preSignLiveData = ResponseMutableLiveData<Response>()
    val signLiveData = ResponseMutableLiveData<Response>()
    val signTogetherLiveData = ResponseMutableLiveData<Response>()
    val loginLiveData = ResponseMutableLiveData<Response>()
    val analysisLiveData = ResponseMutableLiveData<Response>()
    var progressDialog: Dialog? = null // 搜索时进度条
    var poiResult: PoiResultV2? = null // poi返回的结果
    var currentPage = 1
    var query: PoiSearchV2.Query? = null // Poi查询条件类
    var poiSearch: PoiSearchV2? = null // POI搜索
    var mPoiMarker: Marker? = null
    var default_mark:Marker? = null //签到位置
    var default_Sign_Lating: BDLating? = null
    var default_Sign_Location:String? = null //签到位置
    var currentTipPoint: LatLng = LatLng(0.0, 0.0)//获取当前的经纬度
    var Tip_address: String? = null
    var Tip_name: String? = null
    var Tip_City: String? = null
    var courseName: String? = null
    // 声明AMapLocationClient类对象
    var  mLocationClient: AMapLocationClient? = null;
    var mLocationOption: AMapLocationClientOption? = null
    var default_My_Lating:BDLating? = null
    var default_My_Location:String? = null
    var preUrl= ""
    var signUrl= ""
    var uid = ""
    var aid = ""
    var statuscontent = ""
    suspend fun analysis(url: String) {
        repository.request(analysisLiveData, false) {
            val request = NetworkUtils.buildClientRequest(url)
            NetworkUtils.request(request)
        }
    }
    suspend fun analysis2(url: String, cookies: String = "") {
        repository.request(ResponseMutableLiveData(), false) {
            val request = if (cookies.isEmpty()) {
                NetworkUtils.buildClientRequest(url)
            } else NetworkUtils.buildClientRequest(url, cookies)
            NetworkUtils.request(request)
        }
    }
    suspend  fun sign(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.request(signLiveData) {
                val request = NetworkUtils.buildClientRequest(url)
                NetworkUtils.request(request)
            }
        }
    }
    fun analysisForSignTogether(url: String, cookies: String, onSuccess: (Response) -> Unit = {}, onFailure: (String) -> Unit = {}) {
        val request = NetworkUtils.buildClientRequest(url, cookies)
        NetworkUtils.requestAsync(request, onSuccess, onFailure)
    }
    suspend fun request(url: String, cookies: String = "") {
        repository.request(ResponseMutableLiveData(), false) {
            val request = if (cookies.isEmpty()) {
                NetworkUtils.buildClientRequest(url)
            } else NetworkUtils.buildClientRequest(url, cookies)
            NetworkUtils.request(request)
        }
    }

    suspend fun preSign(url: String, cookies: String = "") {
         repository.request(preSignLiveData, false) {
             val request = if (cookies.isEmpty()) {
                 NetworkUtils.buildClientRequest(url)
             } else NetworkUtils.buildClientRequest(url, cookies)
             NetworkUtils.request(request)
         }
    }
    suspend fun signTogether(url: String, cookies: String) {
        repository.request(signTogetherLiveData, false) {
            val request = NetworkUtils.buildClientRequest(url, cookies)
            NetworkUtils.request(request)
        }
    }
    fun tryLogin(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.request(loginLiveData, false) {
                val request = NetworkUtils.buildServerRequest(url)
                NetworkUtils.request(request)
            }
        }
    }
    fun preSignWebGet(it:String,onSuccess: (PreWeb) -> Unit = {}){
        onSuccess(HtmlParser.parsePreSignWebGet(it))
    }

}