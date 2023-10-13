package com.cofbro.qian.view

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.ScaleAnimation
import com.cofbro.qian.R


class LoadingView : View {
    object Params {
        // 此view默认的宽度和高度
        const val DEFAULT_WIDTH = 100f
        const val DEFAULT_HEIGHT = 100f

        // 默认内间距
        const val DEFAULT_PADDING = 30f

        // 小球放大缩小倍数
        const val DEFAULT_SCALE_MULTIPLE = 0.25f
    }

    private var typedArray: TypedArray? = null
    private var innerGap = 0f
    private var pointSize: Float = 0f
    private var originPointSize = 0f
    private val defaultColor = Color.BLUE
    private var startColor: Int = defaultColor
    private var endColor: Int = defaultColor
    private var rightPointColor = defaultColor

    // 此view的 左 上 右 下的坐标
    private var left = 0f
    private var top = 0f
    private var right = 0f
    private var bottom = 0f

    // 此view的宽度和高度
    private var mWidth = Params.DEFAULT_WIDTH
    private var mHeight = Params.DEFAULT_HEIGHT
    private var scaleAnimator: ValueAnimator? = null
    private var traverseAnimator: ValueAnimator? = null
    private var scaleValue = 0f
    private var cx1 = 0f
    private var cx2 = 0f
    private var interval = 0f
    private val leftPointPaint by lazy {
        Paint().apply {
            style = Paint.Style.FILL_AND_STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            isAntiAlias = true
            isDither = true
        }
    }
    private val rightPointPaint by lazy {
        Paint().apply {
            style = Paint.Style.FILL_AND_STROKE
            color = rightPointColor
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            isAntiAlias = true
            isDither = true
        }
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
        typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoadingView)
        initParams()
        initView()
        typedArray?.recycle()
    }

    private fun initView() {

    }

    private fun initParams() {
        startColor =
            typedArray?.getColor(R.styleable.LoadingView_startColor, defaultColor) ?: defaultColor
        endColor =
            typedArray?.getColor(R.styleable.LoadingView_endColor, defaultColor) ?: defaultColor
        rightPointColor =
            typedArray?.getColor(R.styleable.LoadingView_rightPointColor, defaultColor)
                ?: defaultColor
        pointSize = typedArray?.getDimension(R.styleable.LoadingView_pointSize, 0f) ?: 0f
        originPointSize = pointSize
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)

        mWidth = width.toFloat() - Params.DEFAULT_PADDING * 2
        mHeight = height.toFloat() - Params.DEFAULT_PADDING * 2
        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        left = width / 2f - mWidth / 2f
        top = height / 2f - mHeight / 2f
        right = left + mWidth
        bottom = top + mHeight
        // 控制原点大小
        if (pointSize * 2 + Params.DEFAULT_PADDING * 2 > width) {
            pointSize = (width - Params.DEFAULT_PADDING) / 2
            originPointSize = pointSize
        }
        innerGap = width - (Params.DEFAULT_PADDING + pointSize) * 2
        // 控制两点之间的空隙大小
        if (innerGap * 2 > pointSize * 3) {
            innerGap = pointSize * 3 / 2
            left = left + (mWidth - innerGap) / 2 - pointSize - innerGap / 2
            right = right - (mWidth - innerGap) / 2 + pointSize + innerGap / 2
        }
        startScaleAnimator()
        startTraverseAnimator()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            drawLeftPoint(it)
            drawRightPoint(it)
        }
    }

    private fun drawLeftPoint(canvas: Canvas?) {
        val l = left
        val t = height / 2f
        val r = left + pointSize
        val b = t+ pointSize
        val paint = leftPointPaint.apply {
            val linearGradient = LinearGradient(
                l, t,
                r, b,
                startColor, endColor,
                Shader.TileMode.MIRROR
            )
            shader = linearGradient
        }
        cx1 = left + pointSize / 2f
        canvas?.drawCircle(cx1 + interval, height / 2f, pointSize / 2f, paint)
    }

    private fun drawRightPoint(canvas: Canvas?) {
        val l = right - pointSize
        cx2 = right - pointSize / 2f
        val paint = rightPointPaint
        canvas?.drawCircle(cx2 - interval, height / 2f, pointSize / 2f, paint)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        scaleAnimator?.pause()
        traverseAnimator?.pause()
        scaleAnimator = null
        traverseAnimator = null
    }

    private fun startScaleAnimator() {
        val top = 1 + Params.DEFAULT_SCALE_MULTIPLE
        val bottom = 1 - Params.DEFAULT_SCALE_MULTIPLE
        scaleAnimator = ValueAnimator.ofFloat(top, bottom, top).apply {
            duration = 800
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener {
                scaleValue = it.animatedValue as Float
                pointSize = originPointSize * scaleValue
                postInvalidate()
            }
            start()
        }
    }

    private fun startTraverseAnimator() {
        val space = right - left - pointSize
        traverseAnimator = ValueAnimator.ofFloat(0f, space).apply {
            duration = 800
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener {
                interval = it.animatedValue as Float
                postInvalidate()
            }
            start()
        }
    }
}