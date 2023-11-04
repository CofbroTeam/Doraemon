package com.cofbro.qian.view

import android.content.Context
import android.os.Build
import android.text.InputFilter
import android.text.InputType
import android.util.AttributeSet
import android.view.Gravity
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import com.cofbro.qian.R
import com.cofbro.qian.utils.dp2px
import java.lang.StringBuilder


class VerifyCodeView : ViewGroup {
    private var callback: ((String) -> Unit)? = null
    private var start = 0
    private var mSize = 0
    private val mSpace = dp2px(context, 10)
    private var mCurrentIndex = 0
    private val editList = arrayListOf<EditText>()

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        initUi()
    }

    fun setCodeCallback(callback: (String) -> Unit) {
        this.callback = callback
    }

    private fun initUi() {
        for (i in 0..3) {
            val e = EditText(context).apply {
                if (Build.VERSION.SDK_INT >= 29) {
                    setTextCursorDrawable(R.drawable.cursor_color_shape)
                }
                gravity = Gravity.CENTER
                filters = arrayOf(InputFilter.LengthFilter(1))
                inputType = InputType.TYPE_CLASS_PHONE
                textSize = 35f

                //监听内容的改变
                addTextChangedListener(
                    afterTextChanged = {
                        it?.let {
                            if (text.length == 1) {
                                if (mCurrentIndex == 3) {
                                    callback?.let { it1 -> it1(getSMSCode()) }
                                }
                                nextFocus()
                            }
                        }
                    }
                )
                setOnKeyListener { v, keyCode, event ->
                    if (keyCode == KeyEvent.KEYCODE_DEL && event.action == MotionEvent.ACTION_DOWN) {
                        val editText = v as? EditText
                        if (editText?.text?.length == 0) {
                            previousFocus()
                            getCurrentEditText().text.clear()
                        }
                    }
                    false
                }
            }
            addView(e)
            editList.add(e)
        }
    }

    private fun getSMSCode(): String {
        val password = StringBuilder()
        editList.forEach {
            password.append(it.text)
        }
        return password.toString()
    }

    private fun previousFocus() {
        mCurrentIndex--
        if (mCurrentIndex >= 0) {
            editList[mCurrentIndex].requestFocus()
        } else {
            mCurrentIndex = 0
        }
    }

    private fun getCurrentEditText(): EditText {
        return editList[mCurrentIndex]
    }

    private fun nextFocus() {
        mCurrentIndex++
        if (mCurrentIndex < 4) {
            editList[mCurrentIndex].requestFocus()
        } else {
            mCurrentIndex = 3
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        mSize = (width - 3 * mSpace) / 6
        start = (width - mSize * 4 - mSpace * 3) / 2
    }

    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
        for (i in 0..3) {
            val v = getChildAt(i)
            val left = start + (mSize + mSpace) * i
            val right = left + mSize

            v.layout(left, 0, right, mSize + 10)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        postDelayed({
            //第一个EditText的焦点
            editList.first().requestFocus()
            //要弹出键盘
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(editList.first(), 0)
        }, 200)
    }
}