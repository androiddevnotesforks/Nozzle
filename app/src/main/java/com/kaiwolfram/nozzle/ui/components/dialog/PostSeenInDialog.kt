package com.kaiwolfram.nozzle.ui.components.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.kaiwolfram.nozzle.R
import com.kaiwolfram.nozzle.ui.components.text.DialogHeadline
import com.kaiwolfram.nozzle.ui.theme.spacing

@Composable
fun PostSeenInDialog(relays: List<String>, onCloseDialog: () -> Unit) {
    NozzleDialog(onCloseDialog = onCloseDialog) {
        Column {
            DialogHeadline(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.dialogEdge)
                    .padding(top = spacing.large, bottom = spacing.medium),
                headline = stringResource(id = R.string.post_seen_in)
            )
            RelayList(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.dialogEdge)
                    .padding(vertical = spacing.medium),
                relays = relays
            )
        }
    }
}

@Composable
private fun RelayList(relays: List<String>, modifier: Modifier = Modifier) {
    LazyColumn {
        items(relays) { relay ->
            Text(
                modifier = modifier,
                text = relay,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        item { Spacer(modifier = Modifier.height(spacing.large)) }
    }
}
