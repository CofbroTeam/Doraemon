package com.cofbro.qian.view

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.*
import android.text.InputType
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.view.inputmethod.BaseInputConnection
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import com.cofbro.qian.R
import com.cofbro.qian.view.InputView.Config.CLICK_SCALE_RANGE
import com.cofbro.qian.view.InputView.Config.CURSOR_HEIGHT
import com.cofbro.qian.view.InputView.Config.CURSOR_PADDING
import com.cofbro.qian.view.InputView.Config.DEFAULT_HEIGHT
import com.cofbro.qian.view.InputView.Config.DEFAULT_PADDING
import com.cofbro.qian.view.InputView.Config.DEFAULT_TEXT_COUNT
import com.cofbro.qian.view.InputView.Config.DEFAULT_WIDTH
import com.cofbro.qian.view.InputView.Config.HINT_BACKGROUND_PADDING
import com.cofbro.qian.view.InputView.Config.TEXT_INDENTED
import kotlin.math.abs

class InputView : View {
    object Config {
        // 输入框中首字符的缩进距离
        const val TEXT_INDENTED = 30f

        // hint的背景内间距
        const val HINT_BACKGROUND_PADDING = 10f

        // 光标的高度
        const val CURSOR_HEIGHT = 45f

        // 光标离左侧文字的间距
        const val CURSOR_PADDING = 5f

        // bitmap点击扩大范围
        const val CLICK_SCALE_RANGE = 8f

        // 此view默认的宽度和高度
        const val DEFAULT_WIDTH = 800f
        const val DEFAULT_HEIGHT = 130f

        // 默认内间距
        const val DEFAULT_PADDING = 50

        // 默认可输入的最大字数
        const val DEFAULT_TEXT_COUNT = 23
    }

    // 记录第一次 down是否是在bitmap中
    private var alreadyTouchDownInRect = false

    // bitmap/小图标 的rect
    private lateinit var bitmapRect: RectF

    // 此view的属性集合
    private lateinit var typedArray: TypedArray
    private var bitmap: Bitmap? = null

    // 是否是密码文本
    private var isPasswordType = false

    // 由isPasswordType值决定，是否显示输入框右侧的小图标
    private var ifShowBitmap = false

    // 输入框输入的内容
    private var inputString: String = ""

    // 密码输入框中用来隐藏密码的 -> ******
    private var hideInputString = ""
    private var cursorAnimator: Animator? = null
    private var cursorAlpha = 255

    // hint文本横向移动距离
    private var textOffsetY = 0f

    // hint文本的纵向移动距离
    private var textOffsetX = 0f

    // 此view的 左 上 右 下的坐标
    private var left = 0f
    private var top = 0f
    private var right = 0f
    private var bottom = 0f

    // 此view的宽度和高度
    private var mWidth = DEFAULT_WIDTH
    private var mHeight = DEFAULT_HEIGHT

