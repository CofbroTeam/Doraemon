package com.cofbro.qian.wrapper.homework

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.cofbro.hymvvmutils.base.BaseFragment
import com.cofbro.qian.databinding.FragmentHomeworkBinding
import com.cofbro.qian.view.FullScreenDialog
import com.cofbro.qian.wrapper.WrapperActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HomeworkFragment : BaseFragment<HomeworkViewModel, FragmentHomeworkBinding>() {
    private var activity: WrapperActivity? = null
    override fun onAllViewCreated(savedInstanceState: Bundle?) {
        initArgs()
        initObserver()
        doNetwork()
    }

    private fun initArgs() {
        activity = requireActivity() as WrapperActivity
    }

    private fun initObserver() {
        viewModel.homeworkLiveData.observe(this) {
            lifecycleScope.launch(Dispatchers.IO) {
                val data = it.data?.body?.string()
                val url = it.data?.headers?.get("Location")

            }
        }
    }


    private fun doNetwork() {
        activity?.let {
            viewModel.queryHomeworkList("https://mooc1-2.chaoxing.com/mooc-ans/visit/stucoursemiddle?courseid=${activity?.courseId}&clazzid=${activity?.classId}&vc=1&cpi=${activity?.cpi}&ismooc2=1&v=2")
        }
    }

}