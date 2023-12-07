package com.cofbro.qian.wrapper.task

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.cofbro.hymvvmutils.base.BaseFragment
import com.cofbro.hymvvmutils.base.getBySp
import com.cofbro.qian.data.URL
import com.cofbro.qian.databinding.FragmentTaskBinding
import com.cofbro.qian.mapsetting.MapActivity
import com.cofbro.qian.photo.PhotoSignActivity
import com.cofbro.qian.scan.ScanActivity
import com.cofbro.qian.utils.AccountManager
import com.cofbro.qian.utils.AmapUtils
import com.cofbro.qian.utils.CacheUtils
import com.cofbro.qian.utils.Constants
import com.cofbro.qian.utils.HtmlParser
import com.cofbro.qian.utils.SignRecorder
import com.cofbro.qian.utils.getStringExt
import com.cofbro.qian.utils.safeParseToJson
import com.cofbro.qian.utils.showSignResult
import com.cofbro.qian.view.CodingDialog
import com.cofbro.qian.view.FullScreenDialog
import com.cofbro.qian.view.GestureInputDialog
import com.cofbro.qian.wrapper.WrapperActivity
import com.hjq.toast.ToastUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URLEncoder

/**
 * @author cofbro
 * 2023.10.6
 */
class TaskFragment : BaseFragment<TaskViewModel, FragmentTaskBinding>() {
    private var latitude = 0.0
    private var longitude = 0.0
    private var locationText = ""
    private var location = ""
    private var remark = ""
    private var alreadySignCount = 0
    private var otherSignUsers: JSONArray? = null
    private var qrCodeId = ""
    private var alreadySign = false
    private var cookies = ""

    // 签到的aid
    private var id = ""

    // 签到密码
    private var code = ""

    // 手势签到的dialog
    private var gestureInputDialog: GestureInputDialog? = null
    private var codeDialog: AlertDialog? = null
    private var activeId = ""
    private var signTypeData: JSONObject? = null
    private var preSignUrl = ""
    private var refreshing = false
    private val requestCode = 1
    private var activity: WrapperActivity? = null
    private var taskAdapter: TaskAdapter? = null
    private var loadingDialog: Dialog? = null

