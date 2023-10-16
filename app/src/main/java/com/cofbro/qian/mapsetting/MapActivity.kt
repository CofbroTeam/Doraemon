package com.cofbro.qian.mapsetting

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps2d.AMap
import com.amap.api.maps2d.CameraUpdateFactory
import com.amap.api.maps2d.model.LatLng
import com.amap.api.maps2d.model.Marker
import com.amap.api.maps2d.model.MarkerOptions
import com.amap.api.services.core.PoiItemV2
import com.amap.api.services.core.SuggestionCity
import com.amap.api.services.poisearch.PoiResultV2
import com.amap.api.services.poisearch.PoiSearchV2
import com.cofbro.hymvvmutils.base.BaseActivity
import com.cofbro.qian.R
import com.cofbro.qian.data.URL
import com.cofbro.qian.databinding.ActivityMapBinding
import com.cofbro.qian.mapsetting.overlay.Poi2DOverlay
import com.cofbro.qian.mapsetting.util.Constants
import com.cofbro.qian.mapsetting.util.ToastUtil
import com.cofbro.qian.mapsetting.viewmodel.MapViewModel
import com.cofbro.qian.utils.CacheUtils
import com.cofbro.qian.utils.showSignResult
import com.hjq.toast.ToastUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.regex.Matcher
import java.util.regex.Pattern


