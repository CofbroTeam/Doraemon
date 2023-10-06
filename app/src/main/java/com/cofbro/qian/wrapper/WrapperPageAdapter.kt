package com.cofbro.qian.wrapper

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter

class WrapperPageAdapter(fragmentManager: FragmentManager, lifecycle: androidx.lifecycle.Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    private var pageList = emptyList<Fragment>()
    override fun getItemCount(): Int {
        return pageList.size
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> pageList[0]
            1 -> pageList[1]
            else -> pageList[0]
        }
    }

    fun setPageList(new: List<Fragment>) {
        pageList = new
    }
}