    // hint文本
    private var hintText = ""
    private val borderPaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.parseColor("#dfeeff")
        strokeWidth = 5f
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        isAntiAlias = true
        isDither = true
    }
    private val hintPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        textSize = 35f
        color = Color.parseColor("#cccccc")
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        isAntiAlias = true
        isDither = true
    }
    private val hintBackgroundPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        isAntiAlias = true
        isDither = true
    }

    private val cursorPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        strokeWidth = 5f
        color = Color.argb(0, 87, 209, 118)
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        isAntiAlias = true
        isDither = true
    }

    private val textPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        textSize = 40f
        color = Color.BLACK
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        isAntiAlias = true
        isDither = true
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs)

    }

    private fun init(attrs: AttributeSet?) {
        isFocusable = true
        isFocusableInTouchMode = true
        typedArray = context.obtainStyledAttributes(attrs, R.styleable.InputView)
        hintText = typedArray.getString(R.styleable.InputView_hint).toString()
        if (typedArray.getString(R.styleable.InputView_type)
                .toString() == "password"
        ) isPasswordType = true
        ifShowBitmap = isPasswordType
        bitmap = createBitmap(R.drawable.ic_eye_close)
        hintBackgroundPaint.color =
            typedArray.getColor(R.styleable.InputView_hintBackground, Color.parseColor("#ffffff"))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)

        mWidth = width.toFloat() - DEFAULT_PADDING
        mHeight = height.toFloat() - DEFAULT_PADDING
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas.let {
            drawBorder(it!!)
            drawTextBackground(it)
            drawHint(it, hintText)
            drawCursor(it)
            drawInputtedText(it)
            drawBitmap(it)
        }
    }

    // 画输入框的边框
    private fun drawBorder(canvas: Canvas) {
        canvas.drawRoundRect(left, top, right, bottom, 30f, 30f, borderPaint)
    }

    // 画hint的背景
    private fun drawTextBackground(canvas: Canvas) {
        val rect = Rect()
        hintPaint.getTextBounds(hintText, 0, hintText.length, rect)
        val l = left + TEXT_INDENTED - HINT_BACKGROUND_PADDING + textOffsetX
        val t = top + (bottom - top) / 2f - rect.height() / 2f - textOffsetY
        val r = l + rect.width() + HINT_BACKGROUND_PADDING * 2
        val b = t + rect.height()
        canvas.drawRect(l, t, r, b, hintBackgroundPaint)
    }

    // 画hint文字
    private fun drawHint(canvas: Canvas, str: String) {
        if (inputString.isNotEmpty()) hintPaint.color = Color.parseColor("#34c759")
        else hintPaint.color = Color.parseColor("#cccccc")
        canvas.drawText(
            str,
            0,
            str.length,
            left + TEXT_INDENTED + textOffsetX,
            getTextBaseline(hintPaint) - textOffsetY,
            hintPaint
        )
    }

    // 画光标
    private fun drawCursor(canvas: Canvas) {
        var l = left + CURSOR_PADDING
        val t = top + (bottom - top - CURSOR_HEIGHT) / 2f
        val b = t + CURSOR_HEIGHT
        if (hideInputString.isNotEmpty()) {
            val rect = Rect()
            textPaint.getTextBounds(hideInputString, 0, hideInputString.length, rect)
            l += rect.width()
        }

        canvas.drawLine(
            l + TEXT_INDENTED,
            t,
            l + TEXT_INDENTED,
            b,
            cursorPaint
        )
    }

    // 画输入的文字
    private fun drawInputtedText(canvas: Canvas) {
        hideInputString = inputString
        if (isPasswordType) {
            hideInputString = ""
            inputString.forEach { _ ->
                hideInputString += "*"
            }
        }
        canvas.drawText(
            hideInputString,
            0,
            hideInputString.length,
            left + TEXT_INDENTED,
            getTextBaseline(textPaint),
            textPaint
        )
    }

    // 画输入框右侧小图标
    private fun drawBitmap(canvas: Canvas) {
        if (bitmap == null || !ifShowBitmap) return
        val l = right - bitmap!!.width - DEFAULT_PADDING / 2f
        val t = top + (bottom - top - bitmap!!.height) / 2f
        bitmapRect = RectF(
            l - CLICK_SCALE_RANGE,
            t - CLICK_SCALE_RANGE,
            l + bitmap!!.width + CLICK_SCALE_RANGE,
            t + bitmap!!.height + CLICK_SCALE_RANGE
        )
        canvas.drawBitmap(bitmap!!, l, t, textPaint)
    }

    // hint的Y轴上的动画 hint的X轴上的动画
    private fun hintOffsetYAnimation(
        offsetYStart: Float,
        offsetYEnd: Float,
        offsetXStart: Float,
        offsetXEnd: Float
    ) {
        if (getTextString().isNotEmpty()) return
        ValueAnimator.ofFloat(offsetYStart, offsetYEnd).apply {
            duration = 200
            repeatCount = 0
            interpolator = LinearInterpolator()
            addUpdateListener {
                textOffsetY = it.animatedValue as Float
                invalidate()
            }
        }.start()
        ValueAnimator.ofFloat(offsetXStart, offsetXEnd).apply {
            duration = 200
            interpolator = LinearInterpolator()
            repeatCount = 0
            addUpdateListener {
                textOffsetX = it.animatedValue as Float
                invalidate()
            }
        }.start()
    }

    // 光标闪烁的开始动画
    private fun startCursorAnimator() {
        if (cursorAnimator == null) {
            cursorAnimator = ValueAnimator.ofInt(255, 0).apply {
                duration = 1000
                interpolator = LinearInterpolator()
                repeatCount = ValueAnimator.INFINITE
                repeatMode = ValueAnimator.RESTART
                addUpdateListener {
                    cursorAlpha = it.animatedValue as Int
                    cursorPaint.color = Color.argb(cursorAlpha, 87, 209, 118)
                    invalidate()
                }
            }
        }
        cursorAnimator!!.start()
    }

    // 光标停止闪烁的动画
    private fun stopCursorAnimator() {
        if (cursorAnimator != null) {
            cursorAnimator!!.cancel()
            cursorAnimator = null
            cursorPaint.color = Color.argb(0, 87, 209, 118)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        left = width / 2f - mWidth / 2f
        top = height / 2f - mHeight / 2f
        right = left + mWidth
        bottom = top + mHeight
    }

    // 弹出软键盘
    private fun showKeyboard() {
        isFocusable = true
        isFocusableInTouchMode = true
        requestFocus()
        val im =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                ?: return
        im.showSoftInput(this, InputMethodManager.SHOW_FORCED)
    }

    // 收起软键盘
    private fun hideKeyboard() {
        val imm: InputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)

    }

    // 计算得到文本的baseline
    private fun getTextBaseline(paint: Paint): Float {
        val fontMetrics = paint.fontMetrics
        val bottom = fontMetrics.bottom
        val top = fontMetrics.top
        return height / 2f + (bottom + abs(top)) / 2f - bottom
    }

    // 对外暴露的获取文本框内容的方法
    fun getTextString(): String {
        return inputString
    }

    private fun isKeyboardHidden(view: View): Boolean {
        val rect = Rect()
        view.getWindowVisibleDisplayFrame(rect) // 获取 window可见区域高度，不包括键盘
        val visibleHeight = rect.height()
        val screenHeight = Resources.getSystem().displayMetrics.heightPixels // 获取 window高度，包括键盘
        return visibleHeight >= screenHeight
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (!ifShowBitmap) {
            // 键盘收=收起后，再次电机输入框，弹出键盘
            if (isKeyboardHidden(this)) {
                showKeyboard()
            }
            requestFocus()
            return true
        }
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                if (bitmapRect.contains(event.x, event.y)) {
                    bitmap = createBitmap(R.drawable.ic_eye_open)
                    alreadyTouchDownInRect = true
                    isPasswordType = false
                } else if (isKeyboardHidden(this)) {
                    // 键盘收=收起后，再次电机输入框，弹出键盘
                    showKeyboard()
                } else {
                    requestFocus()
                }
            }

            MotionEvent.ACTION_UP -> {
                if (bitmapRect.contains(event.x, event.y)) {
                    bitmap = createBitmap(R.drawable.ic_eye_close)
                    isPasswordType = true
                    alreadyTouchDownInRect = false
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (!bitmapRect.contains(event.x, event.y) && alreadyTouchDownInRect) {
                    bitmap = createBitmap(R.drawable.ic_eye_close)
                    isPasswordType = true
                }
            }
        }
        postInvalidate()
        return true
    }

    override fun onFocusChanged(
        gainFocus: Boolean,
        direction: Int,
        previouslyFocusedRect: Rect?
    ) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect)
        if (gainFocus) {
            showKeyboard()
            borderPaint.color = Color.parseColor("#3170ff")
            hintOffsetYAnimation(0f, (bottom - top) / 2f, 0f, 20f)
            startCursorAnimator()
        } else {
            hideKeyboard()
            borderPaint.color = Color.parseColor("#dfeeff")
            hintOffsetYAnimation((bottom - top) / 2f, 0f, 20f, 0f)
            stopCursorAnimator()
        }
    }

    override fun onCheckIsTextEditor(): Boolean {
        return true
    }

    private fun createBitmap(resId: Int): Bitmap {
        val drawable = ContextCompat.getDrawable(context, resId)
        val b = androidx.core.graphics.createBitmap(
            drawable!!.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(b)
        // 重点 这个坑一定要记住
        /**
         * 坑！！！！！！！！！
         * 坑！！！！！！！！！
         * 坑！！！！！！！！！
         */
        drawable.bounds = Rect(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable.draw(canvas)
        return b
    }

    // 方法，需要返回一个InputConnect对象，这个是和输入法输入内容的桥梁。
    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection {
        // outAttrs就是我们需要设置的输入法的各种类型最重要的就是:
        outAttrs.imeOptions = EditorInfo.IME_FLAG_NO_EXTRACT_UI
        outAttrs.inputType = InputType.TYPE_NULL
        return MyInputConnection(this, true)
    }

    inner class MyInputConnection(targetView: View, fullEditor: Boolean) :
        BaseInputConnection(targetView, fullEditor) {
        override fun commitText(text: CharSequence?, newCursorPosition: Int): Boolean {
            val temp = inputString + text.toString()
            if (temp.length < DEFAULT_TEXT_COUNT) {
                inputString = temp
            }
            invalidate()
            return true
        }

        override fun sendKeyEvent(event: KeyEvent?): Boolean {
            /** 当手指离开的按键的时候 */
            if (event != null) {
                Log.d("tag", "sendKeyEvent:KeyCode=" + event.keyCode)
            }
            if (event?.action == KeyEvent.ACTION_DOWN) {
                if (event.keyCode == KeyEvent.KEYCODE_DEL) {
                    //删除按键
                    if (inputString.isNotEmpty()) {
                        inputString = inputString.substring(0, inputString.length - 1)
                    }
                }
            }
            postInvalidate()
            return true
        }

        override fun deleteSurroundingText(beforeLength: Int, afterLength: Int): Boolean {
            return true
        }

        override fun finishComposingText(): Boolean {
            // 结束组合文本输入的时候，这个方法基本上会出现在切换输入法类型，点击回车（完成、搜索、发送、下一步）点击输入法右上角隐藏按钮会触发。
            return true
        }
    }
}
