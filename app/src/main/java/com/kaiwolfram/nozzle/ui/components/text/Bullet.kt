package com.kaiwolfram.nozzle.ui.components.text

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.kaiwolfram.nozzle.ui.theme.LightGray21

@Composable
fun Bullet(color: Color = LightGray21) {
    Text(
        text = "\u2022",
        color = color,
    )
}
