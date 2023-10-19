package com.cofbro.qian.account.manager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cofbro.hymvvmutils.base.BaseActivity
import com.cofbro.qian.R
import com.cofbro.qian.databinding.ActivityAccountmanagerBinding
import com.cofbro.qian.databinding.ActivityLoginBinding
import com.cofbro.qian.login.LoginActivity
import com.cofbro.qian.login.LoginViewModel

class AccountManagerActivity :  BaseActivity<AccountManagerViewModel, ActivityAccountmanagerBinding>(){
    override fun onActivityCreated(savedInstanceState: Bundle?) {

    }
    private fun initArgs(){

    }
    private fun initViewClick(){
        binding?.addaccount?.setOnClickListener {
            /**
             * 跳转登录界面，并保存信息回到manager 并判定是否为拓展账号
             */
            val intent = Intent(this,LoginActivity(extents = true)::class.java)
            startActivity(intent)
        }
    }

}