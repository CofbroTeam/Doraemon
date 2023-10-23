package com.cofbro.qian.profile

import android.os.Bundle
import android.view.ViewGroup.MarginLayoutParams
import com.cofbro.hymvvmutils.base.BaseFragment
import com.cofbro.qian.databinding.FragmentProfileBinding
import com.cofbro.qian.utils.dp2px
import com.cofbro.qian.utils.getStatusBarHeight

class ProfileFragment : BaseFragment<ProfileViewModel, FragmentProfileBinding>() {
    override fun onAllViewCreated(savedInstanceState: Bundle?) {
        initView()
    }

    private fun initView() {
        adjustMarginOfView()
    }

    private fun adjustMarginOfView() {
        val statusBarHeight = getStatusBarHeight(requireContext())
        val layoutParams = binding?.csMyInfo?.layoutParams as? MarginLayoutParams
        layoutParams?.topMargin = statusBarHeight + dp2px(requireContext(), 5)
    }

}