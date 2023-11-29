package com.cofbro.qian.main

import android.os.Bundle
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.cofbro.hymvvmutils.base.BaseActivity
import com.cofbro.qian.R
import com.cofbro.qian.databinding.ActivityMainBinding
import com.cofbro.qian.friend.FriendFragment
import com.cofbro.qian.home.HomeFragment
import com.cofbro.qian.profile.ProfileFragment
import com.cofbro.qian.utils.AmapUtils
import com.cofbro.qian.update.AutoUpdater
import com.cofbro.qian.utils.CacheUtils
import com.cofbro.qian.utils.Constants
import com.cofbro.qian.utils.Constants.BACK_PRESSED_INTERVAL
import com.hjq.toast.ToastUtils


class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {
    private var currentBackPressedTime = 0L
    private var homeFragment: HomeFragment? = null
    private var friendFragment: FriendFragment? = null
    private var profileFragment: ProfileFragment? = null
    private var lastShowFragment: Fragment? = null
    private var contentId = -1
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        AmapUtils.checkLocationPermission(this)
        CacheUtils.activities[Constants.Cache.MAIN_ACTIVITY] = this
        initView()
        changeNavigationResponsively()
        AutoUpdater(this).checkUpdate()
    }



    private fun initView() {
        contentId = binding?.content?.id ?: -1

        initFirstFragment()

        binding?.navigationBar?.setOnItemSelectedListener { item ->
            if (item.itemId != binding?.navigationBar?.selectedItemId) {
                when (item.itemId) {
                    R.id.tab_home -> {
                        showFragment(homeFragment!!)
                    }

                    R.id.tab_friend -> {
                        if (friendFragment == null) {
                            friendFragment = FriendFragment()
                            supportFragmentManager.beginTransaction()
                                .add(contentId, friendFragment!!, "FriendFragment")
                                .commit()
                        }
                        showFragment(friendFragment!!)
                    }

                    R.id.tab_profile -> {
                        if (profileFragment == null) {
                            profileFragment = ProfileFragment()
                            supportFragmentManager.beginTransaction()
                                .add(contentId, profileFragment!!, "ProfileFragment")
                                .commit()

                        }
                        showFragment(profileFragment!!)
                    }
                }
                return@setOnItemSelectedListener true
            }
            false
        }
    }

    private fun initFirstFragment() {
        homeFragment = HomeFragment()
        supportFragmentManager.beginTransaction()
            .show(homeFragment!!)
            .add(contentId, homeFragment!!, "HomeFragment")
            .commit()
        lastShowFragment = homeFragment
    }


    private fun showFragment(fragmentToShow: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.show(fragmentToShow)
        lastShowFragment?.let {
            transaction.hide(it)
        }
        lastShowFragment = fragmentToShow
        transaction.commit()
    }

    private fun changeNavigationResponsively() {
        binding?.root?.post {
            val windowInsects = ViewCompat.getRootWindowInsets(window.decorView)
            val height = windowInsects?.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.navigationBars())?.bottom ?: 0
            updateLayoutParams(height)
        }
    }

    private fun updateLayoutParams(height: Int) {
        if (height > 80) {
            val layout = binding?.root?.layoutParams as? MarginLayoutParams
            layout?.bottomMargin = height
            binding?.root?.layoutParams = layout
        }
    }


    override fun onBackPressed() {
        if (System.currentTimeMillis() - currentBackPressedTime > BACK_PRESSED_INTERVAL) {
            currentBackPressedTime = System.currentTimeMillis()
            ToastUtils.show("再按一次退出")
            return
        }
        super.onBackPressed()
    }
}