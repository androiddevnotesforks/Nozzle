package com.kaiwolfram.nozzle.ui.components.dropdown

import androidx.compose.foundation.layout.PaddingValues
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
fun RelaysDropdownMenu(
    showMenu: Boolean,
    menuItems: List<RelayActive>,
    onClickIndex: (Int) -> Unit,
    onDismiss: () -> Unit,
    isAutopilot: Boolean? = null,
    onToggleAutopilot: (() -> Unit)? = null
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
        if (isAutopilot != null && onToggleAutopilot != null) {
            CheckboxDropdownMenuItem(
                isChecked = isAutopilot,
                text = stringResource(id = R.string.autopilot),
                contentPadding = PaddingValues(start = spacing.medium, end = spacing.xl),
                onToggle = onToggleAutopilot,
            )
            DropdownDivider()
        }
        menuItems.forEachIndexed { index, item ->
            CheckboxDropdownMenuItem(
                isChecked = item.isActive,
                text = item.relayUrl.removePrefix("wss://"),
                contentPadding = PaddingValues(start = spacing.medium, end = spacing.xl),
                onToggle = { onClickIndex(index) }
            )
        }
    }
}
