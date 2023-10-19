package com.cofbro.qian.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.alibaba.fastjson.JSONObject
import com.cofbro.hymvvmutils.base.BaseActivity
import com.cofbro.hymvvmutils.base.getBySp
import com.cofbro.hymvvmutils.base.saveUsedSp
import com.cofbro.qian.account.manager.AccountManagerActivity
import com.cofbro.qian.data.URL
import com.cofbro.qian.databinding.ActivityLoginBinding
import com.cofbro.qian.main.MainActivity
import com.cofbro.qian.utils.CacheUtils
import com.cofbro.qian.utils.safeParseToJson
import com.hjq.toast.ToastUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity(val extents: Boolean =false) : BaseActivity<LoginViewModel, ActivityLoginBinding>() {
    private var mUsername: String? = null
    private var mPassword: String? = null
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        /**
         * 常规登录
         */
        if (!extents){
            tryLogin()
            initObserver()
            initEvent()
        }else{
            /**
             * 拓展登录
             */
            autoClearFocus()
            login()
        }



    }

    private fun tryLogin() {
        val username = getBySp("username")
        val password = getBySp("password")
        if (!username.isNullOrEmpty() && !password.isNullOrEmpty()) {
            viewModel.login(URL.getLoginPath(username, password))
        }
    }

    private fun initObserver() {
        viewModel.loginLiveData.observe(this) {
            val data = it.data ?: return@observe
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
                    if(!extents){
                        /**
                         * 正常登陆
                         */
                        CacheUtils.cache["uid"] = uid ?: ""
                        CacheUtils.cache["cookies"] = cookies.toString()
                        CacheUtils.cache["fid"] = fid ?: ""
                        // 保存用户信息
                        saveUserInfo()
                        lifecycleScope.launch(Dispatchers.Main) {
                            ToastUtils.show("登录成功！")
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }else{
                        /**
                         * 拓展登录
                         */
                        lifecycleScope.launch(Dispatchers.Main) {

                            ToastUtils.show("添加用户成功！")
                            val intent = Intent(this@LoginActivity, AccountManagerActivity::class.java)
                            /**
                             * 传递用户信息？是否需要CashUtil
                             */
                            startActivity(intent)
                            finish()
                        }

                    }

                } else {
                    if(!extents){
                        ToastUtils.show("账号或密码错误!")
                    }else{
                        ToastUtils.show("账号或密码错误!添加用户失败")
                    }

                }
            }
        }
    }

    private fun saveUserInfo() {
        if (!mUsername.isNullOrEmpty() && !mPassword.isNullOrEmpty()&&!extents) {
            saveUsedSp("username", mUsername!!)
            saveUsedSp("password", mPassword!!)
        }

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