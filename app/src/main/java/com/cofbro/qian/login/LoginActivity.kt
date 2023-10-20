package com.cofbro.qian.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.alibaba.fastjson.JSON
import com.cofbro.hymvvmutils.base.BaseActivity
import com.cofbro.hymvvmutils.base.getBySp
import com.cofbro.hymvvmutils.base.saveUsedSp
import com.cofbro.qian.account.manager.AccountManagerActivity
import com.cofbro.qian.account.manager.User
import com.cofbro.qian.data.URL
import com.cofbro.qian.databinding.ActivityLoginBinding
import com.cofbro.qian.main.MainActivity
import com.cofbro.qian.utils.CacheUtils
import com.cofbro.qian.utils.getJsonArraySp
import com.cofbro.qian.utils.safeParseToJson
import com.hjq.toast.ToastUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONStringer


class LoginActivity : BaseActivity<LoginViewModel, ActivityLoginBinding>() {
    private var mUsername: String? = null
    private var mPassword: String? = null
    private var extents:Boolean = CacheUtils.cacheB["extents"]?:false
    override fun onActivityCreated(savedInstanceState: Bundle?) {

        preGetUserLists()
        /**
         * 常规登录
         */
        if (!extents){
            tryLogin()
            initObserver()
            initEvent()
            autoClearFocus()
            login()
        }else{
            /**
             * 拓展登录不能进入？
             */
            initObserver()
            initEvent()
            autoClearFocus()
            login()
        }



    }
    private fun preGetUserLists(){
        val userLists = getJsonArraySp("userLists")
        CacheUtils.cacheUser["userLists"] = arrayListOf()


        /**
         * 创建单例
         */


        if (!userLists.isNullOrEmpty()){
            val array =  org.json.JSONArray(userLists)
            for (i in 0 until array.length()) {
                val element = array[i].toString().safeParseToJson()
                val uid = element.getString("uid")
                val user = User(user = element.getString("user"), pwd = element.getString("pwd"), cookie = element.getString("cookie"), uid = element.getString("uid"), fid = element.getString("fid"))
                if(CacheUtils.cacheUser["userLists"]?.contains(user) == false){
                    CacheUtils.cacheUser["userLists"]?.add(user)
                }

               Log.v("e",uid)
             //用户信息列表添加
            }
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
                        CacheUtils.cacheB["extents"] = false
                        val userInfo = User(getBySp("username")?:"",getBySp("password")?:"",uid?:"",cookies.toString(),fid?:"")

                        CacheUtils.cacheUser["userLists"]?.add(userInfo)
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
                         * 拓展登录 class User(user:String,pwd:String,uid:String,cookie:String,fid:String)
                         */
                        lifecycleScope.launch(Dispatchers.Main) {

                            val userInfo = User(binding?.ipUsername?.getTextString()?:"",binding?.ipPassword?.getTextString()?:"",uid?:"",cookies.toString(),fid?:"")
                            /**
                             * 判断是否含有userInfo uid判断
                             */
                            if(CacheUtils.cacheUser["userLists"]?.contains(userInfo) == true){
                                ToastUtils.show("已经有该用户了")
                                /*
                                clear
                                 */

                            }else{
                                CacheUtils.cacheUser["userLists"]?.add(userInfo)//用户信息
                                ToastUtils.show("添加用户成功！")
                                val intent = Intent(this@LoginActivity, AccountManagerActivity::class.java)
                                /**
                                 * 传递用户信息？是否需要CashUtil ....需要，储存方式arraylist()
                                 */
                                startActivity(intent)
                                finish()
                            }

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