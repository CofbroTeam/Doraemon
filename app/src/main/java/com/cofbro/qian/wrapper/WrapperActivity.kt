package com.cofbro.qian.wrapper

import android.Manifest
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.cofbro.qian.R
import com.cofbro.qian.databinding.ActivityWrapperBinding
import com.cofbro.qian.wrapper.homework.HomeworkFragment
import com.cofbro.qian.wrapper.task.TaskFragment
import com.google.android.material.tabs.TabLayoutMediator

class WrapperActivity : AppCompatActivity() {
    private var binding: ActivityWrapperBinding? = null
    var courseId = ""
    var classId = ""
    var cpi = ""
    var courseName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWrapperBinding.inflate(layoutInflater, null, false)
        setContentView(binding?.root)
        // 初始化工作
        init()
    }

    private fun init() {
        getLocationPermission()
        initArgs()
        initView()
    }
    private fun getLocationPermission(){
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    // Precise location access granted.
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    // Only approximate location access granted.
                } else -> {
                // No location access granted.
            }
            }
        }

// ...

// Before you perform the actual permission request, check whether your app
// already has the permissions, and whether your app needs to show a permission
// rationale dialog. For more details, see Request permissions.
        locationPermissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION))
    }
    private fun initArgs() {
        courseId = intent.getStringExtra("courseId") ?: ""
        classId = intent.getStringExtra("classId") ?: ""
        cpi = intent.getStringExtra("cpi") ?: ""
        courseName = intent.getStringExtra("courseName") ?: ""
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