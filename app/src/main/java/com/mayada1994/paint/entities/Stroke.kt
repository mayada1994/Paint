package com.mayada1994.paint.entities

import android.graphics.Path

data class Stroke(
    override var color: Int,
    override var strokeWidth: Float,
    var path: Path
) : PaintObject