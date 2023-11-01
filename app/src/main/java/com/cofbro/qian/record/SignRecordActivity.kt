package com.cofbro.qian.record

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSONObject
import com.cofbro.hymvvmutils.base.BaseActivity
import com.cofbro.qian.databinding.ActivitySignRecordBinding
import com.cofbro.qian.utils.SignRecorder
import com.cofbro.qian.utils.dp2px
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
    }

    private fun initObserver() {
        viewModel.loadLiveData.observe(this) {
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
}