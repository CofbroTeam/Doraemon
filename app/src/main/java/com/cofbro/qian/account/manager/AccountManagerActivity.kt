package com.cofbro.qian.account.manager

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.cofbro.hymvvmutils.base.BaseActivity
import com.cofbro.qian.account.adapter.AccountsAdapter
import com.cofbro.qian.data.URL
import com.cofbro.qian.databinding.ActivityAccountmanagerBinding
import com.cofbro.qian.utils.Constants
import com.cofbro.qian.utils.Downloader
import com.cofbro.qian.utils.dp2px
import com.cofbro.qian.utils.getIntExt
import com.cofbro.qian.utils.getJSONArrayExt
import com.cofbro.qian.utils.getStatusBarHeight
import com.cofbro.qian.utils.safeParseToJson
import com.cofbro.qian.view.FullScreenDialog
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.hjq.toast.ToastUtils
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter
import jp.wasabeef.recyclerview.animators.ScaleInLeftAnimator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


/**
 * 关联账号，实现一起签到
 */
class AccountManagerActivity :
    BaseActivity<AccountManagerViewModel, ActivityAccountmanagerBinding>() {
    private var behavior: BottomSheetBehavior<NestedScrollView>? = null
    private var loadingView: FullScreenDialog? = null
    private var mAdapter: AccountsAdapter? = null
    private var data: JSONObject? = null
    private var mUsername = ""
    private var mPassword = ""
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        loadAccountData()
        initView()
        initObserver()
        initEvent()

//        preGetUserLists()
//        initArgs()
//        initView()
//        initViewClick()
    }

    private fun loadAccountData() {
        data = Downloader.acquire(this, Constants.RecycleJson.ACCOUNT_JSON_DATA).safeParseToJson()
        data?.let {
            mAdapter?.setData(it)
        }
    }


    private fun initView() {
        mAdapter = AccountsAdapter()
        mAdapter?.setData(data!!)
        binding?.recyclerView?.apply {
            itemAnimator = ScaleInLeftAnimator()
            adapter = AlphaInAnimationAdapter(mAdapter!!)
            layoutManager = LinearLayoutManager(
                this@AccountManagerActivity,
                LinearLayoutManager.VERTICAL,
                false
            )

            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    val defaultPadding = dp2px(this@AccountManagerActivity, 15)
//                    val toolbarHeight = binding?.appToolBar?.height ?: 0
                    if (parent.layoutManager?.getPosition(view) == 0) {
                        return outRect.set(
                            defaultPadding,
                            getStatusBarHeight(this@AccountManagerActivity) + dp2px(this@AccountManagerActivity, 5),
                            defaultPadding,
                            defaultPadding
                        )
                    }
                    return super.getItemOffsets(outRect, view, parent, state)
                }
            })

        }
        // bottomSheet
        binding?.bottomSheet?.let {
            behavior = BottomSheetBehavior.from(it)
            behavior?.apply {
                isFitToContents = false //展开后开度填充Parent的高度
                expandedOffset = getStatusBarHeight(this@AccountManagerActivity) + 40
                halfExpandedRatio = 0.5f
                isHideable = false
                isDraggable = true
                peekHeight = 120
                setState(BottomSheetBehavior.STATE_HIDDEN)
            }
        }

        binding?.etUsername?.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                behavior?.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }

    private fun initObserver() {
        viewModel.loginLiveData.observe(this) {
            lifecycleScope.launch(Dispatchers.IO) {
                val body = it.data?.body?.string()?.safeParseToJson()
                val headers = it.data?.headers
                if (body?.getBoolean("status") == true) {
                    val cookies: List<String>? = headers?.values("Set-Cookie")
                    saveCookies(cookies)
                }
            }
        }
    }

    private fun initEvent() {
        binding?.tvBinding?.setOnClickListener {
            showLoadingView()
            login()
        }
    }

    private fun login() {
        val username = binding?.etUsername?.text.toString()
        val password = binding?.etPassword?.text.toString()
        if (username.isNotEmpty() && password.isNotEmpty()) {
            mUsername = username
            mPassword = password
            viewModel.login(URL.getLoginPath(username, password))
        }
    }

    private fun saveCookies(cookies: List<String>?) {
        if (cookies?.isNotEmpty() == true) {
            val result = StringBuilder()
            var uid = ""
            var fid = ""
            try {
                for (i in cookies.indices) {
                    val temp =
                        cookies[i].split(";".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()[0]
                    result.append(temp).append(";")
                    if (temp.startsWith("UID")) uid = temp.substring(4)
                    if (temp.startsWith("fid")) fid = temp.substring(4)
                }
                val jsonObject = parseToJSONObject(uid, fid)
                notifyDataSetChanged(jsonObject)
                Downloader.download(
                    this,
                    Constants.RecycleJson.ACCOUNT_JSON_DATA,
                    jsonObject?.toJSONString() ?: ""
                )
                hideLoadingView()
            } catch (_: Exception) {
            }
        } else {
            ToastUtils.show("绑定失败!请检查账号密码是否正确!")
        }
    }

    private fun parseToJSONObject(uid: String, fid: String): JSONObject? {
        val path = filesDir.path + File.separatorChar + Constants.RecycleJson.ACCOUNT_JSON_DATA
        val file = File(path)
        val jsonObject = JSONObject()
        jsonObject["username"] = mUsername
        jsonObject["password"] = mPassword
        jsonObject["uid"] = uid
        jsonObject["fid"] = fid
        if (file.exists()) {
            val newSize = data?.getIntExt("size") ?: 0
            val array = data?.getJSONArrayExt("users") ?: JSONArray()
            array[newSize] = jsonObject
            data?.set("users", array)
            data?.set("size", newSize + 1)
        } else {
            data = JSONObject()
            val array = JSONArray()
            array[0] = jsonObject
            data!!["history"] = "true"
            data!!["size"] = 1
            data!!["users"] = array
        }
        return data
    }

    private fun notifyDataSetChanged(data: JSONObject?) {
        data?.let {
            mAdapter?.setData(it)
        }
    }

    private fun showLoadingView() {
        if (loadingView == null) {
            loadingView = FullScreenDialog(this)
        }
        loadingView?.setCancelable(false)
        loadingView?.show()
    }

    private fun hideLoadingView() {
        loadingView?.dismiss()
        loadingView = null
    }
}