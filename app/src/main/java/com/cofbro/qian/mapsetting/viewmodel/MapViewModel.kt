package com.cofbro.qian.mapsetting.viewmodel

import android.app.Dialog
import com.amap.api.maps2d.model.LatLng
import com.amap.api.maps2d.model.Marker
import com.amap.api.services.poisearch.PoiResultV2
import com.amap.api.services.poisearch.PoiSearchV2
import com.cofbro.hymvvmutils.base.BaseViewModel
import com.cofbro.qian.mapsetting.repository.MapRepository
import com.cofbro.qian.mapsetting.util.Constants

class MapViewModel:BaseViewModel<MapRepository>() {
     var progDialog: Dialog? = null // 搜索时进度条
     var poiResult: PoiResultV2? = null // poi返回的结果
     var currentPage = 1
     var query: PoiSearchV2.Query? = null // Poi查询条件类
     var poiSearch: PoiSearchV2? = null // POI搜索
     var mPoiMarker: Marker? = null
      var currentTipPoint : LatLng = LatLng(0.0, 0.0)//获取当前的经纬度
     var EXTRA_MSG :MutableList<String>? = null
     var Tip_address:String? = null
     var Tip_name:String?=null
     var EXTRA_uid:String? = null
     var EXTRA_aid:String? = null
}