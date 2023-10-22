package com.cofbro.qian.profile

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.util.AttributeSet
import android.view.DragEvent
import com.google.android.material.bottomsheet.BottomSheetDragHandleView

class BottomSheetDrag : BottomSheetDragHandleView{
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onDragEvent(event: DragEvent?): Boolean {
        return super.onDragEvent(event)

    }
}
