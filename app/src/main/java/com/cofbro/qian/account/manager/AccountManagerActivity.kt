package com.cofbro.qian.account.manager

import android.graphics.Rect
import android.os.Bundle
import android.os.Vibrator
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.view.animation.AnticipateOvershootInterpolator
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
import com.cofbro.qian.utils.getStringExt
import com.cofbro.qian.utils.safeParseToJson
import com.cofbro.qian.view.FullScreenDialog
import com.cofbro.qian.view.TipDialog
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.hjq.toast.ToastUtils
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter
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
    private var toolbarHeight = 0
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
        initToolbar()
        binding?.csContent?.apply {
            val height = resources.displayMetrics.heightPixels
            val layout = layoutParams
            layout.height = height
            layoutParams = layout
        }
        mAdapter = AccountsAdapter().apply {
            setItemOnLongClickListener { itemData, _ ->
                vibrate()
                showTipDialog(itemData)
            }
        }
        mAdapter?.setData(data)
        binding?.recyclerView?.apply {
            itemAnimator = ScaleInLeftAnimator()
            adapter = ScaleInAnimationAdapter(mAdapter!!).apply {
                // Change the durations.
                setDuration(1000)
                // Change the interpolator.
                setInterpolator(AnticipateOvershootInterpolator())
                // Disable the first scroll mode.
                setFirstOnly(false)
            }
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
                    val defaultPadding = dp2px(this@AccountManagerActivity, 16)
                    return outRect.set(
                        defaultPadding,
                        0,
                        defaultPadding,
                        dp2px(this@AccountManagerActivity, 12)
                    )
                }
            })

        }
        // bottomSheet
        binding?.bottomSheet?.let {
            behavior = BottomSheetBehavior.from(it)
            behavior?.apply {
                isFitToContents = false
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

    private fun initToolbar() {
        // height of toolbar
        binding?.toolbar?.apply {
            toolbarHeight = getStatusBarHeight(this@AccountManagerActivity) + dp2px(
                this@AccountManagerActivity,
                45
            )
            val csLayout = layoutParams
            csLayout.height = toolbarHeight
        }
        // topMargin of recyclerView
        binding?.recyclerView?.apply {
            val rvLayout = layoutParams as? MarginLayoutParams
            rvLayout?.let {
                it.topMargin = toolbarHeight + dp2px(this@AccountManagerActivity, 5)
                layoutParams = it
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
            bindAccount()
        }

        binding?.tvBack?.setOnClickListener {
            finish()
        }
    }

    private fun bindAccount() {
        showLoadingView()
        login()
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
                ToastUtils.show("绑定成功！")
                clearText()
                hideLoadingView()
            } catch (_: Exception) {
            }
        } else {
            ToastUtils.show("绑定失败!请检查账号密码是否正确!")
        }
    }

    private fun clearText() {
        binding?.etUsername?.text?.clear()
        binding?.etPassword?.text?.clear()
    }

    private fun parseToJSONObject(uid: String, fid: String): JSONObject? {
        val path = filesDir.path + File.separatorChar + Constants.RecycleJson.ACCOUNT_JSON_DATA
        val file = File(path)
        val jsonObject = JSONObject()
        jsonObject["username"] = mUsername
        jsonObject["password"] = mPassword
        jsonObject["uid"] = uid
        jsonObject["fid"] = fid
        jsonObject["picUrl"] = URL.getAvtarImgPath(uid)
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

    private fun showTipDialog(itemData: JSONObject?) {
        TipDialog(this).apply {
            setPositiveClickListener {
                dismiss()
                val uid = itemData?.getStringExt("uid") ?: ""
                mAdapter?.remove(uid)
                updateAccountData()
            }
            setNegativeClickListener {
                dismiss()
            }
            setCancelable(false)
            show()
        }
    }

    private fun updateAccountData() {
        val data = mAdapter?.getData()?.toJSONString() ?: ""
        lifecycleScope.launch(Dispatchers.IO) {
            Downloader.download(
                this@AccountManagerActivity,
                Constants.RecycleJson.ACCOUNT_JSON_DATA,
                data
            )
        }
    }

    private fun vibrate() {
        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(100L)
        }
    }
}