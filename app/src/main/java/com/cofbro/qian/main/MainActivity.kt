package com.cofbro.qian.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.cofbro.hymvvmutils.base.BaseActivity
import com.cofbro.qian.R
import com.cofbro.qian.databinding.ActivityMainBinding
import com.cofbro.qian.friend.FriendFragment
import com.cofbro.qian.home.HomeFragment
import com.cofbro.qian.profile.ProfileFragment


class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {
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

        createAllFragment()

        binding?.navigationBar?.setOnItemSelectedListener { item ->
            if (item.itemId != binding?.navigationBar?.selectedItemId) {
                when (item.itemId) {
                    R.id.tab_home -> {
                        showFragment(homeFragment!!)
                    }

                    R.id.tab_friend -> {
                        showFragment(friendFragment!!)
                    }

                    R.id.tab_profile -> {
                        showFragment(profileFragment!!)
                    }
                }
                return@setOnItemSelectedListener true
            }
            false
        }
    }

    private fun createAllFragment() {
        homeFragment = HomeFragment()
        friendFragment = FriendFragment()
        profileFragment = ProfileFragment()

        supportFragmentManager.beginTransaction()
            .add(contentId, homeFragment!!, "HomeFragment")
            .add(contentId, friendFragment!!, "FriendFragment")
            .add(contentId, profileFragment!!, "profileFragment")
            .hide(friendFragment!!)
            .hide(profileFragment!!)
            .commit()


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


    private fun selectFragment(fragment: Fragment?) {
        if (contentId == -1) return
        fragment?.let {
            val fragmentManager: FragmentManager = supportFragmentManager
            val transaction: FragmentTransaction = fragmentManager.beginTransaction()
            transaction.replace(contentId, fragment)
            transaction.commit()
        }
    }
}