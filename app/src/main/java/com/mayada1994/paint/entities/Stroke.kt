package com.mayada1994.paint.entities

import android.graphics.Path

data class Stroke(
    var color: Int,
    var strokeWidth: Float,
    var path: Path
)