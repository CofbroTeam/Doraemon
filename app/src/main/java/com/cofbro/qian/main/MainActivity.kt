package com.cofbro.qian.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.cofbro.hymvvmutils.base.BaseActivity
import com.cofbro.qian.R
import com.cofbro.qian.databinding.ActivityMainBinding
import com.cofbro.qian.friend.FriendFragment
import com.cofbro.qian.friend.im.chatActivity.ChatTestFragment
import com.cofbro.qian.home.HomeFragment
import com.cofbro.qian.profile.ProfileFragment
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
        initView()
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


    override fun onBackPressed() {
        if (System.currentTimeMillis() - currentBackPressedTime > BACK_PRESSED_INTERVAL) {
            currentBackPressedTime = System.currentTimeMillis()
            ToastUtils.show("再按一次退出")
            return
        }
        super.onBackPressed()
    }
}