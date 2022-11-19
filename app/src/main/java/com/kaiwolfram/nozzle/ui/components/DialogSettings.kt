package com.kaiwolfram.nozzle.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

val dialogShape = RoundedCornerShape(5.dp)
val dialogBackgroundColor = Color.White
val dialogProperties = DialogProperties(
    dismissOnBackPress = true,
    dismissOnClickOutside = true
)
