package com.mayada1994.paint.entities

import android.graphics.RectF

data class Rectangle(
    override var color: Int,
    override var strokeWidth: Float,
    var rectF: RectF
): PaintObject
