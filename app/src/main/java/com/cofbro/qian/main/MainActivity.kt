package com.cofbro.qian.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.cofbro.hymvvmutils.base.BaseActivity
import com.cofbro.qian.R
import com.cofbro.qian.databinding.ActivityMainBinding
import com.cofbro.qian.friend.FriendFragment
import com.cofbro.qian.home.HomeFragment
import com.cofbro.qian.profile.ProfileFragment
import com.google.android.material.navigation.NavigationBarView


class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {
    private var homeFragment: Fragment? = null
    private var contentId = -1
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        initView()
    }


    private fun initView() {
        contentId = binding?.content?.id ?: -1
        homeFragment = HomeFragment()
        selectFragment(homeFragment)

        binding?.navigationBar?.setOnItemSelectedListener { item ->
            if (item.itemId != binding?.navigationBar?.selectedItemId) {
                when (item.itemId) {
                    R.id.tab_home -> {
                        selectFragment(HomeFragment())
                    }

                    R.id.tab_friend -> {
                        selectFragment(FriendFragment())
                    }

                    R.id.tab_profile -> {
                        selectFragment(ProfileFragment())
                    }
                }
                return@setOnItemSelectedListener true
            }
            false
        }
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