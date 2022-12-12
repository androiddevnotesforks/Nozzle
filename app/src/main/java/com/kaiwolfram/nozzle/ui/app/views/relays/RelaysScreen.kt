package com.kaiwolfram.nozzle.ui.app.views.relays

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import com.kaiwolfram.nozzle.R
import com.kaiwolfram.nozzle.ui.components.ActionButton
import com.kaiwolfram.nozzle.ui.components.TopBar
import com.kaiwolfram.nozzle.ui.theme.spacing

@Composable
fun RelaysScreen(
    uiState: RelaysViewModelState,
    onNavigateToFeed: () -> Unit,
) {
    Column {
        TopBar(text = stringResource(id = R.string.relays), onGoBack = onNavigateToFeed)
        LazyColumn {
            itemsIndexed(uiState.activeRelays) { index, relay ->
                ActiveRelay(url = relay,
                    index = index,
                    onLeave = { TODO() })
                // TODO: Last Item has no Divider
                Divider(modifier = Modifier.fillMaxWidth())
            }
            item {
                TextField(
                    label = { Text(text = "add relay") },
                    value = TextFieldValue(),
                    onValueChange = {},
                )
                Divider(modifier = Modifier.fillMaxWidth())
            }
            itemsIndexed(uiState.inactiveRelays) { index, relay ->
                InactiveRelay(index = index, url = relay, onJoin = { TODO() })
                // TODO: Last Item has no Divider
                Divider(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun ActiveRelay(
    url: String,
    index: Int,
    onLeave: (Int) -> Unit,
) {
    Relay(url = url, index = index, isActive = true, onAction = onLeave)
}

@Composable
private fun InactiveRelay(
    url: String,
    index: Int,
    onJoin: (Int) -> Unit,
) {
    Relay(url = url, index = index, isActive = false, onAction = onJoin)
}

@Composable
private fun Relay(
    url: String,
    index: Int,
    isActive: Boolean,
    onAction: (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = spacing.medium, horizontal = spacing.screenEdge),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RelayUrl(
            url = url,
            index = if (isActive) index else {
                null
            }
        )
        ActionButton(
            text = if (isActive) "Leave" else {
                "Join"
            }, onAction = { onAction(index) })
    }
}

@Composable
private fun RelayUrl(
    url: String,
    index: Int? = null,
) {
    Row {
        if (index != null) {
            Text(text = "#${index + 1}", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.padding(spacing.medium))
        }
        Text(text = url)
    }
}
