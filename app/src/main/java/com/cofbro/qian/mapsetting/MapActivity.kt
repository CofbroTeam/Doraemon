package com.cofbro.qian.mapsetting


import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.maps2d.AMap
import com.amap.api.maps2d.CameraUpdateFactory
import com.amap.api.maps2d.model.BitmapDescriptorFactory
import com.amap.api.maps2d.model.LatLng
import com.amap.api.maps2d.model.Marker
import com.amap.api.maps2d.model.MarkerOptions
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.core.PoiItemV2
import com.amap.api.services.core.SuggestionCity
import com.amap.api.services.geocoder.GeocodeResult
import com.amap.api.services.geocoder.GeocodeSearch
import com.amap.api.services.geocoder.RegeocodeQuery
import com.amap.api.services.geocoder.RegeocodeResult
import com.amap.api.services.help.Tip
import com.amap.api.services.poisearch.PoiResultV2
import com.amap.api.services.poisearch.PoiSearchV2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.cofbro.hymvvmutils.base.BaseActivity
import com.cofbro.hymvvmutils.base.getBySp
import com.cofbro.qian.R
import com.cofbro.qian.data.URL
import com.cofbro.qian.databinding.ActivityMapBinding
import com.cofbro.qian.main.MainActivity
import com.cofbro.qian.mapsetting.overlay.Poi2DOverlay
import com.cofbro.qian.mapsetting.util.Constants
import com.cofbro.qian.mapsetting.util.ToastUtil
import com.cofbro.qian.mapsetting.viewmodel.MapViewModel
import com.cofbro.qian.utils.AccountManager
import com.cofbro.qian.utils.AmapUtils
import com.cofbro.qian.utils.CacheUtils
import com.cofbro.qian.utils.SignRecorder
import com.cofbro.qian.utils.dp2px
import com.cofbro.qian.utils.getStringExt
import com.cofbro.qian.utils.safeParseToJson
import com.cofbro.qian.view.FullScreenDialog
import com.hjq.toast.ToastUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.regex.Matcher
import java.util.regex.Pattern
import com.cofbro.qian.utils.AmapUtils.BDLating

