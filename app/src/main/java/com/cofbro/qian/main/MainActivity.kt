package com.cofbro.qian.main

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSONObject
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.cofbro.hymvvmutils.base.BaseActivity
import com.cofbro.qian.data.URL
import com.cofbro.qian.databinding.ActivityMainBinding
import com.cofbro.qian.scan.ScanActivity
import com.cofbro.qian.utils.CacheUtils
import com.cofbro.qian.utils.dp2px
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {
    private var scrolledDx = 0
    private var targetScrollDx = 0
    private var mAdapter: CourseListAdapter? = null
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        initView()
        doNetwork()
        initObserver()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            var enc = data!!.getStringExtra("result")
        }
    }

    private fun initView() {
        // 课程论列表
        mAdapter = CourseListAdapter()
        targetScrollDx = dp2px(this@MainActivity, 76)
        binding?.rvCourseList?.apply {
            // 滑动监听
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    computeVerticalScrollOffset().let {
                        if (it > targetScrollDx) return
                        scrolledDx = it
                    }
                    binding?.appToolBar?.background?.alpha =
                        ((scrolledDx.toFloat() / targetScrollDx.toFloat()) * 255).toInt()
                    Log.d(
                        "MainActivity",
                        "scrolledDx: $scrolledDx, dx: $dx, alpha: ${binding?.appToolBar?.background?.alpha}"
                    )
                }
            })

            onFlingListener = object : RecyclerView.OnFlingListener() {
                override fun onFling(velocityX: Int, velocityY: Int): Boolean {
                    Log.d("MainActivity", "onFling: $velocityY")
                    if (velocityY >= 1200) {
                        binding?.appToolBar?.background?.alpha = 255
                    }
                    return false
                }

            }

            // 增加间距
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    val defaultPadding = dp2px(this@MainActivity, 15)
                    if (parent.layoutManager?.getPosition(view) == 0) {
                        return outRect.set(
                            defaultPadding,
                            dp2px(this@MainActivity, 80),
                            defaultPadding,
                            defaultPadding
                        )
                    }
                    return outRect.set(defaultPadding, 0, defaultPadding, defaultPadding)
                }
            })
        }

        /**
         * ToolBar
         */
        // 用户头像
        val uid = CacheUtils.cache["uid"]
        uid?.let {
            val options = RequestOptions().transform(
                CenterCrop(),
                RoundedCorners(dp2px(this, 5))
            )
            Glide.with(this)
                .load(URL.getAvtarImgPath(it))
                .apply(options)
                .into(binding!!.ivUserAvtar)
        }

        // 导航栏扫码签到
        binding?.ivScanBtn?.setOnClickListener {
            val intent = Intent(this, ScanActivity::class.java)
            startActivity(intent)
        }
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
                    mAdapter?.setOnItemClickListener(object : CourseListAdapter.AdapterListener {
                        override fun onItemClick(courseId: String, classId: String, cpi: String) {
                            queryAllActiveTask(courseId, classId, cpi)
                        }

                    })
                }
            }
        }

        viewModel.queryActiveTaskListLiveData.observe(this) {
            lifecycleScope.launch(Dispatchers.IO) {
                val data = it.data?.body?.string()
                withContext(Dispatchers.Main) {

                }
            }
        }
    }

    private fun doNetwork() {
        // 加载课程列表
        viewModel.loadCourseList(URL.getAllCourseListPath())
    }

    private fun queryAllActiveTask(courseId: String, classId: String, cpi: String) {
        // 查询所有活动
        val uid = CacheUtils.cache["uid"] ?: ""
        viewModel.queryActiveTaskList(URL.gatActiveTaskListPath(courseId, classId, uid, cpi))
    }
}