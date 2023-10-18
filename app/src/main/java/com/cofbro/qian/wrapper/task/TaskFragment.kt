package com.cofbro.qian.wrapper.task

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.fastjson.JSONObject
import com.cofbro.hymvvmutils.base.BaseFragment
import com.cofbro.qian.data.URL
import com.cofbro.qian.databinding.FragmentTaskBinding
import com.cofbro.qian.mapsetting.MapActivity
import com.cofbro.qian.photo.PhotoSignActivity
import com.cofbro.qian.scan.ScanActivity
import com.cofbro.qian.utils.CacheUtils
import com.cofbro.qian.utils.Constants
import com.cofbro.qian.utils.getStringExt
import com.cofbro.qian.utils.safeParseToJson
import com.cofbro.qian.utils.showSignResult
import com.cofbro.qian.view.FullScreenDialog
import com.cofbro.qian.wrapper.WrapperActivity
import com.hjq.toast.ToastUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * @author cofbro
 * 2023.10.6
 */
class TaskFragment : BaseFragment<TaskViewModel, FragmentTaskBinding>() {
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
            if (result != null) {
                Log.v("LOG_RESULT:", result)
            }
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
                val data = it.data?.body?.string()
                withContext(Dispatchers.Main) {
                    hideLoadingView()
                    data?.showSignResult()
                }
            }
        }
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
        Log.v("sign_task", URL.gatActiveTaskListPath(courseId, classId, uid, cpi))
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
        val id = itemData?.getStringExt(Constants.SIGN.ID) ?: ""
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
            // 手势签到，签到码签到
            Constants.SIGN.GESTURE, Constants.SIGN.SIGN_CODE -> {
                viewModel.preSign(preSignUrl)
                signNormally(id)
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

    private suspend fun signNormally(aid: String) {
        // https://mobilelearn.chaoxing.com/widget/sign/pcStuSignController/preSign?activeId=2000072607796
        activity?.let {
            viewModel.sign(URL.getNormalSignPath(it.courseId, it.classId, aid))
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
}