package com.cofbro.qian.wrapper.task

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.cofbro.qian.R

class SignTypeFragment : DialogFragment() {
    private var tvSignDirectly: TextView? = null
    private var tvSignWithCode: TextView? = null
    private var onPositiveClick: ((View) -> Unit)? = null
    private var onNegativeClick: ((View) -> Unit)? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val window = this.dialog?.window
        window?.decorView?.setPadding(0, 0, 0, 0)
        val lp = window?.attributes
        lp?.width = WindowManager.LayoutParams.MATCH_PARENT
        lp?.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp?.gravity = Gravity.BOTTOM
        lp?.windowAnimations = R.style.BottomDialogAnimation
        window?.attributes = lp
        window?.setBackgroundDrawable(ColorDrawable())
        val root = layoutInflater.inflate(R.layout.fragment_choose_sign_type, container, false)

        tvSignDirectly = root.findViewById(R.id.tv_sign_directly)
        tvSignWithCode = root.findViewById(R.id.tv_sign_with_code)

        tvSignDirectly?.setOnClickListener {
            onPositiveClick?.invoke(it)
        }

        tvSignWithCode?.setOnClickListener {
            onNegativeClick?.invoke(it)
        }

        return root
    }

    fun setOnClickSignDirectlyListener(listener: (View) -> Unit) {
        onPositiveClick = listener
    }

    fun setOnClickSignWithCodeListener(listener: (View) -> Unit) {
        onNegativeClick = listener
    }
}