class MapActivity : BaseActivity<MapViewModel, ActivityMapBinding>(), AMap.OnMarkerClickListener,
    AMap.InfoWindowAdapter, PoiSearchV2.OnPoiSearchListener, View.OnClickListener {
    private var uid = ""
    private var aid = ""
    private var preUrl = ""
    private var signUrl = ""
    private var lat: String = ""
    private var long: String = ""
    // 声明AMapLocationClient类对象
    var  mLocationClient: AMapLocationClient? = null;

    var mLocationOption: AMapLocationClientOption? = null
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        getCurrentLocationLatLng()
    }




    /**
     * 根据LocationManager获取定位信息的提供者
     * @param locationManager
     * @return
     */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        initArgs()
        initObserver()
        doNetwork()
        initViewClick()
        initMap(savedInstanceState)
    }

    private fun doNetwork() {
        viewModel.preSign(preUrl)
    }
    private fun getCurrentLocationLatLng() {
        AMapLocationClient.updatePrivacyAgree(applicationContext, true)
        AMapLocationClient.updatePrivacyShow(applicationContext,true,true)
        //初始化定位
        mLocationClient =  AMapLocationClient(applicationContext);
        //设置定位回调监听
        mLocationClient?.setLocationListener { amapLocation ->
            if (amapLocation != null) {

                if (amapLocation.errorCode == 0) {
                    amapLocation.locationType //获取当前定位结果来源，如网络定位结果，详见定位类型表
                    amapLocation.latitude //获取纬度
                    amapLocation.longitude //获取经度
                    amapLocation.accuracy //获取精度信息
                    amapLocation.address //地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                    amapLocation.country //国家信息
                    amapLocation.province //省信息
                    amapLocation.city //城市信息
                    amapLocation.district //城区信息
                    amapLocation.street //街道信息
                    amapLocation.streetNum //街道门牌号信息
                    amapLocation.cityCode //城市编码
                    amapLocation.adCode //地区编码
                    amapLocation.aoiName //获取当前定位点的AOI信息
                    amapLocation.buildingId //获取当前室内定位的建筑物Id
                    amapLocation.floor //获取当前室内定位的楼层
                    amapLocation.gpsAccuracyStatus //获取GPS的当前状态
                    addLatLngMarker(LatLng(amapLocation.latitude,amapLocation.longitude))
                } else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e(
                        "AmapError",
                        "location Error, ErrCode:" + amapLocation.errorCode + ", errInfo:" + amapLocation.errorInfo
                    )
                }
            }
        }
        //初始化AMapLocationClientOption对象
        mLocationOption =  AMapLocationClientOption();

        mLocationOption?.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy;

        // 设置为单次定位  : 默认为false
        mLocationOption?.isOnceLocation = true;
        mLocationOption?.httpTimeOut = 20000;
        mLocationOption?.isLocationCacheEnable = false;
        mLocationClient?.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient?.startLocation();
    }

    private fun initArgs() {
        aid = intent.getStringExtra("aid") ?: ""
        preUrl = intent.getStringExtra("preUrl") ?: ""
        uid = CacheUtils.cache["uid"] ?: ""
    }

    override fun onResume() {
        super.onResume()
        binding?.maps?.onResume();

    }

    override fun onPause() {
        super.onPause()
        // 在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        binding?.maps?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        // 在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        binding?.maps?.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        binding?.maps!!.onSaveInstanceState(outState)
    }

    /**
     * 设置页面监听
     */
    private fun setUpMap() {
        binding?.maps?.map?.setOnMarkerClickListener(this) // 添加点击marker监听事件
        binding?.maps?.map?.setInfoWindowAdapter(this) // 添加显示infowindow监听事件
        binding?.maps?.map?.uiSettings?.isScrollGesturesEnabled = (false)

    }

    /**
     * 显示进度框
     */
    private fun showProgressDialog() {
        if (viewModel.progressDialog == null) viewModel.progressDialog = Dialog(this)
//        progDialog!!(ProgressDialog.STYLE_SPINNER)
//        progDialog!!.isIndeterminate = false
        viewModel.progressDialog?.setCancelable(false)
//        progDialog!!.setMessage("正在搜索:\n$mKeyWords")
        viewModel.progressDialog?.show()
    }

    /**
     * 隐藏进度框
     */
    private fun dismissProgressDialog() {
        if (viewModel.progressDialog != null) {
            viewModel.progressDialog?.dismiss()
        }
    }

    /**
     * 开始进行poi搜索
     */
    private fun doSearchQuery(keywords: String?) {
        showProgressDialog() // 显示进度框
        viewModel.currentPage = 1
        // 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        viewModel.query = PoiSearchV2.Query(keywords, "", Constants.DEFAULT_CITY)
        // 设置每页最多返回多少条poiitem
        viewModel.query?.pageSize = 10
        // 设置查第一页
        viewModel.query?.pageNum = viewModel.currentPage
        viewModel.poiSearch = PoiSearchV2(this, viewModel.query)
        viewModel.poiSearch?.setOnPoiSearchListener(this)
        viewModel.poiSearch?.searchPOIAsyn()
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        marker.showInfoWindow()
        return false
    }

    override fun getInfoContents(marker: Marker?): View? {
        return null
    }

    override fun getInfoWindow(marker: Marker): View {
        val view: View = layoutInflater.inflate(
            R.layout.poikeywordsearch_uri,
            null
        )
        val title = view.findViewById<View>(R.id.title) as TextView
        title.text = marker.title
        val snippet = view.findViewById<View>(R.id.snippet) as TextView
        snippet.text = marker.snippet
        return view
    }

    /**
     * poi没有搜索到数据，返回一些推荐城市的信息
     */
    private fun showSuggestCity(cities: List<SuggestionCity>) {
        var infomation = "推荐城市\n"
        for (i in cities.indices) {
            infomation += """
                城市名称:${cities[i].cityName}城市区号:${cities[i].cityCode}城市编码:${cities[i].adCode}
                
                """.trimIndent()
        }
        ToastUtil.show(this@MapActivity, infomation)
    }

    /**
     * POI信息查询回调方法
     */
    //(p0: PoiResultV2?, p1: Int)
    override fun onPoiSearched(result: PoiResultV2?, rCode: Int) {
        dismissProgressDialog() // 隐藏对话框
        if (rCode == 1000) {
            if (result != null && result.query != null) { // 搜索poi的结果
                if (result.query == viewModel.query) { // 是否是同一条
                    viewModel.poiResult = result
                    // 取得搜索到的poiitems有多少页
                    val poiItems: ArrayList<PoiItemV2>? =
                        viewModel.poiResult!!.pois // 取得第一页的poiitem数据，页数从数字0开始
//                    val suggestionCities = (poiResult!! )
//                        .searchSuggestionCitys // 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
                    if (poiItems != null && poiItems.size > 0) {
                        binding?.maps?.map?.clear() // 清理之前的图标
                        val poi2DOverlay = Poi2DOverlay(binding?.maps?.map, poiItems)
                        poi2DOverlay.removeFromMap()
                        poi2DOverlay.addToMap()
                        poi2DOverlay.zoomToSpan()
                    } else {
                        ToastUtil.show(
                            this@MapActivity,
                            " R.string.no_result"
                        )
                    }
                }
            } else {
                ToastUtil.show(
                    this@MapActivity,
                    "R.string.no_result"
                )
            }
        } else {
            ToastUtil.showerror(this, rCode)
        }
    }

    override fun onPoiItemSearched(p0: PoiItemV2?, p1: Int) {
        // TODO Auto-generated method stub
    }

    /**
     * 用marker展示输入提示list选中数据
     *
     * @param tip
     */
    private fun addTipMarker(tip: ArrayList<String>) {
        if (tip[0] == "") {
            return
        }
        viewModel.mPoiMarker = binding?.maps?.map?.addMarker(MarkerOptions())

        if (tip[3] != "") {
            val markerPosition = LatLng(tip[3].toDouble(), tip[4].toDouble())
            viewModel.mPoiMarker!!.position = markerPosition
            binding?.maps?.map?.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPosition, 17F))
        }
        viewModel.mPoiMarker!!.title = tip[0]
        viewModel.mPoiMarker!!.snippet = tip[1]
    }

    private fun addLatLngMarker(LatLng: LatLng?,default: Boolean = false) {
        if (LatLng == null) {
            return
        }
        viewModel.mPoiMarker = binding?.maps?.map?.addMarker(MarkerOptions())
        val point = LatLng
        val markerPosition = LatLng(point.latitude, point.longitude)
        viewModel.mPoiMarker!!.position = markerPosition
        if (!default){
            binding?.maps?.map?.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPosition, 17F))
        }

        //        mPoiMarker!!.title = tip.name
