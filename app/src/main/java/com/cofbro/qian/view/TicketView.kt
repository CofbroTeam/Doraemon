package com.cofbro.qian.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Region
import android.util.AttributeSet
import android.view.View
import com.cofbro.qian.R

class TicketView : View {
    // 整个ticket的圆角大小
    private var ticketRadius = 80f

    // ticket中间虚线处的圆角大小
    private var crannyRadius = 50f

    // 左边部分的背景
    private val leftPaint = Paint().apply {
        style = Paint.Style.FILL
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        isAntiAlias = true
        isDither = true
    }

    // 右边部分的背景
    private val rightPaint = Paint().apply {
        style = Paint.Style.FILL
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        isAntiAlias = true
        isDither = true
    }

    // 虚线的画笔
    private val dashPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 20f
        pathEffect = DashPathEffect(floatArrayOf(5f, 37f), 10f)
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.SQUARE
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
    )

    fun setTopicColor(color: Int) {
        leftPaint.color = color
        dashPaint.color = leftPaint.color
        invalidate()
    }

    private fun init(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TicketView)
        ticketRadius = typedArray.getDimension(R.styleable.TicketView_ticketRadius, 80f)
        crannyRadius = typedArray.getDimension(R.styleable.TicketView_crannyRadius, 70f)
        leftPaint.color =
            typedArray.getColor(R.styleable.TicketView_leftBackground, Color.parseColor("#3170ff"))
        dashPaint.color = leftPaint.color
        rightPaint.color =
            typedArray.getColor(R.styleable.TicketView_rightBackground, Color.parseColor("#ffffff"))
        typedArray.recycle()
    }

    override fun onDraw(canvas: Canvas?) {
        canvas.let {
            clipRoundedRect(it!!)
            clipRect(it)
            drawRect(it)
            drawDash(it)
        }
    }

    private fun drawDash(canvas: Canvas) {
        canvas.drawLine(width / 3f * 2, 0f, width / 3f * 2, height.toFloat(), dashPaint)
    }

    private fun drawRect(canvas: Canvas) {
        canvas.drawRect(0f, 0f, width / 3f * 2, height.toFloat(), leftPaint)
        canvas.drawRect(
            width / 3f * 2,
            0f,
            width.toFloat(),
            height.toFloat(),
            rightPaint
        )
    }

    private fun clipRect(canvas: Canvas) {
        val path = Path()
        path.addCircle(width / 3f * 2, -60f, crannyRadius, Path.Direction.CCW)
        path.addCircle(width / 3f * 2, height + 60f, crannyRadius, Path.Direction.CCW)
        canvas.clipPath(path, Region.Op.DIFFERENCE)
    }

    private fun clipRoundedRect(canvas: Canvas) {
        val path = Path()
        path.addRoundRect(
            0f,
            0f,
            width.toFloat(),
            height.toFloat(),
            ticketRadius,
            ticketRadius,
            Path.Direction.CCW
        )
        canvas.clipPath(path)
    }

}