package com.kaiwolfram.nozzle.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


data class Sizing(
    val smallIcon: Dp = 16.dp,
    val profilePic: Dp = 40.dp,
    val largeProfilePic: Dp = 60.dp,
)

val LocalSizing = compositionLocalOf { Sizing() }

val sizing: Sizing
    @Composable
    @ReadOnlyComposable
    get() = LocalSizing.current
