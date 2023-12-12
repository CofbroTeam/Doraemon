package com.cofbro.qian.profile.update

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cofbro.hymvvmutils.base.BaseActivity
import com.cofbro.hymvvmutils.base.getBySp
import com.cofbro.qian.R
import com.cofbro.qian.databinding.ActivityUpdateDetailBinding


class UpdateDetailActivity : BaseActivity<UpdateDetailViewModel, ActivityUpdateDetailBinding>() {
    private var remoteVersion = 0L
    private var localVersion = 0L

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        initArgs()
        initView()
    }

    private fun initArgs() {
        remoteVersion = getBySp("remoteVersion")?.toLong() ?: 0L
        localVersion = getBySp("localVersion")?.toLong() ?: 0L
    }

    private fun initView() {
        changeNavigationResponsively()
        startTitleAnimation()
        checkIfLatest()
        initVersionNumber()
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
        binding?.tvVersionDetailNumber?.text = resources.getString(R.string.update_detail_version_number, localVersion.toString())
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
}