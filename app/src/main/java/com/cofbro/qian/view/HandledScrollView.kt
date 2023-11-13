package com.cofbro.qian.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.widget.NestedScrollView
import com.cofbro.qian.utils.dp2px

class HandledScrollView : NestedScrollView {
    private var scrollTopListener: ((isTop: Boolean) -> Unit)? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun setScrollTopListener(scrollTopListener: (isTop: Boolean) -> Unit) {
        this.scrollTopListener = scrollTopListener
    }


    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        if (scrollY > 200f) {
            scrollTopListener?.invoke(false)
        } else {
            scrollTopListener?.invoke(true)
        }
        if (dy > 0 && scrollY < dp2px(context, 200)) {
            scrollBy(0, dy)
            consumed[1] = dy
        }
    }

}