package com.kaiwolfram.nozzle.data.utils

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter

fun createEmptyPainter(): Painter{
    return object : Painter() {
        override val intrinsicSize: Size
            get() = Size.Zero

        override fun DrawScope.onDraw() {
            throw IllegalStateException("empty painter should be overwritten")
        }
    }
}