class MapActivity : BaseActivity<MapViewModel, ActivityMapBinding>(), AMap.OnMarkerClickListener,
    AMap.InfoWindowAdapter, PoiSearchV2.OnPoiSearchListener {
    private var alreadySign = false
    private var cookies = ""
    private var remark = ""
    private var mStatus = true
    private var otherSignUsers: JSONArray? = null
    private var alreadySignCount = 0
    private var loadingDialog: Dialog? = null
    private var preSignOther = false
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        AmapUtils.checkLocationPermission(this)
        AmapUtils.openLocation(this)
        getAvtarImage()
        initArgs()
        initObserver()
        doNetwork()
        initViewClick()
        initMap(savedInstanceState)
        initLocationData()
    }


    private fun doNetwork() {
        lifecycleScope.launch(Dispatchers.IO) {
            analysisAndStartSign(viewModel.aid)
            viewModel.preSign(viewModel.preUrl)
        }
    }

    private fun initLocationData() {
        viewModel.default_Sign_Lating =
            CacheUtils.cache["default_Sign_latitude"]?.toDouble()
                ?.let {
                    CacheUtils.cache["default_Sign_longitude"]?.toDouble()
                        ?.let { it1 -> LatLng(it, it1) }
                }
    }
    private fun initArgs() {
        viewModel.aid = intent.getStringExtra("aid") ?: ""
        viewModel.preUrl = intent.getStringExtra("preUrl") ?: ""
        viewModel.uid = CacheUtils.cache["uid"] ?: ""
        viewModel.courseName = intent.getStringExtra("courseName") ?: ""
    }

    override fun onResume() {
        super.onResume()
        binding?.maps?.onResume()

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
        viewModel.progressDialog?.setCancelable(false)
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
     * poi没有搜索到数据，返回一些推荐城市的信息  Deprecated
     */
    @Deprecated("高德版本更新舍弃")
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
                        viewModel.poiResult!!.pois
                    if (poiItems != null && poiItems.size > 0) {
                        binding?.maps?.map?.clear() // 清理之前的图标
                        val poi2DOverlay = Poi2DOverlay(binding?.maps?.map, poiItems)
                        poi2DOverlay.removeFromMap()
                        poi2DOverlay.addToMap()
                        poi2DOverlay.zoomToSpan()
                    } else {
                        ToastUtil.show(
                            this@MapActivity,
                            "网络错误"
                        )
                    }
                }
            } else {
                ToastUtil.show(
                    this@MapActivity,
                    "网络错误"
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
        val view = View.inflate(applicationContext, R.layout.item_sign_default_mark, null)
        val imageView: ImageView = view.findViewById(R.id.avatar_default)
        imageView.setImageDrawable(binding!!.search.drawable)
        val descriptor = BitmapDescriptorFactory.fromView(view)
        viewModel.mPoiMarker = binding?.maps?.map?.addMarker(MarkerOptions().icon(descriptor))
        if (tip[3] != "") {
            val markerPosition = LatLng(tip[3].toDouble(), tip[4].toDouble())
            viewModel.mPoiMarker!!.position = markerPosition
            binding?.maps?.map?.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPosition, 17F))
        }
        viewModel.mPoiMarker!!.title = tip[0]
        viewModel.mPoiMarker!!.snippet = tip[1]
    }

    private fun addLatingDefaultMarker(LatLng: LatLng?) {
        if (LatLng == null) {
            return
        }
        val view = View.inflate(applicationContext, R.layout.item_sign_default_mark, null)
        val descriptor = BitmapDescriptorFactory.fromView(view)
        viewModel.default_mark = binding?.maps?.map?.addMarker(MarkerOptions().icon(descriptor))
        val point = LatLng
        val markerPosition = LatLng(point.latitude, point.longitude)
        viewModel.default_mark!!.position = markerPosition
    }

    private fun addLatLngMarker(latLng: LatLng?, default: Boolean = false) {
        if (latLng == null) {
            return
        }
        val view = View.inflate(applicationContext, R.layout.item_sign_default_mark, null)
        val imageView: ImageView = view.findViewById(R.id.avatar_default)
        imageView.setImageDrawable(binding!!.search.drawable)
        val descriptor = BitmapDescriptorFactory.fromView(view)
        viewModel.mPoiMarker = binding?.maps?.map?.addMarker(MarkerOptions().icon(descriptor))
        val point = latLng
        val markerPosition = LatLng(point.latitude, point.longitude)
        viewModel.mPoiMarker!!.position = markerPosition
        if (!default) {
            binding?.maps?.map?.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPosition, 17F))
        }
    }

    private fun getAvtarImage() {
        // 用户头像
        val uid = CacheUtils.cache["uid"]
        uid?.let {
            val options = RequestOptions().transform(
                CenterCrop(),
                RoundedCorners(dp2px(applicationContext, 5))
            )
            Glide.with(this@MapActivity)
                .load(URL.getAvtarImgPath(it))
                .apply(options)
                .into(binding!!.search)


        }

    }

    /**
     * 点击事件回调方法
     */

    companion object {
        const val REQUEST_CODE = 100
        const val RESULT_CODE_INPUTTIPS = 101
        const val RESULT_CODE_KEYWORDS = 102
    }

    private fun initViewClick() {
        binding?.selectButton?.setOnClickListener {
            /**
             * 绑定签到
             */
            if (viewModel.statuscontent == "签到成功") {
                ToastUtil.show(applicationContext, "您已经签到过了")
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
            }
            if (viewModel.statuscontent != "签到成功" && viewModel.currentTipPoint.latitude.toInt() != 0 && viewModel.currentTipPoint.latitude.toInt() != 0) {
                // 成功初始化mark并成功定位
                Toast.makeText(this, "修改位置成功", Toast.LENGTH_SHORT).show()
                if (viewModel.Tip_address != null && viewModel.Tip_name != null) {
                    val cityName = viewModel.Tip_City
                    val address = urlEncodeChinese(cityName + " " + viewModel.Tip_name)
                    if (viewModel.currentTipPoint.latitude != 0.0 && viewModel.currentTipPoint.longitude != 0.0) {
                        val Lating = AmapUtils.mapPointGdTurnBaiDu( viewModel.currentTipPoint.latitude,
                            viewModel.currentTipPoint.longitude)
                        viewModel.signUrl =
                            URL.getLocationSignPath(
                                address,
                                viewModel.aid,
                                viewModel.uid,
                                Lating.latitude.toString(),
                                Lating.longitude.toString()
                            )
                        sign(viewModel.signUrl)
                        /**
                         * 实现一起签到
                         */

                    } else {
                        ToastUtils.show("请稍后")
                    }
                } else {
                    /**
                     * 没有任何输入，直接上传默认地址 首先判断是否签到成功 bug:presign无默认位置
                     */
                    if (viewModel.default_Sign_Location?.isNotEmpty() == true) {
                        val defaultUrl = URL.getLocationSignPath(
                            address = viewModel.default_Sign_Location,
                            aid = viewModel.aid,
                            uid = viewModel.uid,
                            lat = viewModel.default_Sign_Lating?.latitude.toString(),
                            long = viewModel.default_Sign_Lating?.longitude.toString()
                        )
                        viewModel.signUrl = defaultUrl
                        sign(defaultUrl)

                    } else {
                        /**
                         * 判断是否签到成功，或者本来就没有签到位置
                         */

                    }

                }
            } else {
                //Toast.makeText(this, "没有定位", Toast.LENGTH_SHORT).show()
                /**
                 * 选择上传让老师看到的位置
                 */
                if (viewModel.currentTipPoint.latitude != 0.0 && viewModel.currentTipPoint.longitude != 0.0) {
                    val Lating = AmapUtils.mapPointGdTurnBaiDu( viewModel.currentTipPoint.latitude,
                        viewModel.currentTipPoint.longitude)
                    viewModel.signUrl =
                        URL.getLocationSignPath(
                            address = binding?.etLocationName?.text.toString(),
                            viewModel.aid,
                            viewModel.uid,
                            Lating.latitude.toString(),
                            Lating.longitude.toString()
                        )
                    sign(viewModel.signUrl)
                } else {

                }
            }
        }
        binding?.mainKeywords?.apply {
            setOnClickListener {
                val intent = Intent(this@MapActivity, InputTipsActivity::class.java)
                intent.putExtra("code", REQUEST_CODE);
                intent.putExtra("aid", viewModel.aid)
                /**
                 * 保存并传递数据
                 */
                startActivity(intent)
            }

        }


    }

    private fun signRecord(body: String = "", cookies: String = "") {
        if (alreadySign) return
        if (body.isNotEmpty()) {
            val status = body.contains("成功") || body.contains("success")
            val uid = if (cookies.isEmpty()) CacheUtils.cache["uid"] ?: "" else findUID(cookies)
            record(uid, status = status)
        } else {
            val uid = if (cookies.isEmpty()) CacheUtils.cache["uid"] ?: "" else findUID(cookies)
            record(uid, status = mStatus)
        }

    }

    private fun record(uid: String, status: Boolean) {
        val courseName = viewModel.courseName
        val statusName = if (status) "成功" else "失败"
        val username = if (remark.isNotEmpty()) "$uid - ($remark)" else uid
        SignRecorder.record(applicationContext, username, courseName!!, statusName)
    }

    private suspend fun analysisAndStartSign(aid: String) {
        viewModel.analysis(URL.getAnalysisPath(aid))
    }

    private fun initObserver() {
        // 签到
        viewModel.signLiveData.observe(this) {
            lifecycleScope.launch(Dispatchers.IO) {
                val data = it.data?.body?.string()
                withContext(Dispatchers.Main) {
                    /**
                     * 回到TaskActivity
                     */
                    if (data == "不在可签到范围内") {
                        finish()
                    } else {
                        /**
                         * 回到TaskFragment
                         */
                        if (data!!.contains("success")) {
                            mStatus = true
                            ToastUtil.show(applicationContext, "签到已成功")

                            signRecord(data)
                            /**
                             * 开始代签
                             */
                            // 开始代签
                            showLoadingView()
                            startSignTogether(data)
//                            startActivity(intent)

                        } else {
                            mStatus = false
                            ToastUtil.show(applicationContext, "签到失败")
//                            val intent = Intent(applicationContext, MainActivity::class.java)
                            signRecord(data)
//                            startActivity(intent)
                        }

                        /**
                         * 回去缺少网络请求
                         */

                    }
                }
            }
        }
        viewModel.analysisLiveData.observe(this) {
            lifecycleScope.launch(Dispatchers.IO) {
                val data = it.data?.body?.string()
                val analysis2Code = data?.substringAfter("code='+'")?.substringBefore("'") ?: ""
                viewModel.analysis2(URL.getAnalysis2Path(analysis2Code))
            }

        }
        viewModel.preSignLiveData.observe(this) {
            lifecycleScope.launch(Dispatchers.IO) {
                it.data?.body?.string()?.let {
                    if (preSignOther) {
                        /**
                         * 代签无需进行操作
                         */
                    } else {
                        viewModel.preSignWebGet(it,
                            onSuccess = { preWeb ->
                                binding?.mainKeywords?.apply {
                                    hint = if (preWeb.locationText?.isNotEmpty() == true) {
                                        preWeb.locationText
                                    } else {
                                        "老师未设置位置,请点击搜索"
                                    }
                                }
                                if (!preWeb.latitude.isNullOrEmpty()&& preWeb.latitude!="-1"&& !preWeb.longitude.isNullOrEmpty()&&preWeb.longitude!="-1") {
                                    viewModel.currentTipPoint =
                                        LatLng(
                                            preWeb.latitude.toDouble(),
                                            preWeb.longitude.toDouble()
                                        )
                                    addLatLngMarker(
                                        LatLng(
                                            viewModel.currentTipPoint.latitude,
                                            viewModel.currentTipPoint.longitude
                                        ), default = true
                                    )
                                    viewModel.default_Sign_Location = preWeb.locationText
                                    viewModel.default_Sign_Location = preWeb.locationText
                                    viewModel.statuscontent = preWeb.statusContent
                                    viewModel.default_Sign_Lating =
                                        LatLng(
                                            preWeb.latitude.toDouble(),
                                            preWeb.longitude.toDouble()
                                        )
                                    /**
                                     * 老师未设置位置 设置提醒
                                     */
                                    if (preWeb.locationText?.isEmpty() == true && preWeb.statusContent != "签到成功") {
                                        ToastUtil.show(
                                            applicationContext,
                                            "老师未设置位置，默认位置为自己位置"
                                        )
                                        viewModel.default_Sign_Lating = viewModel.default_My_Lating
                                    } else if (preWeb.statusContent == "签到成功") {
                                        alreadySign = true
                                    }
                                    CacheUtils.cache["default_Sign_latitude"] = preWeb.latitude
                                    CacheUtils.cache["default_Sign_longitude"] = preWeb.longitude
                                } else {
                                    val lat = preWeb.html.getElementById("latitude")?.`val`() ?: ""
                                    val long =
                                        preWeb.html.getElementById("longitude")?.`val`() ?: ""
                                    if (lat != "-1" && long !="-1") {
                                        viewModel.currentTipPoint =
                                            LatLng(lat.toDouble(), lat.toDouble())
                                        addLatLngMarker(
                                            LatLng(lat.toDouble(), long.toDouble()),
                                            default = true
                                        )
                                        viewModel.default_Sign_Lating =
                                            LatLng(lat.toDouble(), lat.toDouble())
                                        viewModel.default_Sign_Location = preWeb.locationText
                                        viewModel.statuscontent = preWeb.statusContent
                                        if (preWeb.locationText?.isEmpty() == true && preWeb.statusContent != "签到成功") {
                                            ToastUtil.show(
                                                applicationContext,
                                                "老师未设置位置，默认位置为自己位置"
                                            )
                                            viewModel.default_Sign_Lating =
                                                viewModel.default_My_Lating
                                        } else if (preWeb.statusContent == "签到成功") {
                                            alreadySign = true
                                        }
                                        CacheUtils.cache["default_Sign_latitude"] = lat
                                        CacheUtils.cache["default_Sign_longitude"] = long
                                    }
                                }

                            })
                        preSignOther = false
                    }


                }


            }
        }
        // 尝试登录
        viewModel.loginLiveData.observe(this) { response ->
            val data = response.data ?: return@observe
            lifecycleScope.launch(Dispatchers.IO) {
                val body = data.body?.string()?.safeParseToJson()
                val headers = data.headers
                cookies = headers.values("Set-Cookie").toString()
                if (body?.getBoolean("status") == true) {
                    signWith(viewModel.aid, cookies)
                }
            }
        }
        // 绑定签到
        viewModel.signTogetherLiveData.observe(this) { response ->
            val data = response.data ?: return@observe
            lifecycleScope.launch(Dispatchers.IO) {
                val body = data.body?.string() ?: ""
                signRecord(body, cookies)
                if (alreadySignCount < (otherSignUsers?.size ?: 0)) {
                    val itemUser =
                        otherSignUsers?.getOrNull(alreadySignCount) as? JSONObject ?: JSONObject()
                    remark = itemUser.getStringExt(com.cofbro.qian.utils.Constants.Account.REMARK)
                    tryLogin(itemUser)
                    alreadySignCount++
                } else {
                    withContext(Dispatchers.Main) {
                        hideLoadingView()
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        startActivity(intent)
                    }
                }

            }
        }
    }

    private fun showLoadingView() {
        if (loadingDialog == null) {
            loadingDialog = FullScreenDialog(this)
        }
        loadingDialog?.setCancelable(false)
        loadingDialog?.show()
    }

    private fun hideLoadingView() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }

    private suspend fun signWith(id: String, cookies: String) {
        viewModel.analysisForSignTogether(URL.getAnalysisPath(id),
            cookies,
            onSuccess = {
                lifecycleScope.launch(Dispatchers.IO) {
                    val data = it.body?.string()
                    val analysis2Code = data?.substringAfter("code='+'")?.substringBefore("'") ?: ""
                    viewModel.analysis2(URL.getAnalysis2Path(analysis2Code), cookies)
                    delay(200)
                    val uid = findUID(cookies)
                    val tempSignpre = viewModel.preUrl.replace(viewModel.uid, uid)
                    viewModel.preSign(tempSignpre, cookies)
                    /*
                    拼接URL
                     */
                    signTogether(cookies)
                }
            },
            onFailure = { msg ->
                ToastUtils.show(msg)
            }
        )
    }

    private fun sign(url: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            /**
             * 绑定签到  判断
             */
//            analysisAndStartSign(viewModel.aid)
            viewModel.sign(url)
        }

    }

    private suspend fun signTogether(cookies: String) {
        val uid = findUID(cookies)
        val tempUrl = viewModel.signUrl.replace(viewModel.uid, uid)
        viewModel.signTogether(tempUrl, cookies)
    }

    private suspend fun startSignTogether(data: String) {
        // 开始代签
        val signWith = applicationContext.getBySp("signWith")?.toBoolean() ?: false
        if (signWith && (data.contains("success") || data.contains("签到成功"))) {
            // 如果本账号签到成功，则开始自动签到其他绑定账号
            signWithAccounts()
            preSignOther = true
        } else {
            val intent = Intent(applicationContext, MainActivity::class.java)
            preSignOther = false
            startActivity(intent)
        }
        this

    }

    private suspend fun signWithAccounts() {
        withContext(Dispatchers.IO) {
            val data = AccountManager.loadAllAccountData(applicationContext)
            otherSignUsers = data.getJSONArray(com.cofbro.qian.utils.Constants.Account.USERS)
            val firstUser = otherSignUsers?.getOrNull(0) as? JSONObject
            if (firstUser != null) {
                alreadySignCount++
                remark = firstUser.getStringExt(com.cofbro.qian.utils.Constants.Account.REMARK)
                tryLogin(firstUser)
            }
        }
    }

    private fun tryLogin(user: JSONObject) {
        val username = user.getStringExt(com.cofbro.qian.utils.Constants.Account.USERNAME)
        val password = user.getStringExt(com.cofbro.qian.utils.Constants.Account.PASSWORD)
        if (username.isNotEmpty() && password.isNotEmpty()) {
            viewModel.tryLogin(URL.getLoginPath(username, password))
        }
    }

    private fun findUID(cookies: String): String {
        val uid = cookies.substringAfter("UID=")
        return uid.substringBefore(";")
    }

    private fun initMap(savedInstanceState: Bundle?) {
        binding?.maps!!.onCreate(savedInstanceState)
        if (binding?.maps?.map == null) {
            setUpMap()
        }
        binding?.maps?.map?.setOnMapClickListener { latLng -> // 地图 点击 更换marker的经纬度
            binding?.maps?.map?.clear()
            addLatLngMarker(latLng, default = true)
            viewModel.currentTipPoint = LatLng(latLng.latitude,latLng.longitude)
            addLatingDefaultMarker(viewModel.default_Sign_Lating)
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
                if (tip[0].isNotEmpty()) {
                    binding?.selectButton?.visibility = View.VISIBLE
                    binding?.etLocationName?.visibility = View.VISIBLE
                    binding?.mainKeywords?.text = tip[0]
                }

                if (tip[0] != "") {
                    //binding?.cleanKeywords?.visibility = View.VISIBLE
                }
                // 获取完整的name和address
                viewModel.Tip_name = tip[0]
                viewModel.Tip_address = tip[1]
                viewModel.Tip_City = tip[5]

            }
        }
        AmapUtils.getCurrentLocationLatLng(applicationContext,
            onSuccess = { lat, lon, address ->
                viewModel.default_My_Lating =
                    LatLng(lat, lon)
                addLatingDefaultMarker(viewModel.default_My_Lating)
            },
            onError = { error ->
//                ToastUtils.show(error)
            })
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

    override fun onStop() {
        super.onStop()
        SignRecorder.writeJson(applicationContext)
    }


}
