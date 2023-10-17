package com.cofbro.qian.wrapper.homework

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.cofbro.hymvvmutils.base.BaseFragment
import com.cofbro.qian.databinding.FragmentHomeworkBinding
import com.cofbro.qian.view.FullScreenDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HomeworkFragment : BaseFragment<HomeworkViewModel, FragmentHomeworkBinding>() {
    override fun onAllViewCreated(savedInstanceState: Bundle?) {

    }


    private fun doNetwork() {
        viewModel.queryHomeworkList("http://mooc1-api.chaoxing.com/work/stu-work")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dialog = FullScreenDialog(requireContext())
        dialog.setCancelable(false)
        dialog.show()
        lifecycleScope.launch(Dispatchers.IO) {
            delay(2000)
            withContext(Dispatchers.Main) {
                dialog.dismiss()

            }
        }
    }

}