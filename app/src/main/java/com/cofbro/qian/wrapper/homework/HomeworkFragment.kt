package com.cofbro.qian.wrapper.homework

import android.os.Bundle
import com.cofbro.hymvvmutils.base.BaseFragment
import com.cofbro.qian.databinding.FragmentHomeworkBinding


class HomeworkFragment : BaseFragment<HomeworkViewModel, FragmentHomeworkBinding>() {
    override fun onAllViewCreated(savedInstanceState: Bundle?) {

    }

    private fun doNetwork() {
        viewModel.queryHomeworkList("http://mooc1-api.chaoxing.com/work/stu-work")
    }

}