package com.cofbro.qian.wrapper

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.cofbro.qian.R
import com.cofbro.qian.databinding.ActivityWrapperBinding
import com.cofbro.qian.homework.HomeworkFragment
import com.cofbro.qian.task.TaskFragment
import com.google.android.material.tabs.TabLayoutMediator

class WrapperActivity : AppCompatActivity() {
    private var binding: ActivityWrapperBinding? = null
    var courseId = ""
    var classId = ""
    var cpi = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWrapperBinding.inflate(layoutInflater, null, false)
        setContentView(binding?.root)
        // 初始化工作
        init()
    }

    private fun init() {
        initArgs()
        initView()
    }

    private fun initArgs() {
        courseId = intent.getStringExtra("courseId") ?: ""
        classId = intent.getStringExtra("classId") ?: ""
        cpi = intent.getStringExtra("cpi") ?: ""
    }

    private fun initView() {
        initStatusBarStyle()
        initViewPager()
    }

    private fun initStatusBarStyle() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }

    private fun initViewPager() {
        val pageAdapter = WrapperPageAdapter(supportFragmentManager, lifecycle)
        pageAdapter.setPageList(listOf(TaskFragment(), HomeworkFragment()))

        binding?.let {
            it.viewPager.adapter = pageAdapter
            TabLayoutMediator(it.tabLayout, it.viewPager) { tab, position ->
                when (position) {
                    0 -> tab.text = resources.getString(R.string.sign_active_task)
                    1 -> tab.text = resources.getString(R.string.all_homework)
                }
            }.attach()
        }
    }
}