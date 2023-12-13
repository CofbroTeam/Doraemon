package com.cofbro.qian.profile.update

import android.animation.ObjectAnimator
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cofbro.hymvvmutils.base.BaseActivity
import com.cofbro.hymvvmutils.base.getBySp
import com.cofbro.qian.R
import com.cofbro.qian.databinding.ActivityUpdateDetailBinding
import com.cofbro.qian.update.AutoUpdater
import com.cofbro.qian.utils.Constants
import com.cofbro.qian.utils.dp2px
import com.cofbro.qian.utils.getStatusBarHeight
import com.cofbro.qian.view.FullScreenDialog


class UpdateDetailActivity : BaseActivity<UpdateDetailViewModel, ActivityUpdateDetailBinding>() {
    private var clickIntercept = false
    private var loadingView: FullScreenDialog? = null
    private var remoteVersion = 0L
    private var localVersion = 0L

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        initArgs()
        initView()
        initEvent()
    }

    private fun initEvent() {
        binding?.tvVersionDetailDetail?.setOnClickListener {
            toUpdateDetailWebPage()
        }

        binding?.tvVersionDetailUpdate?.setOnClickListener {
            checkUpdate()
        }
    }

    private fun initArgs() {
        remoteVersion = getBySp(Constants.Update.REMOTE_VERSION)?.toLong() ?: 0L
        localVersion = getBySp(Constants.Update.LOCAL_VERSION)?.toLong() ?: 0L
    }

    private fun initView() {
        changeNavigationResponsively()
        startTitleAnimation()
        checkIfLatest()
        initVersionNumber()
        initToolbar()
    }

    private fun initToolbar() {
        // height of toolbar
        binding?.appTool?.apply {
            val toolbarHeight = getStatusBarHeight(this@UpdateDetailActivity) + dp2px(
                this@UpdateDetailActivity,
                50
            )
            val csLayout = layoutParams
            csLayout.height = toolbarHeight
        }
    }

    private fun checkUpdate() {
        if (clickIntercept) return
        clickIntercept = true
        AutoUpdater(this).checkUpdate(true,
            onPreCheck = {
                showLoadingView()
            }, onShowDownloadDialog = {
                hideLoadingView()
                clickIntercept = false
            }
        )
    }

    private fun toUpdateDetailWebPage() {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://www.yuque.com/cofbro/doraemon/krann1gxgu1c52b9")
        )
        startActivity(intent)
    }

    private fun startTitleAnimation() {
        binding?.tvVersionDetailTitle?.post {
            val width = binding?.tvVersionDetailTitle?.width?.toFloat() ?: 0f
            val animator: ObjectAnimator = ObjectAnimator.ofFloat(
                binding?.tvVersionDetailMask,
                "translationX",
                -width,
                width
            ).apply {
                duration = 2500
                repeatCount = ObjectAnimator.INFINITE
            }
            animator.start()
        }
    }

    private fun initVersionNumber() {
        binding?.tvVersionDetailNumber?.text =
            resources.getString(R.string.update_detail_version_number, localVersion.toString())
    }

    private fun checkIfLatest() {
        if (remoteVersion > localVersion) {
            binding?.tvVersionDetailTip?.text =
                resources.getString(R.string.update_detail_version_tip_not_latest)
            binding?.tvVersionDetailUpdate?.visibility = View.VISIBLE
        } else if (remoteVersion == localVersion) {
            binding?.tvVersionDetailTip?.text =
                resources.getString(R.string.update_detail_version_tip_latest)
            binding?.tvVersionDetailUpdate?.visibility = View.GONE
        }
    }

    private fun changeNavigationResponsively() {
        binding?.root?.post {
            val windowInsects = ViewCompat.getRootWindowInsets(window.decorView)
            val height =
                windowInsects?.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.navigationBars())?.bottom
                    ?: 0
            updateLayoutParams(height)
        }
    }

    private fun updateLayoutParams(height: Int) {
        if (height > 80) {
            val layout = binding?.root?.layoutParams as? ViewGroup.MarginLayoutParams
            layout?.bottomMargin = height
            binding?.root?.layoutParams = layout
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