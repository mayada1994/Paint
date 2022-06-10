package com.mayada1994.paint.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.withStyledAttributes
import com.mayada1994.paint.R
import com.mayada1994.paint.entities.Stroke
import kotlin.math.abs


class PaintView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Path representing the drawing so far
    private val drawings = arrayListOf<Stroke>()

    // Path representing what's currently being drawn
    private var curPath = Path()
    private var currentStrokeWidth = 12f
    private var currentColor = ResourcesCompat.getColor(resources, R.color.black, null)

    // Set up the paint with which to draw.
    private val paint = Paint().apply {
        // Smooths out edges of what is drawn without affecting shape.
        isAntiAlias = true
        // Dithering affects how colors with higher-precision than the device are down-sampled.
        isDither = true
        style = Paint.Style.STROKE // default: FILL
        strokeJoin = Paint.Join.ROUND // default: MITER
        strokeCap = Paint.Cap.ROUND // default: BUTT
    }

    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f

    private var currentX = 0f
    private var currentY = 0f

    private val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop

    init {
        context.withStyledAttributes(attrs, R.styleable.PaintView) {
            currentColor = getColor(R.styleable.PaintView_paintColor, currentColor)
            currentStrokeWidth =
                getDimension(R.styleable.PaintView_paintStrokeWidth, currentStrokeWidth)
        }
        paint.apply {
            color = currentColor
            strokeWidth = currentStrokeWidth // default: Hairline-width (really thin)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw the drawing so far
        drawings.forEach {
            paint.apply {
                color = it.color
                strokeWidth = it.strokeWidth
            }
            canvas.drawPath(it.path, paint)
        }
        // Draw any current squiggle
        paint.apply {
            color = currentColor
            strokeWidth = currentStrokeWidth
        }
        canvas.drawPath(curPath, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        motionTouchEventX = event.x
        motionTouchEventY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> touchStart()
            MotionEvent.ACTION_MOVE -> touchMove()
            MotionEvent.ACTION_UP -> touchUp()
        }
        return true
    }

    private fun touchStart() {
        curPath = Path()
        curPath.moveTo(motionTouchEventX, motionTouchEventY)
        currentX = motionTouchEventX
        currentY = motionTouchEventY
    }

    private fun touchMove() {
        val dx = abs(motionTouchEventX - currentX)
        val dy = abs(motionTouchEventY - currentY)
        if (dx >= touchTolerance || dy >= touchTolerance) {
            // QuadTo() adds a quadratic bezier from the last point,
            // approaching control point (x1,y1), and ending at (x2,y2).
            curPath.quadTo(
                currentX,
                currentY,
                (motionTouchEventX + currentX) / 2,
                (motionTouchEventY + currentY) / 2
            )
            currentX = motionTouchEventX
            currentY = motionTouchEventY
        }
        invalidate()
    }

    private fun touchUp() {
        // Add the current path to the drawing so far
        drawings.add(Stroke(currentColor, currentStrokeWidth, curPath))
    }

    fun changeColor(color: Int) {
        currentColor = color
        paint.color = color
    }

    fun changeStrokeWidth(strokeWidth: Float = currentStrokeWidth) {
        currentStrokeWidth = strokeWidth.toPx
        paint.strokeWidth = currentStrokeWidth
    }

    fun getStrokeWidth() = currentStrokeWidth.toDp

    // region Extensions for metrics
    private val Number.toDp get() = this.toFloat() / resources.displayMetrics.density
    private val Number.toPx get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        resources.displayMetrics)
    // endregion
}