package com.cofbro.qian.login.sms

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup.MarginLayoutParams
import androidx.lifecycle.lifecycleScope
import com.alibaba.fastjson.JSONObject
import com.cofbro.hymvvmutils.base.BaseActivity
import com.cofbro.qian.data.URL
import com.cofbro.qian.databinding.ActivitySmsBinding
import com.cofbro.qian.main.MainActivity
import com.cofbro.qian.utils.CacheUtils
import com.cofbro.qian.utils.PhoneLoginUtil
import com.cofbro.qian.utils.dp2px
import com.cofbro.qian.utils.getStatusBarHeight
import com.cofbro.qian.utils.getStringExt
import com.cofbro.qian.utils.safeParseToJson
import com.hjq.toast.ToastUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask

class SMSActivity : BaseActivity<SMSViewModel, ActivitySmsBinding>() {
    private var phoneNumber = ""
    private var timer: Timer? = null
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        initArgs()
        initObserver()
        initView()
        initEvent()
        doNetwork()
    }

    private fun initEvent() {
        binding?.tvCodeResend?.setOnClickListener {
            sendSMS()
        }
    }

    override fun onDestroy() {
        cancelSMSTimer()
        super.onDestroy()
    }

    private fun initObserver() {
        // 发送短信
        viewModel.smsSendLiveData.observe(this) {
            lifecycleScope.launch(Dispatchers.IO) {
                val data = it.data?.body?.string()?.safeParseToJson() ?: JSONObject()
                val status = data.getStringExt("status")
                if (status == "true") {
                    responseSuccessfully()
                }
            }
        }

        // 登录
        viewModel.loginLiveData.observe(this) { response ->
            val data = response.data ?: return@observe
            lifecycleScope.launch(Dispatchers.IO) {
                val body = data.body?.string()?.safeParseToJson() ?: JSONObject()
                if (body.getStringExt("status") == "true") {
                    val list: List<String> = data.headers.values("Set-Cookie")
                    val cookies = StringBuilder()
                    var uid: String? = null
                    var fid: String? = null
                    if (list.isNotEmpty()) {
                        for (i in list.indices) {
                            val temp = list[i].split(";".toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray()[0]
                            cookies.append(temp).append(";")
                            if (temp.startsWith("UID")) uid = temp.substring(4)
                            //if (temp.startsWith("JSESSIONID")) continue
                            if (temp.startsWith("fid")) fid = temp.substring(4)
                        }
                    } else {
                        ToastUtils.show("Cookies获取失败!")
                    }
                    CacheUtils.cache["uid"] = uid ?: ""
                    CacheUtils.cache["cookies"] = cookies.toString()
                    CacheUtils.cache["fid"] = fid ?: ""
                    lifecycleScope.launch(Dispatchers.Main) {
                       toMainActivity()
                    }
                }
            }
        }
    }

    private fun doNetwork() {
        sendSMS()
    }

    private fun saveCookies(cookies: String) {
        val uid = cookies.substringAfter("UID=").substringBefore(";")
        val fid = cookies.substringAfter("fid=").substringBefore(";")
        if (uid.isNotEmpty() && fid.isNotEmpty()) {
            CacheUtils.cache["uid"] = uid
            CacheUtils.cache["cookies"] = cookies
            CacheUtils.cache["fid"] = fid
        }
    }

    private fun toMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        CacheUtils.activities["LoginActivity"]?.finish()
        finish()
    }

    private fun responseSuccessfully() {
        ToastUtils.show("验证码发送成功！")
        startSmsTimer()
    }

    private fun startSmsTimer() {
        var seconds = 60
        timer = Timer()
        timer?.apply {
            schedule(object : TimerTask() {
                override fun run() {
                    runOnUiThread {
                        seconds--
                        setSMSTips(seconds)
                        if (seconds <= 0) {
                            cancelSMSTimer()
                        }
                    }
                }
            }, 0, 1000)
        }
    }

    private fun sendSMS() {
        if (phoneNumber.isNotEmpty()) {
            val smsBody = PhoneLoginUtil.getSendSMSBody(phoneNumber)
            viewModel.sendSMS(URL.getSendCaptchaUrl(), smsBody)
        }
    }

    private fun login(code: String) {
        val loginInfo = PhoneLoginUtil.chaoXingHexCipher(username = phoneNumber, code = code)
        val body = PhoneLoginUtil.getLoginBody(loginInfo ?: "", true)
        viewModel.login(URL.getLoginWithSmsUrl(), body)
    }

    private fun cancelSMSTimer() {
        timer?.let {
            it.cancel()
            timer = null
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setSMSTips(seconds: Int) {
        if (seconds <= 0) {
            binding?.tvCodeResend?.text = "重新发送"
            binding?.tvCodeResend?.isClickable = true
        } else {
            binding?.tvCodeResend?.text = "${seconds}秒后 重发验证码"
            binding?.tvCodeResend?.isClickable = false
        }
    }

    private fun initArgs() {
        phoneNumber = intent.getStringExtra("phoneNumber") ?: ""
    }

    private fun initView() {
        initToolBar()
        initPhoneNumber()
        initVerifyCodeView()
    }

    private fun initVerifyCodeView() {
        binding?.verifyCodeView?.setCodeCallback {
            login(it)
        }
    }

    private fun initPhoneNumber() {
        binding?.tvPhoneNumber?.text = phoneNumber
    }

    private fun initToolBar() {
        val height = getStatusBarHeight(this) + dp2px(this, 5)
        val params = binding?.ivBack?.layoutParams as MarginLayoutParams
        params.topMargin = height
        binding?.ivBack?.layoutParams = params
    }
}