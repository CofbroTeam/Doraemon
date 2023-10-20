package com.cofbro.qian.account.manager

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.fastjson.JSONArray
import com.cofbro.hymvvmutils.base.BaseActivity
import com.cofbro.hymvvmutils.base.saveUsedSp
import com.cofbro.qian.account.adapter.AccountsAdpater
import com.cofbro.qian.databinding.ActivityAccountmanagerBinding
import com.cofbro.qian.login.LoginActivity
import com.cofbro.qian.mapsetting.util.ToastUtil
import com.cofbro.qian.profile.LogoutDialog
import com.cofbro.qian.utils.CacheUtils
import com.cofbro.qian.utils.getJsonArraySp
import com.cofbro.qian.utils.saveJsonArraySp
import com.google.gson.JsonArray

/**
 * 关联账号，实现一起签到
 */
class AccountManagerActivity :  BaseActivity<AccountManagerViewModel, ActivityAccountmanagerBinding>(){
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        preGetUserLists()
        initArgs()
        initView()
        initViewClick()
    }
    private fun preGetUserLists(){
        val userLists = getJsonArraySp("userLists")
        if (!userLists.isNullOrEmpty()){
            Log.v("userLists:",userLists)
        }
    }
    private fun initArgs(){
         viewModel.accountsList = CacheUtils.cacheUser["userLists"]?: arrayListOf()
    }
    private fun initView(){
        binding?.accounts?.apply {
            viewModel.AccountsAdpater = AccountsAdpater(context = applicationContext,viewModel.accountsList)
            viewModel.AccountsAdpater?.apply {
                setDeletDisable {
                    /**
                     * 删除个人信息 并清除list 保护用户数据
                     */
                    CacheUtils.cacheUser["userLists"]?.removeAt(it)
                    this.accounts.removeAt(it)

                }
                setItemClickListener {user->
                        /**
                         * 设计点击切换账号,更换cache,弹出dialog
                         */
                        viewModel.dialog = LogoutDialog(applicationContext, confirmText = "确定切换账户吗？").apply {
                            setConfirmClickListener {
                                CacheUtils.cache["uid"] = user.uid
                                CacheUtils.cache["cookies"] = user.cookie
                                CacheUtils.cache["fid"] = user.fid

                                saveUserInfo(user,context)
                                ToastUtil.show(context,"切换成功")
                            }
                            setCancelClickListener {
                                this.dismiss()
                            }
                        }

                    }
            }
            adapter = viewModel.AccountsAdpater
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        }


    }
    private fun saveUserInfo(user: User,context: Context) {
        clearUserInfo(context)
        if (user.user.isNotEmpty() && user.pwd.isNotEmpty()) {
            saveUsedSp("username", user.user)
            saveUsedSp("password", user.pwd)
        }

    }
    private fun clearUserInfo(context: Context){
        context.saveUsedSp("username", "")
        context.saveUsedSp("password", "")
        Toast.makeText(context, "数据删除成功", Toast.LENGTH_SHORT).show()
    }
    private fun initViewClick(){
        binding?.addaccount?.setOnClickListener {
            /**
             * 跳转登录界面，并保存信息回到manager 并判定是否为拓展账号
             */
            val intent = Intent(this,LoginActivity::class.java)
            CacheUtils.cacheB["extents"] = true
            startActivity(intent)
            finish()
        }
        binding?.deleteaccount?.setOnClickListener {
            /**
             * 显示delete按钮 修改list
             */
            viewModel.AccountsAdpater?.apply {
                showDeletButton()
            }

        }
    }

    /**
     * 为节省储存效率，在destroy时进行getSharedPreferences
     */
    override fun onDestroy() {
        super.onDestroy()
        CacheUtils.cacheUser["userLists"]?.let { saveJsonArraySp("userLists", it) }
    }


}