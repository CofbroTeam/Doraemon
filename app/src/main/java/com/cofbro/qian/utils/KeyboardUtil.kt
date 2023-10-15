package com.cofbro.qian.utils

import android.R
import android.app.Activity
import android.content.res.Resources
import android.graphics.Rect
import android.os.Build
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.WindowInsets
import android.view.WindowInsetsAnimation
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.core.view.WindowInsetsCompat
import kotlin.math.abs


object KeyboardUtil {
    var sDecorViewInvisibleHeightPre = 0
    private var onGlobalLayoutListener: OnGlobalLayoutListener? = null
    private var mNavHeight = 0
    private var sDecorViewDelta = 0
    private fun getDecorViewInvisibleHeight(activity: Activity): Int {
        val decorView = activity.window.decorView
            ?: return sDecorViewInvisibleHeightPre
        val outRect = Rect()
        decorView.getWindowVisibleDisplayFrame(outRect)
        val delta = abs(decorView.bottom - outRect.bottom)
        if (delta <= mNavHeight) {
            sDecorViewDelta = delta
            return 0
        }
        return delta - sDecorViewDelta
    }

    fun registerKeyboardHeightListener(activity: Activity, listener: KeyboardHeightListener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            invokeAbove31(activity, listener)
        } else {
            invokeBelow31(activity, listener)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private fun invokeAbove31(activity: Activity, listener: KeyboardHeightListener) {
        activity.window.decorView.setWindowInsetsAnimationCallback(object :
            WindowInsetsAnimation.Callback(DISPATCH_MODE_STOP) {
            override fun onProgress(
                windowInsets: WindowInsets,
                list: List<WindowInsetsAnimation>
            ): WindowInsets {
                val imeHeight = windowInsets.getInsets(WindowInsetsCompat.Type.ime()).bottom
                val navHeight =
                    windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
                val hasNavigationBar =
                    windowInsets.isVisible(WindowInsetsCompat.Type.navigationBars()) &&
                            windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom > 0
                listener.onKeyboardHeightChanged(
                    if (hasNavigationBar) Math.max(
                        imeHeight - navHeight,
                        0
                    ) else imeHeight
                )
                return windowInsets
            }
        })
    }

    private fun invokeBelow31(activity: Activity, listener: KeyboardHeightListener) {
        val flags = activity.window.attributes.flags
        if (flags and WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS != 0) {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
        val contentView = activity.findViewById<FrameLayout>(R.id.content)
        sDecorViewInvisibleHeightPre = getDecorViewInvisibleHeight(activity)
        onGlobalLayoutListener = OnGlobalLayoutListener {
            val height = getDecorViewInvisibleHeight(activity)
            if (sDecorViewInvisibleHeightPre != height) {
                listener.onKeyboardHeightChanged(height)
                sDecorViewInvisibleHeightPre = height
            }
        }

        //获取到导航栏高度之后再添加布局监听
        mNavHeight = navBarHeight
//        getNavigationBarHeight(activity, object : NavigationBarCallback() {
//            fun onHeight(height: Int, hasNav: Boolean) {
//                mNavHeight = height
//                contentView.viewTreeObserver.addOnGlobalLayoutListener(onGlobalLayoutListener)
//            }
//        })
    }

    fun unregisterKeyboardHeightListener(activity: Activity) {
        onGlobalLayoutListener = null
        val contentView = activity.window.decorView.findViewById<View>(R.id.content)
            ?: return
        contentView.viewTreeObserver.removeGlobalOnLayoutListener(onGlobalLayoutListener)
    }

    private val navBarHeight: Int
        get() {
            val res = Resources.getSystem()
            val resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android")
            return if (resourceId != 0) {
                res.getDimensionPixelSize(resourceId)
            } else {
                0
            }
        }

//    fun getNavigationBarHeight(activity: Activity, callback: NavigationBarCallback) {
//        val view = activity.window.decorView
//        val attachedToWindow = view.isAttachedToWindow
//        if (attachedToWindow) {
//            val windowInsets = ViewCompat.getRootWindowInsets(view)!!
//            val height = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
//            val hasNavigationBar =
//                windowInsets.isVisible(WindowInsetsCompat.Type.navigationBars()) &&
//                        windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom > 0
//            if (height > 0) {
//                callback.onHeight(height, hasNavigationBar)
//            } else {
//                callback.onHeight(navBarHeight, hasNavigationBar)
//            }
//        } else {
//            view.addOnAttachStateChangeListener(object : OnAttachStateChangeListener {
//                override fun onViewAttachedToWindow(v: View) {
//                    val windowInsets = ViewCompat.getRootWindowInsets(v)!!
//                    val height =
//                        windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
//                    val hasNavigationBar =
//                        windowInsets.isVisible(WindowInsetsCompat.Type.navigationBars()) &&
//                                windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom > 0
//                    if (height > 0) {
//                        callback.onHeight(height, hasNavigationBar)
//                    } else {
//                        callback.onHeight(navBarHeight, hasNavigationBar)
//                    }
//                }
//
//                override fun onViewDetachedFromWindow(v: View) {}
//            })
//        }
//    }

    interface KeyboardHeightListener {
        fun onKeyboardHeightChanged(height: Int)
    }
}

