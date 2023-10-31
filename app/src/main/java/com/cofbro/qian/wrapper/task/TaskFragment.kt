package com.cofbro.qian.wrapper.task

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.fastjson.JSONObject
import com.cofbro.hymvvmutils.base.BaseFragment
import com.cofbro.hymvvmutils.base.getBySp
import com.cofbro.qian.data.URL
import com.cofbro.qian.databinding.FragmentTaskBinding
import com.cofbro.qian.mapsetting.MapActivity
import com.cofbro.qian.photo.PhotoSignActivity
import com.cofbro.qian.scan.ScanActivity
import com.cofbro.qian.utils.AccountManager
import com.cofbro.qian.utils.CacheUtils
import com.cofbro.qian.utils.Constants
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

/**
 * @author cofbro
 * 2023.10.6
 */
class TaskFragment : BaseFragment<TaskViewModel, FragmentTaskBinding>() {
    // 代签账号的uid
    private var uidTogether = ""

    // 代签账号的cookie
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
            val id = result?.substringAfter("id=")
            signWithCamera(id)
        }
    }

    private fun initArgs() {
        activity = requireActivity() as WrapperActivity
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
                    hideLoadingView()
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
                    hideLoadingView()
                }
                val data = it.data?.body?.string()
                signTypeData = data?.safeParseToJson()
                // 签到类型获取后，开始签到
                realSign(signTypeData)
            }

        }

        viewModel.signCodeLiveData.observe(this) {
            lifecycleScope.launch(Dispatchers.IO) {
                if (it.data == null) {
                    hideLoadingView()
                }
                val data = it.data?.body?.string()
            }
        }

        // 签到
        viewModel.signLiveData.observe(this) {
            lifecycleScope.launch(Dispatchers.IO) {
                val data = it.data?.body?.string() ?: ""
                withContext(Dispatchers.Main) {
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
                if (body?.getBoolean("status") == true) {
                    val list: List<String> = headers.values("Set-Cookie")
                    cookies = list.toString()
                    signWith(uidTogether, id, code)
                }
            }
        }

        // 绑定签到
        viewModel.signTogetherLiveData.observe(this) {
            lifecycleScope.launch(Dispatchers.IO) {
                val data = it.data?.body?.string() ?: ""
            }
        }
    }

    private suspend fun startSignTogether(data: String) {
        // 开始代签
        codeDialog?.let {
            val signWith = requireActivity().getBySp("signWith")?.toBoolean() ?: false
            if (signWith && (data.contains("success") || data.contains("签到成功"))) {
                // 如果本账号签到成功，则开始自动签到其他绑定账号
                signWithAccounts()
            }
        }
    }

    private fun responseUI(data: String) {
        hideLoadingView()
        data.showSignResult()
        // 清除dialog
        gestureInputDialog?.dismiss()
        codeDialog?.dismiss()
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
            // 签到码签到
            Constants.SIGN.SIGN_CODE -> {
                withContext(Dispatchers.Main) {
                    hideLoadingView()
                    showCodeDialog(id)
                }
            }

            // 手势签到
            Constants.SIGN.GESTURE -> {
                withContext(Dispatchers.Main) {
                    hideLoadingView()
                    showGestureDialog(id)
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
        //val uid = CacheUtils.cache["uid"] ?: ""
        // 暂时不用在url中拼接uid
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.sign(URL.getSignWithCameraPath(id))
        }
    }

    private suspend fun signNormally(aid: String, signCode: String = "") {
        activity?.let {
            viewModel.sign(URL.getNormalSignPath(it.courseId, it.classId, aid, signCode))
        }
    }

    private suspend fun signTogether(aid: String, signCode: String = "") {
        activity?.let {
            viewModel.signTogether(
                URL.getNormalSignPath(it.courseId, it.classId, aid, signCode),
                cookies
            )
        }
    }

    private suspend fun signLoction(api: String) {
        activity?.let {
            viewModel.sign(api)
        }
    }

    /**
     * 服务端现已不下发签到码，客户端发起请求后由服务端校验，
     * 因此暂时没有方法能够拿到密码
     */
    private suspend fun signWithSignCode(aid: String) {
        viewModel.getSignCode(URL.getSignCodePath(aid))
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
        }
        loadingDialog?.setCancelable(false)
        loadingDialog?.show()
    }

    private fun hideLoadingView() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }

    private suspend fun showCodeDialog(id: String) {
        codeDialog = CodingDialog(requireContext()).apply {
            setCancelable(false)
            setPositiveClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    code = it
                    viewModel.preSign(preSignUrl)
                    // 签到
                    signNormally(id, code)
                }
            }

            setNegativeClickListener {
                dismiss()
            }
            show()
        }
    }

    private suspend fun showGestureDialog(id: String) {
        GestureInputDialog(requireContext()).apply {
            show()
            setInputEndListener { inputPwd ->
                // 由于回调出来的密码是 Int数组，需遍历转成字符串
                var inputCode = ""
                inputPwd.forEach {
                    inputCode += it.toString()
                }
                lifecycleScope.launch(Dispatchers.IO) {
                    code = inputCode
                    viewModel.preSign(preSignUrl)
                    // 签到
                    signNormally(id, code)
                }
            }
        }
    }

    private suspend fun signWithAccounts() {
        val data = AccountManager.loadAllAccountData(requireContext())
        val users = data.getJSONArray("users")
        users?.let {
            it.forEach { item ->
                val user = item as? JSONObject ?: JSONObject()
                tryLogin(user)
                delay(1200)
            }
        }

    }

    private fun tryLogin(user: JSONObject) {
        uidTogether = user.getStringExt("uid")
        val username = user.getStringExt("username")
        val password = user.getStringExt("password")
        if (username.isNotEmpty() && password.isNotEmpty()) {
            viewModel.tryLogin(URL.getLoginPath(username, password))
        }
    }

    private suspend fun signWith(uid: String, id: String, code: String = "") {
        val signWithPreSign = preSignUrl.substringBefore("uid=") + "uid=$uid"
        viewModel.preSign(signWithPreSign)
        signTogether(id, code)
    }
}