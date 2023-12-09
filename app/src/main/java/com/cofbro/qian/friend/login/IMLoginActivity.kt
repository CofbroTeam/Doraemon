package com.cofbro.qian.friend.login

import android.content.Intent
import android.os.Bundle
import com.cofbro.hymvvmutils.base.BaseActivity
import com.cofbro.hymvvmutils.base.saveUsedSp
import com.cofbro.qian.databinding.ActivityImLoginBinding
import com.cofbro.qian.friend.im.IMClientUtils
import com.hjq.toast.ToastUtils

class IMLoginActivity : BaseActivity<IMLoginViewModel, ActivityImLoginBinding>() {
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        initEvent()
    }

    private fun initEvent() {
        binding?.tvImLogin?.setOnClickListener {
            loginIM()
        }
    }

    private fun loginIM() {
        val username = binding?.etImUsername?.text.toString()
        val password = binding?.etImPassword?.text.toString()
        if (username.isNotEmpty() && password.isNotEmpty()) {
            IMClientUtils.loginIM(username, password,
                onSuccess = {
                    setResult()
                    saveAccount(username, password)
                    finish()
                },
                onError = {
                    ToastUtils.show(it)
                }
            )
        }
    }

    private fun saveAccount(username: String, password: String) {
        saveUsedSp("account", username)
        saveUsedSp("account_password", password)
    }

    private fun setResult() {
        val intent = Intent()
        intent.putExtra("login", true)
        setResult(RESULT_OK, intent)
        ToastUtils.show("登录成功")
    }


}