//        mPoiMarker!!.snippet = tip.address
    }

    /**
     * 点击事件回调方法
     */
    override fun onClick(v: View) {
//        when (v.id) {
//            R.id.main_keywords -> {
//                val intent = Intent(this, InputTipsActivity::class.java)
//                intent.putExtra("code", REQUEST_CODE);
//                startActivity(intent)
                // 开启页面跳转
//                myActivityLauncher.launch(REQUEST_CODE.toString())
//                startActivityForResult(intent, REQUEST_CODE)
//            }

//            R.id.clean_keywords -> {
//                binding?.mainKeywords?.text = ""
//                binding?.maps?.map?.clear()
//                binding?.cleanKeywords?.visibility = View.GONE
//            }

//            else -> {}
//        }
    }

    companion object {
        const val REQUEST_CODE = 100
        const val RESULT_CODE_INPUTTIPS = 101
        const val RESULT_CODE_KEYWORDS = 102
    }

    private fun initViewClick() {
        //binding?.cleanKeywords?.setOnClickListener(this)
        binding?.selectButton?.setOnClickListener {
            if (viewModel.currentTipPoint.latitude.toInt() != 0 && viewModel.currentTipPoint.latitude.toInt() != 0) {
                // 成功初始化mark并成功定位
                Toast.makeText(this, "定位成功", Toast.LENGTH_SHORT).show()
                if (viewModel.Tip_address != null && viewModel.Tip_name != null) {
                    val cityName = viewModel.Tip_City
                    val address = urlEncodeChinese(cityName + " " + viewModel.Tip_name)
                    if (lat.isNotEmpty() && long.isNotEmpty()) {
                        signUrl =
                            URL.getLocationSignPath(address, aid, uid, lat, long)
                        sign(signUrl)
                    } else {
                        ToastUtils.show("请稍后")
                    }
                }
            } else {
                //Toast.makeText(this, "没有定位", Toast.LENGTH_SHORT).show()
                if (lat.isNotEmpty() && long.isNotEmpty()) {
                    signUrl =
                        URL.getLocationSignPath(
                            address = binding?.etLocationName?.text.toString(),
                            aid,
                            uid,
                            lat,
                            long
                        )
                    sign(signUrl)
                } else {
                    ToastUtils.show("请稍后")
                }
            }
        }
        binding?.mainKeywords?.setOnClickListener {
            val intent = Intent(this, InputTipsActivity::class.java)
            intent.putExtra("code", REQUEST_CODE);
            startActivity(intent)
        }

    }

    private fun initObserver() {
        // 签到
        viewModel.signLiveData.observe(this) {
            lifecycleScope.launch(Dispatchers.IO) {
                val data = it.data?.body?.string()
                withContext(Dispatchers.Main) {
                    data?.showSignResult()
                    finish()
                }
            }
        }

        viewModel.preSignLiveData.observe(this) {
            lifecycleScope.launch(Dispatchers.IO) {
                val data = it.data?.body?.string()
                data?.let {
                    val html = Jsoup.parse(it)
                    val latitude = html.getElementById("locationLatitude")?.`val`()
                    val longitude = html.getElementById("locationLongitude")?.`val`()
                    if (!latitude.isNullOrEmpty() && !longitude.isNullOrEmpty()) {
                        lat = latitude
                        long = longitude
                        addLatLngMarker(LatLng(lat.toDouble(),long.toDouble()),default = true)
                    } else {
                        lat = html.getElementById("latitude")?.`val`() ?: ""
                        long = html.getElementById("longitude")?.`val`() ?: ""
                        addLatLngMarker(LatLng(lat.toDouble(),long.toDouble()),default = true)
                    }

                }

            }
        }

        // 签到
        viewModel.signLiveData.observe(this) {
            lifecycleScope.launch(Dispatchers.IO) {
                val data = it.data?.body?.toString()
                withContext(Dispatchers.Main) {
                    data?.showSignResult()
                }
            }
        }
    }

    private fun sign(url: String) {
        viewModel.sign(url)
    }

    private fun initMap(savedInstanceState: Bundle?) {
        binding?.maps!!.onCreate(savedInstanceState)
        if (binding?.maps?.map == null) {
            setUpMap()
        }
        binding?.maps?.map?.setOnMapClickListener { latLng -> // 地图 点击 更换marker的经纬度
            binding?.maps?.map?.clear()
            addLatLngMarker(latLng)
            viewModel.currentTipPoint = latLng
        }
        if (intent != null && intent.hasExtra(Constants.EXTRA_TIP)) {
            val tip = intent.getStringArrayListExtra(Constants.EXTRA_TIP)
            if (tip != null) {
                /**
                获取完整Tip
                 */
                binding?.maps?.map?.clear()
                viewModel.currentTipPoint = LatLng(tip[3].toDouble(), tip[4].toDouble())
                if (tip[2] == null || tip[2] == "") {
                    doSearchQuery(tip[0])
                } else {
                    addTipMarker(tip)
                }
                binding?.mainKeywords?.text = tip[0]
                if (tip[0] != "") {
                    //binding?.cleanKeywords?.visibility = View.VISIBLE
                }
                // 获取完整的name和address
                viewModel.Tip_name = tip[0]
                viewModel.Tip_address = tip[1]
                viewModel.Tip_City = tip[5]

            }
        }
        getCurrentLocationLatLng()
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

}
