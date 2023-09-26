package com.cofbro.qian.login

import android.os.Bundle
import com.alibaba.fastjson.JSONObject
import com.cofbro.hymvvmutils.base.BaseActivity
import com.cofbro.hymvvmutils.base.saveUsedSp
import com.cofbro.qian.data.URL
import com.cofbro.qian.databinding.ActivityLoginBinding
import com.cofbro.qian.utils.CacheUtils
import com.hjq.toast.ToastUtils

class LoginActivity : BaseActivity<LoginViewModel, ActivityLoginBinding>() {
    private var mUsername: String? = null
    private var mPassword: String? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        initObserver()
        initEvent()
    }

    private fun initObserver() {
        viewModel.loginLiveData.observe(this) {
            val data = it.data ?: return@observe
            val body = JSONObject.parseObject(data.body?.string())
            val headers = data.headers
            if (body.getBoolean("status")) {
                val list: List<String> = headers.values("Set-Cookie")
                val cookies = StringBuilder()
                var uid: String? = null
                if (list.isNotEmpty()) {
                    for (i in list.indices) {
                        val temp = list[i].split(";".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()[0]
                        if (temp.startsWith("UID")) uid = temp.substring(4)
                        if (temp.startsWith("JSESSIONID")) continue
                        cookies.append(temp).append(";")
                    }
                } else {
                    ToastUtils.show("Cookies获取失败!")
                }
                CacheUtils.cache["uid"] = uid ?: ""
                CacheUtils.cache["cookies"] = cookies.toString()

                // 保存用户信息
                saveUserInfo()
                ToastUtils.show("登录成功！")
            }
        }
    }

    private fun saveUserInfo() {
        saveUsedSp("username", mUsername ?: "")
        saveUsedSp("password", mPassword ?: "")
    }

    private fun initEvent() {
        // 清除输入框焦点
        autoClearFocus()
        // 登录
        login()
    }

    private fun login() {
        binding?.tvLogin?.setOnClickListener {
            mUsername = binding?.ipUsername?.getTextString()
            mPassword = binding?.ipPassword?.getTextString()

            if (!mUsername.isNullOrEmpty() && !mPassword.isNullOrEmpty()) {
                viewModel.login(URL.getLoginPath(mUsername!!, mPassword!!))
            }
        }
    }

    private fun autoClearFocus() {
        binding?.root!!.setOnClickListener {
            it.clearFocus()
        }
    }
}