package com.cofbro.qian.wrapper.homework

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.cofbro.qian.R
import com.cofbro.qian.view.FullScreenDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeworkFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_homework, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dialog = FullScreenDialog(requireContext())
        dialog.setCancelable(false)
        dialog.show()
        lifecycleScope.launch(Dispatchers.IO) {
            delay(10000)
            withContext(Dispatchers.Main) {
                dialog.dismiss()

            }
        }
    }
}