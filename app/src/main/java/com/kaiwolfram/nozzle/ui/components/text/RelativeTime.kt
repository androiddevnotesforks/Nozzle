package com.kaiwolfram.nozzle.ui.components.text

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import com.kaiwolfram.nozzle.data.utils.getRelativeTimeSpanString
import com.kaiwolfram.nozzle.ui.theme.LightGray21

@Composable
fun RelativeTime(from: Long, color: Color = LightGray21) {
    Text(
        text = getRelativeTimeSpanString(
            context = LocalContext.current,
            from = from
        ),
        color = color,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}
