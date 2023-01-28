package com.kaiwolfram.nozzle.ui.components.text

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.kaiwolfram.nozzle.ui.theme.Typography

@Composable
fun DialogHeadline(headline: String, modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = headline,
        style = Typography.h6,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}
