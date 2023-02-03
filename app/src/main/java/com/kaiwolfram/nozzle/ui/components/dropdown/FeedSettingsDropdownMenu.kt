package com.kaiwolfram.nozzle.ui.components.dropdown

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.kaiwolfram.nozzle.R
import com.kaiwolfram.nozzle.ui.theme.spacing

@Composable
fun FeedSettingsDropdownMenu(
    showMenu: Boolean,
    isContactsOnly: Boolean,
    isPosts: Boolean,
    isReplies: Boolean,
    onToggleContactsOnly: () -> Unit,
    onTogglePosts: () -> Unit,
    onToggleReplies: () -> Unit,
    onDismiss: () -> Unit
) {
    DropdownMenu(
        expanded = showMenu,
        onDismissRequest = { onDismiss() }
    ) {
        val padding = PaddingValues(start = spacing.medium, end = spacing.xl)
        CheckboxDropdownMenuItem(
            isChecked = isContactsOnly,
            text = stringResource(id = R.string.contacts_only),
            contentPadding = padding,
            onToggle = onToggleContactsOnly,
        )
        Divider(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = spacing.medium))
        CheckboxDropdownMenuItem(
            isChecked = isPosts,
            text = stringResource(id = R.string.posts),
            contentPadding = padding,
            onToggle = onTogglePosts
        )
        CheckboxDropdownMenuItem(
            isChecked = isReplies,
            text = stringResource(id = R.string.replies),
            contentPadding = padding,
            onToggle = onToggleReplies
        )

    }
}
