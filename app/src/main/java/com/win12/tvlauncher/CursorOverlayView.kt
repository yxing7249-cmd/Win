package com.win12.tvlauncher

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class CursorOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    var cursorX = 960f
    var cursorY = 540f
    var isClicking = false

    private val pointerPath = Path()
    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.WHITE
        strokeWidth = 3f
    }
    private val ripplePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.parseColor("#38BDF8")
        strokeWidth = 4f
    }

    init {
        isClickable = false
        isFocusable = false
    }

    fun updatePosition(x: Float, y: Float) {
        cursorX = x.coerceIn(0f, width.toFloat().coerceAtLeast(1f))
        cursorY = y.coerceIn(0f, height.toFloat().coerceAtLeast(1f))
        invalidate()
    }

    fun triggerClickEffect() {
        isClicking = true
        invalidate()
        postDelayed({
            isClicking = false
            invalidate()
        }, 150)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (isClicking) {
            canvas.drawCircle(cursorX, cursorY, 28f, ripplePaint)
        }

        fillPaint.shader = LinearGradient(
            cursorX, cursorY, cursorX + 32f, cursorY + 42f,
            Color.parseColor("#38BDF8"),
            Color.parseColor("#2563EB"),
            Shader.TileMode.CLAMP
        )

        pointerPath.reset()
        pointerPath.moveTo(cursorX, cursorY)
        pointerPath.lineTo(cursorX, cursorY + 40f)
        pointerPath.lineTo(cursorX + 11f, cursorY + 30f)
        pointerPath.lineTo(cursorX + 20f, cursorY + 46f)
        pointerPath.lineTo(cursorX + 26f, cursorY + 43f)
        pointerPath.lineTo(cursorX + 17f, cursorY + 27f)
        pointerPath.lineTo(cursorX + 30f, cursorY + 27f)
        pointerPath.close()

        canvas.drawPath(pointerPath, fillPaint)
        canvas.drawPath(pointerPath, strokePaint)
    }
}
