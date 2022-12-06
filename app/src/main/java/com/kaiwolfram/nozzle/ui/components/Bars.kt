package com.kaiwolfram.nozzle.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kaiwolfram.nozzle.ui.theme.spacing

@Composable
fun TopBar(text: String, onGoBack: (() -> Unit)? = null) {
    TopAppBar {
        if (onGoBack != null) {
            GoBackButton(onGoBack = onGoBack)
        }
        Spacer(modifier = Modifier.width(spacing.large))
        Text(
            text = text,
            style = MaterialTheme.typography.h6,
        )
    }
}
