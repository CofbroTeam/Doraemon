package com.cofbro.qian.mapSetting

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.amap.api.maps2d.AMap
import com.amap.api.maps2d.CameraUpdateFactory
import com.amap.api.maps2d.MapView
import com.amap.api.maps2d.model.LatLng
import com.amap.api.maps2d.model.Marker
import com.amap.api.maps2d.model.MarkerOptions
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.core.PoiItemV2
import com.amap.api.services.core.SuggestionCity
import com.amap.api.services.help.Tip
import com.amap.api.services.poisearch.PoiResultV2
import com.amap.api.services.poisearch.PoiSearchV2
import com.cofbro.qian.R
import com.cofbro.qian.mapSetting.overlay.Poi2DOverlay

import com.cofbro.qian.mapSetting.util.Constants
import com.cofbro.qian.mapSetting.util.MyActivityResultContract
import com.cofbro.qian.mapSetting.util.ToastUtil


open class MainActivity :   Activity(),AMap.OnMarkerClickListener,
    AMap.InfoWindowAdapter, PoiSearchV2.OnPoiSearchListener, View.OnClickListener {
    private var mAMap: AMap? = null
    private var mKeyWords = "" // 要输入的poi搜索关键字
    private var progDialog: Dialog? = null // 搜索时进度条
    private var poiResult: PoiResultV2? = null // poi返回的结果
    private var currentPage = 1
    private var query: PoiSearchV2.Query? = null // Poi查询条件类
    private var poiSearch: PoiSearchV2? = null // POI搜索
    private var mKeywordsTextView: TextView? = null
    private var mPoiMarker: Marker? = null
    private var mCleanKeyWords: ImageView? = null
    var mMapView: MapView? = null
//    val myActivityLauncher = registerForActivityResult(MyActivityResultContract()){ result ->
//        Toast.makeText(applicationContext,result,Toast.LENGTH_SHORT).show()
//
//    }
     @RequiresApi(Build.VERSION_CODES.TIRAMISU)
     override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        mCleanKeyWords = findViewById(R.id.clean_keywords)
        mCleanKeyWords!!.setOnClickListener(this)

          mKeyWords = ""
         mMapView =  findViewById(R.id.maps);
         mMapView!!.onCreate(savedInstanceState)
         if (mAMap == null) {
             mAMap =  mMapView!!.map
             setUpMap()
         }
         init()
    mAMap!!.setOnMapClickListener { latLng -> // 地图 点击 更换marker的经纬度
        mAMap!!.clear()
        addLatLngMarker(latLng)

        Log.v("place", "latitude:$latLng");
    }
       val intent = intent
       if (intent!=null&&intent.hasExtra(Constants.EXTRA_TIP)){
           Log.v("result_tap:","result_have")

          val tip = intent.getParcelableExtra(Constants.EXTRA_TIP,Tip().javaClass)
           if (tip != null) {
               /*
               获取完整Tip
                */
               Log.v("result_tap:",tip.name)
               mAMap!!.clear()
            if (tip.poiID == null || tip.poiID == "") {
                doSearchQuery(tip.name)
            } else {
                addTipMarker(tip)
            }
               mKeywordsTextView!!.text = tip.name
               if (tip.name != "") {
                mCleanKeyWords!!.visibility = View.VISIBLE
            }
           }
       }

    }
    override fun onResume() {
        super.onResume()
        mMapView?.onResume();
    }
    override fun onPause() {
        super.onPause()
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView!!.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView!!.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        mMapView!!.onSaveInstanceState(outState)
    }

    /**
     * 初始化AMap对象
     */
    private fun init() {

        mKeywordsTextView = findViewById(R.id.main_keywords)
        mKeywordsTextView!!.setOnClickListener(this)
    }

    /**
     * 设置页面监听
     */
    private fun setUpMap() {
        mAMap!!.setOnMarkerClickListener(this) // 添加点击marker监听事件
        mAMap!!.setInfoWindowAdapter(this) // 添加显示infowindow监听事件
        mAMap!!.uiSettings.isScrollGesturesEnabled = (false)

    }

    /**
     * 显示进度框
     */
    private fun showProgressDialog() {
        if (progDialog == null) progDialog = Dialog(this)
//        progDialog!!(ProgressDialog.STYLE_SPINNER)
//        progDialog!!.isIndeterminate = false
        progDialog!!.setCancelable(false)
//        progDialog!!.setMessage("正在搜索:\n$mKeyWords")
        progDialog!!.show()
    }

    /**
     * 隐藏进度框
     */
    private fun dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog!!.dismiss()
        }
    }

    /**
     * 开始进行poi搜索
     */
    private fun doSearchQuery(keywords: String?) {
        showProgressDialog() // 显示进度框
        currentPage = 1
        // 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query = PoiSearchV2.Query(keywords, "", Constants.DEFAULT_CITY)
        // 设置每页最多返回多少条poiitem
        query!!.pageSize = 10
        // 设置查第一页
        query!!.pageNum = currentPage
        poiSearch = PoiSearchV2(this, query)
        poiSearch!!.setOnPoiSearchListener(this)
        poiSearch!!.searchPOIAsyn()
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        marker.showInfoWindow()
        return false
    }

    override fun getInfoContents(marker: Marker?): View? {
        return null
    }

    override fun getInfoWindow(marker: Marker): View {
        val view: View = getLayoutInflater().inflate(
            R.layout.poikeywordsearch_uri,
            null
        )
        val title = view.findViewById<View>(R.id.title) as TextView
        title.setText(marker.getTitle())
        val snippet = view.findViewById<View>(R.id.snippet) as TextView
        snippet.setText(marker.getSnippet())
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
        ToastUtil.show(this@MainActivity, infomation)
    }

    /**
     * POI信息查询回调方法
     */
    //(p0: PoiResultV2?, p1: Int)
    override fun onPoiSearched(result: PoiResultV2?, rCode: Int){
        dissmissProgressDialog() // 隐藏对话框
        if (rCode == 1000) {
            if (result != null && result.query != null) { // 搜索poi的结果
                if (result.query == query) { // 是否是同一条
                    poiResult = result
                    // 取得搜索到的poiitems有多少页
                    val poiItems: ArrayList<PoiItemV2>? = poiResult!!.pois // 取得第一页的poiitem数据，页数从数字0开始
//                    val suggestionCities = (poiResult!! )
//                        .searchSuggestionCitys // 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
                    if (poiItems != null && poiItems.size > 0) {
                        mAMap!!.clear() // 清理之前的图标
                        val poi2DOverlay = Poi2DOverlay(mAMap, poiItems)
                        poi2DOverlay.removeFromMap()
                        poi2DOverlay.addToMap()
                        poi2DOverlay.zoomToSpan()
                    }
//                    } else if (suggestionCities != null
//                        && suggestionCities.size > 0
//                    ) {
//                        showSuggestCity(suggestionCities)
//                    }
                    else {
                        ToastUtil.show(
                            this@MainActivity,
                           " R.string.no_result"
                        )
                    }
                }
            } else {
                ToastUtil.show(
                    this@MainActivity,
                    "R.string.no_result"
                )
            }
        } else {
            ToastUtil.showerror(this, rCode)
        }
    }

    override fun onPoiItemSearched(p0: PoiItemV2?, p1: Int)  {
        // TODO Auto-generated method stub
    }

    /**
     * 输入提示activity选择结果后的处理逻辑
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */




    /**
     * 用marker展示输入提示list选中数据
     *
     * @param tip
     */
    private fun addTipMarker(tip: Tip?) {
        if (tip == null) {
            return
        }
        mPoiMarker = mAMap!!.addMarker(MarkerOptions())
        val point = tip.point
        if (point != null) {
            val markerPosition = LatLng(point.latitude, point.longitude)
            mPoiMarker!!.position = markerPosition
            mAMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPosition, 17F))
        }
        mPoiMarker!!.title = tip.name
        mPoiMarker!!.snippet = tip.address
    }

    private fun addLatLngMarker(LatLng: LatLng?) {
        if (LatLng == null) {
            return
        }
        mPoiMarker = mAMap!!.addMarker(MarkerOptions())
        val point = LatLng
        val markerPosition = LatLng(point.latitude, point.longitude)
        mPoiMarker!!.position = markerPosition
        mAMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPosition, 17F))
        //        mPoiMarker!!.title = tip.name
//        mPoiMarker!!.snippet = tip.address
    }

    /**
     * 点击事件回调方法
     */
    override fun onClick(v: View) {
        when (v.id) {
            R.id.main_keywords -> {
                val intent = Intent(this, InputTipsActivity::class.java)
                intent.putExtra("code", REQUEST_CODE);
                startActivity(intent)
                // 开启页面跳转
//                myActivityLauncher.launch(REQUEST_CODE.toString())
//                startActivityForResult(intent, REQUEST_CODE)

            }

            R.id.clean_keywords -> {
                mKeywordsTextView!!.text = ""
                mAMap!!.clear()
                mCleanKeyWords!!.visibility = View.GONE
            }

            else -> {}
        }
    }

    companion object {
        const val REQUEST_CODE = 100
        const val RESULT_CODE_INPUTTIPS = 101
        const val RESULT_CODE_KEYWORDS = 102
    }


}
