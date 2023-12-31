package com.cofbro.qian.login

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.cofbro.hymvvmutils.base.BaseActivity
import com.cofbro.hymvvmutils.base.getBySp
import com.cofbro.hymvvmutils.base.saveUsedSp
import com.cofbro.qian.data.URL
import com.cofbro.qian.databinding.ActivityLoginBinding
import com.cofbro.qian.login.sms.SMSActivity
import com.cofbro.qian.main.MainActivity
import com.cofbro.qian.utils.CacheUtils
import com.cofbro.qian.utils.Constants
import com.cofbro.qian.utils.safeParseToJson
import com.hjq.toast.ToastUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class LoginActivity : BaseActivity<LoginViewModel, ActivityLoginBinding>() {
    private var mUsername: String? = null
    private var mPassword: String? = null
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        tryLogin()
        initObserver()
        initEvent()
        autoClearFocus()
        login()
    }

    private fun tryLogin() {

        val username = getBySp("username")
        val password = getBySp("password")
        if (!username.isNullOrEmpty() && !password.isNullOrEmpty()) {
            viewModel.login(URL.getLoginPath(username, password))
        }
    }

    private fun initObserver() {
        viewModel.loginLiveData.observe(this) { response ->
            val data = response.data ?: return@observe
            lifecycleScope.launch(Dispatchers.IO) {
                val body = data.body?.string()?.safeParseToJson()
                val headers = data.headers
                if (body?.getBoolean("status") == true) {
                    val list: List<String> = headers.values("Set-Cookie")
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
                    saveUserInfo()
                    CacheUtils.cache[Constants.Login.UID] = uid ?: ""
                    CacheUtils.cache[Constants.Login.COOKIES] = cookies.toString()
                    CacheUtils.cache[Constants.Login.FID] = fid ?: ""
                    lifecycleScope.launch(Dispatchers.Main) {
                        ToastUtils.show("登录成功！")
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    ToastUtils.show("账号或密码错误!")
                }
            }
        }
    }

    private fun saveUserInfo() {
        if (!mUsername.isNullOrEmpty() && !mPassword.isNullOrEmpty()) {
            saveUsedSp("username", mUsername!!)
            saveUsedSp("password", mPassword!!)
        }

    }

    private fun initEvent() {
        // 清除输入框焦点
        autoClearFocus()
        // 登录
        login()
        // 手机号登录
        phoneLogin()
    }

    private fun phoneLogin() {
        binding?.tvPhoneLogin?.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                toVerifyCodeActivity()
            }
        }
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

    private fun toVerifyCodeActivity() {
        val phoneNumber = binding?.ipUsername?.getTextString() ?: ""
        if (phoneNumber.length != 11) {
            ToastUtils.show("请输入正确的手机号")
            return
        }
        val intent = Intent(this, SMSActivity::class.java)
        intent.putExtra("phoneNumber", phoneNumber)
        CacheUtils.activities[Constants.Cache.LOGIN_ACTIVITY] = this
        startActivity(intent)
    }

    private fun autoClearFocus() {
        binding?.root!!.setOnClickListener {
            it.clearFocus()
        }
    }
}
