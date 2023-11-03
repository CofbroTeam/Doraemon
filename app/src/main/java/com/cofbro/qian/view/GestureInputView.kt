package com.cofbro.qian.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.google.android.material.internal.ViewUtils.dpToPx

class GestureInputView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    var isTouchAble = true

    @SuppressLint("RestrictedApi")
    private var mRadius = dpToPx(context, 30) // 背景点的半径
        set(value) {
            field = value
            mGap = value
        }
    private var mGap = mRadius // 每个背景点的间隙
    private var theMin = 0f // 宽高中较短的一条边

    private var mStartCx = 0f // 第一个点的 x
    private var mStartCy = 0f // 第一个点的 y

    private var isNeedInitAllDots = true // 是否需要初始化背景点,省去每次绘制时都要 for循环计算每个点的坐标

    private val mDots = arrayListOf<Dot>() // 背景的九个点
    private val mInputPwd = arrayListOf<Int>() // 用户目前输入的密码,如果是对的就要传到外部

    private var formerTouchedDot: Dot? = null
    private var mPath = Path()
    private var mStartX: Float? = null // 绘制到 View外
    private var mStartY: Float? = null
    private var mStopX: Float? = null
    private var mStopY: Float? = null

    /** 输入结束把密码传出去，并接受返回值 */
    private var mInputEndListener: ((inputPwd: List<Int>) -> Unit)? = null

    /** 背景点的 paint */
    private
    val mDotPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#88bbbbbb")
        style = Paint.Style.FILL_AND_STROKE
    }

    private val mLinePaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#66000000")
        strokeWidth = mRadius / 5
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val mWidth = when (MeasureSpec.getMode(widthMeasureSpec)) {
            MeasureSpec.EXACTLY -> MeasureSpec.getSize(widthMeasureSpec)
            MeasureSpec.AT_MOST -> (2 * mGap + 6 * mRadius).toInt()
            else -> 0
        }
        val mHeight = when (MeasureSpec.getMode(heightMeasureSpec)) {
            MeasureSpec.EXACTLY -> MeasureSpec.getSize(heightMeasureSpec)
            MeasureSpec.AT_MOST -> (2 * mGap + 6 * mRadius).toInt()
            else -> 0
        }
        setMeasuredDimension(mWidth, mHeight)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        theMin = min()
        mRadius = theMin / 8
        mStartCx = (width - theMin) / 2 + mRadius
        mStartCy = (height - theMin) / 2 + mRadius
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (isNeedInitAllDots) {
            initAllDots()
            isNeedInitAllDots = false
        }
        drawDotsBackground(canvas)
        showState(canvas)
        drawMoveLine(canvas)
        drawPath(canvas)
    }

    /** 初始化九个点 */
    private fun initAllDots() {
        mDots.clear()
        for (n in 0 until 9) {
            val cx = when (n % 3) {
                0 -> mStartCx
                1 -> mStartCx + mGap + 2 * mRadius
                2 -> mStartCx + 2 * mGap + 4 * mRadius
                else -> 0f
            }
            val cy = when (n / 3) {
                0 -> mStartCy
                1 -> mStartCy + mGap + 2 * mRadius
                2 -> mStartCy + 2 * mGap + 4 * mRadius
                else -> 0f
            }
            mDots.add(Dot(n + 1, cx, cy, mRadius))
        }
    }

    /** 绘制九个点 */
    private fun drawDotsBackground(canvas: Canvas?) {
        for (dot in mDots) {
            canvas?.drawCircle(dot.cx, dot.cy, dot.radius, mDotPaint)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_MOVE -> {
                actionMove(event.x, event.y)
            }

            MotionEvent.ACTION_UP -> {
                actionUp()
            }
        }
        return true
    }

    /** MotionEvent.ACTION_MOVE */
    private fun actionMove(x: Float, y: Float) {
        if (!isTouchAble) return
        mStopX = x
        mStopY = y
        invalidate()
        val currentTouchedDot = whichDotIsTouched(x, y)
        if (currentTouchedDot != null && currentTouchedDot != formerTouchedDot) { // 在点内
            mInputPwd.forEach {
                if (it == currentTouchedDot.order) return
            }
            vibrate()
            mStartX = currentTouchedDot.cx
            mStartY = currentTouchedDot.cy
            if (formerTouchedDot == null) {
                mPath.moveTo(currentTouchedDot.cx, currentTouchedDot.cy)
            } else {
                mPath.lineTo(currentTouchedDot.cx, currentTouchedDot.cy)
            }
            formerTouchedDot = currentTouchedDot
            mInputPwd.add(currentTouchedDot.order)
            currentTouchedDot.state = DotState.Selected
            invalidate()
        }
    }

    /** 震动 */
    private fun vibrate() {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(25, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            // 兼容旧版本的震动效果调用
            vibrator.vibrate(25)
        }
    }

    /** MotionEvent.ACTION_UP */
    private fun actionUp() {
        isTouchAble = false
        mStartX = null
        mStartY = null
        mStopX = null
        mStopY = null

        mInputEndListener?.let {
            it(mInputPwd)
        }
    }

    /** 判断哪个点被触摸 */
    private fun whichDotIsTouched(x: Float, y: Float): Dot? {
        for (n in 0 until 9) {
            if (mDots[n].isTouched(x, y))
                return mDots[n]
        }
        return null
    }

    /** 显示点的状态 */
    private fun showState(canvas: Canvas?) {
        for (n in 0 until 9) {
            val mStatePaintColor = when (mDots[n].state) {
                DotState.Selected -> Color.parseColor("#66000000")
                DotState.Right -> Color.parseColor("#6600ff00")
                DotState.Wrong -> Color.parseColor("#66ff0000")
                else -> Color.parseColor("#00ffffff")
            }
            val mStatePaint = Paint().apply {
                isAntiAlias = true
                color = mStatePaintColor
                style = Paint.Style.FILL_AND_STROKE
            }
            canvas?.drawCircle(mDots[n].cx, mDots[n].cy, mDots[n].radius / 3, mStatePaint)
        }
    }

    /** 绘制连线 */
    private fun drawMoveLine(canvas: Canvas?) {
        if (mStartX != null && mStartY != null && mStopX != null && mStopY != null) {
            canvas?.drawLine(mStartX!!, mStartY!!, mStopX!!, mStopY!!, mLinePaint)
        }
    }

    private fun drawPath(canvas: Canvas?) {
        canvas?.drawPath(mPath, mLinePaint)
    }

    /** 给已经被触摸的点赋予自定义状态 */
    fun setState(state: DotState) {
        for (dot in mDots) {
            if (dot.state == DotState.Selected) {
                dot.state = state
            }
        }
        if (state == DotState.Right) {
            mLinePaint.color = Color.parseColor("#6600ff00")
        } else if (state == DotState.Wrong) {
            mLinePaint.color = Color.parseColor("#66ff0000")
        }
        invalidate() // 重新绘制更新状态
    }

    /** 密码错误后要做的事:初始化数据... */
    fun initData() {
        mLinePaint.color = Color.parseColor("#66000000")
        isNeedInitAllDots = true
        formerTouchedDot = null
        isTouchAble = true
        mInputPwd.clear()
        mPath.reset()
        invalidate()
    }

    /** 给外部密码 */
    fun getPwd() = mInputPwd

    fun setInputEndListener(inputEndListener: (List<Int>) -> Unit) {
        mInputEndListener = inputEndListener
    }

    /** 点的模型 */
    data class Dot(
        val order: Int, val cx: Float, val cy: Float,
        val radius: Float, var state: DotState = DotState.Normal
    ) {
        fun isTouched(x: Float, y: Float): Boolean {
            if (cx - radius * 2 / 3 < x && x < cx + radius * 2 / 3) {
                if (cy - radius * 2 / 3 < y && y < cy + radius * 2 / 3) {
                    return true
                }
            }
            return false
        }
    }

    enum class DotState { Normal, Selected, Right, Wrong }

    private fun min() = width.coerceAtMost(height).toFloat()
}