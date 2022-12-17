package com.kaiwolfram.nozzle.data.utils

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter

val emptyPainter: Painter by lazy {
    object : Painter() {
        override val intrinsicSize: Size
            get() = Size.Zero

        override fun DrawScope.onDraw() {}
    }
}
