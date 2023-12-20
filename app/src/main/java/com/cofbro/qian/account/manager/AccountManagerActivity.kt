package com.cofbro.qian.account.manager

import android.graphics.Rect
import android.os.Bundle
import android.os.Vibrator
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.appcompat.widget.PopupMenu
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.cofbro.hymvvmutils.base.BaseActivity
import com.cofbro.qian.R
import com.cofbro.qian.account.adapter.AccountsAdapter
import com.cofbro.qian.data.URL
import com.cofbro.qian.databinding.ActivityAccountmanagerBinding
import com.cofbro.qian.utils.AccountManager
import com.cofbro.qian.utils.Constants
import com.cofbro.qian.utils.dp2px
import com.cofbro.qian.utils.getIntExt
import com.cofbro.qian.utils.getStatusBarHeight
import com.cofbro.qian.utils.getStringExt
import com.cofbro.qian.utils.safeParseToJson
import com.cofbro.qian.view.dialog.CodingDialog
import com.cofbro.qian.view.dialog.FullScreenDialog
import com.cofbro.qian.view.dialog.TipDialog
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.hjq.toast.ToastUtils
import jp.wasabeef.recyclerview.animators.OvershootInLeftAnimator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


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
            setItemOnLongClickListener { view, itemData, pos ->
                showPopMenu(view, itemData, pos)
            }

            setDataChangedListener {
                // 全量更新
                updateAccountData(it)
                responseLottieView(it)
            }
        }
        notifyAdapterDataChanged(data)
        binding?.recyclerView?.apply {
            itemAnimator = OvershootInLeftAnimator()
            adapter = mAdapter
//            adapter = ScaleInAnimationAdapter(mAdapter!!).apply {
//                // Change the durations.
//                setDuration(1000)
//                // Change the interpolator.
//                setInterpolator(AnticipateOvershootInterpolator())
//                // Disable the first scroll mode.
//                setFirstOnly(false)
//            }
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
                        dp2px(this@AccountManagerActivity, 12),
                        defaultPadding,
                        0
                    )
                }
            })

        }
        // bottomSheet
        binding?.bottomSheet?.let {
            behavior = BottomSheetBehavior.from(it)
            behavior?.apply {
                isFitToContents = false
                expandedOffset = toolbarHeight
                halfExpandedRatio = 0.5f
                isHideable = false
                isDraggable = true
                peekHeight = 120
                setState(BottomSheetBehavior.STATE_HIDDEN)
            }
        }

        binding?.etUsername?.setOnFocusChangeListener { _, hasFocus ->
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
                50
            )
            val csLayout = layoutParams
            csLayout.height = toolbarHeight
        }
        // topMargin of recyclerView
        binding?.content?.apply {
            val rvLayout = layoutParams as? MarginLayoutParams
            rvLayout?.let {
                it.topMargin = toolbarHeight
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
                } else {
                    hideLoadingView()
                    ToastUtils.show("账号或密码错误！")
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

    private suspend fun saveCookies(cookies: List<String>?) {
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
                val jsonObject = buildAccount(uid, fid, cookies.toString())
                withContext(Dispatchers.Main) {
                    jsonObject?.let {
                        mAdapter?.notifyItemInserted()
                    }
                    responseUI(jsonObject)
                }
            } catch (_: Exception) {
            }
        } else {
            ToastUtils.show("绑定失败!请检查账号密码是否正确!")
        }
    }

    private fun responseUI(jsonObject: JSONObject?) {
        if (jsonObject != null) {
            ToastUtils.show("绑定成功！")
            clearText()
        } else {
            ToastUtils.show("账号已存在！")
        }
        hideLoadingView()
    }

    private fun clearText() {
        binding?.etUsername?.text?.clear()
        binding?.etPassword?.text?.clear()
    }

    private fun notifyAdapterDataChanged(data: JSONObject?) {
        data?.let {
            lifecycleScope.launch(Dispatchers.Main) {
                mAdapter?.setAccounts(it)
            }
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
                val uid = itemData?.getStringExt(Constants.Account.UID) ?: ""
                mAdapter?.removeAccount(uid)
            }
            setNegativeClickListener {
                dismiss()
            }
            setCancelable(false)
            show()
        }
    }

    private fun loadAccountData() {
        lifecycleScope.launch(Dispatchers.IO) {
            data = AccountManager.loadAllAccountData(this@AccountManagerActivity)
            data?.let {
                notifyAdapterDataChanged(it)
            }
        }
    }

    private fun buildAccount(uid: String, fid: String, cookies: String): JSONObject? {
        val userArray = data?.getJSONArray(Constants.Account.USERS) ?: JSONArray()
        userArray.forEach {
            val itemData = it as? JSONObject
            if (itemData?.getStringExt(Constants.Account.UID) == uid) {
                return null
            }
        }
        val account = AccountManager.buildAccount(mUsername, mPassword, uid, fid, cookies)
        // data和adapter.accountData同源
        data = AccountManager.bindAccounts(this, data, account)
        return account
    }

    private fun updateAccountData(data: JSONObject?) {
        val newData = data?.toJSONString() ?: ""
        lifecycleScope.launch(Dispatchers.IO) {
            AccountManager.updateAccountData(this@AccountManagerActivity, newData)
        }
    }

    private fun responseLottieView(data: JSONObject?) {
        if (data == null) {
            binding?.lottieView?.visibility = View.VISIBLE
            return
        }
        val size = data.getIntExt(Constants.Account.SIZE).takeIf { it != -1 } ?: 0
        if (size == 0) {
            binding?.lottieView?.visibility = View.VISIBLE
        } else {
            binding?.lottieView?.visibility = View.GONE
        }
    }

    private fun vibrate() {
        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(100L)
        }
    }

    private fun showPopMenu(view: View, itemData: JSONObject?, pos: Int) {
        val menu = PopupMenu(this, view)
        menu.menuInflater.inflate(R.menu.pop_menu, menu.menu)
        menu.setOnMenuItemClickListener {
            if (it.itemId == R.id.pop_add_remark) {
                menu.dismiss()
                showRemarkDialog(itemData, pos)
                return@setOnMenuItemClickListener true
            } else if (it.itemId == R.id.pop_delete) {
                menu.dismiss()
                showTipDialog(itemData)
                return@setOnMenuItemClickListener true
            }
            false
        }
        vibrate()
        menu.show()
    }

    private fun showRemarkDialog(itemData: JSONObject?, pos: Int) {
        CodingDialog(this).apply {
            show()
            setCancelable(false)
            setHint(context.resources.getString(R.string.account_add_remark_hint))
            setTitle(context.resources.getString(R.string.account_add_remark_title))
            setContent(context.resources.getString(R.string.account_add_remark_content))
            setPositiveClickListener {
                itemData?.set(Constants.Account.REMARK, it)
                mAdapter?.notifyItemAccountChanged(pos)
                dismiss()
            }

            setNegativeClickListener {
                dismiss()
            }
        }
    }
}
