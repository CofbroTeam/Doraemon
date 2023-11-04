package com.cofbro.qian.profile.advice

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import com.hjq.toast.ToastUtils


class AdviceFragment : DialogFragment() {
    private var tvQQ: TextView? = null
    private var tvGithub: TextView? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val height = context?.resources?.displayMetrics?.heightPixels?.minus(100)
        val window = this.dialog?.window
        window?.decorView?.setPadding(0, 0, 0, 0)
        val lp = window?.attributes
        lp?.width = WindowManager.LayoutParams.MATCH_PARENT
        lp?.height = height ?: WindowManager.LayoutParams.MATCH_PARENT
        lp?.gravity = Gravity.BOTTOM
        lp?.windowAnimations = R.style.BottomDialogAnimation
        window?.attributes = lp
        window?.setBackgroundDrawable(ColorDrawable())
        val root =  layoutInflater.inflate(R.layout.fragment_advice, container, false)

        tvQQ = root.findViewById(R.id.tv_qq)
        tvGithub = root.findViewById(R.id.tv_github)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tvQQ?.setOnClickListener {
            copyToClipboard("567290278")
        }

        tvGithub?.setOnClickListener {
            copyToClipboard("https://github.com/CofbroTeam/Doraemon")
        }
    }

    private fun copyToClipboard(text: String) {
        val clipboard: ClipboardManager? =
            context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        val clip = ClipData.newPlainText("qian", text)
        clipboard?.setPrimaryClip(clip)
        ToastUtils.show("已复制到系统剪切板")
    }

}