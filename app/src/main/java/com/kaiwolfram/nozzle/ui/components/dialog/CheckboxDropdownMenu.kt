package com.kaiwolfram.nozzle.ui.components.dialog

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.Checkbox
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.kaiwolfram.nozzle.R
import com.kaiwolfram.nozzle.model.RelayActive
import com.kaiwolfram.nozzle.ui.theme.spacing

@Composable
fun RelayCheckboxMenu(
    showMenu: Boolean,
    menuItems: List<RelayActive>,
    onClickIndex: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    DropdownMenu(
        expanded = showMenu,
        onDismissRequest = { onDismiss() }
    ) {
        if (menuItems.isEmpty()) {
            DropdownMenuItem(onClick = { }, enabled = false) {
                Text(
                    text = stringResource(id = R.string.no_relays_available),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        menuItems.forEachIndexed { index, item ->
            DropdownMenuItem(
                onClick = { onClickIndex(index) },
                contentPadding = PaddingValues(start = spacing.medium, end = spacing.xl)
            ) {
                Checkbox(
                    checked = item.isActive,
                    onCheckedChange = { onClickIndex(index) })
                Text(
                    text = item.relayUrl.removePrefix("wss://"),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
