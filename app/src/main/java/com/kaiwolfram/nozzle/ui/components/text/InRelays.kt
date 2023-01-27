package com.kaiwolfram.nozzle.ui.components.text

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import com.kaiwolfram.nozzle.R
import com.kaiwolfram.nozzle.ui.theme.LightGray21
import com.kaiwolfram.nozzle.ui.theme.Shapes

@Composable
fun InRelays(relays: List<String>) {
    if (relays.isNotEmpty()) {
        Row(modifier = Modifier
            .clip(Shapes.small)
            .clickable { }) {
            InRelay(modifier = Modifier.weight(weight = 0.7f, fill = false), relay = relays.first())
            if (relays.size >= 2) {
                AndOthers(
                    modifier = Modifier.weight(weight = 0.3f, fill = false),
                    otherRelaysCount = relays.size
                )
            }
        }
    }
}

@Composable
private fun InRelay(relay: String, modifier: Modifier = Modifier, color: Color = LightGray21) {
    Text(
        modifier = modifier,
        text = buildAnnotatedString {
            withStyle(style = SpanStyle(color = color)) {
                append(stringResource(id = R.string.in_relay))
                append(" ")
            }
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = color)) {
                append(relay.removePrefix("wss://"))
            }
        },
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun AndOthers(
    otherRelaysCount: Int,
    modifier: Modifier = Modifier,
    color: Color = LightGray21
) {
    Text(
        modifier = modifier,
        text = buildAnnotatedString {
            withStyle(style = SpanStyle(color = color)) {
                append(" ")
                append(stringResource(id = R.string.and))
                append(" ")
            }
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = color)) {
                append(otherRelaysCount.toString())
                append(" ")
                append(pluralStringResource(id = R.plurals.other_relays, otherRelaysCount))
            }
        },
        maxLines = 1
    )
}
