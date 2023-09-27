package com.cofbro.qian.main

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.cofbro.hymvvmutils.base.BaseActivity
import com.cofbro.qian.data.URL
import com.cofbro.qian.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {
    private var mAdapter: CourseListAdapter? = null
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        initView()
        doNetwork()
        initObserver()
    }

    private fun initView() {
        mAdapter = CourseListAdapter()

        binding?.rvCourseList?.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                return outRect.set(0, 0, 0, 20)
            }
        })
    }

    private fun initObserver() {
        viewModel.loadCourseListLiveData.observe(this) {
            lifecycleScope.launch(Dispatchers.IO) {
                val s = it.data?.body?.string()
                withContext(Dispatchers.Main) {
                    mAdapter?.setData(JSONObject.parseObject(s))
                    binding?.rvCourseList?.adapter = mAdapter
                    binding?.rvCourseList?.layoutManager =
                        LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
                }
            }
        }
    }

    private fun doNetwork() {
        viewModel.loadCourseList(URL.getAllCourseListPath())
    }
}