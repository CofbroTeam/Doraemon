package com.cofbro.qian.friend.chatActivity.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.os.Build
import android.text.TextPaint
import android.util.AttributeSet
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatEditText
import com.cofbro.qian.R
import com.google.android.material.internal.ViewUtils.dpToPx

@SuppressLint("UseCompatLoadingForDrawables")
class LimitInputView(context: Context, attrs: AttributeSet?) : AppCompatEditText(context, attrs) {
    init {
        background = resources.getDrawable(R.drawable.inputview_bg)
        @RequiresApi(Build.VERSION_CODES.Q)
        textCursorDrawable = resources.getDrawable(R.drawable.inputview_cursor)
        maxHeight = dpToPx(300).toInt()
        textSize = dpToPx(4)
    }

    /** config */
    private var mMaxTextLength = 300

    private val mTipPaddingEnd = dpToPx(15)
    private val mTipPaddingBottom = dpToPx(3)

    fun setMaxTextLength(maxLength: Int) {
        mMaxTextLength = maxLength
    }

    /** config */
    private var mLineHeight = 0 // 行高

    // 输入字数的提示
    private val tipPaint = TextPaint().apply {
        textSize = 8f
        isAntiAlias = true
        isDither = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        tipPaint.textSize = width / 25f
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (oldh != 0 && mLineHeight == 0) {
            // 行高赋值
            mLineHeight = h - oldh
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //drawTip(canvas)
    }

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        if (this.text.toString().length > mMaxTextLength) {
            setText(this.text!!.substring(0, mMaxTextLength))
            setSelection(this.text!!.length)
        }
    }

    private fun drawTip(canvas: Canvas?) {
        val text = if (mMaxTextLength == Int.MAX_VALUE) {
            "${text.toString().length}/∞"
        } else {
            "${text.toString().length}/$mMaxTextLength"
        }

        if (height >= maxHeight) {
            val lastLineBasLine = layout.getLineBaseline(lineCount - 1)
            val lastLineBottom = layout.getLineBottom(lineCount - 1)
            canvas?.drawText(
                text,
                width - mTipPaddingEnd - tipPaint.measureText(text),
                (lastLineBasLine + lastLineBottom) / 2f + mLineHeight,
                tipPaint
            )
        } else {
            canvas?.drawText(
                text,
                width - mTipPaddingEnd - tipPaint.measureText(text),
                height.toFloat() - mTipPaddingBottom,
                tipPaint
            )
        }
    }

    fun clearAndSend(): String {
        val message = text.toString()
        text?.clear()
        return message
    }

    @SuppressLint("RestrictedApi")
    private fun dpToPx(dp: Int): Float = dpToPx(context, dp)
}