    override fun onAllViewCreated(savedInstanceState: Bundle?) {
        initArgs()
        initObserver()
        initView()
        doNetwork()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK) {
            val result = data?.getStringExtra("result")
            // SIGNIN:aid=402742574&source=15&Code=402742574&enc=548DF0246153AF088E756B59F33BF3F4
            // https://mobilelearn.chaoxing.com/widget/sign/e?id=2000072435046&c=2000072435046&enc=BC9662672047A2F2E4A607CC59762973&DB_STRATEGY=PRIMARY_KEY&STRATEGY_PARA=id
            // 这里的id包含url中的所有参数
            showLoadingView()
            val id = result?.substringAfter("id=")
            qrCodeId = id ?: ""
            lifecycleScope.launch(Dispatchers.IO) {
                analysisAndStartSign(id?.substringBefore("&") ?: "")
            }
        }
    }

    private fun initArgs() {
        activity = requireActivity() as WrapperActivity
        SignRecorder.init(requireContext())
    }

    private fun initView() {
        // rv
        binding?.rvSignTask?.apply {
            taskAdapter = TaskAdapter()
            taskAdapter?.setItemClickListener { itemData ->
                sign(itemData)
            }
            adapter = taskAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        // refresh
        binding?.rflSignTask?.apply {
            setOnRefreshListener {
                autoRefresh()
                refreshing = true
                doNetwork()
            }
        }
    }

    private fun initObserver() {
        // 活动列表
        viewModel.queryActiveTaskListLiveData.observe(this) {
            lifecycleScope.launch(Dispatchers.IO) {
                if (refreshing) {
                    refreshing = false
                    binding?.rflSignTask?.finishRefresh()
                }
                if (it.data == null) {
                    withContext(Dispatchers.Main) {
                        hideLoadingView()
                    }
                }
                val data = it.data?.body?.string()
                withContext(Dispatchers.Main) {
                    data?.safeParseToJson()?.let {
                        taskAdapter?.setData(it)
                    }
                }
            }
        }

        // 获取签到类型
        viewModel.signTypeLiveData.observe(this) {
            lifecycleScope.launch(Dispatchers.IO) {
                if (it.data == null) {
                    withContext(Dispatchers.Main) {
                        hideLoadingView()
                    }
                }
                val data = it.data?.body?.string()
                signTypeData = data?.safeParseToJson()
                // 签到类型获取后，开始签到
                realSign(signTypeData)
            }
        }

        viewModel.analysisLiveData.observe(this) {
            lifecycleScope.launch(Dispatchers.IO) {
                val data = it.data?.body?.string()
                val analysis2Code = data?.substringAfter("code='+'")?.substringBefore("'") ?: ""
                viewModel.analysis2(URL.getAnalysis2Path(analysis2Code))
                val courseId = activity?.courseId ?: ""
                val classId = activity?.classId ?: ""
                if (code.isNotEmpty()) {
                    signDirectly()
                } else if (qrCodeId.isNotEmpty()) {
                    signWithCamera(qrCodeId)
                } else {
                    getSignCode(id, classId, courseId)
                }
            }
        }

        viewModel.signCodeLiveData.observe(this) {
            lifecycleScope.launch(Dispatchers.IO) {
                if (it.data == null) {
                    withContext(Dispatchers.Main) {
                        hideLoadingView()
                    }
                } else {
                    val body = it.data?.body?.string() ?: ""
                    code = HtmlParser.parseToSignCode(body)
                    signDirectly()
                }
            }
        }

        // 签到
        viewModel.signLiveData.observe(this) {
            lifecycleScope.launch(Dispatchers.IO) {
                val data = it.data?.body?.string() ?: ""
                withContext(Dispatchers.Main) {
                    // 表示签过到，可以再次io
                    alreadySign = true
                    signRecord(data)
                    responseUI(data)
                    // 开始代签
                    startSignTogether(data)
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
                    signWith(id, code, cookies)
                }
            }
        }

        // cookie签到
        viewModel.cookieSignLiveData.observe(this) {
            lifecycleScope.launch(Dispatchers.IO) {
                cookies = it
                signWith(id, code, cookies)
            }
        }

        // 绑定签到
        // todo 修改签到逻辑
        viewModel.signTogetherLiveData.observe(this) { response ->
            val data = response.data ?: return@observe
            lifecycleScope.launch(Dispatchers.IO) {
                val body = data.body?.string() ?: ""
                signRecord(body, cookies)
                alreadySignCount++
                if (alreadySignCount < (otherSignUsers?.size ?: 0)) {
                    val itemUser =
                        otherSignUsers?.getOrNull(alreadySignCount) as? JSONObject ?: JSONObject()
                    remark = itemUser.getStringExt(Constants.Account.REMARK)
                    tryLogin(itemUser)
                } else {
                    withContext(Dispatchers.Main) {
                        hideLoadingView()
                    }
                }
            }
        }
    }

    override fun onStop() {
        SignRecorder.writeJson(requireContext())
        super.onStop()
    }

    private suspend fun startSignTogether(data: String) {
        // 开始代签
        val signWith = requireActivity().getBySp("signWith")?.toBoolean() ?: false
        if (signWith && (data.contains("success") || data.contains("签到成功"))) {
            // 如果本账号签到成功，则开始自动签到其他绑定账号
            showLoadingView()
            signWithAccounts()
        }
    }

    private suspend fun signDirectly() {
        viewModel.preSign(preSignUrl)
        viewModel.request(URL.checkSignCodePath(id, code))
        // 签到
        signNormally(id, code)
    }

    private suspend fun responseUI(data: String) {
        hideLoadingView()
        data.showSignResult()
        // 清除dialog
        codeDialog?.dismiss()
        responseGestureDialog(data)
    }

    private fun doNetwork() {
        activity?.let {
            queryAllActiveTask(it.courseId, it.classId, it.cpi)
        }
    }

    private fun queryAllActiveTask(courseId: String, classId: String, cpi: String) {
        // 查询所有活动
        val uid = CacheUtils.cache["uid"] ?: ""
        viewModel.queryActiveTaskList(URL.gatActiveTaskListPath(courseId, classId, uid, cpi))
    }

    private fun sign(itemData: JSONObject) {
        // aid
        val id = itemData.getStringExt(Constants.TaskList.ID)
        activeId = id
        // 2代表签到活动
        val type = itemData.getStringExt(Constants.TaskList.ACTIVE_TYPE)
        // 预签到地址
        preSignUrl = itemData.getStringExt(Constants.TaskList.PRE_SIGN_URL)
        // 2 -> 已结束
        val status = itemData.getStringExt(Constants.TaskList.STATUS)
        if (status == Constants.STATUS.CLOSE) {
            hideLoadingView()
            ToastUtils.show("签到已结束")
            return
        }
        showLoadingView()
        if (type == Constants.ACTIVITY.SIGN) {
            lifecycleScope.launch(Dispatchers.IO) {
                // 查询签到类型
                viewModel.findSignType(URL.getSignType(id))
            }
        }

    }

    private suspend fun realSign(itemData: JSONObject?) {
        val type = itemData?.getStringExt(Constants.SIGN.OTHER_ID)
        val ifPhoto = itemData?.getStringExt(Constants.SIGN.IF_PHOTO)
        id = itemData?.getStringExt(Constants.SIGN.ID) ?: ""
        when (type) {
            // 二维码签到
            Constants.SIGN.SCAN_QR -> {
                lifecycleScope.launch(Dispatchers.IO) {
                    // 预签到
                    viewModel.preSign(preSignUrl)
                    // 跳转扫码界面
                    toScanActivity()
                }
            }
            // 普通签到
            Constants.SIGN.NORMAl -> {
                if (ifPhoto == Constants.SIGN.PHOTO) {
                    // 预签到
                    viewModel.preSign(preSignUrl)
                    // 照片签到
                    toPhotoSignActivity(id)
                } else {
                    viewModel.preSign(preSignUrl)
                    // 签到
                    signNormally(id)
                }
            }

            // 签到码签到，手势签到
            Constants.SIGN.SIGN_CODE, Constants.SIGN.GESTURE -> {
                withContext(Dispatchers.Main) {
                    hideLoadingView()
                    showChooseSignTypeDialog(id, type)
                }
            }

            // 定位签到
            Constants.SIGN.LOCATION -> {
                lifecycleScope.launch(Dispatchers.Main) {
                    toMapActivity(activeId, preSignUrl)
                }
            }
        }
    }

    private fun toMapActivity(aid: String, preUrl: String) {
        hideLoadingView()
        val intent = Intent(requireActivity(), MapActivity::class.java)
        intent.putExtra("aid", aid)
        intent.putExtra("preUrl", preUrl)
        /*
        跳转保存数据
         */
        intent.putExtra("courseName", activity?.courseName)
        intent.putExtra("courseId", activity?.courseId)
        intent.putExtra("classId", activity?.classId)
        intent.putExtra("cpi", activity?.cpi)

        startActivity(intent)
    }

    private fun toScanActivity() {
        hideLoadingView()
        val intent = Intent(requireActivity(), ScanActivity::class.java)
        startActivityForResult(intent, requestCode)
    }

    private fun signWithCamera(id: String?) {
        if (id.isNullOrEmpty()) return
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.preSign(preSignUrl)
            locate {
                lifecycleScope.launch(Dispatchers.IO) {
                    val address =
                        "{\"result\":1,\"latitude\":$latitude,\"longitude\":$longitude,\"address\":\"$locationText\"}"
                    location = URLEncoder.encode(address, "UTF-8")
                    viewModel.sign(URL.getSignWithCameraPath(id, location))
                }
            }
        }
    }

    private fun locate(onLocated: () -> Unit) {
        AmapUtils.getCurrentLocationLatLng(requireContext(),
            onSuccess = { lat, lon, location ->
                latitude = lat
                longitude = lon
                locationText = location
                onLocated()
            }, onError = {
                ToastUtils.show(it)
            }
        )
    }

    private suspend fun signNormally(aid: String, signCode: String = "") {
        activity?.let {
            viewModel.sign(URL.getNormalSignPath(it.courseId, it.classId, aid, signCode))
        }
    }

    /**
     * 除二维码签到的代签流程入口
     */
    private suspend fun signTogether(aid: String, signCode: String = "", cookies: String) {
        activity?.let {
            viewModel.signTogether(
                URL.getNormalSignPath(it.courseId, it.classId, aid, signCode),
                cookies
            )
        }
    }

    /**
     * 二维码代签流程入口
     */
    private suspend fun signTogether(qrCodeId: String, cookies: String) {
        val uid = findUID(cookies)
        viewModel.signTogether(URL.getSignWithCameraPath(qrCodeId, location) + "&uid=$uid", cookies)
    }

    /**
     * 签到流程入口
     * @param aid activeId
     */
    private suspend fun analysisAndStartSign(aid: String) {
        viewModel.analysis(URL.getAnalysisPath(aid))
    }

    private suspend fun getSignCode(aid: String, classId: String, courseId: String) {
        viewModel.getSignCode(URL.getSignCodePath(aid, classId, courseId))
    }


    private fun toPhotoSignActivity(aid: String) {
        hideLoadingView()
        val intent = Intent(requireActivity(), PhotoSignActivity::class.java)
        intent.putExtra("aid", aid)
        startActivity(intent)
    }

    private fun showLoadingView() {
        if (loadingDialog == null) {
            loadingDialog = FullScreenDialog(requireContext())
            loadingDialog?.setCancelable(false)
            loadingDialog?.show()
        }
    }

    private fun hideLoadingView() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }

    private fun showCodeDialog(id: String) {
        codeDialog = CodingDialog(requireContext()).apply {
            setCancelable(false)
            setPositiveClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    code = it
                    analysisAndStartSign(id)
                }
            }

            setNegativeClickListener {
                dismiss()
            }
            show()
        }
    }

    private suspend fun responseGestureDialog(data: String) {
        gestureInputDialog?.let {
            if (data.contains("success") || data.contains("成功")) {
                it.setState(true)
                delay(500)
                it.dismiss()
            } else {
                it.setState(false)
                delay(500)
                it.initData()
                it.setIsTouchAble(true)
            }
        }
    }

    private fun showGestureDialog(id: String) {
        gestureInputDialog = GestureInputDialog(requireContext()).apply {
            show()
            setInputEndListener { inputPwd ->
                this.setIsTouchAble(false)
                // 由于回调出来的密码是 Int数组，需遍历转成字符串
                var inputCode = ""
                inputPwd.forEach {
                    inputCode += it.toString()
                }
                lifecycleScope.launch(Dispatchers.IO) {
                    code = inputCode
                    analysisAndStartSign(id)
                }
            }
        }
    }

    private suspend fun signWithAccounts() {
        withContext(Dispatchers.IO) {
            val data = AccountManager.loadAllAccountData(requireContext())
            val cookieSignData = AccountManager.loadAllAccountData(
                requireContext(),
                Constants.RecycleJson.COOKIE_JSON_DATA
            )
            otherSignUsers = data.getJSONArray(Constants.Account.USERS) ?: JSONArray()
            cookieSignData.getJSONArray(Constants.Account.USERS).forEach { user ->
                val timestamp =
                    (user as? JSONObject)?.getStringExt(Constants.Account.TIME)?.toLong() ?: 0L
                if (System.currentTimeMillis() - timestamp <= 24 * 60 * 60 * 1000) {
                    otherSignUsers?.add(user)
                }
            }
            val firstUser = otherSignUsers?.getOrNull(0) as? JSONObject
            if (firstUser != null) {
                remark = firstUser.getStringExt(Constants.Account.REMARK)
                tryLogin(firstUser)
            }
        }
    }

    private fun signRecord(body: String, cookies: String = "") {
        if (!alreadySign) return
        val uid = if (cookies.isEmpty()) CacheUtils.cache["uid"] ?: "" else findUID(cookies)
        val status = body.contains("成功") || body.contains("success")
        record(uid, status)
    }

    private fun record(uid: String, status: Boolean) {
        val courseName = activity?.courseName ?: ""
        val statusName = if (status) "成功" else "失败"
        val username = if (remark.isNotEmpty()) "$uid - ($remark)" else uid
        SignRecorder.record(requireContext(), username, courseName, statusName)
    }

    private fun tryLogin(user: JSONObject) {
        val username = user.getStringExt(Constants.Account.USERNAME)
        val password = user.getStringExt(Constants.Account.PASSWORD)
        val cookieSign = user.getStringExt(Constants.Account.COOKIE)
        if (username.isNotEmpty() && password.isNotEmpty()) {
            viewModel.tryLogin(URL.getLoginPath(username, password))
        } else {
            viewModel.tryLoginWithCookies(cookieSign)
        }
    }

    private suspend fun signWith(id: String, code: String = "", cookies: String) {
        viewModel.analysisForSignTogether(URL.getAnalysisPath(id),
            cookies,
            onSuccess = {
                lifecycleScope.launch(Dispatchers.IO) {
                    val data = it.body?.string()
                    val analysis2Code = data?.substringAfter("code='+'")?.substringBefore("'") ?: ""
                    viewModel.analysis2(URL.getAnalysis2Path(analysis2Code), cookies)
                    delay(200)
                    val uid = findUID(cookies)
                    val signWithPreSign = preSignUrl.substringBefore("uid=") + "uid=$uid"
                    viewModel.preSign(signWithPreSign, cookies)
                    if (qrCodeId.isEmpty()) {
                        viewModel.request(URL.checkSignCodePath(id, code), cookies)
                        signTogether(id, code, cookies)
                    } else {
                        signTogether(qrCodeId, cookies)
                    }
                }
            },
            onFailure = { msg ->
                ToastUtils.show(msg)
            }
        )
    }

    private fun findUID(cookies: String): String {
        val uid = cookies.substringAfter("UID=")
        return uid.substringBefore(";")
    }

    private suspend fun showChooseSignTypeDialog(aid: String, type: String) {
        val fragment = SignTypeFragment().apply {
            setOnClickSignDirectlyListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    analysisAndStartSign(aid)
                }
                dismiss()
            }

            setOnClickSignWithCodeListener {
                if (type == Constants.SIGN.GESTURE) {
                    showGestureDialog(aid)
                } else {
                    showCodeDialog(aid)
                }
                dismiss()
            }
        }
        fragment.show(requireActivity().supportFragmentManager, "SignTypeFragment")
    }
}