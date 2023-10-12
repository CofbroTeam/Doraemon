package com.cofbro.qian.mapsetting
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.telephony.CarrierConfigManager.Bsf
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.amap.api.maps2d.AMap
import com.amap.api.maps2d.CameraUpdateFactory
import com.amap.api.maps2d.MapView
import com.amap.api.maps2d.model.LatLng
import com.amap.api.maps2d.model.Marker
import com.amap.api.maps2d.model.MarkerOptions
import com.amap.api.services.core.PoiItemV2
import com.amap.api.services.core.SuggestionCity
import com.amap.api.services.poisearch.PoiResultV2
import com.amap.api.services.poisearch.PoiSearchV2
import com.cofbro.hymvvmutils.base.BaseActivity
import com.cofbro.qian.R
import com.cofbro.qian.databinding.ActivityMapBinding
import com.cofbro.qian.mapsetting.overlay.Poi2DOverlay
import com.cofbro.qian.mapsetting.util.Constants
import com.cofbro.qian.mapsetting.util.ToastUtil
import com.cofbro.qian.mapsetting.viewmodel.MapViewModel

 class MapActivity :   BaseActivity<MapViewModel,ActivityMapBinding>(),AMap.OnMarkerClickListener,
    AMap.InfoWindowAdapter, PoiSearchV2.OnPoiSearchListener, View.OnClickListener {


     @RequiresApi(Build.VERSION_CODES.TIRAMISU)
     override fun onActivityCreated(savedInstanceState: Bundle?) {
         initViewClick()
         initMap(savedInstanceState)
     }
    override fun onResume() {
        super.onResume()
        binding?.maps?.onResume();

    }
    override fun onPause() {
        super.onPause()
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        binding?.maps?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
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
        if (viewModel.progDialog == null) viewModel.progDialog = Dialog(this)
//        progDialog!!(ProgressDialog.STYLE_SPINNER)
//        progDialog!!.isIndeterminate = false
        viewModel.progDialog?.setCancelable(false)
//        progDialog!!.setMessage("正在搜索:\n$mKeyWords")
        viewModel.progDialog?.show()
    }

    /**
     * 隐藏进度框
     */
    private fun dissmissProgressDialog() {
        if (viewModel.progDialog != null) {
            viewModel.progDialog?.dismiss()
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
        ToastUtil.show(this@MapActivity, infomation)
    }

    /**
     * POI信息查询回调方法
     */
    //(p0: PoiResultV2?, p1: Int)
    override fun onPoiSearched(result: PoiResultV2?, rCode: Int){
        dissmissProgressDialog() // 隐藏对话框
        if (rCode == 1000) {
            if (result != null && result.query != null) { // 搜索poi的结果
                if (result.query == viewModel.query) { // 是否是同一条
                    viewModel.poiResult = result
                    // 取得搜索到的poiitems有多少页
                    val poiItems: ArrayList<PoiItemV2>? = viewModel.poiResult!!.pois // 取得第一页的poiitem数据，页数从数字0开始
//                    val suggestionCities = (poiResult!! )
//                        .searchSuggestionCitys // 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
                    if (poiItems != null && poiItems.size > 0) {
                        binding?.maps?.map?.clear() // 清理之前的图标
                        val poi2DOverlay = Poi2DOverlay( binding?.maps?.map, poiItems)
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
    private fun addTipMarker(tip: ArrayList<String>) {
        if (tip[0] == "") {
            return
        }
        viewModel.mPoiMarker =  binding?.maps?.map?.addMarker(MarkerOptions())

        if (tip[3] != "") {
            val markerPosition = LatLng(tip[3].toDouble(),tip[4].toDouble())
            viewModel.mPoiMarker!!.position = markerPosition
            binding?.maps?.map?.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPosition, 17F))
        }
        viewModel.mPoiMarker!!.title = tip[0]
        viewModel.mPoiMarker!!.snippet = tip[1]
    }

    private fun addLatLngMarker(LatLng: LatLng?) {
        if (LatLng == null) {
            return
        }
        viewModel.mPoiMarker =  binding?.maps?.map?.addMarker(MarkerOptions())
        val point = LatLng
        val markerPosition = LatLng(point.latitude, point.longitude)
        viewModel.mPoiMarker!!.position = markerPosition
        binding?.maps?.map?.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPosition, 17F))
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
                binding?.mainKeywords?.text = ""
                binding?.maps?.map?.clear()
                binding?.cleanKeywords?.visibility = View.GONE
            }

            else -> {}
        }
    }

    companion object {
        const val REQUEST_CODE = 100
        const val RESULT_CODE_INPUTTIPS = 101
        const val RESULT_CODE_KEYWORDS = 102
    }
     private fun initViewClick(){
         binding?.cleanKeywords?.setOnClickListener(this)
         binding?.selectButton?.setOnClickListener {
             if (viewModel.currentTipPoint.latitude.toInt() !=0&&viewModel.currentTipPoint.latitude.toInt()!=0){
                 //成狗初始化mark,并成功定位
                 Toast.makeText(this, "定位成功", Toast.LENGTH_SHORT).show()
                 /**
                  * 传递point,构造伪造位置
                  */

             }else{
                 Toast.makeText(this, "没有定位", Toast.LENGTH_SHORT).show()
             }
         }
         binding?.mainKeywords?.setOnClickListener(this)
     }
     private fun initMap(savedInstanceState: Bundle?){
         binding?.maps!!.onCreate(savedInstanceState)
         if ( binding?.maps?.map == null) {

             setUpMap()
         }

         binding?.maps?.map?.setOnMapClickListener { latLng -> // 地图 点击 更换marker的经纬度
             binding?.maps?.map?.clear()
             addLatLngMarker(latLng)
             viewModel.currentTipPoint = latLng
             Log.v("place", "latitude:$latLng");
         }
         val intent = intent
         if (intent!=null&&intent.hasExtra(Constants.EXTRA_TIP)){
             Log.v("result_tap:","result_have")

             val tip = intent.getStringArrayListExtra(Constants.EXTRA_TIP)
             if (tip != null) {
                 /*
                 获取完整Tip
                  */
                 Log.v("result_tap:",tip[0])
                 binding?.maps?.map?.clear()
                 viewModel.currentTipPoint = LatLng(tip[3].toDouble(),tip[4].toDouble())
                 if (tip[2] == null || tip[2] == "") {
                     doSearchQuery(tip[0])
                 } else {
                     addTipMarker(tip)
                 }
                 binding?.mainKeywords?.text = tip[0]
                 if (tip[0] != "") {
                     binding?.cleanKeywords?.visibility = View.VISIBLE
                 }
             }
         }
     }


}
