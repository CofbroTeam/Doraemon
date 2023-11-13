package com.cofbro.qian.record

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.alibaba.fastjson.JSONObject
import com.cofbro.hymvvmutils.base.BaseActivity
import com.cofbro.qian.databinding.ActivitySignRecordBinding
import com.cofbro.qian.utils.Constants
import com.cofbro.qian.utils.SignRecorder
import com.cofbro.qian.utils.dp2px
import com.cofbro.qian.utils.getIntExt
import com.cofbro.qian.utils.getStatusBarHeight
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignRecordActivity : BaseActivity<SignRecordViewModel, ActivitySignRecordBinding>() {
    private var toolbarHeight = 0
    private var records: JSONObject? = null
    private var mAdapter: SignRecordAdapter? = null
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        initData()
        initView()
        initObserver()
        initEvent()
    }

    private fun initEvent() {
        binding?.ivBack?.setOnClickListener {
            finish()
        }

        binding?.tvRecordTip?.setOnClickListener {
            binding?.rvRecord?.smoothScrollToPosition(0)
        }
    }

    private fun initObserver() {
        viewModel.loadLiveData.observe(this) {
            responseLottieView()
            mAdapter?.setData(records)
        }
    }

    private fun initView() {
        initToolbar()
        binding?.rvRecord?.apply {
            mAdapter = SignRecordAdapter()
            if (records != null) {
                mAdapter?.setData(records)
            }
            adapter = mAdapter
            layoutManager =
                LinearLayoutManager(this@SignRecordActivity, LinearLayoutManager.VERTICAL, false)

            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    if (parent.layoutManager?.getPosition(view) == 0) {
                        return outRect.set(
                            0,
                            toolbarHeight,
                            0,
                            0
                        )
                    }
                    super.getItemOffsets(outRect, view, parent, state)
                }
            })

            addOnScrollListener(object :OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    changeRecordTipView(dy)
                }
            })
        }
    }

    private fun changeRecordTipView(scrollDy: Int) {
        if (scrollDy > 0) {
            binding?.tvRecordTip?.visibility = View.VISIBLE
        } else {
            binding?.tvRecordTip?.visibility = View.GONE
        }
    }

    private fun initToolbar() {
        // height of toolbar
        binding?.toolBar?.apply {
            toolbarHeight = getStatusBarHeight(this@SignRecordActivity) + dp2px(
                this@SignRecordActivity,
                50
            )
            val csLayout = layoutParams
            csLayout.height = toolbarHeight
        }
    }

    private fun initData() {
        lifecycleScope.launch(Dispatchers.IO) {
            records = SignRecorder.readRecords(this@SignRecordActivity)
            viewModel.notifyData()
        }
    }

    private fun responseLottieView() {
        if (records == null) {
            binding?.lottieView?.visibility = View.VISIBLE
            return
        }
        val size = records?.getIntExt(Constants.Recorder.SIZE)?.takeIf { it != -1 } ?: 0
        if (size == 0) {
            binding?.lottieView?.visibility = View.VISIBLE
        } else {
            binding?.lottieView?.visibility = View.GONE
        }
    }
}