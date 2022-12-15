package com.kaiwolfram.nozzle.ui.app.views.relays

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.kaiwolfram.nozzle.R
import com.kaiwolfram.nozzle.ui.components.ClearIcon
import com.kaiwolfram.nozzle.ui.components.TopBar
import com.kaiwolfram.nozzle.ui.theme.sizing
import com.kaiwolfram.nozzle.ui.theme.spacing

@Composable
fun RelaysScreen(
    uiState: RelaysViewModelState,
    onRemoveRelay: (Int) -> Unit,
    onAddRelay: () -> Unit,
    onNavigateToFeed: () -> Unit,
) {
    Column {
        TopBar(text = stringResource(id = R.string.relays), onGoBack = onNavigateToFeed)
        Column {
            uiState.relays.forEachIndexed { index, relay ->
                Relay(url = relay, index = index, onRemoveRelay = onRemoveRelay)
                Divider(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun Relay(
    url: String,
    index: Int,
    onRemoveRelay: (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(vertical = spacing.medium, horizontal = spacing.screenEdge),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RelayUrl(
            url = url,
            index = index
        )
        if (index > 0) {
            ClearIcon(modifier = Modifier
                .size(sizing.mediumIcon)
                .clickable { onRemoveRelay(index) })
        }
    }
}

@Composable
private fun RelayUrl(
    url: String,
    index: Int,
) {
    Row {
        Text(text = "#${index + 1}", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.padding(spacing.medium))
        Text(text = url)
    }
}
